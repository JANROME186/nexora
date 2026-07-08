package com.nexora.hop.platformfoundation.auditcompliance;

/**
 * Public audit recording contract exposed by the audit-compliance module.
 * Other modules use it without depending on audit internals.
 */
public interface AuditRecorder {

    void recordSystemEvent(
            String tenantId,
            String action,
            String subjectType,
            String subjectId,
            String metadataJson);
}
