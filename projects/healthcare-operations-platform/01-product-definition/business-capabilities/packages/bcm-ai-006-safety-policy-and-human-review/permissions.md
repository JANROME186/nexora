---
id: HOP-PERM-BCM-AI-006
format: markdown_structured_payload
type: permissions
name: Safety Policy and Human Review Permissions
version: 1.0.0
status: modeled
---

# Safety Policy and Human Review Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-AI-006
  type: permissions
  status: modeled
capability_id: BCM-AI-006
permissions:
  - code: ai.safety:decide
    scope: tenant
    audit: required
  - code: ai.safety:override
    scope: tenant
    audit: required
  - code: ai.safety:audit
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
