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
