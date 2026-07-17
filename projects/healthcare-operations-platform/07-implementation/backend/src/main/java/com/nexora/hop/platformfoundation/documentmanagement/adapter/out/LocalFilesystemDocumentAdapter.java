package com.nexora.hop.platformfoundation.documentmanagement.adapter.out;

import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentStoragePort;
import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentManagementException;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public final class LocalFilesystemDocumentAdapter implements DocumentStoragePort {

    private final Path storageDirectory;

    public LocalFilesystemDocumentAdapter(@Value("${hop.document-storage.local.path:/tmp/hop-documents}") String storagePath) {
        this.storageDirectory = new File(storagePath).toPath().toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException e) {
            throw new DocumentManagementException("Failed to initialize document storage directory", e);
        }
    }

    private Path resolveSafe(String storageKey) {
        if (storageKey == null || storageKey.contains("..")) {
            throw new IllegalArgumentException("Invalid storage key (path traversal attempt)");
        }
        Path filePath = storageDirectory.resolve(storageKey).normalize();
        if (!filePath.startsWith(storageDirectory)) {
            throw new IllegalArgumentException("Invalid storage key (path traversal attempt)");
        }
        return filePath;
    }

    @Override
    public StorageReference putDocument(byte[] bytes, String contentType) {
        String key = UUID.randomUUID().toString();
        Path filePath = resolveSafe(key);
        try {
            Files.write(filePath, bytes);
            return new StorageReference(StorageReference.StorageProvider.LOCAL_FILESYSTEM, key, LocalDateTime.now());
        } catch (IOException e) {
            throw new DocumentManagementException("Failed to store document", e);
        }
    }

    @Override
    public byte[] getDocument(StorageReference reference) {
        if (reference.storageProvider() != StorageReference.StorageProvider.LOCAL_FILESYSTEM) {
            throw new IllegalArgumentException("Unsupported storage provider: " + reference.storageProvider());
        }
        Path filePath = resolveSafe(reference.storageKey());
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new DocumentManagementException("Failed to retrieve document", e);
        }
    }

    @Override
    public void deleteDocument(StorageReference reference) {
        if (reference.storageProvider() != StorageReference.StorageProvider.LOCAL_FILESYSTEM) {
            throw new IllegalArgumentException("Unsupported storage provider: " + reference.storageProvider());
        }
        Path filePath = resolveSafe(reference.storageKey());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new DocumentManagementException("Failed to delete document", e);
        }
    }
}
