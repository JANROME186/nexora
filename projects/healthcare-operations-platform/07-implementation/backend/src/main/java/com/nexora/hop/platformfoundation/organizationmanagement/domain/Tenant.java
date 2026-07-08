package com.nexora.hop.platformfoundation.organizationmanagement.domain;

import java.time.Instant;

public record Tenant(String tenantId, String name, String status, Instant createdAt, Instant updatedAt) {
}
