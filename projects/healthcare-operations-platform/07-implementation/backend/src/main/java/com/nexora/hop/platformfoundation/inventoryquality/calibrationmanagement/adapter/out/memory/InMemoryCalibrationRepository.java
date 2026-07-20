package com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.domain.CalibrationEvent;
import com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.domain.CalibrationRepository;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryCalibrationRepository implements CalibrationRepository {

  private final ConcurrentHashMap<String, CalibrationEvent> events = new ConcurrentHashMap<>();

  @Override
  public CalibrationEvent save(CalibrationEvent event) {
    events.put(event.calibrationEventId(), event);
    return event;
  }

  @Override
  public List<CalibrationEvent> findByInventoryItemId(String inventoryItemId) {
    return events.values().stream()
        .filter(event -> event.inventoryItemId().equals(inventoryItemId))
        .sorted(Comparator.comparing(CalibrationEvent::performedAt))
        .toList();
  }
}
