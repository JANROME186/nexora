package com.nexora.hop.platformfoundation.inventoryquality.productcatalog.domain;

import java.time.LocalDateTime;

/**
 * VO-CAT-003 EquipmentProfile. Delegated field placeholder on the shared aggregate; reserved
 * for BCM-QLT-004 Equipment Management in COM-MOD-010-BE-002. Modeled here in COM-MOD-010-BE-001
 * so the persisted AGG-013 schema is stable across both backend backlog items.
 */
public record EquipmentProfile(
    String assetTag,
    String serialNumber,
    String manufacturer,
    String model,
    LocalDateTime installedAt,
    String location,
    String availabilityStatus) {}
