package com.nexora.hop.platformfoundation.catalogtestconfiguration.patientpreparationmanagement.domain;

import java.time.Instant;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.LocalizedText;

/**
 * A patient preparation instruction associated with tests or panels (BCM-SVC-005).
 * Modeled in bcm-svc-005-patient-preparation-management/business-model.md (ENT-PRP-001).
 */
public record PreparationInstruction(
        String preparationId,
        String tenantId,
        String laboratoryId,
        String code,
        LocalizedText title,
        LocalizedText instructionText,
        String category,
        Integer durationHours,
        String status,
        int version,
        Instant createdAt,
        Instant updatedAt) {

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_RETIRED = "retired";

    public static final String CATEGORY_FASTING = "fasting";
    public static final String CATEGORY_MEDICATION = "medication";
    public static final String CATEGORY_ACTIVITY = "activity";
    public static final String CATEGORY_TIMING = "timing";
    public static final String CATEGORY_HYDRATION = "hydration";
    public static final String CATEGORY_OTHER = "other";
}
