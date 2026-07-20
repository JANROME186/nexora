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
    String availabilityStatus) {

  public static final String STATUS_AVAILABLE = "available";
  public static final String STATUS_IN_USE = "in_use";
  public static final String STATUS_OUT_OF_SERVICE = "out_of_service";
  public static final String STATUS_RETIRED = "retired";

  public EquipmentProfile withAvailabilityStatus(String status) {
    return new EquipmentProfile(
        assetTag, serialNumber, manufacturer, model, installedAt, location, status);
  }
}
