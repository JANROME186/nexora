package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportBatch;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportBatchRepository;

@Repository
@Profile("!local")
class InMemoryImportBatchRepository implements ImportBatchRepository {

    private final Map<String, ImportBatch> batches = new ConcurrentHashMap<>();

    @Override
    public ImportBatch save(ImportBatch batch) {
        batches.put(batch.importBatchId(), batch);
        return batch;
    }

    @Override
    public Optional<ImportBatch> findById(String importBatchId) {
        return Optional.ofNullable(batches.get(importBatchId));
    }

    @Override
    public List<ImportBatch> findByMigrationJobId(String migrationJobId) {
        return batches.values().stream().filter(batch -> batch.migrationJobId().equals(migrationJobId)).toList();
    }
}
