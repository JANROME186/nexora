package com.nexora.hop.platformfoundation.documentmanagement.domain;

public interface DocumentStoragePort {
    StorageReference putDocument(byte[] bytes, String contentType);
    byte[] getDocument(StorageReference reference);
    void deleteDocument(StorageReference reference);
}
