package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Observability record of a billing event submitted through {@link BillingAdapterPort}
 * (package-manifest.yaml billing-adapter boundary). INV-MKT-003: this record is an integration
 * output only, never mutated by or authoritative over entitlement, license or clinical state.
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
        AuditMetadata audit) {
}
