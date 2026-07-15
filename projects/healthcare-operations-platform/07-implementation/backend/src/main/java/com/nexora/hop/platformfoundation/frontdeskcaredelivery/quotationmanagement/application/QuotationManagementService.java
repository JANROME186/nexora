package com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.application;

import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.optionalText;
import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.requiredOneOf;
import static com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskValidation.requiredText;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
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
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.CreateDiagnosticOrderCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.DiagnosticOrderManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.OrderLineInput;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequest;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequestRepository;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskConflictException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.FrontDeskEntityNotFoundException;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared.InvalidFrontDeskCommandException;

/**
 * Compiles BCM-ATT-006 (Quotation Management) generatable outputs and a functional baseline for
 * RN-001..RN-008. Owns a standalone {@link QuotationRequest} aggregate; conversion delegates to
 * {@link DiagnosticOrderManagementService} (BCM-LAB-001) rather than persisting order state
 * itself (RN-005), and never depends on the unbuilt MVP-MOD-005 Sale aggregate (TD-DEF-001).
 * <p>
 * <b>BE-002 hooks:</b> discount policy here is a fixed baseline
 * ({@value #STANDARD_MAX_DISCOUNT_PERCENTAGE}% without override,
 * {@value #OVERRIDE_MAX_DISCOUNT_PERCENTAGE}% with {@code quotation.discount.override}); the
 * tenant-configurable, role-aware policy described in RN-003 is deferred. Quotation validity
 * defaults to {@value #DEFAULT_VALIDITY_DAYS} days when not supplied.
 */
@Service
public class QuotationManagementService {

    private static final List<String> ITEM_KINDS = List.of(QuotationLine.KIND_TEST, QuotationLine.KIND_PANEL);
    private static final List<String> DISCOUNT_KINDS = List.of(
            QuotationRequest.DISCOUNT_PERCENTAGE, QuotationRequest.DISCOUNT_FIXED_AMOUNT,
            QuotationRequest.DISCOUNT_PROMOTION_CODE);

    static final int STANDARD_MAX_DISCOUNT_PERCENTAGE = 20;
    static final int OVERRIDE_MAX_DISCOUNT_PERCENTAGE = 50;
    static final int DEFAULT_VALIDITY_DAYS = 15;
    private static final String DEFAULT_CURRENCY = "USD";

    private final QuotationRequestRepository repository;
    private final TestCatalogService testCatalogService;
    private final PanelCatalogService panelCatalogService;
    private final PriceListManagementService priceListManagementService;
    private final DiagnosticOrderManagementService diagnosticOrderManagementService;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public QuotationManagementService(
            QuotationRequestRepository repository,
            TestCatalogService testCatalogService,
            PanelCatalogService panelCatalogService,
            PriceListManagementService priceListManagementService,
            DiagnosticOrderManagementService diagnosticOrderManagementService,
            AuditRecorder auditRecorder) {
        this(repository, testCatalogService, panelCatalogService, priceListManagementService,
                diagnosticOrderManagementService, auditRecorder, Clock.systemUTC());
    }

