package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain;

import java.util.Map;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/** Entity of BCM-PLT-010 (ENT-MIG-003): the anti-corruption field-mapping applied to an {@link ImportBatch}. */
public record MappingTemplate(
        String mappingTemplateId,
        String importBatchId,
        Map<String, String> fieldMappings,
        Map<String, String> codeDictionaries,
        AuditMetadata audit) {
}
