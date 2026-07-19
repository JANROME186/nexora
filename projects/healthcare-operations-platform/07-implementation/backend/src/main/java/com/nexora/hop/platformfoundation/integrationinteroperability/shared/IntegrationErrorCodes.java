package com.nexora.hop.platformfoundation.integrationinteroperability.shared;

/**
 * Canonical structured error codes for BCM-PLT-004 Integration Management and BCM-PLT-005 API
 * Management, matching each capability's {@code openapi-source.yaml} {@code error_model
 * .domain_errors} exactly. First HOP capability set to carry a first-class {@code code} field on
 * every error response (TD-I18N-002 trigger).
 */
public final class IntegrationErrorCodes {

    // BCM-PLT-004 Integration Management (RN-001..RN-006)
    public static final String INTEGRATION_RAW_PAYLOAD_BYPASS_ATTEMPTED =
            "INTEGRATION_RAW_PAYLOAD_BYPASS_ATTEMPTED";
    public static final String INTEGRATION_NORMALIZATION_FAILED = "INTEGRATION_NORMALIZATION_FAILED";
    public static final String INTEGRATION_DUPLICATE_MESSAGE_ID = "INTEGRATION_DUPLICATE_MESSAGE_ID";
    public static final String INTEGRATION_RETRY_LIMIT_EXCEEDED = "INTEGRATION_RETRY_LIMIT_EXCEEDED";
    public static final String INTEGRATION_SCOPE_MISMATCH = "INTEGRATION_SCOPE_MISMATCH";
    public static final String INTEGRATION_MESSAGE_DEAD_LETTERED = "INTEGRATION_MESSAGE_DEAD_LETTERED";
    public static final String INTEGRATION_RETRY_NOT_YET_DUE = "INTEGRATION_RETRY_NOT_YET_DUE";

    // BCM-PLT-005 API Management (RN-001..RN-006)
    public static final String API_OPERATION_NOT_CLASSIFIED = "API_OPERATION_NOT_CLASSIFIED";
    public static final String API_PARTNER_KEY_INVALID_OR_SCOPE_MISMATCH =
            "API_PARTNER_KEY_INVALID_OR_SCOPE_MISMATCH";
    public static final String API_DEPRECATION_WINDOW_MISSING = "API_DEPRECATION_WINDOW_MISSING";
    public static final String API_RATE_LIMIT_EXCEEDED = "API_RATE_LIMIT_EXCEEDED";
    public static final String API_MANAGEMENT_SCOPE_MISMATCH = "API_MANAGEMENT_SCOPE_MISMATCH";
    public static final String API_DEPRECATION_WINDOW_NOT_ELAPSED = "API_DEPRECATION_WINDOW_NOT_ELAPSED";

    private IntegrationErrorCodes() {
    }
}
