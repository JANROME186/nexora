package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface CriticalResultEscalationRepository {

    CriticalResultEscalation save(CriticalResultEscalation escalation);

    Optional<CriticalResultEscalation> findById(UUID escalationId);

    Optional<CriticalResultEscalation> findByResultId(ResultId resultId);

    List<CriticalResultEscalation> findOpenEscalations(String tenantId);

    List<CriticalResultEscalation> findAll();
}
