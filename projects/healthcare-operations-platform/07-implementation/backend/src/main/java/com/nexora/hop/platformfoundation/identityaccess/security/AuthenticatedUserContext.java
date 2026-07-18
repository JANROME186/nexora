package com.nexora.hop.platformfoundation.identityaccess.security;

import java.util.List;

/** Request-scoped authenticated principal resolved before controller execution. */
public record AuthenticatedUserContext(
    String userId, String tenantId, String branchId, List<String> roleCodes, boolean fixture) {

  public AuthenticatedUserContext {
    roleCodes = roleCodes == null ? List.of() : List.copyOf(roleCodes);
  }

  public boolean hasRoles() {
    return !roleCodes.isEmpty();
  }
}
