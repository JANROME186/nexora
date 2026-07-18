package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.List;

public interface ReconciliationReportRepository {

    ReconciliationReport save(ReconciliationReport report);

    List<ReconciliationReport> findByMigrationJobId(String migrationJobId);
}
