package com.nexora.hop.platformfoundation.marketplaceentitlements.commercialoffers.domain;

import java.util.List;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Root aggregate of AGG-031 CommercialOffer (BCM-PLT-011). Customer-facing offer, bundle, trial
 * and pricing reference (commercial-offer.yaml). {@code tierCodes} models {@code OfferTier}
 * membership; {@code trialPeriodDays} models {@code TrialPolicy}; {@code billingEventRulesSummary}
 * models {@code BillingEventRule} as free text (OFFER-004: provider-specific details never live
 * here).
 */
public record CommercialOffer(
        String offerId,
        String packageId,
        String packageVersion,
        String offerCode,
        String offerType,
        String lifecycleStatus,
        List<String> tierCodes,
        Integer trialPeriodDays,
        String billingEventRulesSummary,
        int effectiveVersion,
        AuditMetadata audit) {

    public static final String TYPE_BASE_PLAN = "base_plan";
    public static final String TYPE_EXPANSION_PACKAGE = "expansion_package";
    public static final String TYPE_USAGE_ADDON = "usage_addon";
    public static final String TYPE_SERVICES_PACKAGE = "services_package";

    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PENDING_REVIEW = "pending_review";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_DEPRECATED = "deprecated";
    public static final String STATUS_RETIRED = "retired";
}
