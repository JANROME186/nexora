package com.nexora.hop.platformfoundation.catalogtestconfiguration.analytecatalog.domain;

import java.time.Instant;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

/**
 * An atomic measurable component of a diagnostic test (BCM-SVC-004).
 * Modeled in bcm-svc-004-analyte-catalog/business-model.yaml (ENT-ANL-001).
 */
public record AnalyteDefinition(
        String analyteId,
        String tenantId,
        String laboratoryId,
        String code,
        LocalizedText name,
        String loincCode,
        String resultDataType,
        String measurementUnit,
        Integer decimalPrecision,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_RETIRED = "retired";

    public static final String TYPE_NUMERIC = "numeric";
    public static final String TYPE_QUALITATIVE = "qualitative";
    public static final String TYPE_SEMI_QUANTITATIVE = "semi_quantitative";
    public static final String TYPE_TEXT = "text";
    public static final String TYPE_CODED = "coded";
}
