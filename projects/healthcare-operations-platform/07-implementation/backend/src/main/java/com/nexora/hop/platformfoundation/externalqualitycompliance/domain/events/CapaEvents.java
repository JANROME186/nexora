package com.nexora.hop.platformfoundation.externalqualitycompliance.domain.events;

import java.time.Instant;
import java.util.UUID;

public class CapaEvents {

    public record CapaInvestigationInitiatedEvent(
            UUID capaId,
            String capaNumber,
            String title,
            String sourceCategory,
            String sourceReferenceId,
            UUID assignedInvestigatorId,
            Instant occurredAt
    ) {}

    public record CapaInvestigationApprovedEvent(
            UUID capaId,
            String capaNumber,
            Instant occurredAt
    ) {}

    public record CapaInvestigationVerifiedEvent(
            UUID capaId,
            String capaNumber,
            String effectivenessRating,
            String status,
            Instant occurredAt
    ) {}
}
