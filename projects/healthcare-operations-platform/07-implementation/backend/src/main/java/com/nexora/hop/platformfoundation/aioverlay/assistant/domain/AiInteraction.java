package com.nexora.hop.platformfoundation.aioverlay.assistant.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

public record AiInteraction(
        String sessionId,
        String tenantId,
        String actorId,
        String purpose,
        String sourceContextType,
        String sourceContextId,
        String userPrompt,
        String draftOutput,
        List<String> citations,
        String confidenceBand,
        String safetyDecision,
        String reviewStatus,
        String reviewerId,
        String reviewReason,
        String modelProviderRef,
        String modelNameRef,
        String policyVersion,
        String lifecycleStatus,
        AuditMetadata audit) {

    public static final String SAFETY_ALLOWED = "allowed_with_human_review";
    public static final String SAFETY_BLOCKED = "blocked";
    public static final String REVIEW_REQUIRED = "human_review_required";
    public static final String REVIEW_ACCEPTED = "accepted";
    public static final String REVIEW_REJECTED = "rejected";
    public static final String STATUS_GENERATED = "generated";
    public static final String STATUS_ARCHIVED = "archived";

    public AiInteraction withReview(String status, String reviewerId, String reason, LocalDateTime reviewedAt) {
        AuditMetadata updatedAudit = new AuditMetadata(audit.createdBy(), audit.createdAt(), reviewerId, reviewedAt);
        return new AiInteraction(
                sessionId, tenantId, actorId, purpose, sourceContextType, sourceContextId, userPrompt,
                draftOutput, citations, confidenceBand, safetyDecision, status, reviewerId, reason,
                modelProviderRef, modelNameRef, policyVersion, STATUS_ARCHIVED, updatedAudit);
    }
}
