package com.nexora.hop.platformfoundation.publicweb.marketplace;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.application.CommercialOfferService;
import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain.CommercialOffer;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.application.PackageCatalogService;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.MarketplacePackage;
import com.nexora.hop.platformfoundation.publicweb.PublicWebConstants;
import com.nexora.hop.platformfoundation.publicweb.PublicWebErrorCodes;
import com.nexora.hop.platformfoundation.publicweb.PublicWebException;

/**
 * Anonymous public read of published marketplace packages and commercial offers (BCM-PLT-011 public_surface, TD-WEB-001).
 * Never exposes unpublished, draft or retired packages/offers. Response DTOs exclude tenantId,
 * entitlement data, billing records and internal audit details.
 */
@RestController
@RequestMapping(PublicWebConstants.MARKETPLACE_BASE_PATH)
public class PublicMarketplaceController {

    private final PackageCatalogService packageCatalogService;
    private final CommercialOfferService commercialOfferService;

    public PublicMarketplaceController(
            PackageCatalogService packageCatalogService,
            CommercialOfferService commercialOfferService) {
        this.packageCatalogService = packageCatalogService;
        this.commercialOfferService = commercialOfferService;
    }

    @GetMapping("/packages/published")
    public ResponseEntity<List<PublicMarketplacePackageSnapshot>> listPublishedPackages() {
        List<PublicMarketplacePackageSnapshot> published = packageCatalogService.listPublishedPackages()
                .stream()
                .map(this::toPackageSnapshot)
                .toList();
        return ResponseEntity.ok(published);
    }

    @GetMapping("/packages/{packageId}/published-snapshot")
    public ResponseEntity<PublicMarketplacePackageSnapshot> getPublishedPackageSnapshot(
            @PathVariable String packageId) {
        MarketplacePackage pkg;
        try {
            pkg = packageCatalogService.getPackage(packageId);
        } catch (Exception ex) {
            throw new PublicWebException(
                    HttpStatus.NOT_FOUND,
                    PublicWebErrorCodes.PUBLIC_CATALOG_NOT_PUBLISHED,
                    "Requested marketplace package is not published.");
        }

        if (!MarketplacePackage.STATUS_PUBLISHED.equals(pkg.status())) {
            throw new PublicWebException(
                    HttpStatus.NOT_FOUND,
                    PublicWebErrorCodes.PUBLIC_CATALOG_NOT_PUBLISHED,
                    "Requested marketplace package is not published.");
        }

        return ResponseEntity.ok(toPackageSnapshot(pkg));
    }

    @GetMapping("/offers/published")
    public ResponseEntity<List<PublicMarketplaceOfferSnapshot>> listPublishedOffers(
            @RequestParam(required = false) String packageId) {
        List<PublicMarketplaceOfferSnapshot> publishedOffers = commercialOfferService.listOffers(packageId)
                .stream()
                .filter(offer -> CommercialOffer.STATUS_PUBLISHED.equals(offer.lifecycleStatus()))
                .map(this::toOfferSnapshot)
                .toList();
        return ResponseEntity.ok(publishedOffers);
    }

    private PublicMarketplacePackageSnapshot toPackageSnapshot(MarketplacePackage pkg) {
        return new PublicMarketplacePackageSnapshot(
                pkg.packageId(),
                pkg.code(),
                pkg.name(),
                pkg.category(),
                pkg.capabilityMappings(),
                pkg.status());
    }

    private PublicMarketplaceOfferSnapshot toOfferSnapshot(CommercialOffer offer) {
        return new PublicMarketplaceOfferSnapshot(
                offer.offerId(),
                offer.packageId(),
                offer.packageVersion(),
                offer.offerCode(),
                offer.offerType(),
                offer.lifecycleStatus(),
                offer.tierCodes(),
                offer.trialPeriodDays(),
                offer.billingEventRulesSummary());
    }
}
