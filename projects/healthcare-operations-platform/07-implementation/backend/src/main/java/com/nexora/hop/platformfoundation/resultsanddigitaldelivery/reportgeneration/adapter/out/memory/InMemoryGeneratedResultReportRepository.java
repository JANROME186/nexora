package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.adapter.out.memory;

import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.domain.GeneratedResultReport;
import com.nexora.hop.platformfoundation.resultsanddigitaldelivery.reportgeneration.domain.GeneratedResultReportRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryGeneratedResultReportRepository implements GeneratedResultReportRepository {

    private final Map<UUID, GeneratedResultReport> store = new ConcurrentHashMap<>();

    @Override
    public GeneratedResultReport save(GeneratedResultReport report) {
        store.put(report.getReportId(), report);
        return report;
    }

    @Override
    public Optional<GeneratedResultReport> findById(UUID reportId) {
        return Optional.ofNullable(store.get(reportId));
    }

    @Override
    public List<GeneratedResultReport> findByResultId(ResultId resultId, TenantId tenantId) {
        List<GeneratedResultReport> results = new ArrayList<>();
        for (GeneratedResultReport report : store.values()) {
            if (report.getResultId().equals(resultId) && report.getTenantId().equals(tenantId)) {
                results.add(report);
            }
        }
        return results;
    }
}
