package com.nexora.hop.platformfoundation.identityaccess.application;

public record AssignRoleCommand(String roleCode, String scopeType, String scopeId, String actorUserId) {
}
