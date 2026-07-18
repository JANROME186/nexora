package com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement.domain;

import java.util.List;

import com.nexora.hop.platformfoundation.sharedkernel.domain.AuditMetadata;

/**
 * Child entity of BCM-PLT-005 (ENT-APIM-002). A tenant-scoped credential granting access to a set
 * of partner-classified API operations (RN-002/INV-APIM-002: valid, non-revoked, non-expired,
 * scope- and tenant-matched).
 */
public record PartnerApiKey(
        String keyId,
        String tenantId,
        String consumerName,
        List<String> grantedScopes,
        String rateLimitPolicyRef,
        String status,
        AuditMetadata audit) {

    public static final String STATUS_ACTIVE = "active";
    public static final String STATUS_REVOKED = "revoked";
    public static final String STATUS_EXPIRED = "expired";

    public boolean isUsable() {
        return STATUS_ACTIVE.equals(status);
    }
}
