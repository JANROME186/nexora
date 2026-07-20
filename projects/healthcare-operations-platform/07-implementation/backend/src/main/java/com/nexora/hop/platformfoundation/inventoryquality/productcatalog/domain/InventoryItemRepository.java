package com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain;

import java.util.List;
import java.util.Optional;

/** Persistence port for AGG-013 InventoryItem. */
public interface InventoryItemRepository {

  InventoryItem save(InventoryItem item);

  Optional<InventoryItem> findById(String inventoryItemId);

  Optional<InventoryItem> findByScopeAndCode(
      String tenantId, String laboratoryId, String branchId, String itemCode);

  List<InventoryItem> findByScope(String tenantId, String laboratoryId, String branchId);
}
