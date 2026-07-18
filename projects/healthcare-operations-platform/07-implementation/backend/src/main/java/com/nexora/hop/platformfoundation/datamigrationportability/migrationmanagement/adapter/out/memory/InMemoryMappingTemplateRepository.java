package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.memory;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MappingTemplate;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MappingTemplateRepository;

@Repository
@Profile("!local")
class InMemoryMappingTemplateRepository implements MappingTemplateRepository {

    private final Map<String, MappingTemplate> templatesByImportBatchId = new ConcurrentHashMap<>();

    @Override
    public MappingTemplate save(MappingTemplate template) {
        templatesByImportBatchId.put(template.importBatchId(), template);
        return template;
    }

    @Override
    public Optional<MappingTemplate> findByImportBatchId(String importBatchId) {
        return Optional.ofNullable(templatesByImportBatchId.get(importBatchId));
    }
}
