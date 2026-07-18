package com.nexora.hop.platformfoundation.documentmanagement.domain;

import java.util.Optional;
import java.util.UUID;

public interface StoredDocumentRepository {
    StoredDocument save(StoredDocument document);
    Optional<StoredDocument> findById(UUID documentId);
}
