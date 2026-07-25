package com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain.RadiologyReport;
import com.nexora.hop.platformfoundation.imagingoperations.radiologysignature.domain.RadiologyReportRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("!local")
public class InMemoryRadiologyReportRepository implements RadiologyReportRepository {

    private final Map<String, RadiologyReport> store = new ConcurrentHashMap<>();

    @Override
    public RadiologyReport save(RadiologyReport report) {
        store.put(report.tenantId() + ":" + report.reportId(), report);
        return report;
    }

    @Override
    public Optional<RadiologyReport> findById(String tenantId, String reportId) {
        return Optional.ofNullable(store.get(tenantId + ":" + reportId));
    }

    @Override
    public List<RadiologyReport> findByStudyId(String tenantId, String studyId) {
        return store.values().stream()
                .filter(r -> r.tenantId().equals(tenantId) && r.studyId().equals(studyId))
                .toList();
    }
}
