package com.nexora.hop.platformfoundation.inventoryquality.equipmentmanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.time.LocalDateTime;

/** Append-only availability transition record for BCM-QLT-004 Equipment Management. */
public record EquipmentAvailabilityChange(
    String changeId,
    String inventoryItemId,
    String tenantId,
    String branchId,
    String previousStatus,
    String newStatus,
    String reasonCode,
    String changedBy,
    LocalDateTime changedAt,
    AuditMetadata audit) {

  public static final String REASON_ROUTINE = "routine";
  public static final String REASON_CALIBRATION_FAILED = "calibration_failed";
  public static final String REASON_MAINTENANCE_SCHEDULED = "maintenance_scheduled";
  public static final String REASON_MAINTENANCE_COMPLETED = "maintenance_completed";
  public static final String REASON_DECOMMISSIONED = "decommissioned";
  public static final String REASON_OTHER = "other";
}
