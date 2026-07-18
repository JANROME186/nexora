package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.memory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportValidationReport;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportValidationReportRepository;

@Repository
@Profile("!local")
class InMemoryImportValidationReportRepository implements ImportValidationReportRepository {

    private final Map<String, ImportValidationReport> latestByImportBatchId = new ConcurrentHashMap<>();

    @Override
    public ImportValidationReport save(ImportValidationReport report) {
        latestByImportBatchId.put(report.importBatchId(), report);
        return report;
    }

    @Override
    public Optional<ImportValidationReport> findLatestByImportBatchId(String importBatchId) {
        return Optional.ofNullable(latestByImportBatchId.get(importBatchId));
    }
}
