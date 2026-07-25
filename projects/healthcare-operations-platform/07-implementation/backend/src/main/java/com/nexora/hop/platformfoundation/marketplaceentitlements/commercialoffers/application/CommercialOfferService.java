package com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain.CommercialOffer;
import com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain.CommercialOfferRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.application.CompatibilityEvaluator;
import com.nexora.hop.platformfoundation.marketplaceentitlements.compatibilityevaluation.domain.CompatibilityDecision;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersion;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packagecatalog.domain.PackageVersionRepository;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.InvalidMarketplaceCommandException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceConflictException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceEntityNotFoundException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.shared.MarketplaceErrorCodes;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.application.TenantEntitlementService;
import com.nexora.hop.platformfoundation.marketplaceentitlements.tenantentitlements.domain.TenantEntitlement;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Compiles the generatable outputs of BCM-PLT-011's commercialoffers capability (AGG-031
 * CommercialOffer): publication, listing and tenant acceptance (OFFER-001..OFFER-004).
 */
@Service
public class CommercialOfferService {

    private final CommercialOfferRepository offerRepository;
    private final PackageVersionRepository versionRepository;
    private final CompatibilityEvaluator compatibilityEvaluator;
    private final TenantEntitlementService entitlementService;
    private final AuditRecorder auditRecorder;
    private final Clock clock;

    @Autowired
    public CommercialOfferService(
            CommercialOfferRepository offerRepository, PackageVersionRepository versionRepository,
            CompatibilityEvaluator compatibilityEvaluator, TenantEntitlementService entitlementService,
            AuditRecorder auditRecorder) {
        this(offerRepository, versionRepository, compatibilityEvaluator, entitlementService, auditRecorder,
                Clock.systemUTC());
    }

    CommercialOfferService(
            CommercialOfferRepository offerRepository, PackageVersionRepository versionRepository,
            CompatibilityEvaluator compatibilityEvaluator, TenantEntitlementService entitlementService,
            AuditRecorder auditRecorder, Clock clock) {
        this.offerRepository = offerRepository;
        this.versionRepository = versionRepository;
        this.compatibilityEvaluator = compatibilityEvaluator;
        this.entitlementService = entitlementService;
        this.auditRecorder = auditRecorder;
        this.clock = clock;
    }

    /** OFFER-001: an offer cannot be published unless its package version is already published. */
    public CommercialOffer publishOffer(
            String packageId, String packageVersion, String offerCode, String offerType, List<String> tierCodes,
            Integer trialPeriodDays, String billingEventRulesSummary, String actorId) {
        String pkg = requiredText(packageId, "Package id is required.");
        String version = requiredText(packageVersion, "Package version is required.");
        PackageVersion targetVersion = versionRepository.findByPackageIdAndVersion(pkg, version)
                .orElseThrow(() -> new MarketplaceEntityNotFoundException(
                        "Package version was not found.", MarketplaceErrorCodes.PACKAGE_VERSION_NOT_FOUND));
        if (!PackageVersion.STATUS_PUBLISHED.equals(targetVersion.lifecycleStatus())) {
            throw new MarketplaceConflictException(
                    "Package " + pkg + " version " + version + " must be published before an offer can reference it.",
                    MarketplaceErrorCodes.OFFER_NOT_AVAILABLE);
        }
        String code = requiredText(offerCode, "Offer code is required.");
        String type = requiredText(offerType, "Offer type is required.");
        String actor = requiredText(actorId, "Actor id is required.");

        LocalDateTime now = LocalDateTime.now(clock);
        CommercialOffer published = offerRepository.save(new CommercialOffer(
                newId(), pkg, version, code, type, CommercialOffer.STATUS_PUBLISHED,
                tierCodes == null ? List.of() : List.copyOf(tierCodes), trialPeriodDays, billingEventRulesSummary, 1,
                new AuditMetadata(actor, now, actor, now)));
        auditRecorder.recordSystemEvent("platform", "CommercialOfferPublished", "CommercialOffer",
                published.offerId(), "{\"offerCode\":\"%s\"}".formatted(code));
        return published;
    }

    public List<CommercialOffer> listOffers(String packageId) {
        return packageId == null || packageId.isBlank() ? offerRepository.findAll() : offerRepository.findByPackageId(packageId);
    }

    /**
     * OFFER-002: a tenant cannot buy an offer when compatibility evaluation fails. Acceptance
     * grants the tenant a {@link TenantEntitlement} for the offer's package (RN-MKT-002's
     * "active entitlement" precondition begins here).
     */
    public TenantEntitlement acceptOffer(String offerId, String tenantId, String actorId) {
        CommercialOffer offer = offerRepository.findById(requiredText(offerId, "Offer id is required."))
                .orElseThrow(() -> new MarketplaceEntityNotFoundException(
                        "Commercial offer was not found.", MarketplaceErrorCodes.OFFER_NOT_FOUND));
        if (!CommercialOffer.STATUS_PUBLISHED.equals(offer.lifecycleStatus())) {
            throw new MarketplaceConflictException(
                    "Offer " + offerId + " is not available for acceptance.", MarketplaceErrorCodes.OFFER_NOT_AVAILABLE);
        }
        CompatibilityDecision decision = compatibilityEvaluator.evaluate(offer.packageId(), offer.packageVersion());
        if (!decision.allowsInstallation()) {
            throw new MarketplaceConflictException(
                    "Offer " + offerId + " failed compatibility evaluation: " + decision.decision() + ".",
                    MarketplaceErrorCodes.COMPATIBILITY_FAILED);
        }
        TenantEntitlement granted = entitlementService.grantEntitlement(
                tenantId, offer.packageId(), offer.offerId(), null, actorId);
        auditRecorder.recordSystemEvent(tenantId, "CommercialOfferAccepted", "CommercialOffer", offer.offerId(),
                "{\"entitlementId\":\"%s\"}".formatted(granted.entitlementId()));
        return granted;
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new InvalidMarketplaceCommandException(message, MarketplaceErrorCodes.MARKETPLACE_COMMAND_INVALID);
        }
        return value;
    }

    private static String newId() {
        return UUID.randomUUID().toString();
    }
}
