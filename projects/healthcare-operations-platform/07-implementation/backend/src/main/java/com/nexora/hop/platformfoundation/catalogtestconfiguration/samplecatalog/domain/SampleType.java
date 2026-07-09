package com.nexora.hop.platformfoundation.catalogtestconfiguration.samplecatalog.domain;

import java.time.Instant;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

/**
 * A catalog of biological sample kinds (BCM-SVC-007).
 * Modeled in bcm-svc-007-sample-catalog/business-model.yaml (ENT-SMP-001).
 */
public record SampleType(
        String sampleTypeId,
        String tenantId,
        String laboratoryId,
        String code,
        LocalizedText name,
        String matrix,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_RETIRED = "retired";

    public static final String MATRIX_BLOOD = "blood";
    public static final String MATRIX_SERUM = "serum";
    public static final String MATRIX_PLASMA = "plasma";
    public static final String MATRIX_URINE = "urine";
    public static final String MATRIX_STOOL = "stool";
    public static final String MATRIX_SWAB = "swab";
    public static final String MATRIX_TISSUE = "tissue";
    public static final String MATRIX_OTHER = "other";
}
