---
id: AUTHZ-ARCH-001
name: Authorization Model
version: 0.19.0
status: Draft
owner: Security Architecture
artifact_type: authorization_architecture
---

# Authorization Model

## Objective

Define how Nexora controls access to functionality and data across laboratories, branches, roles, positions, users, portals, APIs, and mobile apps.

## Authorization approach

Nexora will combine:

- RBAC: role-based access control.
- ABAC: attribute-based access control for tenant, branch, ownership, status, country, license and feature flags.
- Permission catalog: explicit permission keys for every protected action.

## Permission naming convention

`domain.resource.action.scope`

Examples:

- `patient.record.create.branch`
- `patient.record.read.tenant`
- `result.report.validate.branch`
- `billing.invoice.cancel.branch`
- `security.user.assign-role.tenant`

## Organizational hierarchy

Authorization must consider:

- Laboratory.
- Branch.
- Department.
- Position.
- Role.
- User.
- Delegations.
- Temporary access.

## Required enforcement points

- API Gateway.
- Backend use cases.
- Frontend action visibility.
- Mobile action visibility.
- Report generation.
- Data exports.
- AI assistant access.

## Critical rule

Hiding a button in the UI is never sufficient authorization. Every protected operation must be enforced in backend use cases.
