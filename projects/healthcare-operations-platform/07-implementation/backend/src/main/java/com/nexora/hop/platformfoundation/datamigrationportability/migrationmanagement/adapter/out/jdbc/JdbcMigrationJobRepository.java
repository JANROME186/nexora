package com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationJob;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.MigrationJobRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcMigrationJobRepository implements MigrationJobRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcMigrationJobRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MigrationJob save(MigrationJob job) {
        jdbcTemplate.update("""
                insert into data_migration_portability.migration_jobs
                    (migration_job_id, tenant_id, laboratory_id, source_system_name, status,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (migration_job_id) do update set
                    status = excluded.status, updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                job.migrationJobId(), job.tenantId(), job.laboratoryId(), job.sourceSystemName(), job.status(),
                job.audit().createdBy(), Timestamp.valueOf(job.audit().createdAt()), job.audit().updatedBy(),
                Timestamp.valueOf(job.audit().updatedAt()));
        return job;
    }

    @Override
    public Optional<MigrationJob> findById(String migrationJobId) {
        return jdbcTemplate.query(SELECT_SQL + " where migration_job_id = ?", JdbcMigrationJobRepository::map,
                migrationJobId).stream().findFirst();
    }

    @Override
    public List<MigrationJob> findByTenantId(String tenantId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ?", JdbcMigrationJobRepository::map, tenantId);
    }

    private static final String SELECT_SQL = """
            select migration_job_id, tenant_id, laboratory_id, source_system_name, status,
                   created_by, created_at, updated_by, updated_at
            from data_migration_portability.migration_jobs
            """;

    private static MigrationJob map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new MigrationJob(
                resultSet.getString("migration_job_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("source_system_name"),
                resultSet.getString("status"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
