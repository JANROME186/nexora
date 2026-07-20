package com.nexora.hop.platformfoundation.inventoryquality.consumptiontracking.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** BCM-INV-007 ConsumptionRecord. */
public record ConsumptionRecord(
    String consumptionRecordId,
    String inventoryItemId,
    String stockLotId,
    String tenantId,
    String laboratoryId,
    String branchId,
    String diagnosticOrderId,
    String testDefinitionId,
    BigDecimal consumedQuantity,
    String consumptionContext,
    LocalDateTime occurredAt,
    String createdBy,
    LocalDateTime createdAt) {

  public static final String CONTEXT_TEST_PROCESSING = "test_processing";
  public static final String CONTEXT_INTERNAL_QC = "internal_qc";
  public static final String CONTEXT_CALIBRATION = "calibration";
}
