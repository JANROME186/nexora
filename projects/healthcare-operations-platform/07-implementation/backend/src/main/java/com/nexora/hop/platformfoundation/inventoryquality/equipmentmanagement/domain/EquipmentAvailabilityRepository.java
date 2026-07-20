package com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain;

import java.util.List;

public interface EquipmentAvailabilityRepository {

  EquipmentAvailabilityChange save(EquipmentAvailabilityChange change);

  List<EquipmentAvailabilityChange> findByInventoryItemId(String inventoryItemId);
}
