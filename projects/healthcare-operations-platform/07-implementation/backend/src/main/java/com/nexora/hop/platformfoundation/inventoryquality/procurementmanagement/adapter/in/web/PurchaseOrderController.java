package com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.application.ProcurementManagementService;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrder;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrderLine;
import com.nexora.hop.platformfoundation.inventoryquality.stockentries.application.StockEntryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Rendered controller for bcm-inv-004-procurement-management/openapi-source.md. */
@RestController
@RequestMapping("/api/inventory/purchase-orders")
class PurchaseOrderController {

  private final ProcurementManagementService procurement;
  private final StockEntryService stockEntryService;

  PurchaseOrderController(
      ProcurementManagementService procurement, StockEntryService stockEntryService) {
    this.procurement = procurement;
    this.stockEntryService = stockEntryService;
  }

  @PostMapping
  ResponseEntity<PurchaseOrderResponse> createPurchaseOrder(
      @Valid @RequestBody CreatePurchaseOrderRequest request) {
    List<ProcurementManagementService.CreatePurchaseOrderCommand.LineCommand> lines =
        request.lines().stream()
            .map(
                l ->
                    new ProcurementManagementService.CreatePurchaseOrderCommand.LineCommand(
                        l.inventoryItemId(), l.orderedQuantity(), l.unitCost()))
            .toList();
    PurchaseOrder order =
        procurement.createOrder(
            new ProcurementManagementService.CreatePurchaseOrderCommand(
                request.tenantId(),
                request.laboratoryId(),
                request.branchId(),
                request.supplierId(),
                request.supplierName(),
                request.currencyCode(),
                lines,
                request.actorId()));
    return ResponseEntity.created(URI.create("/api/inventory/purchase-orders/" + order.purchaseOrderId()))
        .body(PurchaseOrderResponse.from(order));
  }

  @GetMapping
  ResponseEntity<List<PurchaseOrderResponse>> listPurchaseOrders(
      @RequestParam String tenantId,
      @RequestParam String laboratoryId,
      @RequestParam String branchId) {
    return ResponseEntity.ok(
        procurement.listOrders(tenantId, laboratoryId, branchId).stream()
            .map(PurchaseOrderResponse::from)
            .toList());
  }

  @GetMapping("/{purchaseOrderId}")
  ResponseEntity<PurchaseOrderResponse> getPurchaseOrder(@PathVariable String purchaseOrderId) {
    return ResponseEntity.ok(PurchaseOrderResponse.from(procurement.getOrder(purchaseOrderId)));
  }

  @PostMapping("/{purchaseOrderId}/submit")
  ResponseEntity<PurchaseOrderResponse> submitPurchaseOrder(
      @PathVariable String purchaseOrderId, @RequestBody(required = false) ActorRequest request) {
    String actorId = request == null ? "system" : request.actorId();
    return ResponseEntity.ok(PurchaseOrderResponse.from(procurement.submitOrder(purchaseOrderId, actorId)));
  }

  @PostMapping("/{purchaseOrderId}/approve")
  ResponseEntity<PurchaseOrderResponse> approvePurchaseOrder(
      @PathVariable String purchaseOrderId, @Valid @RequestBody ApprovalRequest request) {
    return ResponseEntity.ok(
        PurchaseOrderResponse.from(procurement.approveOrder(purchaseOrderId, request.actorId())));
  }

  @PostMapping("/{purchaseOrderId}/cancel")
  ResponseEntity<PurchaseOrderResponse> cancelPurchaseOrder(
      @PathVariable String purchaseOrderId, @Valid @RequestBody CancelRequest request) {
    return ResponseEntity.ok(
        PurchaseOrderResponse.from(
            procurement.cancelOrder(purchaseOrderId, request.reason(), request.actorId())));
  }

  @PostMapping("/{purchaseOrderId}/lines/{lineId}/receive")
  ResponseEntity<PurchaseOrderResponse> receivePurchaseOrderLine(
      @PathVariable String purchaseOrderId,
      @PathVariable String lineId,
      @Valid @RequestBody ReceiveLineRequest request) {
    // Delegate to BCM-INV-005 StockEntries; the entry service coordinates the InventoryItem
    // stockSummary mutation and calls back into ProcurementManagement for the header/line state
    // update (single source of truth for RN-002 receive-vs-ordered validation).
    PurchaseOrder order =
        stockEntryService.applyReceiptForPurchaseOrderLine(
            purchaseOrderId, lineId, request.receivedQuantity(), request.stockLotId(),
            request.actorId());
    return ResponseEntity.ok(PurchaseOrderResponse.from(order));
  }

  record CreatePurchaseOrderRequest(
      @NotBlank String tenantId,
      @NotBlank String laboratoryId,
      @NotBlank String branchId,
      @NotBlank String supplierId,
      @NotBlank String supplierName,
      @NotBlank String currencyCode,
      @NotEmpty List<LineRequest> lines,
      @NotBlank String actorId) {

    record LineRequest(
        @NotBlank String inventoryItemId, BigDecimal orderedQuantity, BigDecimal unitCost) {}
  }

  record ActorRequest(String actorId) {}

  record ApprovalRequest(@NotBlank String actorId) {}

  record CancelRequest(@NotBlank String reason, @NotBlank String actorId) {}

  record ReceiveLineRequest(BigDecimal receivedQuantity, String stockLotId, @NotBlank String actorId) {}

  record PurchaseOrderResponse(
      String purchaseOrderId,
      String tenantId,
      String laboratoryId,
      String branchId,
      String supplierId,
      String supplierName,
      String status,
      BigDecimal totalAmount,
      String currencyCode,
      String approverId,
      String cancellationReason,
      List<LineResponse> lines) {

    static PurchaseOrderResponse from(PurchaseOrder order) {
      return new PurchaseOrderResponse(
          order.purchaseOrderId(),
          order.tenantId(),
          order.laboratoryId(),
          order.branchId(),
          order.supplier().supplierId(),
          order.supplier().supplierName(),
          order.status(),
          order.totalAmount(),
          order.currencyCode(),
          order.approverId(),
          order.cancellationReason(),
          order.lines().stream().map(LineResponse::from).toList());
    }
  }

  record LineResponse(
      String purchaseOrderLineId,
      String inventoryItemId,
      BigDecimal orderedQuantity,
      BigDecimal unitCost,
      BigDecimal receivedQuantity,
      String lineStatus) {

    static LineResponse from(PurchaseOrderLine line) {
      return new LineResponse(
          line.purchaseOrderLineId(),
          line.inventoryItemId(),
          line.orderedQuantity(),
          line.unitCost(),
          line.receivedQuantity(),
          line.lineStatus());
    }
  }
}
