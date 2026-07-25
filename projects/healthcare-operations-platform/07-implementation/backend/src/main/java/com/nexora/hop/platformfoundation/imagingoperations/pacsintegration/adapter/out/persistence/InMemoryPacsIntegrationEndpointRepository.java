package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.domain.PacsIntegrationEndpoint;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.domain.PacsIntegrationEndpointRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
public class InMemoryPacsIntegrationEndpointRepository implements PacsIntegrationEndpointRepository {

    private final Map<String, PacsIntegrationEndpoint> store = new ConcurrentHashMap<>();

    @Override
    public PacsIntegrationEndpoint save(PacsIntegrationEndpoint endpoint) {
        store.put(endpoint.tenantId() + ":" + endpoint.endpointId(), endpoint);
        return endpoint;
    }

    @Override
    public Optional<PacsIntegrationEndpoint> findById(String tenantId, String endpointId) {
        return Optional.ofNullable(store.get(tenantId + ":" + endpointId));
    }

    @Override
    public Optional<PacsIntegrationEndpoint> findByPacsNodeId(String tenantId, String pacsNodeId) {
        return store.values().stream()
                .filter(e -> e.tenantId().equals(tenantId) && e.pacsNodeId().equalsIgnoreCase(pacsNodeId))
                .findFirst();
    }

    @Override
    public List<PacsIntegrationEndpoint> findAllByTenant(String tenantId) {
        return store.values().stream()
                .filter(e -> e.tenantId().equals(tenantId))
                .toList();
    }
}
