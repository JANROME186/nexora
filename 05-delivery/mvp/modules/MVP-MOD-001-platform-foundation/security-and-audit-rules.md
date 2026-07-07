# MVP-MOD-001 — Security and Audit Rules

## Security

- All APIs require authenticated access except explicit health endpoints.
- Role assignments must include scope.
- Service identities must be distinguishable from human users.
- Authorization must check actor, permission and scope.
- Tenant isolation is mandatory for tenant-scoped data.

## Audit

Audit is required for:

- Tenant creation or status change.
- Laboratory creation or status change.
- Branch creation or status change.
- User creation, activation, lock or suspension.
- Role assignment or permission grant.
- Audit event query.

## Minimum Audit Fields

- Event id.
- Occurrence timestamp.
- Actor id.
- Actor type.
- Tenant id where applicable.
- Action.
- Subject type.
- Subject id.
- Metadata.
