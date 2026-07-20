package com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.domain.MaintenanceEvent;
import com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.domain.MaintenanceRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryMaintenanceRepository implements MaintenanceRepository {

  private final ConcurrentHashMap<String, MaintenanceEvent> events = new ConcurrentHashMap<>();

  @Override
  public MaintenanceEvent save(MaintenanceEvent event) {
    events.put(event.maintenanceEventId(), event);
    return event;
  }

  @Override
  public Optional<MaintenanceEvent> findById(String maintenanceEventId) {
    return Optional.ofNullable(events.get(maintenanceEventId));
  }

  @Override
  public List<MaintenanceEvent> findByInventoryItemId(String inventoryItemId) {
    return events.values().stream()
        .filter(event -> event.inventoryItemId().equals(inventoryItemId))
        .sorted(Comparator.comparing(MaintenanceEvent::startedAt))
        .toList();
  }
}
