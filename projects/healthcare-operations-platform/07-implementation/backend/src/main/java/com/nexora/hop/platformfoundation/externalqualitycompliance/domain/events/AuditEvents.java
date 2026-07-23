package com.nexora.hop.platformfoundation.externalqualitycompliance.domain.events;

import java.time.Instant;
import java.util.UUID;

public class AuditEvents {

    public record AuditScheduleCreatedEvent(
            UUID auditId,
            String auditCode,
            String title,
            String category,
            UUID leadAuditorId,
            Instant occurredAt
    ) {}

    public record AuditFindingRecordedEvent(
            UUID auditId,
            String auditCode,
            UUID findingId,
            String severity,
            UUID capaId,
            Instant occurredAt
    ) {}

    public record AuditScheduleClosedEvent(
            UUID auditId,
            String auditCode,
            Instant occurredAt
    ) {}
}
