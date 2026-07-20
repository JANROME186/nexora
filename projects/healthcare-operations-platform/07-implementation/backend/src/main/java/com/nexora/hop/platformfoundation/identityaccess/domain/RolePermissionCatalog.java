package com.nexora.hop.platformfoundation.identityaccess.domain;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * Deny-by-default mapping from role code to the {@link PermissionCode} screens that role may
 * access, per the IAM permission model
 * (enterprise-product-foundation-standard.yaml {@code mandatory_foundations.iam_permission_model}).
 * <p>
 * Any role code not present in this catalog resolves to an empty permission set rather than
 * throwing — deny-by-default, not fail-by-default. This is a baseline mapping of plausible HOP
 * job functions to screens; finer-grained per-action permissions are follow-up scope.
 */
public final class RolePermissionCatalog {

    public static final String ADMIN = "ADMIN";
    public static final String FRONT_DESK = "FRONT_DESK";
    public static final String CASHIER = "CASHIER";
    public static final String LAB_TECHNICIAN = "LAB_TECHNICIAN";
    public static final String MEDICAL_REVIEWER = "MEDICAL_REVIEWER";
    public static final String RESULTS_COORDINATOR = "RESULTS_COORDINATOR";
    public static final String PATIENT = "PATIENT";
    public static final String REFERRING_DOCTOR = "REFERRING_DOCTOR";
    public static final String SUPPORT = "SUPPORT";

    private static final Map<String, Set<PermissionCode>> ROLE_PERMISSIONS = Map.of(
            ADMIN, EnumSet.allOf(PermissionCode.class),
            FRONT_DESK, EnumSet.of(
                    PermissionCode.SCREEN_PERSON_SEARCH,
                    PermissionCode.SCREEN_PATIENTS,
                    PermissionCode.SCREEN_DOCTORS,
                    PermissionCode.SCREEN_PATIENT_REGISTRATIONS,
                    PermissionCode.SCREEN_RECEPTION,
                    PermissionCode.SCREEN_DIAGNOSTIC_ORDERS,
                    PermissionCode.SCREEN_DIAGNOSTIC_CATALOG),
            CASHIER, EnumSet.of(
                    PermissionCode.SCREEN_CASH_SESSIONS,
                    PermissionCode.SCREEN_SALES,
                    PermissionCode.SCREEN_BILLING_REQUESTS),
            LAB_TECHNICIAN, EnumSet.of(
                    PermissionCode.SCREEN_SAMPLE_COLLECTION,
                    PermissionCode.SCREEN_SAMPLE_LABELING,
                    PermissionCode.SCREEN_SAMPLE_RECEPTION,
                    PermissionCode.SCREEN_LABORATORY_PROCESSING,
                    PermissionCode.SCREEN_TECHNICAL_VALIDATION),
            MEDICAL_REVIEWER, EnumSet.of(
                    PermissionCode.SCREEN_MEDICAL_VALIDATION,
                    PermissionCode.SCREEN_RESULT_RELEASE,
                    PermissionCode.SCREEN_RESULT_SEARCH),
            RESULTS_COORDINATOR, EnumSet.of(
                    PermissionCode.SCREEN_RESULT_SEARCH,
                    PermissionCode.SCREEN_RESULT_REPORTS,
                    PermissionCode.SCREEN_CRITICAL_ESCALATIONS,
                    PermissionCode.SCREEN_RESULT_NOTIFICATIONS),
            PATIENT, EnumSet.of(
                    PermissionCode.PORTAL_PATIENT_PROFILE_VIEW,
                    PermissionCode.PORTAL_PATIENT_APPOINTMENTS_VIEW,
                    PermissionCode.PORTAL_PATIENT_ORDERS_VIEW,
                    PermissionCode.PORTAL_PATIENT_RESULTS_VIEW,
                    PermissionCode.PORTAL_PATIENT_NOTIFICATIONS_VIEW),
            REFERRING_DOCTOR, EnumSet.of(
                    PermissionCode.PORTAL_DOCTOR_PATIENTS_VIEW,
                    PermissionCode.PORTAL_DOCTOR_RESULTS_VIEW,
                    PermissionCode.PORTAL_DOCTOR_ORDERS_VIEW,
                    PermissionCode.PORTAL_DOCTOR_NOTIFICATIONS_VIEW),
            SUPPORT, EnumSet.of(
                    PermissionCode.PORTAL_SUPPORT_IMPERSONATE));

    private RolePermissionCatalog() {
    }

    /**
     * Returns the permissions granted to {@code roleCode}, or an empty set (deny-by-default) when
     * the role code is unknown, blank, or {@code null}.
     */
    public static Set<PermissionCode> permissionsFor(String roleCode) {
        if (roleCode == null) {
            return Set.of();
        }
        return ROLE_PERMISSIONS.getOrDefault(roleCode, Set.of());
    }
}
