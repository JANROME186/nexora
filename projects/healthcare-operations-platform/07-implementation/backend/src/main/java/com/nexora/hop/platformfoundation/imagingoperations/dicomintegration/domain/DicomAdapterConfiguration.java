package com.nexora.hop.platformfoundation.imagingoperations.dicomintegration.domain;

import java.time.Instant;

public record DicomAdapterConfiguration(
        String configurationId,
        String tenantId,
        String aeTitle,
        String host,
        int port,
        String modalityType,
        String connectionStatus,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
) {}
