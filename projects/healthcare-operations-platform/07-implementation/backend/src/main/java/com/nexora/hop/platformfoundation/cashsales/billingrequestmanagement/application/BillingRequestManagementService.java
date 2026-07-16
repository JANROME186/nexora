package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.application;

import static com.nexora.hop.platformfoundation.cashsales.shared.CashSalesValidation.optionalText;
import static com.nexora.hop.platformfoundation.cashsales.shared.CashSalesValidation.requiredText;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.BillingRequestRepository;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalProfileSnapshot;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.InvoiceRequest;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.TaxLine;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.application.CashierOperationsService;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.Sale;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesConflictException;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesEntityNotFoundException;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesErrorCodes;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

@Service
public class BillingRequestManagementService {

    private static final String DEFAULT_TAX_CODE = "STANDARD";
    private static final BigDecimal DEFAULT_TAX_RATE = BigDecimal.ZERO;

    private final BillingRequestRepository repository;
    private final CashierOperationsService cashierOperationsService;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public BillingRequestManagementService(
            BillingRequestRepository repository,
            CashierOperationsService cashierOperationsService,
            AuditRecorder auditRecorder) {
        this(repository, cashierOperationsService, auditRecorder, Clock.systemUTC());
    }

    BillingRequestManagementService(
            BillingRequestRepository repository,
            CashierOperationsService cashierOperationsService,
            AuditRecorder auditRecorder,
            Clock clock) {
        this.repository = repository;
        this.cashierOperationsService = cashierOperationsService;
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

    public InvoiceRequest submit(String invoiceRequestId) {
        require(invoiceRequestId);
        throw new CashSalesConflictException(
                CashSalesErrorCodes.BILLING_ADAPTER_NOT_CONFIGURED
                        + ": fiscal adapter submission is implemented by MVP-MOD-005-BE-002.");
    }

    public InvoiceRequest retry(String invoiceRequestId) {
        require(invoiceRequestId);
        throw new CashSalesConflictException(
                CashSalesErrorCodes.BILLING_ADAPTER_NOT_CONFIGURED
                        + ": fiscal adapter retry is implemented by MVP-MOD-005-BE-002.");
    }

    public InvoiceRequest cancel(String invoiceRequestId) {
        require(invoiceRequestId);
        throw new CashSalesConflictException(
                CashSalesErrorCodes.BILLING_ADAPTER_NOT_CONFIGURED
                        + ": fiscal adapter cancellation is implemented by MVP-MOD-005-BE-002.");
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
}
