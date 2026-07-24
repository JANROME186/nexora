package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.QualityEventIntake;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.QualityEventIntakeRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("local")
public class JdbcQualityEventIntakeRepository implements QualityEventIntakeRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcQualityEventIntakeRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public QualityEventIntake save(QualityEventIntake event) {
        String sql = """
                INSERT INTO quality_event_intakes (
                    event_id, tenant_id, source_system, event_type, severity, title,
                    description, payload_json, capa_id, ingested_at, created_by, created_at
                ) VALUES (
                    :eventId, :tenantId, :sourceSystem, :eventType, :severity, :title,
                    :description, :payloadJson, :capaId, :ingestedAt, :createdBy, :createdAt
                ) ON CONFLICT (event_id) DO UPDATE SET
                    capa_id = EXCLUDED.capa_id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("eventId", event.getEventId().toString())
                .addValue("tenantId", event.getTenantId() != null ? event.getTenantId().value() : null)
                .addValue("sourceSystem", event.getSourceSystem())
                .addValue("eventType", event.getEventType())
                .addValue("severity", event.getSeverity())
                .addValue("title", event.getTitle())
                .addValue("description", event.getDescription())
                .addValue("payloadJson", event.getPayloadJson())
                .addValue("capaId", event.getCapaId() != null ? event.getCapaId().toString() : null)
                .addValue("ingestedAt", java.sql.Timestamp.from(event.getIngestedAt()))
                .addValue("createdBy", event.getAudit() != null ? event.getAudit().createdBy() : "system")
                .addValue("createdAt", event.getAudit() != null && event.getAudit().createdAt() != null ? java.sql.Timestamp.valueOf(event.getAudit().createdAt()) : java.sql.Timestamp.from(Instant.now()));

        jdbcTemplate.update(sql, params);
        return event;
    }

    @Override
    public Optional<QualityEventIntake> findById(UUID id) {
        String sql = "SELECT * FROM quality_event_intakes WHERE event_id = :id";
        List<QualityEventIntake> list = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id.toString()), this::mapRow);
        return list.stream().findFirst();
    }

    @Override
    public List<QualityEventIntake> findAll(String sourceSystem, String severity) {
        StringBuilder sql = new StringBuilder("SELECT * FROM quality_event_intakes WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (sourceSystem != null && !sourceSystem.isBlank()) {
            sql.append(" AND LOWER(source_system) = LOWER(:sourceSystem)");
            params.addValue("sourceSystem", sourceSystem.trim());
        }
        if (severity != null && !severity.isBlank()) {
            sql.append(" AND LOWER(severity) = LOWER(:severity)");
            params.addValue("severity", severity.trim());
        }

        return jdbcTemplate.query(sql.toString(), params, this::mapRow);
    }

    private QualityEventIntake mapRow(ResultSet rs, int rowNum) throws SQLException {
        String tenantStr = rs.getString("tenant_id");
        String createdBy = rs.getString("created_by");

        return new QualityEventIntake(
                UUID.fromString(rs.getString("event_id")),
                new TenantId(tenantStr != null ? tenantStr : UUID.randomUUID().toString()),
                rs.getString("source_system"),
                rs.getString("event_type"),
                rs.getString("severity"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getString("payload_json"),
                rs.getString("capa_id") != null ? UUID.fromString(rs.getString("capa_id")) : null,
                rs.getTimestamp("ingested_at").toInstant(),
                new AuditMetadata(createdBy != null ? createdBy : "system", LocalDateTime.now(), createdBy != null ? createdBy : "system", LocalDateTime.now())
        );
    }
}
