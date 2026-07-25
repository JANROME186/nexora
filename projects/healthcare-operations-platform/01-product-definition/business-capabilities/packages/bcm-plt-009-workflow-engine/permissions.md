---
id: HOP-PERM-BCM-PLT-009
format: markdown_structured_payload
type: permissions
name: Workflow Engine Permissions
version: 1.0.0
---

# Workflow Engine Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-PLT-009
  type: permissions
  name: Workflow Engine Permissions
  version: 1.0.0
roles:
- SYSTEM_ADMIN
- PLATFORM_OPERATIONS
permissions:
- code: workflow:read
  description: View workflow definitions and execution logs.
  assigned_roles:
  - SYSTEM_ADMIN
  - PLATFORM_OPERATIONS
- code: workflow:execute
  description: Manually trigger operational workflows (backups, upgrades, maintenance).
  assigned_roles:
  - SYSTEM_ADMIN
  - PLATFORM_OPERATIONS
- code: workflow:rollback
  description: Manually initiate emergency rollback.
  assigned_roles:
  - SYSTEM_ADMIN
```
