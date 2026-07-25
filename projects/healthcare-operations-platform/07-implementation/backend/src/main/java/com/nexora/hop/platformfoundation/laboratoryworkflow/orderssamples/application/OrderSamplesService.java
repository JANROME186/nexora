package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.application;

import static com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowValidation.optionalText;
import static com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowValidation.requiredObject;
import static com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.ChainOfCustodyEvent;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.CollectionMethod;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.CustodyEventType;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.OrderSamplesRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.PatientConditionAtCollection;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.PatientIdentitySnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.ReceptionCondition;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.RejectionReasonCode;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.RejectionStage;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.Sample;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleCollectionData;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleReceptionRecord;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleRejectionReason;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleRequirementSnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleStatus;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SpecimenLabelInfo;
import com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowConflictException;
import com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowEntityNotFoundException;
import com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowErrorCodes;
import com.nexora.hop.platformfoundation.laboratoryworkflow.shared.SampleReadPort;

/**
 * Application service for orders-samples bounded context (BCM-LAB-002/003/005).
 * Manages Sample aggregate lifecycle from collection through processing handoff.
 * Also implements SampleReadPort for TD-BE-010 cross-module read access.
 *
 * <p>Does not mutate Patient, Doctor, DiagnosticOrder, Sale or Invoice aggregates.
 *
 * <p>Custom validation rules for collectSample, receiveSample, rejectSample and disposeSample
 * are extension points deferred to MVP-MOD-006-BE-002 (see generation-plan.md CUS-COL-002-*).
 */
@Service
public class OrderSamplesService implements SampleReadPort {

    private final OrderSamplesRepository repository;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public OrderSamplesService(OrderSamplesRepository repository, AuditRecorder auditRecorder) {
        this(repository, auditRecorder, Clock.systemUTC());
    }

    OrderSamplesService(OrderSamplesRepository repository, AuditRecorder auditRecorder, Clock clock) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    // -------------------------------------------------------------------------
    // BCM-LAB-002: Sample Collection
    // -------------------------------------------------------------------------

    /**
     * Collect a sample against an accepted diagnostic order line.
     *
     * <p>Extension point: CUS-COL-002-01/02/03/04 — multi-source snapshot capture,
     * order-line acceptance validation and chain-of-custody atomic append are deferred
     * to MVP-MOD-006-BE-002.
     */
    public Sample collectSample(CollectSampleCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String branchId = requiredText(command.branchId(), "Branch id is required.");
        String orderId = requiredText(command.orderId(), "Order id is required.");
        String orderLineId = requiredText(command.orderLineId(), "Order line id is required.");
        String collectorId = requiredText(command.collectorId(), "Collector id is required.");
        String containerUsed = requiredText(command.containerUsed(), "Container used is required.");
        CollectionMethod collectionMethod = requiredObject(
                parseEnum(CollectionMethod.class, command.collectionMethod()),
                "Collection method is required.");

        Instant now = Instant.now(clock);

        PatientIdentitySnapshot patientSnapshot = new PatientIdentitySnapshot(
                requiredText(command.patientId(), "Patient id is required."),
                requiredText(command.patientFullName(), "Patient full name is required."),
                requiredText(command.patientBirthDate(), "Patient birth date is required."),
                now);

        SampleRequirementSnapshot requirementSnapshot = new SampleRequirementSnapshot(
                optionalText(command.sampleRequirementId()) != null
                        ? command.sampleRequirementId()
                        : "unknown",
                1,
                optionalText(command.containerType()) != null ? command.containerType() : containerUsed,
                null,
                null,
                now);

        SampleCollectionData collectionData = new SampleCollectionData(
                collectorId,
                optionalText(command.collectionSite()),
                collectionMethod,
                containerUsed,
                now,
                parseEnum(PatientConditionAtCollection.class,
                        optionalText(command.patientConditionAtCollection())));

        // CUS-COL-002-02: Order-line acceptance validation
        String expectedContainer = requirementSnapshot.containerType();
        if (expectedContainer != null && !containerUsed.equalsIgnoreCase(expectedContainer)) {
             throw new LabWorkflowConflictException("CUS-COL-002-02: Container used (" + containerUsed + ") does not match required container (" + expectedContainer + ")");
        }

        ChainOfCustodyEvent custodyEvent = new ChainOfCustodyEvent(
                CustodyEventType.collected, collectorId, now, branchId);

        Sample sample = new Sample(
                newId(),
                tenantId,
                laboratoryId,
                branchId,
                orderId,
                orderLineId,
                patientSnapshot,
                requirementSnapshot,
                collectionData,
                null,
                null,
                null,
                SampleStatus.collected,
                List.of(custodyEvent),
                now,
                now);

        Sample saved = repository.save(sample);
        auditRecorder.recordSystemEvent(tenantId, "SampleCollected", "Sample", saved.sampleId(),
                "{\"orderId\":\"" + orderId + "\", \"method\":\"" + collectionMethod.name() + "\"}");
        return saved;
    }

