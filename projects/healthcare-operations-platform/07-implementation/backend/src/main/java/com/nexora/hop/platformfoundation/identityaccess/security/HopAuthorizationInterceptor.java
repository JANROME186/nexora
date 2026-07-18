package com.nexora.hop.platformfoundation.identityaccess.security;

import com.nexora.hop.platformfoundation.identityaccess.application.AuthorizationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class HopAuthorizationInterceptor implements HandlerInterceptor {

  private final EndpointPermissionRegistry endpointPermissionRegistry;
  private final HopAuthenticationResolver authenticationResolver;
  private final AuthorizationService authorizationService;
  private final HopSecurityProperties properties;

  public HopAuthorizationInterceptor(
      EndpointPermissionRegistry endpointPermissionRegistry,
      HopAuthenticationResolver authenticationResolver,
      AuthorizationService authorizationService,
      HopSecurityProperties properties) {
    this.endpointPermissionRegistry = endpointPermissionRegistry;
    this.authenticationResolver = authenticationResolver;
    this.authorizationService = authorizationService;
    this.properties = properties;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws IOException {
    if (!properties.enforceRequestAuthorization()) {
      return true;
    }
    var requiredAccess =
        endpointPermissionRegistry.resolve(request.getMethod(), request.getRequestURI());
    if (requiredAccess.isEmpty()) {
      return true;
    }
    var context = authenticationResolver.resolve(request);
    if (context.isEmpty() || !context.get().hasRoles()) {
      writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "AUTHENTICATION_REQUIRED");
      return false;
    }
    AuthenticatedUserContextHolder.set(context.get());
    boolean allowed =
        authorizationService
            .permissionsForRoles(context.get().roleCodes())
            .contains(requiredAccess.get().permission());
    if (!allowed) {
      writeError(response, HttpServletResponse.SC_FORBIDDEN, "PERMISSION_DENIED");
      return false;
    }
    return true;
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    AuthenticatedUserContextHolder.clear();
  }

  private static void writeError(HttpServletResponse response, int status, String message)
      throws IOException {
    response.setStatus(status);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write("{\"status\":" + status + ",\"message\":\"" + message + "\"}");
  }
}
