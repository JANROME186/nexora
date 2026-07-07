# 02 Business Rules

| ID | Rule | Priority |
|---|---|---|
| CAP-003-BR-001 | Every user must belong to exactly one tenant. | Critical |
| CAP-003-BR-002 | Every employee must belong to one tenant and may be assigned to one or more branches. | Critical |
| CAP-003-BR-003 | A user account may be linked to one employee profile. | High |
| CAP-003-BR-004 | A user cannot access a branch unless explicitly assigned or granted all-branch access. | Critical |
| CAP-003-BR-005 | Permissions must be evaluated using tenant, branch, role and policy attributes. | Critical |
| CAP-003-BR-006 | Administrative users cannot assign permissions they do not have. | Critical |
| CAP-003-BR-007 | Super administrator privileges must be separated from tenant administrator privileges. | Critical |
| CAP-003-BR-008 | A suspended tenant disables access for all tenant users except platform support roles under controlled policy. | Critical |
| CAP-003-BR-009 | A deactivated employee cannot authenticate or perform actions. | Critical |
| CAP-003-BR-010 | A user with pending cash drawer operations cannot be deactivated without reassignment or closure. | High |
| CAP-003-BR-011 | Role changes must be audited with previous and new values. | Critical |
| CAP-003-BR-012 | Permission changes must be auditable and traceable to the administrator who performed the change. | Critical |
| CAP-003-BR-013 | Clinical result validation permissions must require explicit assignment. | Critical |
| CAP-003-BR-014 | Caja cancellation permissions must be separate from caja sales permissions. | Critical |
| CAP-003-BR-015 | Inventory adjustment permissions must be separate from inventory read permissions. | High |
| CAP-003-BR-016 | A user must have only one active session policy at a time. | Medium |
| CAP-003-BR-017 | Authentication failures must be tracked for security monitoring. | High |
| CAP-003-BR-018 | Permission checks must be enforced by backend APIs and must not depend only on UI hiding. | Critical |
| CAP-003-BR-019 | Access reviews must be possible by tenant, branch, role, employee and permission. | High |
| CAP-003-BR-020 | Emergency access, when enabled, must be time-bound and fully audited. | High |
