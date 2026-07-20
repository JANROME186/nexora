/**
 * Permission model for the employee-portal navigation (enterprise-product-foundation-standard,
 * `mandatory_foundations.iam_permission_model`: menus must be generated dynamically from the
 * logged-in user's roles/permissions, and the frontend must hide unauthorized navigation).
 *
 * `PermissionCode` mirrors the backend's `identityaccess.domain.PermissionCode` enum identifiers
 * (one permission per employee-portal screen). `ScreenKey` enumerates every AppShell screen and
 * `SCREEN_TO_PERMISSION` maps each screen 1:1 to the permission that must be present in the
 * current session to render its navigation tab.
 */
export type ScreenKey =
  | "tenants"
  | "laboratories"
  | "branches"
  | "users"
  | "role-assignments"
  | "audit-events"
  | "diagnostic-catalog"
  | "person-search"
  | "patients"
  | "doctors"
  | "patient-registrations"
  | "reception"
  | "diagnostic-orders"
  | "cash-sessions"
  | "sales"
  | "billing-requests"
  | "sample-collection"
  | "sample-labeling"
  | "sample-reception"
  | "laboratory-processing"
  | "technical-validation"
  | "medical-validation"
  | "result-release"
  | "result-search"
  | "result-reports"
  | "critical-escalations"
  | "result-notifications"
  | "integration-endpoints"
  | "api-management"
  | "migration-jobs"
  | "inventory-catalog"
  | "inventory-reagents"
  | "inventory-lots"
  | "inventory-procurement"
  | "inventory-stock-movements"
  | "inventory-adjustments"
  | "inventory-waste"
  | "internal-quality-controls"
  | "equipment"
  | "calibrations"
  | "maintenance";

export type PermissionCode =
  | "SCREEN_TENANTS"
  | "SCREEN_LABORATORIES"
  | "SCREEN_BRANCHES"
  | "SCREEN_USERS"
  | "SCREEN_ROLE_ASSIGNMENTS"
  | "SCREEN_AUDIT_EVENTS"
  | "SCREEN_DIAGNOSTIC_CATALOG"
  | "SCREEN_PERSON_SEARCH"
  | "SCREEN_PATIENTS"
  | "SCREEN_DOCTORS"
  | "SCREEN_PATIENT_REGISTRATIONS"
  | "SCREEN_RECEPTION"
  | "SCREEN_DIAGNOSTIC_ORDERS"
  | "SCREEN_CASH_SESSIONS"
  | "SCREEN_SALES"
  | "SCREEN_BILLING_REQUESTS"
  | "SCREEN_SAMPLE_COLLECTION"
  | "SCREEN_SAMPLE_LABELING"
  | "SCREEN_SAMPLE_RECEPTION"
  | "SCREEN_LABORATORY_PROCESSING"
  | "SCREEN_TECHNICAL_VALIDATION"
  | "SCREEN_MEDICAL_VALIDATION"
  | "SCREEN_RESULT_RELEASE"
  | "SCREEN_RESULT_SEARCH"
  | "SCREEN_RESULT_REPORTS"
  | "SCREEN_CRITICAL_ESCALATIONS"
  | "SCREEN_RESULT_NOTIFICATIONS"
  | "SCREEN_INTEGRATION_ENDPOINTS"
  | "SCREEN_API_MANAGEMENT"
  | "SCREEN_MIGRATION_JOBS"
  | "SCREEN_INVENTORY_CATALOG"
  | "SCREEN_INVENTORY_REAGENTS"
  | "SCREEN_INVENTORY_LOTS"
  | "SCREEN_INVENTORY_PROCUREMENT"
  | "SCREEN_INVENTORY_STOCK_MOVEMENTS"
  | "SCREEN_INVENTORY_ADJUSTMENTS"
  | "SCREEN_INVENTORY_WASTE"
  | "SCREEN_INTERNAL_QUALITY_CONTROLS"
  | "SCREEN_EQUIPMENT"
  | "SCREEN_CALIBRATIONS"
  | "SCREEN_MAINTENANCE";

