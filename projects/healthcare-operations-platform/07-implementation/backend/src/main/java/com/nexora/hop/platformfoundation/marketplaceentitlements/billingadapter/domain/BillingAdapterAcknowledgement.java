package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain;

/** Adapter acknowledgement for a submitted billing event (mirrors {@code IntegrationAcknowledgement}). */
public record BillingAdapterAcknowledgement(String providerReference, String adapterStatus, String canonicalErrorCode) {

    public static final String STATUS_ACCEPTED = "accepted";
    public static final String STATUS_REJECTED = "rejected";
}
