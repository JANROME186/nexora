package com.nexora.hop.platformfoundation.peopleclinicalmasterdata.shared;

import java.time.LocalDate;

/**
 * Government or institutional identification document, modeled as {@code VO-PER-003} in the
 * Person Management business model. Reused by Patient (BCM-PER-002) and Doctor (BCM-PER-003)
 * aggregates as the primary identifier.
 */
public record PersonDocument(
        String documentType,
        String documentNumber,
        String issuingCountry,
        LocalDate issuedAt,
        LocalDate expiresAt) {

    public static final String TYPE_NATIONAL_ID = "national_id";
    public static final String TYPE_PASSPORT = "passport";
    public static final String TYPE_DRIVERS_LICENSE = "drivers_license";
    public static final String TYPE_TAX_ID = "tax_id";
    public static final String TYPE_PROFESSIONAL_LICENSE = "professional_license";
    public static final String TYPE_OTHER = "other";

    /**
     * Simple tenant-agnostic masking used by read-model projections (see BCM-PER-002 RN-008 and
     * BCM-PER-003 RN-008). Advanced tenant-configured masking is deferred to MVP-MOD-003-BE-002.
     */
    public String maskedNumber() {
        if (documentNumber == null || documentNumber.isBlank()) {
            return null;
        }
        String trimmed = documentNumber.trim();
        if (trimmed.length() <= 4) {
            return "*".repeat(trimmed.length());
        }
        int visible = 4;
        return "*".repeat(trimmed.length() - visible) + trimmed.substring(trimmed.length() - visible);
    }
}
