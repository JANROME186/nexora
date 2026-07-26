package com.nexora.hop.platformfoundation.aioverlay.assistant.adapter.out.local;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiDraftGeneratorPort;

@Component
class LocalDeterministicAiDraftGenerator implements AiDraftGeneratorPort {

    @Override
    public AiDraft generate(String purpose, String sourceContextType, String sourceContextId, String prompt) {
        String text = "Draft advisory output for %s using %s/%s. Human review is required before operational use."
                .formatted(purpose, sourceContextType, sourceContextId);
        return new AiDraft(
                text,
                List.of("%s:%s".formatted(sourceContextType, sourceContextId), "policy:AI-SAFE-001"),
                "medium",
                "replaceable-local-adapter",
                "deterministic-draft-v1");
    }
}
