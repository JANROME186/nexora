package com.nexora.hop.platformfoundation.identityaccess.domain;

/**
 * The {@code domain.resource.action.scope} permission grammar from AUTHZ-ARCH-001, modeled
 * alongside the coarser {@link PermissionCode} screen-level enum (TD-IAM-002, TD-IAM-003). This is
 * introduced incrementally, one endpoint at a time, rather than replacing {@link PermissionCode}
 * wholesale.
 */
public record PermissionScope(String domain, String resource, String action, String scope) {

  public static final PermissionScope QUALITY_CAPA_APPROVE_TENANT =
      new PermissionScope("quality", "capa", "approve", "tenant");

  public String grammar() {
    return domain + "." + resource + "." + action + "." + scope;
  }
}
