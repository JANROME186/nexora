package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.memory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportExecution;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportExecutionRepository;

@Repository
@Profile("!local")
class InMemoryImportExecutionRepository implements ImportExecutionRepository {

    private final Map<String, List<ImportExecution>> executionsByJobId = new ConcurrentHashMap<>();

    @Override
    public ImportExecution save(ImportExecution execution) {
        List<ImportExecution> existing = executionsByJobId.computeIfAbsent(
                execution.migrationJobId(), key -> new CopyOnWriteArrayList<>());
        existing.removeIf(candidate -> candidate.executionId().equals(execution.executionId()));
        existing.add(execution);
        return execution;
    }

    @Override
    public Optional<ImportExecution> findLatestByMigrationJobId(String migrationJobId) {
        return executionsByJobId.getOrDefault(migrationJobId, List.of()).stream()
                .max(Comparator.comparingInt(ImportExecution::attemptNumber));
    }

    @Override
    public List<ImportExecution> findByMigrationJobId(String migrationJobId) {
        return List.copyOf(executionsByJobId.getOrDefault(migrationJobId, List.of()));
    }
}
