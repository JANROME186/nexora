package com.nexora.hop.platformfoundation.documentmanagement.adapter.in.web;

import com.nexora.hop.platformfoundation.documentmanagement.application.DocumentManagementService;
import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentManagementException;
import com.nexora.hop.platformfoundation.documentmanagement.domain.RetentionPolicy;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StoredDocument;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documents")
public class DocumentManagementController {

    private final DocumentManagementService service;

    public DocumentManagementController(DocumentManagementService service) {
        this.service = service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StoredDocumentResponse> uploadDocument(
            @RequestParam("ownerCapability") String ownerCapability,
            @RequestParam("ownerReferenceId") String ownerReferenceId,
            @RequestParam(value = "complianceCategory", required = false) String complianceCategory,
            @RequestPart("file") MultipartFile file) throws IOException {

        byte[] content = file != null ? file.getBytes() : new byte[0];
        String contentType = file != null && file.getContentType() != null ? file.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        UUID ownerRef = parseUuidOrGenerate(ownerReferenceId);

        StoredDocument doc = service.uploadDocument(
                new TenantId(UUID.randomUUID().toString()),
                new LaboratoryId(UUID.randomUUID().toString()),
                ownerCapability,
                ownerRef,
                1,
                content,
                contentType,
                complianceCategory != null ? complianceCategory : "GENERAL",
                RetentionPolicy.standard(null),
                new AuditMetadata("system", LocalDateTime.now(), "system", LocalDateTime.now())
        );

        DocumentManagementService.DocumentRecord record = service.getDocumentRecord(doc.getDocumentId())
                .orElseThrow(() -> new DocumentManagementException("Document created but record not found"));

        return ResponseEntity.status(HttpStatus.CREATED).body(StoredDocumentResponse.from(record));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoredDocumentResponse> getDocumentMetadata(@PathVariable UUID id) {
        DocumentManagementService.DocumentRecord record = service.getDocumentRecord(id)
                .orElseThrow(() -> new DocumentManagementException("Document not found with ID: " + id));
        return ResponseEntity.ok(StoredDocumentResponse.from(record));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadDocumentBinary(@PathVariable UUID id) {
        DocumentManagementService.DocumentRecord record = service.getDocumentRecord(id)
                .orElseThrow(() -> new DocumentManagementException("Document not found with ID: " + id));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document-" + id + "\"")
                .contentType(MediaType.parseMediaType(record.metadata().getContentType()))
                .body(record.content());
    }

    @PutMapping("/{id}/legal-hold")
    public ResponseEntity<StoredDocumentResponse> updateLegalHold(
            @PathVariable UUID id,
            @RequestBody UpdateLegalHoldRequest request) {
        boolean legalHold = request != null && request.legalHold();
        DocumentManagementService.DocumentRecord updated = service.updateLegalHold(id, legalHold);
        return ResponseEntity.ok(StoredDocumentResponse.from(updated));
    }

    @PostMapping("/evidence-package")
    public ResponseEntity<EvidencePackageResponse> createComplianceEvidencePackage(
            @RequestBody CreateEvidencePackageRequest request) {
        String title = request != null ? request.title() : "Evidence Package";
        List<UUID> docIds = request != null && request.documentIds() != null ? request.documentIds() : List.of();

        DocumentManagementService.EvidencePackageRecord pkg = service.createEvidencePackage(title, docIds);
        return ResponseEntity.status(HttpStatus.CREATED).body(EvidencePackageResponse.from(pkg));
    }

    private static UUID parseUuidOrGenerate(String val) {
        if (val == null || val.isBlank()) {
            return UUID.randomUUID();
        }
        try {
            return UUID.fromString(val);
        } catch (IllegalArgumentException e) {
            return UUID.nameUUIDFromBytes(val.getBytes());
        }
    }

    public record UpdateLegalHoldRequest(boolean legalHold) {}

    public record CreateEvidencePackageRequest(String title, List<UUID> documentIds) {}

    public record EvidencePackageResponse(
            UUID packageId,
            String title,
            int documentCount,
            Instant generatedAt
    ) {
        static EvidencePackageResponse from(DocumentManagementService.EvidencePackageRecord pkg) {
            return new EvidencePackageResponse(
                    pkg.packageId(),
                    pkg.title(),
                    pkg.documentIds().size(),
                    pkg.generatedAt()
            );
        }
    }

    public record StoredDocumentResponse(
            UUID documentId,
            String ownerCapability,
            String ownerReferenceId,
            String contentType,
            String contentHash,
            long sizeBytes,
            String complianceCategory,
            boolean legalHold,
            String status
    ) {
        static StoredDocumentResponse from(DocumentManagementService.DocumentRecord record) {
            StoredDocument meta = record.metadata();
            return new StoredDocumentResponse(
                    meta.getDocumentId(),
                    meta.getOwnerCapability(),
                    meta.getOwnerReferenceId() != null ? meta.getOwnerReferenceId().toString() : "",
                    meta.getContentType(),
                    meta.getContentHash(),
                    meta.getSizeBytes(),
                    record.complianceCategory(),
                    record.legalHold(),
                    meta.getStatus() != null ? meta.getStatus().name() : "STORED"
            );
        }
    }
}
