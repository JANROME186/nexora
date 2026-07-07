# MVP-MOD-001 — Test Plan

## Contract Tests

- Create tenant returns `201`.
- Create laboratory rejects missing tenant.
- Create branch rejects missing laboratory.
- Create user rejects invalid email.
- Assign role rejects missing scope.
- Audit search requires permission.

## Domain Tests

- Laboratory belongs to one tenant.
- Branch belongs to one laboratory.
- Role assignment is scoped.
- User status changes emit events.
- Audit events are append-only.

## Security Tests

- Anonymous request to protected API returns unauthorized.
- Actor without permission receives forbidden.
- Branch-scoped actor cannot manage another branch.
- Service identity can append audit event but cannot manage users.

## Smoke Tests

- Start local stack.
- Create tenant.
- Create laboratory.
- Create branch.
- Create tenant admin.
- Assign role.
- Query audit event as authorized actor.
