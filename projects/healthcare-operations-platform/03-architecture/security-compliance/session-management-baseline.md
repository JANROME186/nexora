# HOP Login and Session Management Baseline

Machine-readable source: `session-management-baseline.yaml`. Produced by `HOP-ENT-FOUND-001`.

## Current state (repository-audit confirmed)

No stack has real authentication today. Backend: 0 `SecurityConfig`/`@PreAuthorize`/`UserDetails`
matches anywhere. Employee portal: no `AuthContext` existed before this backlog item. Mobile app:
a pre-existing local session scaffold (`localAuth.ts`, `sessionStore.ts`) with no real credential
verification.

## Hardcoded/fixture actor inventory and remediation

| Location | Classification | Action taken |
|---|---|---|
| `IdentityAccessService.assignRole` (`createdBy` hardcoded to `"system"`) | Real gap — user-initiated action mislabeled as system | **Fixed**: `AssignRoleCommand`/`AssignRoleRequest` now require an explicit `actorUserId`, validated like other required fields |
| `AuditComplianceService.SYSTEM_ACTOR_ID` | Legitimate system actor | No change — correctly scoped to system-initiated audit events |
| `CriticalResultEscalationService` / `ResultNotificationService` `"system"` audit metadata | Legitimate system actor | No change — system-generated trails, not user actions |
| Employee-portal `SessionContext.LOCAL_DEV_FIXTURE_SESSION` (new) | Documented local-dev fixture | Named, JSDoc-disclosed as non-production, defaults to `ADMIN` role |
| Mobile-app `localAuth.ts` (pre-existing) | Documented local-dev fixture | JSDoc disclosure added this iteration |

The one genuine hardcoded-productive-actor bug found in the repository was fixed in code. The two
fixtures that remain are explicitly isolated and documented, not silently masquerading as
production identity.

## Required capabilities baseline

Login, logout, session expiration, tenant/branch context, role/permission loading and secure token
storage are all **defined but not yet implemented** — see the YAML companion's
`required_capabilities_baseline_definition` for target state per capability, mapped against
`identity-access-architecture.md`'s MVP scope (username/password + JWT/OIDC-compatible).

## Implementation backlog (new, not yet scheduled to a specific iteration)

1. **BE-AUTH-001** — backend authentication (JWT/OIDC-compatible) + request-scoped principal.
2. **BE-AUTH-002** — wire `AuthorizationService` into a request-scoped enforcement layer.
3. **FE-AUTH-001** — replace the employee-portal fixture session with real login.
4. **APP-AUTH-001** — replace the mobile-app local auth stand-in with real login (after a renderer
   stack is selected under TD-APP-001).

## Closure gate compliance

Both closure-gate requirements are satisfied: a defined baseline with implementation backlog
exists, and the single real hardcoded-actor gap found in the codebase was fixed rather than merely
documented.
