package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

import java.util.UUID;

public class AuditFinding {

    public enum Severity {
        CRITICAL,
        MAJOR,
        MINOR,
        OPPORTUNITY_FOR_IMPROVEMENT;

        public static Severity fromString(String val) {
            if (val == null || val.isBlank()) return MINOR;
            try {
                return Severity.valueOf(val.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return MINOR;
            }
        }
    }

    private final UUID findingId;
    private final String clauseReference;
    private final Severity severity;
    private final String observation;
    private final String evidenceReference;
    private UUID capaId;

    public AuditFinding(
            UUID findingId,
            String clauseReference,
            Severity severity,
            String observation,
            String evidenceReference,
            UUID capaId) {
        if (observation == null || observation.isBlank()) {
            throw new ExternalQualityComplianceException("AUDIT_FINDING_OBSERVATION_REQUIRED", "quality.error.finding_observation_required", "Finding observation is required");
        }

        this.findingId = findingId != null ? findingId : UUID.randomUUID();
        this.clauseReference = clauseReference != null ? clauseReference.trim() : "";
        this.severity = severity != null ? severity : Severity.MINOR;
        this.observation = observation.trim();
        this.evidenceReference = evidenceReference != null ? evidenceReference.trim() : "";
        this.capaId = capaId;
    }

    public void linkCapa(UUID capaId) {
        this.capaId = capaId;
    }

    public UUID getFindingId() { return findingId; }
    public String getClauseReference() { return clauseReference; }
    public Severity getSeverity() { return severity; }
    public String getObservation() { return observation; }
    public String getEvidenceReference() { return evidenceReference; }
    public UUID getCapaId() { return capaId; }
}
