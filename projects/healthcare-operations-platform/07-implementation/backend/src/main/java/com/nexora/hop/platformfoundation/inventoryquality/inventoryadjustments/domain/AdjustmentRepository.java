package com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.domain;

import java.util.List;

public interface AdjustmentRepository {

  AdjustmentRecord save(AdjustmentRecord record);

  List<AdjustmentRecord> findByScope(String tenantId, String laboratoryId, String branchId);
}
