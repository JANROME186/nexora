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
     * Masks the document number using the platform default policy (last 4 characters visible,
     * {@code '*'} mask character). Read-model projections that need tenant-configured masking
     * (BCM-PER-002 RN-008, BCM-PER-003 RN-008) should call {@link #maskedNumber(DocumentNumberMaskingPolicy)}
     * with the tenant's policy instead.
     */
    public String maskedNumber() {
        return maskedNumber(DocumentNumberMaskingPolicy.DEFAULT);
    }

    /**
     * Masks the document number per {@code policy}: the last {@code policy.visibleCharacters()}
     * characters stay visible and the rest are replaced with {@code policy.maskCharacter()}; a
     * number no longer than the visible-character count is masked in full so no digit is ever
     * exposed by a permissive policy applied to a short number.
     */
    public String maskedNumber(DocumentNumberMaskingPolicy policy) {
        if (documentNumber == null || documentNumber.isBlank()) {
            return null;
        }
        String trimmed = documentNumber.trim();
        String maskChar = String.valueOf(policy.maskCharacter());
        int visible = policy.visibleCharacters();
        if (trimmed.length() <= visible) {
            return maskChar.repeat(trimmed.length());
        }
        return maskChar.repeat(trimmed.length() - visible) + trimmed.substring(trimmed.length() - visible);
    }

    /**
     * Tenant-configurable document/credential number masking policy (BCM-PER-002 RN-008,
     * BCM-PER-003 RN-008). {@link #DEFAULT} reproduces the platform's original fixed masking
     * behavior (last 4 characters visible, {@code '*'} mask character). Closes technical debt
     * TD-BE-008.
     */
    public record DocumentNumberMaskingPolicy(int visibleCharacters, char maskCharacter) {

        public static final DocumentNumberMaskingPolicy DEFAULT = new DocumentNumberMaskingPolicy(4, '*');

        public DocumentNumberMaskingPolicy {
            if (visibleCharacters < 0) {
                throw new IllegalArgumentException("Visible character count must not be negative.");
            }
        }
    }
}
