package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.criticalresults.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.UserId;

import java.time.LocalDateTime;
import java.util.UUID;

public class CriticalResultEscalation {

    private UUID escalationId;
    private TenantId tenantId;
    private LaboratoryId laboratoryId;
    private ResultId resultId;
    private String criticalReason;
    private UserId assignedHandlerId;
    private int escalationTier;
    private LocalDateTime acknowledgementDeadline;
    private UserId acknowledgedBy;
    private LocalDateTime acknowledgedAt;
    private Status status;
    private AuditMetadata audit;

    public enum Status {
        OPEN,
        ACKNOWLEDGED,
        ESCALATED,
        CLOSED
    }

    protected CriticalResultEscalation() {}

    public CriticalResultEscalation(
            UUID escalationId,
            TenantId tenantId,
            LaboratoryId laboratoryId,
            ResultId resultId,
            String criticalReason,
            LocalDateTime acknowledgementDeadline,
            AuditMetadata audit) {

        this.escalationId = escalationId;
        this.tenantId = tenantId;
        this.laboratoryId = laboratoryId;
        this.resultId = resultId;
        this.criticalReason = criticalReason;
        this.escalationTier = 1;
        this.acknowledgementDeadline = acknowledgementDeadline;
        this.status = Status.OPEN;
        this.audit = audit;
    }

    public void assignHandler(UserId handlerId, AuditMetadata updateAudit) {
        this.assignedHandlerId = handlerId;
        this.audit = updateAudit;
    }

    public void escalate(LocalDateTime nextDeadline, AuditMetadata updateAudit) {
        this.escalationTier += 1;
        this.status = Status.ESCALATED;
        this.acknowledgementDeadline = nextDeadline;
        this.audit = updateAudit;
    }

    public void acknowledge(UserId userId, LocalDateTime time, AuditMetadata updateAudit) {
        this.acknowledgedBy = userId;
        this.acknowledgedAt = time;
        this.status = Status.ACKNOWLEDGED;
        this.audit = updateAudit;
    }

    public void close(AuditMetadata updateAudit) {
        // RN-003: An escalation can be closed only after both acknowledgedBy and acknowledgedAt are recorded
        if (this.acknowledgedBy == null || this.acknowledgedAt == null) {
            throw new IllegalStateException("ESCALATION_ACKNOWLEDGEMENT_INCOMPLETE");
        }
        this.status = Status.CLOSED;
        this.audit = updateAudit;
    }

    public UUID getEscalationId() { return escalationId; }
    public TenantId getTenantId() { return tenantId; }
    public LaboratoryId getLaboratoryId() { return laboratoryId; }
    public ResultId getResultId() { return resultId; }
    public String getCriticalReason() { return criticalReason; }
    public UserId getAssignedHandlerId() { return assignedHandlerId; }
    public int getEscalationTier() { return escalationTier; }
    public LocalDateTime getAcknowledgementDeadline() { return acknowledgementDeadline; }
    public UserId getAcknowledgedBy() { return acknowledgedBy; }
    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public Status getStatus() { return status; }
    public AuditMetadata getAudit() { return audit; }
}
