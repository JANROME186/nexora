package com.nexora.hop.platformfoundation.notificationmanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NotificationDomainTest {

    @Test
    void testNotificationRequest() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        NotificationRequest r1 = new NotificationRequest(UUID.randomUUID(), new TenantId("t1"), new LaboratoryId("l1"), "recipient", NotificationRequest.Channel.SMS, "template", "payload", audit);
        assertNotNull(r1.toString());
        assertEquals(r1, r1);
        assertNotNull(r1.hashCode());
    }
}
