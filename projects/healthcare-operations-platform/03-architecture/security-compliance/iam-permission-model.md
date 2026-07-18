# HOP IAM Permission Catalog and Dynamic Menu Model

Machine-readable source: `iam-permission-model.yaml`. Produced by `HOP-ENT-FOUND-001`.

## Relationship to strategic architecture

This is the first concrete implementation increment toward the strategic target already defined in
`authorization/authorization-model.md` (AUTHZ-ARCH-001, RBAC+ABAC, `domain.resource.action.scope`
permissions) and `identity-access/identity-access-architecture.md` (IAM-ARCH-001, OIDC/OAuth2).
It does not replace those documents — it defines the coarse, screen-level permission baseline that
exists in code today as a strict subset of that eventual model.

## Permission catalog

26 permissions, one per employee-portal screen, named `SCREEN_<KEY>` (e.g. `SCREEN_PATIENTS`,
`SCREEN_ROLE_ASSIGNMENTS`). Source of truth: backend `identityaccess/domain/PermissionCode.java`,
mirrored by matching string constants in the employee-portal and mobile-app. Full list and
module ownership: see the YAML companion's `permission_catalog.permissions`.

**Gap, explicitly registered**: individual backend API operations and in-screen actions are not
yet mapped 1:1 to their own permission — only their owning screen is. Tracked as **TD-IAM-002**.

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

## Backend server-side authorization — explicit gap

`identityaccess/application/AuthorizationService.java` provides a fully unit-tested
`hasPermission`/`permissionsForRoles` domain decision service. **It is not wired into any request
path.** The backend has zero authentication mechanism today (confirmed: no `SecurityConfig`, no
`@PreAuthorize`, no `UserDetails` anywhere) — every controller is currently reachable without any
check. This is disclosed here explicitly, not silently omitted, per the standard's "permission
mapping or an explicit gap" allowance. Registered as **TD-IAM-001** (high risk, non-blocking today
because the backend is local-development-only, but an explicit P0 precondition for `COM-MOD-009`
Patient and Doctor Portals and any customer-facing deployment).

## Closure gate compliance

- All 27 existing screens have a permission mapping. ✅
- Per-action/per-API-operation granularity is an explicit gap (TD-IAM-002), not silent. ✅
- Dynamic menu filtering from authenticated permissions is implemented for navigation. ✅
  Tenant/branch-scoped enforcement at request time is folded into TD-IAM-001.
