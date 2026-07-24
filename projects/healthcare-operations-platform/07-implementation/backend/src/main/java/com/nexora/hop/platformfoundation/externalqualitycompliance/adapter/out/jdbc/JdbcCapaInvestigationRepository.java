package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.CapaInvestigationRepository;
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
public class JdbcCapaInvestigationRepository implements CapaInvestigationRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcCapaInvestigationRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public CapaInvestigation save(CapaInvestigation capa) {
        String sql = """
                INSERT INTO capa_investigations (
                    capa_id, capa_number, tenant_id, title, source_category, source_reference_id,
                    assigned_investigator_id, target_completion_date, status, root_cause_methodology,
                    root_cause_summary, effectiveness_rating, closure_notes, created_by, created_at
                ) VALUES (
                    :capaId, :capaNumber, :tenantId, :title, :sourceCategory, :sourceReferenceId,
                    :assignedInvestigatorId, :targetCompletionDate, :status, :rootCauseMethodology,
                    :rootCauseSummary, :effectivenessRating, :closureNotes, :createdBy, :createdAt
                ) ON CONFLICT (capa_id) DO UPDATE SET
                    status = EXCLUDED.status,
                    root_cause_methodology = EXCLUDED.root_cause_methodology,
                    root_cause_summary = EXCLUDED.root_cause_summary,
                    effectiveness_rating = EXCLUDED.effectiveness_rating,
                    closure_notes = EXCLUDED.closure_notes
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("capaId", capa.getCapaId().toString())
                .addValue("capaNumber", capa.getCapaNumber())
                .addValue("tenantId", capa.getTenantId() != null ? capa.getTenantId().value() : null)
                .addValue("title", capa.getTitle())
                .addValue("sourceCategory", capa.getSourceCategory())
                .addValue("sourceReferenceId", capa.getSourceReferenceId())
                .addValue("assignedInvestigatorId", capa.getAssignedInvestigatorId() != null ? capa.getAssignedInvestigatorId().toString() : null)
                .addValue("targetCompletionDate", capa.getTargetCompletionDate() != null ? java.sql.Date.valueOf(capa.getTargetCompletionDate()) : null)
                .addValue("status", capa.getStatus().name())
                .addValue("rootCauseMethodology", capa.getRootCauseMethodology())
                .addValue("rootCauseSummary", capa.getRootCauseSummary())
                .addValue("effectivenessRating", capa.getEffectivenessRating().name())
                .addValue("closureNotes", capa.getClosureNotes())
                .addValue("createdBy", capa.getAudit() != null ? capa.getAudit().createdBy() : "system")
                .addValue("createdAt", capa.getAudit() != null && capa.getAudit().createdAt() != null ? java.sql.Timestamp.valueOf(capa.getAudit().createdAt()) : java.sql.Timestamp.from(Instant.now()));

        jdbcTemplate.update(sql, params);
        return capa;
    }

    @Override
    public Optional<CapaInvestigation> findById(UUID id) {
        String sql = "SELECT * FROM capa_investigations WHERE capa_id = :id";
        List<CapaInvestigation> list = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id.toString()), this::mapRow);
        return list.stream().findFirst();
    }

    @Override
    public List<CapaInvestigation> findAll(String status, String sourceCategory) {
        StringBuilder sql = new StringBuilder("SELECT * FROM capa_investigations WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (status != null && !status.isBlank()) {
            sql.append(" AND LOWER(status) = LOWER(:status)");
            params.addValue("status", status.trim());
        }
        if (sourceCategory != null && !sourceCategory.isBlank()) {
            sql.append(" AND LOWER(source_category) = LOWER(:sourceCategory)");
            params.addValue("sourceCategory", sourceCategory.trim());
        }

        return jdbcTemplate.query(sql.toString(), params, this::mapRow);
    }

    private CapaInvestigation mapRow(ResultSet rs, int rowNum) throws SQLException {
        String tenantStr = rs.getString("tenant_id");
        String createdBy = rs.getString("created_by");

        return new CapaInvestigation(
                UUID.fromString(rs.getString("capa_id")),
                rs.getString("capa_number"),
                new TenantId(tenantStr != null ? tenantStr : UUID.randomUUID().toString()),
                rs.getString("title"),
                rs.getString("source_category"),
                rs.getString("source_reference_id"),
                rs.getString("assigned_investigator_id") != null ? UUID.fromString(rs.getString("assigned_investigator_id")) : UUID.randomUUID(),
                rs.getDate("target_completion_date") != null ? rs.getDate("target_completion_date").toLocalDate() : null,
                CapaInvestigation.Status.fromString(rs.getString("status")),
                rs.getString("root_cause_methodology"),
                rs.getString("root_cause_summary"),
                CapaInvestigation.EffectivenessRating.fromString(rs.getString("effectiveness_rating")),
                rs.getString("closure_notes"),
                new AuditMetadata(createdBy != null ? createdBy : "system", LocalDateTime.now(), createdBy != null ? createdBy : "system", LocalDateTime.now())
        );
    }
}
