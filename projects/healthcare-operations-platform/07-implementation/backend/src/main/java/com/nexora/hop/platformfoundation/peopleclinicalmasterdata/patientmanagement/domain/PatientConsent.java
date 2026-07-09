package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.patientmanagement.domain;

import java.time.Instant;

/**
 * Recorded consent statement given by the patient or representative. Modeled as ENT-PAT-004.
 * Consent revocation is append-only (BCM-PER-002 RN-007); revocation-history behavior is deferred
 * to MVP-MOD-003-BE-002.
 */
public record PatientConsent(
        String consentId,
        String patientId,
        String consentType,
        boolean granted,
        String grantedBy,
        Instant grantedAt,
        Instant revokedAt,
        String evidenceReference) {

    public static final String TYPE_DATA_PROCESSING = "data_processing";
    public static final String TYPE_PORTAL_ACCESS = "portal_access";
    public static final String TYPE_NOTIFICATION_CHANNEL = "notification_channel";
    public static final String TYPE_MARKETING = "marketing";
    public static final String TYPE_RESEARCH = "research";

    public static final String GRANTED_BY_PATIENT = "patient";
    public static final String GRANTED_BY_REPRESENTATIVE = "representative";
}
