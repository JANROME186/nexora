---
id: HOP-PRM-BCM-ORG-003
format: markdown_structured_payload
type: permissions
name: Branch Management Permissions
version: 1.0.0
---

# Branch Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PRM-BCM-ORG-003
  type: permissions
  name: Branch Management Permissions
  version: 1.0.0
permissions:
- code: branch:create
  name: Create Branch
  description: Grants permission to register new branch locations under a laboratory.
  scope: tenant_admin
  resource: organization.branch
  action: create
- code: branch:read
  name: Read Branch Profile
  description: Grants read access to branch profile, address, schedules, and capacity
    parameters.
  scope: tenant_user
  resource: organization.branch
  action: read
- code: branch:update
  name: Update Branch Profile
  description: Grants permission to edit branch details, address, and capacity.
  scope: branch_admin
  resource: organization.branch
  action: update
- code: branch:manage_schedule
  name: Manage Branch Schedule
  description: Grants permission to update operating hours and holiday schedules.
  scope: branch_admin
  resource: organization.branch.schedule
  action: manage
- code: branch:update_status
  name: Update Branch Status
  description: Grants permission to transition branch status (OPERATIONAL, MAINTENANCE,
    SUSPENDED, CLOSED).
  scope: tenant_admin
  resource: organization.branch.status
  action: update
```
