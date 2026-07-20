package com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.domain.WasteRecord;
import com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.domain.WasteRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryWasteRepository implements WasteRepository {

  private final Map<String, WasteRecord> records = new ConcurrentHashMap<>();

  @Override
  public WasteRecord save(WasteRecord record) {
    records.put(record.wasteRecordId(), record);
    return record;
  }

  @Override
  public List<WasteRecord> findByScope(String tenantId, String laboratoryId, String branchId) {
    return records.values().stream()
        .filter(r -> Objects.equals(r.tenantId(), tenantId))
        .filter(r -> Objects.equals(r.laboratoryId(), laboratoryId))
        .filter(r -> Objects.equals(r.branchId(), branchId))
        .toList();
  }
}
