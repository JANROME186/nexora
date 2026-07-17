package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import java.time.LocalDateTime;
import java.util.UUID;

public record ResultViewedEvent(
        UUID deliveryTicketId,
        ResultId resultId,
        TenantId tenantId,
        String recipientId,
        LocalDateTime viewedAt) {
}
