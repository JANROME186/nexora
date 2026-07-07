# 02 Business Rules

| ID | Rule | Priority |
|---|---|---|
| CAP-002-BR-001 | Every tenant must have one primary laboratory organization. | Critical |
| CAP-002-BR-002 | A laboratory may have one or many branches. | Critical |
| CAP-002-BR-003 | Every branch must belong to exactly one tenant. | Critical |
| CAP-002-BR-004 | A branch cannot operate unless its status is Active. | Critical |
| CAP-002-BR-005 | A branch must have at least one valid address before it can be activated. | High |
| CAP-002-BR-006 | A branch must have at least one operational schedule before it can accept appointments. | High |
| CAP-002-BR-007 | Branch codes must be unique within the same tenant. | Critical |
| CAP-002-BR-008 | Legal tax identifiers must follow the active country pack validation rules. | High |
| CAP-002-BR-009 | A branch may restrict available studies and imaging services. | High |
| CAP-002-BR-010 | Orders must be created under a specific branch. | Critical |
| CAP-002-BR-011 | Caja operations must be scoped to a branch. | Critical |
| CAP-002-BR-012 | Inventory stock may be global, branch-level or warehouse-level depending on configuration. | High |
| CAP-002-BR-013 | Users may have access to one, many or all branches depending on assigned permissions. | Critical |
| CAP-002-BR-014 | A branch with pending orders cannot be deleted; it can only be deactivated. | Critical |
| CAP-002-BR-015 | All changes to organizational structure must be audited. | Critical |
| CAP-002-BR-016 | The primary branch cannot be deactivated unless another active branch is assigned as primary. | High |
| CAP-002-BR-017 | Country, timezone, currency and locale must be defined at tenant level and may be overridden at branch level. | High |
| CAP-002-BR-018 | A branch can only enable services supported by the tenant's license plan. | High |
| CAP-002-BR-019 | Service availability must consider branch schedule, holidays and equipment/resource availability. | Medium |
| CAP-002-BR-020 | Branch configuration changes must not break existing historical transactions. | Critical |
