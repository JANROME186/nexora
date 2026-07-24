package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public final class CapaInvestigation {

    public enum Status {
        INITIATED,
        RCA_COMPLETED,
        PLAN_APPROVED,
        IN_PROGRESS,
        VERIFICATION_PENDING,
        CLOSED,
        REOPENED;

        public static Status fromString(String val) {
            if (val == null || val.isBlank()) return INITIATED;
            try {
                return Status.valueOf(val.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return INITIATED;
            }
        }
    }

    public enum EffectivenessRating {
        NONE,
        EFFECTIVE,
        PARTIALLY_EFFECTIVE,
        INEFFECTIVE;

        public static EffectivenessRating fromString(String val) {
            if (val == null || val.isBlank()) return NONE;
            try {
                return EffectivenessRating.valueOf(val.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return NONE;
            }
        }
    }

    private final UUID capaId;
    private final String capaNumber;
    private final TenantId tenantId;
    private String title;
    private String sourceCategory;
    private String sourceReferenceId;
    private UUID assignedInvestigatorId;
    private LocalDate targetCompletionDate;
    private Status status;
    private String rootCauseMethodology;
    private String rootCauseSummary;
    private EffectivenessRating effectivenessRating;
    private String closureNotes;
    private AuditMetadata audit;

    public CapaInvestigation(
            UUID capaId,
            String capaNumber,
            TenantId tenantId,
            String title,
            String sourceCategory,
            String sourceReferenceId,
            UUID assignedInvestigatorId,
            LocalDate targetCompletionDate,
            AuditMetadata audit) {
        if (title == null || title.isBlank()) {
            throw new ExternalQualityDomainException("CAPA_TITLE_REQUIRED", "quality.error.capa_title_required", "CAPA title is required");
        }
        if (assignedInvestigatorId == null) {
            throw new ExternalQualityDomainException("CAPA_INVESTIGATOR_REQUIRED", "quality.error.investigator_required", "Assigned investigator ID is required");
        }

        this.capaId = capaId != null ? capaId : UUID.randomUUID();
        this.capaNumber = capaNumber != null && !capaNumber.isBlank() ? capaNumber.trim() : generateCapaNumber();
        this.tenantId = tenantId != null ? tenantId : new TenantId(UUID.randomUUID().toString());
        this.title = title.trim();
        this.sourceCategory = sourceCategory != null ? sourceCategory.trim() : "OPERATIONAL_EVENT";
        this.sourceReferenceId = sourceReferenceId != null ? sourceReferenceId.trim() : "";
        this.assignedInvestigatorId = assignedInvestigatorId;
        this.targetCompletionDate = targetCompletionDate != null ? targetCompletionDate : LocalDate.now().plusDays(30);
        this.status = Status.INITIATED;
        this.effectivenessRating = EffectivenessRating.NONE;
        this.audit = audit != null ? audit : new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now());
    }

    public CapaInvestigation(
            UUID capaId,
            String capaNumber,
            TenantId tenantId,
            String title,
            String sourceCategory,
            String sourceReferenceId,
            UUID assignedInvestigatorId,
            LocalDate targetCompletionDate,
            Status status,
            String rootCauseMethodology,
            String rootCauseSummary,
            EffectivenessRating effectivenessRating,
            String closureNotes,
            AuditMetadata audit) {
        this.capaId = capaId;
        this.capaNumber = capaNumber;
        this.tenantId = tenantId;
        this.title = title;
        this.sourceCategory = sourceCategory;
        this.sourceReferenceId = sourceReferenceId;
        this.assignedInvestigatorId = assignedInvestigatorId;
        this.targetCompletionDate = targetCompletionDate;
        this.status = status != null ? status : Status.INITIATED;
        this.rootCauseMethodology = rootCauseMethodology;
        this.rootCauseSummary = rootCauseSummary;
        this.effectivenessRating = effectivenessRating != null ? effectivenessRating : EffectivenessRating.NONE;
        this.closureNotes = closureNotes;
        this.audit = audit != null ? audit : new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now());
    }

    public void recordRca(String rootCauseMethodology, String rootCauseSummary, AuditMetadata updateAudit) {
        if (rootCauseSummary == null || rootCauseSummary.isBlank()) {
            throw new ExternalQualityDomainException("CAPA_RCA_SUMMARY_REQUIRED", "quality.error.rca_summary_required", "RCA summary is required");
        }
        this.rootCauseMethodology = rootCauseMethodology != null ? rootCauseMethodology.trim() : "5_WHY";
        this.rootCauseSummary = rootCauseSummary.trim();
        this.status = Status.RCA_COMPLETED;
        if (updateAudit != null) this.audit = updateAudit;
    }

    public void approveActionPlan(AuditMetadata updateAudit) {
        if (this.status != Status.RCA_COMPLETED && this.status != Status.INITIATED) {
            throw new ExternalQualityDomainException("CAPA_INVALID_STATE_FOR_APPROVAL", "quality.error.invalid_state_approval", "CAPA cannot be approved from state: " + this.status);
        }
        this.status = Status.PLAN_APPROVED;
        if (updateAudit != null) this.audit = updateAudit;
    }

    public void verifyEffectiveness(EffectivenessRating rating, String closureNotes, AuditMetadata updateAudit) {
        if (rating == null || rating == EffectivenessRating.NONE) {
            throw new ExternalQualityDomainException("CAPA_RATING_REQUIRED", "quality.error.effectiveness_rating_required", "Effectiveness rating is required");
        }
        this.effectivenessRating = rating;
        this.closureNotes = closureNotes != null ? closureNotes.trim() : "";
        if (rating == EffectivenessRating.EFFECTIVE) {
            this.status = Status.CLOSED;
        } else {
            this.status = Status.REOPENED;
        }
        if (updateAudit != null) this.audit = updateAudit;
    }

    private static String generateCapaNumber() {
        return "CAPA-" + System.currentTimeMillis() % 1000000;
    }

    public UUID getCapaId() { return capaId; }
    public String getCapaNumber() { return capaNumber; }
    public TenantId getTenantId() { return tenantId; }
    public String getTitle() { return title; }
    public String getSourceCategory() { return sourceCategory; }
    public String getSourceReferenceId() { return sourceReferenceId; }
    public UUID getAssignedInvestigatorId() { return assignedInvestigatorId; }
    public LocalDate getTargetCompletionDate() { return targetCompletionDate; }
    public Status getStatus() { return status; }
    public String getRootCauseMethodology() { return rootCauseMethodology; }
    public String getRootCauseSummary() { return rootCauseSummary; }
    public EffectivenessRating getEffectivenessRating() { return effectivenessRating; }
    public String getClosureNotes() { return closureNotes; }
    public AuditMetadata getAudit() { return audit; }
}
