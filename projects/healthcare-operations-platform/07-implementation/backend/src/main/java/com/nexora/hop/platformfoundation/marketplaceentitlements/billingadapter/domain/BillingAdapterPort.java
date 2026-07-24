package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain;

/**
 * Provider-agnostic outbound port (package-manifest.yaml/commercial-offer.yaml OFFER-004) mirroring
 * {@code FiscalAdapterPort}, {@code NotificationProviderPort}, {@code DocumentStoragePort} and
 * {@code IntegrationAdapterPort}. Implementations must be replaceable with a real billing provider
 * integration (e.g. Stripe, a payment gateway) without any domain or application code change.
 * INV-MKT-003: a billing event is an integration output only and can never become the source of
 * truth for entitlement, license or clinical state.
 */
public interface BillingAdapterPort {

    /**
     * Submits a billing event to the provider boundary.
     *
     * @throws BillingAdapterException if the event is rejected or the provider boundary is
     *         unavailable (canonical error code {@code PROVIDER_ADAPTER_UNAVAILABLE})
     */
    BillingAdapterAcknowledgement submitBillingEvent(
            String tenantId, String entitlementId, String eventType, long amountMinorUnits, String currency,
            String providerReference);
}
