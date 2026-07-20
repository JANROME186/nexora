package com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain.EquipmentAvailabilityChange;
import com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain.EquipmentAvailabilityRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryEquipmentAvailabilityRepository implements EquipmentAvailabilityRepository {

  private final Map<String, EquipmentAvailabilityChange> changes = new ConcurrentHashMap<>();

  @Override
  public EquipmentAvailabilityChange save(EquipmentAvailabilityChange change) {
    changes.put(change.changeId(), change);
    return change;
  }

  @Override
  public List<EquipmentAvailabilityChange> findByInventoryItemId(String inventoryItemId) {
    return changes.values().stream()
        .filter(change -> Objects.equals(change.inventoryItemId(), inventoryItemId))
        .sorted(Comparator.comparing(EquipmentAvailabilityChange::changedAt))
        .toList();
  }
}
