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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: MVP-MOD-001-SEC-001
  type: security-and-audit-rules
  name: MVP-MOD-001 Platform Foundation Security and Audit Rules
  version: 1.0.0
  status: approved
  human_readable: security-and-audit-rules.md
  machine_readable: security-and-audit-rules.md
  module: MVP-MOD-001
security:
  rules:
  - All APIs require authenticated access except explicit health endpoints.
  - Role assignments must include scope.
  - Service identities must be distinguishable from human users.
  - Authorization must check actor, permission and scope.
  - Tenant isolation is mandatory for tenant-scoped data.
audit:
  required_for:
  - Tenant creation or status change.
  - Laboratory creation or status change.
  - Branch creation or status change.
  - User creation, activation, lock or suspension.
  - Role assignment or permission grant.
  - Audit event query.
  minimum_fields:
  - Event id
  - Occurrence timestamp
  - Actor id
  - Actor type
  - Tenant id where applicable
  - Action
  - Subject type
  - Subject id
  - Metadata
```
