# HOP IAM Permission Catalog and Dynamic Menu Model

Machine-readable source: `iam-permission-model.yaml`. Produced by `HOP-ENT-FOUND-001`.

## Relationship to strategic architecture

This is the first concrete implementation increment toward the strategic target already defined in
`authorization/authorization-model.md` (AUTHZ-ARCH-001, RBAC+ABAC, `domain.resource.action.scope`
permissions) and `identity-access/identity-access-architecture.md` (IAM-ARCH-001, OIDC/OAuth2).
It does not replace those documents — it defines the coarse, screen-level permission baseline that
exists in code today as a strict subset of that eventual model.

## Permission catalog

27 permissions, one per employee-portal screen, named `SCREEN_<KEY>` (e.g. `SCREEN_PATIENTS`,
`SCREEN_ROLE_ASSIGNMENTS`). Source of truth: backend `identityaccess/domain/PermissionCode.java`,
mirrored by matching string constants in the employee-portal and mobile-app. Full list and
module ownership: see the YAML companion's `permission_catalog.permissions`.

Backend API paths are now mapped to their owning screen/capability permission at request time
through `EndpointPermissionRegistry` and `HopAuthorizationInterceptor`. Individual in-screen actions
and fully granular `domain.resource.action.scope` permissions are not yet modeled 1:1. That residual
granularity gap remains tracked as **TD-IAM-002** and is now materially reduced rather than open.

## Role → permission catalog

Deny-by-default. 6 baseline roles (`ADMIN`, `FRONT_DESK`, `CASHIER`, `LAB_TECHNICIAN`,
`MEDICAL_REVIEWER`, `RESULTS_COORDINATOR`) map to permission subsets — see YAML for the full
mapping. `identityaccess/domain/RolePermissionCatalog.java` is the source of truth; an unmapped
role code resolves to an empty permission set.

## Dynamic menu / action filtering

- **Employee portal**: `AppShell.tsx` renders only the navigation tabs whose permission is present
  in the active session's permission set (from the new `SessionContext`), driven by a local
  development fixture session (see `session-management-baseline.md`) defaulting to `ADMIN`.
  Button/action-level filtering within a screen is not yet implemented (TD-IAM-002).
- **Mobile app**: same filtering pattern applied to the route model via
  `visibleRoutesForPermissions()`; no rendered UI exists yet.

## Backend server-side authorization

`identityaccess/application/AuthorizationService.java` provides a fully unit-tested
`hasPermission`/`permissionsForRoles` domain decision service. It is now wired into request handling:
mapped `/api/**` paths resolve to an `EndpointAccessRule`, the request is authenticated through
`HopAuthenticationResolver`, the authenticated context is exposed through
`AuthenticatedUserContextHolder`, and unauthorized calls receive explicit 401/403 responses.

`TD-IAM-001` is closed for the current local-development baseline. Production deployment still must
disable fixture authentication and bind the same authorization surface to the planned OIDC/IdP flow.

## Closure gate compliance

- All 27 existing screens have a permission mapping. ✅
- Mapped backend API paths enforce permission checks at request time. ✅
- Per-action/per-API-operation granularity remains an explicit reduced gap (TD-IAM-002), not silent. ✅
- Dynamic menu filtering from authenticated permissions is implemented for navigation. ✅
  Tenant/branch context is propagated through the session headers and available to the request
  context; row-level tenant enforcement remains tracked separately in data/security debt.
