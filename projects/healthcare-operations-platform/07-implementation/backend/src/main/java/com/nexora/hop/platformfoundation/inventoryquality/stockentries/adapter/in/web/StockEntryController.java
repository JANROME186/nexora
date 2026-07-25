package com.nexora.hop.platformfoundation.inventoryquality.stockentries.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.stockentries.application.StockEntryService;
import com.nexora.hop.platformfoundation.inventoryquality.stockentries.domain.StockEntryRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Rendered controller for bcm-inv-005-stock-entries/openapi-source.md. */
@RestController
@RequestMapping("/api/inventory/stock-entries")
class StockEntryController {

  private final StockEntryService service;

  StockEntryController(StockEntryService service) {
    this.service = service;
  }

  @PostMapping
  ResponseEntity<StockEntryResponse> applyStockReceipt(
      @Valid @RequestBody ApplyStockReceiptRequest request) {
    StockEntryRecord entry =
        service.applyStockReceipt(
            new StockEntryService.ApplyStockReceiptCommand(
                request.inventoryItemId(),
                request.stockLotId(),
                request.purchaseOrderId(),
                request.purchaseOrderLineId(),
                request.quantity(),
                request.entryType(),
                request.reasonCode(),
                request.actorId()));
    return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED)
        .body(StockEntryResponse.from(entry));
  }

  @GetMapping
  ResponseEntity<List<StockEntryResponse>> listStockEntries(
      @RequestParam String tenantId,
      @RequestParam String laboratoryId,
      @RequestParam String branchId) {
    return ResponseEntity.ok(
        service.listEntries(tenantId, laboratoryId, branchId).stream()
            .map(StockEntryResponse::from)
            .toList());
  }

  record ApplyStockReceiptRequest(
      @NotBlank String inventoryItemId,
      String stockLotId,
      String purchaseOrderId,
      String purchaseOrderLineId,
      BigDecimal quantity,
      @NotBlank String entryType,
      String reasonCode,
      @NotBlank String actorId) {}

  record StockEntryResponse(
      String stockEntryId,
      String inventoryItemId,
      String stockLotId,
      String tenantId,
      String laboratoryId,
      String branchId,
      String purchaseOrderLineId,
      BigDecimal quantity,
      String entryType,
      String reasonCode,
      Instant receivedAt) {

    static StockEntryResponse from(StockEntryRecord record) {
      return new StockEntryResponse(
          record.stockEntryId(),
          record.inventoryItemId(),
          record.stockLotId(),
          record.tenantId(),
          record.laboratoryId(),
          record.branchId(),
          record.purchaseOrderLineId(),
          record.quantity(),
          record.entryType(),
          record.reasonCode(),
          record.receivedAt().atZone(ZoneOffset.UTC).toInstant());
    }
  }
}
