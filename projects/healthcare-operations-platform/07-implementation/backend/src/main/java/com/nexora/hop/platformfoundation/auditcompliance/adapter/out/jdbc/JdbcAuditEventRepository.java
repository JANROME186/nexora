package com.nexora.hop.platformfoundation.auditcompliance.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEvent;
import com.nexora.hop.platformfoundation.auditcompliance.domain.AuditEventRepository;

@Repository
@Profile("local")
class JdbcAuditEventRepository implements AuditEventRepository {

    private final JdbcTemplate jdbcTemplate;

    JdbcAuditEventRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AuditEvent append(AuditEvent event) {
        jdbcTemplate.update("""
                insert into audit.audit_events (
                    audit_event_id, occurred_at, tenant_id, actor_id, actor_type,
                    action, subject_type, subject_id, metadata_json
                )
                values (?, ?, ?, ?, ?, ?, ?, ?, ?::jsonb)
                """,
                event.auditEventId(),
                Timestamp.from(event.occurredAt()),
                event.tenantId(),
                event.actorId(),
                event.actorType(),
                event.action(),
                event.subjectType(),
                event.subjectId(),
                event.metadataJson());
        return event;
    }

    @Override
    public List<AuditEvent> search(String tenantId, String subjectId) {
        StringBuilder sql = new StringBuilder("""
                select audit_event_id, occurred_at, tenant_id, actor_id, actor_type,
                       action, subject_type, subject_id, metadata_json::text as metadata_json
                from audit.audit_events
                where 1 = 1
                """);
        List<Object> parameters = new ArrayList<>();
        if (tenantId != null) {
            sql.append(" and tenant_id = ?");
            parameters.add(tenantId);
        }
        if (subjectId != null) {
            sql.append(" and subject_id = ?");
            parameters.add(subjectId);
        }
        sql.append(" order by occurred_at asc");
        return jdbcTemplate.query(sql.toString(), JdbcAuditEventRepository::mapEvent, parameters.toArray());
    }

    private static AuditEvent mapEvent(ResultSet resultSet, int rowNumber) throws SQLException {
        return new AuditEvent(
                resultSet.getString("audit_event_id"),
                instant(resultSet, "occurred_at"),
                resultSet.getString("tenant_id"),
                resultSet.getString("actor_id"),
                resultSet.getString("actor_type"),
                resultSet.getString("action"),
                resultSet.getString("subject_type"),
                resultSet.getString("subject_id"),
                resultSet.getString("metadata_json"));
    }

    private static Instant instant(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toInstant();
    }
}
