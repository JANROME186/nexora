package com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.adapter.out.memory;

import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrder;
import com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain.PurchaseOrderRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
class InMemoryPurchaseOrderRepository implements PurchaseOrderRepository {

  private final Map<String, PurchaseOrder> orders = new ConcurrentHashMap<>();

  @Override
  public PurchaseOrder save(PurchaseOrder order) {
    orders.put(order.purchaseOrderId(), order);
    return order;
  }

  @Override
  public Optional<PurchaseOrder> findById(String purchaseOrderId) {
    return Optional.ofNullable(orders.get(purchaseOrderId));
  }

  @Override
  public List<PurchaseOrder> findByScope(String tenantId, String laboratoryId, String branchId) {
    return orders.values().stream()
        .filter(o -> Objects.equals(o.tenantId(), tenantId))
        .filter(o -> Objects.equals(o.laboratoryId(), laboratoryId))
        .filter(o -> Objects.equals(o.branchId(), branchId))
        .toList();
  }
}
