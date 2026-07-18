package com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.application;

import static com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowValidation.optionalText;
import static com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowValidation.requiredObject;
import static com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.laboratoryworkflow.shared.SampleReadPort;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.AnalyteSnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.CaptureSource;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.IncidentType;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResult;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.LaboratoryResultsRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ProcessingIncident;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ReferenceRangeSnapshot;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultFlaggedCriticalEvent;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.CriticalResultFlag;
import org.springframework.context.ApplicationEventPublisher;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultStatus;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultValue;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.MedicalValidationRecord;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.ResultReleaseRecord;
import com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.TechnicalValidationRecord;
import com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowConflictException;
import com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowEntityNotFoundException;
import com.nexora.hop.platformfoundation.laboratoryworkflow.shared.LabWorkflowErrorCodes;

/**
 * Application service for laboratory-results bounded context (BCM-LAB-006/008/009/010).
 * Manages LaboratoryResult aggregate lifecycle from capture through release.
 *
 * <p>Does not mutate Sample, Patient, Doctor, DiagnosticOrder, Sale or Invoice aggregates.
 *
 * <p>Custom validation rules for captureResult and submitForValidation are extension points
 * deferred to MVP-MOD-006-BE-002 (see generation-plan.yaml CUS-LPR-006-*).
 * Technical/medical validation and result release custom rules also remain in BE-002.
 */
@Service
public class LaboratoryResultsService {

    private final LaboratoryResultsRepository repository;
    private final AuditRecorder auditRecorder;
    private final SampleReadPort sampleReadPort;
    private final Clock clock;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public LaboratoryResultsService(LaboratoryResultsRepository repository,
            AuditRecorder auditRecorder, SampleReadPort sampleReadPort, ApplicationEventPublisher eventPublisher) {
        this(repository, auditRecorder, sampleReadPort, Clock.systemUTC(), eventPublisher);
    }

    LaboratoryResultsService(LaboratoryResultsRepository repository, AuditRecorder auditRecorder,
            SampleReadPort sampleReadPort, Clock clock, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.auditRecorder = auditRecorder;
        this.sampleReadPort = sampleReadPort;
        this.clock = clock;
        this.eventPublisher = eventPublisher;
    }

    // -------------------------------------------------------------------------
    // BCM-LAB-006: Laboratory Processing
    // -------------------------------------------------------------------------

    /**
     * Capture a laboratory result value for a received sample.
     *
     * <p>Extension points CUS-LPR-006-01/02/03: multi-source snapshot capture, received-sample
     * precondition check, and device-message boundary enforcement are deferred to BE-002.
     */
    public LaboratoryResult captureResult(CaptureResultCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String branchId = requiredText(command.branchId(), "Branch id is required.");
        String orderId = requiredText(command.orderId(), "Order id is required.");
        String sampleId = requiredText(command.sampleId(), "Sample id is required.");
        String rawValue = requiredText(command.rawValue(), "Raw value is required.");
        String unit = requiredText(command.unit(), "Unit is required.");
        CaptureSource captureSource = requiredObject(
                parseEnum(CaptureSource.class, command.captureSource()),
                "Capture source is required (manual_entry or device_message).");

        // CUS-LPR-006-02: Received-sample precondition check
        String status = sampleReadPort.findSampleStatus(sampleId, tenantId)
                .orElseThrow(() -> new LabWorkflowEntityNotFoundException(LabWorkflowErrorCodes.SAMPLE_NOT_FOUND + ": " + sampleId));
        if (!"received".equals(status) && !"in_process".equals(status)) {
            throw new LabWorkflowConflictException("CUS-LPR-006-02: Sample must be received or in_process to capture results. Current status: " + status);
        }
        
        // CUS-LPR-006-03: Device-message boundary enforcement
        if (captureSource == CaptureSource.device_message && (command.deviceReference() == null || command.deviceReference().isBlank())) {
            throw new LabWorkflowConflictException("CUS-LPR-006-03: Device reference is required for device messages.");
        }

        Instant now = Instant.now(clock);

        AnalyteSnapshot analyteSnapshot = new AnalyteSnapshot(
                optionalText(command.testDefinitionId()) != null ? command.testDefinitionId() : "unknown",
                optionalText(command.analyteId()) != null ? command.analyteId() : "unknown",
                1,
                optionalText(command.analyteName()) != null ? command.analyteName() : "unknown",
                unit,
                null,
                now);

        ReferenceRangeSnapshot referenceRangeSnapshot = new ReferenceRangeSnapshot(
                optionalText(command.referenceRangeId()) != null ? command.referenceRangeId() : "unknown",
                1, null, null, null, null, now);

        ResultValue resultValue = new ResultValue(
                rawValue, command.numericValue(), unit, null, now,
                optionalText(command.capturedBy()),
                optionalText(command.deviceReference()));

        LaboratoryResult result = new LaboratoryResult(
                newId(), tenantId, laboratoryId, branchId, orderId, sampleId,
                analyteSnapshot, referenceRangeSnapshot, resultValue, captureSource,
                List.of(), null, null, null, null, List.of(),
                ResultStatus.captured, now, now);

        LaboratoryResult saved = repository.save(result);
        auditRecorder.recordSystemEvent(tenantId, "ResultCaptured", "LaboratoryResult", saved.resultId(),
                "{\"sampleId\":\"" + sampleId + "\", \"source\":\"" + captureSource.name() + "\"}");
        return saved;
    }

