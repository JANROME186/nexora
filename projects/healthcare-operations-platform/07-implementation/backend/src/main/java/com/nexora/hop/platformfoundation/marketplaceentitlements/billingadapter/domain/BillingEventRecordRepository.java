package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain;

import java.util.List;
import java.util.Optional;

public interface BillingEventRecordRepository {

    BillingEventRecord save(BillingEventRecord record);

    List<BillingEventRecord> findByTenantId(String tenantId);

    /** Backs idempotency: {@code providerReference} doubles as the caller-supplied idempotency key. */
    Optional<BillingEventRecord> findByTenantIdAndProviderReference(String tenantId, String providerReference);
}
