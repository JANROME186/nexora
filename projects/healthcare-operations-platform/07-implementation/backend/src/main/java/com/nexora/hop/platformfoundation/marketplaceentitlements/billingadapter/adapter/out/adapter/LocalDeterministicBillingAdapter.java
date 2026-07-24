package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.adapter.out.adapter;

import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterAcknowledgement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterException;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterPort;

/**
 * Local deterministic implementation of {@link BillingAdapterPort} for local development and
 * tests, mirroring {@code LocalDeterministicPassthroughIntegrationAdapter} and
 * {@code LocalDeterministicFiscalAdapter}. Not a real payment-provider integration. Rejects a
 * non-positive amount or an unsupported currency (license-plan.yaml {@code base_currencies},
 * matched as an exact-case canonical ISO-4217 code, avoiding locale-sensitive case folding), and
 * deterministically simulates a provider outage for the literal marker event type
 * {@code "SIMULATE_PROVIDER_DOWN"} so {@code PROVIDER_ADAPTER_UNAVAILABLE} is exercisable in tests
 * without a real provider dependency.
 */
@Component
public class LocalDeterministicBillingAdapter implements BillingAdapterPort {

    private static final String PROVIDER_ID = "local-billing-passthrough";
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("MXN", "USD");
    private static final String SIMULATE_PROVIDER_DOWN_MARKER = "SIMULATE_PROVIDER_DOWN";

    @Override
    public BillingAdapterAcknowledgement submitBillingEvent(
            String tenantId, String entitlementId, String eventType, long amountMinorUnits, String currency,
            String providerReference) {
        if (SIMULATE_PROVIDER_DOWN_MARKER.equals(eventType)) {
            throw new BillingAdapterException(
                    "Local billing adapter provider boundary is unavailable.", "PROVIDER_ADAPTER_UNAVAILABLE");
        }
        if (amountMinorUnits < 0) {
            throw new BillingAdapterException("Billing amount must not be negative.", "MARKETPLACE_COMMAND_INVALID");
        }
        if (currency == null || !SUPPORTED_CURRENCIES.contains(currency)) {
            throw new BillingAdapterException(
                    "Currency must be one of " + SUPPORTED_CURRENCIES + ".", "MARKETPLACE_COMMAND_INVALID");
        }
        String resolvedReference = providerReference != null && !providerReference.isBlank()
                ? providerReference
                : PROVIDER_ID + "-" + UUID.randomUUID();
        return new BillingAdapterAcknowledgement(resolvedReference, BillingAdapterAcknowledgement.STATUS_ACCEPTED, null);
    }
}
