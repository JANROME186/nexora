---
id: HOP-PERM-BCM-PLT-002
format: markdown_structured_payload
type: permissions
name: Platform Configuration Permissions
version: 1.0.0
---

# Platform Configuration Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-PLT-002
  type: permissions
  name: Platform Configuration Permissions
  version: 1.0.0
roles:
- SYSTEM_ADMIN
- PLATFORM_OPERATIONS
- TENANT_ADMIN
permissions:
- code: config:read
  description: Read platform parameters and feature flags.
  assigned_roles:
  - SYSTEM_ADMIN
  - PLATFORM_OPERATIONS
  - TENANT_ADMIN
- code: config:manage
  description: Modify platform parameters and encrypted keys.
  assigned_roles:
  - SYSTEM_ADMIN
- code: feature_flags:toggle
  description: Enable, disable, or alter feature flag rollout rules.
  assigned_roles:
  - SYSTEM_ADMIN
  - PLATFORM_OPERATIONS
- code: masking_policy:manage
  description: Configure tenant PII data masking policies (addressing TD-BE-008).
  assigned_roles:
  - SYSTEM_ADMIN
  - TENANT_ADMIN
```
