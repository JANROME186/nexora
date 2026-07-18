package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationEndpoint;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationEndpointRepository;

@Repository
@Profile("!local")
class InMemoryIntegrationEndpointRepository implements IntegrationEndpointRepository {

    private final Map<String, IntegrationEndpoint> endpoints = new ConcurrentHashMap<>();

    @Override
    public IntegrationEndpoint save(IntegrationEndpoint endpoint) {
        endpoints.put(endpoint.endpointId(), endpoint);
        return endpoint;
    }

    @Override
    public Optional<IntegrationEndpoint> findById(String endpointId) {
        return Optional.ofNullable(endpoints.get(endpointId));
    }

    @Override
    public List<IntegrationEndpoint> findByTenantId(String tenantId) {
        return endpoints.values().stream().filter(endpoint -> endpoint.tenantId().equals(tenantId)).toList();
    }
}
