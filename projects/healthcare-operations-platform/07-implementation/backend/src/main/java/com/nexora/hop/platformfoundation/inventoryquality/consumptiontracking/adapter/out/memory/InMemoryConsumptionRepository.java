package com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.domain.ConsumptionRecord;
import com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.domain.ConsumptionRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryConsumptionRepository implements ConsumptionRepository {

  private final Map<String, ConsumptionRecord> records = new ConcurrentHashMap<>();

  @Override
  public ConsumptionRecord save(ConsumptionRecord record) {
    records.put(record.consumptionRecordId(), record);
    return record;
  }

  @Override
  public List<ConsumptionRecord> findByScope(String tenantId, String laboratoryId, String branchId) {
    return records.values().stream()
        .filter(r -> Objects.equals(r.tenantId(), tenantId))
        .filter(r -> Objects.equals(r.laboratoryId(), laboratoryId))
        .filter(r -> Objects.equals(r.branchId(), branchId))
        .toList();
  }
}
