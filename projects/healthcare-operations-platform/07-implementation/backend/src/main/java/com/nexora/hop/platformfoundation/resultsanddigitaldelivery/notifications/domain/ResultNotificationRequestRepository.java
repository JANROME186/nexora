package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface ResultNotificationRequestRepository {

    ResultNotificationRequest save(ResultNotificationRequest request);

    Optional<ResultNotificationRequest> findById(UUID id);

    List<ResultNotificationRequest> findByRecipientId(String recipientId);

    List<ResultNotificationRequest> findAll();
}
