package com.nexora.hop.platformfoundation.documentmanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;

import java.util.UUID;

public class StoredDocument {
    
    private UUID documentId;
    private TenantId tenantId;
    private LaboratoryId laboratoryId;
    private String ownerCapability;
    private UUID ownerReferenceId;
    private int documentVersion;
    private String contentType;
    private String contentHash;
    private int sizeBytes;
    private StorageReference storageReference;
    private RetentionPolicy retentionPolicy;
    private Status status;
    private AuditMetadata audit;

    public enum Status {
        STORED,
        SUPERSEDED,
        PENDING_DISPOSAL,
        DISPOSED
    }

    protected StoredDocument() {} // For JPA/persistence frameworks

    public StoredDocument(
            UUID documentId,
            TenantId tenantId,
            LaboratoryId laboratoryId,
            String ownerCapability,
            UUID ownerReferenceId,
            int documentVersion,
            String contentType,
            String contentHash,
            int sizeBytes,
            StorageReference storageReference,
            RetentionPolicy retentionPolicy,
            AuditMetadata audit) {
        
        this.documentId = documentId;
        this.tenantId = tenantId;
        this.laboratoryId = laboratoryId;
        this.ownerCapability = ownerCapability;
        this.ownerReferenceId = ownerReferenceId;
        this.documentVersion = documentVersion;
        this.contentType = contentType;
        this.contentHash = contentHash;
        this.sizeBytes = sizeBytes;
        this.storageReference = storageReference;
        this.retentionPolicy = retentionPolicy;
        this.status = Status.STORED;
        this.audit = audit;
    }

    public void supersede(AuditMetadata updateAudit) {
        if (this.status != Status.STORED) {
            throw new IllegalStateException("Only STORED documents can be superseded");
        }
        this.status = Status.SUPERSEDED;
        this.audit = updateAudit;
    }

    public void scheduleDisposal(AuditMetadata updateAudit) {
        if (this.retentionPolicy != null && this.retentionPolicy.legalHold()) {
            throw new IllegalStateException("Cannot dispose document under legal hold");
        }
        this.status = Status.PENDING_DISPOSAL;
        this.audit = updateAudit;
    }

    public void markDisposed(AuditMetadata updateAudit) {
        if (this.status != Status.PENDING_DISPOSAL) {
            throw new IllegalStateException("Document must be pending disposal");
        }
        this.status = Status.DISPOSED;
        this.audit = updateAudit;
    }

    public UUID getDocumentId() { return documentId; }
    public TenantId getTenantId() { return tenantId; }
    public LaboratoryId getLaboratoryId() { return laboratoryId; }
    public String getOwnerCapability() { return ownerCapability; }
    public UUID getOwnerReferenceId() { return ownerReferenceId; }
    public int getDocumentVersion() { return documentVersion; }
    public String getContentType() { return contentType; }
    public String getContentHash() { return contentHash; }
    public int getSizeBytes() { return sizeBytes; }
    public StorageReference getStorageReference() { return storageReference; }
    public RetentionPolicy getRetentionPolicy() { return retentionPolicy; }
    public Status getStatus() { return status; }
    public AuditMetadata getAudit() { return audit; }
}
