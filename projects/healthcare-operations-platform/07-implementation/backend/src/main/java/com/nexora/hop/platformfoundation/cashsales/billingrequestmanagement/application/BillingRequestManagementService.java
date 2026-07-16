package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.application;

import static com.nexora.hop.platformfoundation.cashsales.shared.CashSalesValidation.optionalText;
import static com.nexora.hop.platformfoundation.cashsales.shared.CashSalesValidation.requiredText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.BillingRequestRepository;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterException;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterPort;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterRequest;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterResponse;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalProfileSnapshot;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.InvoiceRequest;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.TaxLine;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.application.CashierOperationsService;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.Sale;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesConflictException;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesEntityNotFoundException;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesErrorCodes;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

/**
 * Application service for Billing Request Management (BCM-ATT-008). Manages the full
 * InvoiceRequest lifecycle: creation, submission, retry, cancellation. Delegates fiscal
 * provider interaction to the provider-agnostic {@link FiscalAdapterPort}. Does not mutate
 * clinical, patient, order, quotation or catalog aggregates.
 *
 * <p>Idempotency: submit and retry operations generate a deterministic idempotency key from
 * invoiceRequestId + ":" + version. The adapter correlation id is preserved when present; the
 * deterministic key is used as the fallback correlation reference for retry and audit flows.</p>
 */
@Service
public class BillingRequestManagementService {

    private static final String DEFAULT_TAX_CODE = "STANDARD";
    private static final BigDecimal DEFAULT_TAX_RATE = BigDecimal.ZERO;

    private final BillingRequestRepository repository;
    private final CashierOperationsService cashierOperationsService;
    private final FiscalAdapterPort fiscalAdapterPort;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public BillingRequestManagementService(
            BillingRequestRepository repository,
            CashierOperationsService cashierOperationsService,
            FiscalAdapterPort fiscalAdapterPort,
            AuditRecorder auditRecorder) {
        this(repository, cashierOperationsService, fiscalAdapterPort, auditRecorder, Clock.systemUTC());
    }

