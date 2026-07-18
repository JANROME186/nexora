package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ReconciliationReport;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ReconciliationReportRepository;

@Repository
@Profile("!local")
class InMemoryReconciliationReportRepository implements ReconciliationReportRepository {

    private final Map<String, List<ReconciliationReport>> reportsByJobId = new ConcurrentHashMap<>();

    @Override
    public ReconciliationReport save(ReconciliationReport report) {
        reportsByJobId.computeIfAbsent(report.migrationJobId(), key -> new CopyOnWriteArrayList<>()).add(report);
        return report;
    }

    @Override
    public List<ReconciliationReport> findByMigrationJobId(String migrationJobId) {
        return List.copyOf(reportsByJobId.getOrDefault(migrationJobId, List.of()));
    }
}
