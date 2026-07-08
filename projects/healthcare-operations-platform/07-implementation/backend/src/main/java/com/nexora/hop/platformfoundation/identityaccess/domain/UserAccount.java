package com.nexora.hop.platformfoundation.identityaccess.domain;

import java.time.Instant;

public record UserAccount(
        String userId,
        String tenantId,
        String displayName,
        String email,
        String status,
        Instant createdAt,
        Instant updatedAt) {
}
