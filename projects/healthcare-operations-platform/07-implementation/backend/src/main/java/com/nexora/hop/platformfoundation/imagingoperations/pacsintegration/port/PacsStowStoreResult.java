package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.port;

import java.time.Instant;

public record PacsStowStoreResult(
        String storeId,
        String studyInstanceUid,
        String pacsNodeId,
        int instancesStored,
        String status,
        int responseCode,
        Instant storedAt
) {}
