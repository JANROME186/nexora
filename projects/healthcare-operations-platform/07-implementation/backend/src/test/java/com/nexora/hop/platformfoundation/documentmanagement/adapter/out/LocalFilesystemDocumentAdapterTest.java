package com.nexora.hop.platformfoundation.documentmanagement.adapter.out;

import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LocalFilesystemDocumentAdapterTest {

    @TempDir
    Path tempDir;

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
        StorageReference ref = new StorageReference(StorageReference.StorageProvider.LOCAL_FILESYSTEM, "non-existent-file.txt", java.time.LocalDateTime.now());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> adapter.getDocument(ref));
        assertEquals("Failed to retrieve document", thrown.getMessage());
    }
}
