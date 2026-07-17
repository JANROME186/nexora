package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.OrderSamplesRepository;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.Sample;
import com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain.SampleStatus;

/**
 * In-memory repository for Sample aggregate (test and default profile).
 */
@Repository
@Profile("!local")
class InMemoryOrderSamplesRepository implements OrderSamplesRepository {

    private final Map<String, Sample> store = new ConcurrentHashMap<>();

    @Override
    public Sample save(Sample sample) {
        store.put(key(sample.sampleId(), sample.tenantId()), sample);
        return sample;
    }

    @Override
    public Optional<Sample> findById(String sampleId, String tenantId) {
        return Optional.ofNullable(store.get(key(sampleId, tenantId)));
    }

    @Override
    public List<Sample> findByOrderId(String orderId, String tenantId) {
        return store.values().stream()
                .filter(s -> tenantId.equals(s.tenantId()) && orderId.equals(s.orderId()))
                .toList();
    }

    @Override
    public List<Sample> findByStatus(SampleStatus status, String tenantId) {
        return store.values().stream()
                .filter(s -> tenantId.equals(s.tenantId()) && status == s.status())
                .toList();
    }

    @Override
    public List<Sample> findCollectionWorklist(String tenantId, String branchId) {
        return store.values().stream()
                .filter(s -> tenantId.equals(s.tenantId())
                        && branchId.equals(s.branchId())
                        && s.status() == SampleStatus.collected)
                .toList();
    }

    @Override
    public List<Sample> findReceptionWorklist(String tenantId, String laboratoryId) {
        return store.values().stream()
                .filter(s -> tenantId.equals(s.tenantId())
                        && laboratoryId.equals(s.laboratoryId())
                        && s.status() == SampleStatus.labeled)
                .toList();
    }

    private static String key(String sampleId, String tenantId) {
        return tenantId + ":" + sampleId;
    }
}
