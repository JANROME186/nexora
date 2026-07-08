# ADR-014: Identity, Access & Workforce Management as a Core Capability

## Status

Accepted

## Context

Nexora requires secure multi-tenant and multi-branch operations. Users must be able to operate only within their authorized laboratory, branch, role and permission scope.

## Decision

Identity, Access & Workforce Management is defined as CAP-003 and treated as a core capability that protects every clinical, administrative and financial module.

Authorization will use RBAC + ABAC. Backend APIs must enforce authorization directly. UI and mobile visibility rules are not considered security controls.

## Consequences

- Every API must declare required permissions.
- Branch-scoped access must be validated in backend policies.
- Permission and role changes require auditability.
- Future SSO/MFA can be added without changing the business capability model.
