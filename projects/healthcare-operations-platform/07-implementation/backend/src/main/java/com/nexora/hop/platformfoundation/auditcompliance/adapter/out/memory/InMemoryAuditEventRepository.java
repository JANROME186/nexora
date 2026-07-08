package com.nexora.hop.platformfoundation.auditcompliance.adapter.out.memory;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEvent;
import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEventRepository;

@Repository
@Profile("!local")
class InMemoryAuditEventRepository implements AuditEventRepository {

    private final List<AuditEvent> events = new CopyOnWriteArrayList<>();

    @Override
    public AuditEvent append(AuditEvent event) {
        events.add(event);
        return event;
    }

    @Override
    public List<AuditEvent> search(String tenantId, String subjectId) {
        return events.stream()
                .filter(event -> tenantId == null || tenantId.equals(event.tenantId()))
                .filter(event -> subjectId == null || subjectId.equals(event.subjectId()))
                .sorted(Comparator.comparing(AuditEvent::occurredAt))
                .toList();
    }
}
