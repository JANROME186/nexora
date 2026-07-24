package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.EntitlementDecision;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlementRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/** Unit coverage for RN-MKT-002/RN-MKT-005's centralized entitlement gate. */
class EntitlementPolicyEvaluatorTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);

    @Test
    void allowsWhenAnActiveNonExpiredEntitlementExists() {
        TenantEntitlementRepository repository = mock(TenantEntitlementRepository.class);
        when(repository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE, null)));
        EntitlementPolicyEvaluator evaluator = new EntitlementPolicyEvaluator(repository, CLOCK);

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.allowed()).isTrue();
    }

    @Test
    void deniesMissingEntitlementWhenNoneExist() {
        TenantEntitlementRepository repository = mock(TenantEntitlementRepository.class);
        when(repository.findByTenantIdAndPackageId("tenant-1", "pkg-1")).thenReturn(List.of());
        EntitlementPolicyEvaluator evaluator = new EntitlementPolicyEvaluator(repository, CLOCK);

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_MISSING_ENTITLEMENT);
    }

    @Test
    void deniesExpiredWhenEntitlementExpirationHasPassed() {
        TenantEntitlementRepository repository = mock(TenantEntitlementRepository.class);
        when(repository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_ACTIVE,
                        LocalDateTime.now(CLOCK).minusDays(1))));
        EntitlementPolicyEvaluator evaluator = new EntitlementPolicyEvaluator(repository, CLOCK);

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_EXPIRED);
    }

    @Test
    void deniesExpiredWhenOnlyRevokedEntitlementsExist() {
        TenantEntitlementRepository repository = mock(TenantEntitlementRepository.class);
        when(repository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(entitlement(TenantEntitlement.STATUS_REVOKED, null)));
        EntitlementPolicyEvaluator evaluator = new EntitlementPolicyEvaluator(repository, CLOCK);

        EntitlementDecision decision = evaluator.evaluate("tenant-1", "pkg-1");
        assertThat(decision.allowed()).isFalse();
        assertThat(decision.decision()).isEqualTo(EntitlementDecision.DENIED_EXPIRED);
    }

    private TenantEntitlement entitlement(String status, LocalDateTime expiresAt) {
        AuditMetadata audit = new AuditMetadata("actor", LocalDateTime.now(CLOCK), "actor", LocalDateTime.now(CLOCK));
        return new TenantEntitlement("ent-1", "tenant-1", "pkg-1", null, status, LocalDateTime.now(CLOCK), expiresAt,
                null, audit);
    }
}
