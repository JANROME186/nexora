package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.QualityEventIntake;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.QualityEventIntakeRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("!local")
public class InMemoryQualityEventIntakeRepository implements QualityEventIntakeRepository {

    private final Map<UUID, QualityEventIntake> store = new ConcurrentHashMap<>();

    @Override
    public QualityEventIntake save(QualityEventIntake event) {
        store.put(event.getEventId(), event);
        return event;
    }

    @Override
    public Optional<QualityEventIntake> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<QualityEventIntake> findAll(String sourceSystem, String severity) {
        return store.values().stream()
                .filter(e -> sourceSystem == null || sourceSystem.isBlank() || e.getSourceSystem().equalsIgnoreCase(sourceSystem.trim()))
                .filter(e -> severity == null || severity.isBlank() || e.getSeverity().equalsIgnoreCase(severity.trim()))
                .toList();
    }
}
