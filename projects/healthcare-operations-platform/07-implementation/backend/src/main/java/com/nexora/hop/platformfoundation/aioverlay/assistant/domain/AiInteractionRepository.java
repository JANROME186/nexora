package com.nexora.hop.platformfoundation.aioverlay.assistant.domain;

import java.util.List;
import java.util.Optional;

public interface AiInteractionRepository {

    AiInteraction save(AiInteraction interaction);

    Optional<AiInteraction> findByTenantIdAndSessionId(String tenantId, String sessionId);

    List<AiInteraction> findByTenantId(String tenantId);
}
