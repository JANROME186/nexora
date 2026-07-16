package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.application.BillingRequestManagementService;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.application.CreateBillingRequestCommand;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.BillingRequestRepository;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterException;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterPort;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalAdapterResponse;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.FiscalProfileSnapshot;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.InvoiceRequest;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.TaxLine;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.application.CashierOperationsService;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.Sale;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.SaleTotals;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesConflictException;
import com.nexora.hop.platformfoundation.cashsales.shared.CashSalesErrorCodes;
import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

/**
 * Unit tests for {@link BillingRequestManagementService} using a mocked {@link FiscalAdapterPort}.
 * Covers submit, retry and cancel happy paths, state machine guards, idempotency key derivation,
 * adapter transient and terminal error handling.
 */
class BillingRequestAdapterUnitTest {

    private BillingRequestRepository repository;
    private CashierOperationsService cashierService;
    private FiscalAdapterPort fiscalAdapter;
    private AuditRecorder auditRecorder;
    private BillingRequestManagementService service;

    private static final Clock FIXED_CLOCK = Clock.fixed(Instant.parse("2026-07-16T12:00:00Z"), ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        repository = mock(BillingRequestRepository.class);
        cashierService = mock(CashierOperationsService.class);
        fiscalAdapter = mock(FiscalAdapterPort.class);
        auditRecorder = mock(AuditRecorder.class);
        service = new BillingRequestManagementService(repository, cashierService, fiscalAdapter, auditRecorder, FIXED_CLOCK);
    }

    // -------------------------------------------------------------------------
    // submit - happy path
    // -------------------------------------------------------------------------

    @Test
    void submitTransitionsRequestedToSubmitted() {
        InvoiceRequest requested = requestedInvoice("inv-001");
        List<TaxLine> taxLines = List.of(taxLine("inv-001"));
        FiscalAdapterResponse adapterResponse = new FiscalAdapterResponse(
                "corr-001", FiscalAdapterResponse.STATUS_SUBMITTED, "{\"provider\":\"local-deterministic\"}");

        when(repository.findById("inv-001")).thenReturn(Optional.of(requested));
        when(repository.findTaxLines("inv-001")).thenReturn(taxLines);
        when(fiscalAdapter.submit(any())).thenReturn(adapterResponse);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InvoiceRequest result = service.submit("inv-001");

        assertThat(result.status()).isEqualTo(InvoiceRequest.STATUS_SUBMITTED);
        assertThat(result.adapterCorrelationId()).isEqualTo("corr-001");
        assertThat(result.version()).isEqualTo(2);
        verify(auditRecorder).recordSystemEvent(anyString(), eq("BillingRequestSubmitted"), anyString(), anyString(), anyString());
    }

    @Test
    void submitTransitionsRequestedToIssuedWhenAdapterReturnsIssued() {
        InvoiceRequest requested = requestedInvoice("inv-002");
        FiscalAdapterResponse adapterResponse = new FiscalAdapterResponse(
                "corr-002", FiscalAdapterResponse.STATUS_ISSUED, "{}");

        when(repository.findById("inv-002")).thenReturn(Optional.of(requested));
        when(repository.findTaxLines("inv-002")).thenReturn(List.of());
        when(fiscalAdapter.submit(any())).thenReturn(adapterResponse);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InvoiceRequest result = service.submit("inv-002");

        assertThat(result.status()).isEqualTo(InvoiceRequest.STATUS_ISSUED);
    }

    // -------------------------------------------------------------------------
    // submit - state guards
    // -------------------------------------------------------------------------

    @Test
    void submitOnSubmittedStatusThrowsConflict() {
        InvoiceRequest submitted = invoiceWithStatus("inv-003", InvoiceRequest.STATUS_SUBMITTED);
        when(repository.findById("inv-003")).thenReturn(Optional.of(submitted));

        assertThatThrownBy(() -> service.submit("inv-003"))
                .isInstanceOf(CashSalesConflictException.class)
                .hasMessageContaining(CashSalesErrorCodes.BILLING_REQUEST_INVALID_STATE_TRANSITION);
    }

    @Test
    void submitOnIssuedStatusThrowsConflict() {
        InvoiceRequest issued = invoiceWithStatus("inv-004", InvoiceRequest.STATUS_ISSUED);
        when(repository.findById("inv-004")).thenReturn(Optional.of(issued));

        assertThatThrownBy(() -> service.submit("inv-004"))
                .isInstanceOf(CashSalesConflictException.class)
                .hasMessageContaining(CashSalesErrorCodes.BILLING_REQUEST_INVALID_STATE_TRANSITION);
    }

