package com.nexora.hop.platformfoundation.auditcompliance.application;

public record RecordAuditEventCommand(
        String tenantId,
        String actorId,
        String actorType,
        String action,
        String subjectType,
        String subjectId,
        String metadataJson) {
}
