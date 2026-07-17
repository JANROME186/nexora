package com.nexora.hop.platformfoundation.documentmanagement.domain;

import java.time.LocalDateTime;

public record StorageReference(
    StorageProvider storageProvider,
    String storageKey,
    LocalDateTime storedAt
) {
    public enum StorageProvider {
        LOCAL_FILESYSTEM,
        OBJECT_STORAGE_COMPATIBLE
    }
}
