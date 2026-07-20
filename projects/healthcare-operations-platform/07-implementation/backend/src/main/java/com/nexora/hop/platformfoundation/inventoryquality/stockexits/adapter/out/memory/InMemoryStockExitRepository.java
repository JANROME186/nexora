package com.nexora.hop.platformfoundation.inventoryquality.stockexits.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.stockexits.domain.StockExitRecord;
import com.nexora.hop.platformfoundation.inventoryquality.stockexits.domain.StockExitRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryStockExitRepository implements StockExitRepository {

  private final Map<String, StockExitRecord> exits = new ConcurrentHashMap<>();

  @Override
  public StockExitRecord save(StockExitRecord exit) {
    exits.put(exit.stockExitId(), exit);
    return exit;
  }

  @Override
  public List<StockExitRecord> findByScope(String tenantId, String laboratoryId, String branchId) {
    return exits.values().stream()
        .filter(e -> Objects.equals(e.tenantId(), tenantId))
        .filter(e -> Objects.equals(e.laboratoryId(), laboratoryId))
        .filter(e -> Objects.equals(e.branchId(), branchId))
        .toList();
  }
}
