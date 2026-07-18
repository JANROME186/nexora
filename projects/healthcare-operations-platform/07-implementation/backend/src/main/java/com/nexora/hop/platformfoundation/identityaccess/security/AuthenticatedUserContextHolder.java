package com.nexora.hop.platformfoundation.identityaccess.security;

import java.util.Optional;

/** Thread-local holder for the authenticated user bound to the current HTTP request. */
public final class AuthenticatedUserContextHolder {

  private static final ThreadLocal<AuthenticatedUserContext> CURRENT = new ThreadLocal<>();

  private AuthenticatedUserContextHolder() {}

  public static void set(AuthenticatedUserContext context) {
    CURRENT.set(context);
  }

  public static Optional<AuthenticatedUserContext> current() {
    return Optional.ofNullable(CURRENT.get());
  }

  public static void clear() {
    CURRENT.remove();
  }
}
