package com.nexora.hop.platformfoundation.inventoryquality.maintenancemanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import java.time.LocalDateTime;

/** Maintenance event for BCM-QLT-005 Maintenance Management. */
public record MaintenanceEvent(
    String maintenanceEventId,
    String inventoryItemId,
    String tenantId,
    String branchId,
    String maintenanceType,
    String performedBy,
    String externalTechnicianRef,
    String description,
    LocalDateTime startedAt,
    LocalDateTime completedAt,
    Integer downtimeMinutes,
    LocalDateTime nextScheduledAt,
    AuditMetadata audit) {

  public static final String TYPE_PREVENTIVE = "preventive";
  public static final String TYPE_CORRECTIVE = "corrective";

  public MaintenanceEvent complete(
      LocalDateTime completionTime, Integer downtime, LocalDateTime nextScheduled, AuditMetadata newAudit) {
    return new MaintenanceEvent(
        maintenanceEventId,
        inventoryItemId,
        tenantId,
        branchId,
        maintenanceType,
        performedBy,
        externalTechnicianRef,
        description,
        startedAt,
        completionTime,
        downtime,
        nextScheduled,
        newAudit);
  }
}
