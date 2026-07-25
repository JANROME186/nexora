package com.nexora.hop.platformfoundation.imagingoperations.pacsintegration.domain;

import java.time.Instant;

public record PacsIntegrationEndpoint(
        String endpointId,
        String tenantId,
        String pacsNodeId,
        String baseUrl,
        String protocol,
        String status,
        String authCredentialsMasked,
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
) {}
