package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.application;

import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.domain.DicomAdapterConfiguration;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.domain.DicomAdapterConfigurationRepository;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port.DicomGatewayPort;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingErrorCode;
import com.nexora.hop.platformfoundation.imagingoperations.shared.ImagingNotFoundException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class DicomIntegrationService {

    private final DicomAdapterConfigurationRepository repository;
    private final DicomGatewayPort dicomGatewayPort;

    public DicomIntegrationService(DicomAdapterConfigurationRepository repository, DicomGatewayPort dicomGatewayPort) {
        this.repository = repository;
        this.dicomGatewayPort = dicomGatewayPort;
    }

    public DicomAdapterConfiguration registerConfiguration(
            String tenantId,
            String aeTitle,
            String host,
            int port,
            String modalityType,
            String actorId) {
        boolean connected = dicomGatewayPort.testConnection(aeTitle, host, port);
        String status = connected ? "ACTIVE" : "INACTIVE";
        String configId = UUID.randomUUID().toString();
        Instant now = Instant.now();
        DicomAdapterConfiguration config = new DicomAdapterConfiguration(
                configId, tenantId, aeTitle, host, port, modalityType, status,
                actorId, now, actorId, now
        );
        return repository.save(config);
    }

    public DicomAdapterConfiguration getConfiguration(String tenantId, String configurationId) {
        return repository.findById(tenantId, configurationId)
                .orElseThrow(() -> new ImagingNotFoundException(ImagingErrorCode.DICOM_CONFIG_NOT_FOUND, "DICOM configuration " + configurationId + " not found"));
    }

    public List<DicomAdapterConfiguration> listConfigurations(String tenantId) {
        return repository.findAllByTenant(tenantId);
    }

    public String testCEcho(String tenantId, String configurationId) {
        DicomAdapterConfiguration config = getConfiguration(tenantId, configurationId);
        return dicomGatewayPort.echoCEcho(config.aeTitle());
    }
}
