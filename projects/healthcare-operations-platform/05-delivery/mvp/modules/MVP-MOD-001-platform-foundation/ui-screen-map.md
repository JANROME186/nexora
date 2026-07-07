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
