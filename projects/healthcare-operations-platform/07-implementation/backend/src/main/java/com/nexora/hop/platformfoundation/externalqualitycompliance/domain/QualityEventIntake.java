package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public final class QualityEventIntake {

    private final UUID eventId;
    private final TenantId tenantId;
    private final String sourceSystem;
    private final String eventType;
    private final String severity;
    private final String title;
    private final String description;
    private final String payloadJson;
    private UUID capaId;
    private final Instant ingestedAt;
    private AuditMetadata audit;

    public QualityEventIntake(
            UUID eventId,
            TenantId tenantId,
            String sourceSystem,
            String eventType,
            String severity,
            String title,
            String description,
            String payloadJson,
            UUID capaId,
            Instant ingestedAt,
            AuditMetadata audit) {
        if (sourceSystem == null || sourceSystem.isBlank()) {
            throw new ExternalQualityDomainException("EVENT_SOURCE_REQUIRED", "quality.error.event_source_required", "Source system is required");
        }
        if (eventType == null || eventType.isBlank()) {
            throw new ExternalQualityDomainException("EVENT_TYPE_REQUIRED", "quality.error.event_type_required", "Event type is required");
        }

        this.eventId = eventId != null ? eventId : UUID.randomUUID();
        this.tenantId = tenantId != null ? tenantId : new TenantId(UUID.randomUUID().toString());
        this.sourceSystem = sourceSystem.trim();
        this.eventType = eventType.trim();
        this.severity = severity != null ? severity.trim().toUpperCase() : "MEDIUM";
        this.title = title != null ? title.trim() : eventType;
        this.description = description != null ? description.trim() : "";
        this.payloadJson = payloadJson != null ? payloadJson.trim() : "{}";
        this.capaId = capaId;
        this.ingestedAt = ingestedAt != null ? ingestedAt : Instant.now();
        this.audit = audit != null ? audit : new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now());
    }

    public void linkCapa(UUID capaId) {
        this.capaId = capaId;
    }

    public UUID getEventId() { return eventId; }
    public TenantId getTenantId() { return tenantId; }
    public String getSourceSystem() { return sourceSystem; }
    public String getEventType() { return eventType; }
    public String getSeverity() { return severity; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPayloadJson() { return payloadJson; }
    public UUID getCapaId() { return capaId; }
    public Instant getIngestedAt() { return ingestedAt; }
    public AuditMetadata getAudit() { return audit; }
}
