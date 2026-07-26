package com.nexora.hop.platformfoundation.aioverlay.assistant.adapter.out.memory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteraction;
import com.nexora.hop.platformfoundation.aioverlay.assistant.domain.AiInteractionRepository;

@Repository
@Profile("!local")
class InMemoryAiInteractionRepository implements AiInteractionRepository {

    private final Map<String, AiInteraction> interactions = new ConcurrentHashMap<>();

    @Override
    public AiInteraction save(AiInteraction interaction) {
        interactions.put(interaction.sessionId(), interaction);
        return interaction;
    }

    @Override
    public Optional<AiInteraction> findByTenantIdAndSessionId(String tenantId, String sessionId) {
        return Optional.ofNullable(interactions.get(sessionId))
                .filter(candidate -> candidate.tenantId().equals(tenantId));
    }

    @Override
    public List<AiInteraction> findByTenantId(String tenantId) {
        return interactions.values().stream()
                .filter(candidate -> candidate.tenantId().equals(tenantId))
                .toList();
    }
}
