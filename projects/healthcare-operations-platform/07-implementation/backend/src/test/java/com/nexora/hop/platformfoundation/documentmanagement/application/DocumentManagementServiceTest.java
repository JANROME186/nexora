package com.nexora.hop.platformfoundation.documentmanagement.application;

import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentStoragePort;
import com.nexora.hop.platformfoundation.documentmanagement.domain.RetentionPolicy;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StoredDocument;
import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DocumentManagementServiceTest {

    @Test
    void shouldStoreDocumentSuccessfully() {
        DocumentStoragePort storagePort = mock(DocumentStoragePort.class);
        when(storagePort.putDocument(any(), any())).thenReturn(new StorageReference(StorageReference.StorageProvider.LOCAL_FILESYSTEM, "path/to/file.pdf", java.time.LocalDateTime.now()));

        DocumentManagementService service = new DocumentManagementService(storagePort);

        AuditMetadata audit = new AuditMetadata("user-1", java.time.LocalDateTime.now(), "user-1", java.time.LocalDateTime.now());
        TenantId tenantId = new TenantId("tenant-1");
        LaboratoryId laboratoryId = new LaboratoryId("lab-1");
        byte[] content = "test content".getBytes();

        StoredDocument document = service.storeDocument(
                tenantId,
                laboratoryId,
                "capability",
                UUID.randomUUID(),
                1,
                content,
                "application/pdf",
                new RetentionPolicy(java.time.LocalDate.now().plusYears(10), false),
                audit
        );

        assertNotNull(document);
        assertNotNull(document.getDocumentId());
        assertEquals("tenant-1", document.getTenantId().value());
        assertEquals("lab-1", document.getLaboratoryId().value());
        assertEquals("application/pdf", document.getContentType());
        assertEquals(content.length, document.getSizeBytes());
        assertEquals(StorageReference.StorageProvider.LOCAL_FILESYSTEM, document.getStorageReference().storageProvider());
        assertNotNull(document.getContentHash());
    }
}
