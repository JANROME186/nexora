---
id: HOP-PERM-BCM-AI-003
format: markdown_structured_payload
type: permissions
name: Result and Case Summaries Permissions
version: 1.0.0
status: modeled
---

# Result and Case Summaries Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-AI-003
  type: permissions
  status: modeled
capability_id: BCM-AI-003
permissions:
  - code: ai.summary:request
    scope: tenant
    audit: required
  - code: ai.summary:approve
    scope: tenant
    audit: required
  - code: ai.summary:audit
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
