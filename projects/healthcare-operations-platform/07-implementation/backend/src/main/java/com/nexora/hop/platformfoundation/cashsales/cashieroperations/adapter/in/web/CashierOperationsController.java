package com.nexora.hop.platformfoundation.cashsales.cashieroperations.adapter.in.web;

import java.net.URI;
import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.cashsales.cashieroperations.application.CashierOperationsService;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.application.CloseCashSessionCommand;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.application.CreateSaleCommand;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.application.OpenCashSessionCommand;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.application.RegisterPaymentCommand;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.CashSession;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.PaymentAllocation;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.Sale;
import com.nexora.hop.platformfoundation.cashsales.cashieroperations.domain.SaleLine;

@RestController
@RequestMapping("/api/revenue/cashier")
class CashierOperationsController {

    private final CashierOperationsService service;

    CashierOperationsController(CashierOperationsService service) {
        this.service = service;
    }

    @PostMapping("/sessions")
    ResponseEntity<CashSession> openCashSession(@Valid @RequestBody OpenSessionRequest request) {
        CashSession created = service.openSession(new OpenCashSessionCommand(
                request.tenantId(), request.laboratoryId(), request.branchId(), request.openedBy(),
                request.openingAmount(), request.currency()));
        return ResponseEntity.created(URI.create("/api/revenue/cashier/sessions/" + created.sessionId()))
                .body(created);
    }

    @PostMapping("/sessions/{sessionId}/close")
    ResponseEntity<CashSession> closeCashSession(@PathVariable String sessionId,
            @Valid @RequestBody CloseSessionRequest request) {
        return ResponseEntity.ok(service.closeSession(sessionId,
                new CloseCashSessionCommand(request.countedAmount(), request.currency(), request.varianceReason())));
    }

    @GetMapping("/sessions/{sessionId}")
    ResponseEntity<CashSession> getCashSession(@PathVariable String sessionId) {
        return ResponseEntity.ok(service.getSession(sessionId));
    }

    @GetMapping("/sessions")
    ResponseEntity<List<CashSession>> listCashSessions(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.listSessions(tenantId));
    }

    @PostMapping("/sales")
    ResponseEntity<Sale> createSale(@Valid @RequestBody CreateSaleRequest request) {
        Sale created = service.createSale(new CreateSaleCommand(
                request.tenantId(), request.sourceType(), request.sourceReferenceId(), request.actorId()));
        return ResponseEntity.created(URI.create("/api/revenue/cashier/sales/" + created.saleId())).body(created);
    }

    @GetMapping("/sales/{saleId}")
    ResponseEntity<Sale> getSale(@PathVariable String saleId) {
        return ResponseEntity.ok(service.getSale(saleId));
    }

    @GetMapping("/sales")
    ResponseEntity<List<Sale>> listSales(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.listSales(tenantId));
    }

    @GetMapping("/sales/{saleId}/lines")
    ResponseEntity<List<SaleLine>> listSaleLines(@PathVariable String saleId) {
        return ResponseEntity.ok(service.listSaleLines(saleId));
    }

    @GetMapping("/sales/{saleId}/payments")
    ResponseEntity<List<PaymentAllocation>> listPayments(@PathVariable String saleId) {
        return ResponseEntity.ok(service.listPayments(saleId));
    }

    @PostMapping("/sales/{saleId}/payments")
    ResponseEntity<PaymentAllocation> registerPayment(@PathVariable String saleId,
            @Valid @RequestBody RegisterPaymentRequest request) {
        PaymentAllocation created = service.registerPayment(saleId, new RegisterPaymentCommand(
                request.amount(), request.currency(), request.method(), request.sessionId(), request.reference(),
                request.registeredBy()));
        return ResponseEntity.created(URI.create("/api/revenue/cashier/sales/" + saleId
                + "/payments/" + created.paymentId())).body(created);
    }

    @PostMapping("/sales/{saleId}/cancel")
    ResponseEntity<Sale> cancelSale(@PathVariable String saleId, @Valid @RequestBody CancelSaleRequest request) {
        return ResponseEntity.ok(service.cancelSale(saleId, request.reasonCode()));
    }

    record OpenSessionRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            @NotBlank String openedBy,
            @NotNull BigDecimal openingAmount,
            String currency) {
    }

    record CloseSessionRequest(@NotNull BigDecimal countedAmount, String currency, String varianceReason) {
    }

    record CreateSaleRequest(@NotBlank String tenantId, @NotBlank String sourceType,
            @NotBlank String sourceReferenceId, String actorId) {
    }

    record RegisterPaymentRequest(@NotNull BigDecimal amount, String currency, @NotBlank String method,
            String sessionId, String reference, @NotBlank String registeredBy) {
    }

    record CancelSaleRequest(@NotBlank String reasonCode) {
    }
}
