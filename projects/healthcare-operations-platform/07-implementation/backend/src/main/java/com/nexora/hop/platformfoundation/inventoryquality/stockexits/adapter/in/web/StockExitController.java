package com.nexora.hop.platformfoundation.inventoryquality.stockexits.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.stockexits.application.StockExitService;
import com.nexora.hop.platformfoundation.inventoryquality.stockexits.domain.StockExitRecord;
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

/** Rendered controller for bcm-inv-006-stock-exits/openapi-source.yaml. */
@RestController
@RequestMapping("/api/inventory/stock-exits")
class StockExitController {

  private final StockExitService service;

  StockExitController(StockExitService service) {
    this.service = service;
  }

  @PostMapping
  ResponseEntity<StockExitResponse> applyStockExit(@Valid @RequestBody ApplyStockExitRequest request) {
    StockExitRecord exit =
        service.applyStockExit(
            new StockExitService.ApplyStockExitCommand(
                request.inventoryItemId(),
                request.stockLotId(),
                request.destinationBranchId(),
                request.quantity(),
                request.exitType(),
                request.reasonCode(),
                request.actorId()));
    return ResponseEntity.status(HttpStatus.CREATED).body(StockExitResponse.from(exit));
  }

  @GetMapping
  ResponseEntity<List<StockExitResponse>> listStockExits(
      @RequestParam String tenantId,
      @RequestParam String laboratoryId,
      @RequestParam String branchId) {
    return ResponseEntity.ok(
        service.listExits(tenantId, laboratoryId, branchId).stream()
            .map(StockExitResponse::from)
            .toList());
  }

  record ApplyStockExitRequest(
      @NotBlank String inventoryItemId,
      @NotBlank String stockLotId,
      String destinationBranchId,
      BigDecimal quantity,
      @NotBlank String exitType,
      String reasonCode,
      @NotBlank String actorId) {}

  record StockExitResponse(
      String stockExitId,
      String inventoryItemId,
      String stockLotId,
      String tenantId,
      String laboratoryId,
      String branchId,
      String destinationBranchId,
      BigDecimal quantity,
      String exitType,
      String reasonCode,
      Instant occurredAt) {

    static StockExitResponse from(StockExitRecord record) {
      return new StockExitResponse(
          record.stockExitId(),
          record.inventoryItemId(),
          record.stockLotId(),
          record.tenantId(),
          record.laboratoryId(),
          record.branchId(),
          record.destinationBranchId(),
          record.quantity(),
          record.exitType(),
          record.reasonCode(),
          record.occurredAt().atZone(ZoneOffset.UTC).toInstant());
    }
  }
}
