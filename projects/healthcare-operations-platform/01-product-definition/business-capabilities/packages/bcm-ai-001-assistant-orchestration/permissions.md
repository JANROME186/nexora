---
id: HOP-PERM-BCM-AI-001
format: markdown_structured_payload
type: permissions
name: Assistant Orchestration Permissions
version: 1.0.0
status: modeled
---

# Assistant Orchestration Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-AI-001
  type: permissions
  status: modeled
capability_id: BCM-AI-001
permissions:
  - code: ai.assistant:use
    scope: tenant
    audit: required
  - code: ai.assistant:review
    scope: tenant
    audit: required
  - code: ai.assistant:admin
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
