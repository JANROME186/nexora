package com.nexora.hop.platformfoundation.notificationmanagement.domain;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

public interface NotificationRequestRepository {

    NotificationRequest save(NotificationRequest request);

    Optional<NotificationRequest> findById(UUID notificationId);

    List<NotificationRequest> findByRecipientId(String recipientId);

    List<NotificationRequest> findAll();
}
