package com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.application;

import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.optionalText;
import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.requiredText;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.application.PanelCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.panelcatalog.domain.PanelDefinition;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.application.TestCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinition;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionCatalogSelection;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionRequest;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.admissionmanagement.domain.AdmissionRequestRepository;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.CreateDiagnosticOrderCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.DiagnosticOrderManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.OrderLineInput;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.application.ReceptionManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.domain.ReceptionVisit;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskConflictException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskEntityNotFoundException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskErrorCodes;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskPolicyStore;

/**
 * Compiles BCM-ATT-004 (Admission Management) generatable outputs and implements its custom rules
 * RN-001..RN-007. Commit is the completeness gate that delegates order creation to
 * {@link DiagnosticOrderManagementService} (BCM-LAB-001) rather than persisting order state itself
 * (RN-004).
 * <p>
 * <b>MVP-MOD-004-BE-002 refinement:</b> {@link #commit(String, CommitAdmissionRequestCommand)}
 * gates only the acknowledgements the tenant's policy actually requires
 * ({@link FrontDeskPolicyStore#requiredAdmissionAcknowledgementsFor(String)}, both consent and
 * sample-requirement acknowledgement by default), rather than requiring both unconditionally
 * (RN-003). A required acknowledgement that is missing still blocks the commit safely.
 */
@Service
public class AdmissionManagementService {

    private static final List<String> ITEM_KINDS = List.of(
            AdmissionCatalogSelection.KIND_TEST, AdmissionCatalogSelection.KIND_PANEL);

    private final AdmissionRequestRepository repository;
    private final ReceptionManagementService receptionManagementService;
    private final DiagnosticOrderManagementService diagnosticOrderManagementService;
    private final TestCatalogService testCatalogService;
    private final PanelCatalogService panelCatalogService;
    private final FrontDeskPolicyStore policyStore;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public AdmissionManagementService(
            AdmissionRequestRepository repository,
            ReceptionManagementService receptionManagementService,
            DiagnosticOrderManagementService diagnosticOrderManagementService,
            TestCatalogService testCatalogService,
            PanelCatalogService panelCatalogService,
            FrontDeskPolicyStore policyStore,
            AuditRecorder auditRecorder) {
        this(repository, receptionManagementService, diagnosticOrderManagementService, testCatalogService,
                panelCatalogService, policyStore, auditRecorder, Clock.systemUTC());
    }

