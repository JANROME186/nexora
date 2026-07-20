package com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.application.LotManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLot;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Rendered controller for bcm-inv-003-lot-management/openapi-source.yaml. */
@RestController
@RequestMapping("/api/inventory/lots")
class StockLotController {

  private final LotManagementService service;

  StockLotController(LotManagementService service) {
    this.service = service;
  }

  @PostMapping("/items/{inventoryItemId}/lots")
  ResponseEntity<StockLotResponse> registerStockLot(
      @PathVariable String inventoryItemId, @Valid @RequestBody RegisterStockLotRequest request) {
    StockLot lot =
        service.registerLot(
            inventoryItemId,
            new LotManagementService.RegisterStockLotCommand(
                request.lotNumber(),
                request.supplierId(),
                request.supplierName(),
                request.expirationDate(),
                request.receivedQuantity(),
                request.actorId()));
    return ResponseEntity.created(URI.create("/api/inventory/lots/lots/" + lot.stockLotId()))
        .body(StockLotResponse.from(lot));
  }

  @GetMapping("/items/{inventoryItemId}/lots")
  ResponseEntity<List<StockLotResponse>> listStockLots(@PathVariable String inventoryItemId) {
    return ResponseEntity.ok(
        service.listLots(inventoryItemId).stream().map(StockLotResponse::from).toList());
  }

  @PostMapping("/lots/{stockLotId}/quarantine")
  ResponseEntity<StockLotResponse> quarantineStockLot(
      @PathVariable String stockLotId, @RequestBody(required = false) ActorRequest request) {
    String actorId = request == null ? "system" : request.actorId();
    return ResponseEntity.ok(StockLotResponse.from(service.quarantineLot(stockLotId, actorId)));
  }

  @PostMapping("/lots/{stockLotId}/expire")
  ResponseEntity<StockLotResponse> expireStockLot(
      @PathVariable String stockLotId, @RequestBody(required = false) ActorRequest request) {
    String actorId = request == null ? "system" : request.actorId();
    return ResponseEntity.ok(StockLotResponse.from(service.expireLot(stockLotId, actorId)));
  }

  record RegisterStockLotRequest(
      @NotBlank String lotNumber,
      String supplierId,
      String supplierName,
      LocalDate expirationDate,
      BigDecimal receivedQuantity,
      @NotBlank String actorId) {}

  record ActorRequest(String actorId) {}

  record StockLotResponse(
      String stockLotId,
      String inventoryItemId,
      String tenantId,
      String laboratoryId,
      String branchId,
      String lotNumber,
      String supplierId,
      String supplierName,
      LocalDate expirationDate,
      BigDecimal receivedQuantity,
      BigDecimal remainingQuantity,
      String status) {

    static StockLotResponse from(StockLot lot) {
      return new StockLotResponse(
          lot.stockLotId(),
          lot.inventoryItemId(),
          lot.tenantId(),
          lot.laboratoryId(),
          lot.branchId(),
          lot.lotNumber(),
          lot.supplier() == null ? null : lot.supplier().supplierId(),
          lot.supplier() == null ? null : lot.supplier().supplierName(),
          lot.expirationDate(),
          lot.receivedQuantity(),
          lot.remainingQuantity(),
          lot.status());
    }
  }
}