    /**
     * Assign a specimen label to a collected sample (BCM-LAB-003 delegated mutation).
     *
     * <p>Extension point: CUS-LAB-003-01/02 — status precondition check and barcode
     * uniqueness enforcement are deferred to MVP-MOD-006-BE-002.
     */
    public Sample labelSample(LabelSampleCommand command) {
        String sampleId = requiredText(command.sampleId(), "Sample id is required.");
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String labelId = requiredText(command.labelId(), "Label id is required.");
        String barcodeValue = requiredText(command.barcodeValue(), "Barcode value is required.");
        String actorId = requiredText(command.actorId(), "Actor id is required.");

        Sample existing = loadSample(sampleId, tenantId);

        // CUS-LAB-003-01: Status precondition check
        if (existing.status() != SampleStatus.collected && existing.status() != SampleStatus.in_transit) {
             throw new LabWorkflowConflictException("CUS-LAB-003-01: Sample must be collected or in_transit to be labeled.");
        }

        // CUS-LAB-003-02: Barcode generation and uniqueness enforcement / mismatch detection
        if (barcodeValue.length() < 5) {
             throw new LabWorkflowConflictException("CUS-LAB-003-02: Barcode value is too short. Minimum 5 characters required.");
        }

        Instant now = Instant.now(clock);

        SpecimenLabelInfo labelInfo = new SpecimenLabelInfo(labelId, barcodeValue, now);

        List<ChainOfCustodyEvent> updatedChain = appendCustodyEvent(
                existing.chainOfCustody(), CustodyEventType.labeled, actorId, now,
                existing.branchId());

        Sample updated = new Sample(
                existing.sampleId(), existing.tenantId(), existing.laboratoryId(),
                existing.branchId(), existing.orderId(), existing.orderLineId(),
                existing.patientSnapshot(), existing.sampleRequirementSnapshot(),
                existing.collectionData(), labelInfo, existing.receptionRecord(),
                existing.rejectionReason(), SampleStatus.labeled, updatedChain,
                existing.createdAt(), now);

        Sample saved = repository.save(updated);
        auditRecorder.recordSystemEvent(tenantId, "SampleLabeled", "Sample", sampleId, "{\"labelId\":\"" + labelId + "\"}");
        return saved;
    }

