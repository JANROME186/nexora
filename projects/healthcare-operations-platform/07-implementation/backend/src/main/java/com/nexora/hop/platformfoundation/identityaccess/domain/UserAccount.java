package com.nexora.hop.platformfoundation.identityaccess.domain;

import java.time.Instant;

public record UserAccount(
        String userId,
        String tenantId,
        String displayName,
        String email,
        String status,
        Instant createdAt,
        Instant updatedAt,
        String username,
        String passwordHash,
        int failedLoginAttempts,
        Instant lockedUntil,
        Instant lastLoginAt) {

    public UserAccount(
            String userId,
            String tenantId,
            String displayName,
            String email,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        this(userId, tenantId, displayName, email, status, createdAt, updatedAt, email, "", 0, null, null);
    }

    public UserAccount withPasswordHash(String passwordHash) {
        return new UserAccount(userId, tenantId, displayName, email, status, createdAt, updatedAt, username, passwordHash, failedLoginAttempts, lockedUntil, lastLoginAt);
    }

    public UserAccount withStatus(String status) {
        return new UserAccount(userId, tenantId, displayName, email, status, createdAt, updatedAt, username, passwordHash, failedLoginAttempts, lockedUntil, lastLoginAt);
    }

    public UserAccount withFailedLoginAttempts(int failedLoginAttempts) {
        return new UserAccount(userId, tenantId, displayName, email, status, createdAt, updatedAt, username, passwordHash, failedLoginAttempts, lockedUntil, lastLoginAt);
    }

    public UserAccount withLockedUntil(Instant lockedUntil) {
        return new UserAccount(userId, tenantId, displayName, email, status, createdAt, updatedAt, username, passwordHash, failedLoginAttempts, lockedUntil, lastLoginAt);
    }

    public UserAccount withLastLoginAt(Instant lastLoginAt) {
        return new UserAccount(userId, tenantId, displayName, email, status, createdAt, updatedAt, username, passwordHash, failedLoginAttempts, lockedUntil, lastLoginAt);
    }
}
