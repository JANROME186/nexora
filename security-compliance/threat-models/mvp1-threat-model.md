---
id: THREAT-MVP1-001
name: MVP 1 Threat Model
version: 0.19.0
status: Draft
owner: Security Architecture
artifact_type: threat_model
---

# MVP 1 Threat Model

## Scope

Initial threat model for MVP 1 modules:

- Identity and access.
- Patients.
- Physicians.
- Orders.
- Samples.
- Results.
- Cash and billing.
- Portals.
- APIs.

## Key threats

| Threat | Risk | Baseline control |
|---|---|---|
| Unauthorized patient data access | High | Tenant isolation, RBAC/ABAC, audit |
| Cross-tenant data leakage | Critical | Tenant filters, tests, data access guards |
| Result tampering | Critical | Audit trail, versioning, validation permissions |
| Payment cancellation abuse | High | Permission checks, reason required, audit |
| Credential compromise | High | MFA support, password policy, token lifecycle |
| API abuse | High | Rate limits, auth, input validation |
| Data export misuse | High | Export permissions, logging, purpose control |
| AI data leakage | High | AI privacy controls, provider abstraction, logging |
| Secrets exposure | Critical | Secret provider, scanning, no secrets in repo |

## Required validation

Every MVP 1 module must include security acceptance criteria.
