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

    // Secure self-access boundary for PATIENT role
    if (!allowed && context.get().roleCodes().contains("PATIENT")) {
      String uri = request.getRequestURI();
      if (uri.startsWith("/api/people/patients/")) {
        String sub = uri.substring("/api/people/patients/".length());
        int slashIdx = sub.indexOf('/');
        String pathPatientId = slashIdx == -1 ? sub : sub.substring(0, slashIdx);
        if (pathPatientId.equals(context.get().userId())) {
          allowed = authorizationService
              .permissionsForRoles(context.get().roleCodes())
              .contains(com.nexora.hop.platformfoundation.identityaccess.domain.PermissionCode.PORTAL_PATIENT_PROFILE_VIEW);
        }
      } else if (uri.startsWith("/api/clinical-operations/laboratory-results/") && uri.endsWith("/notifications")) {
        allowed = authorizationService
            .permissionsForRoles(context.get().roleCodes())
            .contains(com.nexora.hop.platformfoundation.identityaccess.domain.PermissionCode.PORTAL_PATIENT_NOTIFICATIONS_VIEW);
      }
    }

    // Additional cross-patient data access prevention for results history
    if (allowed && context.get().roleCodes().contains("PATIENT")) {
      String uri = request.getRequestURI();
      if (uri.startsWith("/api/results/history/patient/")) {
        String pathPatientId = uri.substring("/api/results/history/patient/".length());
        if (!pathPatientId.equals(context.get().userId())) {
          allowed = false;
        }
      }
    }

    // Secure self-access boundary for REFERRING_DOCTOR role (COM-MOD-009-PORTAL-002 doctor
    // portal): the coarse permission check above only grants employee SCREEN_* permissions, so a
    // doctor is re-checked here against the PORTAL_DOCTOR_* permissions. Fine-grained ownership
    // (which patients this doctor actually referred) is enforced downstream by
    // DiagnosticOrderController's doctorId filter and ResultHistoryService's referral check,
    // since it requires real order data the interceptor does not have access to.
    if (!allowed && context.get().roleCodes().contains("REFERRING_DOCTOR")) {
      String uri = request.getRequestURI();
      String method = request.getMethod();
      var permissions = authorizationService.permissionsForRoles(context.get().roleCodes());
      if ("/api/clinical-operations/diagnostic-orders".equals(uri)
          && "GET".equalsIgnoreCase(method)
          && context.get().userId().equals(request.getParameter("doctorId"))) {
        allowed = permissions.contains(
            com.nexora.hop.platformfoundation.identityaccess.domain.PermissionCode.PORTAL_DOCTOR_ORDERS_VIEW);
      } else if (uri.startsWith("/api/results/history/patient/")) {
        allowed = permissions.contains(
            com.nexora.hop.platformfoundation.identityaccess.domain.PermissionCode.PORTAL_DOCTOR_RESULTS_VIEW);
      } else if (uri.startsWith("/api/clinical-operations/laboratory-results/") && uri.endsWith("/notifications")) {
        allowed = permissions.contains(
            com.nexora.hop.platformfoundation.identityaccess.domain.PermissionCode.PORTAL_DOCTOR_NOTIFICATIONS_VIEW);
      }
    }

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
