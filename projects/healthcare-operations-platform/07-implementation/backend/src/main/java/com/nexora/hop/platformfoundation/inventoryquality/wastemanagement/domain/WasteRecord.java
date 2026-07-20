package com.nexora.hop.platformfoundation.inventoryquality.wastemanagement.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** BCM-INV-009 WasteRecord (immutable disposal receipt). */
public record WasteRecord(
    String wasteRecordId,
    String inventoryItemId,
    String stockLotId,
    String tenantId,
    String laboratoryId,
    String branchId,
    BigDecimal disposedQuantity,
    String reasonCode,
    String reasonNote,
    LocalDateTime disposedAt,
    String createdBy,
    LocalDateTime createdAt) {}
