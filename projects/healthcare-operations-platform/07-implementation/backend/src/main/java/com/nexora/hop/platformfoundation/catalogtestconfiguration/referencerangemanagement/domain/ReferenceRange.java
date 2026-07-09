package com.nexora.hop.platformfoundation.catalogtestconfiguration.referencerangemanagement.domain;

import java.time.Instant;
import java.time.LocalDate;

/**
 * A demographic-segmented range of expected analyte values (BCM-SVC-006).
 * Modeled in bcm-svc-006-reference-range-management/business-model.yaml (ENT-REF-001).
 */
public record ReferenceRange(
        String rangeId,
        String tenantId,
        String laboratoryId,
        String analyteRefId,
        int version,
        String status,
        LocalDate effectiveFrom,
        LocalDate effectiveTo,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_RETIRED = "retired";
}
