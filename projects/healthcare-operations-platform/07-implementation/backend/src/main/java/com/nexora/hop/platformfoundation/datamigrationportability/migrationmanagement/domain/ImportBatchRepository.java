package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface ImportBatchRepository {

    ImportBatch save(ImportBatch batch);

    Optional<ImportBatch> findById(String importBatchId);

    List<ImportBatch> findByMigrationJobId(String migrationJobId);
}
