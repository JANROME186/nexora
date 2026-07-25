package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Unit coverage for RN-MKT-002/RN-MKT-005's centralized entitlement gate and the full
 * entitlement-policy.md {@code evaluation_order} (COM-MOD-017-BE-002, TD-BE-018).
 */
class EntitlementPolicyEvaluatorTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    private TenantEntitlementRepository entitlementRepository;
    private TenantDirectory tenantDirectory;
    private MarketplacePackageRepository packageRepository;
    private CompatibilityEvaluator compatibilityEvaluator;
    private EntitlementPolicyEvaluator evaluator;

    @BeforeEach
    void setUp() {
        entitlementRepository = mock(TenantEntitlementRepository.class);
        tenantDirectory = mock(TenantDirectory.class);
        packageRepository = mock(MarketplacePackageRepository.class);
        compatibilityEvaluator = mock(CompatibilityEvaluator.class);
        evaluator = new EntitlementPolicyEvaluator(
                entitlementRepository, tenantDirectory, packageRepository, compatibilityEvaluator, CLOCK);

        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(true);
        when(tenantDirectory.tenantStatus("tenant-1")).thenReturn(Optional.of(TenantLifecycle.STATUS_ACTIVE));
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(marketplacePackage(MarketplacePackage.STATUS_PUBLISHED)));
    }

    @Test
    void allowsWhenAnActiveNonExpiredEntitlementExists() {
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, null, null)));

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void deniesMissingEntitlementWhenNoneExist() {
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1")).thenReturn(List.of());

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_MISSING_ENTITLEMENT);
    }

    @Test
    void deniesExpiredWhenEntitlementExpirationHasPassed() {
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, LocalDateTime.now(CLOCK).minusDays(1), null)));

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_EXPIRED);
    }

    @Test
    void deniesExpiredWhenOnlyRevokedEntitlementsExist() {
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_REVOKED, null, null)));

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_EXPIRED);
    }

    @Test
    void deniesTenantStatusWhenTenantDoesNotExist() {
        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(false);

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_TENANT_INACTIVE);
    }

    @Test
    void deniesTenantStatusWhenTenantIsSuspended() {
        when(tenantDirectory.tenantStatus("tenant-1")).thenReturn(Optional.of(TenantLifecycle.STATUS_SUSPENDED));

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_TENANT_INACTIVE);
    }

    @Test
    void deniesTenantStatusWhenTenantIsArchived() {
        when(tenantDirectory.tenantStatus("tenant-1")).thenReturn(Optional.of(TenantLifecycle.STATUS_ARCHIVED));

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_TENANT_INACTIVE);
    }

    @Test
    void allowsTenantStatusWhenTenantIsPendingProvisioning() {
        when(tenantDirectory.tenantStatus("tenant-1")).thenReturn(Optional.of(TenantLifecycle.STATUS_PENDING_PROVISIONING));
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, null, null)));

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void deniesPackageStatusWhenPackageIsRetired() {
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(marketplacePackage(MarketplacePackage.STATUS_RETIRED)));

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_SUSPENDED_PACKAGE);
    }

    @Test
    void deniesPackageStatusWhenPackageIsUnknown() {
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.empty());

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_SUSPENDED_PACKAGE);
    }

    @Test
    void deniesCompatibilityStatusWhenRequestedVersionIsIncompatible() {
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, null, null)));
        when(compatibilityEvaluator.evaluate("pkg-1", "2.0.0")).thenReturn(
                new CompatibilityDecision(CompatibilityDecision.DECISION_INCOMPATIBLE, "block_installation", "major mismatch"));

        EntitlementDecision decision = evaluator.evaluate(
                new EntitlementEvaluationRequest("tenant-1", "pkg-1", "2.0.0", null, null, false, null));
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_INCOMPATIBLE_VERSION);
    }

    @Test
    void deniesIamPermissionWhenPermissionGrantedIsFalse() {
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, null, null)));

        EntitlementDecision decision = evaluator.evaluate(
                new EntitlementEvaluationRequest("tenant-1", "pkg-1", null, false, null, false, null));
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_MISSING_PERMISSION);
    }

    @Test
    void deniesFeatureFlagWhenFeatureFlagEnabledIsFalse() {
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, null, null)));

        EntitlementDecision decision = evaluator.evaluate(
                new EntitlementEvaluationRequest("tenant-1", "pkg-1", null, null, false, false, null));
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_FEATURE_DISABLED);
    }

    @Test
    void deniesClinicalSafetyAcknowledgementWhenPackageIsClinicalSafetyCriticalAndUnacknowledged() {
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(
                new MarketplacePackage("pkg-1", "code-1", "name", "category", List.of("BCM-LAB-002"),
                        MarketplacePackage.STATUS_PUBLISHED, fixtureAudit())));
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, null, null)));

        EntitlementDecision decision = evaluator.evaluate(
                new EntitlementEvaluationRequest("tenant-1", "pkg-1", null, null, null, false, null));
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_CLINICAL_SAFETY_ACKNOWLEDGEMENT_REQUIRED);
    }

    @Test
    void allowsClinicalSafetyCriticalPackageWhenAcknowledged() {
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(
                new MarketplacePackage("pkg-1", "code-1", "name", "category", List.of("BCM-RES-006"),
                        MarketplacePackage.STATUS_PUBLISHED, fixtureAudit())));
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, null, null)));

        EntitlementDecision decision = evaluator.evaluate(
                new EntitlementEvaluationRequest("tenant-1", "pkg-1", null, null, null, true, null));
        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void deniesUsageLimitWhenRequestedUsageExceedsEntitlementLimit() {
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, null, 10)));

        EntitlementDecision decision = evaluator.evaluate(
                new EntitlementEvaluationRequest("tenant-1", "pkg-1", null, null, null, false, 11));
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_USAGE_LIMIT);
    }

    @Test
    void allowsUsageWithinEntitlementLimit() {
        when(entitlementRepository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, null, 10)));

        EntitlementDecision decision = evaluator.evaluate(
                new EntitlementEvaluationRequest("tenant-1", "pkg-1", null, null, null, false, 10));
        assertThat(decision.allowed()).isTrue();
    }

    private TenantEntitlement entitlement(String status, LocalDateTime expiresAt, Integer usageLimit) {
        return new TenantEntitlement("ent-1", "tenant-1", "pkg-1", null, status, LocalDateTime.now(CLOCK), expiresAt,
                null, usageLimit, fixtureAudit());
    }

    private MarketplacePackage marketplacePackage(String status) {
        return new MarketplacePackage("pkg-1", "code-1", "name", "category", List.of("BCM-PLT-011"), status, fixtureAudit());
    }

    private AuditMetadata fixtureAudit() {
        return new AuditMetadata("actor", LocalDateTime.now(CLOCK), "actor", LocalDateTime.now(CLOCK));
    }
}
