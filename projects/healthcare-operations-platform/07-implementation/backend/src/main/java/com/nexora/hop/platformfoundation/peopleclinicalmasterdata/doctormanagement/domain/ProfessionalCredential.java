package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.doctormanagement.domain;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Professional licensing credential attached to a doctor. Modeled as ENT-DOC-002 and owned by
 * the {@link Doctor} aggregate. Verification cascade (BCM-PER-003 RN-004) and expiration
 * cascade (RN-005) are deferred to MVP-MOD-003-BE-002.
 */
public record ProfessionalCredential(
        String credentialId,
        String doctorId,
        String credentialType,
        String credentialNumber,
        String issuingAuthority,
        String issuingCountry,
        LocalDate issuedAt,
        LocalDate expiresAt,
        String verificationStatus,
        Instant verifiedAt) {

    public static final String TYPE_MEDICAL_LICENSE = "medical_license";
    public static final String TYPE_SPECIALTY_CERTIFICATION = "specialty_certification";
    public static final String TYPE_BOARD_CERTIFICATION = "board_certification";
    public static final String TYPE_INSTITUTIONAL_REGISTRATION = "institutional_registration";
    public static final String TYPE_OTHER = "other";

    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_VERIFIED = "verified";
    public static final String STATUS_EXPIRED = "expired";
    public static final String STATUS_REVOKED = "revoked";
}
