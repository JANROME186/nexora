package com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.domain;

import java.util.List;

public interface ConsumptionRepository {

  ConsumptionRecord save(ConsumptionRecord record);

  List<ConsumptionRecord> findByScope(String tenantId, String laboratoryId, String branchId);
}
