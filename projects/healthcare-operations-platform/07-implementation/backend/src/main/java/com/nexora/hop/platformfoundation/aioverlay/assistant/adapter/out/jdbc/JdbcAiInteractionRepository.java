package com.nexora.hop.platformfoundation.aioverlay.assistant.adapter.out.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteraction;
import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteractionRepository;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Repository
@Profile("local")
class JdbcAiInteractionRepository implements AiInteractionRepository {

    private static final String SELECT_SQL = """
            select session_id, tenant_id, actor_id, purpose, source_context_type, source_context_id,
                   user_prompt, draft_output, citations_text, confidence_band, safety_decision,
                   review_status, reviewer_id, review_reason, model_provider_ref, model_name_ref,
                   policy_version, lifecycle_status, created_by, created_at, updated_by, updated_at
              from ai_overlay.ai_interactions
            """;

    private final JdbcTemplate jdbcTemplate;

    JdbcAiInteractionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AiInteraction save(AiInteraction interaction) {
        jdbcTemplate.update("""
                insert into ai_overlay.ai_interactions
                    (session_id, tenant_id, actor_id, purpose, source_context_type, source_context_id,
                     user_prompt, draft_output, citations_text, confidence_band, safety_decision,
                     review_status, reviewer_id, review_reason, model_provider_ref, model_name_ref,
                     policy_version, lifecycle_status, created_by, created_at, updated_by, updated_at)
                values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                on conflict (session_id) do update set
                    draft_output = excluded.draft_output,
                    citations_text = excluded.citations_text,
                    confidence_band = excluded.confidence_band,
                    safety_decision = excluded.safety_decision,
                    review_status = excluded.review_status,
                    reviewer_id = excluded.reviewer_id,
                    review_reason = excluded.review_reason,
                    lifecycle_status = excluded.lifecycle_status,
                    updated_by = excluded.updated_by,
                    updated_at = excluded.updated_at
                """,
                interaction.sessionId(), interaction.tenantId(), interaction.actorId(), interaction.purpose(),
                interaction.sourceContextType(), interaction.sourceContextId(), interaction.userPrompt(),
                interaction.draftOutput(), String.join("\n", interaction.citations()), interaction.confidenceBand(),
                interaction.safetyDecision(), interaction.reviewStatus(), interaction.reviewerId(),
                interaction.reviewReason(), interaction.modelProviderRef(), interaction.modelNameRef(),
                interaction.policyVersion(), interaction.lifecycleStatus(), interaction.audit().createdBy(),
                Timestamp.valueOf(interaction.audit().createdAt()), interaction.audit().updatedBy(),
                Timestamp.valueOf(interaction.audit().updatedAt()));
        return interaction;
    }

    @Override
    public Optional<AiInteraction> findByTenantIdAndSessionId(String tenantId, String sessionId) {
        return jdbcTemplate.query(
                        SELECT_SQL + " where tenant_id = ? and session_id = ?",
                        JdbcAiInteractionRepository::map,
                        tenantId,
                        sessionId)
                .stream()
                .findFirst();
    }

    @Override
    public List<AiInteraction> findByTenantId(String tenantId) {
        return jdbcTemplate.query(
                SELECT_SQL + " where tenant_id = ? order by created_at desc",
                JdbcAiInteractionRepository::map,
                tenantId);
    }

    @SuppressWarnings("PMD.UnusedFormalParameter")
    private static AiInteraction map(ResultSet resultSet, int rowNumber) throws SQLException {
        return new AiInteraction(
                resultSet.getString("session_id"),
                resultSet.getString("tenant_id"),
                resultSet.getString("actor_id"),
                resultSet.getString("purpose"),
                resultSet.getString("source_context_type"),
                resultSet.getString("source_context_id"),
                resultSet.getString("user_prompt"),
                resultSet.getString("draft_output"),
                splitLines(resultSet.getString("citations_text")),
                resultSet.getString("confidence_band"),
                resultSet.getString("safety_decision"),
                resultSet.getString("review_status"),
                resultSet.getString("reviewer_id"),
                resultSet.getString("review_reason"),
                resultSet.getString("model_provider_ref"),
                resultSet.getString("model_name_ref"),
                resultSet.getString("policy_version"),
                resultSet.getString("lifecycle_status"),
                new AuditMetadata(
                        resultSet.getString("created_by"),
                        localDateTime(resultSet, "created_at"),
                        resultSet.getString("updated_by"),
                        localDateTime(resultSet, "updated_at")));
    }

    private static List<String> splitLines(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.asList(value.split("\\R"));
    }

    private static LocalDateTime localDateTime(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getTimestamp(columnName).toLocalDateTime();
    }
}
