package com.nexora.hop.platformfoundation.identityaccess.domain;

import java.time.Instant;

/**
 * A non-interactive principal (TD-IAM-003) that authenticates with a client id/secret pair
 * instead of a human username/password, and is granted exactly one role code rather than the
 * multi-role assignment model human {@link UserAccount}s use.
 */
public record ServiceAccountCredential(
        String serviceAccountId,
        String tenantId,
        String clientId,
        String clientSecretHash,
        String roleCode,
        String status,
        Instant createdAt) {

    public ServiceAccountCredential withStatus(String status) {
        return new ServiceAccountCredential(
                serviceAccountId, tenantId, clientId, clientSecretHash, roleCode, status, createdAt);
    }

    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }
}
