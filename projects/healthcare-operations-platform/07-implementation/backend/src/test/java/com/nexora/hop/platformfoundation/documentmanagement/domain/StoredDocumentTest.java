package com.nexora.hop.platformfoundation.documentmanagement.domain;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.LaboratoryId;
import com.nexora.hop.platformfoundation.sharedkernel.domain.ids.TenantId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class StoredDocumentTest {

    @Test
    void testStoredDocumentLifecycle() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        StorageReference ref = new StorageReference(StorageReference.StorageProvider.LOCAL_FILESYSTEM, "key", LocalDateTime.now());
        RetentionPolicy policy = new RetentionPolicy(LocalDate.now().plusDays(10), false);

        StoredDocument doc = new StoredDocument(
                UUID.randomUUID(),
                new TenantId("t1"),
                new LaboratoryId("l1"),
                "cap",
                UUID.randomUUID(),
                1,
                "application/pdf",
                "hash",
                100,
                ref,
                policy,
                audit
        );

        assertEquals(StoredDocument.Status.STORED, doc.getStatus());

        AuditMetadata update1 = new AuditMetadata("user2", LocalDateTime.now(), "user2", LocalDateTime.now());
        doc.supersede(update1);
        assertEquals(StoredDocument.Status.SUPERSEDED, doc.getStatus());

        // Test transitions that fail
        assertThrows(IllegalStateException.class, () -> doc.supersede(update1));
    }

    @Test
    void testStoredDocumentDisposal() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        StorageReference ref = new StorageReference(StorageReference.StorageProvider.LOCAL_FILESYSTEM, "key", LocalDateTime.now());
        RetentionPolicy policy = new RetentionPolicy(LocalDate.now().plusDays(10), false);

        StoredDocument doc = new StoredDocument(
                UUID.randomUUID(),
                new TenantId("t1"),
                new LaboratoryId("l1"),
                "cap",
                UUID.randomUUID(),
                1,
                "application/pdf",
                "hash",
                100,
                ref,
                policy,
                audit
        );

        AuditMetadata update1 = new AuditMetadata("user2", LocalDateTime.now(), "user2", LocalDateTime.now());
        doc.scheduleDisposal(update1);
        assertEquals(StoredDocument.Status.PENDING_DISPOSAL, doc.getStatus());

        doc.markDisposed(update1);
        assertEquals(StoredDocument.Status.DISPOSED, doc.getStatus());
        
        assertThrows(IllegalStateException.class, () -> doc.markDisposed(update1));
    }

    @Test
    void testStoredDocumentLegalHold() {
        AuditMetadata audit = new AuditMetadata("user", LocalDateTime.now(), "user", LocalDateTime.now());
        StorageReference ref = new StorageReference(StorageReference.StorageProvider.LOCAL_FILESYSTEM, "key", LocalDateTime.now());
        RetentionPolicy policy = new RetentionPolicy(LocalDate.now().plusDays(10), true); // legal hold

        StoredDocument doc = new StoredDocument(
                UUID.randomUUID(),
                new TenantId("t1"),
                new LaboratoryId("l1"),
                "cap",
                UUID.randomUUID(),
                1,
                "application/pdf",
                "hash",
                100,
                ref,
                policy,
                audit
        );

        AuditMetadata update1 = new AuditMetadata("user2", LocalDateTime.now(), "user2", LocalDateTime.now());
        assertThrows(IllegalStateException.class, () -> doc.scheduleDisposal(update1));
    }
}
