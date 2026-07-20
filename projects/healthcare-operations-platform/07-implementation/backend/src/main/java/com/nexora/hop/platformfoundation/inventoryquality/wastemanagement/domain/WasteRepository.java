package com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.domain;

import java.util.List;

public interface WasteRepository {

  WasteRecord save(WasteRecord record);

  List<WasteRecord> findByScope(String tenantId, String laboratoryId, String branchId);
}
