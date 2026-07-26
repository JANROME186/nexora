package com.nexora.hop.platformfoundation.aioverlay.rules.application;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteraction;
import com.nexora.hop.platformfoundation.aioverlay.rules.domain.AiOverlayCapability;
import com.nexora.hop.platformfoundation.aioverlay.shared.AiOverlayErrorCode;
import com.nexora.hop.platformfoundation.aioverlay.shared.AiOverlayException;

class AiOverlayCapabilityRuleEngineTest {

    private final AiOverlayCapabilityRuleEngine ruleEngine = new AiOverlayCapabilityRuleEngine();

    @Test
    void allowsSourceContextTypeDeclaredByTheCapabilityScope() {
        ruleEngine.validateRequest(AiOverlayCapability.OCR_DOCUMENT_INTAKE, "referral");
        ruleEngine.validateRequest(AiOverlayCapability.SEMANTIC_SEARCH, "Search_Index");
    }

    @Test
    void rejectsSourceContextTypeOutsideTheCapabilityScope() {
        assertThatThrownBy(() -> ruleEngine.validateRequest(AiOverlayCapability.OCR_DOCUMENT_INTAKE, "email"))
                .isInstanceOf(AiOverlayException.class)
                .extracting(ex -> ((AiOverlayException) ex).getErrorCode())
                .isEqualTo(AiOverlayErrorCode.AI_SOURCE_CONTEXT_NOT_ALLOWED);
    }

    @Test
    void requiresAtLeastOneCitationOnGeneratedOutput() {
        assertThatThrownBy(() -> ruleEngine.validateDraft(
                        AiOverlayCapability.RESULT_CASE_SUMMARY, List.of(), AiInteraction.REVIEW_REQUIRED))
                .isInstanceOf(AiOverlayException.class)
                .extracting(ex -> ((AiOverlayException) ex).getErrorCode())
                .isEqualTo(AiOverlayErrorCode.AI_CITATIONS_REQUIRED);
    }

    @Test
    void requiresOutputToStayHumanReviewRequired() {
        assertThatThrownBy(() -> ruleEngine.validateDraft(
                        AiOverlayCapability.RETRIEVAL_GROUNDING, List.of("case:case-1"), AiInteraction.REVIEW_ACCEPTED))
                .isInstanceOf(AiOverlayException.class)
                .extracting(ex -> ((AiOverlayException) ex).getErrorCode())
                .isEqualTo(AiOverlayErrorCode.AI_POLICY_BLOCKED);
    }

    @Test
    void acceptsAWellFormedDraft() {
        assertThatCode(() -> ruleEngine.validateDraft(
                        AiOverlayCapability.SEMANTIC_SEARCH, List.of("document:doc-1"), AiInteraction.REVIEW_REQUIRED))
                .doesNotThrowAnyException();
    }
}
