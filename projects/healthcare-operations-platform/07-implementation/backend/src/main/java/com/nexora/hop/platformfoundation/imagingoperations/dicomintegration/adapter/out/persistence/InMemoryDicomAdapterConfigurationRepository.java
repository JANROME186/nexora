package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.domain.DicomAdapterConfiguration;
import com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.domain.DicomAdapterConfigurationRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
public class InMemoryDicomAdapterConfigurationRepository implements DicomAdapterConfigurationRepository {

    private final Map<String, DicomAdapterConfiguration> store = new ConcurrentHashMap<>();

    @Override
    public DicomAdapterConfiguration save(DicomAdapterConfiguration config) {
        store.put(config.tenantId() + ":" + config.configurationId(), config);
        return config;
    }

    @Override
    public Optional<DicomAdapterConfiguration> findById(String tenantId, String configurationId) {
        return Optional.ofNullable(store.get(tenantId + ":" + configurationId));
    }

    @Override
    public Optional<DicomAdapterConfiguration> findByAeTitle(String tenantId, String aeTitle) {
        return store.values().stream()
                .filter(c -> c.tenantId().equals(tenantId) && c.aeTitle().equalsIgnoreCase(aeTitle))
                .findFirst();
    }

    @Override
    public List<DicomAdapterConfiguration> findAllByTenant(String tenantId) {
        return store.values().stream()
                .filter(c -> c.tenantId().equals(tenantId))
                .toList();
    }
}
