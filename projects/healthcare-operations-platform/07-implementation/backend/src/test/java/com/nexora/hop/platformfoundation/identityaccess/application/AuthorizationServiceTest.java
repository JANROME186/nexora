package com.nexora.hop.platformfoundation.identityaccess.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.nexora.hop.platformfoundation.identityaccess.domain.PermissionCode;
import com.nexora.hop.platformfoundation.identityaccess.domain.RolePermissionCatalog;

class AuthorizationServiceTest {

    private AuthorizationService service;

    @BeforeEach
    void setUp() {
        service = new AuthorizationService();
    }

    @Test
    void hasPermissionIsTrueWhenTheRoleGrantsIt() {
        assertThat(service.hasPermission(RolePermissionCatalog.CASHIER, PermissionCode.SCREEN_SALES)).isTrue();
    }

    @Test
    void hasPermissionIsFalseWhenTheRoleDoesNotGrantIt() {
        assertThat(service.hasPermission(RolePermissionCatalog.CASHIER, PermissionCode.SCREEN_MEDICAL_VALIDATION))
                .isFalse();
    }

    @Test
    void hasPermissionIsFalseForAnUnknownRole() {
        assertThat(service.hasPermission("NOT_A_REAL_ROLE", PermissionCode.SCREEN_SALES)).isFalse();
    }

    @Test
    void permissionsForRolesUnionsPermissionsAcrossMultipleRoles() {
        Set<PermissionCode> permissions = service.permissionsForRoles(
                List.of(RolePermissionCatalog.FRONT_DESK, RolePermissionCatalog.CASHIER));

        assertThat(permissions).containsAll(RolePermissionCatalog.permissionsFor(RolePermissionCatalog.FRONT_DESK));
        assertThat(permissions).containsAll(RolePermissionCatalog.permissionsFor(RolePermissionCatalog.CASHIER));
        assertThat(permissions).doesNotContain(PermissionCode.SCREEN_MEDICAL_VALIDATION);
    }

    @Test
    void permissionsForRolesReturnsEmptySetForNullCollection() {
        assertThat(service.permissionsForRoles(null)).isEmpty();
    }
}
