package com.nexora.hop.platformfoundation.frontdeskcaredelivery.shared;

/**
 * Stable domain error codes for the custom-implementation rules of BCM-LAB-001, BCM-ATT-001,
 * BCM-ATT-003, BCM-ATT-004 and BCM-ATT-006, extracted from each capability package's
 * {@code openapi-source.md} {@code error_model.domain_errors} list (the model is the source of
 * truth; these constants are a 1:1 mechanical projection of it, not a new design decision).
 * <p>
 * Every {@link FrontDeskConflictException} and {@link FrontDeskEntityNotFoundException} thrown for
 * a custom-implementation rule prefixes its message with one of these codes so API consumers can
 * branch on a stable identifier instead of parsing prose (HOP-QA-ALIGN-005 message
 * externalization baseline). Only codes actually reachable by a runtime throw site are declared
 * here; the model's authorization-scope codes (RN-004/RN-005-class {@code *_SCOPE_MISMATCH} and
 * {@code *_BOUNDARY_VIOLATION} entries) are enforced generically and are not yet backed by a
 * runtime throw site, so they are intentionally not duplicated here until that enforcement exists.
 */
public final class FrontDeskErrorCodes {

    private FrontDeskErrorCodes() {
    }

    // bcm-lab-001-diagnostic-order-management
    public static final String ORDER_DOCTOR_NOT_ELIGIBLE = "ORDER_DOCTOR_NOT_ELIGIBLE";
    public static final String ORDER_NO_LINES = "ORDER_NO_LINES";
    public static final String ORDER_PRICING_SNAPSHOT_REQUIRED = "ORDER_PRICING_SNAPSHOT_REQUIRED";
    public static final String ORDER_TERMINAL_STATE_IMMUTABLE = "ORDER_TERMINAL_STATE_IMMUTABLE";
    public static final String ORDER_CANCELLATION_OVERRIDE_REQUIRED = "ORDER_CANCELLATION_OVERRIDE_REQUIRED";
    public static final String ORDER_CATALOG_ITEM_NOT_PUBLISHED = "ORDER_CATALOG_ITEM_NOT_PUBLISHED";

    // bcm-att-001-appointment-scheduling
    public static final String APPOINTMENT_BRANCH_NOT_ACTIVE = "APPOINTMENT_BRANCH_NOT_ACTIVE";
    public static final String APPOINTMENT_WINDOW_OVERLAP = "APPOINTMENT_WINDOW_OVERLAP";
    public static final String APPOINTMENT_BRANCH_CAPACITY_EXCEEDED = "APPOINTMENT_BRANCH_CAPACITY_EXCEEDED";
    public static final String APPOINTMENT_CATALOG_ITEM_NOT_PUBLISHED = "APPOINTMENT_CATALOG_ITEM_NOT_PUBLISHED";
    public static final String APPOINTMENT_NO_SHOW_GRACE_PERIOD_ACTIVE = "APPOINTMENT_NO_SHOW_GRACE_PERIOD_ACTIVE";

    // bcm-att-003-reception-management
    public static final String RECEPTION_IDENTITY_NOT_CONFIRMED = "RECEPTION_IDENTITY_NOT_CONFIRMED";
    public static final String RECEPTION_APPOINTMENT_NOT_CHECKED_IN = "RECEPTION_APPOINTMENT_NOT_CHECKED_IN";

    // bcm-att-004-admission-management
    public static final String ADMISSION_IDENTITY_NOT_CONFIRMED = "ADMISSION_IDENTITY_NOT_CONFIRMED";
    public static final String ADMISSION_CATALOG_INCOMPLETE = "ADMISSION_CATALOG_INCOMPLETE";
    public static final String ADMISSION_CONSENT_OR_SAMPLE_ACK_MISSING = "ADMISSION_CONSENT_OR_SAMPLE_ACK_MISSING";

    // bcm-att-006-quotation-management
    public static final String QUOTATION_CATALOG_ITEM_NOT_PUBLISHED = "QUOTATION_CATALOG_ITEM_NOT_PUBLISHED";
    public static final String QUOTATION_PRICING_SNAPSHOT_REQUIRED = "QUOTATION_PRICING_SNAPSHOT_REQUIRED";
    public static final String QUOTATION_DISCOUNT_POLICY_EXCEEDED = "QUOTATION_DISCOUNT_POLICY_EXCEEDED";
    public static final String QUOTATION_EXPIRED = "QUOTATION_EXPIRED";
    public static final String QUOTATION_TERMINAL_STATE_IMMUTABLE = "QUOTATION_TERMINAL_STATE_IMMUTABLE";
}
