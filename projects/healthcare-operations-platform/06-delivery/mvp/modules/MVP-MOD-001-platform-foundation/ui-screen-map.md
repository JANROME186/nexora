# MVP-MOD-001 — UI Screen Map

## Employee Portal Screens

| Screen | Actors | Purpose |
| --- | --- | --- |
| Platform Tenant List | ACT-001 | Create and review tenants. |
| Tenant Settings | ACT-001, ACT-002 | Manage tenant-level configuration. |
| Laboratory List | ACT-001, ACT-002 | Create and manage laboratories. |
| Branch List | ACT-002, ACT-003 | Create and manage branches. |
| User Management | ACT-001, ACT-002, ACT-003 | Create users and manage status. |
| Role Assignment | ACT-001, ACT-002, ACT-003 | Assign scoped roles. |
| Audit Search | ACT-001, ACT-002, ACT-003 | Review authorized audit records. |

## UX Requirements

- Every administration screen must show current tenant/laboratory/branch scope.
- Destructive or access-changing actions require confirmation.
- Permission failures must be clear and non-technical.
- Audit search must avoid exposing data outside actor scope.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: MVP-MOD-001-UI-001
  type: ui-screen-map
  name: MVP-MOD-001 Platform Foundation UI Screen Map
  version: 1.0.0
  status: approved
  human_readable: ui-screen-map.md
  machine_readable: ui-screen-map.md
  module: MVP-MOD-001
employee_portal_screens:
- screen: Platform Tenant List
  actors:
  - ACT-001
  purpose: Create and review tenants.
- screen: Tenant Settings
  actors:
  - ACT-001
  - ACT-002
  purpose: Manage tenant-level configuration.
- screen: Laboratory List
  actors:
  - ACT-001
  - ACT-002
  purpose: Create and manage laboratories.
- screen: Branch List
  actors:
  - ACT-002
  - ACT-003
  purpose: Create and manage branches.
- screen: User Management
  actors:
  - ACT-001
  - ACT-002
  - ACT-003
  purpose: Create users and manage status.
- screen: Role Assignment
  actors:
  - ACT-001
  - ACT-002
  - ACT-003
  purpose: Assign scoped roles.
- screen: Audit Search
  actors:
  - ACT-001
  - ACT-002
  - ACT-003
  purpose: Review authorized audit records.
ux_requirements:
- Every administration screen must show current tenant/laboratory/branch scope.
- Destructive or access-changing actions require confirmation.
- Permission failures must be clear and non-technical.
- Audit search must avoid exposing data outside actor scope.
```
