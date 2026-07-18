package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.Optional;

public interface MappingTemplateRepository {

    MappingTemplate save(MappingTemplate template);

    Optional<MappingTemplate> findByImportBatchId(String importBatchId);
}
