package com.nexora.hop.platformfoundation.aioverlay.shared;

public enum AiOverlayErrorCode {
    AI_COMMAND_INVALID("ai.error.command_invalid"),
    AI_SESSION_NOT_FOUND("ai.error.session_not_found"),
    AI_REVIEW_REASON_REQUIRED("ai.error.review_reason_required"),
    AI_POLICY_BLOCKED("ai.error.policy_blocked");

    private final String messageKey;

    AiOverlayErrorCode(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
