package com.nexora.hop.platformfoundation.identityaccess.application;

public record CreateUserCommand(String tenantId, String displayName, String email) {
}