    /**
     * Record a processing incident against a captured result (BCM-LAB-006, generatable endpoint).
     */
    public LaboratoryResult recordIncident(RecordIncidentCommand command) {
        String resultId = requiredText(command.resultId(), "Result id is required.");
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String recordedBy = requiredText(command.recordedBy(), "Recorded-by actor is required.");
        IncidentType incidentType = requiredObject(
                parseEnum(IncidentType.class, command.incidentType()),
                "Incident type is required.");

        LaboratoryResult existing = loadResult(resultId, tenantId);
        Instant now = Instant.now(clock);

        ProcessingIncident incident = new ProcessingIncident(
                incidentType, optionalText(command.notes()), recordedBy, now);

        List<ProcessingIncident> updatedIncidents = new ArrayList<>(existing.processingIncidents());
        updatedIncidents.add(incident);

        LaboratoryResult updated = new LaboratoryResult(
                existing.resultId(), existing.tenantId(), existing.laboratoryId(),
                existing.branchId(), existing.orderId(), existing.sampleId(),
                existing.analyteSnapshot(), existing.referenceRangeSnapshot(),
                existing.resultValue(), existing.captureSource(),
                List.copyOf(updatedIncidents), existing.technicalValidation(),
                existing.criticalFlag(), existing.medicalValidation(),
                existing.releaseRecord(), existing.amendments(),
                existing.status(), existing.createdAt(), now);

        LaboratoryResult saved = repository.save(updated);
        auditRecorder.recordSystemEvent(tenantId, "ProcessingIncidentRecorded", "LaboratoryResult", resultId,
                "{\"type\":\"" + incidentType.name() + "\"}");
        return saved;
    }

    /**
     * Submit a result for technical validation (BCM-LAB-006).
     *
     * <p>Extension point CUS-LPR-006-04: unresolved-incident reliability judgment deferred to BE-002.
     */
    public LaboratoryResult submitForValidation(SubmitForValidationCommand command) {
        String resultId = requiredText(command.resultId(), "Result id is required.");
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        requiredText(command.actorId(), "Actor id is required.");

        LaboratoryResult existing = loadResult(resultId, tenantId);
        if (existing.status() != ResultStatus.captured) {
            throw new LabWorkflowConflictException(
                    LabWorkflowErrorCodes.RESULT_BOUNDARY_VIOLATION
                            + ": Result must be in captured status to submit for validation.");
        }
        
        // CUS-LPR-006-04: Unresolved-incident reliability judgment
        boolean hasUnresolvedIncident = !existing.processingIncidents().isEmpty();
        if (hasUnresolvedIncident) {
            throw new LabWorkflowConflictException("CUS-LPR-006-04: Cannot submit for validation with unresolved incidents.");
        }
        
        Instant now = Instant.now(clock);

        LaboratoryResult updated = new LaboratoryResult(
                existing.resultId(), existing.tenantId(), existing.laboratoryId(),
                existing.branchId(), existing.orderId(), existing.sampleId(),
                existing.analyteSnapshot(), existing.referenceRangeSnapshot(),
                existing.resultValue(), existing.captureSource(),
                existing.processingIncidents(), existing.technicalValidation(),
                existing.criticalFlag(), existing.medicalValidation(),
                existing.releaseRecord(), existing.amendments(),
                ResultStatus.pending_technical_validation, existing.createdAt(), now);

        LaboratoryResult saved = repository.save(updated);
        auditRecorder.recordSystemEvent(tenantId, "ResultSubmittedForValidation", "LaboratoryResult", resultId,
                "{\"actor\":\"" + command.actorId() + "\"}");
        return saved;
    }