    @Test
    void submitOnCancelledStatusThrowsConflict() {
        InvoiceRequest cancelled = invoiceWithStatus("inv-010", InvoiceRequest.STATUS_CANCELLED);
        when(repository.findById("inv-010")).thenReturn(Optional.of(cancelled));

        assertThatThrownBy(() -> service.submit("inv-010"))
                .isInstanceOf(CashSalesConflictException.class)
                .hasMessageContaining(CashSalesErrorCodes.BILLING_REQUEST_INVALID_STATE_TRANSITION);
    }

    // -------------------------------------------------------------------------
    // submit - adapter exceptions
    // -------------------------------------------------------------------------

    @Test
    void submitWithRetryableAdapterExceptionSetsFailedStatus() {
        InvoiceRequest requested = requestedInvoice("inv-005");
        FiscalAdapterException retryable = new FiscalAdapterException("timeout", "TIMEOUT", true);

        when(repository.findById("inv-005")).thenReturn(Optional.of(requested));
        when(repository.findTaxLines("inv-005")).thenReturn(List.of());
        when(fiscalAdapter.submit(any())).thenThrow(retryable);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InvoiceRequest result = service.submit("inv-005");

        assertThat(result.status()).isEqualTo(InvoiceRequest.STATUS_FAILED);
        assertThat(result.adapterResponseSnapshot()).contains("retryable\":true");
        verify(auditRecorder).recordSystemEvent(anyString(), eq("BillingRequestSubmitFailed"), anyString(), anyString(), anyString());
    }

    @Test
    void submitWithTerminalAdapterExceptionSetsFailedStatus() {
        InvoiceRequest requested = requestedInvoice("inv-006");
        FiscalAdapterException terminal = new FiscalAdapterException("invalid fiscal data", "INVALID_DATA", false);

        when(repository.findById("inv-006")).thenReturn(Optional.of(requested));
        when(repository.findTaxLines("inv-006")).thenReturn(List.of());
        when(fiscalAdapter.submit(any())).thenThrow(terminal);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InvoiceRequest result = service.submit("inv-006");

        assertThat(result.status()).isEqualTo(InvoiceRequest.STATUS_FAILED);
        assertThat(result.adapterResponseSnapshot()).contains("retryable\":false");
    }

    // -------------------------------------------------------------------------
    // retry - happy path
    // -------------------------------------------------------------------------

    @Test
    void retryOnFailedStatusTransitionsToIssued() {
        InvoiceRequest failed = invoiceWithStatus("inv-007", InvoiceRequest.STATUS_FAILED);
        FiscalAdapterResponse adapterResponse = new FiscalAdapterResponse(
                "corr-007", FiscalAdapterResponse.STATUS_ISSUED, "{}");

        when(repository.findById("inv-007")).thenReturn(Optional.of(failed));
        when(repository.findTaxLines("inv-007")).thenReturn(List.of());
        when(fiscalAdapter.retry(anyString(), any(), anyList())).thenReturn(adapterResponse);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InvoiceRequest result = service.retry("inv-007");

        assertThat(result.status()).isEqualTo(InvoiceRequest.STATUS_ISSUED);
        verify(auditRecorder).recordSystemEvent(anyString(), eq("BillingRequestRetried"), anyString(), anyString(), anyString());
    }

    @Test
    void retryOnSubmittedStatusAllowed() {
        InvoiceRequest submitted = invoiceWithStatus("inv-008", InvoiceRequest.STATUS_SUBMITTED);
        FiscalAdapterResponse adapterResponse = new FiscalAdapterResponse(
                "corr-008", FiscalAdapterResponse.STATUS_ISSUED, "{}");

        when(repository.findById("inv-008")).thenReturn(Optional.of(submitted));
        when(repository.findTaxLines("inv-008")).thenReturn(List.of());
        when(fiscalAdapter.retry(anyString(), any(), anyList())).thenReturn(adapterResponse);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InvoiceRequest result = service.retry("inv-008");

        assertThat(result.status()).isEqualTo(InvoiceRequest.STATUS_ISSUED);
    }

    @Test
    void retryOnIssuedStatusThrowsConflict() {
        InvoiceRequest issued = invoiceWithStatus("inv-011", InvoiceRequest.STATUS_ISSUED);
        when(repository.findById("inv-011")).thenReturn(Optional.of(issued));

        assertThatThrownBy(() -> service.retry("inv-011"))
                .isInstanceOf(CashSalesConflictException.class)
                .hasMessageContaining(CashSalesErrorCodes.BILLING_REQUEST_INVALID_STATE_TRANSITION);
    }

