package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.List;
import java.util.Optional;

public interface MigrationJobRepository {

    MigrationJob save(MigrationJob job);

    Optional<MigrationJob> findById(String migrationJobId);

    List<MigrationJob> findByTenantId(String tenantId);
}
