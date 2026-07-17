package com.nexora.hop.platformfoundation.documentmanagement.application;

import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentStoragePort;
import com.nexora.hop.platformfoundation.documentmanagement.domain.RetentionPolicy;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StoredDocument;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@Service
public class DocumentManagementService {

    private final DocumentStoragePort storagePort;

    public DocumentManagementService(DocumentStoragePort storagePort) {
        this.storagePort = storagePort;
    }

    public StoredDocument storeDocument(
            TenantId tenantId,
            LaboratoryId laboratoryId,
            String ownerCapability,
            UUID ownerReferenceId,
            int documentVersion,
            byte[] content,
            String contentType,
            RetentionPolicy retentionPolicy,
            AuditMetadata audit) {

        String contentHash = computeHash(content);
        StorageReference ref = storagePort.putDocument(content, contentType);

        return new StoredDocument(
                UUID.randomUUID(),
                tenantId,
                laboratoryId,
                ownerCapability,
                ownerReferenceId,
                documentVersion,
                contentType,
                contentHash,
                content.length,
                ref,
                retentionPolicy,
                audit
        );
    }

    private String computeHash(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content);
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentManagementException("Failed to compute SHA-256 hash", e);
        }
    }
}
