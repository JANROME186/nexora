package com.nexora.hop.platformfoundation.notificationmanagement.adapter.out.local;

import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationProviderPort;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequest;
import org.springframework.stereotype.Component;

@Component
public class LocalDeterministicNotificationProvider implements NotificationProviderPort {

    @Override
    public void dispatch(NotificationRequest request) {
        // System log simulating successful provider dispatch
        System.out.println("Dispatched notification " + request.getNotificationId() + " to " + request.getRecipientAddress() + " via " + request.getChannel());
    }
}
