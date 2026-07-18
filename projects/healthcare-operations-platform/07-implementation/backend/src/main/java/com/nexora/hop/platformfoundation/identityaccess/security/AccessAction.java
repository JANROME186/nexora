package com.nexora.hop.platformfoundation.identityaccess.security;

/** HTTP-facing action used to bind every backend endpoint to an auditable IAM decision. */
public enum AccessAction {
  READ,
  CREATE,
  UPDATE,
  DELETE,
  EXECUTE
}
