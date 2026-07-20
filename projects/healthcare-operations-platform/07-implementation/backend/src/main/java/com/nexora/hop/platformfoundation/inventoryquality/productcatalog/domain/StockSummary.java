package com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * VO-CAT-001 StockSummary. Rollup value object owned structurally by BCM-INV-001 but mutated
 * exclusively through the Apply* delegated commands of BCM-INV-005/006/007/008/009. See
 * business-model.yaml INV-CAT-002 (onHandQuantity must never go negative).
 */
public record StockSummary(
    BigDecimal onHandQuantity,
    BigDecimal reservedQuantity,
    BigDecimal reorderPoint,
    BigDecimal reorderQuantity,
    LocalDateTime lastMovementAt) {

  public static StockSummary empty() {
    return new StockSummary(BigDecimal.ZERO, BigDecimal.ZERO, null, null, null);
  }

  public StockSummary withOnHandDelta(BigDecimal delta, LocalDateTime at) {
    BigDecimal newOnHand = onHandQuantity.add(delta);
    return new StockSummary(newOnHand, reservedQuantity, reorderPoint, reorderQuantity, at);
  }
}
