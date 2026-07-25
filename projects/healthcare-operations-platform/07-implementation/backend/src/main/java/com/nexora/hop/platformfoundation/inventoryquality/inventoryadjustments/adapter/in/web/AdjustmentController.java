package com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.application.InventoryAdjustmentService;
import com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.domain.AdjustmentRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Rendered controller for bcm-inv-008-inventory-adjustments/openapi-source.md. */
@RestController
@RequestMapping("/api/inventory/adjustments")
class AdjustmentController {

  private final InventoryAdjustmentService service;

  AdjustmentController(InventoryAdjustmentService service) {
    this.service = service;
  }

  @PostMapping
  ResponseEntity<AdjustmentResponse> applyAdjustment(
      @Valid @RequestBody ApplyAdjustmentRequest request) {
    AdjustmentRecord record =
        service.applyAdjustment(
            new InventoryAdjustmentService.ApplyAdjustmentCommand(
                request.inventoryItemId(),
                request.stockLotId(),
                request.deltaQuantity(),
                request.reasonCode(),
                request.reasonNote(),
                request.requestedBy(),
                request.approverId(),
                request.actorId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(AdjustmentResponse.from(record));
  }

  @GetMapping
  ResponseEntity<List<AdjustmentResponse>> listAdjustments(
      @RequestParam String tenantId,
      @RequestParam String laboratoryId,
      @RequestParam String branchId) {
    return ResponseEntity.ok(
        service.listAdjustments(tenantId, laboratoryId, branchId).stream()
            .map(AdjustmentResponse::from)
            .toList());
  }

  record ApplyAdjustmentRequest(
      @NotBlank String inventoryItemId,
      String stockLotId,
      BigDecimal deltaQuantity,
      @NotBlank String reasonCode,
      String reasonNote,
      @NotBlank String requestedBy,
      @NotBlank String approverId,
      @NotBlank String actorId) {}

  record AdjustmentResponse(
      String adjustmentId,
      String inventoryItemId,
      String stockLotId,
      String tenantId,
      String laboratoryId,
      String branchId,
      BigDecimal deltaQuantity,
      String reasonCode,
      String reasonNote,
      String approverId,
      String requestedBy,
      Instant occurredAt) {

    static AdjustmentResponse from(AdjustmentRecord record) {
      return new AdjustmentResponse(
          record.adjustmentId(),
          record.inventoryItemId(),
          record.stockLotId(),
          record.tenantId(),
          record.laboratoryId(),
          record.branchId(),
          record.deltaQuantity(),
          record.reasonCode(),
          record.reasonNote(),
          record.approverId(),
          record.requestedBy(),
          record.occurredAt().atZone(ZoneOffset.UTC).toInstant());
    }
  }
}
