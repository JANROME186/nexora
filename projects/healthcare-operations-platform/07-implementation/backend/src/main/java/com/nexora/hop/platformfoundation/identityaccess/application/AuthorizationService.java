package com.nexora.hop.platformfoundation.identityaccess.application;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.nexora.hop.platformfoundation.identityaccess.domain.PermissionCode;
import com.nexora.hop.platformfoundation.identityaccess.domain.RolePermissionCatalog;

/**
 * Domain-level authorization decision service for the IAM permission model
 * (enterprise-product-foundation-standard.md {@code mandatory_foundations.iam_permission_model}).
 * <p>
 * This service answers "does role X hold permission Y" and "what is the union of permissions
 * across roles Y1..Yn" (a {@link com.nexora.hop.platformfoundation.identityaccess.domain.RoleAssignment
 * user} may hold several role assignments at once). It is ready to be invoked once request-time
 * authenticated-principal resolution exists in this backend — there is currently no
 * Spring Security / login mechanism at all here, which is intentionally out of scope for this
 * change.
 */
@Service
public class AuthorizationService {

    /** Whether {@code roleCode} grants {@code permission} (deny-by-default for unknown roles). */
    public boolean hasPermission(String roleCode, PermissionCode permission) {
        return RolePermissionCatalog.permissionsFor(roleCode).contains(permission);
    }

    /** Union of permissions granted across all of {@code roleCodes}. */
    public Set<PermissionCode> permissionsForRoles(Collection<String> roleCodes) {
        Set<PermissionCode> permissions = EnumSet.noneOf(PermissionCode.class);
        if (roleCodes == null) {
            return permissions;
        }
        for (String roleCode : roleCodes) {
            permissions.addAll(RolePermissionCatalog.permissionsFor(roleCode));
        }
        return permissions;
    }

    /**
     * Union of domain.resource.action.scope grammar grants across all of {@code roleCodes}
     * (TD-IAM-003).
     */
    public Set<String> scopedPermissionsForRoles(Collection<String> roleCodes) {
        Set<String> permissions = new java.util.HashSet<>();
        if (roleCodes == null) {
            return permissions;
        }
        for (String roleCode : roleCodes) {
            permissions.addAll(RolePermissionCatalog.scopedPermissionsFor(roleCode));
        }
        return permissions;
    }
}
