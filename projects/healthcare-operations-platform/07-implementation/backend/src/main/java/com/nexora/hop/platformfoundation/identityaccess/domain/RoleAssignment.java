package com.nexora.hop.platformfoundation.identityaccess.domain;

import java.time.Instant;

public record RoleAssignment(
        String roleAssignmentId,
        String userId,
        String roleCode,
        String scopeType,
        String scopeId,
        Instant createdAt,
        String createdBy) {
}
