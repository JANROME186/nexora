package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.adapter.in.web;

import java.net.URI;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.CreateDiagnosticOrderCommand;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.DiagnosticOrderManagementService;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.application.OrderLineInput;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.DiagnosticOrder;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain.OrderLine;

/**
 * Rendered controller for {@code bcm-lab-001-diagnostic-order-management/openapi-source.yaml}
 * (base path /api/clinical-operations/diagnostic-orders).
 */
@RestController
@RequestMapping("/api/clinical-operations/diagnostic-orders")
class DiagnosticOrderController {

    private final DiagnosticOrderManagementService service;

    DiagnosticOrderController(DiagnosticOrderManagementService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<List<DiagnosticOrder>> listDiagnosticOrders(
            @RequestParam String tenantId, @RequestParam(required = false) String doctorId) {
        if (doctorId == null || doctorId.isBlank()) {
            return ResponseEntity.ok(service.list(tenantId));
        }
        // Real server-side filtering for the doctor-portal "my referred orders" view
        // (COM-MOD-009-PORTAL-002): the caller never receives another doctor's orders.
        return ResponseEntity.ok(service.listReferredByDoctor(tenantId, doctorId));
    }

    @GetMapping("/{orderId}")
    ResponseEntity<DiagnosticOrder> getDiagnosticOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(service.get(orderId));
    }

    @GetMapping("/{orderId}/lines")
    ResponseEntity<List<OrderLine>> listDiagnosticOrderLines(@PathVariable String orderId) {
        return ResponseEntity.ok(service.getOrderLines(orderId));
    }

    @PostMapping
    ResponseEntity<DiagnosticOrder> createDiagnosticOrder(@Valid @RequestBody CreateOrderRequest request) {
        DiagnosticOrder created = service.create(new CreateDiagnosticOrderCommand(
                request.tenantId(), request.laboratoryId(), request.branchId(), request.intakeChannel(),
                request.sourceReferenceId(), request.patientId(), request.doctorId(), request.actorId(),
                request.lines() == null ? List.of() : request.lines().stream().map(OrderLineRequest::toInput).toList()));
        return ResponseEntity.created(URI.create("/api/clinical-operations/diagnostic-orders/" + created.orderId()))
                .body(created);
    }

    @PostMapping("/{orderId}/price")
    ResponseEntity<DiagnosticOrder> priceDiagnosticOrder(@PathVariable String orderId,
            @RequestBody(required = false) PriceOrderRequest request) {
        String currency = request == null ? null : request.currency();
        return ResponseEntity.ok(service.price(orderId, currency));
    }

    @PostMapping("/{orderId}/accept")
    ResponseEntity<DiagnosticOrder> acceptDiagnosticOrder(@PathVariable String orderId,
            @RequestBody(required = false) AcceptOrderRequest request) {
        String clinicalNotes = request == null ? null : request.clinicalNotes();
        return ResponseEntity.ok(service.accept(orderId, clinicalNotes));
    }

    @PostMapping("/{orderId}/cancel")
    ResponseEntity<DiagnosticOrder> cancelDiagnosticOrder(@PathVariable String orderId,
            @Valid @RequestBody CancelOrderRequest request) {
        return ResponseEntity.ok(service.cancel(orderId, request.reasonCode(), request.overrideJustification()));
    }

    @PostMapping("/{orderId}/complete")
    ResponseEntity<DiagnosticOrder> completeDiagnosticOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(service.complete(orderId));
    }

    record OrderLineRequest(@NotBlank String testDefinitionId, @NotBlank String catalogItemKind, Integer quantity) {
        OrderLineInput toInput() {
            return new OrderLineInput(testDefinitionId, catalogItemKind, quantity);
        }
    }

    record CreateOrderRequest(
            @NotBlank String tenantId,
            @NotBlank String laboratoryId,
            @NotBlank String branchId,
            @NotBlank String intakeChannel,
            String sourceReferenceId,
            @NotBlank String patientId,
            String doctorId,
            String actorId,
            @NotEmpty List<OrderLineRequest> lines) {
    }

    record PriceOrderRequest(String currency) {
    }

    record AcceptOrderRequest(String clinicalNotes) {
    }

    record CancelOrderRequest(@NotBlank String reasonCode, String overrideJustification) {
    }
}