export const SCREEN_TO_PERMISSION: Record<ScreenKey, PermissionCode> = {
  tenants: "SCREEN_TENANTS",
  laboratories: "SCREEN_LABORATORIES",
  branches: "SCREEN_BRANCHES",
  users: "SCREEN_USERS",
  "role-assignments": "SCREEN_ROLE_ASSIGNMENTS",
  "audit-events": "SCREEN_AUDIT_EVENTS",
  "diagnostic-catalog": "SCREEN_DIAGNOSTIC_CATALOG",
  "person-search": "SCREEN_PERSON_SEARCH",
  patients: "SCREEN_PATIENTS",
  doctors: "SCREEN_DOCTORS",
  "patient-registrations": "SCREEN_PATIENT_REGISTRATIONS",
  reception: "SCREEN_RECEPTION",
  "diagnostic-orders": "SCREEN_DIAGNOSTIC_ORDERS",
  "cash-sessions": "SCREEN_CASH_SESSIONS",
  sales: "SCREEN_SALES",
  "billing-requests": "SCREEN_BILLING_REQUESTS",
  "sample-collection": "SCREEN_SAMPLE_COLLECTION",
  "sample-labeling": "SCREEN_SAMPLE_LABELING",
  "sample-reception": "SCREEN_SAMPLE_RECEPTION",
  "laboratory-processing": "SCREEN_LABORATORY_PROCESSING",
  "technical-validation": "SCREEN_TECHNICAL_VALIDATION",
  "medical-validation": "SCREEN_MEDICAL_VALIDATION",
  "result-release": "SCREEN_RESULT_RELEASE",
  "result-search": "SCREEN_RESULT_SEARCH",
  "result-reports": "SCREEN_RESULT_REPORTS",
  "critical-escalations": "SCREEN_CRITICAL_ESCALATIONS",
  "result-notifications": "SCREEN_RESULT_NOTIFICATIONS",
  "integration-endpoints": "SCREEN_INTEGRATION_ENDPOINTS",
  "api-management": "SCREEN_API_MANAGEMENT",
  "migration-jobs": "SCREEN_MIGRATION_JOBS",
  "inventory-catalog": "SCREEN_INVENTORY_CATALOG",
  "inventory-reagents": "SCREEN_INVENTORY_REAGENTS",
  "inventory-lots": "SCREEN_INVENTORY_LOTS",
  "inventory-procurement": "SCREEN_INVENTORY_PROCUREMENT",
  "inventory-stock-movements": "SCREEN_INVENTORY_STOCK_MOVEMENTS",
  "inventory-adjustments": "SCREEN_INVENTORY_ADJUSTMENTS",
  "inventory-waste": "SCREEN_INVENTORY_WASTE",
  "internal-quality-controls": "SCREEN_INTERNAL_QUALITY_CONTROLS",
  equipment: "SCREEN_EQUIPMENT",
  calibrations: "SCREEN_CALIBRATIONS",
  maintenance: "SCREEN_MAINTENANCE",
};

/** Every permission code, derived from `SCREEN_TO_PERMISSION` so the two never drift apart. */
export const PERMISSION_CODES: readonly PermissionCode[] = Object.values(SCREEN_TO_PERMISSION);

export type RoleCode =
  | "ADMIN"
  | "FRONT_DESK"
  | "CASHIER"
  | "LAB_TECHNICIAN"
  | "MEDICAL_REVIEWER"
  | "RESULTS_COORDINATOR";

/**
 * Mirrors the backend's `identityaccess/domain/RolePermissionCatalog.java` role -> permission
 * assignments so the employee-portal filters navigation the same way the backend would enforce
 * authorization server-side. ADMIN is granted every screen permission (derived from
 * `PERMISSION_CODES`, not hand-duplicated) so it stays in sync automatically.
 */
export const ROLE_PERMISSION_CATALOG: Record<RoleCode, readonly PermissionCode[]> = {
  ADMIN: PERMISSION_CODES,
  FRONT_DESK: [
    "SCREEN_PERSON_SEARCH",
    "SCREEN_PATIENTS",
    "SCREEN_DOCTORS",
    "SCREEN_PATIENT_REGISTRATIONS",
    "SCREEN_RECEPTION",
    "SCREEN_DIAGNOSTIC_ORDERS",
    "SCREEN_DIAGNOSTIC_CATALOG",
  ],
  CASHIER: ["SCREEN_CASH_SESSIONS", "SCREEN_SALES", "SCREEN_BILLING_REQUESTS"],
  LAB_TECHNICIAN: [
    "SCREEN_SAMPLE_COLLECTION",
    "SCREEN_SAMPLE_LABELING",
    "SCREEN_SAMPLE_RECEPTION",
    "SCREEN_LABORATORY_PROCESSING",
    "SCREEN_TECHNICAL_VALIDATION",
  ],
  MEDICAL_REVIEWER: ["SCREEN_MEDICAL_VALIDATION", "SCREEN_RESULT_RELEASE", "SCREEN_RESULT_SEARCH"],
  RESULTS_COORDINATOR: [
    "SCREEN_RESULT_SEARCH",
    "SCREEN_RESULT_REPORTS",
    "SCREEN_CRITICAL_ESCALATIONS",
    "SCREEN_RESULT_NOTIFICATIONS",
  ],
};

/** Union of every permission granted by the given roles. */
export function permissionsForRoles(roleCodes: readonly string[]): Set<PermissionCode> {
  const permissions = new Set<PermissionCode>();
  for (const roleCode of roleCodes) {
    const rolePermissions = ROLE_PERMISSION_CATALOG[roleCode as RoleCode];
    if (!rolePermissions) {
      continue;
    }
    for (const permission of rolePermissions) {
      permissions.add(permission);
    }
  }
  return permissions;
}
