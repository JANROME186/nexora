package com.nexora.hop.platformfoundation.aioverlay.rules.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class AiOverlayCapabilityTest {

    @Test
    void resolvesEachCapabilityFromItsExactPurposeKey() {
        assertThat(AiOverlayCapability.fromPurpose("ocr_document_intake"))
                .contains(AiOverlayCapability.OCR_DOCUMENT_INTAKE);
        assertThat(AiOverlayCapability.fromPurpose("result_case_summary"))
                .contains(AiOverlayCapability.RESULT_CASE_SUMMARY);
        assertThat(AiOverlayCapability.fromPurpose("semantic_search"))
                .contains(AiOverlayCapability.SEMANTIC_SEARCH);
        assertThat(AiOverlayCapability.fromPurpose("retrieval_grounding"))
                .contains(AiOverlayCapability.RETRIEVAL_GROUNDING);
    }

    @Test
    void resolutionIsCaseAndWhitespaceTolerant() {
        assertThat(AiOverlayCapability.fromPurpose("  OCR_Document_Intake  "))
                .contains(AiOverlayCapability.OCR_DOCUMENT_INTAKE);
    }

    @Test
    void freeFormPurposesDoNotMatchAnyCapability() {
        assertThat(AiOverlayCapability.fromPurpose("result summary")).isEmpty();
        assertThat(AiOverlayCapability.fromPurpose(null)).isEmpty();
        assertThat(AiOverlayCapability.fromPurpose("")).isEmpty();
    }

    @Test
    void eachCapabilityDeclaresItsOwnAllowedSourceContextScope() {
        Optional<AiOverlayCapability> ocr = AiOverlayCapability.fromPurpose("ocr_document_intake");
        assertThat(ocr).isPresent();
        assertThat(ocr.get().getAllowedSourceContextTypes()).contains("referral", "invoice");
        assertThat(ocr.get().getCapabilityId()).isEqualTo("BCM-AI-002");
    }
}
