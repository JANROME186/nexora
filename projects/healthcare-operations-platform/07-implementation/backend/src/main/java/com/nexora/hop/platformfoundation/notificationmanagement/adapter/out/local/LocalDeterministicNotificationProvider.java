package com.nexora.hop.platformfoundation.notificationmanagement.adapter.out.local;

import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationProviderPort;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LocalDeterministicNotificationProvider implements NotificationProviderPort {
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalDeterministicNotificationProvider.class);

    @Override
    public void dispatch(NotificationRequest request) {
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info(
                    "Dispatched notification {} to {} via {}",
                    sanitizeLogValue(request.getNotificationId()),
                    sanitizeLogValue(request.getRecipientAddress()),
                    sanitizeLogValue(request.getChannel()));
        }
    }

    private static String sanitizeLogValue(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString().replace('\r', '_').replace('\n', '_');
    }
}
