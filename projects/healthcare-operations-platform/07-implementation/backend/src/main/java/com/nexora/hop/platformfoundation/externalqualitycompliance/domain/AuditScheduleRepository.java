package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuditScheduleRepository {
    AuditSchedule save(AuditSchedule audit);
    Optional<AuditSchedule> findById(UUID id);
    List<AuditSchedule> findAll(String category, String status);
}
