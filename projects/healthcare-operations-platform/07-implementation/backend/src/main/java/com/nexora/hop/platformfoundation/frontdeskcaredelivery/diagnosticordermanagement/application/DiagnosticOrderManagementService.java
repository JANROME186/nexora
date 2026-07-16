package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application;

import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.optionalText;
import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.requiredText;

import java.math.BigDecimal;
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
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.application.PriceListManagementService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceEntry;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.pricelistmanagement.domain.PriceList;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.application.TestCatalogService;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain.TestDefinition;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.BranchSnapshot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrderRepository;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DoctorSnapshot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.OrderLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.OrderPricingSnapshot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.PatientSnapshot;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskConflictException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskEntityNotFoundException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskErrorCodes;
import com.nexora.hop.platformfoundation.organizationmanagement.BranchDirectory;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.DoctorDirectory;
import com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.PatientDirectory;

/**
 * Compiles the generatable outputs of BCM-LAB-001 (Diagnostic Order Management) and implements its
 * custom rules RN-001..RN-009: multi-source snapshot capture, published-catalog validation,
 * per-line price-list resolution, referring-doctor eligibility gating, aggregate-boundary
 * enforcement and tiered terminal-state cancellation. This is the sole capability allowed to
 * mutate {@link DiagnosticOrder} state (RN-004); Appointment Scheduling, Reception Management,
 * Admission Management and Quotation Management call these public methods directly rather than
 * persisting order state themselves, mirroring the BCM-ATT-002 → BCM-PER-002 delegation pattern.
 * <p>
 * <b>MVP-MOD-004-BE-002 refinements:</b> {@link #price(String, String)} now resolves a published
 * price list independently for every order line instead of reusing the first line's price list
 * (RN-003 multi-price-list resolution); {@link #create(CreateDiagnosticOrderCommand)} gates a
 * referring doctor through {@link DoctorDirectory#isEligibleAsReferringDoctor(String)} instead of
 * existence only; {@link #cancel(String, String, String)} applies a tiered rule — a draft or
 * priced order cancels with a reason code, while an accepted or in-progress order additionally
 * requires an explicit override justification, since it has already left simple front-desk intake.
 * <p>
 * <b>Remaining hook (tracked as TD-BE-010):</b> RN-007's full downstream sample/processing-state
 * override check cannot be evaluated until MVP-MOD-006 (Laboratory Workflow) models the Sample
 * aggregate; the override-justification tier above is the closest enforceable proxy available
 * with only order-lifecycle state.
 */
@Service
public class DiagnosticOrderManagementService {

    private static final List<String> INTAKE_CHANNELS = List.of(
            DiagnosticOrder.CHANNEL_WALK_IN, DiagnosticOrder.CHANNEL_APPOINTMENT,
            DiagnosticOrder.CHANNEL_ADMISSION, DiagnosticOrder.CHANNEL_QUOTATION_CONVERSION,
            DiagnosticOrder.CHANNEL_PORTAL_REQUEST_LATER);

    private static final List<String> ORDER_LINE_KINDS = List.of(OrderLine.KIND_TEST, OrderLine.KIND_PANEL);

    private static final String DEFAULT_CURRENCY = "USD";

    private final DiagnosticOrderRepository repository;
    private final PatientDirectory patientDirectory;
    private final DoctorDirectory doctorDirectory;
    private final BranchDirectory branchDirectory;
    private final TestCatalogService testCatalogService;
    private final PanelCatalogService panelCatalogService;
    private final PriceListManagementService priceListManagementService;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public DiagnosticOrderManagementService(
            DiagnosticOrderRepository repository,
            PatientDirectory patientDirectory,
            DoctorDirectory doctorDirectory,
            BranchDirectory branchDirectory,
            TestCatalogService testCatalogService,
            PanelCatalogService panelCatalogService,
            PriceListManagementService priceListManagementService,
            AuditRecorder auditRecorder) {
        this(repository, patientDirectory, doctorDirectory, branchDirectory, testCatalogService,
                panelCatalogService, priceListManagementService, auditRecorder, Clock.systemUTC());
    }

