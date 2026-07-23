package com.nexora.hop.platformfoundation.externalqualitycompliance.domain.events;

import java.time.Instant;
import java.util.UUID;

public record ExternalQualityEvaluationScoredEvent(
        UUID evaluationId,
        String programCode,
        String surveyCycle,
        double zScore,
        String performanceRating,
        UUID capaInvestigationId,
        Instant occurredAt
) {}
