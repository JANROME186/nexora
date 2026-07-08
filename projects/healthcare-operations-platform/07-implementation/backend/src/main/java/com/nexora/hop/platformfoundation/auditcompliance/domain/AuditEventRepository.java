package com.nexora.hop.platformfoundation.auditcompliance.domain;

import java.util.List;

public interface AuditEventRepository {

    AuditEvent append(AuditEvent event);

    List<AuditEvent> search(String tenantId, String subjectId);
}