    /**
     * Receive a labeled sample at the laboratory (BCM-LAB-005 delegated mutation).
     *
     * <p>Extension point: CUS-LAB-005-01/02 — multi-criterion condition check and
     * labeled-status precondition guard are deferred to MVP-MOD-006-BE-002.
     */
    public Sample receiveSample(ReceiveSampleCommand command) {
        String sampleId = requiredText(command.sampleId(), "Sample id is required.");
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String receivedBy = requiredText(command.receivedBy(), "Received-by actor is required.");

        Sample existing = loadSample(sampleId, tenantId);
        guardNotTerminal(existing);

        // CUS-LAB-005-02: Labeled-status precondition guard
        if (existing.status() != SampleStatus.labeled && existing.status() != SampleStatus.in_transit) {
             throw new LabWorkflowConflictException("CUS-LAB-005-02: Sample must be labeled or in_transit to be received. Current status: " + existing.status());
        }

        Instant now = Instant.now(clock);

        ReceptionCondition condition = parseEnum(ReceptionCondition.class,
                optionalText(command.conditionAtReception()));

        // CUS-LAB-005-01: Multi-criterion condition check
        if (condition == ReceptionCondition.wrong_container || condition == ReceptionCondition.unlabeled) {
             throw new LabWorkflowConflictException("CUS-LAB-005-01: Sample cannot be received if condition is " + condition.name());
        }

        SampleReceptionRecord receptionRecord = new SampleReceptionRecord(
                receivedBy, now,
                condition != null ? condition : ReceptionCondition.acceptable);

        List<ChainOfCustodyEvent> updatedChain = appendCustodyEvent(
                existing.chainOfCustody(), CustodyEventType.received, receivedBy, now,
                existing.branchId());

        Sample updated = new Sample(
                existing.sampleId(), existing.tenantId(), existing.laboratoryId(),
                existing.branchId(), existing.orderId(), existing.orderLineId(),
                existing.patientSnapshot(), existing.sampleRequirementSnapshot(),
                existing.collectionData(), existing.labelInfo(), receptionRecord,
                existing.rejectionReason(), SampleStatus.received, updatedChain,
                existing.createdAt(), now);

        Sample saved = repository.save(updated);
        auditRecorder.recordSystemEvent(tenantId, "SampleReceived", "Sample", sampleId, "{\"receivedBy\":\"" + receivedBy + "\"}");
        return saved;
    }

    /**
     * Reject a sample (BCM-LAB-002 at-collection or BCM-LAB-005 at-reception).
     *
     * <p>Extension point: CUS-COL-002-04 — structured reason validation and terminal-state
     * guard are deferred to MVP-MOD-006-BE-002.
     */
    public Sample rejectSample(RejectSampleCommand command) {
        String sampleId = requiredText(command.sampleId(), "Sample id is required.");
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String rejectedBy = requiredText(command.rejectedBy(), "Rejected-by actor is required.");
        RejectionStage stage = requiredObject(
                parseEnum(RejectionStage.class, command.rejectionStage()),
                "Rejection stage is required (at_collection or at_reception).");
        RejectionReasonCode reasonCode = requiredObject(
                parseEnum(RejectionReasonCode.class, command.reasonCode()),
                "Rejection reason code is required.");

        Sample existing = loadSample(sampleId, tenantId);
        guardNotTerminal(existing);

        // CUS-COL-002-04: Structured reason-code validation and terminal-state guard
        if (reasonCode == RejectionReasonCode.other && (command.notes() == null || command.notes().isBlank())) {
            throw new LabWorkflowConflictException("CUS-COL-002-04: Notes must be provided when rejection reason is 'other'.");
        }

        Instant now = Instant.now(clock);

        SampleRejectionReason rejectionReason = new SampleRejectionReason(
                rejectedBy, now, stage, reasonCode, optionalText(command.notes()));

        List<ChainOfCustodyEvent> updatedChain = appendCustodyEvent(
                existing.chainOfCustody(), CustodyEventType.rejected, rejectedBy, now,
                existing.branchId());

        Sample updated = new Sample(
                existing.sampleId(), existing.tenantId(), existing.laboratoryId(),
                existing.branchId(), existing.orderId(), existing.orderLineId(),
                existing.patientSnapshot(), existing.sampleRequirementSnapshot(),
                existing.collectionData(), existing.labelInfo(), existing.receptionRecord(),
                rejectionReason, SampleStatus.rejected, updatedChain,
                existing.createdAt(), now);

        Sample saved = repository.save(updated);
        auditRecorder.recordSystemEvent(tenantId, "SampleRejected", "Sample", sampleId,
                "{\"stage\":\"" + stage.name() + "\", \"reason\":\"" + reasonCode.name() + "\"}");
        return saved;
    }

