# HOP Login and Session Management Baseline

Machine-readable source: `session-management-baseline.md`. Produced by `HOP-ENT-FOUND-001`.

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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SESSION-BASE-001
  type: session-management-baseline
  name: HOP Login and Session Management Baseline
  version: 1.0.0
  status: approved
  human_readable: session-management-baseline.md
  machine_readable: session-management-baseline.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-17
  source_backlog_item: HOP-ENT-FOUND-001
purpose: 'Define the login/logout/session-expiration/authenticated-user-context baseline
  required before IAM permission enforcement and customer-facing portals can proceed,
  and account for every currently hardcoded or fixture actor in the codebase, per
  ../../../../nexora-framework/02-standards/standards/enterprise-product-foundation-standard.md
  (mandatory_foundations.login_and_session_management).

  '
current_state_confirmed_by_repository_audit:
  backend: 'Request-time authorization now exists for mapped API paths through Spring
    Security, HopAuthenticationResolver, HopAuthorizationInterceptor, AuthenticatedUserContextHolder
    and EndpointPermissionRegistry. The current credential source is a documented
    local-development bearer/header fixture, not production OIDC/IdP authentication.

    '
  employee_portal: 'SessionContext persists local session metadata, drives permission-based
    navigation and sends X-HOP-User-Id, X-HOP-Tenant-Id, X-HOP-Branch-Id and Authorization
    headers to the backend. It remains a documented local-development fixture, not
    a production login mechanism.

    '
  mobile_app: 'A local session scaffold already existed prior to this backlog item
    (07-implementation/mobile-app/src/auth/localAuth.ts, sessionStore.ts) producing
    tokens shaped `local-session:<tenantId>:<userId>` with no password/JWT/verification
    logic. This backlog item added an explicit JSDoc disclosure that it is a local/dev
    stand-in, not production auth.

    '
hardcoded_and_fixture_actor_inventory:
- location: '07-implementation/backend/.../identityaccess/application/IdentityAccessService.java
    (assignRole method)

    '
  prior_state: The RoleAssignment.createdBy field was hardcoded to the literal "system"
    for every role assignment, even though assigning a role is a real, user-initiated
    administrative action, not a system-initiated event.
  remediation_this_iteration: 'AssignRoleCommand now requires an explicit actorUserId,
    validated the same way as other required command fields; IdentityAccessController''s
    AssignRoleRequest requires actorUserId in the request body. The literal "system"
    is no longer used for this path.

    '
  residual_gap: 'actorUserId is still supplied explicitly by the role-assignment request;
    future productization should derive it from AuthenticatedUserContextHolder for
    every user-initiated action.

    '
- location: 07-implementation/backend/src/main/resources/application.properties
  classification: documented_local_development_fixture
  fixture_name: hop.security.local-fixture-enabled
  reason: 'Keeps local development and existing tests runnable with a known ADMIN
    fixture when no Authorization header is supplied. When disabled, mapped API paths
    require bearer/session headers and return 401/403 as tested by HopAuthorizationInterceptorTest.

    '
- location: '07-implementation/backend/.../auditcompliance/application/AuditComplianceService.java
    (SYSTEM_ACTOR_ID constant)

    '
  classification: legitimate_system_actor_not_a_gap
  reason: 'Used only for genuinely system-initiated audit events (not user actions).
    Named, documented, isolated to a single constant. No change required.

    '
- location: '07-implementation/backend/.../resultsanddigitaldelivery/criticalresults/application/CriticalResultEscalationService.java
    and .../notifications/application/ResultNotificationService.java ("system" AuditMetadata)

    '
  classification: legitimate_system_actor_not_a_gap
  reason: System-generated notification/escalation audit trails, not attributed user
    actions. Out of scope for this iteration; flagged here for completeness of the
    inventory.
