package com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.InvalidMarketplaceCommandException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceConflictException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceEntityNotFoundException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceErrorCodes;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlementRepository;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

class TenantEntitlementServiceTest {

    private TenantEntitlementRepository repository;
    private TenantDirectory tenantDirectory;
    private TenantEntitlementService service;

    @BeforeEach
    void setUp() {
        repository = mock(TenantEntitlementRepository.class);
        tenantDirectory = mock(TenantDirectory.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        service = new TenantEntitlementService(repository, tenantDirectory, mock(AuditRecorder.class), clock);
    }

    @Test
    void grantEntitlementRejectsUnknownTenant() {
        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(false);
        MarketplaceEntityNotFoundException exception = assertThrows(MarketplaceEntityNotFoundException.class,
                () -> service.grantEntitlement("tenant-1", "pkg-1", null, null, "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.TENANT_NOT_FOUND);
    }

    @Test
    void grantEntitlementCreatesActiveEntitlement() {
        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(true);
        TenantEntitlement granted = service.grantEntitlement("tenant-1", "pkg-1", "offer-1", null, "operator-1");
        assertThat(granted.status()).isEqualTo(TenantEntitlement.STATUS_ACTIVE);
        assertThat(granted.offerId()).isEqualTo("offer-1");
        assertThat(granted.usageLimit()).isNull();
    }

    @Test
    void grantEntitlementAcceptsAnOptionalUsageLimit() {
        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(true);
        TenantEntitlement granted = service.grantEntitlement("tenant-1", "pkg-1", "offer-1", null, 100, "operator-1");
        assertThat(granted.usageLimit()).isEqualTo(100);
    }

    @Test
    void grantEntitlementRejectsNegativeUsageLimit() {
        when(tenantDirectory.tenantExists("tenant-1")).thenReturn(true);
        InvalidMarketplaceCommandException exception = assertThrows(InvalidMarketplaceCommandException.class,
                () -> service.grantEntitlement("tenant-1", "pkg-1", "offer-1", null, -1, "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.MARKETPLACE_COMMAND_INVALID);
    }

    @Test
    void revokeEntitlementRejectsCrossTenantAccess() {
        when(repository.findById("ent-1")).thenReturn(Optional.of(fixtureEntitlement("tenant-1")));
        MarketplaceEntityNotFoundException exception = assertThrows(MarketplaceEntityNotFoundException.class,
                () -> service.revokeEntitlement("tenant-other", "ent-1", "no longer needed", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.ENTITLEMENT_NOT_FOUND);
    }

    @Test
    void revokeEntitlementRejectsAlreadyRevoked() {
        TenantEntitlement revoked = new TenantEntitlement(
                "ent-1", "tenant-1", "pkg-1", null, TenantEntitlement.STATUS_REVOKED, LocalDateTime.now(), null,
                "prior reason", null, fixtureAudit());
        when(repository.findById("ent-1")).thenReturn(Optional.of(revoked));
        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class,
                () -> service.revokeEntitlement("tenant-1", "ent-1", "again", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.ENTITLEMENT_NOT_FOUND);
    }

    @Test
    void revokeEntitlementSucceedsForActiveEntitlement() {
        when(repository.findById("ent-1")).thenReturn(Optional.of(fixtureEntitlement("tenant-1")));
        TenantEntitlement revoked = service.revokeEntitlement("tenant-1", "ent-1", "no longer needed", "operator-1");
        assertThat(revoked.status()).isEqualTo(TenantEntitlement.STATUS_REVOKED);
        assertThat(revoked.revokedReason()).isEqualTo("no longer needed");
    }

    private TenantEntitlement fixtureEntitlement(String tenantId) {
        return new TenantEntitlement(
                "ent-1", tenantId, "pkg-1", null, TenantEntitlement.STATUS_ACTIVE, LocalDateTime.now(), null, null,
                null, fixtureAudit());
    }

    private AuditMetadata fixtureAudit() {
        return new AuditMetadata("operator-1", LocalDateTime.now(), "operator-1", LocalDateTime.now());
    }
}
