package com.nexora.hop.platformfoundation.inventoryquality.productcatalog.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItem;
import com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain.InventoryItemRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryInventoryItemRepository implements InventoryItemRepository {

  private final Map<String, InventoryItem> items = new ConcurrentHashMap<>();

  @Override
  public InventoryItem save(InventoryItem item) {
    items.put(item.inventoryItemId(), item);
    return item;
  }

  @Override
  public Optional<InventoryItem> findById(String inventoryItemId) {
    return Optional.ofNullable(items.get(inventoryItemId));
  }

  @Override
  public Optional<InventoryItem> findByScopeAndCode(
      String tenantId, String laboratoryId, String branchId, String itemCode) {
    return items.values().stream()
        .filter(item -> Objects.equals(item.tenantId(), tenantId))
        .filter(item -> Objects.equals(item.laboratoryId(), laboratoryId))
        .filter(item -> Objects.equals(item.branchId(), branchId))
        .filter(item -> Objects.equals(item.itemCode(), itemCode))
        .findFirst();
  }

  @Override
  public List<InventoryItem> findByScope(String tenantId, String laboratoryId, String branchId) {
    return items.values().stream()
        .filter(item -> Objects.equals(item.tenantId(), tenantId))
        .filter(item -> Objects.equals(item.laboratoryId(), laboratoryId))
        .filter(item -> Objects.equals(item.branchId(), branchId))
        .toList();
  }
}
