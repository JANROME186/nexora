package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Attached identification, insurance or medical support document. Modeled as ENT-PAT-005 in the
 * Patient Management business model.
 */
public record PatientDocument(
        String documentId,
        String patientId,
        String category,
        String fileReference,
        Instant uploadedAt,
        LocalDate expiresAt) {

    public static final String CATEGORY_IDENTIFICATION = "identification";
    public static final String CATEGORY_INSURANCE = "insurance";
    public static final String CATEGORY_AUTHORIZATION = "authorization";
    public static final String CATEGORY_MEDICAL_REPORT = "medical_report";
    public static final String CATEGORY_OTHER = "other";
}
