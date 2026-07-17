package com.nexora.hop.platformfoundation.notificationmanagement.application;

import com.nexora.hop.platformfoundation.notificationmanagement.adapter.out.local.LocalDeterministicNotificationProvider;
import com.nexora.hop.platformfoundation.notificationmanagement.adapter.out.memory.InMemoryNotificationRequestRepository;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationProviderPort;
import com.nexora.hop.platformfoundation.notificationmanagement.domain.NotificationRequest;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificationManagementServiceTest {

    private InMemoryNotificationRequestRepository repository;
    private NotificationProviderPort providerPort;
    private NotificationManagementService service;

    @BeforeEach
    void setUp() {
        repository = new InMemoryNotificationRequestRepository();
        providerPort = new LocalDeterministicNotificationProvider();
        service = new NotificationManagementService(repository, providerPort);
    }

    @Test
    void shouldSubmitAndDispatchNotificationSuccessfully() {
        TenantId tenantId = new TenantId("tenant-123");
        LaboratoryId labId = new LaboratoryId("lab-456");
        AuditMetadata audit = new AuditMetadata("test-user", LocalDateTime.now(), "test-user", LocalDateTime.now());

        NotificationRequest request = service.submitNotificationRequest(
                tenantId,
                labId,
                "recipient-789",
                "patient",
                "sms",
                "tpl_test",
                Map.of("param1", "value1"),
                audit
        );

        assertNotNull(request);
        assertEquals(NotificationRequest.Status.DISPATCHED, request.getStatus());
        assertEquals("recipient-789", request.getRecipientAddress());
        assertEquals(NotificationRequest.Channel.SMS, request.getChannel());

        // Verify stored in repository
        assertTrue(repository.findById(request.getNotificationId()).isPresent());
    }

    @Test
    void shouldTransitionToFailedStatusIfDispatchThrowsException() {
        // Provider throws exception on dispatch
        NotificationProviderPort failingProvider = req -> {
            throw new RuntimeException("Simulated provider failure");
        };
        service = new NotificationManagementService(repository, failingProvider);

        TenantId tenantId = new TenantId("tenant-123");
        LaboratoryId labId = new LaboratoryId("lab-456");
        AuditMetadata audit = new AuditMetadata("test-user", LocalDateTime.now(), "test-user", LocalDateTime.now());

        NotificationRequest request = service.submitNotificationRequest(
                tenantId,
                labId,
                "recipient-789",
                "patient",
                "sms",
                "tpl_test",
                Map.of("param1", "value1"),
                audit
        );

        assertNotNull(request);
        assertEquals(NotificationRequest.Status.FAILED, request.getStatus());
    }

    @Test
    void shouldDefaultToEmailWhenChannelIsMissingOrInvalid() {
        TenantId tenantId = new TenantId("tenant-123");
        LaboratoryId labId = new LaboratoryId("lab-456");
        AuditMetadata audit = new AuditMetadata("test-user", LocalDateTime.now(), "test-user", LocalDateTime.now());

        NotificationRequest missingChannel = service.submitNotificationRequest(
                tenantId,
                labId,
                "recipient-789",
                "patient",
                null,
                "tpl_missing_channel",
                null,
                audit
        );
        NotificationRequest invalidChannel = service.submitNotificationRequest(
                tenantId,
                labId,
                "recipient-790",
                "patient",
                "unsupported-channel",
                "tpl_invalid_channel",
                Map.of("locale", "es-MX"),
                audit
        );

        assertEquals(NotificationRequest.Channel.EMAIL, missingChannel.getChannel());
        assertEquals(NotificationRequest.Channel.EMAIL, invalidChannel.getChannel());
        assertEquals(2, service.listAllRequests().size());
    }
}
