package com.nexora.hop.platformfoundation.documentmanagement.adapter.out;

import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentManagementException;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LocalFilesystemDocumentAdapterTest {

    Path tempDir;

    @BeforeEach
    void createWorkspaceTempDir() throws IOException {
        tempDir = Path.of("target", "test-documents", UUID.randomUUID().toString());
        Files.createDirectories(tempDir);
    }

    @Test
    void shouldPutAndGetDocumentSuccessfully() throws IOException {
        LocalFilesystemDocumentAdapter adapter = new LocalFilesystemDocumentAdapter(tempDir.toString());
        byte[] content = "hello world".getBytes();

        StorageReference ref = adapter.putDocument(content, "text/plain");
        assertNotNull(ref);
        assertEquals(StorageReference.StorageProvider.LOCAL_FILESYSTEM, ref.storageProvider());

        byte[] retrieved = adapter.getDocument(ref);
        assertArrayEquals(content, retrieved);
    }

    @Test
    void shouldThrowExceptionWhenDocumentNotFound() {
        LocalFilesystemDocumentAdapter adapter = new LocalFilesystemDocumentAdapter(tempDir.toString());
        StorageReference ref = new StorageReference(
                StorageReference.StorageProvider.LOCAL_FILESYSTEM, "non-existent-file.txt", LocalDateTime.now());

        DocumentManagementException thrown = assertThrows(DocumentManagementException.class, () -> adapter.getDocument(ref));
        assertEquals("Failed to retrieve document", thrown.getMessage());
    }

    @Test
    void shouldThrowExceptionOnPathTraversalAttempt() {
        LocalFilesystemDocumentAdapter adapter = new LocalFilesystemDocumentAdapter(tempDir.toString());
        StorageReference ref = new StorageReference(
                StorageReference.StorageProvider.LOCAL_FILESYSTEM, "../escaped-file.txt", LocalDateTime.now());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> adapter.getDocument(ref));
        assertTrue(thrown.getMessage().contains("path traversal attempt"));
    }

    @Test
    void shouldDeleteStoredDocumentAndRejectUnsupportedProvider() throws IOException {
        LocalFilesystemDocumentAdapter adapter = new LocalFilesystemDocumentAdapter(tempDir.toString());
        StorageReference stored = adapter.putDocument("delete me".getBytes(), "text/plain");

        adapter.deleteDocument(stored);
        assertThrows(DocumentManagementException.class, () -> adapter.getDocument(stored));

        StorageReference unsupported = new StorageReference(
                StorageReference.StorageProvider.OBJECT_STORAGE_COMPATIBLE, "object-storage-key", LocalDateTime.now());
        assertThrows(IllegalArgumentException.class, () -> adapter.getDocument(unsupported));
        assertThrows(IllegalArgumentException.class, () -> adapter.deleteDocument(unsupported));
    }
}
