package com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.domain.ReceptionVisit;
import com.nexora.hop.platformfoundation.frontdeskcaredelivery.receptionmanagement.domain.ReceptionVisitRepository;

@Repository
@Profile("!local")
class InMemoryReceptionVisitRepository implements ReceptionVisitRepository {

    private final Map<String, ReceptionVisit> visits = new ConcurrentHashMap<>();

    @Override
    public ReceptionVisit save(ReceptionVisit visit) {
        visits.put(visit.visitId(), visit);
        return visit;
    }

    @Override
    public Optional<ReceptionVisit> findById(String visitId) {
        return Optional.ofNullable(visits.get(visitId));
    }

    @Override
    public List<ReceptionVisit> findByTenantId(String tenantId) {
        return visits.values().stream().filter(v -> v.tenantId().equals(tenantId)).toList();
    }
}
