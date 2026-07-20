package com.nexora.hop.platformfoundation.inventoryquality.procurementmanagement.domain;

import java.math.BigDecimal;

public record PurchaseOrderLine(
    String purchaseOrderLineId,
    String inventoryItemId,
    BigDecimal orderedQuantity,
    BigDecimal unitCost,
    BigDecimal receivedQuantity,
    String lineStatus) {

  public static final String STATUS_PENDING = "pending";
  public static final String STATUS_PARTIALLY_RECEIVED = "partially_received";
  public static final String STATUS_RECEIVED = "received";

  public PurchaseOrderLine withReceipt(BigDecimal additionalQuantity, String newStatus) {
    return new PurchaseOrderLine(
        purchaseOrderLineId,
        inventoryItemId,
        orderedQuantity,
        unitCost,
        receivedQuantity.add(additionalQuantity),
        newStatus);
  }
}
