package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicy;
import com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain.RateLimitPolicyRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcRateLimitPolicyRepository implements RateLimitPolicyRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcRateLimitPolicyRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RateLimitPolicy save(RateLimitPolicy policy) {
        jdbcTemplate.update("""
                insert into integration_interoperability.rate_limit_policies
                    (policy_id, classification, requests_per_minute, consumer_identification_method,
                     created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (classification) do update set
                    requests_per_minute = excluded.requests_per_minute,
                    consumer_identification_method = excluded.consumer_identification_method,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                policy.policyId(), policy.classification(), policy.requestsPerMinute(),
                policy.consumerIdentificationMethod(), policy.audit().createdBy(),
                Timestamp.valueOf(policy.audit().createdAt()), policy.audit().updatedBy(),
                Timestamp.valueOf(policy.audit().updatedAt()));
        return policy;
    }

    @Override
    public Optional<RateLimitPolicy> findByClassification(String classification) {
        return jdbcTemplate.query("""
                select policy_id, classification, requests_per_minute, consumer_identification_method,
                       created_by, created_at, updated_by, updated_at
                from integration_interoperability.rate_limit_policies
                where classification = ?
                """, JdbcRateLimitPolicyRepository::map, classification).stream().findFirst();
    }

    @Override
    public Optional<RateLimitPolicy> findById(String policyId) {
        return jdbcTemplate.query("""
                select policy_id, classification, requests_per_minute, consumer_identification_method,
                       created_by, created_at, updated_by, updated_at
                from integration_interoperability.rate_limit_policies
                where policy_id = ?
                """, JdbcRateLimitPolicyRepository::map, policyId).stream().findFirst();
    }

    private static RateLimitPolicy map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new RateLimitPolicy(
                resultSet.getString("policy_id"),
                resultSet.getString("classification"),
                resultSet.getInt("requests_per_minute"),
                resultSet.getString("consumer_identification_method"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
