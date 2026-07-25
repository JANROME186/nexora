package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain;

/**
 * Input to {@code EntitlementPolicyEvaluator.evaluate}, carrying the optional context each
 * entitlement-policy.md {@code evaluation_order} step beyond the mandatory {@code tenant_status}/
 * {@code package_status}/{@code license_status} steps needs. A field left {@code null} (or, for
 * {@code clinicalSafetyAcknowledged}, {@code false}) means that step's caller-supplied context was
 * not applicable to the current command and the corresponding gate is skipped.
 *
 * <p>{@code permissionGranted} and {@code featureFlagEnabled} are pre-resolved facts, not lookup
 * keys: {@code EntitlementPolicyEvaluator} is the policy decision point (RN-MKT-005 centralizes
 * the decision), while resolving a role's IAM permission or a tenant's feature-flag state is a
 * policy information point concern that belongs to whichever caller already has access to
 * identityaccess/platformconfiguration (this module does not depend on either, by design, to keep
 * marketplaceentitlements's Spring Modulith dependency graph acyclic).
 */
public record EntitlementEvaluationRequest(
        String tenantId,
        String packageId,
        String requestedVersion,
        Boolean permissionGranted,
        Boolean featureFlagEnabled,
        boolean clinicalSafetyAcknowledged,
        Integer requestedUsage) {

    /** A request evaluating only the mandatory tenant_status/package_status/license_status steps. */
    public static EntitlementEvaluationRequest basic(String tenantId, String packageId) {
        return new EntitlementEvaluationRequest(tenantId, packageId, null, null, null, false, null);
    }
}
