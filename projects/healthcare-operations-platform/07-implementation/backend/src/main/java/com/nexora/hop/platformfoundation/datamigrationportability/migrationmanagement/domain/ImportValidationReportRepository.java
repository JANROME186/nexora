package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.Optional;

public interface ImportValidationReportRepository {

    ImportValidationReport save(ImportValidationReport report);

    Optional<ImportValidationReport> findLatestByImportBatchId(String importBatchId);
}
