package com.nexora.hop.platformfoundation.notificationmanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDomainTest {

    @Test
    void notificationRequestStartsQueuedAndCanBeDispatched() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        UUID notificationId = UUID.randomUUID();
        NotificationRequest request = new NotificationRequest(notificationId, new TenantId("t1"), new LaboratoryId("l1"),
                "recipient", NotificationRequest.Channel.SMS, "template", "payload", audit);

        assertEquals(notificationId, request.getNotificationId());
        assertEquals(new TenantId("t1"), request.getTenantId());
        assertEquals(new LaboratoryId("l1"), request.getLaboratoryId());
        assertEquals("recipient", request.getRecipientAddress());
        assertEquals(NotificationRequest.Channel.SMS, request.getChannel());
        assertEquals("template", request.getSubject());
        assertEquals("payload", request.getContent());
        assertEquals(NotificationRequest.Status.QUEUED, request.getStatus());
        assertNotNull(request.getRequestedAt());
        assertNull(request.getDispatchedAt());
        assertEquals(audit, request.getAudit());

        AuditMetadata dispatchAudit = new AuditMetadata("dispatcher", LocalDateTime.now(), "dispatcher", LocalDateTime.now());
        request.dispatch(dispatchAudit);

        assertEquals(NotificationRequest.Status.DISPATCHED, request.getStatus());
        assertNotNull(request.getDispatchedAt());
        assertEquals(dispatchAudit, request.getAudit());
    }

    @Test
    void dispatchedNotificationCannotBeDispatchedTwiceButCanFail() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        NotificationRequest request = new NotificationRequest(UUID.randomUUID(), new TenantId("t1"), new LaboratoryId("l1"),
                "recipient", NotificationRequest.Channel.EMAIL, "subject", "content", audit);
        AuditMetadata updateAudit = new AuditMetadata("dispatcher", LocalDateTime.now(), "dispatcher", LocalDateTime.now());

        request.dispatch(updateAudit);

        assertThrows(IllegalStateException.class, () -> request.dispatch(updateAudit));
        request.fail(updateAudit);
        assertEquals(NotificationRequest.Status.FAILED, request.getStatus());
        assertEquals(updateAudit, request.getAudit());
    }
}
