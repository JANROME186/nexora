package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import java.util.UUID;

public record ResultDeliveryAuthorizedEvent(
        UUID deliveryTicketId,
        ResultId resultId,
        TenantId tenantId,
        String recipientType,
        String deliveryChannel) {
}
