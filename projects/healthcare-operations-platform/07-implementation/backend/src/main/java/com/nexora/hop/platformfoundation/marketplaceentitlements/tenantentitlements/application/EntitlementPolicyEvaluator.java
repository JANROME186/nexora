package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.EntitlementDecision;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlementRepository;

/**
 * Central entitlement policy evaluator (RN-MKT-005: entitlement checks must be centralized and
 * cannot be hard-coded in controllers, UI components or mobile views). Compiles the
 * {@code tenant_status}-adjacent {@code entitlement} step of entitlement-policy.md's
 * {@code evaluation_order}: an active, non-expired {@link TenantEntitlement} for
 * {@code (tenantId, packageId)} is required (RN-MKT-002, INV-MKT-002). The remaining
 * evaluation_order steps (package_status, license_status, compatibility_status beyond
 * installation-time evaluation, iam_permission, feature_flag, clinical_safety_control,
 * usage_limit) are the explicit custom_implementation_point deferred to a future BE-002
 * (TD-BE-018); this evaluator's basic entitlement-existence gate must never be bypassed by any
 * caller.
 */
@Service
public class EntitlementPolicyEvaluator {

    private final TenantEntitlementRepository entitlementRepository;
    private final Clock clock;

    @Autowired
    public EntitlementPolicyEvaluator(TenantEntitlementRepository entitlementRepository) {
        this(entitlementRepository, Clock.systemUTC());
    }

    EntitlementPolicyEvaluator(TenantEntitlementRepository entitlementRepository, Clock clock) {
        this.entitlementRepository = entitlementRepository;
        this.clock = clock;
    }

    public EntitlementDecision evaluate(String tenantId, String packageId) {
        List<TenantEntitlement> candidates = entitlementRepository.findByTenantIdAndPackageId(tenantId, packageId);
        LocalDateTime now = LocalDateTime.now(clock);
        boolean hasAnyEntitlement = !candidates.isEmpty();
        boolean hasActive = candidates.stream().anyMatch(candidate -> candidate.isActive(now));
        if (hasActive) {
            return new EntitlementDecision(EntitlementDecision.ALLOWED, null);
        }
        if (hasAnyEntitlement) {
            return new EntitlementDecision(
                    EntitlementDecision.DENIED_EXPIRED,
                    "Tenant " + tenantId + " has no active (non-expired, non-revoked) entitlement for package "
                            + packageId + ".");
        }
        return new EntitlementDecision(
                EntitlementDecision.DENIED_MISSING_ENTITLEMENT,
                "Tenant " + tenantId + " has no entitlement granted for package " + packageId + ".");
    }
}