    QuotationManagementService(
            QuotationRequestRepository repository,
            TestCatalogService testCatalogService,
            PanelCatalogService panelCatalogService,
            PriceListManagementService priceListManagementService,
            DiagnosticOrderManagementService diagnosticOrderManagementService,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.testCatalogService = testCatalogService;
        this.panelCatalogService = panelCatalogService;
        this.priceListManagementService = priceListManagementService;
        this.diagnosticOrderManagementService = diagnosticOrderManagementService;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    /** RN-001: quotation lines must reference only published catalog items. */
    public QuotationRequest start(StartQuotationCommand command) {
        String tenantId = requiredText(command.tenantId(), "Tenant id is required.");
        String laboratoryId = requiredText(command.laboratoryId(), "Laboratory id is required.");
        String branchId = requiredText(command.branchId(), "Branch id is required.");
        List<StartQuotationCommand.QuotationLineInput> requestedLines =
                command.lines() == null ? List.of() : command.lines();

        String quotationId = newId();
        List<QuotationLine> lines = new ArrayList<>();
        for (StartQuotationCommand.QuotationLineInput input : requestedLines) {
            lines.add(buildLine(quotationId, input));
        }

        Instant now = Instant.now(clock);
        QuotationRequest quotation = new QuotationRequest(
                quotationId, tenantId, laboratoryId, branchId, optionalText(command.patientId()),
                optionalText(command.prospectiveFullName()), optionalText(command.prospectivePhone()),
                optionalText(command.prospectiveEmail()), null, 0, null, null, null, null,
                QuotationRequest.STATUS_DRAFT, null, null, optionalText(command.actorId()), 1, now, now);
        QuotationRequest saved = repository.save(quotation);
        for (QuotationLine line : lines) {
            repository.saveLine(line);
        }
        auditRecorder.recordSystemEvent(tenantId, "QuotationDrafted", "QuotationRequest", quotationId,
                "{\"branchId\":\"%s\"}".formatted(jsonText(branchId)));
        return saved;
    }

    /** RN-002, RN-003, RN-008: price-list resolution, discount policy and pricing snapshot capture. */
    public QuotationRequest issue(String quotationId, IssueQuotationCommand command) {
        QuotationRequest quotation = require(quotationId);
        List<QuotationLine> lines = repository.findLines(quotationId);
        if (lines.isEmpty()) {
            throw new FrontDeskConflictException("A quotation must contain at least one line before it can be issued.");
        }
        String currency = optionalText(command.currency()) == null ? DEFAULT_CURRENCY : command.currency();

        QuotationLine firstLine = lines.get(0);
        PriceList priceList = priceListManagementService.getEffectivePriceSnapshot(
                firstLine.catalogItemKind(), firstLine.testDefinitionId(), currency, null, null);
        List<PriceEntry> entries = priceListManagementService.getEntries(priceList.priceListId());

        BigDecimal subtotal = BigDecimal.ZERO;
        for (QuotationLine line : lines) {
            PriceEntry entry = entries.stream()
                    .filter(candidate -> candidate.itemType().equals(line.catalogItemKind())
                            && candidate.itemRefId().equals(line.testDefinitionId()))
                    .findFirst()
                    .orElseThrow(() -> new FrontDeskConflictException(
                            "QUOTATION_PRICING_SNAPSHOT_REQUIRED: no price entry resolves for catalog item "
                                    + line.testDefinitionId() + " in the resolved price list."));
            QuotationLine priced = new QuotationLine(line.lineId(), line.quotationId(), line.testDefinitionId(),
                    line.catalogItemKind(), line.publishedVersion(), line.quantity(), entry.price());
            repository.saveLine(priced);
            subtotal = subtotal.add(entry.price().amount().multiply(BigDecimal.valueOf(line.quantity())));
        }

        BigDecimal total = subtotal;
        String discountKind = null;
        BigDecimal discountValue = null;
        if (command.discountKind() != null) {
            discountKind = requiredOneOf(command.discountKind(), "Discount kind is invalid.",
                    DISCOUNT_KINDS.toArray(String[]::new));
            discountValue = command.discountValue() == null ? BigDecimal.ZERO : command.discountValue();
            int maxPercentage = command.discountOverride() ? OVERRIDE_MAX_DISCOUNT_PERCENTAGE : STANDARD_MAX_DISCOUNT_PERCENTAGE;
            if (QuotationRequest.DISCOUNT_PERCENTAGE.equals(discountKind)) {
                if (discountValue.compareTo(BigDecimal.valueOf(maxPercentage)) > 0) {
                    throw new FrontDeskConflictException(
                            "QUOTATION_DISCOUNT_POLICY_EXCEEDED: discount exceeds the " + maxPercentage
                                    + "% policy limit.");
                }
                total = subtotal.subtract(subtotal.multiply(discountValue).divide(BigDecimal.valueOf(100)));
            } else if (QuotationRequest.DISCOUNT_FIXED_AMOUNT.equals(discountKind)) {
                BigDecimal maxAmount = subtotal.multiply(BigDecimal.valueOf(maxPercentage)).divide(BigDecimal.valueOf(100));
                if (discountValue.compareTo(maxAmount) > 0) {
                    throw new FrontDeskConflictException(
                            "QUOTATION_DISCOUNT_POLICY_EXCEEDED: discount exceeds the " + maxPercentage
                                    + "% policy limit.");
                }
                total = subtotal.subtract(discountValue);
            }
        }

        int validityDays = command.validityDays() == null ? DEFAULT_VALIDITY_DAYS : command.validityDays();
        LocalDate validUntil = LocalDate.now(clock).plusDays(validityDays);

        QuotationRequest issued = new QuotationRequest(
                quotation.quotationId(), quotation.tenantId(), quotation.laboratoryId(), quotation.branchId(),
                quotation.patientId(), quotation.prospectiveFullName(), quotation.prospectivePhone(),
                quotation.prospectiveEmail(), priceList.priceListId(), priceList.version(),
                new Money(currency, total), discountKind, discountValue, validUntil, QuotationRequest.STATUS_ISSUED,
                quotation.convertedOrderId(), quotation.cancellationReason(), quotation.actorId(),
                quotation.version() + 1, quotation.createdAt(), Instant.now(clock));
        QuotationRequest saved = repository.save(issued);
        auditRecorder.recordSystemEvent(saved.tenantId(), "QuotationIssued", "QuotationRequest", quotationId,
                "{\"priceListId\":\"%s\",\"totalAmount\":\"%s\",\"validUntil\":\"%s\"}"
                        .formatted(jsonText(priceList.priceListId()), total.toPlainString(), validUntil));
        return saved;
    }

    /** RN-004: cannot accept an expired quotation. */
    public QuotationRequest accept(String quotationId) {
        QuotationRequest quotation = require(quotationId);
        if (!QuotationRequest.STATUS_ISSUED.equals(quotation.status())) {
            throw new FrontDeskConflictException("Only an issued quotation can be accepted.");
        }
        if (quotation.validUntil() != null && quotation.validUntil().isBefore(LocalDate.now(clock))) {
            throw new FrontDeskConflictException("QUOTATION_EXPIRED: the quotation validity window has elapsed.");
        }
        QuotationRequest accepted = withStatus(quotation, QuotationRequest.STATUS_ACCEPTED, quotation.cancellationReason());
        QuotationRequest saved = repository.save(accepted);
        auditRecorder.recordSystemEvent(saved.tenantId(), "QuotationAccepted", "QuotationRequest", quotationId,
                "{\"totalAmount\":\"%s\"}".formatted(saved.totalAmount() == null ? "" : saved.totalAmount().amount().toPlainString()));
        return saved;
    }

    /** RN-005, RN-007: converts an accepted quotation into a draft diagnostic order. */
    public QuotationRequest convert(String quotationId) {
        QuotationRequest quotation = require(quotationId);
        if (!QuotationRequest.STATUS_ACCEPTED.equals(quotation.status())) {
            throw new FrontDeskConflictException("Only an accepted quotation can be converted.");
        }
        if (quotation.patientId() == null) {
            throw new InvalidFrontDeskCommandException(
                    "A quotation can only convert into a diagnostic order once linked to a registered patient.");
        }
        List<OrderLineInput> lines = repository.findLines(quotationId).stream()
                .map(line -> new OrderLineInput(line.testDefinitionId(), line.catalogItemKind(), line.quantity()))
                .toList();
        DiagnosticOrder order = diagnosticOrderManagementService.create(new CreateDiagnosticOrderCommand(
                quotation.tenantId(), quotation.laboratoryId(), quotation.branchId(),
                DiagnosticOrder.CHANNEL_QUOTATION_CONVERSION, quotationId, quotation.patientId(), null,
                quotation.actorId(), lines));

        QuotationRequest converted = new QuotationRequest(
                quotation.quotationId(), quotation.tenantId(), quotation.laboratoryId(), quotation.branchId(),
                quotation.patientId(), quotation.prospectiveFullName(), quotation.prospectivePhone(),
                quotation.prospectiveEmail(), quotation.priceListId(), quotation.priceListVersion(),
                quotation.totalAmount(), quotation.discountKind(), quotation.discountValue(), quotation.validUntil(),
                QuotationRequest.STATUS_CONVERTED, order.orderId(), quotation.cancellationReason(),
                quotation.actorId(), quotation.version() + 1, quotation.createdAt(), Instant.now(clock));
        QuotationRequest saved = repository.save(converted);
        auditRecorder.recordSystemEvent(saved.tenantId(), "QuotationConverted", "QuotationRequest", quotationId,
                "{\"convertedOrderId\":\"%s\"}".formatted(jsonText(order.orderId())));
        return saved;
    }

    public QuotationRequest cancel(String quotationId, String reasonCode) {
        QuotationRequest quotation = require(quotationId);
        requireOpen(quotation);
        QuotationRequest cancelled = withStatus(quotation, QuotationRequest.STATUS_CANCELLED,
                optionalText(reasonCode) == null ? "unspecified" : reasonCode);
        QuotationRequest saved = repository.save(cancelled);
        auditRecorder.recordSystemEvent(saved.tenantId(), "QuotationClosed", "QuotationRequest", quotationId,
                "{\"reasonCode\":\"%s\"}".formatted(jsonText(saved.cancellationReason())));
        return saved;
    }

    public QuotationRequest expire(String quotationId) {
        QuotationRequest quotation = require(quotationId);
        if (!QuotationRequest.STATUS_ISSUED.equals(quotation.status())) {
            throw new FrontDeskConflictException("Only an issued quotation can expire.");
        }
        QuotationRequest expired = withStatus(quotation, QuotationRequest.STATUS_EXPIRED, quotation.cancellationReason());
        QuotationRequest saved = repository.save(expired);
        auditRecorder.recordSystemEvent(saved.tenantId(), "QuotationClosed", "QuotationRequest", quotationId,
                "{\"reasonCode\":\"expired\"}");
        return saved;
    }

    public QuotationRequest get(String quotationId) {
        return require(quotationId);
    }

    public List<QuotationRequest> list(String tenantId) {
        return repository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    public List<QuotationLine> getLines(String quotationId) {
        require(quotationId);
        return repository.findLines(quotationId);
    }

    private QuotationLine buildLine(String quotationId, StartQuotationCommand.QuotationLineInput input) {
        String kind = requiredOneOf(input.catalogItemKind(), "Quotation line kind is invalid.",
                ITEM_KINDS.toArray(String[]::new));
        String testDefinitionId = requiredText(input.testDefinitionId(), "Quotation line catalog item id is required.");
        int quantity = input.quantity() == null ? 1 : input.quantity();
        int publishedVersion;
        if (QuotationLine.KIND_TEST.equals(kind)) {
            TestDefinition testDefinition = testCatalogService.get(testDefinitionId);
            if (!TestDefinition.STATUS_PUBLISHED.equals(testDefinition.status())) {
                throw new FrontDeskConflictException(
                        "QUOTATION_CATALOG_ITEM_NOT_PUBLISHED: test " + testDefinitionId + " is not published.");
            }
            publishedVersion = testDefinition.version();
        } else {
            PanelDefinition panelDefinition = panelCatalogService.get(testDefinitionId);
            if (!PanelDefinition.STATUS_PUBLISHED.equals(panelDefinition.status())) {
                throw new FrontDeskConflictException(
                        "QUOTATION_CATALOG_ITEM_NOT_PUBLISHED: panel " + testDefinitionId + " is not published.");
            }
            publishedVersion = panelDefinition.version();
        }
        return new QuotationLine(newId(), quotationId, testDefinitionId, kind, publishedVersion, quantity, null);
    }

    private void requireOpen(QuotationRequest quotation) {
        if (QuotationRequest.STATUS_CONVERTED.equals(quotation.status())
                || QuotationRequest.STATUS_CANCELLED.equals(quotation.status())) {
            throw new FrontDeskConflictException(
                    "QUOTATION_TERMINAL_STATE_IMMUTABLE: a converted or cancelled quotation cannot be modified.");
        }
    }

    private QuotationRequest withStatus(QuotationRequest quotation, String status, String cancellationReason) {
        return new QuotationRequest(
                quotation.quotationId(), quotation.tenantId(), quotation.laboratoryId(), quotation.branchId(),
                quotation.patientId(), quotation.prospectiveFullName(), quotation.prospectivePhone(),
                quotation.prospectiveEmail(), quotation.priceListId(), quotation.priceListVersion(),
                quotation.totalAmount(), quotation.discountKind(), quotation.discountValue(), quotation.validUntil(),
                status, quotation.convertedOrderId(), cancellationReason, quotation.actorId(),
                quotation.version() + 1, quotation.createdAt(), Instant.now(clock));
    }

    private QuotationRequest require(String quotationId) {
        return repository.findById(requiredText(quotationId, "Quotation id is required."))
                .orElseThrow(() -> new FrontDeskEntityNotFoundException("Quotation was not found."));
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
