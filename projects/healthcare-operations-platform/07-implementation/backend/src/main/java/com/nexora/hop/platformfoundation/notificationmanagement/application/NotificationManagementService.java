package com.nexora.hop.platformfoundation.notificationmanagement.application;

import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationProviderPort;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequest;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequestRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.Map;
import java.util.List;

@Service
public class NotificationManagementService {

    private final NotificationRequestRepository repository;
    private final NotificationProviderPort providerPort;

    public NotificationManagementService(NotificationRequestRepository repository, NotificationProviderPort providerPort) {
        this.repository = repository;
        this.providerPort = providerPort;
    }

    public NotificationRequest submitNotificationRequest(
            TenantId tenantId,
            LaboratoryId laboratoryId,
            String recipientId,
            String recipientType,
            String channel,
            String templateReference,
            Map<String, String> parameters,
            AuditMetadata audit) {

        NotificationRequest.Channel targetChannel;
        try {
            targetChannel = NotificationRequest.Channel.valueOf(channel.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            targetChannel = NotificationRequest.Channel.EMAIL; // default fallback
        }

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
        } catch (Exception e) {
            request.fail(audit);
        }

        repository.save(request);
        return request;
    }

    public List<NotificationRequest> listAllRequests() {
        return repository.findAll();
    }
}
