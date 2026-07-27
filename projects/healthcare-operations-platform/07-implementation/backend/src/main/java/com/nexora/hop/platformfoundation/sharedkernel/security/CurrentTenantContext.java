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

  /**
   * TD-DB-004: whether the current request's role set grants platform-wide, cross-tenant reach
   * (e.g. the {@code ADMIN} role). Consumed by {@code TenantSessionDataSource} to bypass the
   * database-layer row-level-security policy for legitimate cross-tenant platform operations
   * (tenant provisioning, support assistance), which is why RLS remains defense-in-depth rather
   * than the sole enforcement point: application-level authorization still decides who may reach
   * a given endpoint in the first place.
   */
  private static final ThreadLocal<Boolean> PLATFORM_BYPASS = new ThreadLocal<>();

  private CurrentTenantContext() {}

  public static void set(String tenantId) {
    set(tenantId, false);
  }

  public static void set(String tenantId, boolean platformBypass) {
    CURRENT.set(tenantId);
    PLATFORM_BYPASS.set(platformBypass);
  }

  public static Optional<String> current() {
    return Optional.ofNullable(CURRENT.get());
  }

  public static boolean isPlatformBypass() {
    return Boolean.TRUE.equals(PLATFORM_BYPASS.get());
  }

  public static void clear() {
    CURRENT.remove();
    PLATFORM_BYPASS.remove();
  }
}
