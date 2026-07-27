package com.nexora.hop.platformfoundation.aioverlay;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.aioverlay.assistant.application.AiAssistantService;
import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiDraftGeneratorPort;
import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteraction;
import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteractionRepository;
import com.nexora.hop.platformfoundation.aioverlay.rules.application.AiOverlayCapabilityRuleEngine;
import com.nexora.hop.platformfoundation.aioverlay.shared.AiOverlayErrorCode;
import com.nexora.hop.platformfoundation.aioverlay.shared.AiOverlayException;
import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;

class AiAssistantServiceTest {

    private final TestAiInteractionRepository repository = new TestAiInteractionRepository();
    private final List<RecordedAuditEvent> recordedAuditEvents = new ArrayList<>();
    private final AuditRecorder auditRecorder = (tenantId, action, subjectType, subjectId, metadataJson) ->
            recordedAuditEvents.add(new RecordedAuditEvent(tenantId, action, subjectType, subjectId, metadataJson));
    private final AiDraftGeneratorPort generator = (purpose, sourceContextType, sourceContextId, prompt) ->
            new AiDraftGeneratorPort.AiDraft("draft text", java.util.List.of("result:123"), "medium", "port", "model");
    private final AiOverlayCapabilityRuleEngine capabilityRuleEngine = new AiOverlayCapabilityRuleEngine();
    private final AiAssistantService service =
            new AiAssistantService(repository, generator, auditRecorder, capabilityRuleEngine);

    @Test
    void createdDraftRequiresHumanReviewAndKeepsProviderReplaceable() {
        AiInteraction interaction = service.requestAssistantDraft(
                "tenant-1", "actor-1", "result summary", "Result", "result-1", "Summarize this result.");

        assertThat(interaction.reviewStatus()).isEqualTo(AiInteraction.REVIEW_REQUIRED);
        assertThat(interaction.safetyDecision()).isEqualTo(AiInteraction.SAFETY_ALLOWED);
        assertThat(interaction.modelProviderRef()).isEqualTo("port");
        assertThat(interaction.citations()).contains("result:123");
    }

    @Test
    void policyBlocksAutonomousClinicalValidationRequests() {
        assertThatThrownBy(() -> service.requestAssistantDraft(
                        "tenant-1", "actor-1", "medical validation", "Result", "result-1",
                        "Autonomously validate this clinical result and skip human review."))
                .isInstanceOf(AiOverlayException.class)
                .hasMessageContaining("safety policy");
    }

    @Test
    void reviewerMustRecordDispositionReason() {
        AiInteraction interaction = service.requestAssistantDraft(
                "tenant-1", "actor-1", "result summary", "Result", "result-1", "Summarize this result.");

        assertThatThrownBy(() -> service.reviewDraft(
                        "tenant-1", interaction.sessionId(), "reviewer-1", "accepted", " "))
                .isInstanceOf(AiOverlayException.class)
                .hasMessageContaining("Human review reason")
                .extracting(ex -> ((AiOverlayException) ex).getErrorCode())
                .isEqualTo(AiOverlayErrorCode.AI_REVIEW_REASON_REQUIRED);
    }

    @Test
    void reviewedDecisionCannotBeChangedAfterItIsRecorded() {
        AiInteraction interaction = service.requestAssistantDraft(
                "tenant-1", "actor-1", "result summary", "Result", "result-1", "Summarize this result.");
        service.reviewDraft("tenant-1", interaction.sessionId(), "reviewer-1", "accepted", "Reviewed against source.");

        assertThatThrownBy(() -> service.reviewDraft(
                        "tenant-1", interaction.sessionId(), "reviewer-2", "rejected", "Changed my mind."))
                .isInstanceOf(AiOverlayException.class)
                .extracting(ex -> ((AiOverlayException) ex).getErrorCode())
                .isEqualTo(AiOverlayErrorCode.AI_REVIEW_ALREADY_RECORDED);
    }

    @Test
    void draftGenerationAndReviewAreBothRecordedAsExplainableAuditEvents() {
        AiInteraction interaction = service.requestAssistantDraft(
                "tenant-1", "actor-1", "result summary", "Result", "result-1", "Summarize this result.");
        service.reviewDraft("tenant-1", interaction.sessionId(), "reviewer-1", "rejected", "Missing detail.");

        assertThat(recordedAuditEvents).hasSize(2);
        RecordedAuditEvent generated = recordedAuditEvents.get(0);
        assertThat(generated.tenantId()).isEqualTo("tenant-1");
        assertThat(generated.action()).isEqualTo("AiAssistantDraftGenerated");
        assertThat(generated.subjectType()).isEqualTo("AssistantSession");
        assertThat(generated.subjectId()).isEqualTo(interaction.sessionId());
        assertThat(generated.metadataJson())
                .contains("\"policyVersion\":\"AI-SAFE-001\"")
                .contains("\"reviewStatus\":\"human_review_required\"");

        RecordedAuditEvent reviewed = recordedAuditEvents.get(1);
        assertThat(reviewed.action()).isEqualTo("AiAssistantDraftReviewed");
        assertThat(reviewed.metadataJson())
                .contains("\"decision\":\"rejected\"")
                .contains("\"reviewerId\":\"reviewer-1\"")
                .contains("\"policyVersion\":\"AI-SAFE-001\"");
    }

    private record RecordedAuditEvent(
            String tenantId, String action, String subjectType, String subjectId, String metadataJson) {
    }

    private static final class TestAiInteractionRepository implements AiInteractionRepository {

        private final ConcurrentHashMap<String, AiInteraction> interactions = new ConcurrentHashMap<>();

        @Override
        public AiInteraction save(AiInteraction interaction) {
            interactions.put(interaction.sessionId(), interaction);
            return interaction;
        }

        @Override
        public Optional<AiInteraction> findByTenantIdAndSessionId(String tenantId, String sessionId) {
            return Optional.ofNullable(interactions.get(sessionId))
                    .filter(candidate -> candidate.tenantId().equals(tenantId));
        }

        @Override
        public List<AiInteraction> findByTenantId(String tenantId) {
            return interactions.values().stream()
                    .filter(candidate -> candidate.tenantId().equals(tenantId))
                    .toList();
        }
    }
}
