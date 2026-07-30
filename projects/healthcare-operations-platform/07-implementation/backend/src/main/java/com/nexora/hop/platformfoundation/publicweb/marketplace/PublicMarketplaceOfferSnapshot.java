package com.nexora.hop.platformfoundation.publicweb.marketplace;

import java.util.List;

/**
 * Public, read-only snapshot DTO for published commercial offers (BCM-PLT-011 public_surface).
 * Excludes internal audit metadata and tenant entitlement state.
 */
public record PublicMarketplaceOfferSnapshot(
        String offerId,
        String packageId,
        String packageVersion,
        String offerCode,
        String offerType,
        String lifecycleStatus,
        List<String> tierCodes,
        Integer trialPeriodDays,
        String billingEventRulesSummary) {
}
