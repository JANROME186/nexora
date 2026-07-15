package com.nexora.hop.platformfoundation.frontdeskcaredelivery.diagnosticordermanagement.domain;

import java.time.Instant;

import com.nexora.hop.platformfoundation.catalogtestconfiguration.shared.Money;

/**
 * Immutable copy of the price-list entries applied to the order, captured at order time from
 * BCM-SVC-009 (VO-ORD-005). Line-level amounts live on each {@link OrderLine#unitAmount()}; this
 * snapshot carries the resolved price list identity and the computed total.
 */
public record OrderPricingSnapshot(
        String priceListId,
        int priceListVersion,
        Money totalAmount,
        Instant capturedAt) {
}
