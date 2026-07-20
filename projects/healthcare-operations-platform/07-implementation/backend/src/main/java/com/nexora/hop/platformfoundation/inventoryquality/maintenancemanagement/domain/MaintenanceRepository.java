package com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.domain;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository {

  MaintenanceEvent save(MaintenanceEvent event);

  Optional<MaintenanceEvent> findById(String maintenanceEventId);

  List<MaintenanceEvent> findByInventoryItemId(String inventoryItemId);
}
