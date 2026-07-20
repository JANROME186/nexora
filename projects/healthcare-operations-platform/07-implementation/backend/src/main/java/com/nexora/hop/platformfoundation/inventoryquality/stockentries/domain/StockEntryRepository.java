package com.nexora.hop.platformfoundation.inventoryquality.stockentries.domain;

import java.util.List;

public interface StockEntryRepository {

  StockEntryRecord save(StockEntryRecord entry);

  List<StockEntryRecord> findByInventoryItemId(String inventoryItemId);

  List<StockEntryRecord> findByScope(String tenantId, String laboratoryId, String branchId);
}
