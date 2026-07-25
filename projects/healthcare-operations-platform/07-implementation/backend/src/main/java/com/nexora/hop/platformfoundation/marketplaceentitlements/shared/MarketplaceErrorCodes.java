package com.nexora.hop.platformfoundation.marketplaceentitlements.shared;

/**
 * Canonical structured error codes for BCM-PLT-011 Product Marketplace and Entitlements, matching
 * {@code openapi-source.md error_model.standard_errors} plus the additional not-found/conflict
 * codes every capability needs, mirroring {@code IntegrationErrorCodes}.
 */
public final class MarketplaceErrorCodes {

    // openapi-source.md error_model.standard_errors
    public static final String PACKAGE_NOT_FOUND = "PACKAGE_NOT_FOUND";
    public static final String OFFER_NOT_AVAILABLE = "OFFER_NOT_AVAILABLE";
    public static final String ENTITLEMENT_REQUIRED = "ENTITLEMENT_REQUIRED";
    public static final String ENTITLEMENT_EXPIRED = "ENTITLEMENT_EXPIRED";
    public static final String COMPATIBILITY_FAILED = "COMPATIBILITY_FAILED";
    public static final String INSTALLATION_CONFLICT = "INSTALLATION_CONFLICT";
    public static final String ROLLBACK_NOT_AVAILABLE = "ROLLBACK_NOT_AVAILABLE";
    public static final String PROVIDER_ADAPTER_UNAVAILABLE = "PROVIDER_ADAPTER_UNAVAILABLE";

    // Additional not-found/validation/conflict codes needed by generated outputs
    public static final String MARKETPLACE_COMMAND_INVALID = "MARKETPLACE_COMMAND_INVALID";
    public static final String TENANT_NOT_FOUND = "TENANT_NOT_FOUND";
    public static final String PACKAGE_VERSION_NOT_FOUND = "PACKAGE_VERSION_NOT_FOUND";
    public static final String PACKAGE_VERSION_CONFLICT = "PACKAGE_VERSION_CONFLICT";
    public static final String PACKAGE_CAPABILITY_MAPPING_REQUIRED = "PACKAGE_CAPABILITY_MAPPING_REQUIRED";
    public static final String OFFER_NOT_FOUND = "OFFER_NOT_FOUND";
    public static final String ENTITLEMENT_NOT_FOUND = "ENTITLEMENT_NOT_FOUND";
    public static final String INSTALLATION_NOT_FOUND = "INSTALLATION_NOT_FOUND";

    // COM-MOD-017-BE-002: full entitlement-policy.md evaluation_order decision_outputs
    public static final String TENANT_NOT_ACTIVE = "TENANT_NOT_ACTIVE";
    public static final String PACKAGE_SUSPENDED = "PACKAGE_SUSPENDED";
    public static final String PERMISSION_REQUIRED = "PERMISSION_REQUIRED";
    public static final String FEATURE_NOT_ENABLED = "FEATURE_NOT_ENABLED";
    public static final String CLINICAL_SAFETY_ACKNOWLEDGEMENT_REQUIRED = "CLINICAL_SAFETY_ACKNOWLEDGEMENT_REQUIRED";
    public static final String USAGE_LIMIT_EXCEEDED = "USAGE_LIMIT_EXCEEDED";

    private MarketplaceErrorCodes() {
    }
}
