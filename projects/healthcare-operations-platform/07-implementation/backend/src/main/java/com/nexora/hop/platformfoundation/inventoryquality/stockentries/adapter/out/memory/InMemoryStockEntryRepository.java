package com.nexora.hop.platformfoundation.inventoryquality.stockentries.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.stockentries.domain.StockEntryRecord;
import com.nexora.hop.platformfoundation.inventoryquality.stockentries.domain.StockEntryRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryStockEntryRepository implements StockEntryRepository {

  private final Map<String, StockEntryRecord> entries = new ConcurrentHashMap<>();

  @Override
  public StockEntryRecord save(StockEntryRecord entry) {
    entries.put(entry.stockEntryId(), entry);
    return entry;
  }

  @Override
  public List<StockEntryRecord> findByInventoryItemId(String inventoryItemId) {
    return entries.values().stream()
        .filter(e -> Objects.equals(e.inventoryItemId(), inventoryItemId))
        .toList();
  }

  @Override
  public List<StockEntryRecord> findByScope(String tenantId, String laboratoryId, String branchId) {
    return entries.values().stream()
        .filter(e -> Objects.equals(e.tenantId(), tenantId))
        .filter(e -> Objects.equals(e.laboratoryId(), laboratoryId))
        .filter(e -> Objects.equals(e.branchId(), branchId))
        .toList();
  }
}