    AdmissionManagementService(
            AdmissionRequestRepository repository,
            ReceptionManagementService receptionManagementService,
            DiagnosticOrderManagementService diagnosticOrderManagementService,
            TestCatalogService testCatalogService,
            PanelCatalogService panelCatalogService,
            FrontDeskPolicyStore policyStore,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.receptionManagementService = receptionManagementService;
        this.diagnosticOrderManagementService = diagnosticOrderManagementService;
        this.testCatalogService = testCatalogService;
        this.panelCatalogService = panelCatalogService;
        this.policyStore = policyStore;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    /** RN-001: the originating reception visit must have confirmed identity. */
    public AdmissionRequest start(StartAdmissionRequestCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String branchId = requiredText(command.branchId(), "Branch id is required.");
        String visitId = requiredText(command.visitId(), "Reception visit id is required.");
        String patientId = requiredText(command.patientId(), "Patient id is required.");

        ReceptionVisit visit = receptionManagementService.get(visitId);
        if (!visit.identityConfirmed()) {
            throw new FrontDeskConflictException(
                    FrontDeskErrorCodes.ADMISSION_IDENTITY_NOT_CONFIRMED
                            + ": the reception visit must have confirmed identity.");
        }

        Instant now = Instant.now(clock);
        AdmissionRequest admission = new AdmissionRequest(
                newId(), tenantId, laboratoryId, branchId, visitId, patientId, optionalText(command.doctorId()),
                null, false, false, AdmissionRequest.STATUS_DRAFT, null, null,
                optionalText(command.actorId()), 1, now, now);
        AdmissionRequest saved = repository.save(admission);
        auditRecorder.recordSystemEvent(tenantId, "AdmissionRequestStarted", "AdmissionRequest", saved.admissionId(),
                "{\"branchId\":\"%s\",\"visitId\":\"%s\"}".formatted(jsonText(branchId), jsonText(visitId)));
        return saved;
    }

    /** RN-002: catalog selection must be non-empty and every item published. */
    public AdmissionRequest markReady(String admissionId, MarkAdmissionReadyCommand command) {
        AdmissionRequest admission = require(admissionId);
        List<MarkAdmissionReadyCommand.CatalogSelectionInput> selections =
                command.catalogSelection() == null ? List.of() : command.catalogSelection();
        if (selections.isEmpty()) {
            throw new FrontDeskConflictException(
                    FrontDeskErrorCodes.ADMISSION_CATALOG_INCOMPLETE
                            + ": at least one catalog item must be selected.");
        }
        List<AdmissionCatalogSelection> persisted = new ArrayList<>();
        for (MarkAdmissionReadyCommand.CatalogSelectionInput input : selections) {
            String kind = requiredOneOf(input.catalogItemKind(), "Catalog selection kind is invalid.",
                    ITEM_KINDS.toArray(String[]::new));
            String testDefinitionId = requiredText(input.testDefinitionId(), "Catalog selection id is required.");
            validatePublished(kind, testDefinitionId);
            int quantity = input.quantity() == null ? 1 : input.quantity();
            AdmissionCatalogSelection selection = new AdmissionCatalogSelection(
                    newId(), admissionId, testDefinitionId, kind, quantity);
            repository.saveSelection(selection);
            persisted.add(selection);
        }

        AdmissionRequest updated = new AdmissionRequest(
                admission.admissionId(), admission.tenantId(), admission.laboratoryId(), admission.branchId(),
                admission.visitId(), admission.patientId(), admission.doctorId(),
                optionalText(command.clinicalNotesDraft()), admission.consentConfirmed(),
                admission.sampleRequirementsAcknowledged(), AdmissionRequest.STATUS_READY_FOR_ORDER,
                admission.createdOrderId(), admission.rejectionReason(), admission.actorId(),
                admission.version() + 1, admission.createdAt(), Instant.now(clock));
        AdmissionRequest saved = repository.save(updated);
        auditRecorder.recordSystemEvent(saved.tenantId(), "AdmissionMarkedReady", "AdmissionRequest", admissionId,
                "{\"catalogSelectionCount\":%d}".formatted(persisted.size()));
        return saved;
    }

    /**
     * RN-003, RN-004: gates only the tenant-required acknowledgements, then delegates to
     * BCM-LAB-001. A required acknowledgement (consent, sample requirements, or both, per
     * {@link FrontDeskPolicyStore#requiredAdmissionAcknowledgementsFor(String)}) that is missing
     * blocks the commit safely; an acknowledgement the tenant does not require does not.
     */
    public AdmissionRequest commit(String admissionId, CommitAdmissionRequestCommand command) {
        AdmissionRequest admission = require(admissionId);
        if (!AdmissionRequest.STATUS_READY_FOR_ORDER.equals(admission.admissionStatus())) {
            throw new FrontDeskConflictException("Only an admission ready for order can be committed.");
        }
        java.util.Set<String> requiredAcknowledgements =
                policyStore.requiredAdmissionAcknowledgementsFor(admission.tenantId());
        boolean consentMissing = requiredAcknowledgements.contains(FrontDeskPolicyStore.ACK_CONSENT)
                && !command.consentConfirmed();
        boolean sampleAckMissing = requiredAcknowledgements.contains(FrontDeskPolicyStore.ACK_SAMPLE_REQUIREMENTS)
                && !command.sampleRequirementsAcknowledged();
        if (consentMissing || sampleAckMissing) {
            throw new FrontDeskConflictException(
                    FrontDeskErrorCodes.ADMISSION_CONSENT_OR_SAMPLE_ACK_MISSING
                            + ": consent and sample-requirement acknowledgement are required.");
        }

        List<AdmissionCatalogSelection> selections = repository.findSelections(admissionId);
        List<OrderLineInput> lines = selections.stream()
                .map(selection -> new OrderLineInput(selection.testDefinitionId(), selection.catalogItemKind(),
                        selection.quantity()))
                .toList();

        DiagnosticOrder order = diagnosticOrderManagementService.create(new CreateDiagnosticOrderCommand(
                admission.tenantId(), admission.laboratoryId(), admission.branchId(), DiagnosticOrder.CHANNEL_ADMISSION,
                admissionId, admission.patientId(), admission.doctorId(), admission.actorId(), lines));
        diagnosticOrderManagementService.price(order.orderId(), null);
        DiagnosticOrder accepted = diagnosticOrderManagementService.accept(order.orderId(), admission.clinicalNotesDraft());

        AdmissionRequest committed = new AdmissionRequest(
                admission.admissionId(), admission.tenantId(), admission.laboratoryId(), admission.branchId(),
                admission.visitId(), admission.patientId(), admission.doctorId(), admission.clinicalNotesDraft(),
                true, true, AdmissionRequest.STATUS_ORDER_CREATED, accepted.orderId(), admission.rejectionReason(),
                admission.actorId(), admission.version() + 1, admission.createdAt(), Instant.now(clock));
        AdmissionRequest saved = repository.save(committed);
        auditRecorder.recordSystemEvent(saved.tenantId(), "AdmissionRequestCommitted", "AdmissionRequest",
                admissionId, "{\"createdOrderId\":\"%s\",\"branchId\":\"%s\"}"
                        .formatted(jsonText(accepted.orderId()), jsonText(saved.branchId())));
        return saved;
    }

    public AdmissionRequest reject(String admissionId, String rejectionReason) {
        AdmissionRequest admission = require(admissionId);
        String resolvedReason = optionalText(rejectionReason) == null ? "unspecified" : rejectionReason;
        AdmissionRequest rejected = new AdmissionRequest(
                admission.admissionId(), admission.tenantId(), admission.laboratoryId(), admission.branchId(),
                admission.visitId(), admission.patientId(), admission.doctorId(), admission.clinicalNotesDraft(),
                admission.consentConfirmed(), admission.sampleRequirementsAcknowledged(),
                AdmissionRequest.STATUS_REJECTED, admission.createdOrderId(), resolvedReason, admission.actorId(),
                admission.version() + 1, admission.createdAt(), Instant.now(clock));
        AdmissionRequest saved = repository.save(rejected);
        auditRecorder.recordSystemEvent(saved.tenantId(), "AdmissionRequestRejected", "AdmissionRequest",
                admissionId, "{\"rejectionReason\":\"%s\"}".formatted(jsonText(resolvedReason)));
        return saved;
    }

    public AdmissionRequest get(String admissionId) {
        return require(admissionId);
    }

    public List<AdmissionRequest> list(String tenantId) {
        return repository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    public List<AdmissionCatalogSelection> getSelections(String admissionId) {
        require(admissionId);
        return repository.findSelections(admissionId);
    }

    private void validatePublished(String kind, String testDefinitionId) {
        if (AdmissionCatalogSelection.KIND_TEST.equals(kind)) {
            TestDefinition testDefinition = testCatalogService.get(testDefinitionId);
            if (!TestDefinition.STATUS_PUBLISHED.equals(testDefinition.status())) {
                throw new FrontDeskConflictException(
                        FrontDeskErrorCodes.ADMISSION_CATALOG_INCOMPLETE
                                + ": test " + testDefinitionId + " is not published.");
            }
        } else {
            PanelDefinition panelDefinition = panelCatalogService.get(testDefinitionId);
            if (!PanelDefinition.STATUS_PUBLISHED.equals(panelDefinition.status())) {
                throw new FrontDeskConflictException(
                        FrontDeskErrorCodes.ADMISSION_CATALOG_INCOMPLETE
                                + ": panel " + testDefinitionId + " is not published.");
            }
        }
    }

    private AdmissionRequest require(String admissionId) {
        return repository.findById(requiredText(admissionId, "Admission id is required."))
                .orElseThrow(() -> new FrontDeskEntityNotFoundException("Admission request was not found."));
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String jsonText(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
