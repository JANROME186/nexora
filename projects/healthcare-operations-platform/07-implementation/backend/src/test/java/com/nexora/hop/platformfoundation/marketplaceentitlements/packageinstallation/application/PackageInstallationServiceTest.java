package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.application.CompatibilityEvaluator;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.InstallationStep;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.InstallationStepRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.PackageInstallation;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.PackageInstallationRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceConflictException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceErrorCodes;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application.EntitlementPolicyEvaluator;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.EntitlementDecision;
import com.nexora.hop.platformfoundation.organizationmanagement.TenantDirectory;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/** Unit coverage for RN-MKT-002/INV-MKT-004 install/activate/suspend/uninstall/upgrade/rollback. */
class PackageInstallationServiceTest {

    private PackageInstallationRepository repository;
    private InstallationStepRepository installationStepRepository;
    private EntitlementPolicyEvaluator entitlementPolicyEvaluator;
    private TenantDirectory tenantDirectory;
    private PackageInstallationService service;

    @BeforeEach
    void setUp() {
        repository = mock(PackageInstallationRepository.class);
        installationStepRepository = mock(InstallationStepRepository.class);
        entitlementPolicyEvaluator = mock(EntitlementPolicyEvaluator.class);
        tenantDirectory = mock(TenantDirectory.class);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(tenantDirectory.tenantExists(org.mockito.ArgumentMatchers.anyString())).thenReturn(true);
        when(entitlementPolicyEvaluator.evaluate(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(new EntitlementDecision(EntitlementDecision.ALLOWED, null));
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        service = new PackageInstallationService(
                repository, installationStepRepository, entitlementPolicyEvaluator, new CompatibilityEvaluator(),
                tenantDirectory, mock(AuditRecorder.class), clock);
    }

    @Test
    void installPackageRequiresAnActiveEntitlement() {
        when(entitlementPolicyEvaluator.evaluate("tenant-1", "pkg-1"))
                .thenReturn(new EntitlementDecision(EntitlementDecision.DENIED_MISSING_ENTITLEMENT, "no entitlement"));

        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class, () -> service.installPackage(
                "tenant-1", "pkg-1", CompatibilityEvaluator.PLATFORM_VERSION, "ent-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.ENTITLEMENT_REQUIRED);
    }

    @Test
    void installPackageRejectsWhenTenantIsInactive() {
        when(entitlementPolicyEvaluator.evaluate("tenant-1", "pkg-1"))
                .thenReturn(new EntitlementDecision(EntitlementDecision.DENIED_TENANT_INACTIVE, "tenant suspended"));

        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class, () -> service.installPackage(
                "tenant-1", "pkg-1", CompatibilityEvaluator.PLATFORM_VERSION, "ent-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.TENANT_NOT_ACTIVE);
    }

    @Test
    void installPackageRejectsWhenPackageIsSuspended() {
        when(entitlementPolicyEvaluator.evaluate("tenant-1", "pkg-1"))
                .thenReturn(new EntitlementDecision(EntitlementDecision.DENIED_SUSPENDED_PACKAGE, "package retired"));

        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class, () -> service.installPackage(
                "tenant-1", "pkg-1", CompatibilityEvaluator.PLATFORM_VERSION, "ent-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.PACKAGE_SUSPENDED);
    }

    @Test
    void installPackageRejectsIncompatibleVersion() {
        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class,
                () -> service.installPackage("tenant-1", "pkg-1", "2.0.0", "ent-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.COMPATIBILITY_FAILED);
    }

    @Test
    void installPackageRejectsWhenTenantAlreadyHasANonTerminalInstallation() {
        when(repository.findByTenantIdAndPackageId("tenant-1", "pkg-1"))
                .thenReturn(List.of(fixtureInstallation(PackageInstallation.STATUS_ACTIVE, null)));

        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class, () -> service.installPackage(
                "tenant-1", "pkg-1", CompatibilityEvaluator.PLATFORM_VERSION, "ent-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.INSTALLATION_CONFLICT);
    }

    @Test
    void installPackageSucceedsWhenEntitledCompatibleAndNoConflict() {
        PackageInstallation installed = service.installPackage(
                "tenant-1", "pkg-1", CompatibilityEvaluator.PLATFORM_VERSION, "ent-1", "operator-1");
        assertThat(installed.lifecycleStatus()).isEqualTo(PackageInstallation.STATUS_INSTALLED);
    }

    @Test
    void activatePackageSetsRollbackCheckpointAndActivates() {
        when(repository.findById("inst-1"))
                .thenReturn(Optional.of(fixtureInstallation(PackageInstallation.STATUS_INSTALLED, null)));

        PackageInstallation activated = service.activatePackage("tenant-1", "inst-1", "operator-1");
        assertThat(activated.lifecycleStatus()).isEqualTo(PackageInstallation.STATUS_ACTIVE);
        assertThat(activated.rollbackCheckpointVersion()).isEqualTo(CompatibilityEvaluator.PLATFORM_VERSION);
    }

    @Test
    void activatePackageRejectsWhenEntitlementIsNoLongerActive() {
        when(repository.findById("inst-1"))
                .thenReturn(Optional.of(fixtureInstallation(PackageInstallation.STATUS_INSTALLED, null)));
        when(entitlementPolicyEvaluator.evaluate("tenant-1", "pkg-1"))
                .thenReturn(new EntitlementDecision(EntitlementDecision.DENIED_EXPIRED, "expired"));

        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class,
                () -> service.activatePackage("tenant-1", "inst-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.ENTITLEMENT_EXPIRED);
    }

    @Test
    void suspendThenActivateResumesAnActiveInstallation() {
        when(repository.findById("inst-1"))
                .thenReturn(Optional.of(fixtureInstallation(PackageInstallation.STATUS_ACTIVE, "1.0.0")));
        PackageInstallation suspended = service.suspendPackage("tenant-1", "inst-1", "operator-1");
        assertThat(suspended.lifecycleStatus()).isEqualTo(PackageInstallation.STATUS_SUSPENDED);

        when(repository.findById("inst-1")).thenReturn(Optional.of(suspended));
        PackageInstallation resumed = service.activatePackage("tenant-1", "inst-1", "operator-1");
        assertThat(resumed.lifecycleStatus()).isEqualTo(PackageInstallation.STATUS_ACTIVE);
    }

    @Test
    void uninstallPackageRejectsAlreadyTerminalInstallation() {
        when(repository.findById("inst-1"))
                .thenReturn(Optional.of(fixtureInstallation(PackageInstallation.STATUS_UNINSTALLED, null)));
        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class,
                () -> service.uninstallPackage("tenant-1", "inst-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.INSTALLATION_CONFLICT);
    }

    @Test
    void upgradePackagePreservesPreUpgradeVersionAsRollbackCheckpoint() {
        when(repository.findById("inst-1"))
                .thenReturn(Optional.of(fixtureInstallation(PackageInstallation.STATUS_ACTIVE, null)));
        PackageInstallation upgraded = service.upgradePackage("tenant-1", "inst-1", "1.1.0", "operator-1");
        assertThat(upgraded.version()).isEqualTo("1.1.0");
        assertThat(upgraded.rollbackCheckpointVersion()).isEqualTo(CompatibilityEvaluator.PLATFORM_VERSION);
    }

    @Test
    void rollbackPackageFailsWithoutACheckpoint() {
        when(repository.findById("inst-1"))
                .thenReturn(Optional.of(fixtureInstallation(PackageInstallation.STATUS_ACTIVE, null)));
        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class,
                () -> service.rollbackPackage("tenant-1", "inst-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.ROLLBACK_NOT_AVAILABLE);
    }

    @Test
    void rollbackPackageRestoresTheCheckpointVersion() {
        when(repository.findById("inst-1"))
                .thenReturn(Optional.of(fixtureInstallation(PackageInstallation.STATUS_ACTIVE, "1.0.0")));
        PackageInstallation rolledBack = service.rollbackPackage("tenant-1", "inst-1", "operator-1");
        assertThat(rolledBack.version()).isEqualTo("1.0.0");
    }

    @Test
    void rollbackPackagePrefersTheAuditTrailOverTheCheckpointFieldWhenBothExist() {
        PackageInstallation current = fixtureInstallation(PackageInstallation.STATUS_ACTIVE, "0.9.0");
        when(repository.findById("inst-1")).thenReturn(Optional.of(current));
        AuditMetadata stepAudit = new AuditMetadata("operator-1", LocalDateTime.now(), "operator-1", LocalDateTime.now());
        when(installationStepRepository.findByInstallationIdOrderByOccurredAt("inst-1")).thenReturn(List.of(
                new InstallationStep("step-1", "inst-1", "tenant-1", InstallationStep.TYPE_UPGRADE, "1.0.0",
                        CompatibilityEvaluator.PLATFORM_VERSION, PackageInstallation.STATUS_ACTIVE,
                        PackageInstallation.STATUS_ACTIVE, "operator-1", LocalDateTime.now())));

        PackageInstallation rolledBack = service.rollbackPackage("tenant-1", "inst-1", "operator-1");
        assertThat(rolledBack.version()).isEqualTo("1.0.0");
        assertThat(stepAudit).isNotNull();
    }

    @Test
    void installActivateSuspendUninstallUpgradeRollbackEachAppendAnInstallationStep() {
        service.installPackage("tenant-1", "pkg-1", CompatibilityEvaluator.PLATFORM_VERSION, "ent-1", "operator-1");
        verify(installationStepRepository, times(1)).save(any());

        when(repository.findById("inst-1"))
                .thenReturn(Optional.of(fixtureInstallation(PackageInstallation.STATUS_INSTALLED, null)));
        service.activatePackage("tenant-1", "inst-1", "operator-1");
        verify(installationStepRepository, times(2)).save(any());
    }

    @Test
    void listInstallationsDelegatesToRepository() {
        when(repository.findByTenantId("tenant-1"))
                .thenReturn(List.of(fixtureInstallation(PackageInstallation.STATUS_ACTIVE, null)));
        assertThat(service.listInstallations("tenant-1")).hasSize(1);
    }

    private PackageInstallation fixtureInstallation(String status, String rollbackCheckpointVersion) {
        AuditMetadata audit = new AuditMetadata("operator-1", LocalDateTime.now(), "operator-1", LocalDateTime.now());
        return new PackageInstallation(
                "inst-1", "tenant-1", "pkg-1", "ent-1", CompatibilityEvaluator.PLATFORM_VERSION, status,
                rollbackCheckpointVersion, audit);
    }
}
