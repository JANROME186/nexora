package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.port;

import java.time.Instant;

public record DicomTransferResult(
        String transferId,
        String studyInstanceUid,
        String destinationAeTitle,
        String status,
        int transferredInstances,
        long durationMs,
        Instant transferredAt
) {}
