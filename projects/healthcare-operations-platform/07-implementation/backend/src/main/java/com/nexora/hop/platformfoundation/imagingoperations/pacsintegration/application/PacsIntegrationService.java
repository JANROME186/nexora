package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.application;

import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.domain.PacsIntegrationEndpoint;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.domain.PacsIntegrationEndpointRepository;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port.PacsBridgePort;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingErrorCode;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class PacsIntegrationService {

    private final PacsIntegrationEndpointRepository repository;
    private final PacsBridgePort pacsBridgePort;

    public PacsIntegrationService(PacsIntegrationEndpointRepository repository, PacsBridgePort pacsBridgePort) {
        this.repository = repository;
        this.pacsBridgePort = pacsBridgePort;
    }

    public PacsIntegrationEndpoint registerEndpoint(
            String tenantId,
            String pacsNodeId,
            String baseUrl,
            String protocol,
            String actorId) {
        boolean alive = pacsBridgePort.pingEndpoint(baseUrl, protocol);
        String status = alive ? "ONLINE" : "OFFLINE";
        String endpointId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        PacsIntegrationEndpoint endpoint = new PacsIntegrationEndpoint(
                endpointId, tenantId, pacsNodeId, baseUrl, protocol, status,
                "***MASKED***", actorId, now, actorId, now
        );
        return repository.save(endpoint);
    }

    public PacsIntegrationEndpoint getEndpoint(String tenantId, String endpointId) {
        return repository.findById(tenantId, endpointId)
                .orElseThrow(() -> new ImagingNotFoundException(ImagingErrorCode.PACS_ENDPOINT_NOT_FOUND, "PACS endpoint " + endpointId + " not found"));
    }

    public List<PacsIntegrationEndpoint> listEndpoints(String tenantId) {
        return repository.findAllByTenant(tenantId);
    }

    public String queryStudy(String tenantId, String endpointId, String accessionNumber) {
        PacsIntegrationEndpoint endpoint = getEndpoint(tenantId, endpointId);
        return pacsBridgePort.queryStudyInstances(endpoint.pacsNodeId(), accessionNumber);
    }
}
