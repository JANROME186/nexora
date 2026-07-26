---
id: HOP-PERM-BCM-AI-008
format: markdown_structured_payload
type: permissions
name: AI Audit and Evaluation Permissions
version: 1.0.0
status: modeled
---

# AI Audit and Evaluation Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-AI-008
  type: permissions
  status: modeled
capability_id: BCM-AI-008
permissions:
  - code: ai.audit:view
    scope: tenant
    audit: required
  - code: ai.evaluation:run
    scope: tenant
    audit: required
  - code: ai.evaluation:export
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
