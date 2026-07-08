package com.nexora.hop.platformfoundation.auditcompliance.domain;

import java.time.Instant;

public record AuditEvent(
        String auditEventId,
        Instant occurredAt,
        String tenantId,
        String actorId,
        String actorType,
        String action,
        String subjectType,
        String subjectId,
        String metadataJson) {
}
