package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface ImportExecutionRepository {

    ImportExecution save(ImportExecution execution);

    Optional<ImportExecution> findLatestByMigrationJobId(String migrationJobId);

    List<ImportExecution> findByMigrationJobId(String migrationJobId);
}
