package com.nexora.hop.platformfoundation.inventoryquality.productcatalog.adapter.in.web;

import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.application.InventoryItemService;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.EquipmentProfile;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.ReagentProfile;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.StockSummary;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Rendered controller for bcm-inv-001-product-catalog/openapi-source.yaml (InventoryItem resource). */
@RestController
@RequestMapping("/api/inventory/catalog")
class InventoryItemController {

  private final InventoryItemService service;

  InventoryItemController(InventoryItemService service) {
    this.service = service;
  }

  @PostMapping("/items")
  ResponseEntity<InventoryItemResponse> createInventoryItem(
      @Valid @RequestBody RegisterInventoryItemRequest request) {
    InventoryItem created =
        service.registerItem(
            new InventoryItemService.RegisterInventoryItemCommand(
                request.tenantId(),
                request.laboratoryId(),
                request.branchId(),
                request.itemCode(),
                request.itemName(),
                request.itemType(),
                request.classification(),
                request.unitOfMeasure(),
                request.actorId()));
    return ResponseEntity.created(URI.create("/api/inventory/catalog/items/" + created.inventoryItemId()))
        .body(InventoryItemResponse.from(created));
  }

  @GetMapping("/items")
  ResponseEntity<List<InventoryItemResponse>> listInventoryItems(
      @RequestParam String tenantId,
      @RequestParam String laboratoryId,
      @RequestParam String branchId) {
    return ResponseEntity.ok(
        service.listItems(tenantId, laboratoryId, branchId).stream()
            .map(InventoryItemResponse::from)
            .toList());
  }

  @GetMapping("/items/{inventoryItemId}")
  ResponseEntity<InventoryItemResponse> getInventoryItem(@PathVariable String inventoryItemId) {
    return ResponseEntity.ok(InventoryItemResponse.from(service.getItem(inventoryItemId)));
  }

  @PutMapping("/items/{inventoryItemId}")
  ResponseEntity<InventoryItemResponse> updateInventoryItem(
      @PathVariable String inventoryItemId,
      @Valid @RequestBody UpdateInventoryItemRequest request) {
    InventoryItem updated =
        service.updateItem(
            inventoryItemId,
            new InventoryItemService.UpdateInventoryItemCommand(
                request.itemName(),
                request.itemType(),
                request.classification(),
                request.unitOfMeasure(),
                request.status(),
                request.actorId()));
    return ResponseEntity.ok(InventoryItemResponse.from(updated));
  }

  @PostMapping("/items/{inventoryItemId}/discontinue")
  ResponseEntity<InventoryItemResponse> discontinueInventoryItem(
      @PathVariable String inventoryItemId, @RequestBody(required = false) ActorRequest request) {
    String actorId = request == null ? "system" : request.actorId();
    return ResponseEntity.ok(
        InventoryItemResponse.from(service.discontinueItem(inventoryItemId, actorId)));
  }

  record RegisterInventoryItemRequest(
      @NotBlank String tenantId,
      @NotBlank String laboratoryId,
      @NotBlank String branchId,
      @NotBlank String itemCode,
      @NotBlank String itemName,
      @NotBlank String itemType,
      @NotBlank String classification,
      @NotBlank String unitOfMeasure,
      @NotBlank String actorId) {}

  record UpdateInventoryItemRequest(
      @NotBlank String itemName,
      @NotBlank String itemType,
      @NotBlank String classification,
      @NotBlank String unitOfMeasure,
      @NotBlank String status,
      @NotBlank String actorId) {}

  record ActorRequest(String actorId) {}

  record InventoryItemResponse(
      String inventoryItemId,
      String tenantId,
      String laboratoryId,
      String branchId,
      String itemCode,
      String itemName,
      String itemType,
      String classification,
      String unitOfMeasure,
      String status,
      StockSummary stockSummary,
      ReagentProfile reagentProfile,
      EquipmentProfile equipmentProfile,
      Instant createdAt,
      Instant updatedAt) {

    static InventoryItemResponse from(InventoryItem item) {
      return new InventoryItemResponse(
          item.inventoryItemId(),
          item.tenantId(),
          item.laboratoryId(),
          item.branchId(),
          item.itemCode(),
          item.itemName(),
          item.itemType(),
          item.classification(),
          item.unitOfMeasure(),
          item.status(),
          item.stockSummary(),
          item.reagentProfile(),
          item.equipmentProfile(),
          item.audit().createdAt().atZone(ZoneOffset.UTC).toInstant(),
          item.audit().updatedAt().atZone(ZoneOffset.UTC).toInstant());
    }
  }
}
