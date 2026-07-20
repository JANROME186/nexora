/**
 * Permission model for the doctor-portal navigation (enterprise-product-foundation-standard,
 * `mandatory_foundations.iam_permission_model`: menus must be generated dynamically from the
 * logged-in user's roles/permissions, and the frontend must hide unauthorized navigation).
 *
 * Mirrors the backend's `identityaccess.domain.PermissionCode`/`RolePermissionCatalog`
 * `REFERRING_DOCTOR` grants (COM-MOD-009-PORTAL-002): a referring doctor may view their referred
 * patients, those patients' released results, their own diagnostic orders/status, and their
 * notifications. Nothing here grants write access — every screen is read-only.
 */
export type ScreenKey = "patients" | "results" | "orders" | "notifications";

export type PermissionCode =
  | "PORTAL_DOCTOR_PATIENTS_VIEW"
  | "PORTAL_DOCTOR_RESULTS_VIEW"
  | "PORTAL_DOCTOR_ORDERS_VIEW"
  | "PORTAL_DOCTOR_NOTIFICATIONS_VIEW";

export const SCREEN_TO_PERMISSION: Record<ScreenKey, PermissionCode> = {
  patients: "PORTAL_DOCTOR_PATIENTS_VIEW",
  results: "PORTAL_DOCTOR_RESULTS_VIEW",
  orders: "PORTAL_DOCTOR_ORDERS_VIEW",
  notifications: "PORTAL_DOCTOR_NOTIFICATIONS_VIEW",
};

/** Every permission code, derived from `SCREEN_TO_PERMISSION` so the two never drift apart. */
export const PERMISSION_CODES: readonly PermissionCode[] = Object.values(SCREEN_TO_PERMISSION);

export type RoleCode = "REFERRING_DOCTOR";

/**
 * Mirrors the backend's `identityaccess/domain/RolePermissionCatalog.java` `REFERRING_DOCTOR`
 * grant so the doctor-portal filters navigation the same way the backend enforces authorization
 * server-side (`HopAuthorizationInterceptor`'s REFERRING_DOCTOR self-access blocks).
 */
export const ROLE_PERMISSION_CATALOG: Record<RoleCode, readonly PermissionCode[]> = {
  REFERRING_DOCTOR: PERMISSION_CODES,
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
