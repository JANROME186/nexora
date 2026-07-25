package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain;

/**
 * Outcome of an entitlement policy evaluation (entitlement-policy.md {@code decision_outputs}).
 * COM-MOD-017-BE-002 adds the remaining decision outputs beyond {@code allowed}/{@code
 * denied_missing_entitlement}/{@code denied_expired} so every {@code evaluation_order} step named
 * by entitlement-policy.md has a matching outcome.
 */
public record EntitlementDecision(String decision, String reason) {

    public static final String ALLOWED = "allowed";
    public static final String DENIED_MISSING_ENTITLEMENT = "denied_missing_entitlement";
    public static final String DENIED_EXPIRED = "denied_expired";
    public static final String DENIED_TENANT_INACTIVE = "denied_tenant_inactive";
    public static final String DENIED_SUSPENDED_PACKAGE = "denied_suspended_package";
    public static final String DENIED_INCOMPATIBLE_VERSION = "denied_incompatible_version";
    public static final String DENIED_MISSING_PERMISSION = "denied_missing_permission";
    public static final String DENIED_FEATURE_DISABLED = "denied_feature_disabled";
    public static final String DENIED_CLINICAL_SAFETY_ACKNOWLEDGEMENT_REQUIRED =
            "denied_clinical_safety_acknowledgement_required";
    public static final String DENIED_USAGE_LIMIT = "denied_usage_limit";

    public boolean allowed() {
        return ALLOWED.equals(decision);
    }
}
