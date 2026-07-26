---
id: HOP-PERM-BCM-AI-005
format: markdown_structured_payload
type: permissions
name: Retrieval Knowledge Grounding Permissions
version: 1.0.0
status: modeled
---

# Retrieval Knowledge Grounding Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-AI-005
  type: permissions
  status: modeled
capability_id: BCM-AI-005
permissions:
  - code: ai.grounding:manage
    scope: tenant
    audit: required
  - code: ai.grounding:use
    scope: tenant
    audit: required
  - code: ai.grounding:audit
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
