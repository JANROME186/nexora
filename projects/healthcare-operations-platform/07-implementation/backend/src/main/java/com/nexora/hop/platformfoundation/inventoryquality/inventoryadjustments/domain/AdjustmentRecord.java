package com.nexora.hop.platformfoundation.inventoryquality.inventoryadjustments.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** BCM-INV-008 AdjustmentRecord (dual-actor approved correction to on-hand quantity). */
public record AdjustmentRecord(
    String adjustmentId,
    String inventoryItemId,
    String stockLotId,
    String tenantId,
    String laboratoryId,
    String branchId,
    BigDecimal deltaQuantity,
    String reasonCode,
    String reasonNote,
    String approverId,
    String requestedBy,
    LocalDateTime occurredAt,
    String createdBy,
    LocalDateTime createdAt) {}
