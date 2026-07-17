package com.nexora.hop.platformfoundation.notificationmanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationRequest {
    
    private UUID notificationId;
    private TenantId tenantId;
    private LaboratoryId laboratoryId;
    private String recipientAddress;
    private Channel channel;
    private String subject;
    private String content;
    private Status status;
    private LocalDateTime requestedAt;
    private LocalDateTime dispatchedAt;
    private AuditMetadata audit;

    public enum Channel {
        EMAIL,
        SMS,
        PUSH
    }

    public enum Status {
        QUEUED,
        DISPATCHED,
        FAILED
    }

    protected NotificationRequest() {}

    public NotificationRequest(
            UUID notificationId,
            TenantId tenantId,
            LaboratoryId laboratoryId,
            String recipientAddress,
            Channel channel,
            String subject,
            String content,
            AuditMetadata audit) {
        
        this.notificationId = notificationId;
        this.tenantId = tenantId;
        this.laboratoryId = laboratoryId;
        this.recipientAddress = recipientAddress;
        this.channel = channel;
        this.subject = subject;
        this.content = content;
        this.status = Status.QUEUED;
        this.requestedAt = LocalDateTime.now();
        this.audit = audit;
    }

    public void dispatch(AuditMetadata updateAudit) {
        if (this.status != Status.QUEUED) {
            throw new IllegalStateException("Only QUEUED notifications can be dispatched");
        }
        this.status = Status.DISPATCHED;
        this.dispatchedAt = LocalDateTime.now();
        this.audit = updateAudit;
    }

    public void fail(AuditMetadata updateAudit) {
        this.status = Status.FAILED;
        this.audit = updateAudit;
    }

    public UUID getNotificationId() { return notificationId; }
    public TenantId getTenantId() { return tenantId; }
    public LaboratoryId getLaboratoryId() { return laboratoryId; }
    public String getRecipientAddress() { return recipientAddress; }
    public Channel getChannel() { return channel; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public Status getStatus() { return status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public LocalDateTime getDispatchedAt() { return dispatchedAt; }
    public AuditMetadata getAudit() { return audit; }
}
