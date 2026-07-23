package com.nexora.hop.platformfoundation.organizationmanagement.application;

/** BCM-ORG-001 {@code updateTenantStatus} command: activate, suspend or archive a tenant. */
public record UpdateTenantStatusCommand(String tenantId, String status, String reason) {
}
