---
id: TD-IAM-003
format: markdown_structured_payload
type: technical-debt-item
name: BCM-PLT-001 MFA, service-account credentials and the domain.resource.action.scope
  permission grammar are not implemented
version: 2.0.0
status: closed
---

# Bcm Plt 001 Mfa, Service Account Credentials And The Domain.Resource.Action.Scope Permission Grammar Are Not Implemented

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-IAM-003
  type: technical-debt-item
  name: BCM-PLT-001 MFA, service-account credentials and the domain.resource.action.scope
    permission grammar are not implemented
  version: 2.0.0
  status: closed
  created_date: 2026-07-23
  closed_date: 2026-07-26
  closed_during_backlog_item: HOP-HARD-IAM-001
source:
  discovered_during_backlog_item: COM-MOD-012-BE-001
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  evidence: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-BE-001-validation.md
classification:
  category: identity_access_capability_gap
  affected_area: identity_access_extensions
  affected_components:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/application/IdentityAccessService.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/application/TotpService.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/domain/UserAccount.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/domain/ServiceAccountCredential.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/domain/PermissionScope.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthenticationResolver.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthorizationInterceptor.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/adapter/in/web/AuthController.java
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/adapter/in/web/IdentityAccessController.java
  risk_level: low
  urgency: low
  blocking: false
  reason_non_blocking: All three acceptance criteria are met with real, tested runtime
    behavior rather than a stub. Residual risk is documented under residual_risk below
    and is scoped to depth-of-adoption, not the absence of the capability.
current_state:
  issue: 'HOP-HARD-IAM-001 closed this debt: (1) MFA is enforced on IdentityAccessService.login
    via a new RFC 6238 TotpService — a user enrolled through POST /api/auth/mfa/enroll
    must supply a valid 30-second TOTP code (with +/-1 step drift tolerance) or login
    throws MfaRequiredException/MfaVerificationFailedException (mapped to 401 by
    AuthExceptionHandler). (2) Service-account credentials are a new non-interactive
    principal: IdentityAccessController.createServiceAccount (protected by SCREEN_USERS,
    same as human user management) provisions a client id/BCrypt-hashed secret pair;
    POST /api/auth/service-token authenticates it via IdentityAccessService.authenticateServiceAccount
    without ever touching a human UserAccount or session, returning a service-session:
    token that HopAuthenticationResolver resolves strictly from the persisted
    ServiceAccountCredential (the role is never trusted from the token itself, unlike
    local-session:, so a forged token cannot grant an arbitrary role). (3) The
    domain.resource.action.scope grammar is modeled by the new PermissionScope record
    and piloted end to end on POST /api/quality/capa/{id}/approve: HopAuthorizationInterceptor
    authorizes that specific action through AuthorizationService.scopedPermissionsForRoles
    instead of the coarse SCREEN_CAPA_MANAGEMENT flat permission, and RolePermissionCatalog
    grants the equivalent scoped permission only to ADMIN (the only role that already
    held the flat permission), so no existing screen-level access changed.'
  residual_risk:
  - MFA is enforced only on the human username/password login path
    (IdentityAccessService.login); other authentication entry points
    (local-session fixture bootstrap, assistance-session support impersonation)
    do not require a second factor. Accepted because those paths are either local-dev
    fixtures or already sandboxed to the read-only SUPPORT role.
  - Service accounts have no self-service secret rotation or revocation endpoint yet;
    an operator must provision a new credential and stop using the old clientId to
    rotate. Accepted because no production integration consumes this capability yet.
  - The domain.resource.action.scope grammar is adopted for exactly one endpoint
    (CAPA approval) as a proof that the grammar and PermissionCode can coexist without
    regression; it is not yet the primary authorization model. TD-IAM-002 tracks
    further incremental adoption.
target_state:
  preferred_remediation: Extend MFA enforcement to any future non-fixture authentication
    entry points, add service-account secret rotation/revocation, and migrate additional
    high-risk endpoints to the domain.resource.action.scope grammar incrementally as
    TD-IAM-002 already tracks.
  quality_goal: Extend the IAM model further only when a concrete operation needs it,
    not speculatively ahead of need.
remediation:
  strategy: closed_with_documented_residual_risk
  owner: backend_team
  estimated_effort: large
  estimated_cost_impact: medium
  target_backlog: HOP-HARD-IAM-001
  acceptance_criteria:
  - MFA is enforceable on at least one authentication path. [met]
  - Service-account credentials authenticate without a human session. [met]
  - At least one endpoint is registered against the domain.resource.action.scope grammar
    instead of a flat PermissionCode, without regressing existing screen-level checks. [met]
  progress_evidence:
  - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/application/TotpService.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/application/TotpServiceTest.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/application/IdentityAccessServiceTest.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthenticationResolverTest.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/adapter/in/web/AuthControllerStandaloneTest.java
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthorizationInterceptorTest.java
  - 08-qa/qa/final-hardening/HOP-HARD-IAM-001-validation.md
  owner_or_responsible_role: backend_team
```
