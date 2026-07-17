package com.nexora.hop.platformfoundation.notificationmanagement.domain;

public interface NotificationProviderPort {
    void dispatch(NotificationRequest request);
}
