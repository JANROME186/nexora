package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.ApiSurfaceRegistration;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.ApiSurfaceRegistrationRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcApiSurfaceRegistrationRepository implements ApiSurfaceRegistrationRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcApiSurfaceRegistrationRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ApiSurfaceRegistration save(ApiSurfaceRegistration registration) {
        jdbcTemplate.update("""
                insert into integration_interoperability.api_surface_registrations
                    (registration_id, tenant_id, owner_capability, operation_id, classification, api_version,
                     deprecation_status, deprecation_window_from, deprecation_window_to, migration_note,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (operation_id) do update set
                    classification = excluded.classification, api_version = excluded.api_version,
                    deprecation_status = excluded.deprecation_status,
                    deprecation_window_from = excluded.deprecation_window_from,
                    deprecation_window_to = excluded.deprecation_window_to,
                    migration_note = excluded.migration_note, updated_by = excluded.updated_by,
                    updated_at = excluded.updated_at
                """,
                registration.registrationId(), registration.tenantId(), registration.ownerCapability(),
                registration.operationId(), registration.classification(), registration.apiVersion(),
                registration.deprecationStatus(), nullableTimestamp(registration.deprecationWindowFrom()),
                nullableTimestamp(registration.deprecationWindowTo()), registration.migrationNote(),
                registration.audit().createdBy(), Timestamp.valueOf(registration.audit().createdAt()),
                registration.audit().updatedBy(), Timestamp.valueOf(registration.audit().updatedAt()));
        return registration;
    }

    @Override
    public Optional<ApiSurfaceRegistration> findByOperationId(String operationId) {
        return jdbcTemplate.query(SELECT_SQL + " where operation_id = ?",
                JdbcApiSurfaceRegistrationRepository::map, operationId).stream().findFirst();
    }

    @Override
    public List<ApiSurfaceRegistration> findAll() {
        return jdbcTemplate.query(SELECT_SQL, JdbcApiSurfaceRegistrationRepository::map);
    }

    private static final String SELECT_SQL = """
            select registration_id, tenant_id, owner_capability, operation_id, classification, api_version,
                   deprecation_status, deprecation_window_from, deprecation_window_to, migration_note,
                   created_by, created_at, updated_by, updated_at
            from integration_interoperability.api_surface_registrations
            """;

    private static ApiSurfaceRegistration map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new ApiSurfaceRegistration(
                resultSet.getString("registration_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("owner_capability"),
                resultSet.getString("operation_id"),
                resultSet.getString("classification"),
                resultSet.getString("api_version"),
                resultSet.getString("deprecation_status"),
                nullableLocalDateTime(resultSet, "deprecation_window_from"),
                nullableLocalDateTime(resultSet, "deprecation_window_to"),
                resultSet.getString("migration_note"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static Timestamp nullableTimestamp(LocalDateTime value) {
        return value == null ? null : Timestamp.valueOf(value);
    }

    private static LocalDateTime nullableLocalDateTime(ResultSet resultSet, String columnName) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columnName);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
