# MVP-MOD-001 — Domain Model

## Bounded Contexts

- `organization-management`
- `identity-access`
- `audit-compliance`
- `observability`

## Aggregates

### Laboratory

Owns:

- Laboratory profile.
- Laboratory settings.
- Laboratory license metadata.

Key events:

- `LaboratoryCreated`
- `LaboratoryUpdated`
- `LaboratorySuspended`

### Branch

Owns:

- Branch profile.
- Branch schedule baseline.
- Branch operational status.

Key events:

- `BranchCreated`
- `BranchUpdated`
- `BranchActivated`
- `BranchSuspended`

### UserAccount

Owns:

- Credentials reference.
- User status.
- Assigned roles.
- Access policies.

Key events:

- `UserCreated`
- `UserActivated`
- `UserLocked`
- `RoleAssigned`
- `PermissionGranted`

## Value Objects

- `TenantId`
- `LaboratoryId`
- `BranchId`
- `UserId`
- `RoleCode`
- `PermissionCode`
- `AccessScope`
- `AuditActor`
- `AuditSubject`

## Invariants

- A laboratory must belong to one tenant.
- A branch must belong to one laboratory.
- A user account must belong to one tenant.
- Role assignments must be scoped.
- Protected state changes must emit audit events.
- Audit events are append-only.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: MVP-MOD-001-DOMAIN-001
  type: domain-model
  name: MVP-MOD-001 Platform Foundation Domain Model
  version: 1.0.0
  status: approved
  human_readable: domain-model.md
  machine_readable: domain-model.md
  module: MVP-MOD-001
bounded_contexts:
- organization-management
- identity-access
- audit-compliance
- observability
aggregates:
- name: Laboratory
  owns:
  - Laboratory profile
  - Laboratory settings
  - Laboratory license metadata
  key_events:
  - LaboratoryCreated
  - LaboratoryUpdated
  - LaboratorySuspended
- name: Branch
  owns:
  - Branch profile
  - Branch schedule baseline
  - Branch operational status
  key_events:
  - BranchCreated
  - BranchUpdated
  - BranchActivated
  - BranchSuspended
- name: UserAccount
  owns:
  - Credentials reference
  - User status
  - Assigned roles
  - Access policies
  key_events:
  - UserCreated
  - UserActivated
  - UserLocked
  - RoleAssigned
  - PermissionGranted
value_objects:
- TenantId
- LaboratoryId
- BranchId
- UserId
- RoleCode
- PermissionCode
- AccessScope
- AuditActor
- AuditSubject
invariants:
- A laboratory must belong to one tenant.
- A branch must belong to one laboratory.
- A user account must belong to one tenant.
- Role assignments must be scoped.
- Protected state changes must emit audit events.
- Audit events are append-only.
```
