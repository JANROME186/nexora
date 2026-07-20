package com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * New aggregate for BCM-INV-003 Lot Management. Tracks lot-level metadata (lotNumber, supplier
 * snapshot, expiration date) and remainingQuantity for a specific batch of an InventoryItem. Its
 * remainingQuantity may only be decremented by BCM-INV-006 StockExits, BCM-INV-007
 * ConsumptionTracking, BCM-INV-008 InventoryAdjustments and BCM-INV-009 WasteManagement.
 */
public record StockLot(
    String stockLotId,
    String inventoryItemId,
    String tenantId,
    String laboratoryId,
    String branchId,
    String lotNumber,
    SupplierSnapshot supplier,
    LocalDate expirationDate,
    BigDecimal receivedQuantity,
    BigDecimal remainingQuantity,
    String status,
    AuditMetadata audit) {

  public static final String STATUS_AVAILABLE = "available";
  public static final String STATUS_QUARANTINED = "quarantined";
  public static final String STATUS_EXPIRED = "expired";
  public static final String STATUS_DISPOSED = "disposed";

  public boolean canBeConsumed() {
    return STATUS_AVAILABLE.equals(status);
  }

  public boolean isTerminal() {
    return STATUS_DISPOSED.equals(status) || STATUS_EXPIRED.equals(status);
  }

  public StockLot withRemainingQuantity(BigDecimal newQuantity, AuditMetadata newAudit) {
    return new StockLot(
        stockLotId,
        inventoryItemId,
        tenantId,
        laboratoryId,
        branchId,
        lotNumber,
        supplier,
        expirationDate,
        receivedQuantity,
        newQuantity,
        status,
        newAudit);
  }

  public StockLot withStatus(String newStatus, AuditMetadata newAudit) {
    return new StockLot(
        stockLotId,
        inventoryItemId,
        tenantId,
        laboratoryId,
        branchId,
        lotNumber,
        supplier,
        expirationDate,
        receivedQuantity,
        remainingQuantity,
        newStatus,
        newAudit);
  }
}