    public BillingRequestManagementService(
            BillingRequestRepository repository,
            CashierOperationsService cashierOperationsService,
            FiscalAdapterPort fiscalAdapterPort,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.cashierOperationsService = cashierOperationsService;
        this.fiscalAdapterPort = fiscalAdapterPort;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    public InvoiceRequest create(CreateBillingRequestCommand command) {
        String saleId = requiredText(command.saleId(), "Sale id is required.");
        repository.findBySaleId(saleId).ifPresent(existing -> {
            throw new CashSalesConflictException("A billing request already exists for this sale.");
        });
        Sale sale = cashierOperationsService.getSale(saleId);
        if (!Sale.STATUS_PAID.equals(sale.status())) {
            throw new CashSalesConflictException(
                    CashSalesErrorCodes.BILLING_SALE_REQUIRED
                            + ": billing request requires a paid sale.");
        }
        FiscalProfileSnapshot fiscalProfile = new FiscalProfileSnapshot(
                requiredText(command.legalName(), "Fiscal legal name is required."),
                requiredText(command.taxIdentifier(), "Tax identifier is required."),
                requiredText(command.fiscalAddress(), "Fiscal address is required."),
                optionalText(command.fiscalRegime()), Instant.now(clock));
        String invoiceRequestId = newId();
        InvoiceRequest request = new InvoiceRequest(invoiceRequestId, sale.tenantId(), sale.laboratoryId(),
                sale.branchId(), sale.saleId(), sale.patientId(), fiscalProfile, InvoiceRequest.STATUS_REQUESTED,
                null, null, optionalText(command.actorId()), 1, Instant.now(clock), Instant.now(clock));
        InvoiceRequest saved = repository.save(request);
        TaxLine taxLine = buildTaxLine(invoiceRequestId, sale, command.taxCode(), command.taxRate());
        repository.saveTaxLine(taxLine);
        auditRecorder.recordSystemEvent(sale.tenantId(), "BillingRequestCreated", "InvoiceRequest",
                saved.invoiceRequestId(), "{\"saleId\":\"%s\"}".formatted(saleId));
        return saved;
    }

    /**
     * Submits a billing request to the fiscal adapter. Only {@code requested} status is
     * submittable. Generates a deterministic idempotency key, calls the adapter, and updates
     * the invoice request status based on the response.
     */
    public InvoiceRequest submit(String invoiceRequestId) {
        InvoiceRequest current = require(invoiceRequestId);
        if (!InvoiceRequest.STATUS_REQUESTED.equals(current.status())) {
            throw new CashSalesConflictException(
                    CashSalesErrorCodes.BILLING_REQUEST_INVALID_STATE_TRANSITION
                            + ": only a billing request in 'requested' status can be submitted; current status is '"
                            + current.status() + "'.");
        }
        List<TaxLine> taxLines = repository.findTaxLines(invoiceRequestId);
        String idempotencyKey = deriveIdempotencyKey(invoiceRequestId, current.version());
        Money saleTotal = taxLines.isEmpty()
                ? new Money("USD", BigDecimal.ZERO)
                : taxLines.get(0).baseAmount();
        FiscalAdapterRequest adapterRequest = new FiscalAdapterRequest(
                idempotencyKey, invoiceRequestId, current.fiscalProfileSnapshot(), taxLines, saleTotal);
        try {
            FiscalAdapterResponse response = fiscalAdapterPort.submit(adapterRequest);
            InvoiceRequest updated = applyAdapterResponse(current, response, idempotencyKey);
            InvoiceRequest saved = repository.save(updated);
            auditRecorder.recordSystemEvent(current.tenantId(), "BillingRequestSubmitted", "InvoiceRequest",
                    invoiceRequestId, "{\"adapterStatus\":\"%s\",\"correlationId\":\"%s\"}"
                            .formatted(response.adapterStatus(), response.correlationId()));
            return saved;
        } catch (FiscalAdapterException e) {
            return handleAdapterException(current, e, idempotencyKey, "BillingRequestSubmitFailed");
        }
    }

    /**
     * Retries a previously submitted billing request. Only {@code submitted} or {@code failed}
     * status is retryable. Terminal states ({@code issued}, {@code cancelled}) reject retry.
     */
    public InvoiceRequest retry(String invoiceRequestId) {
        InvoiceRequest current = require(invoiceRequestId);
        if (!InvoiceRequest.STATUS_SUBMITTED.equals(current.status())
                && !InvoiceRequest.STATUS_FAILED.equals(current.status())) {
            throw new CashSalesConflictException(
                    CashSalesErrorCodes.BILLING_REQUEST_INVALID_STATE_TRANSITION
                            + ": only a billing request in 'submitted' or 'failed' status can be retried; current status is '"
                            + current.status() + "'.");
        }
        List<TaxLine> taxLines = repository.findTaxLines(invoiceRequestId);
        String idempotencyKey = deriveIdempotencyKey(invoiceRequestId, 1);
        try {
            FiscalAdapterResponse response = fiscalAdapterPort.retry(
                    idempotencyKey, current.adapterCorrelationId(), taxLines);
            InvoiceRequest updated = applyAdapterResponse(current, response, idempotencyKey);
            InvoiceRequest saved = repository.save(updated);
            auditRecorder.recordSystemEvent(current.tenantId(), "BillingRequestRetried", "InvoiceRequest",
                    invoiceRequestId, "{\"adapterStatus\":\"%s\"}".formatted(response.adapterStatus()));
            return saved;
        } catch (FiscalAdapterException e) {
            return handleAdapterException(current, e, idempotencyKey, "BillingRequestRetryFailed");
        }
    }

    /**
     * Cancels a billing request. Terminal states ({@code issued}, {@code cancelled}) are
     * immutable and reject cancellation.
     */
    public InvoiceRequest cancel(String invoiceRequestId) {
        InvoiceRequest current = require(invoiceRequestId);
        if (InvoiceRequest.STATUS_ISSUED.equals(current.status())
                || InvoiceRequest.STATUS_CANCELLED.equals(current.status())) {
            throw new CashSalesConflictException(
                    CashSalesErrorCodes.BILLING_REQUEST_INVALID_STATE_TRANSITION
                            + ": cannot cancel a billing request in terminal status '"
                            + current.status() + "'.");
        }
        String idempotencyKey = deriveIdempotencyKey(invoiceRequestId, 1);
        try {
            FiscalAdapterResponse response = fiscalAdapterPort.cancel(
                    idempotencyKey, current.adapterCorrelationId());
            InvoiceRequest updated = new InvoiceRequest(
                    current.invoiceRequestId(), current.tenantId(), current.laboratoryId(),
                    current.branchId(), current.saleId(), current.patientId(),
                    current.fiscalProfileSnapshot(), InvoiceRequest.STATUS_CANCELLED,
                    response.correlationId() != null ? response.correlationId() : current.adapterCorrelationId(),
                    response.rawResponseSnapshot(),
                    current.actorId(), current.version() + 1, current.createdAt(), Instant.now(clock));
            InvoiceRequest saved = repository.save(updated);
            auditRecorder.recordSystemEvent(current.tenantId(), "BillingRequestCancelled", "InvoiceRequest",
                    invoiceRequestId, "{\"adapterStatus\":\"%s\"}".formatted(response.adapterStatus()));
            return saved;
        } catch (FiscalAdapterException e) {
            return handleAdapterException(current, e, idempotencyKey, "BillingRequestCancelFailed");
        }
    }

    public InvoiceRequest get(String invoiceRequestId) {
        return require(invoiceRequestId);
    }

    public List<InvoiceRequest> list(String tenantId) {
        return repository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    public List<TaxLine> getTaxLines(String invoiceRequestId) {
        require(invoiceRequestId);
        return repository.findTaxLines(invoiceRequestId);
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private InvoiceRequest applyAdapterResponse(InvoiceRequest current, FiscalAdapterResponse response,
            String idempotencyKey) {
        String newStatus = mapAdapterStatus(response.adapterStatus(), current.status());
        String correlationId = response.correlationId() != null ? response.correlationId()
                : (current.adapterCorrelationId() != null ? current.adapterCorrelationId() : idempotencyKey);
        return new InvoiceRequest(
                current.invoiceRequestId(), current.tenantId(), current.laboratoryId(),
                current.branchId(), current.saleId(), current.patientId(),
                current.fiscalProfileSnapshot(), newStatus, correlationId,
                response.rawResponseSnapshot(),
                current.actorId(), current.version() + 1, current.createdAt(), Instant.now(clock));
    }

    private InvoiceRequest handleAdapterException(InvoiceRequest current, FiscalAdapterException e,
            String idempotencyKey, String auditEvent) {
        String errorCode = e.isRetryable()
                ? CashSalesErrorCodes.BILLING_ADAPTER_TRANSIENT_ERROR
                : CashSalesErrorCodes.BILLING_ADAPTER_TERMINAL_ERROR;
        String correlationId = current.adapterCorrelationId() != null
                ? current.adapterCorrelationId() : idempotencyKey;
        InvoiceRequest failed = new InvoiceRequest(
                current.invoiceRequestId(), current.tenantId(), current.laboratoryId(),
                current.branchId(), current.saleId(), current.patientId(),
                current.fiscalProfileSnapshot(), InvoiceRequest.STATUS_FAILED,
                correlationId, "{\"errorCode\":\"%s\",\"message\":\"%s\",\"retryable\":%b}"
                        .formatted(e.getNormalizedErrorCode(), escapeJson(e.getMessage()), e.isRetryable()),
                current.actorId(), current.version() + 1, current.createdAt(), Instant.now(clock));
        InvoiceRequest saved = repository.save(failed);
        auditRecorder.recordSystemEvent(current.tenantId(), auditEvent, "InvoiceRequest",
                current.invoiceRequestId(), "{\"errorCode\":\"%s\",\"retryable\":%b}"
                        .formatted(errorCode, e.isRetryable()));
        return saved;
    }

    private static String mapAdapterStatus(String adapterStatus, String currentStatus) {
        return switch (adapterStatus) {
            case FiscalAdapterResponse.STATUS_SUBMITTED -> InvoiceRequest.STATUS_SUBMITTED;
            case FiscalAdapterResponse.STATUS_ISSUED -> InvoiceRequest.STATUS_ISSUED;
            case FiscalAdapterResponse.STATUS_FAILED -> InvoiceRequest.STATUS_FAILED;
            case FiscalAdapterResponse.STATUS_CANCELLED -> InvoiceRequest.STATUS_CANCELLED;
            default -> currentStatus;
        };
    }

    private static String deriveIdempotencyKey(String invoiceRequestId, int version) {
        try {
            String raw = invoiceRequestId + ":" + version;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash).substring(0, 36);
        } catch (NoSuchAlgorithmException e) {
            return invoiceRequestId.substring(0, Math.min(36, invoiceRequestId.length()));
        }
    }

    private TaxLine buildTaxLine(String invoiceRequestId, Sale sale, String taxCode, BigDecimal taxRate) {
        BigDecimal resolvedRate = taxRate == null ? DEFAULT_TAX_RATE : taxRate;
        Money baseAmount = sale.totals().totalAmount();
        BigDecimal taxAmount = baseAmount.amount().multiply(resolvedRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return new TaxLine(newId(), invoiceRequestId, baseAmount,
                optionalText(taxCode) == null ? DEFAULT_TAX_CODE : taxCode.trim(), resolvedRate,
                new Money(baseAmount.currency(), taxAmount));
    }

    private InvoiceRequest require(String invoiceRequestId) {
        return repository.findById(requiredText(invoiceRequestId, "Invoice request id is required."))
                .orElseThrow(() -> new CashSalesEntityNotFoundException("Billing request was not found."));
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }

    private static String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