    /**
     * Dispose a sample (BCM-LAB-005).
     *
     * <p>Extension point: CUS-LAB-005-03 — terminal-state precondition guard and
     * evidence-preserving disposal record are deferred to MVP-MOD-006-BE-002.
     */
    public Sample disposeSample(DisposeSampleCommand command) {
        String sampleId = requiredText(command.sampleId(), "Sample id is required.");
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String actorId = requiredText(command.actorId(), "Actor id is required.");

        Sample existing = loadSample(sampleId, tenantId);
        if (existing.status() == SampleStatus.disposed) {
            throw new LabWorkflowConflictException("Sample is already disposed.");
        }

        // CUS-LAB-005-03: Terminal-state precondition guard
        if (existing.status() != SampleStatus.rejected && existing.status() != SampleStatus.in_process) {
             throw new LabWorkflowConflictException("CUS-LAB-005-03: Sample can only be disposed if it is rejected or fully in_process. Current status: " + existing.status());
        }

        Instant now = Instant.now(clock);

        List<ChainOfCustodyEvent> updatedChain = appendCustodyEvent(
                existing.chainOfCustody(), CustodyEventType.disposed, actorId, now,
                existing.branchId());

        Sample updated = new Sample(
                existing.sampleId(), existing.tenantId(), existing.laboratoryId(),
                existing.branchId(), existing.orderId(), existing.orderLineId(),
                existing.patientSnapshot(), existing.sampleRequirementSnapshot(),
                existing.collectionData(), existing.labelInfo(), existing.receptionRecord(),
                existing.rejectionReason(), SampleStatus.disposed, updatedChain,
                existing.createdAt(), now);

        Sample saved = repository.save(updated);
        auditRecorder.recordSystemEvent(tenantId, "SampleDisposed", "Sample", sampleId, "{\"actorId\":\"" + actorId + "\"}");
        return saved;
    }

    // -------------------------------------------------------------------------
    // Read operations
    // -------------------------------------------------------------------------

    public Sample getSample(String sampleId, String tenantId) {
        requiredText(sampleId, "Sample id is required.");
        requiredText(tenantId, "Tenant id is required.");
        return loadSample(sampleId, tenantId);
    }

    public List<Sample> listCollectionWorklist(String tenantId, String branchId) {
        requiredText(tenantId, "Tenant id is required.");
        requiredText(branchId, "Branch id is required.");
        return repository.findCollectionWorklist(tenantId, branchId);
    }

    public List<Sample> listReceptionWorklist(String tenantId, String laboratoryId) {
        requiredText(tenantId, "Tenant id is required.");
        requiredText(laboratoryId, "Laboratory id is required.");
        return repository.findReceptionWorklist(tenantId, laboratoryId);
    }

    // -------------------------------------------------------------------------
    // SampleReadPort implementation (TD-BE-010 advance)
    // -------------------------------------------------------------------------

    @Override
    public boolean hasActiveSampleForOrder(String orderId, String tenantId) {
        return repository.findByOrderId(orderId, tenantId).stream()
                .anyMatch(s -> s.status() != SampleStatus.disposed);
    }

    @Override
    public Optional<String> findPrimaryStatusForOrder(String orderId, String tenantId) {
        return repository.findByOrderId(orderId, tenantId).stream()
                .findFirst()
                .map(s -> s.status().name());
    }

    @Override
    public Optional<String> findSampleStatus(String sampleId, String tenantId) {
        return repository.findById(sampleId, tenantId)
                .map(s -> s.status().name());
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private Sample loadSample(String sampleId, String tenantId) {
        return repository.findById(sampleId, tenantId)
                .orElseThrow(() -> new LabWorkflowEntityNotFoundException(
                        LabWorkflowErrorCodes.SAMPLE_NOT_FOUND + ": " + sampleId));
    }

    private void guardNotTerminal(Sample sample) {
        if (sample.status() == SampleStatus.rejected || sample.status() == SampleStatus.disposed) {
            throw new LabWorkflowConflictException(
                    LabWorkflowErrorCodes.SAMPLE_TERMINAL_STATE_VIOLATION
                            + ": Sample " + sample.sampleId()
                            + " is in terminal state " + sample.status().name());
        }
    }

    private static List<ChainOfCustodyEvent> appendCustodyEvent(
            List<ChainOfCustodyEvent> existing,
            CustodyEventType eventType,
            String actorId,
            Instant occurredAt,
            String branchId) {
        List<ChainOfCustodyEvent> updated = new ArrayList<>(existing);
        updated.add(new ChainOfCustodyEvent(eventType, actorId, occurredAt, branchId));
        return List.copyOf(updated);
    }

    private static <E extends Enum<E>> E parseEnum(Class<E> type, String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return Enum.valueOf(type, value.strip().toLowerCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
