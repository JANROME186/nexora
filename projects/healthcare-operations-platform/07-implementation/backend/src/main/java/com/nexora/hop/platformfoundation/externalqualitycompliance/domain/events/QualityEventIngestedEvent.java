package com.nexora.hop.platformfoundation.externalqualitycompliance.domain.events;

import java.time.Instant;
import java.util.UUID;

public record QualityEventIngestedEvent(
        UUID eventId,
        String sourceSystem,
        String eventType,
        String severity,
        UUID capaId,
        Instant occurredAt
) {}
