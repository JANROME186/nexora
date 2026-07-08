package com.nexora.hop.platformfoundation.organizationmanagement.domain;

import java.time.Instant;

public record Branch(
        String branchId,
        String tenantId,
        String laboratoryId,
        String name,
        String status,
        Instant createdAt,
        Instant updatedAt) {
}
