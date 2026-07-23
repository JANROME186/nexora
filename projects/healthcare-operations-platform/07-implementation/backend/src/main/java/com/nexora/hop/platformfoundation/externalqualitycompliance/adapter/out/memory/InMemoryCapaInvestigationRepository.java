package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigationRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("local | test")
public class InMemoryCapaInvestigationRepository implements CapaInvestigationRepository {

    private final Map<UUID, CapaInvestigation> store = new ConcurrentHashMap<>();

    @Override
    public CapaInvestigation save(CapaInvestigation capa) {
        store.put(capa.getCapaId(), capa);
        return capa;
    }

    @Override
    public Optional<CapaInvestigation> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<CapaInvestigation> findAll(String status, String sourceCategory) {
        return store.values().stream()
                .filter(c -> status == null || status.isBlank() || c.getStatus().name().equalsIgnoreCase(status.trim()))
                .filter(c -> sourceCategory == null || sourceCategory.isBlank() || c.getSourceCategory().equalsIgnoreCase(sourceCategory.trim()))
                .toList();
    }
}
