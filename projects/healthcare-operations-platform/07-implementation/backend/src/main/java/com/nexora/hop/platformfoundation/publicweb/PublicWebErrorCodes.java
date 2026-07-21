package com.nexora.hop.platformfoundation.publicweb;

/**
 * Canonical structured error codes for COM-MOD-011 public-classified operations. Every value has
 * a matching {@code public.error.<code, lowercase>} entry in every i18n bundle so the shared
 * {@code messageKey} field on the error envelope is always resolvable.
 */
public final class PublicWebErrorCodes {

    /** RN-004/RN-007: the anonymous public rate limit window was exceeded. */
    public static final String PUBLIC_RATE_LIMIT_EXCEEDED = "PUBLIC_RATE_LIMIT_EXCEEDED";

    /** The requested catalog record is not published and therefore invisible to public callers. */
    public static final String PUBLIC_CATALOG_NOT_PUBLISHED = "PUBLIC_CATALOG_NOT_PUBLISHED";

    /** Generic invalid public appointment intake command (BCM-ATT-001 RN-008). */
    public static final String PUBLIC_APPOINTMENT_REQUEST_INVALID = "PUBLIC_APPOINTMENT_REQUEST_INVALID";

    /** Generic invalid public quotation intake command (BCM-ATT-006 RN-009). */
    public static final String PUBLIC_QUOTATION_REQUEST_INVALID = "PUBLIC_QUOTATION_REQUEST_INVALID";

    /** RN-008/RN-009: at least one prospective-contact reachable field must be supplied. */
    public static final String PUBLIC_PROSPECTIVE_CONTACT_REQUIRED = "PUBLIC_PROSPECTIVE_CONTACT_REQUIRED";

    /** Non-{@code public_website} channels cannot be posted through {@code /api/public/**}. */
    public static final String PUBLIC_CHANNEL_FORBIDDEN = "PUBLIC_CHANNEL_FORBIDDEN";

    private PublicWebErrorCodes() {
    }
}
