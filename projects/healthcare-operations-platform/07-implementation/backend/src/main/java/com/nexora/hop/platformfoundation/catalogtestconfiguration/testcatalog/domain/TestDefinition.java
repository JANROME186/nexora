package com.nexora.hop.platformfoundation.catalogtestconfiguration.testcatalog.domain;

import java.time.Instant;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

/**
 * The atomic orderable clinical test definition (BCM-SVC-002, AGG-006 TestDefinition root).
 * Modeled in bcm-svc-002-test-catalog/business-model.yaml (ENT-TST-001).
 */
public record TestDefinition(
        String testDefinitionId,
        String tenantId,
        String laboratoryId,
        String code,
        LocalizedText name,
        String methodology,
        String measurementUnit,
        String resultType,
        Integer turnaroundTimeHours,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_RETIRED = "retired";

    public static final String RESULT_NUMERIC = "numeric";
    public static final String RESULT_QUALITATIVE = "qualitative";
    public static final String RESULT_SEMI_QUANTITATIVE = "semi_quantitative";
    public static final String RESULT_TEXT = "text";
    public static final String RESULT_STRUCTURED = "structured";
}
