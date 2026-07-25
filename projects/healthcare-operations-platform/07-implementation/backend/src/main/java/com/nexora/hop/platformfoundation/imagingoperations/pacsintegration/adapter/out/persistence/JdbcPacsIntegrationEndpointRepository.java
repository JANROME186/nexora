package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.adapter.out.persistence;

import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.domain.PacsIntegrationEndpoint;
import com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.domain.PacsIntegrationEndpointRepository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@Profile("local")
public class JdbcPacsIntegrationEndpointRepository implements PacsIntegrationEndpointRepository {

    private static final String SELECT_SQL = """
            select endpoint_id, tenant_id, pacs_node_id, base_url, protocol, status,
                   auth_credentials_masked, created_by, created_at, updated_by, updated_at
            from imaging_operations.pacs_integration_endpoints
            """;

    private final JdbcTemplate jdbcTemplate;

    public JdbcPacsIntegrationEndpointRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PacsIntegrationEndpoint save(PacsIntegrationEndpoint endpoint) {
        jdbcTemplate.update("""
                insert into imaging_operations.pacs_integration_endpoints
                    (endpoint_id, tenant_id, pacs_node_id, base_url, protocol, status,
                     auth_credentials_masked, created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (endpoint_id) do update set
                    base_url = excluded.base_url, protocol = excluded.protocol,
                    status = excluded.status, auth_credentials_masked = excluded.auth_credentials_masked,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                endpoint.endpointId(), endpoint.tenantId(), endpoint.pacsNodeId(), endpoint.baseUrl(),
                endpoint.protocol(), endpoint.status(), endpoint.authCredentialsMasked(), endpoint.createdBy(),
                Timestamp.from(endpoint.createdAt()), endpoint.updatedBy(), Timestamp.from(endpoint.updatedAt()));
        return endpoint;
    }

    @Override
    public Optional<PacsIntegrationEndpoint> findById(String tenantId, String endpointId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and endpoint_id = ?",
                JdbcPacsIntegrationEndpointRepository::map, tenantId, endpointId).stream().findFirst();
    }

    @Override
    public Optional<PacsIntegrationEndpoint> findByPacsNodeId(String tenantId, String pacsNodeId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ? and lower(pacs_node_id) = lower(?)",
                JdbcPacsIntegrationEndpointRepository::map, tenantId, pacsNodeId).stream().findFirst();
    }

    @Override
    public List<PacsIntegrationEndpoint> findAllByTenant(String tenantId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ?",
                JdbcPacsIntegrationEndpointRepository::map, tenantId);
    }

    private static PacsIntegrationEndpoint map(ResultSet rs, int rowNum) throws SQLException {
        return new PacsIntegrationEndpoint(
                rs.getString("endpoint_id"),
                rs.getString("tenant_id"),
                rs.getString("pacs_node_id"),
                rs.getString("base_url"),
                rs.getString("protocol"),
                rs.getString("status"),
                rs.getString("auth_credentials_masked"),
                rs.getString("created_by"),
                rs.getTimestamp("created_at").toInstant(),
                rs.getString("updated_by"),
                rs.getTimestamp("updated_at").toInstant()
        );
    }
}
