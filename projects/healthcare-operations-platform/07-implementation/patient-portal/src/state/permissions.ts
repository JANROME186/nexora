/**
 * Permission model for the patient-portal navigation (enterprise-product-foundation-standard,
 * `mandatory_foundations.iam_permission_model`: menus must be generated dynamically from the
 * logged-in user's roles/permissions, and the frontend must hide unauthorized navigation).
 */
export type ScreenKey =
  "profile" | "results" | "appointments" | "orders" | "notifications" | "imaging";

export type PermissionCode =
  | "PORTAL_PATIENT_PROFILE_VIEW"
  | "PORTAL_PATIENT_RESULTS_VIEW"
  | "PORTAL_PATIENT_APPOINTMENTS_VIEW"
  | "PORTAL_PATIENT_ORDERS_VIEW"
  | "PORTAL_PATIENT_NOTIFICATIONS_VIEW"
  | "PORTAL_PATIENT_IMAGING_VIEW"
  | "PORTAL_DOCTOR_PATIENTS_VIEW"
  | "PORTAL_DOCTOR_RESULTS_VIEW";

export const SCREEN_TO_PERMISSION: Record<ScreenKey, PermissionCode> = {
  profile: "PORTAL_PATIENT_PROFILE_VIEW",
  results: "PORTAL_PATIENT_RESULTS_VIEW",
  appointments: "PORTAL_PATIENT_APPOINTMENTS_VIEW",
  orders: "PORTAL_PATIENT_ORDERS_VIEW",
  notifications: "PORTAL_PATIENT_NOTIFICATIONS_VIEW",
  imaging: "PORTAL_PATIENT_IMAGING_VIEW",
};

export const PERMISSION_CODES: readonly PermissionCode[] = Object.values(SCREEN_TO_PERMISSION);

export type RoleCode = "PATIENT" | "REFERRING_DOCTOR";

export const ROLE_PERMISSION_CATALOG: Record<RoleCode, readonly PermissionCode[]> = {
  PATIENT: [
    "PORTAL_PATIENT_PROFILE_VIEW",
    "PORTAL_PATIENT_RESULTS_VIEW",
    "PORTAL_PATIENT_APPOINTMENTS_VIEW",
    "PORTAL_PATIENT_ORDERS_VIEW",
    "PORTAL_PATIENT_NOTIFICATIONS_VIEW",
    "PORTAL_PATIENT_IMAGING_VIEW",
  ],
  REFERRING_DOCTOR: ["PORTAL_DOCTOR_PATIENTS_VIEW", "PORTAL_DOCTOR_RESULTS_VIEW"],
};

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
