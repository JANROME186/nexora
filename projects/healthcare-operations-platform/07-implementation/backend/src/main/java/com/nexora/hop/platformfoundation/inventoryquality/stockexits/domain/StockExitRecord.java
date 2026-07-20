package com.nexora.hop.platformfoundation.inventoryquality.stockexits.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** BCM-INV-006 StockExitRecord (immutable receipt of an inventory decrease that leaves the branch). */
public record StockExitRecord(
    String stockExitId,
    String inventoryItemId,
    String stockLotId,
    String tenantId,
    String laboratoryId,
    String branchId,
    String destinationBranchId,
    BigDecimal quantity,
    String exitType,
    String reasonCode,
    LocalDateTime occurredAt,
    String createdBy,
    LocalDateTime createdAt) {

  public static final String EXIT_TYPE_INTER_BRANCH_TRANSFER = "inter_branch_transfer";
  public static final String EXIT_TYPE_RETURN_TO_SUPPLIER = "return_to_supplier";
  public static final String EXIT_TYPE_INTERNAL_RELOCATION = "internal_relocation";
}
