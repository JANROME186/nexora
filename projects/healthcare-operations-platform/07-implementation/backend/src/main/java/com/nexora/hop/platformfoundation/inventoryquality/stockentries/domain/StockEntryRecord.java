package com.nexora.hop.platformfoundation.inventoryquality.stockentries.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** BCM-INV-005 StockEntryRecord (immutable receipt of an inventory increase). */
public record StockEntryRecord(
    String stockEntryId,
    String inventoryItemId,
    String stockLotId,
    String tenantId,
    String laboratoryId,
    String branchId,
    String purchaseOrderLineId,
    BigDecimal quantity,
    String entryType,
    String reasonCode,
    LocalDateTime receivedAt,
    String createdBy,
    LocalDateTime createdAt) {

  public static final String ENTRY_TYPE_PURCHASE_RECEIPT = "purchase_receipt";
  public static final String ENTRY_TYPE_TRANSFER_IN = "transfer_in";
  public static final String ENTRY_TYPE_INITIAL_LOAD = "initial_load";
  public static final String ENTRY_TYPE_RETURN = "return";
}
