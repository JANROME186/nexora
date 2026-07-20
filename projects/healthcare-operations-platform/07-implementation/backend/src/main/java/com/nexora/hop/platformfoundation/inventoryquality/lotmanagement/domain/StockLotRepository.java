package com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface StockLotRepository {

  StockLot save(StockLot lot);

  Optional<StockLot> findById(String stockLotId);

  Optional<StockLot> findByInventoryItemIdAndLotNumber(String inventoryItemId, String lotNumber);

  List<StockLot> findByInventoryItemId(String inventoryItemId);
}
