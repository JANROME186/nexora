package com.nexora.hop.platformfoundation.aioverlay.rules.application;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteraction;
import com.nexora.hop.platformfoundation.aioverlay.rules.domain.AiOverlayCapability;
import com.nexora.hop.platformfoundation.aioverlay.shared.AiOverlayErrorCode;
import com.nexora.hop.platformfoundation.aioverlay.shared.AiOverlayException;

/**
 * Custom guardrail rules for OCR Document Intake (BCM-AI-002), Result and Case Summaries
 * (BCM-AI-003), Semantic Search (BCM-AI-004) and Retrieval Knowledge Grounding (BCM-AI-005), on
 * top of the generic BCM-AI-001 assistant orchestration. Each capability's
 * {@code business-rules.md} declares the same three guardrail statements
 * (advisory/attributable/tenant-scoped/reviewable output); this engine enforces them as concrete
 * checks:
 * <ul>
 *   <li>rule 002 (attributable, tenant-scoped): the source context type must belong to the
 *   capability's declared scope;</li>
 *   <li>rule 001 (source citations required): generated output must carry at least one
 *   citation;</li>
 *   <li>rule 003 (reviewable, never auto-applied): output must stay human-review-required until a
 *   reviewer decides.</li>
 * </ul>
 */
@Component
public class AiOverlayCapabilityRuleEngine {

    public void validateRequest(AiOverlayCapability capability, String sourceContextType) {
        String normalized = sourceContextType == null
                ? "" : sourceContextType.strip().toLowerCase(Locale.ROOT);
        if (!capability.getAllowedSourceContextTypes().contains(normalized)) {
            throw new AiOverlayException(
                    "%s requires a source context type in %s.".formatted(
                            capability.getCapabilityId(), capability.getAllowedSourceContextTypes()),
                    AiOverlayErrorCode.AI_SOURCE_CONTEXT_NOT_ALLOWED);
        }
    }

    public void validateDraft(AiOverlayCapability capability, List<String> citations, String reviewStatus) {
        if (citations == null || citations.isEmpty()) {
            throw new AiOverlayException(
                    "%s output must include at least one source citation.".formatted(capability.getCapabilityId()),
                    AiOverlayErrorCode.AI_CITATIONS_REQUIRED);
        }
        if (!AiInteraction.REVIEW_REQUIRED.equals(reviewStatus)) {
            throw new AiOverlayException(
                    "%s output must remain human-review-required until reviewed.".formatted(capability.getCapabilityId()),
                    AiOverlayErrorCode.AI_POLICY_BLOCKED);
        }
    }
}
