package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.UserId;
import java.time.LocalDateTime;
import java.util.UUID;

public record CriticalResultAcknowledgedEvent(
        UUID escalationId,
        ResultId resultId,
        TenantId tenantId,
        UserId acknowledgedBy,
        LocalDateTime acknowledgedAt) {
}
