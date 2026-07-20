package com.nexora.hop.platformfoundation.inventoryquality.calibrationmanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Calibration history event for BCM-QLT-003 Calibration Management. */
public record CalibrationEvent(
    String calibrationEventId,
    String inventoryItemId,
    String tenantId,
    String branchId,
    String calibrationStandardRef,
    String performedBy,
    LocalDateTime performedAt,
    String result,
    LocalDate nextDueDate,
    String certificateReference,
    AuditMetadata audit) {

  public static final String RESULT_PASS = "pass";
  public static final String RESULT_FAIL = "fail";
  public static final String RESULT_ADJUSTED = "adjusted";
}