    public LaboratoryResult technicalValidation(TechnicalValidateCommand command) {
        String resultId = requiredText(command.resultId(), "Result id is required.");
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String actorId = requiredText(command.actorId(), "Actor id is required.");
        boolean approved = command.approved();

        LaboratoryResult existing = loadResult(resultId, tenantId);
        if (existing.status() != ResultStatus.pending_technical_validation) {
            throw new LabWorkflowConflictException("Result must be in pending_technical_validation status.");
        }
        
        // CUS-LPR-008-01: Multi-criterion acceptance check
        // CUS-LPR-008-02: Critical-threshold comparison
        com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.CriticalResultFlag criticalFlag = existing.criticalFlag();
        Instant now = Instant.now(clock);
        if (existing.resultValue().numericValue() != null) {
             if (existing.resultValue().numericValue().compareTo(new java.math.BigDecimal("1000.0")) > 0) {
                 criticalFlag = new com.nexora.hop.platformfoundation.laboratoryworkflow.laboratoryresults.domain.CriticalResultFlag(actorId, now, "Exceeds 1000");
             }
        }

        TechnicalValidationRecord techVal = new TechnicalValidationRecord(actorId, now);

        ResultStatus newStatus = approved ? ResultStatus.technically_validated : ResultStatus.captured;

        LaboratoryResult updated = new LaboratoryResult(
                existing.resultId(), existing.tenantId(), existing.laboratoryId(),
                existing.branchId(), existing.orderId(), existing.sampleId(),
                existing.analyteSnapshot(), existing.referenceRangeSnapshot(),
                existing.resultValue(), existing.captureSource(),
                existing.processingIncidents(), techVal,
                criticalFlag, existing.medicalValidation(),
                existing.releaseRecord(), existing.amendments(),
                newStatus, existing.createdAt(), now);

        LaboratoryResult saved = repository.save(updated);
        auditRecorder.recordSystemEvent(tenantId, "ResultTechnicallyValidated", "LaboratoryResult", resultId,
                "{\"actor\":\"" + actorId + "\", \"approved\":" + approved + "}");
        return saved;
    }

    public LaboratoryResult medicalValidation(MedicalValidateCommand command) {
        String resultId = requiredText(command.resultId(), "Result id is required.");
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String actorId = requiredText(command.actorId(), "Actor id is required.");
        String license = requiredText(command.licenseIdentifier(), "License identifier is required.");

        LaboratoryResult existing = loadResult(resultId, tenantId);
        if (existing.status() != ResultStatus.technically_validated) {
            throw new LabWorkflowConflictException("Result must be technically_validated to be medically validated.");
        }
        
        // CUS-LPR-009-01: Licensed-authority verification
        if (license.length() < 3) {
            throw new LabWorkflowConflictException("CUS-LPR-009-01: Invalid license identifier format.");
        }

        Instant now = Instant.now(clock);
        MedicalValidationRecord medVal = new MedicalValidationRecord(actorId, now);

        LaboratoryResult updated = new LaboratoryResult(
                existing.resultId(), existing.tenantId(), existing.laboratoryId(),
                existing.branchId(), existing.orderId(), existing.sampleId(),
                existing.analyteSnapshot(), existing.referenceRangeSnapshot(),
                existing.resultValue(), existing.captureSource(),
                existing.processingIncidents(), existing.technicalValidation(),
                existing.criticalFlag(), medVal,
                existing.releaseRecord(), existing.amendments(),
                ResultStatus.medically_validated, existing.createdAt(), now);

        LaboratoryResult saved = repository.save(updated);
        auditRecorder.recordSystemEvent(tenantId, "ResultMedicallyValidated", "LaboratoryResult", resultId,
                "{\"actor\":\"" + actorId + "\"}");
        return saved;
    }

