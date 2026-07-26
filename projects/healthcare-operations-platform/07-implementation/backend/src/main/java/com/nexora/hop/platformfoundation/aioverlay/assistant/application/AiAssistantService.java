package com.nexora.hop.platformfoundation.aioverlay.assistant.application;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiDraftGeneratorPort;
import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteraction;
import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteractionRepository;
import com.nexora.hop.platformfoundation.aioverlay.rules.application.AiOverlayCapabilityRuleEngine;
import com.nexora.hop.platformfoundation.aioverlay.rules.domain.AiOverlayCapability;
import com.nexora.hop.platformfoundation.aioverlay.shared.AiOverlayErrorCode;
import com.nexora.hop.platformfoundation.aioverlay.shared.AiOverlayException;
import com.nexora.hop.platformfoundation.aioverlay.shared.AiOverlayNotFoundException;
import com.nexora.hop.platformfoundation.auditcompliance.AuditRecorder;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

@Service
public class AiAssistantService {

    private static final String POLICY_VERSION = "AI-SAFE-001";

    private final AiInteractionRepository repository;
    private final AiDraftGeneratorPort draftGenerator;
    private final AuditRecorder auditRecorder;
    private final AiOverlayCapabilityRuleEngine capabilityRuleEngine;
    private final Clock clock;

    @Autowired
    public AiAssistantService(
            AiInteractionRepository repository, AiDraftGeneratorPort draftGenerator, AuditRecorder auditRecorder,
            AiOverlayCapabilityRuleEngine capabilityRuleEngine) {
        this(repository, draftGenerator, auditRecorder, capabilityRuleEngine, Clock.systemUTC());
    }

    public AiAssistantService(
            AiInteractionRepository repository, AiDraftGeneratorPort draftGenerator,
            AuditRecorder auditRecorder, AiOverlayCapabilityRuleEngine capabilityRuleEngine, Clock clock) {
        this.repository = repository;
        this.draftGenerator = draftGenerator;
        this.auditRecorder = auditRecorder;
        this.capabilityRuleEngine = capabilityRuleEngine;
        this.clock = clock;
    }

    public AiInteraction requestAssistantDraft(
            String tenantId, String actorId, String purpose, String sourceContextType,
            String sourceContextId, String prompt) {
        String tenant = requiredText(tenantId, "Tenant id is required.");
        String actor = requiredText(actorId, "Actor id is required.");
        String requestedPurpose = requiredText(purpose, "AI assistance purpose is required.");
        String contextType = requiredText(sourceContextType, "Source context type is required.");
        String contextId = requiredText(sourceContextId, "Source context id is required.");
        String userPrompt = requiredText(prompt, "Prompt is required.");
        enforcePolicy(userPrompt);
        Optional<AiOverlayCapability> capability = AiOverlayCapability.fromPurpose(requestedPurpose);
        capability.ifPresent(value -> capabilityRuleEngine.validateRequest(value, contextType));

        AiDraftGeneratorPort.AiDraft draft = draftGenerator.generate(
                requestedPurpose, contextType, contextId, userPrompt);
        capability.ifPresent(
                value -> capabilityRuleEngine.validateDraft(value, draft.citations(), AiInteraction.REVIEW_REQUIRED));
        LocalDateTime now = LocalDateTime.now(clock);
        AiInteraction created = repository.save(new AiInteraction(
                UUID.randomUUID().toString(), tenant, actor, requestedPurpose, contextType, contextId,
                userPrompt, draft.text(), List.copyOf(draft.citations()), draft.confidenceBand(),
                AiInteraction.SAFETY_ALLOWED, AiInteraction.REVIEW_REQUIRED, null, null,
                draft.modelProviderRef(), draft.modelNameRef(), POLICY_VERSION, AiInteraction.STATUS_GENERATED,
                new AuditMetadata(actor, now, actor, now)));
        auditRecorder.recordSystemEvent(
                tenant, "AiAssistantDraftGenerated", "AssistantSession", created.sessionId(),
                "{\"policyVersion\":\"%s\",\"reviewStatus\":\"%s\"}"
                        .formatted(POLICY_VERSION, AiInteraction.REVIEW_REQUIRED));
        return created;
    }

    public AiInteraction reviewDraft(
            String tenantId, String sessionId, String reviewerId, String decision, String reason) {
        String reviewer = requiredText(reviewerId, "Reviewer id is required.");
        String normalizedDecision = normalizeReviewDecision(requiredText(decision, "Review decision is required."));
        if (normalizedDecision == null) {
            throw new AiOverlayException(
                    "Review decision must be accepted or rejected.", AiOverlayErrorCode.AI_COMMAND_INVALID);
        }
        String reviewReason = requiredText(reason, "Human review reason is required.");
        AiInteraction current = requireSession(tenantId, sessionId);
        AiInteraction reviewed = repository.save(current.withReview(
                normalizedDecision, reviewer, reviewReason, LocalDateTime.now(clock)));
        auditRecorder.recordSystemEvent(
                current.tenantId(), "AiAssistantDraftReviewed", "AssistantSession", current.sessionId(),
                "{\"decision\":\"%s\"}".formatted(normalizedDecision));
        return reviewed;
    }

    public AiInteraction getSession(String tenantId, String sessionId) {
        return requireSession(tenantId, sessionId);
    }

    public List<AiInteraction> listAuditRecords(String tenantId) {
        return repository.findByTenantId(requiredText(tenantId, "Tenant id is required."));
    }

    private AiInteraction requireSession(String tenantId, String sessionId) {
        return repository.findByTenantIdAndSessionId(
                        requiredText(tenantId, "Tenant id is required."),
                        requiredText(sessionId, "Session id is required."))
                .orElseThrow(() -> new AiOverlayNotFoundException("AI assistant session was not found."));
    }

    private static void enforcePolicy(String prompt) {
        if (prompt.contains("autonomously validate")
                || prompt.contains("autonomous diagnosis")
                || prompt.contains("skip human review")) {
            throw new AiOverlayException(
                    "AI request violates human-review safety policy.", AiOverlayErrorCode.AI_POLICY_BLOCKED);
        }
    }

    private static String normalizeReviewDecision(String decision) {
        if (AiInteraction.REVIEW_ACCEPTED.equals(decision)) {
            return AiInteraction.REVIEW_ACCEPTED;
        }
        if (AiInteraction.REVIEW_REJECTED.equals(decision)) {
            return AiInteraction.REVIEW_REJECTED;
        }
        return null;
    }

    private static String requiredText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new AiOverlayException(message, AiOverlayErrorCode.AI_COMMAND_INVALID);
        }
        return value.strip();
    }
}
