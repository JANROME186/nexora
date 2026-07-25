package com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.application.WasteManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.domain.WasteRecord;
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

/** Rendered controller for bcm-inv-009-waste-management/openapi-source.md. */
@RestController
@RequestMapping("/api/inventory/waste")
class WasteController {

  private final WasteManagementService service;

  WasteController(WasteManagementService service) {
    this.service = service;
  }

  @PostMapping
  ResponseEntity<WasteResponse> applyWasteDisposal(@Valid @RequestBody ApplyWasteRequest request) {
    WasteRecord record =
        service.applyWasteDisposal(
            new WasteManagementService.ApplyWasteCommand(
                request.inventoryItemId(),
                request.stockLotId(),
                request.disposedQuantity(),
                request.reasonCode(),
                request.reasonNote(),
                request.actorId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(WasteResponse.from(record));
  }

  @GetMapping
  ResponseEntity<List<WasteResponse>> listWasteRecords(
      @RequestParam String tenantId,
      @RequestParam String laboratoryId,
      @RequestParam String branchId) {
    return ResponseEntity.ok(
        service.listWaste(tenantId, laboratoryId, branchId).stream()
            .map(WasteResponse::from)
            .toList());
  }

  record ApplyWasteRequest(
      @NotBlank String inventoryItemId,
      @NotBlank String stockLotId,
      BigDecimal disposedQuantity,
      @NotBlank String reasonCode,
      String reasonNote,
      @NotBlank String actorId) {}

  record WasteResponse(
      String wasteRecordId,
      String inventoryItemId,
      String stockLotId,
      String tenantId,
      String laboratoryId,
      String branchId,
      BigDecimal disposedQuantity,
      String reasonCode,
      String reasonNote,
      Instant disposedAt) {

    static WasteResponse from(WasteRecord record) {
      return new WasteResponse(
          record.wasteRecordId(),
          record.inventoryItemId(),
          record.stockLotId(),
          record.tenantId(),
          record.laboratoryId(),
          record.branchId(),
          record.disposedQuantity(),
          record.reasonCode(),
          record.reasonNote(),
          record.disposedAt().atZone(ZoneOffset.UTC).toInstant());
    }
  }
}
