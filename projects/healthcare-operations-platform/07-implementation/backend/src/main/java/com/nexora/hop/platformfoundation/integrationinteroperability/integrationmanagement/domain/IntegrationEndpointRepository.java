package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface IntegrationEndpointRepository {

    IntegrationEndpoint save(IntegrationEndpoint endpoint);

    Optional<IntegrationEndpoint> findById(String endpointId);

    List<IntegrationEndpoint> findByTenantId(String tenantId);
}
