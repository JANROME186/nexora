# HOP Login and Session Management Baseline

Machine-readable source: `session-management-baseline.yaml`. Produced by `HOP-ENT-FOUND-001`.

## Current state (repository-audit confirmed)

The product has a local-development authentication baseline, not production identity integration.
Backend request handling now includes Spring Security, `HopAuthenticationResolver`,
`HopAuthorizationInterceptor`, `AuthenticatedUserContextHolder` and explicit 401/403 behavior for
mapped API paths. Employee portal has a `SessionContext` that persists local session metadata and
sends `X-HOP-*` headers. Mobile API access can inject the same session headers. Mobile still has a
pre-existing local session scaffold (`localAuth.ts`, `sessionStore.ts`) with no real credential
verification.

## Hardcoded/fixture actor inventory and remediation

| Location | Classification | Action taken |
|---|---|---|
| `IdentityAccessService.assignRole` (`createdBy` hardcoded to `"system"`) | Real gap — user-initiated action mislabeled as system | **Fixed**: `AssignRoleCommand`/`AssignRoleRequest` now require an explicit `actorUserId`, validated like other required fields |
| `AuditComplianceService.SYSTEM_ACTOR_ID` | Legitimate system actor | No change — correctly scoped to system-initiated audit events |
| `CriticalResultEscalationService` / `ResultNotificationService` `"system"` audit metadata | Legitimate system actor | No change — system-generated trails, not user actions |
| Backend `hop.security.local-fixture-enabled` | Documented local-dev fixture | Defaults to enabled only for local/dev compatibility; can be disabled to force real bearer-token headers and 401 responses |
| Employee-portal `SessionContext.LOCAL_DEV_FIXTURE_SESSION` | Documented local-dev fixture | Named, JSDoc-disclosed as non-production, defaults to `ADMIN` role and propagates tenant/branch/token headers |
| Mobile-app `localAuth.ts` (pre-existing) | Documented local-dev fixture | JSDoc disclosure added this iteration |

The one genuine hardcoded-productive-actor bug found in the repository was fixed in code. The two
fixtures that remain are explicitly isolated and documented, not silently masquerading as
production identity.

## Required capabilities baseline

Login, logout, session expiration, tenant/branch context, role/permission loading and secure token
storage are defined in the YAML companion. The current implementation covers request-time backend
authorization for mapped API paths and web/mobile propagation of tenant/branch/token headers. Real
login, JWT/OIDC token issuance, refresh, expiry and secure device storage remain scheduled
productization work under the identity-access roadmap.

## Implementation backlog (new, not yet scheduled to a specific iteration)

1. **BE-AUTH-001** — replace local bearer-token fixtures with JWT/OIDC-compatible authentication.
2. **FE-AUTH-001** — replace the employee-portal fixture session with real login.
3. **APP-AUTH-001** — replace the mobile-app local auth stand-in with real login (after a renderer
   stack is selected under TD-APP-001).

## Closure gate compliance

Both closure-gate requirements are satisfied: a defined baseline with implementation backlog exists,
the single real hardcoded-actor gap found in the codebase was fixed, and mapped API paths now enforce
request-time permissions in the backend.
