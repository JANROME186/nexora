package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.util.UUID;

public class ResultNotificationRequest {
    
    private UUID resultNotificationId;
    private ResultId resultId;
    private TenantId tenantId;
    private PatientId patientId;
    private UUID underlyingNotificationId;
    private AuditMetadata audit;

    protected ResultNotificationRequest() {}

    public ResultNotificationRequest(
            UUID resultNotificationId,
            ResultId resultId,
            TenantId tenantId,
            PatientId patientId,
            UUID underlyingNotificationId,
            AuditMetadata audit) {
        
        this.resultNotificationId = resultNotificationId;
        this.resultId = resultId;
        this.tenantId = tenantId;
        this.patientId = patientId;
        this.underlyingNotificationId = underlyingNotificationId;
        this.audit = audit;
    }

    public UUID getResultNotificationId() { return resultNotificationId; }
    public ResultId getResultId() { return resultId; }
    public TenantId getTenantId() { return tenantId; }
    public PatientId getPatientId() { return patientId; }
    public UUID getUnderlyingNotificationId() { return underlyingNotificationId; }
    public AuditMetadata getAudit() { return audit; }
}
