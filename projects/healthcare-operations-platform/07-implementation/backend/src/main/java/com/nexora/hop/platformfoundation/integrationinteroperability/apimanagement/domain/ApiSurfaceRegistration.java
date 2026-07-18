package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain;

import java.time.LocalDateTime;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Root aggregate of BCM-PLT-005 API Management (ENT-APIM-001). Governs public/internal/partner
 * classification and deprecation lifecycle for another capability's API operation; never
 * implements that capability's own business logic (INV-APIM-004). An operation with no
 * registration defaults to internal (INV-APIM-001).
 */
public record ApiSurfaceRegistration(
        String registrationId,
        String tenantId,
        String ownerCapability,
        String operationId,
        String classification,
        String apiVersion,
        String deprecationStatus,
        LocalDateTime deprecationWindowFrom,
        LocalDateTime deprecationWindowTo,
        String migrationNote,
        AuditMetadata audit) {

    public static final String CLASSIFICATION_PUBLIC = "public";
    public static final String CLASSIFICATION_INTERNAL = "internal";
    public static final String CLASSIFICATION_PARTNER = "partner";

    public static final String DEPRECATION_ACTIVE = "active";
    public static final String DEPRECATION_SCHEDULED = "deprecation_scheduled";
    public static final String DEPRECATION_RETIRED = "retired";
}