- location: 07-implementation/employee-portal/src/state/SessionContext.tsx (new)
  classification: documented_local_development_fixture
  fixture_name: LOCAL_DEV_FIXTURE_SESSION
  reason: 'The employee portal has no production login flow yet, but it now persists
    a local session object and sends the standard X-HOP-* session headers consumed
    by backend request-time authorization. The fixture remains named/commented as
    non-production and must be replaced by a real session sourced from login before
    external deployment.

    '
- location: 07-implementation/mobile-app/src/auth/localAuth.ts (pre-existing)
  classification: documented_local_development_fixture
  reason: Pre-existing local/dev session stand-in; this iteration added explicit JSDoc
    disclosure that it is not production authentication.
required_capabilities_baseline_definition:
  login:
    target: Username/password plus JWT/OIDC-compatible architecture for MVP, per identity-access-architecture.md's
      MVP scope, without precluding migration to an external IdP (Keycloak, Authentik,
      Entra ID, Okta, Auth0, Cognito).
    status: local_fixture_header_context_implemented
  logout:
    target: Explicit session/token invalidation, client-side session state clearing.
    status: not_yet_implemented
  session_expiration:
    target: Short-lived access tokens with refresh rotation where supported (per identity-access-architecture.md).
    status: not_yet_implemented
  authenticated_user_context:
    target: A request-scoped principal (backend) and a real session object (frontend/mobile)
      replacing today's caller-supplied ids and local-dev fixtures.
    status: implemented_for_local_fixture_and_local_session_tokens (not backed by
      production credentials)
  tenant_and_branch_context:
    target: Active tenant/branch resolved from the authenticated session, not a UI-selected
      value.
    status: partially_implemented_for_local_fixtures (employee-portal SessionContext
      and mobile API clients propagate X-HOP-Tenant-Id and X-HOP-Branch-Id; production
      login must derive these from the authenticated principal before external portals
      per COM-MOD-009)
  role_permission_loading:
    target: Permissions loaded from the authenticated user's real role assignments
      at login.
    status: enforced_for_local_fixture_and_local_session_roles by HopAuthorizationInterceptor
  unauthorized_and_forbidden_error_handling:
    target: Consistent 401 (not authenticated) / 403 (authenticated, not authorized)
      responses, audit-logged.
    status: implemented_for_mapped_api_paths (401/403 tested in HopAuthorizationInterceptorTest)
  secure_storage_for_client_tokens_or_session_references:
    target: httpOnly secure cookies or equivalent secure storage for real session
      tokens once login exists; localStorage is acceptable only for the non-sensitive
      locale preference added by this iteration (see localization-strategy.md), never
      for credentials.
    status: not_applicable_yet (no real tokens exist to store)
  implementation_backlog:
  - id: BE-AUTH-001
    name: Replace local bearer/header fixtures with JWT/OIDC-compatible authentication
      and derive user/tenant/branch from the authenticated principal
    depends_on: none (foundational)
    blocks:
    - COM-MOD-009
  - id: FE-AUTH-001
    name: Replace employee-portal LOCAL_DEV_FIXTURE_SESSION with a real login flow
      calling BE-AUTH-001
    depends_on:
    - BE-AUTH-001
  - id: APP-AUTH-001
    name: Replace mobile-app localAuth.ts local/dev stand-in with a real login flow,
      once a renderer stack is selected (TD-APP-001)
    depends_on:
    - BE-AUTH-001
    - TD-APP-001
closure_gate_compliance: '"Login, logout, session expiration, authenticated user context
  and permission loading baseline are defined with implementation backlog": satisfied
  via required_capabilities_baseline_definition and implementation_backlog above.
  Request-time backend permission enforcement is implemented for mapped API paths.
  "Eliminate or isolate any hardcoded user/actor of productive use; if it remains
  as a local/test fixture, it must be clearly separated and documented": satisfied
  — the one genuine hardcoded-productive-actor gap found (IdentityAccessService''s
  "system" role-assignment actor) was fixed in code; remaining backend/web/mobile
  fixtures are explicitly named, documented and isolated as non-production, per hardcoded_and_fixture_actor_inventory
  above.

  '
```
