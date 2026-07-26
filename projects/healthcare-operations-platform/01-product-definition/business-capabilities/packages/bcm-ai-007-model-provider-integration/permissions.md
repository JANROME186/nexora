---
id: HOP-PERM-BCM-AI-007
format: markdown_structured_payload
type: permissions
name: Model Provider Integration Permissions
version: 1.0.0
status: modeled
---

# Model Provider Integration Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-AI-007
  type: permissions
  status: modeled
capability_id: BCM-AI-007
permissions:
  - code: ai.provider:configure
    scope: tenant
    audit: required
  - code: ai.provider:route
    scope: tenant
    audit: required
  - code: ai.provider:audit
    scope: tenant
    audit: required
roles:
  - AI_OPERATOR
  - AI_REVIEWER
  - TENANT_ADMIN
iam_alignment:
  screen_level_permissions_supported: true
  future_granular_permission_alignment: TD-IAM-002
```
