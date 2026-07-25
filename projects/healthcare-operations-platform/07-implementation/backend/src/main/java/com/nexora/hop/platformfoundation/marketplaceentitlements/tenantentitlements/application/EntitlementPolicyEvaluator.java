package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.application.CompatibilityEvaluator;
import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain.CompatibilityDecision;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackage;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackageRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.EntitlementDecision;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.EntitlementEvaluationRequest;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlementRepository;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.organizationmanagement.domain.TenantLifecycle;

/**
 * Central entitlement policy evaluator (RN-MKT-005: entitlement checks must be centralized and
 * cannot be hard-coded in controllers, UI components or mobile views). Implements the full
 * entitlement-policy.md {@code evaluation_order} (COM-MOD-017-BE-002, closing TD-BE-018):
 * {@code tenant_status}, {@code package_status}, {@code license_status}, {@code
 * compatibility_status}, {@code iam_permission}, {@code feature_flag}, {@code
 * clinical_safety_control} and {@code usage_limit}. Steps whose caller-supplied context in
 * {@link EntitlementEvaluationRequest} is absent (null/false) are skipped as not applicable to the
 * current command; {@link #evaluate(String, String)} evaluates only the three mandatory steps
 * that never depend on optional context (tenant_status, package_status, license_status).
 */
@Service
public class EntitlementPolicyEvaluator {

    /**
     * capabilityMappings prefixes treated as clinical-safety-critical (BCM-LAB Laboratory
     * Workflow, BCM-RES Results and Digital Delivery own critical-result/notification-trace rules,
     * per business-rules-catalog.md BRM-001-R013's {@code clinical_safety} category).
     */
    private static final Set<String> CLINICAL_SAFETY_CAPABILITY_PREFIXES = Set.of("BCM-LAB", "BCM-RES");

    /**
     * tenant_status only blocks a tenant explicitly taken out of service; {@code
     * PENDING_PROVISIONING} is a legitimate pre-activation state in which a newly onboarded tenant
     * already uses the platform (BCM-ORG-001 {@code provisionTenant} onboards tenants directly into
     * this status), so it must not be treated as inactive here.
     */
    private static final Set<String> BLOCKED_TENANT_STATUSES =
            Set.of(TenantLifecycle.STATUS_SUSPENDED, TenantLifecycle.STATUS_ARCHIVED);

    private final TenantEntitlementRepository entitlementRepository;
    private final TenantDirectory tenantDirectory;
    private final MarketplacePackageRepository packageRepository;
    private final CompatibilityEvaluator compatibilityEvaluator;
    private final Clock clock;

    @Autowired
    public EntitlementPolicyEvaluator(
            TenantEntitlementRepository entitlementRepository, TenantDirectory tenantDirectory,
            MarketplacePackageRepository packageRepository, CompatibilityEvaluator compatibilityEvaluator) {
        this(entitlementRepository, tenantDirectory, packageRepository, compatibilityEvaluator, Clock.systemUTC());
    }

    EntitlementPolicyEvaluator(
            TenantEntitlementRepository entitlementRepository, TenantDirectory tenantDirectory,
            MarketplacePackageRepository packageRepository, CompatibilityEvaluator compatibilityEvaluator,
            Clock clock) {
        this.entitlementRepository = entitlementRepository;
        this.tenantDirectory = tenantDirectory;
        this.packageRepository = packageRepository;
        this.compatibilityEvaluator = compatibilityEvaluator;
        this.clock = clock;
    }

    /** Evaluates only the mandatory tenant_status/package_status/license_status steps. */
    public EntitlementDecision evaluate(String tenantId, String packageId) {
        return evaluate(EntitlementEvaluationRequest.basic(tenantId, packageId));
    }

