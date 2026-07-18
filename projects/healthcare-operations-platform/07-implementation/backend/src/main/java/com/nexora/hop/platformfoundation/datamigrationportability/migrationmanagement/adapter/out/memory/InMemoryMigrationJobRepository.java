package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationJob;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationJobRepository;

@Repository
@Profile("!local")
class InMemoryMigrationJobRepository implements MigrationJobRepository {

    private final Map<String, MigrationJob> jobs = new ConcurrentHashMap<>();

    @Override
    public MigrationJob save(MigrationJob job) {
        jobs.put(job.migrationJobId(), job);
        return job;
    }

    @Override
    public Optional<MigrationJob> findById(String migrationJobId) {
        return Optional.ofNullable(jobs.get(migrationJobId));
    }

    @Override
    public List<MigrationJob> findByTenantId(String tenantId) {
        return jobs.values().stream().filter(job -> job.tenantId().equals(tenantId)).toList();
    }
}
