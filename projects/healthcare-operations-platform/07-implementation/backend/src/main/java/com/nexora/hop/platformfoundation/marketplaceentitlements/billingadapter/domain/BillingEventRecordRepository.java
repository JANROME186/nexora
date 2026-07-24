package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain;

import java.util.List;

public interface BillingEventRecordRepository {

    BillingEventRecord save(BillingEventRecord record);

    List<BillingEventRecord> findByTenantId(String tenantId);
}
