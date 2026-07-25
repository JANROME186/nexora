package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.domain;

import java.util.List;
import java.util.Optional;

public interface PacsIntegrationEndpointRepository {
    PacsIntegrationEndpoint save(PacsIntegrationEndpoint endpoint);
    Optional<PacsIntegrationEndpoint> findById(String tenantId, String endpointId);
    Optional<PacsIntegrationEndpoint> findByPacsNodeId(String tenantId, String pacsNodeId);
    List<PacsIntegrationEndpoint> findAllByTenant(String tenantId);
}
