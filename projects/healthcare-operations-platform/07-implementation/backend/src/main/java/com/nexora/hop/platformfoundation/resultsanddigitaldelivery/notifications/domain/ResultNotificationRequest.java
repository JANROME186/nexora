package com.nexora.hop.platformfoundation.resultsanddigitaldelivery.notifications.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.PatientId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.ResultId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.time.LocalDateTime;
import java.util.UUID;

public class ResultNotificationRequest {

    private UUID resultNotificationId;
    private ResultId resultId;
    private TenantId tenantId;
    private PatientId patientId;
    private UUID underlyingNotificationId; // legacy field for backward compatibility
    private AuditMetadata audit;

    // New business model fields
    private String recipientId;
    private String recipientType; // patient, patient_representative, referring_doctor
    private String triggerReason; // result_delivered, result_critical, result_amended
    private String composedTemplateReference;
    private UUID dispatchReference;
    private String dispatchStatus; // pending_submission, submitted, dispatched, delivered, failed
    private String channel; // sms, email (BCM-RES-007 dispatch channel used for this request)
    private LocalDateTime dispatchedAt;
    private LocalDateTime deliveredAt;
    private String failureReason;

    protected ResultNotificationRequest() {}

    // Legacy constructor
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
        // Default new fields
        this.recipientId = patientId.value();
        this.recipientType = "patient";
        this.triggerReason = "result_delivered";
        this.composedTemplateReference = "tpl_result_default";
        this.dispatchReference = underlyingNotificationId;
        this.dispatchStatus = "dispatched";
        this.channel = "sms";
    }

    // Full constructor
    public ResultNotificationRequest(
            UUID resultNotificationId,
            ResultId resultId,
            TenantId tenantId,
            PatientId patientId,
            String recipientId,
            String recipientType,
            String triggerReason,
            String composedTemplateReference,
            String channel,
            AuditMetadata audit) {

        this.resultNotificationId = resultNotificationId;
        this.resultId = resultId;
        this.tenantId = tenantId;
        this.patientId = patientId;
        this.recipientId = recipientId;
        this.recipientType = recipientType;
        this.triggerReason = triggerReason;
        this.composedTemplateReference = composedTemplateReference;
        this.channel = channel;
        this.dispatchStatus = "pending_submission";
        this.audit = audit;
    }

    public void submit(UUID dispatchRef, AuditMetadata updateAudit) {
        this.dispatchReference = dispatchRef;
        this.underlyingNotificationId = dispatchRef;
        this.dispatchStatus = "submitted";
        this.dispatchedAt = LocalDateTime.now();
        this.audit = updateAudit;
    }

    public void updateDispatchStatus(String status, AuditMetadata updateAudit) {
        this.dispatchStatus = status;
        this.audit = updateAudit;
    }

    public UUID getResultNotificationId() { return resultNotificationId; }
    public ResultId getResultId() { return resultId; }
    public TenantId getTenantId() { return tenantId; }
    public PatientId getPatientId() { return patientId; }
    public UUID getUnderlyingNotificationId() { return underlyingNotificationId; }
    public AuditMetadata getAudit() { return audit; }

    public String getRecipientId() { return recipientId; }
    public String getRecipientType() { return recipientType; }
    public String getTriggerReason() { return triggerReason; }
    public String getComposedTemplateReference() { return composedTemplateReference; }
    public UUID getDispatchReference() { return dispatchReference; }
    public String getDispatchStatus() { return dispatchStatus; }
    public String getChannel() { return channel; }
    public LocalDateTime getDispatchedAt() { return dispatchedAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public String getFailureReason() { return failureReason; }
}
