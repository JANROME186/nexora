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
              readWrite("/api/identity/service-accounts", PermissionCode.SCREEN_USERS, "BCM-IAM-001"),
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
              readWrite("/api/results/history/patient", PermissionCode.PORTAL_PATIENT_RESULTS_VIEW, "BCM-RES-005"),
              readWrite(
                  "/api/results/delivery", PermissionCode.SCREEN_RESULT_RELEASE, "BCM-RES-004"),
              readWrite(
                  "/api/results/critical-escalations",
                  PermissionCode.SCREEN_CRITICAL_ESCALATIONS,
                  "BCM-RES-006"),
              readWrite(
                  "/api/platform/integration",
                  PermissionCode.SCREEN_INTEGRATION_ENDPOINTS,
                  "BCM-PLT-004"),
              readWrite(
                  "/api/platform/api-management",
                  PermissionCode.SCREEN_API_MANAGEMENT,
                  "BCM-PLT-005"),
              readWrite(
                  "/api/platform/migration",
                  PermissionCode.SCREEN_MIGRATION_JOBS,
                  "BCM-PLT-010"),
              readWrite(
                  "/api/platform/config",
                  PermissionCode.SCREEN_PLATFORM_CONFIGURATION,
                  "BCM-PLT-002"),
              readWrite(
                  "/api/platform/feature-flags",
                  PermissionCode.SCREEN_PLATFORM_CONFIGURATION,
                  "BCM-PLT-002"),
              readWrite(
                  "/api/inventory/catalog",
                  PermissionCode.SCREEN_INVENTORY_CATALOG,
                  "BCM-INV-001"),
              readWrite(
                  "/api/inventory/reagents",
                  PermissionCode.SCREEN_INVENTORY_REAGENTS,
                  "BCM-INV-002"),
              readWrite(
                  "/api/inventory/lots",
                  PermissionCode.SCREEN_INVENTORY_LOTS,
                  "BCM-INV-003"),
              readWrite(
                  "/api/inventory/purchase-orders",
                  PermissionCode.SCREEN_INVENTORY_PROCUREMENT,
                  "BCM-INV-004"),
              readWrite(
                  "/api/inventory/stock-entries",
                  PermissionCode.SCREEN_INVENTORY_STOCK_MOVEMENTS,
                  "BCM-INV-005"),
              readWrite(
                  "/api/inventory/stock-exits",
                  PermissionCode.SCREEN_INVENTORY_STOCK_MOVEMENTS,
                  "BCM-INV-006"),
              readWrite(
                  "/api/inventory/consumption",
                  PermissionCode.SCREEN_INVENTORY_STOCK_MOVEMENTS,
                  "BCM-INV-007"),
              readWrite(
                  "/api/inventory/adjustments",
                  PermissionCode.SCREEN_INVENTORY_ADJUSTMENTS,
                  "BCM-INV-008"),
              readWrite(
                  "/api/inventory/waste",
                  PermissionCode.SCREEN_INVENTORY_WASTE,
                  "BCM-INV-009"),
              readWrite(
                  "/api/quality/internal-controls",
                  PermissionCode.SCREEN_INTERNAL_QUALITY_CONTROLS,
                  "BCM-QLT-001"),
              readWrite(
                  "/api/quality/external-controls",
                  PermissionCode.SCREEN_EXTERNAL_QUALITY_CONTROLS,
                  "BCM-QLT-002"),
              readWrite(
                  "/api/quality/capa",
                  PermissionCode.SCREEN_CAPA_MANAGEMENT,
                  "BCM-QLT-006"),
              readWrite(
                  "/api/quality/audits",
                  PermissionCode.SCREEN_AUDIT_MANAGEMENT,
                  "BCM-QLT-007"),
              readWrite(
                  "/api/quality/events",
                  PermissionCode.SCREEN_CAPA_MANAGEMENT,
                  "BCM-QLT-006"),
              readWrite(
                  "/api/documents",
                  PermissionCode.SCREEN_DOCUMENT_MANAGEMENT,
                  "BCM-PLT-008"),
              readWrite(
                  "/api/quality/calibrations",
                  PermissionCode.SCREEN_CALIBRATIONS,
                  "BCM-QLT-003"),
              readWrite(
                  "/api/quality/equipment",
                  PermissionCode.SCREEN_EQUIPMENT,
                  "BCM-QLT-004"),
              readWrite(
                  "/api/quality/maintenance",
                  PermissionCode.SCREEN_MAINTENANCE,
                  "BCM-QLT-005"),
              readWrite(
                  "/api/auth/assistance",
                  PermissionCode.PORTAL_SUPPORT_IMPERSONATE,
                  "BCM-PLT-001"),
              readWrite(
                  "/api/marketplace/packages",
                  PermissionCode.SCREEN_MARKETPLACE_PACKAGES,
                  "BCM-PLT-011"),
              readWrite(
                  "/api/marketplace/compatibility",
                  PermissionCode.SCREEN_MARKETPLACE_PACKAGES,
                  "BCM-PLT-011"),
              readWrite(
                  "/api/marketplace/offers",
                  PermissionCode.SCREEN_MARKETPLACE_OFFERS,
                  "BCM-PLT-011"),
              readWrite(
                  "/api/marketplace/entitlements",
                  PermissionCode.SCREEN_MARKETPLACE_ENTITLEMENTS,
                  "BCM-PLT-011"),
              readWrite(
                  "/api/marketplace/installations",
                  PermissionCode.SCREEN_MARKETPLACE_INSTALLATIONS,
                  "BCM-PLT-011"),
              readWrite(
                  "/api/marketplace/billing",
                  PermissionCode.SCREEN_MARKETPLACE_INSTALLATIONS,
                  "BCM-PLT-011"),
              readWrite(
                  "/api/v1/imaging/bcm-img-001",
                  PermissionCode.SCREEN_IMAGING_APPOINTMENTS,
                  "BCM-IMG-001"),
              readWrite(
                  "/api/v1/imaging/bcm-img-002",
                  PermissionCode.SCREEN_IMAGING_RECEPTION,
                  "BCM-IMG-002"),
              readWrite(
                  "/api/v1/imaging/bcm-img-003",
                  PermissionCode.SCREEN_IMAGING_STUDIES,
                  "BCM-IMG-003"),
              readWrite(
                  "/api/v1/imaging/bcm-img-004",
                  PermissionCode.SCREEN_IMAGING_DICOM,
                  "BCM-IMG-004"),
              readWrite(
                  "/api/v1/imaging/bcm-img-005",
                  PermissionCode.SCREEN_IMAGING_PACS,
                  "BCM-IMG-005"),
              readWrite(
                  "/api/v1/imaging/bcm-img-006",
                  PermissionCode.SCREEN_IMAGING_DICTATION,
                  "BCM-IMG-006"),
              readWrite(
                  "/api/v1/imaging/bcm-img-007",
                  PermissionCode.SCREEN_IMAGING_REPORTS,
                  "BCM-IMG-007"),
              readWrite(
                  "/api/v1/imaging/bcm-img-008",
                  PermissionCode.SCREEN_IMAGING_DELIVERY,
                  "BCM-IMG-008"),
              readWrite(
                  "/api/v1/imaging/appointments",
                  PermissionCode.SCREEN_IMAGING_APPOINTMENTS,
                  "BCM-IMG-001"),
              readWrite(
                  "/api/v1/imaging/receptions",
                  PermissionCode.SCREEN_IMAGING_RECEPTION,
                  "BCM-IMG-002"),
              readWrite(
                  "/api/v1/imaging/studies",
                  PermissionCode.SCREEN_IMAGING_STUDIES,
                  "BCM-IMG-003"),
              readWrite(
                  "/api/v1/imaging/dicom-configs",
                  PermissionCode.SCREEN_IMAGING_DICOM,
                  "BCM-IMG-004"),
              readWrite(
                  "/api/v1/imaging/pacs-endpoints",
                  PermissionCode.SCREEN_IMAGING_PACS,
                  "BCM-IMG-005"),
              readWrite(
                  "/api/v1/imaging/dictations",
                  PermissionCode.SCREEN_IMAGING_DICTATION,
                  "BCM-IMG-006"),
              readWrite(
                  "/api/v1/imaging/reports",
                  PermissionCode.SCREEN_IMAGING_REPORTS,
                  "BCM-IMG-007"),
              readWrite(
                  "/api/v1/imaging/delivery-packages",
                  PermissionCode.SCREEN_IMAGING_DELIVERY,
                  "BCM-IMG-008"),
              readWrite("/api/ai", PermissionCode.SCREEN_AI_ASSISTANT, "BCM-AI-001"))
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
