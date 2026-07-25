package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.domain;

import java.util.List;
import java.util.Optional;

public interface DicomAdapterConfigurationRepository {
    DicomAdapterConfiguration save(DicomAdapterConfiguration config);
    Optional<DicomAdapterConfiguration> findById(String tenantId, String configurationId);
    Optional<DicomAdapterConfiguration> findByAeTitle(String tenantId, String aeTitle);
    List<DicomAdapterConfiguration> findAllByTenant(String tenantId);
}
