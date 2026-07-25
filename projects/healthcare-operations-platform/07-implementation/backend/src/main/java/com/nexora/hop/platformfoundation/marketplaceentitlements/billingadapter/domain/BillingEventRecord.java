package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Observability record of a billing event submitted through {@link BillingAdapterPort}
 * (package-manifest.md billing-adapter boundary). INV-MKT-003: this record is an integration
 * output only, never mutated by or authoritative over entitlement, license or clinical state.
 * {@code providerReference} doubles as the idempotency key when the caller supplies one
 * (COM-MOD-017-BE-002); {@code retryCount} tracks how many times {@link
 * BillingAdapterPort#retrySubmission} has been invoked for this record.
 */
public record BillingEventRecord(
        String billingEventId,
        String tenantId,
        String entitlementId,
        String eventType,
        long amountMinorUnits,
        String currency,
        String providerReference,
        String adapterStatus,
        int retryCount,
        AuditMetadata audit) {
}
