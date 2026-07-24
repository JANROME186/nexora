package com.nexora.hop.platformfoundation.externalqualitycompliance.adapter.out.jdbc;

import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityEvaluation;
import com.nexora.hop.platformfoundation.externalqualitycompliance.domain.ExternalQualityEvaluationRepository;
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
public class JdbcExternalQualityEvaluationRepository implements ExternalQualityEvaluationRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public JdbcExternalQualityEvaluationRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ExternalQualityEvaluation save(ExternalQualityEvaluation evaluation) {
        String sql = """
                INSERT INTO external_quality_evaluations (
                    evaluation_id, tenant_id, provider_name, program_code, survey_cycle,
                    test_definition_id, sample_code, measured_value, peer_group_mean,
                    peer_group_sd, peer_group_count, z_score, performance_rating,
                    capa_investigation_id, stored_document_id, evaluated_at, created_by, created_at
                ) VALUES (
                    :evaluationId, :tenantId, :providerName, :programCode, :surveyCycle,
                    :testDefinitionId, :sampleCode, :measuredValue, :peerGroupMean,
                    :peerGroupSd, :peerGroupCount, :zScore, :performanceRating,
                    :capaInvestigationId, :storedDocumentId, :evaluatedAt, :createdBy, :createdAt
                ) ON CONFLICT (evaluation_id) DO UPDATE SET
                    peer_group_mean = EXCLUDED.peer_group_mean,
                    peer_group_sd = EXCLUDED.peer_group_sd,
                    peer_group_count = EXCLUDED.peer_group_count,
                    z_score = EXCLUDED.z_score,
                    performance_rating = EXCLUDED.performance_rating,
                    capa_investigation_id = EXCLUDED.capa_investigation_id,
                    stored_document_id = EXCLUDED.stored_document_id,
                    evaluated_at = EXCLUDED.evaluated_at
                """;

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("evaluationId", evaluation.getEvaluationId().toString())
                .addValue("tenantId", evaluation.getTenantId() != null ? evaluation.getTenantId().value() : null)
                .addValue("providerName", evaluation.getProviderName())
                .addValue("programCode", evaluation.getProgramCode())
                .addValue("surveyCycle", evaluation.getSurveyCycle())
                .addValue("testDefinitionId", evaluation.getTestDefinitionId().toString())
                .addValue("sampleCode", evaluation.getSampleCode())
                .addValue("measuredValue", evaluation.getMeasuredValue())
                .addValue("peerGroupMean", evaluation.getPeerGroupMean())
                .addValue("peerGroupSd", evaluation.getPeerGroupSd())
                .addValue("peerGroupCount", evaluation.getPeerGroupCount())
                .addValue("zScore", evaluation.getZScore())
                .addValue("performanceRating", evaluation.getPerformanceRating().name())
                .addValue("capaInvestigationId", evaluation.getCapaInvestigationId() != null ? evaluation.getCapaInvestigationId().toString() : null)
                .addValue("storedDocumentId", evaluation.getStoredDocumentId() != null ? evaluation.getStoredDocumentId().toString() : null)
                .addValue("evaluatedAt", evaluation.getEvaluatedAt() != null ? java.sql.Timestamp.from(evaluation.getEvaluatedAt()) : null)
                .addValue("createdBy", evaluation.getAudit() != null ? evaluation.getAudit().createdBy() : "system")
                .addValue("createdAt", evaluation.getAudit() != null && evaluation.getAudit().createdAt() != null ? java.sql.Timestamp.valueOf(evaluation.getAudit().createdAt()) : java.sql.Timestamp.from(Instant.now()));

        jdbcTemplate.update(sql, params);
        return evaluation;
    }

    @Override
    public Optional<ExternalQualityEvaluation> findById(UUID id) {
        String sql = "SELECT * FROM external_quality_evaluations WHERE evaluation_id = :id";
        List<ExternalQualityEvaluation> list = jdbcTemplate.query(sql, new MapSqlParameterSource("id", id.toString()), this::mapRow);
        return list.stream().findFirst();
    }

    @Override
    public List<ExternalQualityEvaluation> findAll(String programCode, String rating) {
        StringBuilder sql = new StringBuilder("SELECT * FROM external_quality_evaluations WHERE 1=1");
        MapSqlParameterSource params = new MapSqlParameterSource();

        if (programCode != null && !programCode.isBlank()) {
            sql.append(" AND LOWER(program_code) = LOWER(:programCode)");
            params.addValue("programCode", programCode.trim());
        }
        if (rating != null && !rating.isBlank()) {
            sql.append(" AND LOWER(performance_rating) = LOWER(:rating)");
            params.addValue("rating", rating.trim());
        }

        return jdbcTemplate.query(sql.toString(), params, this::mapRow);
    }

    private ExternalQualityEvaluation mapRow(ResultSet rs, int rowNum) throws SQLException {
        String tenantStr = rs.getString("tenant_id");
        String createdBy = rs.getString("created_by");

        return new ExternalQualityEvaluation(
                UUID.fromString(rs.getString("evaluation_id")),
                new TenantId(tenantStr != null ? tenantStr : UUID.randomUUID().toString()),
                rs.getString("provider_name"),
                rs.getString("program_code"),
                rs.getString("survey_cycle"),
                UUID.fromString(rs.getString("test_definition_id")),
                rs.getString("sample_code"),
                rs.getDouble("measured_value"),
                rs.getObject("peer_group_mean") != null ? rs.getDouble("peer_group_mean") : null,
                rs.getObject("peer_group_sd") != null ? rs.getDouble("peer_group_sd") : null,
                rs.getObject("peer_group_count") != null ? rs.getInt("peer_group_count") : null,
                rs.getObject("z_score") != null ? rs.getDouble("z_score") : null,
                ExternalQualityEvaluation.Rating.fromString(rs.getString("performance_rating")),
                rs.getString("capa_investigation_id") != null ? UUID.fromString(rs.getString("capa_investigation_id")) : null,
                rs.getString("stored_document_id") != null ? UUID.fromString(rs.getString("stored_document_id")) : null,
                rs.getTimestamp("evaluated_at") != null ? rs.getTimestamp("evaluated_at").toInstant() : null,
                new AuditMetadata(createdBy != null ? createdBy : "system", LocalDateTime.now(), createdBy != null ? createdBy : "system", LocalDateTime.now())
        );
    }
}
