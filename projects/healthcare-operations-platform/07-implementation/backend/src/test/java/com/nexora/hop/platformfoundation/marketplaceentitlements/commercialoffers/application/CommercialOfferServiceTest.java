package com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
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

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain.CommercialOffer;
import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain.CommercialOfferRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.application.CompatibilityEvaluator;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersion;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersionRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceConflictException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceEntityNotFoundException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceErrorCodes;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application.TenantEntitlementService;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

class CommercialOfferServiceTest {

    private CommercialOfferRepository offerRepository;
    private PackageVersionRepository versionRepository;
    private TenantEntitlementService entitlementService;
    private CommercialOfferService service;

    @BeforeEach
    void setUp() {
        offerRepository = mock(CommercialOfferRepository.class);
        versionRepository = mock(PackageVersionRepository.class);
        entitlementService = mock(TenantEntitlementService.class);
        when(offerRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        Clock clock = Clock.fixed(Instant.parse("2026-01-01T00:00:00Z"), ZoneOffset.UTC);
        service = new CommercialOfferService(
                offerRepository, versionRepository, new CompatibilityEvaluator(), entitlementService,
                mock(AuditRecorder.class), clock);
    }

    @Test
    void publishOfferRequiresThePackageVersionToBePublished() {
        when(versionRepository.findByPackageIdAndVersion("pkg-1", "1.0.0"))
                .thenReturn(Optional.of(fixtureVersion(PackageVersion.STATUS_CERTIFIED)));

        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class, () -> service.publishOffer(
                "pkg-1", "1.0.0", "offer-code", CommercialOffer.TYPE_BASE_PLAN, List.of("tier-1"), 14, "summary",
                "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.OFFER_NOT_AVAILABLE);
    }

    @Test
    void publishOfferSucceedsWhenPackageVersionIsPublished() {
        when(versionRepository.findByPackageIdAndVersion("pkg-1", "1.0.0"))
                .thenReturn(Optional.of(fixtureVersion(PackageVersion.STATUS_PUBLISHED)));

        CommercialOffer offer = service.publishOffer(
                "pkg-1", "1.0.0", "offer-code", CommercialOffer.TYPE_BASE_PLAN, List.of("tier-1"), 14, "summary",
                "operator-1");
        assertThat(offer.lifecycleStatus()).isEqualTo(CommercialOffer.STATUS_PUBLISHED);
    }

    @Test
    void acceptOfferRejectsWhenOfferIsNotPublished() {
        when(offerRepository.findById("offer-1")).thenReturn(Optional.of(fixtureOffer(CommercialOffer.STATUS_DRAFT)));
        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class,
                () -> service.acceptOffer("offer-1", "tenant-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.OFFER_NOT_AVAILABLE);
    }

    @Test
    void acceptOfferRejectsWhenCompatibilityFails() {
        CommercialOffer offer = new CommercialOffer(
                "offer-1", "pkg-1", "9.9.9", "offer-code", CommercialOffer.TYPE_BASE_PLAN,
                CommercialOffer.STATUS_PUBLISHED, List.of(), null, null, 1, fixtureAudit());
        when(offerRepository.findById("offer-1")).thenReturn(Optional.of(offer));

        MarketplaceConflictException exception = assertThrows(MarketplaceConflictException.class,
                () -> service.acceptOffer("offer-1", "tenant-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.COMPATIBILITY_FAILED);
    }

    @Test
    void acceptOfferGrantsAnEntitlementWhenCompatible() {
        when(offerRepository.findById("offer-1")).thenReturn(Optional.of(fixtureOffer(CommercialOffer.STATUS_PUBLISHED)));
        TenantEntitlement granted = new TenantEntitlement(
                "ent-1", "tenant-1", "pkg-1", "offer-1", TenantEntitlement.STATUS_ACTIVE, LocalDateTime.now(), null,
                null, fixtureAudit());
        when(entitlementService.grantEntitlement("tenant-1", "pkg-1", "offer-1", null, "operator-1"))
                .thenReturn(granted);

        TenantEntitlement result = service.acceptOffer("offer-1", "tenant-1", "operator-1");
        assertThat(result.entitlementId()).isEqualTo("ent-1");
    }

    @Test
    void acceptOfferThrowsNotFoundForUnknownOffer() {
        when(offerRepository.findById("missing")).thenReturn(Optional.empty());
        MarketplaceEntityNotFoundException exception = assertThrows(MarketplaceEntityNotFoundException.class,
                () -> service.acceptOffer("missing", "tenant-1", "operator-1"));
        assertThat(exception.code()).isEqualTo(MarketplaceErrorCodes.OFFER_NOT_FOUND);
    }

    @Test
    void listOffersFiltersByPackageIdWhenProvided() {
        when(offerRepository.findByPackageId("pkg-1")).thenReturn(List.of(fixtureOffer(CommercialOffer.STATUS_PUBLISHED)));
        assertThat(service.listOffers("pkg-1")).hasSize(1);
    }

    @Test
    void listOffersReturnsAllWhenPackageIdIsBlank() {
        when(offerRepository.findAll()).thenReturn(List.of(fixtureOffer(CommercialOffer.STATUS_PUBLISHED)));
        assertThat(service.listOffers(null)).hasSize(1);
    }

    private CommercialOffer fixtureOffer(String status) {
        return new CommercialOffer(
                "offer-1", "pkg-1", CompatibilityEvaluator.PLATFORM_VERSION, "offer-code",
                CommercialOffer.TYPE_BASE_PLAN, status, List.of("tier-1"), 14, "summary", 1, fixtureAudit());
    }

    private PackageVersion fixtureVersion(String lifecycleStatus) {
        return new PackageVersion("ver-1", "pkg-1", "1.0.0", lifecycleStatus, true, true, true, true, fixtureAudit());
    }

    private AuditMetadata fixtureAudit() {
        return new AuditMetadata("operator-1", LocalDateTime.now(), "operator-1", LocalDateTime.now());
    }
}
