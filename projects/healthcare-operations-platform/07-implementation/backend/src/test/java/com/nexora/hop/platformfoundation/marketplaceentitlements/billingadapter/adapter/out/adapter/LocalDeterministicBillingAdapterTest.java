package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.adapter.out.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterAcknowledgement;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingAdapterException;

class LocalDeterministicBillingAdapterTest {

    private final LocalDeterministicBillingAdapter adapter = new LocalDeterministicBillingAdapter();

    @Test
    void acceptsAValidEventAndEchoesTheProviderReference() {
        BillingAdapterAcknowledgement ack = adapter.submitBillingEvent(
                "tenant-1", "ent-1", "subscription_charge", 1999, "USD", "ref-123");
        assertThat(ack.adapterStatus()).isEqualTo(BillingAdapterAcknowledgement.STATUS_ACCEPTED);
        assertThat(ack.providerReference()).isEqualTo("ref-123");
    }

    @Test
    void generatesAReferenceWhenNoneIsSupplied() {
        BillingAdapterAcknowledgement ack = adapter.submitBillingEvent(
                "tenant-1", "ent-1", "subscription_charge", 500, "MXN", null);
        assertThat(ack.providerReference()).startsWith("local-billing-passthrough-");
    }

    @Test
    void rejectsNegativeAmount() {
        BillingAdapterException exception = assertThrows(BillingAdapterException.class,
                () -> adapter.submitBillingEvent("tenant-1", "ent-1", "charge", -1, "USD", null));
        assertThat(exception.canonicalErrorCode()).isEqualTo("MARKETPLACE_COMMAND_INVALID");
    }

    @Test
    void rejectsUnsupportedCurrency() {
        BillingAdapterException exception = assertThrows(BillingAdapterException.class,
                () -> adapter.submitBillingEvent("tenant-1", "ent-1", "charge", 100, "EUR", null));
        assertThat(exception.canonicalErrorCode()).isEqualTo("MARKETPLACE_COMMAND_INVALID");
    }

    @Test
    void simulatesProviderOutageForTheMarkerEventType() {
        BillingAdapterException exception = assertThrows(BillingAdapterException.class,
                () -> adapter.submitBillingEvent("tenant-1", "ent-1", "SIMULATE_PROVIDER_DOWN", 100, "USD", null));
        assertThat(exception.canonicalErrorCode()).isEqualTo("PROVIDER_ADAPTER_UNAVAILABLE");
    }

    @Test
    void retrySubmissionHonoursTheIdempotencyKeyAsTheProviderReference() {
        BillingAdapterAcknowledgement ack = adapter.retrySubmission(
                "tenant-1", "ref-123", "ent-1", "subscription_charge", 1999, "USD");
        assertThat(ack.adapterStatus()).isEqualTo(BillingAdapterAcknowledgement.STATUS_ACCEPTED);
        assertThat(ack.providerReference()).isEqualTo("ref-123");
    }

    @Test
    void retrySubmissionRejectsAMissingIdempotencyKey() {
        BillingAdapterException exception = assertThrows(BillingAdapterException.class,
                () -> adapter.retrySubmission("tenant-1", " ", "ent-1", "charge", 100, "USD"));
        assertThat(exception.canonicalErrorCode()).isEqualTo("MARKETPLACE_COMMAND_INVALID");
    }
}
