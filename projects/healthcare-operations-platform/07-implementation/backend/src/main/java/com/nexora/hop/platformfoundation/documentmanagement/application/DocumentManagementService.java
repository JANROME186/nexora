package com.nexora.hop.platformfoundation.documentmanagement.application;

import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentManagementException;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DocumentManagementService {

    private final DocumentStoragePort storagePort;
    private final Map<UUID, DocumentRecord> documentRegistry = new ConcurrentHashMap<>();
    private final Map<UUID, EvidencePackageRecord> packageRegistry = new ConcurrentHashMap<>();

    public record DocumentRecord(
            StoredDocument metadata,
            byte[] content,
            String complianceCategory,
            boolean legalHold
    ) {}

    public record EvidencePackageRecord(
            UUID packageId,
            String title,
            List<UUID> documentIds,
            Instant generatedAt
    ) {}

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
        return uploadDocument(
                tenantId,
                laboratoryId,
                ownerCapability,
                ownerReferenceId,
                documentVersion,
                content,
                contentType,
                "COMPLIANCE_EVIDENCE",
                retentionPolicy,
                audit
        );
    }

    public StoredDocument uploadDocument(
            TenantId tenantId,
            LaboratoryId laboratoryId,
            String ownerCapability,
            UUID ownerReferenceId,
            int documentVersion,
            byte[] content,
            String contentType,
            String complianceCategory,
            RetentionPolicy retentionPolicy,
            AuditMetadata audit) {

        byte[] safeContent = content != null ? content : new byte[0];
        String contentHash = computeHash(safeContent);
        StorageReference ref = storagePort != null
                ? storagePort.putDocument(safeContent, contentType)
                : new StorageReference(StorageReference.StorageProvider.LOCAL_FILESYSTEM, "local-" + UUID.randomUUID(), LocalDateTime.now());

        UUID docId = UUID.randomUUID();
        RetentionPolicy effectivePolicy = retentionPolicy != null ? retentionPolicy : RetentionPolicy.standard(LocalDate.now().plusYears(5));

        StoredDocument storedDocument = new StoredDocument(
                docId,
                tenantId != null ? tenantId : new TenantId(UUID.randomUUID().toString()),
                laboratoryId != null ? laboratoryId : new LaboratoryId(UUID.randomUUID().toString()),
                ownerCapability != null ? ownerCapability : "GENERAL",
                ownerReferenceId != null ? ownerReferenceId : UUID.randomUUID(),
                documentVersion > 0 ? documentVersion : 1,
                contentType != null ? contentType : "application/octet-stream",
                contentHash,
                safeContent.length,
                ref,
                effectivePolicy,
                audit != null ? audit : new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now())
        );

        DocumentRecord record = new DocumentRecord(
                storedDocument,
                safeContent,
                complianceCategory != null ? complianceCategory : "GENERAL",
                effectivePolicy.legalHold()
        );

        documentRegistry.put(docId, record);
        return storedDocument;
    }

    public Optional<DocumentRecord> getDocumentRecord(UUID documentId) {
        return Optional.ofNullable(documentRegistry.get(documentId));
    }

    public DocumentRecord updateLegalHold(UUID documentId, boolean legalHold) {
        DocumentRecord record = documentRegistry.get(documentId);
        if (record == null) {
            throw new DocumentManagementException("Document not found with ID: " + documentId);
        }
        DocumentRecord updated = new DocumentRecord(
                record.metadata(),
                record.content(),
                record.complianceCategory(),
                legalHold
        );
        documentRegistry.put(documentId, updated);
        return updated;
    }

    public EvidencePackageRecord createEvidencePackage(String title, List<UUID> documentIds) {
        UUID packageId = UUID.randomUUID();
        List<UUID> validDocIds = documentIds != null ? documentIds : List.of();
        EvidencePackageRecord pkg = new EvidencePackageRecord(
                packageId,
                title != null ? title : "Compliance Evidence Package",
                validDocIds,
                Instant.now()
        );
        packageRegistry.put(packageId, pkg);
        return pkg;
    }

    private String computeHash(byte[] content) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(content);
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new DocumentManagementException("Failed to compute SHA-256 hash", e);
        }
    }
}
