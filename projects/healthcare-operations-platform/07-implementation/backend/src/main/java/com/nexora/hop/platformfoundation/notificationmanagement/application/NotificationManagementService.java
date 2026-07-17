package com.nexora.hop.platformfoundation.notificationmanagement.application;

import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationProviderPort;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequest;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequestRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class NotificationManagementService {

    private final NotificationRequestRepository repository;
    private final NotificationProviderPort providerPort;

    public NotificationManagementService(NotificationRequestRepository repository, NotificationProviderPort providerPort) {
        this.repository = repository;
        this.providerPort = providerPort;
    }

    @SuppressWarnings("PMD.AvoidCatchingGenericException")
    public NotificationRequest submitNotificationRequest(
            TenantId tenantId,
            LaboratoryId laboratoryId,
            String recipientId,
            String recipientType,
            String channel,
            String templateReference,
            Map<String, String> parameters,
            AuditMetadata audit) {

        NotificationRequest.Channel targetChannel = parseChannel(channel);

        UUID notificationId = UUID.randomUUID();
        NotificationRequest request = new NotificationRequest(
                notificationId,
                tenantId,
                laboratoryId,
                recipientId,
                targetChannel,
                templateReference,
                parameters != null ? parameters.toString() : "",
                audit
        );

        repository.save(request);

        // Perform preference check and provider dispatch
        try {
            providerPort.dispatch(request);
            request.dispatch(audit);
        } catch (RuntimeException e) {
            request.fail(audit);
        }

        repository.save(request);
        return request;
    }

    public List<NotificationRequest> listAllRequests() {
        return repository.findAll();
    }

    private NotificationRequest.Channel parseChannel(String channel) {
        if (channel == null || channel.isBlank()) {
            return NotificationRequest.Channel.EMAIL;
        }
        try {
            return NotificationRequest.Channel.valueOf(channel.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return NotificationRequest.Channel.EMAIL;
        }
    }
}