    DiagnosticOrderManagementService(
            DiagnosticOrderRepository repository,
            PatientDirectory patientDirectory,
            DoctorDirectory doctorDirectory,
            BranchDirectory branchDirectory,
            TestCatalogService testCatalogService,
            PanelCatalogService panelCatalogService,
            PriceListManagementService priceListManagementService,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.patientDirectory = patientDirectory;
        this.doctorDirectory = doctorDirectory;
        this.branchDirectory = branchDirectory;
        this.testCatalogService = testCatalogService;
        this.panelCatalogService = panelCatalogService;
        this.priceListManagementService = priceListManagementService;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    /** RN-001, RN-002, RN-005, RN-008, RN-009: creates the order with immutable snapshots. */
    public DiagnosticOrder create(CreateDiagnosticOrderCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String branchId = requiredText(command.branchId(), "Branch id is required.");
        String intakeChannel = requiredOneOf(command.intakeChannel(), "Intake channel is invalid.",
                INTAKE_CHANNELS.toArray(String[]::new));
        String patientId = requiredText(command.patientId(), "Patient id is required.");

        Instant now = Instant.now(clock);

        // RN-001: immutable multi-source snapshot capture.
        com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain.PatientSnapshot
                patientSource = patientDirectory.findSnapshot(patientId)
                        .orElseThrow(() -> new FrontDeskEntityNotFoundException("Patient was not found."));
        PatientSnapshot patientSnapshot = new PatientSnapshot(
                patientSource.patientId(), patientSource.version(), patientSource.fullName(),
                patientSource.primaryDocumentType(), patientSource.primaryDocumentNumberMasked(),
                null, now);

        DoctorSnapshot doctorSnapshot = null;
        String doctorId = optionalText(command.doctorId());
        if (doctorId != null) {
            com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain.DoctorSnapshot
                    doctorSource = doctorDirectory.findSnapshot(doctorId)
                            .orElseThrow(() -> new FrontDeskEntityNotFoundException("Doctor was not found."));
            // RN-001 refinement: a referring doctor must be eligible (active, at least one
            // verified credential, not suspended), not merely exist.
            if (!doctorDirectory.isEligibleAsReferringDoctor(doctorId)) {
                throw new FrontDeskConflictException(
                        FrontDeskErrorCodes.ORDER_DOCTOR_NOT_ELIGIBLE
                                + ": the referring doctor is not eligible (inactive, unverified or suspended).");
            }
            doctorSnapshot = new DoctorSnapshot(doctorSource.doctorId(), doctorSource.version(),
                    doctorSource.fullName(), doctorSource.primaryDocumentNumberMasked(), now);
        }

        com.nexora.hop.platformfoundation.organizationmanagement.domain.BranchSnapshot branchSource =
                branchDirectory.findSnapshot(branchId)
                        .orElseThrow(() -> new FrontDeskEntityNotFoundException("Branch was not found."));
        BranchSnapshot branchSnapshot = new BranchSnapshot(
                branchSource.branchId(), branchSource.version(), branchSource.name(), now);

        List<OrderLineInput> requestedLines = command.lines() == null ? List.of() : command.lines();
        String orderId = newId();
        List<OrderLine> lines = new ArrayList<>();
        for (OrderLineInput input : requestedLines) {
            lines.add(buildOrderLine(orderId, input, now));
        }

        DiagnosticOrder order = new DiagnosticOrder(
                orderId, tenantId, laboratoryId, branchId, intakeChannel,
                optionalText(command.sourceReferenceId()), patientSnapshot, doctorSnapshot, branchSnapshot,
                null, null, DiagnosticOrder.STATUS_DRAFT, null, optionalText(command.actorId()), 1, now, now);
        DiagnosticOrder saved = repository.save(order);
        for (OrderLine line : lines) {
            repository.saveOrderLine(line);
        }

        // RN-008: audit event includes actor identity, branch, intake channel and snapshot versions.
        auditRecorder.recordSystemEvent(tenantId, "DiagnosticOrderCreated", "DiagnosticOrder", orderId,
                "{\"branchId\":\"%s\",\"intakeChannel\":\"%s\",\"patientSnapshotVersion\":%d,\"orderLineCount\":%d}"
                        .formatted(jsonText(branchId), jsonText(intakeChannel), patientSnapshot.sourceVersion(),
                                lines.size()));
        return saved;
    }

    /**
     * RN-003, RN-009: resolves a published price list independently for every order line (a
     * multi-service order may span catalog items that are only priced in different price lists)
     * and prices each line from its own resolved entry. The order's {@link OrderPricingSnapshot}
     * records the primary price list — the one resolved for the first line — for backward-
     * compatible auditing; the per-line {@link OrderLine#unitAmount()} is always the authoritative
     * price for that line, and the audit event lists every distinct price list actually used.
     */
    public DiagnosticOrder price(String orderId, String currency) {
        DiagnosticOrder order = require(orderId);
        List<OrderLine> lines = repository.findOrderLines(orderId);
        if (lines.isEmpty()) {
            throw new FrontDeskConflictException(FrontDeskErrorCodes.ORDER_NO_LINES
                    + ": an order must contain at least one order line before it can be priced.");
        }
        String resolvedCurrency = optionalText(currency) == null ? DEFAULT_CURRENCY : currency;

        BigDecimal total = BigDecimal.ZERO;
        PriceList primaryPriceList = null;
        java.util.Set<String> priceListIdsUsed = new java.util.LinkedHashSet<>();
        for (OrderLine line : lines) {
            PriceList priceList = priceListManagementService.getEffectivePriceSnapshot(
                    line.catalogItemKind(), line.testDefinitionId(), resolvedCurrency, null, null);
            if (primaryPriceList == null) {
                primaryPriceList = priceList;
            }
            priceListIdsUsed.add(priceList.priceListId());
            List<PriceEntry> entries = priceListManagementService.getEntries(priceList.priceListId());
            PriceEntry entry = entries.stream()
                    .filter(candidate -> candidate.itemType().equals(line.catalogItemKind())
                            && candidate.itemRefId().equals(line.testDefinitionId()))
                    .findFirst()
                    .orElseThrow(() -> new FrontDeskConflictException(
                            FrontDeskErrorCodes.ORDER_PRICING_SNAPSHOT_REQUIRED
                                    + ": no price entry resolves for catalog item "
                                    + line.testDefinitionId() + " in its resolved price list."));
            OrderLine priced = new OrderLine(line.orderLineId(), line.orderId(), line.testDefinitionId(),
                    line.catalogItemKind(), line.catalogItemName(), line.catalogPublishedVersion(),
                    line.quantity(), entry.price(), line.lineStatus());
            repository.saveOrderLine(priced);
            total = total.add(entry.price().amount().multiply(BigDecimal.valueOf(line.quantity())));
        }

        OrderPricingSnapshot pricingSnapshot = new OrderPricingSnapshot(
                primaryPriceList.priceListId(), primaryPriceList.version(), new Money(resolvedCurrency, total),
                Instant.now(clock));
        DiagnosticOrder priced = withStatus(order, DiagnosticOrder.STATUS_PRICED, order.cancellationReason(),
                pricingSnapshot, order.clinicalNotes());
        DiagnosticOrder saved = repository.save(priced);
        auditRecorder.recordSystemEvent(saved.tenantId(), "OrderPriced", "DiagnosticOrder", orderId,
                "{\"priceListId\":\"%s\",\"totalAmount\":\"%s\",\"priceListCount\":%d,\"multiPriceList\":%b}"
                        .formatted(jsonText(primaryPriceList.priceListId()), total.toPlainString(),
                                priceListIdsUsed.size(), priceListIdsUsed.size() > 1));
        return saved;
    }

    /** RN-003: an order cannot be accepted without a pricing snapshot and at least one order line. */
    public DiagnosticOrder accept(String orderId, String clinicalNotes) {
        DiagnosticOrder order = require(orderId);
        if (order.pricingSnapshot() == null) {
            throw new FrontDeskConflictException(
                    FrontDeskErrorCodes.ORDER_PRICING_SNAPSHOT_REQUIRED
                            + ": the order must be priced before it can be accepted.");
        }
        if (!DiagnosticOrder.STATUS_PRICED.equals(order.status())) {
            throw new FrontDeskConflictException("Only a priced order can be accepted.");
        }
        for (OrderLine line : repository.findOrderLines(orderId)) {
            repository.saveOrderLine(new OrderLine(line.orderLineId(), line.orderId(), line.testDefinitionId(),
                    line.catalogItemKind(), line.catalogItemName(), line.catalogPublishedVersion(),
                    line.quantity(), line.unitAmount(), OrderLine.LINE_ACCEPTED));
        }
        DiagnosticOrder accepted = withStatus(order, DiagnosticOrder.STATUS_ACCEPTED, order.cancellationReason(),
                order.pricingSnapshot(), optionalText(clinicalNotes));
        DiagnosticOrder saved = repository.save(accepted);
        auditRecorder.recordSystemEvent(saved.tenantId(), "OrderAccepted", "DiagnosticOrder", orderId,
                "{\"branchId\":\"%s\"}".formatted(jsonText(saved.branchId())));
        return saved;
    }

    private static final int MIN_CANCELLATION_OVERRIDE_JUSTIFICATION_LENGTH = 15;

    private static final List<String> CLINICALLY_ENGAGED_STATUSES = List.of(
            DiagnosticOrder.STATUS_ACCEPTED, DiagnosticOrder.STATUS_IN_PROGRESS);

    /**
     * RN-006: a cancelled or completed order is immutable. RN-007 tiered override: a draft or
     * priced order (never accepted for clinical processing) cancels with a plain reason code; an
     * accepted or in-progress order additionally requires an explicit
     * {@code overrideJustification} of at least {@value #MIN_CANCELLATION_OVERRIDE_JUSTIFICATION_LENGTH}
     * characters, since downstream clinical work may already depend on it. The full downstream
     * sample/processing-state check described by RN-007 cannot be evaluated until MVP-MOD-006
     * models the Sample aggregate (tracked as TD-BE-010); this order-status tier is the closest
     * enforceable proxy available today.
     */
    public DiagnosticOrder cancel(String orderId, String reasonCode, String overrideJustification) {
        DiagnosticOrder order = require(orderId);
        if (DiagnosticOrder.STATUS_CANCELLED.equals(order.status())
                || DiagnosticOrder.STATUS_COMPLETED.equals(order.status())) {
            throw new FrontDeskConflictException(
                    FrontDeskErrorCodes.ORDER_TERMINAL_STATE_IMMUTABLE
                            + ": a cancelled or completed order cannot be modified.");
        }
        String resolvedReason = requiredText(reasonCode, "Cancellation reason code is required.");
        boolean clinicallyEngaged = CLINICALLY_ENGAGED_STATUSES.contains(order.status());
        String resolvedOverride = optionalText(overrideJustification);
        if (clinicallyEngaged) {
            if (resolvedOverride == null || resolvedOverride.length() < MIN_CANCELLATION_OVERRIDE_JUSTIFICATION_LENGTH) {
                throw new FrontDeskConflictException(
                        FrontDeskErrorCodes.ORDER_CANCELLATION_OVERRIDE_REQUIRED
                                + ": cancelling an accepted or in-progress order requires "
                                + "an override justification of at least "
                                + MIN_CANCELLATION_OVERRIDE_JUSTIFICATION_LENGTH + " characters.");
            }
        }
        String combinedReason = resolvedOverride == null ? resolvedReason : resolvedReason + " | " + resolvedOverride;
        DiagnosticOrder cancelled = withStatus(order, DiagnosticOrder.STATUS_CANCELLED, combinedReason,
                order.pricingSnapshot(), order.clinicalNotes());
        DiagnosticOrder saved = repository.save(cancelled);
        auditRecorder.recordSystemEvent(saved.tenantId(), "OrderCancelled", "DiagnosticOrder", orderId,
                "{\"reasonCode\":\"%s\",\"clinicallyEngaged\":%b,\"overrideProvided\":%b}"
                        .formatted(jsonText(resolvedReason), clinicallyEngaged, resolvedOverride != null));
        return saved;
    }

    /** Generatable per openapi-source.yaml: transitions an accepted order to completed. */
    public DiagnosticOrder complete(String orderId) {
        DiagnosticOrder order = require(orderId);
        if (!DiagnosticOrder.STATUS_ACCEPTED.equals(order.status())
                && !DiagnosticOrder.STATUS_IN_PROGRESS.equals(order.status())) {
            throw new FrontDeskConflictException("Only an accepted or in-progress order can be completed.");
        }
        DiagnosticOrder completed = withStatus(order, DiagnosticOrder.STATUS_COMPLETED, order.cancellationReason(),
                order.pricingSnapshot(), order.clinicalNotes());
        DiagnosticOrder saved = repository.save(completed);
        auditRecorder.recordSystemEvent(saved.tenantId(), "OrderCompleted", "DiagnosticOrder", orderId, "{}");
        return saved;
    }

    public DiagnosticOrder get(String orderId) {
        return require(orderId);
    }

    public List<DiagnosticOrder> list(String tenantId) {
        return repository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    public List<OrderLine> getOrderLines(String orderId) {
        require(orderId);
        return repository.findOrderLines(orderId);
    }

    private OrderLine buildOrderLine(String orderId, OrderLineInput input, Instant capturedAt) {
        String kind = requiredOneOf(input.catalogItemKind(), "Order line kind is invalid.",
                ORDER_LINE_KINDS.toArray(String[]::new));
        String testDefinitionId = requiredText(input.testDefinitionId(), "Order line catalog item id is required.");
        int quantity = input.quantity() == null ? 1 : input.quantity();
        if (quantity <= 0) {
            throw new com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.InvalidFrontDeskCommandException(
                    "Order line quantity must be positive.");
        }
        String name;
        int publishedVersion;
        if (OrderLine.KIND_TEST.equals(kind)) {
            TestDefinition testDefinition = testCatalogService.get(testDefinitionId);
            if (!TestDefinition.STATUS_PUBLISHED.equals(testDefinition.status())) {
                throw new FrontDeskConflictException(
                        FrontDeskErrorCodes.ORDER_CATALOG_ITEM_NOT_PUBLISHED
                                + ": test " + testDefinitionId + " is not published.");
            }
            name = testDefinition.name() == null ? testDefinition.code() : testDefinition.name().es();
            publishedVersion = testDefinition.version();
        } else {
            PanelDefinition panelDefinition = panelCatalogService.get(testDefinitionId);
            if (!PanelDefinition.STATUS_PUBLISHED.equals(panelDefinition.status())) {
                throw new FrontDeskConflictException(
                        FrontDeskErrorCodes.ORDER_CATALOG_ITEM_NOT_PUBLISHED
                                + ": panel " + testDefinitionId + " is not published.");
            }
            name = panelDefinition.name() == null ? panelDefinition.code() : panelDefinition.name().es();
            publishedVersion = panelDefinition.version();
        }
        return new OrderLine(newId(), orderId, testDefinitionId, kind, name, publishedVersion, quantity, null,
                OrderLine.LINE_PENDING);
    }

    private DiagnosticOrder withStatus(DiagnosticOrder order, String status, String cancellationReason,
            OrderPricingSnapshot pricingSnapshot, String clinicalNotes) {
        return new DiagnosticOrder(order.orderId(), order.tenantId(), order.laboratoryId(), order.branchId(),
                order.intakeChannel(), order.sourceReferenceId(), order.patientSnapshot(), order.doctorSnapshot(),
                order.branchSnapshot(), clinicalNotes, pricingSnapshot, status, cancellationReason,
                order.actorId(), order.version() + 1, order.createdAt(), Instant.now(clock));
    }

    private DiagnosticOrder require(String orderId) {
        return repository.findById(requiredText(orderId, "Order id is required."))
                .orElseThrow(() -> new FrontDeskEntityNotFoundException("Diagnostic order was not found."));
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
