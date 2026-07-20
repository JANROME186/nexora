package com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.domain;

import java.util.List;

public interface CalibrationRepository {

  CalibrationEvent save(CalibrationEvent event);

  List<CalibrationEvent> findByInventoryItemId(String inventoryItemId);
}
