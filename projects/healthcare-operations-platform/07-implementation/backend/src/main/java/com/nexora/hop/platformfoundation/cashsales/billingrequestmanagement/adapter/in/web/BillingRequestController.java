package com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.adapter.in.web;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.application.BillingRequestManagementService;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.application.CreateBillingRequestCommand;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.InvoiceRequest;
import com.nexora.hop.platformfoundation.cashsales.billingrequestmanagement.domain.TaxLine;

@RestController
@RequestMapping("/api/revenue/billing-requests")
class BillingRequestController {

    private final BillingRequestManagementService service;

    BillingRequestController(BillingRequestManagementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<InvoiceRequest>> listBillingRequests(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.list(tenantId));
    }

    @GetMapping("/{invoiceRequestId}")
    ResponseEntity<InvoiceRequest> getBillingRequest(@PathVariable String invoiceRequestId) {
        return ResponseEntity.ok(service.get(invoiceRequestId));
    }

    @GetMapping("/{invoiceRequestId}/tax-lines")
    ResponseEntity<List<TaxLine>> listTaxLines(@PathVariable String invoiceRequestId) {
        return ResponseEntity.ok(service.getTaxLines(invoiceRequestId));
    }

    @PostMapping
    ResponseEntity<InvoiceRequest> createBillingRequest(@Valid @RequestBody CreateBillingRequest request) {
        InvoiceRequest created = service.create(new CreateBillingRequestCommand(
                request.saleId(), request.legalName(), request.taxIdentifier(), request.fiscalAddress(),
                request.fiscalRegime(), request.taxCode(), request.taxRate(), request.actorId()));
        return ResponseEntity.created(URI.create("/api/revenue/billing-requests/" + created.invoiceRequestId()))
                .body(created);
    }

    @PostMapping("/{invoiceRequestId}/submit")
    ResponseEntity<InvoiceRequest> submitBillingRequest(@PathVariable String invoiceRequestId) {
        return ResponseEntity.ok(service.submit(invoiceRequestId));
    }

    @PostMapping("/{invoiceRequestId}/retry")
    ResponseEntity<InvoiceRequest> retryBillingRequest(@PathVariable String invoiceRequestId) {
        return ResponseEntity.ok(service.retry(invoiceRequestId));
    }

    @PostMapping("/{invoiceRequestId}/cancel")
    ResponseEntity<InvoiceRequest> cancelBillingRequest(@PathVariable String invoiceRequestId) {
        return ResponseEntity.ok(service.cancel(invoiceRequestId));
    }

    record CreateBillingRequest(
            @NotBlank String saleId,
            @NotBlank String legalName,
            @NotBlank String taxIdentifier,
            @NotBlank String fiscalAddress,
            String fiscalRegime,
            String taxCode,
            BigDecimal taxRate,
            String actorId) {
    }
}
