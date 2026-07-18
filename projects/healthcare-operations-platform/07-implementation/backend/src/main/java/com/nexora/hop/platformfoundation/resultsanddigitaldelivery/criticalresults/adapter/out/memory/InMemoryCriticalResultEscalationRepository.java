package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.adapter.out.memory;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.domain.CriticalResultEscalation;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.domain.CriticalResultEscalationRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@org.springframework.context.annotation.Profile({"local", "test"})
public class InMemoryCriticalResultEscalationRepository implements CriticalResultEscalationRepository {

    private final Map<UUID, CriticalResultEscalation> store = new ConcurrentHashMap<>();

    @Override
    public CriticalResultEscalation save(CriticalResultEscalation escalation) {
        store.put(escalation.getEscalationId(), escalation);
        return escalation;
    }

    @Override
    public Optional<CriticalResultEscalation> findById(UUID escalationId) {
        return Optional.ofNullable(store.get(escalationId));
    }

    @Override
    public Optional<CriticalResultEscalation> findByResultId(ResultId resultId) {
        for (CriticalResultEscalation esc : store.values()) {
            if (esc.getResultId().equals(resultId)) {
                return Optional.of(esc);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<CriticalResultEscalation> findOpenEscalations(String tenantId) {
        List<CriticalResultEscalation> results = new ArrayList<>();
        for (CriticalResultEscalation esc : store.values()) {
            if (esc.getTenantId().value().equals(tenantId) && esc.getStatus() != CriticalResultEscalation.Status.CLOSED) {
                results.add(esc);
            }
        }
        return results;
    }

    @Override
    public List<CriticalResultEscalation> findAll() {
        return new ArrayList<>(store.values());
    }
}
