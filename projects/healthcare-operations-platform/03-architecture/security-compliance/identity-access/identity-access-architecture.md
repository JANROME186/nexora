---
id: IAM-ARCH-001
name: Identity and Access Architecture
version: 0.19.0
status: Draft
owner: Security Architecture
artifact_type: identity_architecture
---

# Identity and Access Architecture

## Objective

Define how Nexora authenticates users, services, applications, devices, and agents in a provider-agnostic way.

## Identity model

Nexora must support multiple identity providers through an abstraction layer:

- Local development identity provider.
- Keycloak or Authentik for self-hosted/on-premise deployments.
- Enterprise IdPs such as Entra ID, Okta, Auth0, Cognito, or equivalent.

## Identity types

- Platform administrator.
- Laboratory owner.
- Branch administrator.
- Employee.
- Physician.
- Patient.
- External system.
- Service account.
- AI agent identity.

## Authentication requirements

- OpenID Connect preferred.
- OAuth2 for delegated access.
- MFA supported by policy.
- Short-lived access tokens.
- Refresh token rotation where supported.
- Service-to-service authentication.
- API key strategy only for specific external integrations and never for user sessions.

## Tenant-aware identity

A user may belong to multiple laboratories or branches. Permissions must always be evaluated within the active tenant context.

## MVP scope

MVP 1 may start with username/password plus JWT/OIDC-compatible architecture, but it must not prevent migration to an external IdP.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: IAM-ARCH-001
name: Identity and Access Architecture
version: 0.19.0
status: Draft
identity_provider_strategy:
  provider_agnostic: true
  supported_patterns:
  - local-development-idp
  - self-hosted-idp
  - enterprise-idp
standards:
- OpenID Connect
- OAuth2
- JWT
identity_types:
- platform_admin
- laboratory_owner
- branch_admin
- employee
- physician
- patient
- external_system
- service_account
- ai_agent
requirements:
  tenant_aware_identity: true
  mfa_supported: true
  service_to_service_authentication: true
  api_keys_limited_to_integrations: true
```
