package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain;

/** Outcome of an entitlement policy evaluation (entitlement-policy.md {@code decision_outputs}). */
public record EntitlementDecision(String decision, String reason) {

    public static final String ALLOWED = "allowed";
    public static final String DENIED_MISSING_ENTITLEMENT = "denied_missing_entitlement";
    public static final String DENIED_EXPIRED = "denied_expired";

    public boolean allowed() {
        return ALLOWED.equals(decision);
    }
}
