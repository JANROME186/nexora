package com.nexora.hop.platformfoundation.laboratoryworkflow.orderssamples.domain;

import java.util.List;
import java.util.Optional;

/**
 * Repository port for the Sample aggregate (AGG-008, orders-samples bounded context).
 * Implementations: InMemoryOrderSamplesRepository (test/default), JdbcOrderSamplesRepository (local profile).
 */
public interface OrderSamplesRepository {

    Sample save(Sample sample);

    Optional<Sample> findById(String sampleId, String tenantId);

    List<Sample> findByOrderId(String orderId, String tenantId);

    List<Sample> findByStatus(SampleStatus status, String tenantId);

    List<Sample> findCollectionWorklist(String tenantId, String branchId);

    List<Sample> findReceptionWorklist(String tenantId, String laboratoryId);
}
