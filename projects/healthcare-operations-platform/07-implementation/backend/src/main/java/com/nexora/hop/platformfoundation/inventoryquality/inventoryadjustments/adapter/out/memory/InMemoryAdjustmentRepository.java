package com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.domain.AdjustmentRecord;
import com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.domain.AdjustmentRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryAdjustmentRepository implements AdjustmentRepository {

  private final Map<String, AdjustmentRecord> records = new ConcurrentHashMap<>();

  @Override
  public AdjustmentRecord save(AdjustmentRecord record) {
    records.put(record.adjustmentId(), record);
    return record;
  }

  @Override
  public List<AdjustmentRecord> findByScope(String tenantId, String laboratoryId, String branchId) {
    return records.values().stream()
        .filter(r -> Objects.equals(r.tenantId(), tenantId))
        .filter(r -> Objects.equals(r.laboratoryId(), laboratoryId))
        .filter(r -> Objects.equals(r.branchId(), branchId))
        .toList();
  }
}
