package com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.ExternalMessageEnvelope;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationMessageRecord;
import com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement.domain.IntegrationMessageRecordRepository;
import com.nexora.hop.platformfoundation.sharedkernel.DelimitedTextCodec;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcIntegrationMessageRecordRepository implements IntegrationMessageRecordRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcIntegrationMessageRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IntegrationMessageRecord save(IntegrationMessageRecord record) {
        jdbcTemplate.update("""
                insert into integration_interoperability.integration_message_records
                    (message_id, endpoint_id, external_message_id, correlation_id, source_protocol,
                     raw_payload_reference, received_at, message_type, canonical_fields_text,
                     target_bounded_context, normalization_status, canonical_error_code, retry_count,
                     next_retry_at, dead_letter_reason, created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (message_id) do update set
                    correlation_id = excluded.correlation_id, message_type = excluded.message_type,
                    canonical_fields_text = excluded.canonical_fields_text,
                    target_bounded_context = excluded.target_bounded_context,
                    normalization_status = excluded.normalization_status,
                    canonical_error_code = excluded.canonical_error_code, retry_count = excluded.retry_count,
                    next_retry_at = excluded.next_retry_at, dead_letter_reason = excluded.dead_letter_reason,
                    updated_by = excluded.updated_by, updated_at = excluded.updated_at
                """,
                record.messageId(), record.endpointId(), record.externalMessageId(), record.correlationId(),
                record.envelope().sourceProtocol(), record.envelope().rawPayloadReference(),
                Timestamp.from(record.envelope().receivedAt()), record.normalizedMessageType(),
                DelimitedTextCodec.joinStringMap(record.canonicalFields()), record.targetBoundedContext(),
                record.normalizationStatus(), record.canonicalErrorCode(), record.retryCount(),
                record.nextRetryAt() == null ? null : Timestamp.valueOf(record.nextRetryAt()),
                record.deadLetterReason(), record.audit().createdBy(), Timestamp.valueOf(record.audit().createdAt()),
                record.audit().updatedBy(), Timestamp.valueOf(record.audit().updatedAt()));
        return record;
    }

    @Override
    public Optional<IntegrationMessageRecord> findById(String messageId) {
        return jdbcTemplate.query(SELECT_SQL + " where message_id = ?",
                JdbcIntegrationMessageRecordRepository::map, messageId).stream().findFirst();
    }

    @Override
    public Optional<IntegrationMessageRecord> findByEndpointIdAndExternalMessageId(
            String endpointId, String externalMessageId) {
        return jdbcTemplate.query(SELECT_SQL + " where endpoint_id = ? and external_message_id = ?",
                JdbcIntegrationMessageRecordRepository::map, endpointId, externalMessageId).stream().findFirst();
    }

    @Override
    public List<IntegrationMessageRecord> findByEndpointId(String endpointId) {
        return jdbcTemplate.query(SELECT_SQL + " where endpoint_id = ?",
                JdbcIntegrationMessageRecordRepository::map, endpointId);
    }

    private static final String SELECT_SQL = """
            select message_id, endpoint_id, external_message_id, correlation_id, source_protocol,
                   raw_payload_reference, received_at, message_type, canonical_fields_text,
                   target_bounded_context, normalization_status, canonical_error_code, retry_count,
                   next_retry_at, dead_letter_reason, created_by, created_at, updated_by, updated_at
            from integration_interoperability.integration_message_records
            """;

    private static IntegrationMessageRecord map(ResultSet resultSet, int rowNumber) throws SQLException {
        Instant receivedAt = resultSet.getTimestamp("received_at").toInstant();
        ExternalMessageEnvelope envelope = new ExternalMessageEnvelope(
                resultSet.getString("source_protocol"), resultSet.getString("raw_payload_reference"), receivedAt);
        return new IntegrationMessageRecord(
                resultSet.getString("message_id"),
                resultSet.getString("endpoint_id"),
                resultSet.getString("external_message_id"),
                envelope,
                resultSet.getString("correlation_id"),
                resultSet.getString("message_type"),
                DelimitedTextCodec.splitStringMap(resultSet.getString("canonical_fields_text")),
                resultSet.getString("target_bounded_context"),
                resultSet.getString("normalization_status"),
                resultSet.getString("canonical_error_code"),
                resultSet.getInt("retry_count"),
                nullableLocalDateTime(resultSet, "next_retry_at"),
                resultSet.getString("dead_letter_reason"),
                new AuditMetadata(
                        resultSet.getString("created_by"), localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"), localDateTime(resultSet, "updated_at")));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }

    private static LocalDateTime nullableLocalDateTime(ResultSet resultSet, String columnName) throws SQLException {
        Timestamp timestamp = resultSet.getTimestamp(columnName);
        return timestamp == null ? null : timestamp.toLocalDateTime();
    }
}
