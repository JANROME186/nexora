---
id: HOP-PRM-BCM-ORG-002
format: markdown_structured_payload
type: permissions
name: Laboratory Management Permissions
version: 1.0.0
---

# Laboratory Management Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PRM-BCM-ORG-002
  type: permissions
  name: Laboratory Management Permissions
  version: 1.0.0
permissions:
- code: laboratory:create
  name: Create Laboratory
  description: Grants permission to register new laboratories under tenant administration.
  scope: tenant_admin
  resource: organization.laboratory
  action: create
- code: laboratory:read
  name: Read Laboratory Profile
  description: Grants read access to laboratory profile, sanitary licenses, and operating
    parameters.
  scope: tenant_user
  resource: organization.laboratory
  action: read
- code: laboratory:update
  name: Update Laboratory Profile
  description: Grants permission to edit laboratory branding, tax ID, and operating
    hours.
  scope: tenant_admin
  resource: organization.laboratory
  action: update
- code: laboratory:manage_license
  name: Manage Sanitary License & Director
  description: Grants permission to update sanitary licenses, upload certificates,
    and assign clinical directors.
  scope: compliance_officer
  resource: organization.laboratory.license
  action: manage
- code: laboratory:update_status
  name: Update Laboratory Status
  description: Grants permission to suspend, reactivate, or archive laboratory operations.
  scope: tenant_admin
  resource: organization.laboratory.status
  action: update
```
