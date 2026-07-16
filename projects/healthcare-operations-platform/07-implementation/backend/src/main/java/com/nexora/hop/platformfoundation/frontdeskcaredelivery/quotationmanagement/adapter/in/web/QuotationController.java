package com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.adapter.in.web;

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

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.application.IssueQuotationCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.application.QuotationManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.application.StartQuotationCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationLine;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.quotationmanagement.domain.QuotationRequest;

/**
 * Rendered controller for {@code bcm-att-006-quotation-management/openapi-source.yaml} (base path
 * /api/care-delivery/quotations).
 */
@RestController
@RequestMapping("/api/care-delivery/quotations")
class QuotationController {

    private final QuotationManagementService service;

    QuotationController(QuotationManagementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<QuotationRequest>> listQuotations(@RequestParam String tenantId) {
        return ResponseEntity.ok(service.list(tenantId));
    }

    @GetMapping("/{quotationId}")
    ResponseEntity<QuotationRequest> getQuotation(@PathVariable String quotationId) {
        return ResponseEntity.ok(service.get(quotationId));
    }

    @GetMapping("/{quotationId}/lines")
    ResponseEntity<List<QuotationLine>> listQuotationLines(@PathVariable String quotationId) {
        return ResponseEntity.ok(service.getLines(quotationId));
    }

    @PostMapping
    ResponseEntity<QuotationRequest> startQuotation(@Valid @RequestBody StartQuotationRequest request) {
        QuotationRequest started = service.start(new StartQuotationCommand(
                request.tenantId(), request.laboratoryId(), request.branchId(), request.patientId(),
                request.prospectiveFullName(), request.prospectivePhone(), request.prospectiveEmail(),
                request.actorId(),
                request.lines() == null ? List.of() : request.lines().stream().map(QuotationLineRequest::toInput).toList()));
        return ResponseEntity.created(URI.create("/api/care-delivery/quotations/" + started.quotationId()))
                .body(started);
    }

    @PostMapping("/{quotationId}/issue")
    ResponseEntity<QuotationRequest> issueQuotation(@PathVariable String quotationId,
            @RequestBody(required = false) IssueQuotationRequest request) {
        IssueQuotationRequest resolved = request == null ? new IssueQuotationRequest(null, null, null, null, null) : request;
        return ResponseEntity.ok(service.issue(quotationId, new IssueQuotationCommand(
                resolved.currency(), resolved.discountKind(), resolved.discountValue(), resolved.validityDays(),
                Boolean.TRUE.equals(resolved.discountOverride()))));
    }

    @PostMapping("/{quotationId}/accept")
    ResponseEntity<QuotationRequest> acceptQuotation(@PathVariable String quotationId) {
        return ResponseEntity.ok(service.accept(quotationId));
    }

    @PostMapping("/{quotationId}/convert")
    ResponseEntity<QuotationRequest> convertQuotation(@PathVariable String quotationId) {
        return ResponseEntity.ok(service.convert(quotationId));
    }

    @PostMapping("/{quotationId}/cancel")
    ResponseEntity<QuotationRequest> cancelQuotation(@PathVariable String quotationId,
            @RequestBody(required = false) CancelQuotationRequest request) {
        String reasonCode = request == null ? null : request.reasonCode();
        return ResponseEntity.ok(service.cancel(quotationId, reasonCode));
    }

    @PostMapping("/{quotationId}/expire")
    ResponseEntity<QuotationRequest> expireQuotation(@PathVariable String quotationId) {
        return ResponseEntity.ok(service.expire(quotationId));
    }

    record QuotationLineRequest(@NotBlank String testDefinitionId, @NotBlank String catalogItemKind, Integer quantity) {
        StartQuotationCommand.QuotationLineInput toInput() {
            return new StartQuotationCommand.QuotationLineInput(testDefinitionId, catalogItemKind, quantity);
        }
    }

    record StartQuotationRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            String patientId,
            String prospectiveFullName,
            String prospectivePhone,
            String prospectiveEmail,
            String actorId,
            List<QuotationLineRequest> lines) {
    }

    record IssueQuotationRequest(
            String currency,
            String discountKind,
            BigDecimal discountValue,
            Integer validityDays,
            Boolean discountOverride) {
    }

    record CancelQuotationRequest(String reasonCode) {
    }
}
