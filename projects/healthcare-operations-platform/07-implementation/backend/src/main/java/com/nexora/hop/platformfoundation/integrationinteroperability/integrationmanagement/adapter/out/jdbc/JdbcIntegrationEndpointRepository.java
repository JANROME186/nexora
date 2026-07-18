package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationEndpoint;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationEndpointRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcIntegrationEndpointRepository implements IntegrationEndpointRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcIntegrationEndpointRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IntegrationEndpoint save(IntegrationEndpoint endpoint) {
        jdbcTemplate.update("""
                insert into integration_interoperability.integration_endpoints
                    (endpoint_id, tenant_id, laboratory_id, endpoint_name, protocol, direction, status,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (endpoint_id) do update set
                    endpoint_name = excluded.endpoint_name, status = excluded.status,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                endpoint.endpointId(), endpoint.tenantId(), endpoint.laboratoryId(), endpoint.endpointName(),
                endpoint.protocol(), endpoint.direction(), endpoint.status(), endpoint.audit().createdBy(),
                Timestamp.valueOf(endpoint.audit().createdAt()), endpoint.audit().updatedBy(),
                Timestamp.valueOf(endpoint.audit().updatedAt()));
        return endpoint;
    }

    @Override
    public Optional<IntegrationEndpoint> findById(String endpointId) {
        return jdbcTemplate.query(SELECT_SQL + " where endpoint_id = ?", JdbcIntegrationEndpointRepository::map,
                endpointId).stream().findFirst();
    }

    @Override
    public List<IntegrationEndpoint> findByTenantId(String tenantId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ?", JdbcIntegrationEndpointRepository::map,
                tenantId);
    }

    private static final String SELECT_SQL = """
            select endpoint_id, tenant_id, laboratory_id, endpoint_name, protocol, direction, status,
                   created_by, created_at, updated_by, updated_at
            from integration_interoperability.integration_endpoints
            """;

    private static IntegrationEndpoint map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new IntegrationEndpoint(
                resultSet.getString("endpoint_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("laboratory_id"),
                resultSet.getString("endpoint_name"),
                resultSet.getString("protocol"),
                resultSet.getString("direction"),
                resultSet.getString("status"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
