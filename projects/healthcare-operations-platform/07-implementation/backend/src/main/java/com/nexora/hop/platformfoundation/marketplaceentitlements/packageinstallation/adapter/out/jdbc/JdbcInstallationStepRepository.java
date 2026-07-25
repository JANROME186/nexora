package com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.InstallationStep;
import com.nexora.hop.platformfoundation.marketplaceentitlements.packageinstallation.domain.InstallationStepRepository;

@Repository
@Profile("local")
class JdbcInstallationStepRepository implements InstallationStepRepository {

    private static final String SELECT_SQL = """
            select step_id, installation_id, tenant_id, step_type, from_version, to_version,
                   from_status, to_status, actor_id, occurred_at
            from marketplace_entitlements.installation_steps
            """;

    private final JdbcTemplate jdbcTemplate;

    JdbcInstallationStepRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public InstallationStep save(InstallationStep step) {
        jdbcTemplate.update("""
                insert into marketplace_entitlements.installation_steps
                    (step_id, installation_id, tenant_id, step_type, from_version, to_version,
                     from_status, to_status, actor_id, occurred_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (step_id) do nothing
                """,
                step.stepId(), step.installationId(), step.tenantId(), step.stepType(), step.fromVersion(),
                step.toVersion(), step.fromStatus(), step.toStatus(), step.actorId(),
                Timestamp.valueOf(step.occurredAt()));
        return step;
    }

    @Override
    public List<InstallationStep> findByInstallationIdOrderByOccurredAt(String installationId) {
        return jdbcTemplate.query(SELECT_SQL + " where installation_id = ? order by occurred_at asc",
                JdbcInstallationStepRepository::map, installationId);
    }

    private static InstallationStep map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new InstallationStep(
                resultSet.getString("step_id"),
                resultSet.getString("installation_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("step_type"),
                resultSet.getString("from_version"),
                resultSet.getString("to_version"),
                resultSet.getString("from_status"),
                resultSet.getString("to_status"),
                resultSet.getString("actor_id"),
                localDateTime(resultSet, "occurred_at"));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
