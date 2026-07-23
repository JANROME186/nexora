package com.nexora.hop.platformfoundation.externalqualitycompliance.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AuditSchedule {

    public enum Status {
        PLANNED,
        IN_PROGRESS,
        COMPLETED,
        CLOSED;

        public static Status fromString(String val) {
            if (val == null || val.isBlank()) return PLANNED;
            try {
                return Status.valueOf(val.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                return PLANNED;
            }
        }
    }

    private final UUID auditId;
    private final String auditCode;
    private final TenantId tenantId;
    private String title;
    private String category;
    private String standardReference;
    private UUID leadAuditorId;
    private LocalDate plannedStartDate;
    private LocalDate plannedEndDate;
    private Status status;
    private final List<AuditFinding> findings;
    private AuditMetadata audit;

    public AuditSchedule(
            UUID auditId,
            String auditCode,
            TenantId tenantId,
            String title,
            String category,
            String standardReference,
            UUID leadAuditorId,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate,
            AuditMetadata audit) {
        if (title == null || title.isBlank()) {
            throw new ExternalQualityComplianceException("AUDIT_TITLE_REQUIRED", "quality.error.audit_title_required", "Audit title is required");
        }
        if (leadAuditorId == null) {
            throw new ExternalQualityComplianceException("AUDIT_LEAD_AUDITOR_REQUIRED", "quality.error.lead_auditor_required", "Lead auditor ID is required");
        }

        this.auditId = auditId != null ? auditId : UUID.randomUUID();
        this.auditCode = auditCode != null && !auditCode.isBlank() ? auditCode.trim() : generateAuditCode();
        this.tenantId = tenantId != null ? tenantId : new TenantId(UUID.randomUUID().toString());
        this.title = title.trim();
        this.category = category != null ? category.trim() : "INTERNAL";
        this.standardReference = standardReference != null ? standardReference.trim() : "ISO 15189";
        this.leadAuditorId = leadAuditorId;
        this.plannedStartDate = plannedStartDate != null ? plannedStartDate : LocalDate.now();
        this.plannedEndDate = plannedEndDate != null ? plannedEndDate : LocalDate.now().plusDays(7);
        this.status = Status.PLANNED;
        this.findings = new ArrayList<>();
        this.audit = audit != null ? audit : new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now());
    }

    public AuditSchedule(
            UUID auditId,
            String auditCode,
            TenantId tenantId,
            String title,
            String category,
            String standardReference,
            UUID leadAuditorId,
            LocalDate plannedStartDate,
            LocalDate plannedEndDate,
            Status status,
            List<AuditFinding> findings,
            AuditMetadata audit) {
        this.auditId = auditId;
        this.auditCode = auditCode;
        this.tenantId = tenantId;
        this.title = title;
        this.category = category;
        this.standardReference = standardReference;
        this.leadAuditorId = leadAuditorId;
        this.plannedStartDate = plannedStartDate;
        this.plannedEndDate = plannedEndDate;
        this.status = status != null ? status : Status.PLANNED;
        this.findings = findings != null ? new ArrayList<>(findings) : new ArrayList<>();
        this.audit = audit != null ? audit : new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now());
    }

    public AuditFinding addFinding(String clauseReference, AuditFinding.Severity severity, String observation, String evidenceReference, AuditMetadata updateAudit) {
        AuditFinding finding = new AuditFinding(UUID.randomUUID(), clauseReference, severity, observation, evidenceReference, null);
        this.findings.add(finding);
        if (this.status == Status.PLANNED) {
            this.status = Status.IN_PROGRESS;
        }
        if (updateAudit != null) this.audit = updateAudit;
        return finding;
    }

    public void closeAudit(AuditMetadata updateAudit) {
        this.status = Status.CLOSED;
        if (updateAudit != null) this.audit = updateAudit;
    }

    private static String generateAuditCode() {
        return "AUD-" + System.currentTimeMillis() % 1000000;
    }

    public UUID getAuditId() { return auditId; }
    public String getAuditCode() { return auditCode; }
    public TenantId getTenantId() { return tenantId; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getStandardReference() { return standardReference; }
    public UUID getLeadAuditorId() { return leadAuditorId; }
    public LocalDate getPlannedStartDate() { return plannedStartDate; }
    public LocalDate getPlannedEndDate() { return plannedEndDate; }
    public Status getStatus() { return status; }
    public List<AuditFinding> getFindings() { return Collections.unmodifiableList(findings); }
    public AuditMetadata getAudit() { return audit; }
}
