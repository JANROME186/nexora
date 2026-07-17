package com.nexora.hop.platformfoundation.documentmanagement.adapter.out;

import com.nexora.hop.platformfoundation.documentmanagement.domain.DocumentStoragePort;
import com.nexora.hop.platformfoundation.documentmanagement.domain.StorageReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class LocalFilesystemDocumentAdapter implements DocumentStoragePort {

    private final Path storageDirectory;

    public LocalFilesystemDocumentAdapter(@Value("${hop.document-storage.local.path:/tmp/hop-documents}") String storagePath) {
        this.storageDirectory = Paths.get(storagePath);
        try {
            Files.createDirectories(this.storageDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize document storage directory", e);
        }
    }

    @Override
    public StorageReference putDocument(byte[] bytes, String contentType) {
        String key = UUID.randomUUID().toString();
        Path filePath = storageDirectory.resolve(key);
        try {
            Files.write(filePath, bytes);
            return new StorageReference(StorageReference.StorageProvider.LOCAL_FILESYSTEM, key, LocalDateTime.now());
        } catch (IOException e) {
            throw new RuntimeException("Failed to store document", e);
        }
    }

    @Override
    public byte[] getDocument(StorageReference reference) {
        if (reference.storageProvider() != StorageReference.StorageProvider.LOCAL_FILESYSTEM) {
            throw new IllegalArgumentException("Unsupported storage provider: " + reference.storageProvider());
        }
        Path filePath = storageDirectory.resolve(reference.storageKey());
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve document", e);
        }
    }

    @Override
    public void deleteDocument(StorageReference reference) {
        if (reference.storageProvider() != StorageReference.StorageProvider.LOCAL_FILESYSTEM) {
            throw new IllegalArgumentException("Unsupported storage provider: " + reference.storageProvider());
        }
        Path filePath = storageDirectory.resolve(reference.storageKey());
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete document", e);
        }
    }
}
