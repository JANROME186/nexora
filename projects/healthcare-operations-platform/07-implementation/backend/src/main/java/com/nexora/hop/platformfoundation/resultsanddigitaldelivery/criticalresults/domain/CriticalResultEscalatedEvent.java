package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import java.util.UUID;

public record CriticalResultEscalatedEvent(
        UUID escalationId,
        ResultId resultId,
        TenantId tenantId,
        int newTier) {
}