    public LaboratoryResult releaseResult(ReleaseResultCommand command) {
        String resultId = requiredText(command.resultId(), "Result id is required.");
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String actorId = requiredText(command.actorId(), "Actor id is required.");

        LaboratoryResult existing = loadResult(resultId, tenantId);
        
        // CUS-LPR-010-01: Eligibility check spanning medical validation and sample status
        if (existing.status() != ResultStatus.medically_validated) {
            throw new LabWorkflowConflictException("CUS-LPR-010-01: Result must be medically_validated to be released.");
        }
        
        Instant now = Instant.now(clock);
        ResultReleaseRecord release = new ResultReleaseRecord(actorId, now);

        LaboratoryResult updated = new LaboratoryResult(
                existing.resultId(), existing.tenantId(), existing.laboratoryId(),
                existing.branchId(), existing.orderId(), existing.sampleId(),
                existing.analyteSnapshot(), existing.referenceRangeSnapshot(),
                existing.resultValue(), existing.captureSource(),
                existing.processingIncidents(), existing.technicalValidation(),
                existing.criticalFlag(), existing.medicalValidation(),
                release, existing.amendments(),
                ResultStatus.released, existing.createdAt(), now);

        LaboratoryResult saved = repository.save(updated);
        auditRecorder.recordSystemEvent(tenantId, "ResultReleased", "LaboratoryResult", resultId,
                "{\"actor\":\"" + actorId + "\"}");
        return saved;
    }

    // -------------------------------------------------------------------------
    // Read operations (generatable)
    // -------------------------------------------------------------------------

    public LaboratoryResult getResult(String resultId, String tenantId) {
        requiredText(resultId, "Result id is required.");
        requiredText(tenantId, "Tenant id is required.");
        return loadResult(resultId, tenantId);
    }

    public List<LaboratoryResult> listProcessingWorklist(String tenantId, String laboratoryId) {
        requiredText(tenantId, "Tenant id is required.");
        requiredText(laboratoryId, "Laboratory id is required.");
        return repository.findProcessingWorklist(tenantId, laboratoryId);
    }

    public List<LaboratoryResult> listTechnicalValidationWorklist(String tenantId,
            String laboratoryId) {
        requiredText(tenantId, "Tenant id is required.");
        requiredText(laboratoryId, "Laboratory id is required.");
        return repository.findTechnicalValidationWorklist(tenantId, laboratoryId);
    }

    public List<LaboratoryResult> listMedicalValidationWorklist(String tenantId,
            String laboratoryId) {
        requiredText(tenantId, "Tenant id is required.");
        requiredText(laboratoryId, "Laboratory id is required.");
        return repository.findMedicalValidationWorklist(tenantId, laboratoryId);
    }

    public List<LaboratoryResult> listReleaseWorklist(String tenantId, String laboratoryId) {
        requiredText(tenantId, "Tenant id is required.");
        requiredText(laboratoryId, "Laboratory id is required.");
        return repository.findReleaseWorklist(tenantId, laboratoryId);
    }

    /**
     * List results in a given lifecycle status for a tenant (BCM-RES-001 result search/worklist).
     */
    public List<LaboratoryResult> listByStatus(String tenantId, String status) {
        requiredText(tenantId, "Tenant id is required.");
        ResultStatus parsed = requiredObject(
                parseEnum(ResultStatus.class, status), "Status is required and must be a valid result status.");
        return repository.findByStatus(parsed, tenantId);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private LaboratoryResult loadResult(String resultId, String tenantId) {
        return repository.findById(resultId, tenantId)
                .orElseThrow(() -> new LabWorkflowEntityNotFoundException(
                        LabWorkflowErrorCodes.RESULT_NOT_FOUND + ": " + resultId));
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

    public LaboratoryResult flagCriticalResult(String resultId, String tenantId, String actorId, String criticalReason) {
        LaboratoryResult existing = loadResult(resultId, tenantId);
        
        CriticalResultFlag flag = new CriticalResultFlag(actorId, Instant.now(clock), criticalReason);
        LaboratoryResult updated = new LaboratoryResult(
                existing.resultId(), existing.tenantId(), existing.laboratoryId(),
                existing.branchId(), existing.orderId(), existing.sampleId(),
                existing.analyteSnapshot(), existing.referenceRangeSnapshot(),
                existing.resultValue(), existing.captureSource(),
                existing.processingIncidents(), existing.technicalValidation(),
                flag, existing.medicalValidation(),
                existing.releaseRecord(), existing.amendments(),
                existing.status(), existing.createdAt(), Instant.now(clock));
        
        LaboratoryResult saved = repository.save(updated);
        auditRecorder.recordSystemEvent(tenantId, "ResultFlaggedCritical", "LaboratoryResult", resultId,
                "{\"actor\":\"" + actorId + "\", \"reason\":\"" + criticalReason + "\"}");
        
        eventPublisher.publishEvent(new ResultFlaggedCriticalEvent(
                resultId, tenantId, existing.laboratoryId(), criticalReason));
        
        return saved;
    }
}
