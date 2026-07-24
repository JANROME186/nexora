package com.nexora.hop.platformfoundation.marketplaceentitlements.shared;

/**
 * Canonical structured error codes for BCM-PLT-011 Product Marketplace and Entitlements, matching
 * {@code openapi-source.yaml error_model.standard_errors} plus the additional not-found/conflict
 * codes every capability needs, mirroring {@code IntegrationErrorCodes}.
 */
public final class MarketplaceErrorCodes {

    // openapi-source.yaml error_model.standard_errors
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

    private MarketplaceErrorCodes() {
    }
}
