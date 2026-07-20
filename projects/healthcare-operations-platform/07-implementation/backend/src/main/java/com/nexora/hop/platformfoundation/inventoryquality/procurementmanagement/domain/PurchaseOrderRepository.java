package com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository {

  PurchaseOrder save(PurchaseOrder order);

  Optional<PurchaseOrder> findById(String purchaseOrderId);

  List<PurchaseOrder> findByScope(String tenantId, String laboratoryId, String branchId);
}
