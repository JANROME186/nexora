package com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackage;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackageRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersion;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersionRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.InvalidMarketplaceCommandException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceConflictException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceEntityNotFoundException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceErrorCodes;

/** Unit coverage for RN-MKT-001/RN-MKT-004/INV-MKT-001 package submission, certification and publication. */
class PackageCatalogServiceTest {

    private MarketplacePackageRepository packageRepository;
    private PackageVersionRepository versionRepository;
    private PackageCatalogService service;

    @BeforeEach
    void setUp() {
        packageRepository = mock(MarketplacePackageRepository.class);
        versionRepository = mock(PackageVersionRepository.class);
        when(packageRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(versionRepository.save(org.mockito.ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        service = new PackageCatalogService(packageRepository, versionRepository, mock(AuditRecorder.class), clock);
    }

    @Test
    void submitPackageRequiresAtLeastOneCapabilityMapping() {
        InvalidMarketplaceCommandException exception = assertThrows(InvalidMarketplaceCommandException.class,
                () -> service.submitPackage("pkg-1", "Pkg One", "platform", List.of(), "1.0.0", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.PACKAGE_CAPABILITY_MAPPING_REQUIRED);
    }

    @Test
    void submitPackageRejectsDuplicateCode() {
        when(packageRepository.findByCode("dup")).thenReturn(Optional.of(fixturePackage(MarketplacePackage.STATUS_SUBMITTED)));
        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class,
                () -> service.submitPackage("dup", "Pkg", "platform", List.of("BCM-PLT-001"), "1.0.0", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.PACKAGE_VERSION_CONFLICT);
    }

    @Test
    void submitPackageCreatesPackageAndDraftVersion() {
        MarketplacePackage created = service.submitPackage(
                "pkg-2", "Pkg Two", "platform", List.of("BCM-PLT-011"), "1.0.0", "operator-1");
        assertThat(created.status()).isEqualTo(MarketplacePackage.STATUS_SUBMITTED);
        assertThat(created.capabilityMappings()).containsExactly("BCM-PLT-011");
    }

    @Test
    void publishPackageRequiresCertifiedVersion() {
        MarketplacePackage pkg = fixturePackage(MarketplacePackage.STATUS_SUBMITTED);
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(versionRepository.findByPackageIdAndVersion("pkg-1", "1.0.0"))
                .thenReturn(Optional.of(fixtureVersion(PackageVersion.STATUS_DRAFT)));

        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class,
                () -> service.publishPackage("pkg-1", "1.0.0", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.PACKAGE_VERSION_CONFLICT);
    }

    @Test
    void publishPackageSucceedsWhenVersionIsCertified() {
        MarketplacePackage pkg = fixturePackage(MarketplacePackage.STATUS_SUBMITTED);
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(pkg));
        when(versionRepository.findByPackageIdAndVersion("pkg-1", "1.0.0"))
                .thenReturn(Optional.of(fixtureVersion(PackageVersion.STATUS_CERTIFIED)));

        MarketplacePackage published = service.publishPackage("pkg-1", "1.0.0", "operator-1");
        assertThat(published.status()).isEqualTo(MarketplacePackage.STATUS_PUBLISHED);
    }

    @Test
    void certifyPackageVersionRequiresAllFourApprovalsBeforeCertifying() {
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(fixturePackage(MarketplacePackage.STATUS_SUBMITTED)));
        when(versionRepository.findByPackageIdAndVersion("pkg-1", "1.0.0"))
                .thenReturn(Optional.of(fixtureVersion(PackageVersion.STATUS_DRAFT)));

        PackageVersion incomplete = service.certifyPackageVersion(
                "pkg-1", "1.0.0", true, true, false, true, "operator-1");
        assertThat(incomplete.lifecycleStatus()).isEqualTo(PackageVersion.STATUS_DRAFT);
        assertThat(incomplete.isReadyForPublication()).isFalse();
    }

    @Test
    void certifyPackageVersionCertifiesWhenAllApprovalsPresent() {
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(fixturePackage(MarketplacePackage.STATUS_SUBMITTED)));
        when(versionRepository.findByPackageIdAndVersion("pkg-1", "1.0.0"))
                .thenReturn(Optional.of(fixtureVersion(PackageVersion.STATUS_DRAFT)));

        PackageVersion certified = service.certifyPackageVersion(
                "pkg-1", "1.0.0", true, true, true, true, "operator-1");
        assertThat(certified.lifecycleStatus()).isEqualTo(PackageVersion.STATUS_CERTIFIED);
        assertThat(certified.isReadyForPublication()).isTrue();
    }

    @Test
    void retirePackageVersionTransitionsToRetired() {
        when(packageRepository.findById("pkg-1")).thenReturn(Optional.of(fixturePackage(MarketplacePackage.STATUS_PUBLISHED)));
        when(versionRepository.findByPackageIdAndVersion("pkg-1", "1.0.0"))
                .thenReturn(Optional.of(fixtureVersion(PackageVersion.STATUS_PUBLISHED)));

        PackageVersion retired = service.retirePackageVersion("pkg-1", "1.0.0", "operator-1");
        assertThat(retired.lifecycleStatus()).isEqualTo(PackageVersion.STATUS_RETIRED);
    }

    @Test
    void getPackageThrowsNotFoundForUnknownId() {
        when(packageRepository.findById("missing")).thenReturn(Optional.empty());
        MarketplaceEntityNotFoundException exception = assertThrows(MarketplaceEntityNotFoundException.class,
                () -> service.getPackage("missing"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.PACKAGE_NOT_FOUND);
    }

    @Test
    void getPackageVersionThrowsNotFoundForUnknownVersion() {
        when(versionRepository.findByPackageIdAndVersion("pkg-1", "9.9.9")).thenReturn(Optional.empty());
        MarketplaceEntityNotFoundException exception = assertThrows(MarketplaceEntityNotFoundException.class,
                () -> service.getPackageVersion("pkg-1", "9.9.9"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.PACKAGE_VERSION_NOT_FOUND);
    }

    @Test
    void listPublishedPackagesDelegatesToRepository() {
        when(packageRepository.findByStatus(MarketplacePackage.STATUS_PUBLISHED))
                .thenReturn(List.of(fixturePackage(MarketplacePackage.STATUS_PUBLISHED)));
        assertThat(service.listPublishedPackages()).hasSize(1);
    }

    private MarketplacePackage fixturePackage(String status) {
        var audit = new com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata(
                "operator-1", java.time.LocalDateTime.now(), "operator-1", java.time.LocalDateTime.now());
        return new MarketplacePackage("pkg-1", "pkg-1-code", "Pkg One", "platform", List.of("BCM-PLT-011"), status, audit);
    }

    private PackageVersion fixtureVersion(String lifecycleStatus) {
        var audit = new com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata(
                "operator-1", java.time.LocalDateTime.now(), "operator-1", java.time.LocalDateTime.now());
        return new PackageVersion("ver-1", "pkg-1", "1.0.0", lifecycleStatus, false, false, false, false, null, audit);
    }
}
