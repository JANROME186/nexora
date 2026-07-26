package com.nexora.hop.platformfoundation.aioverlay.assistant.domain;

import java.util.List;

@FunctionalInterface
public interface AiDraftGeneratorPort {

    AiDraft generate(String purpose, String sourceContextType, String sourceContextId, String prompt);

    record AiDraft(String text, List<String> citations, String confidenceBand, String modelProviderRef, String modelNameRef) {
    }
}
