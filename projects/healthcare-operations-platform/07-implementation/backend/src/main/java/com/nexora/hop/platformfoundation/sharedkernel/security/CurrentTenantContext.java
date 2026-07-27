package com.nexora.hop.platformfoundation.sharedkernel.security;

import java.util.Optional;

/**
 * Request-scoped tenant identifier bound by {@code identityaccess}'s authorization interceptor,
 * exposed here (in the {@code OPEN} sharedkernel module) so business modules that do not depend on
 * {@code identityaccess} can still resolve the real authenticated tenant instead of fabricating
 * one. See TD-IAM-004.
 */
public final class CurrentTenantContext {

  private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

  private CurrentTenantContext() {}

  public static void set(String tenantId) {
    CURRENT.set(tenantId);
  }

  public static Optional<String> current() {
    return Optional.ofNullable(CURRENT.get());
  }

  public static void clear() {
    CURRENT.remove();
  }
}
