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

    // New business-model fields
    private String recipientType; // patient, patient_representative, referring_doctor
    private String recipientId;
    private String deliveryChannel; // patient_portal, doctor_portal, mobile_app
    private DeliveryAuthorizationCheck authorizationCheck;
    private LocalDateTime deliveredAt;
    private LocalDateTime viewedAt;

    public enum Status {
        PENDING_AUTHORIZATION,
        AUTHORIZED,
        DELIVERED,
        VIEWED,
        WITHHELD,
        EXPIRED
    }

    protected ResultDeliveryTicket() {}

    // Legacy constructor for backward compatibility
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
        this.recipientType = "patient";
        this.recipientId = patientId.value();
        this.deliveryChannel = "patient_portal";
        this.authorizationCheck = new DeliveryAuthorizationCheck(true, true, false, LocalDateTime.now());
    }

    // Full business model constructor
    public ResultDeliveryTicket(
            UUID ticketId,
            ResultId resultId,
            TenantId tenantId,
            PatientId patientId,
            String accessCode,
            LocalDateTime expiresAt,
            String recipientType,
            String recipientId,
            String deliveryChannel,
            DeliveryAuthorizationCheck authorizationCheck,
            AuditMetadata audit) {

        this.ticketId = ticketId;
        this.resultId = resultId;
        this.tenantId = tenantId;
        this.patientId = patientId;
        this.accessCode = accessCode;
        this.expiresAt = expiresAt;
        this.status = Status.AUTHORIZED;
        this.recipientType = recipientType;
        this.recipientId = recipientId;
        this.deliveryChannel = deliveryChannel;
        this.authorizationCheck = authorizationCheck;
        this.audit = audit;
    }

    public void withhold(AuditMetadata updateAudit) {
        this.status = Status.WITHHELD;
        this.audit = updateAudit;
    }

    public void markViewed(LocalDateTime viewedTime, AuditMetadata updateAudit) {
        this.status = Status.VIEWED;
        this.viewedAt = viewedTime;
        this.audit = updateAudit;
    }

    public void markDelivered(LocalDateTime deliveredTime, AuditMetadata updateAudit) {
        this.status = Status.DELIVERED;
        this.deliveredAt = deliveredTime;
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

    public String getRecipientType() { return recipientType; }
    public String getRecipientId() { return recipientId; }
    public String getDeliveryChannel() { return deliveryChannel; }
    public DeliveryAuthorizationCheck getAuthorizationCheck() { return authorizationCheck; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public LocalDateTime getViewedAt() { return viewedAt; }
}
