package com.nexora.hop.platformfoundation.organizationmanagement.application;

/** BCM-ORG-001 {@code provisionTenant} command (business-model.yaml {@code TenantRoot} fields). */
public record ProvisionTenantCommand(
        String code, String legalName, String tradeName, String taxId, String tier) {
}
