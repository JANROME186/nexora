package com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingEventRecord;
import com.nexora.hop.platformfoundation.marketplaceentitlements.billingadapter.domain.BillingEventRecordRepository;

@Repository
@Profile("!local")
class InMemoryBillingEventRecordRepository implements BillingEventRecordRepository {

    private final Map<String, BillingEventRecord> records = new ConcurrentHashMap<>();

    @Override
    public BillingEventRecord save(BillingEventRecord record) {
        records.put(record.billingEventId(), record);
        return record;
    }

    @Override
    public List<BillingEventRecord> findByTenantId(String tenantId) {
        return records.values().stream().filter(candidate -> candidate.tenantId().equals(tenantId)).toList();
    }
}
