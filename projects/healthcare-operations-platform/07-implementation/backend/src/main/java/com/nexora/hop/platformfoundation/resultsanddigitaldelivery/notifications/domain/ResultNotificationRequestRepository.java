package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ResultNotificationRequestRepository {

    ResultNotificationRequest save(ResultNotificationRequest request);

    Optional<ResultNotificationRequest> findById(UUID id);

    List<ResultNotificationRequest> findByRecipientId(String recipientId);

    List<ResultNotificationRequest> findByResultId(ResultId resultId, TenantId tenantId);

    List<ResultNotificationRequest> findAll();
}
