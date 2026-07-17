package com.nexora.hop.platformfoundation.notificationmanagement.domain;

@FunctionalInterface
public interface NotificationProviderPort {
    void dispatch(NotificationRequest request);
}
