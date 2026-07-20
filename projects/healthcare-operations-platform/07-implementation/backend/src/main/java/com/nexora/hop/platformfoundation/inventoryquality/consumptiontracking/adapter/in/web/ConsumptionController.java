package com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.application.ConsumptionTrackingService;
import com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.domain.ConsumptionRecord;
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

/** Rendered controller for bcm-inv-007-consumption-tracking/openapi-source.yaml. */
@RestController
@RequestMapping("/api/inventory/consumption")
class ConsumptionController {

  private final ConsumptionTrackingService service;

  ConsumptionController(ConsumptionTrackingService service) {
    this.service = service;
  }

  @PostMapping
  ResponseEntity<ConsumptionResponse> applyConsumption(@Valid @RequestBody ApplyConsumptionRequest request) {
    ConsumptionRecord record =
        service.applyConsumption(
            new ConsumptionTrackingService.ApplyConsumptionCommand(
                request.inventoryItemId(),
                request.stockLotId(),
                request.diagnosticOrderId(),
                request.testDefinitionId(),
                request.consumedQuantity(),
                request.consumptionContext(),
                request.actorId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(ConsumptionResponse.from(record));
  }

  @GetMapping
  ResponseEntity<List<ConsumptionResponse>> listConsumptionRecords(
      @RequestParam String tenantId,
      @RequestParam String laboratoryId,
      @RequestParam String branchId) {
    return ResponseEntity.ok(
        service.listConsumption(tenantId, laboratoryId, branchId).stream()
            .map(ConsumptionResponse::from)
            .toList());
  }

  record ApplyConsumptionRequest(
      @NotBlank String inventoryItemId,
      String stockLotId,
      String diagnosticOrderId,
      String testDefinitionId,
      BigDecimal consumedQuantity,
      @NotBlank String consumptionContext,
      @NotBlank String actorId) {}

  record ConsumptionResponse(
      String consumptionRecordId,
      String inventoryItemId,
      String stockLotId,
      String tenantId,
      String laboratoryId,
      String branchId,
      String diagnosticOrderId,
      String testDefinitionId,
      BigDecimal consumedQuantity,
      String consumptionContext,
      Instant occurredAt) {

    static ConsumptionResponse from(ConsumptionRecord record) {
      return new ConsumptionResponse(
          record.consumptionRecordId(),
          record.inventoryItemId(),
          record.stockLotId(),
          record.tenantId(),
          record.laboratoryId(),
          record.branchId(),
          record.diagnosticOrderId(),
          record.testDefinitionId(),
          record.consumedQuantity(),
          record.consumptionContext(),
          record.occurredAt().atZone(ZoneOffset.UTC).toInstant());
    }
  }
}
