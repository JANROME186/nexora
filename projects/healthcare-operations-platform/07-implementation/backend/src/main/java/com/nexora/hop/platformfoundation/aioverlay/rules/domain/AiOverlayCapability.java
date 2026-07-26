package com.nexora.hop.platformfoundation.aioverlay.rules.domain;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

/**
 * The four BCM-AI-002..005 capabilities that ship custom guardrail rules on top of the generic
 * BCM-AI-001 assistant orchestration compiled by COM-MOD-015-BE-001. Each maps 1:1 to a
 * {@code business-rules.md}/{@code permissions.md} capability package and keeps its own
 * tenant-scoped, attributable set of allowed source context types.
 */
public enum AiOverlayCapability {

    OCR_DOCUMENT_INTAKE(
            "BCM-AI-002", "ocr_document_intake", Set.of("referral", "order", "invoice", "external_report")),
    RESULT_CASE_SUMMARY(
            "BCM-AI-003", "result_case_summary", Set.of("case", "result")),
    SEMANTIC_SEARCH(
            "BCM-AI-004", "semantic_search", Set.of("search_index", "case", "result", "document")),
    RETRIEVAL_GROUNDING(
            "BCM-AI-005", "retrieval_grounding", Set.of("knowledge_base", "case", "result", "document"));

    private final String capabilityId;
    private final String purposeKey;
    private final Set<String> allowedSourceContextTypes;

    AiOverlayCapability(String capabilityId, String purposeKey, Set<String> allowedSourceContextTypes) {
        this.capabilityId = capabilityId;
        this.purposeKey = purposeKey;
        this.allowedSourceContextTypes = allowedSourceContextTypes;
    }

    public String getCapabilityId() {
        return capabilityId;
    }

    public String getPurposeKey() {
        return purposeKey;
    }

    public Set<String> getAllowedSourceContextTypes() {
        return allowedSourceContextTypes;
    }

    /** Resolves a capability from an assistant request's {@code purpose}, when it matches one of these four. */
    public static Optional<AiOverlayCapability> fromPurpose(String purpose) {
        if (purpose == null || purpose.isBlank()) {
            return Optional.empty();
        }
        String normalized = purpose.strip().toLowerCase(Locale.ROOT);
        return Arrays.stream(values()).filter(capability -> capability.purposeKey.equals(normalized)).findFirst();
    }
}
