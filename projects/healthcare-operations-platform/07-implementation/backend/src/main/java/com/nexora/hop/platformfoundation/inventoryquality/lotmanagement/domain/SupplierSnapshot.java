package com.nexora.hop.platformfoundation.inventoryquality.lotmanagement.domain;

/**
 * Immutable read-only reference to AGG-014 Supplier (owned by the not-yet-modeled
 * BCM-PER-006 Supplier Management). Captured on the lot at receipt time so the lot's supplier
 * context remains stable even if the master supplier profile changes later.
 */
public record SupplierSnapshot(String supplierId, String supplierName) {}