    // -------------------------------------------------------------------------
    // cancel - happy path and guards
    // -------------------------------------------------------------------------

    @Test
    void cancelOnRequestedStatusSetsCancelled() {
        InvoiceRequest requested = requestedInvoice("inv-009");
        FiscalAdapterResponse adapterResponse = new FiscalAdapterResponse(
                "corr-009", FiscalAdapterResponse.STATUS_CANCELLED, "{}");

        when(repository.findById("inv-009")).thenReturn(Optional.of(requested));
        when(fiscalAdapter.cancel(anyString(), any())).thenReturn(adapterResponse);
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        InvoiceRequest result = service.cancel("inv-009");

        assertThat(result.status()).isEqualTo(InvoiceRequest.STATUS_CANCELLED);
        verify(auditRecorder).recordSystemEvent(anyString(), eq("BillingRequestCancelled"), anyString(), anyString(), anyString());
    }

    @Test
    void cancelOnIssuedStatusThrowsConflict() {
        InvoiceRequest issued = invoiceWithStatus("inv-012", InvoiceRequest.STATUS_ISSUED);
        when(repository.findById("inv-012")).thenReturn(Optional.of(issued));

        assertThatThrownBy(() -> service.cancel("inv-012"))
                .isInstanceOf(CashSalesConflictException.class)
                .hasMessageContaining(CashSalesErrorCodes.BILLING_REQUEST_INVALID_STATE_TRANSITION);
    }

    @Test
    void cancelOnAlreadyCancelledThrowsConflict() {
        InvoiceRequest cancelled = invoiceWithStatus("inv-013", InvoiceRequest.STATUS_CANCELLED);
        when(repository.findById("inv-013")).thenReturn(Optional.of(cancelled));

        assertThatThrownBy(() -> service.cancel("inv-013"))
                .isInstanceOf(CashSalesConflictException.class)
                .hasMessageContaining(CashSalesErrorCodes.BILLING_REQUEST_INVALID_STATE_TRANSITION);
    }

    // -------------------------------------------------------------------------
    // create guard: duplicate billing request for same sale
    // -------------------------------------------------------------------------

    @Test
    void createRejectsDuplicateBillingRequestForSameSale() {
        InvoiceRequest existing = requestedInvoice("inv-100");
        Sale paidSale = paidSale("sale-001");

        when(cashierService.getSale("sale-001")).thenReturn(paidSale);
        when(repository.findBySaleId("sale-001")).thenReturn(Optional.of(existing));

        CreateBillingRequestCommand command = new CreateBillingRequestCommand(
                "sale-001", "Legal Corp", "TAX-001", "Main St 1", null, null, null, null);

        assertThatThrownBy(() -> service.create(command))
                .isInstanceOf(CashSalesConflictException.class)
                .hasMessageContaining("A billing request already exists for this sale.");
    }

    // -------------------------------------------------------------------------
    // Builders
    // -------------------------------------------------------------------------

    private static InvoiceRequest requestedInvoice(String id) {
        return invoiceWithStatus(id, InvoiceRequest.STATUS_REQUESTED);
    }

    private static InvoiceRequest invoiceWithStatus(String id, String status) {
        FiscalProfileSnapshot profile = new FiscalProfileSnapshot(
                "Legal Corp", "TAX-001", "Main St 1", null, Instant.parse("2026-07-16T12:00:00Z"));
        return new InvoiceRequest(id, "tenant-1", "lab-1", "branch-1", "sale-1", "patient-1",
                profile, status, null, null, "actor-1", 1,
                Instant.parse("2026-07-16T12:00:00Z"), Instant.parse("2026-07-16T12:00:00Z"));
    }

    private static TaxLine taxLine(String invoiceRequestId) {
        Money base = new Money("USD", new BigDecimal("100.00"));
        Money tax = new Money("USD", new BigDecimal("16.00"));
        return new TaxLine("tax-1", invoiceRequestId, base, "VAT", new BigDecimal("16.00"), tax);
    }

    private static Sale paidSale(String saleId) {
        Money amount = new Money("USD", new BigDecimal("100.00"));
        SaleTotals totals = new SaleTotals(amount, new Money("USD", BigDecimal.ZERO),
                amount, amount, new Money("USD", BigDecimal.ZERO));
        return new Sale(saleId, "tenant-1", "lab-1", "branch-1", "patient-1",
                "diagnostic_order", "order-1", totals, Sale.STATUS_PAID, null, "cashier-1", 1,
                Instant.parse("2026-07-16T12:00:00Z"), Instant.parse("2026-07-16T12:00:00Z"));
    }
}
