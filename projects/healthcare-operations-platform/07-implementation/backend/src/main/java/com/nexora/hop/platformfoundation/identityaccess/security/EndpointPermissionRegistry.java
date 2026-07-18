package com.nexora.hop.platformfoundation.identityaccess.security;

import com.nexora.hop.platformfoundation.identityaccess.domain.PermissionCode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

/**
 * Central API/action permission map. New controllers must be registered here before they can be
 * considered ready by the enterprise foundation quality gate.
 */
@Component
public class EndpointPermissionRegistry {

  private static final List<PathRule> RULES =
      List.of(
              readWrite("/api/platform/tenants", PermissionCode.SCREEN_TENANTS, "BCM-ORG-001"),
              readWrite(
                  "/api/organization/laboratories",
                  PermissionCode.SCREEN_LABORATORIES,
                  "BCM-ORG-002"),
              readWrite(
                  "/api/organization/branches", PermissionCode.SCREEN_BRANCHES, "BCM-ORG-003"),
              readWrite("/api/identity/users", PermissionCode.SCREEN_USERS, "BCM-IAM-001"),
              readWrite("/api/audit/events", PermissionCode.SCREEN_AUDIT_EVENTS, "BCM-PLT-006"),
              readWrite("/api/catalog", PermissionCode.SCREEN_DIAGNOSTIC_CATALOG, "BCM-SVC"),
              readWrite("/api/people/persons", PermissionCode.SCREEN_PERSON_SEARCH, "BCM-PER-001"),
              readWrite("/api/people/patients", PermissionCode.SCREEN_PATIENTS, "BCM-PER-002"),
              readWrite("/api/people/doctors", PermissionCode.SCREEN_DOCTORS, "BCM-PER-003"),
              readWrite(
                  "/api/care-delivery/patient-registrations",
                  PermissionCode.SCREEN_PATIENT_REGISTRATIONS,
                  "BCM-ATT-002"),
              readWrite(
                  "/api/care-delivery/appointments",
                  PermissionCode.SCREEN_RECEPTION,
                  "BCM-ATT-001"),
              readWrite(
                  "/api/care-delivery/reception-visits",
                  PermissionCode.SCREEN_RECEPTION,
                  "BCM-ATT-003"),
              readWrite(
                  "/api/care-delivery/admission-requests",
                  PermissionCode.SCREEN_RECEPTION,
                  "BCM-ATT-004"),
              readWrite(
                  "/api/care-delivery/quotations",
                  PermissionCode.SCREEN_DIAGNOSTIC_ORDERS,
                  "BCM-ATT-006"),
              readWrite(
                  "/api/clinical-operations/diagnostic-orders",
                  PermissionCode.SCREEN_DIAGNOSTIC_ORDERS,
                  "BCM-CLI-001"),
              readWrite(
                  "/api/revenue/cashier/sessions",
                  PermissionCode.SCREEN_CASH_SESSIONS,
                  "BCM-REV-001"),
              readWrite("/api/revenue/cashier/sales", PermissionCode.SCREEN_SALES, "BCM-REV-002"),
              readWrite(
                  "/api/revenue/billing-requests",
                  PermissionCode.SCREEN_BILLING_REQUESTS,
                  "BCM-REV-003"),
              readWrite(
                  "/api/revenue/billing", PermissionCode.SCREEN_BILLING_REQUESTS, "BCM-REV-003"),
              readWrite(
                  "/api/clinical-operations/samples",
                  PermissionCode.SCREEN_SAMPLE_COLLECTION,
                  "BCM-CLI-002"),
              readWrite(
                  "/api/clinical-operations/laboratory-results",
                  PermissionCode.SCREEN_LABORATORY_PROCESSING,
                  "BCM-CLI-005"),
              readWrite("/api/results/search", PermissionCode.SCREEN_RESULT_SEARCH, "BCM-RES-001"),
              readWrite(
                  "/api/results/delivery", PermissionCode.SCREEN_RESULT_RELEASE, "BCM-RES-004"),
              readWrite(
                  "/api/results/critical-escalations",
                  PermissionCode.SCREEN_CRITICAL_ESCALATIONS,
                  "BCM-RES-006"))
          .stream()
          .sorted(Comparator.comparingInt((PathRule rule) -> rule.pathPrefix().length()).reversed())
          .toList();

  public Optional<EndpointAccessRule> resolve(String method, String requestPath) {
    if (requestPath == null || !requestPath.startsWith("/api/")) {
      return Optional.empty();
    }
    if ("/api/platform/health".equals(requestPath)) {
      return Optional.empty();
    }
    AccessAction action = actionFor(method);
    return RULES.stream()
        .filter(
            rule ->
                requestPath.equals(rule.pathPrefix())
                    || requestPath.startsWith(rule.pathPrefix() + "/"))
        .findFirst()
        .map(rule -> new EndpointAccessRule(rule.permission(), action, rule.capability()));
  }

  public boolean isMappedApi(String requestPath) {
    return resolve(HttpMethod.GET.name(), requestPath).isPresent();
  }

  private static AccessAction actionFor(String method) {
    if (HttpMethod.GET.matches(method)) {
      return AccessAction.READ;
    }
    if (HttpMethod.POST.matches(method)) {
      return AccessAction.CREATE;
    }
    if (HttpMethod.PUT.matches(method) || HttpMethod.PATCH.matches(method)) {
      return AccessAction.UPDATE;
    }
    if (HttpMethod.DELETE.matches(method)) {
      return AccessAction.DELETE;
    }
    return AccessAction.EXECUTE;
  }

  private static PathRule readWrite(
      String pathPrefix, PermissionCode permission, String capability) {
    return new PathRule(pathPrefix, permission, capability);
  }

  private record PathRule(String pathPrefix, PermissionCode permission, String capability) {}
}
