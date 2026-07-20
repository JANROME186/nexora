package com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLot;
import com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain.StockLotRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryStockLotRepository implements StockLotRepository {

  private final Map<String, StockLot> lots = new ConcurrentHashMap<>();

  @Override
  public StockLot save(StockLot lot) {
    lots.put(lot.stockLotId(), lot);
    return lot;
  }

  @Override
  public Optional<StockLot> findById(String stockLotId) {
    return Optional.ofNullable(lots.get(stockLotId));
  }

  @Override
  public Optional<StockLot> findByInventoryItemIdAndLotNumber(
      String inventoryItemId, String lotNumber) {
    return lots.values().stream()
        .filter(lot -> Objects.equals(lot.inventoryItemId(), inventoryItemId))
        .filter(lot -> Objects.equals(lot.lotNumber(), lotNumber))
        .findFirst();
  }

  @Override
  public List<StockLot> findByInventoryItemId(String inventoryItemId) {
    return lots.values().stream()
        .filter(lot -> Objects.equals(lot.inventoryItemId(), inventoryItemId))
        .toList();
  }
}
