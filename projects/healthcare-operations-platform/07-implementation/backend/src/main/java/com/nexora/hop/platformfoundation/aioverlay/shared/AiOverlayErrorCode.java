package com.nexora.hop.platformfoundation.aioverlay.shared;

public enum AiOverlayErrorCode {
    AI_COMMAND_INVALID("ai.error.command_invalid"),
    AI_SESSION_NOT_FOUND("ai.error.session_not_found"),
    AI_REVIEW_REASON_REQUIRED("ai.error.review_reason_required"),
    AI_POLICY_BLOCKED("ai.error.policy_blocked"),
    AI_SOURCE_CONTEXT_NOT_ALLOWED("ai.error.source_context_not_allowed"),
    AI_CITATIONS_REQUIRED("ai.error.citations_required"),
    AI_REVIEW_ALREADY_RECORDED("ai.error.review_already_recorded");

    private final String messageKey;

    AiOverlayErrorCode(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
