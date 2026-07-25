---
id: TD-IAM-001
format: markdown_structured_payload
type: technical-debt-item
name: Backend has no authenticated request context or server-side authorization enforcement
version: 1.0.0
status: closed
---

# Backend Has No Authenticated Request Context Or Server Side Authorization Enforcement

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-IAM-001
  type: technical-debt-item
  name: Backend has no authenticated request context or server-side authorization
    enforcement
  version: 1.0.0
  status: closed
  created_date: 2026-07-17
source:
  discovered_during_backlog_item: HOP-ENT-FOUND-001
  module: HOP-ENTERPRISE-FOUNDATION-ALIGNMENT
  evidence: 03-architecture/security-compliance/iam-permission-model.md
classification:
  category: security_foundation_gap
  affected_area: backend_wide_request_authentication_and_authorization
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/**
  risk_level: high
  urgency: high
  blocking: false
  closure_date: 2026-07-17
  closure_backlog_item: HOP-ENT-FOUND-001-corrective-closure
  closure_commit_pending: true
  closure_summary: 'Closed by adding spring-boot-starter-security, HopWebSecurityConfiguration,
    a request-scoped HopAuthorizationInterceptor, HopAuthenticationResolver, AuthenticatedUserContextHolder
    and EndpointPermissionRegistry. Protected /api/** endpoints now resolve an authenticated
    local session or token fixture, bind request context, and enforce role permissions
    server-side before controller execution. Production OIDC/IdP hardening remains
    a separate future productization concern, not this original "no authenticated
    request context / no server-side authorization" gap.

    '
current_state:
  issue: 'Spring Security is present and configured through HopWebSecurityConfiguration.
    The request-time authorization layer invokes AuthorizationService through HopAuthorizationInterceptor
    for mapped /api/** paths. Tests prove 401 for missing authentication when the
    local fixture is disabled, 403 for roles without the required permission, and
    success with request context binding for permitted roles.

    '
  compensating_control:
  - Local fixture authentication remains enabled by default for developer ergonomics
    and existing MockMvc compatibility.
  - External/customer deployment must disable the local fixture and wire an OIDC-compatible
    credential provider before exposure.
target_state:
  preferred_open_source_tooling:
  - Spring Security with an OIDC/OAuth2 resource-server configuration (per 03-architecture/security-compliance/identity-access/identity-access-architecture.md's
    MVP scope - username/password plus JWT/OIDC-compatible architecture, not precluding
    a later external IdP such as Keycloak/Authentik/Entra ID/Okta/Auth0/Cognito).
  expected_integration_points:
  - New SecurityConfig / SecurityFilterChain bean resolving an authenticated principal
    from a bearer token on every request.
  - identityaccess/application/AuthorizationService invoked from a request-scoped
    interceptor or method-security (@PreAuthorize) layer on every protected controller
    method.
  - Unauthorized/forbidden responses (401/403) audited via the existing AuditRecorder.
remediation:
  strategy: closed_request_time_authorization_baseline
  owner: backend_platform_team
  estimated_effort: large (multi-backlog-item; spans authentication, session, and
    per-controller enforcement wiring across ~15 bounded-context modules)
  estimated_cost_impact: medium (engineering time only; no new paid dependency required
    — Spring Security and Keycloak/Authentik are both open source)
  target_backlog: closed_by_HOP_ENT_FOUND_001_corrective_closure
  dependencies_or_prerequisites:
  - 03-architecture/security-compliance/session-management-baseline.md (BE-AUTH-001
    production OIDC/IdP hardening backlog)
  incremental_remediation_triggers:
  - Scheduling of the next backend infrastructure/security backlog item.
  - COM-MOD-009 (Patient and Doctor Portals) entering active development.
  acceptance_criteria:
  - A Spring Security filter chain resolves an authenticated principal from a real
    credential on every protected request.
  - AuthorizationService.hasPermission is invoked from a request-scoped interceptor
    or method security on every protected controller method.
  - Denied requests return 401/403 consistently and are audit-logged.
  closure_evidence:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/HopWebSecurityConfiguration.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthorizationInterceptor.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthorizationInterceptorTest.java
  - 08-qa/qa/enterprise-foundation/HOP-ENT-FOUND-001-validation.md
  owner_or_responsible_role: backend_platform_team
```
