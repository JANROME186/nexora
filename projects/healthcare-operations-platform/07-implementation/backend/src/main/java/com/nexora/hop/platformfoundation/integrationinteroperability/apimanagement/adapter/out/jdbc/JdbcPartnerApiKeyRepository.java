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

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.PartnerApiKey;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.PartnerApiKeyRepository;
import com.nexora.hop.platformfoundation.sharedkernel.DelimitedTextCodec;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcPartnerApiKeyRepository implements PartnerApiKeyRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcPartnerApiKeyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public PartnerApiKey save(PartnerApiKey key) {
        jdbcTemplate.update("""
                insert into integration_interoperability.partner_api_keys
                    (key_id, tenant_id, consumer_name, granted_scopes_text, rate_limit_policy_ref, status,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (key_id) do update set
                    status = excluded.status, rate_limit_policy_ref = excluded.rate_limit_policy_ref,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                key.keyId(), key.tenantId(), key.consumerName(), DelimitedTextCodec.joinList(key.grantedScopes()),
                key.rateLimitPolicyRef(), key.status(), key.audit().createdBy(),
                Timestamp.valueOf(key.audit().createdAt()), key.audit().updatedBy(),
                Timestamp.valueOf(key.audit().updatedAt()));
        return key;
    }

    @Override
    public Optional<PartnerApiKey> findById(String keyId) {
        return jdbcTemplate.query(SELECT_SQL + " where key_id = ?", JdbcPartnerApiKeyRepository::map, keyId)
                .stream().findFirst();
    }

    @Override
    public List<PartnerApiKey> findByTenantId(String tenantId) {
        return jdbcTemplate.query(SELECT_SQL + " where tenant_id = ?", JdbcPartnerApiKeyRepository::map, tenantId);
    }

    private static final String SELECT_SQL = """
            select key_id, tenant_id, consumer_name, granted_scopes_text, rate_limit_policy_ref, status,
                   created_by, created_at, updated_by, updated_at
            from integration_interoperability.partner_api_keys
            """;

    private static PartnerApiKey map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new PartnerApiKey(
                resultSet.getString("key_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("consumer_name"),
                DelimitedTextCodec.splitList(resultSet.getString("granted_scopes_text")),
                resultSet.getString("rate_limit_policy_ref"),
                resultSet.getString("status"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
