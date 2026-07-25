---
id: HOP-BM-BCM-PLT-001
format: markdown_structured_payload
type: business-model
name: Identity and Access Management Business Model
version: 0.1.0
status: modeled
---

# Identity And Access Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-PLT-001
  type: business-model
  name: Identity and Access Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-001
  bounded_context: identity-access
  primary_aggregate: UserAccount
  model_kind: aggregate_owner
entities:
- id: ENT-IAM-001
  name: UserAccount
  is_aggregate_root: true
  description: 'Represents a platform user account, containing security credentials,
    roles, scopes, and status. It is shared across employee, patient, and doctor contexts.

    '
  fields:
  - name: userId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: username
    type: string
    required: true
  - name: email
    type: string
    required: true
  - name: passwordHash
    type: string
    required: true
  - name: status
    type: enum
    values:
    - pending_activation
    - active
    - locked
    - suspended
    required: true
  - name: failedLoginAttempts
    type: integer
    required: true
  - name: lockedUntil
    type: datetime
    required: false
  - name: assignedRoles
    type: list
    element_type: AssignedRole
    required: true
  - name: accessPolicies
    type: list
    element_type: AccessPolicy
    required: true
  - name: lastLoginAt
    type: datetime
    required: false
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-IAM-001
  name: AssignedRole
  description: Role code mapped to a scope type (tenant, laboratory, branch).
  fields:
  - name: roleCode
    type: string
    required: true
  - name: scopeType
    type: enum
    values:
    - tenant
    - laboratory
    - branch
    required: true
  - name: scopeId
    type: uuid
    required: true
- id: VO-IAM-002
  name: AccessPolicy
  description: Explicit permissions or restrictions attached directly to a user account.
  fields:
  - name: permissionCode
    type: string
    required: true
  - name: policyEffect
    type: enum
    values:
    - allow
    - deny
    required: true
invariants:
- id: INV-IAM-001
  statement: Only active users can initiate an authenticated session. Suspended, locked,
    or pending_activation users are blocked.
- id: INV-IAM-002
  statement: After 5 consecutive failed login attempts, status transitions to locked
    for a tenant-configured lockout duration (default 15 minutes).
- id: INV-IAM-003
  statement: Support-assisted login (impersonation) requires an active ticket/consent
    record, is restricted to non-clinical/non-financial screens, and must write an
    immutable audit log referencing the assistance reason.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-007 AuditMetadata
- brm_alignment:
  - BRM-001-R018 (all session actions must be audited)
```
