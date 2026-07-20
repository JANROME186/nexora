package com.nexora.hop.platformfoundation.inventoryquality.stockexits.domain;

import java.util.List;

public interface StockExitRepository {

  StockExitRecord save(StockExitRecord exit);

  List<StockExitRecord> findByScope(String tenantId, String laboratoryId, String branchId);
}
