package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.memory;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditSchedule;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.AuditScheduleRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("!local")
public class InMemoryAuditScheduleRepository implements AuditScheduleRepository {

    private final Map<UUID, AuditSchedule> store = new ConcurrentHashMap<>();

    @Override
    public AuditSchedule save(AuditSchedule audit) {
        store.put(audit.getAuditId(), audit);
        return audit;
    }

    @Override
    public Optional<AuditSchedule> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<AuditSchedule> findAll(String category, String status) {
        return store.values().stream()
                .filter(a -> category == null || category.isBlank() || a.getCategory().equalsIgnoreCase(category.trim()))
                .filter(a -> status == null || status.isBlank() || a.getStatus().name().equalsIgnoreCase(status.trim()))
                .toList();
    }
}
