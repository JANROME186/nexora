package com.nexora.hop.platformfoundation.identityaccess.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;

class RolePermissionCatalogTest {

    @Test
    void adminHoldsAllPermissions() {
        assertThat(RolePermissionCatalog.permissionsFor(RolePermissionCatalog.ADMIN))
                .containsExactlyInAnyOrder(PermissionCode.values());
    }

    @Test
    void frontDeskHoldsItsExpectedScreens() {
        assertThat(RolePermissionCatalog.permissionsFor(RolePermissionCatalog.FRONT_DESK))
                .containsExactlyInAnyOrder(
                        PermissionCode.SCREEN_PERSON_SEARCH,
                        PermissionCode.SCREEN_PATIENTS,
                        PermissionCode.SCREEN_DOCTORS,
                        PermissionCode.SCREEN_PATIENT_REGISTRATIONS,
                        PermissionCode.SCREEN_RECEPTION,
                        PermissionCode.SCREEN_DIAGNOSTIC_ORDERS,
                        PermissionCode.SCREEN_DIAGNOSTIC_CATALOG);
    }

    @Test
    void cashierHoldsItsExpectedScreens() {
        assertThat(RolePermissionCatalog.permissionsFor(RolePermissionCatalog.CASHIER))
                .containsExactlyInAnyOrder(
                        PermissionCode.SCREEN_CASH_SESSIONS,
                        PermissionCode.SCREEN_SALES,
                        PermissionCode.SCREEN_BILLING_REQUESTS);
    }

    @Test
    void labTechnicianHoldsItsExpectedScreens() {
        assertThat(RolePermissionCatalog.permissionsFor(RolePermissionCatalog.LAB_TECHNICIAN))
                .containsExactlyInAnyOrder(
                        PermissionCode.SCREEN_SAMPLE_COLLECTION,
                        PermissionCode.SCREEN_SAMPLE_LABELING,
                        PermissionCode.SCREEN_SAMPLE_RECEPTION,
                        PermissionCode.SCREEN_LABORATORY_PROCESSING,
                        PermissionCode.SCREEN_TECHNICAL_VALIDATION);
    }

    @Test
    void medicalReviewerHoldsItsExpectedScreens() {
        assertThat(RolePermissionCatalog.permissionsFor(RolePermissionCatalog.MEDICAL_REVIEWER))
                .containsExactlyInAnyOrder(
                        PermissionCode.SCREEN_MEDICAL_VALIDATION,
                        PermissionCode.SCREEN_RESULT_RELEASE,
                        PermissionCode.SCREEN_RESULT_SEARCH);
    }

    @Test
    void resultsCoordinatorHoldsItsExpectedScreens() {
        assertThat(RolePermissionCatalog.permissionsFor(RolePermissionCatalog.RESULTS_COORDINATOR))
                .containsExactlyInAnyOrder(
                        PermissionCode.SCREEN_RESULT_SEARCH,
                        PermissionCode.SCREEN_RESULT_REPORTS,
                        PermissionCode.SCREEN_CRITICAL_ESCALATIONS,
                        PermissionCode.SCREEN_RESULT_NOTIFICATIONS);
    }

    @Test
    void referringDoctorHoldsItsExpectedDoctorPortalScreens() {
        assertThat(RolePermissionCatalog.permissionsFor(RolePermissionCatalog.REFERRING_DOCTOR))
                .containsExactlyInAnyOrder(
                        PermissionCode.PORTAL_DOCTOR_PATIENTS_VIEW,
                        PermissionCode.PORTAL_DOCTOR_RESULTS_VIEW,
                        PermissionCode.PORTAL_DOCTOR_ORDERS_VIEW,
                        PermissionCode.PORTAL_DOCTOR_NOTIFICATIONS_VIEW);
    }

    @Test
    void unknownRoleCodeIsDeniedByDefault() {
        assertThat(RolePermissionCatalog.permissionsFor("NOT_A_REAL_ROLE")).isEqualTo(Set.of());
    }

    @Test
    void nullRoleCodeIsDeniedByDefault() {
        assertThat(RolePermissionCatalog.permissionsFor(null)).isEqualTo(Set.of());
    }
}
