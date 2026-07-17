package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.delivery.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResultDeliveryTicket {
    
    private UUID ticketId;
    private ResultId resultId;
    private TenantId tenantId;
    private PatientId patientId;
    private String accessCode;
    private Status status;
    private LocalDateTime expiresAt;
    private AuditMetadata audit;

    public enum Status {
        AUTHORIZED,
        WITHHELD,
        EXPIRED
    }

    protected ResultDeliveryTicket() {}

    public ResultDeliveryTicket(
            UUID ticketId,
            ResultId resultId,
            TenantId tenantId,
            PatientId patientId,
            String accessCode,
            LocalDateTime expiresAt,
            AuditMetadata audit) {
        
        this.ticketId = ticketId;
        this.resultId = resultId;
        this.tenantId = tenantId;
        this.patientId = patientId;
        this.accessCode = accessCode;
        this.expiresAt = expiresAt;
        this.status = Status.AUTHORIZED;
        this.audit = audit;
    }

    public void withhold(AuditMetadata updateAudit) {
        this.status = Status.WITHHELD;
        this.audit = updateAudit;
    }

    public UUID getTicketId() { return ticketId; }
    public ResultId getResultId() { return resultId; }
    public TenantId getTenantId() { return tenantId; }
    public PatientId getPatientId() { return patientId; }
    public String getAccessCode() { return accessCode; }
    public Status getStatus() { return status; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public AuditMetadata getAudit() { return audit; }
}
