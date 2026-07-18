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

import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportExecution;
import com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement.domain.ImportExecutionRepository;
import com.nexora.hop.platformfoundation.sharedkernel.DelimitedTextCodec;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcImportExecutionRepository implements ImportExecutionRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcImportExecutionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ImportExecution save(ImportExecution execution) {
        jdbcTemplate.update("""
                insert into data_migration_portability.import_executions
                    (execution_id, migration_job_id, import_batch_id, attempt_number,
                     domain_commands_invoked_text, checkpoint, status, created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (execution_id) do update set
                    status = excluded.status, checkpoint = excluded.checkpoint,
                    domain_commands_invoked_text = excluded.domain_commands_invoked_text,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                execution.executionId(), execution.migrationJobId(), execution.importBatchId(),
                execution.attemptNumber(), DelimitedTextCodec.joinList(execution.domainCommandsInvoked()),
                execution.checkpoint(), execution.status(), execution.audit().createdBy(),
                Timestamp.valueOf(execution.audit().createdAt()), execution.audit().updatedBy(),
                Timestamp.valueOf(execution.audit().updatedAt()));
        return execution;
    }

    @Override
    public Optional<ImportExecution> findLatestByMigrationJobId(String migrationJobId) {
        return jdbcTemplate.query(SELECT_SQL + " where migration_job_id = ? order by attempt_number desc limit 1",
                JdbcImportExecutionRepository::map, migrationJobId).stream().findFirst();
    }

    @Override
    public List<ImportExecution> findByMigrationJobId(String migrationJobId) {
        return jdbcTemplate.query(SELECT_SQL + " where migration_job_id = ? order by attempt_number",
                JdbcImportExecutionRepository::map, migrationJobId);
    }

    private static final String SELECT_SQL = """
            select execution_id, migration_job_id, import_batch_id, attempt_number,
                   domain_commands_invoked_text, checkpoint, status, created_by, created_at, updated_by, updated_at
            from data_migration_portability.import_executions
            """;

    private static ImportExecution map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new ImportExecution(
                resultSet.getString("execution_id"),
                resultSet.getString("migration_job_id"),
                resultSet.getString("import_batch_id"),
                resultSet.getInt("attempt_number"),
                DelimitedTextCodec.splitList(resultSet.getString("domain_commands_invoked_text")),
                resultSet.getString("checkpoint"),
                resultSet.getString("status"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
