package com.nexora.hop.platformfoundation.organizationmanagement.domain;

import java.time.Instant;

/**
 * BCM-ORG-001 {@code TenantRoot} aggregate root. {@code tier} and {@code isolationStrategy} are
 * intentionally plain strings rather than Java enums: they are persisted verbatim and validated
 * against {@link TenantLifecycle} at the application boundary, matching the pattern already used
 * by {@code status} so the JDBC and in-memory adapters stay symmetrical.
 */
public record Tenant(
        String tenantId,
        String code,
        String legalName,
        String tradeName,
        String taxId,
        String status,
        String tier,
        String isolationStrategy,
        Instant createdAt,
        Instant updatedAt) {
}