    /** Evaluates the full entitlement-policy.md evaluation_order for the supplied context. */
    public EntitlementDecision evaluate(EntitlementEvaluationRequest request) {
        EntitlementDecision tenantStatusDecision = evaluateTenantStatus(request.tenantId());
        if (tenantStatusDecision != null) {
            return tenantStatusDecision;
        }
        Optional<MarketplacePackage> marketplacePackage = packageRepository.findById(request.packageId());
        EntitlementDecision packageStatusDecision = evaluatePackageStatus(marketplacePackage);
        if (packageStatusDecision != null) {
            return packageStatusDecision;
        }
        LocalDateTime now = LocalDateTime.now(clock);
        List<TenantEntitlement> candidates =
                entitlementRepository.findByTenantIdAndPackageId(request.tenantId(), request.packageId());
        EntitlementDecision licenseStatusDecision = evaluateLicenseStatus(request, candidates, now);
        if (licenseStatusDecision != null) {
            return licenseStatusDecision;
        }
        TenantEntitlement activeEntitlement = candidates.stream().filter(candidate -> candidate.isActive(now))
                .findFirst().orElseThrow();

        if (request.requestedVersion() != null) {
            CompatibilityDecision decision = compatibilityEvaluator.evaluate(
                    request.packageId(), request.requestedVersion());
            if (!decision.allowsInstallation()) {
                return new EntitlementDecision(EntitlementDecision.DENIED_INCOMPATIBLE_VERSION,
                        "Package " + request.packageId() + " version " + request.requestedVersion()
                                + " failed compatibility evaluation: " + decision.decision() + ".");
            }
        }
        if (Boolean.FALSE.equals(request.permissionGranted())) {
            return new EntitlementDecision(EntitlementDecision.DENIED_MISSING_PERMISSION,
                    "Actor does not hold the permission required by package " + request.packageId() + ".");
        }
        if (Boolean.FALSE.equals(request.featureFlagEnabled())) {
            return new EntitlementDecision(EntitlementDecision.DENIED_FEATURE_DISABLED,
                    "Required feature flag is not enabled for tenant " + request.tenantId() + ".");
        }
        if (isClinicalSafetyCritical(marketplacePackage) && !request.clinicalSafetyAcknowledged()) {
            return new EntitlementDecision(EntitlementDecision.DENIED_CLINICAL_SAFETY_ACKNOWLEDGEMENT_REQUIRED,
                    "Package " + request.packageId() + " maps to a clinical-safety-critical capability and requires "
                            + "explicit clinical safety acknowledgement.");
        }
        if (activeEntitlement.usageLimit() != null && request.requestedUsage() != null
                && request.requestedUsage() > activeEntitlement.usageLimit()) {
            return new EntitlementDecision(EntitlementDecision.DENIED_USAGE_LIMIT,
                    "Requested usage " + request.requestedUsage() + " exceeds entitlement usage limit "
                            + activeEntitlement.usageLimit() + " for package " + request.packageId() + ".");
        }
        return new EntitlementDecision(EntitlementDecision.ALLOWED, null);
    }

    private EntitlementDecision evaluateTenantStatus(String tenantId) {
        if (!tenantDirectory.tenantExists(tenantId)) {
            return new EntitlementDecision(
                    EntitlementDecision.DENIED_TENANT_INACTIVE, "Tenant " + tenantId + " was not found.");
        }
        String status = tenantDirectory.tenantStatus(tenantId).orElse(null);
        if (BLOCKED_TENANT_STATUSES.contains(status)) {
            return new EntitlementDecision(EntitlementDecision.DENIED_TENANT_INACTIVE,
                    "Tenant " + tenantId + " is not active (status " + status + ").");
        }
        return null;
    }

    private EntitlementDecision evaluatePackageStatus(Optional<MarketplacePackage> marketplacePackage) {
        if (marketplacePackage.isEmpty() || MarketplacePackage.STATUS_RETIRED.equals(marketplacePackage.get().status())) {
            return new EntitlementDecision(
                    EntitlementDecision.DENIED_SUSPENDED_PACKAGE, "Marketplace package is unavailable or retired.");
        }
        return null;
    }

    private EntitlementDecision evaluateLicenseStatus(
            EntitlementEvaluationRequest request, List<TenantEntitlement> candidates, LocalDateTime now) {
        boolean hasAnyEntitlement = !candidates.isEmpty();
        boolean hasActive = candidates.stream().anyMatch(candidate -> candidate.isActive(now));
        if (hasActive) {
            return null;
        }
        if (hasAnyEntitlement) {
            return new EntitlementDecision(
                    EntitlementDecision.DENIED_EXPIRED,
                    "Tenant " + request.tenantId() + " has no active (non-expired, non-revoked) entitlement for package "
                            + request.packageId() + ".");
        }
        return new EntitlementDecision(
                EntitlementDecision.DENIED_MISSING_ENTITLEMENT,
                "Tenant " + request.tenantId() + " has no entitlement granted for package " + request.packageId() + ".");
    }

    private static boolean isClinicalSafetyCritical(Optional<MarketplacePackage> marketplacePackage) {
        return marketplacePackage
                .map(pkg -> pkg.capabilityMappings().stream()
                        .anyMatch(mapping -> CLINICAL_SAFETY_CAPABILITY_PREFIXES.stream().anyMatch(mapping::startsWith)))
                .orElse(false);
    }
}
