package com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain;

import java.util.List;
import java.util.Optional;

public interface RadiologyReportRepository {
    RadiologyReport save(RadiologyReport report);
    Optional<RadiologyReport> findById(String tenantId, String reportId);
    List<RadiologyReport> findByStudyId(String tenantId, String studyId);
}
