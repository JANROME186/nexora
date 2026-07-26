---
id: HOP-PERM-BCM-AI-004
format: markdown_structured_payload
type: permissions
name: Semantic Search Permissions
version: 1.0.0
status: modeled
---

# Semantic Search Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-AI-004
  type: permissions
  status: modeled
capability_id: BCM-AI-004
permissions:
  - code: ai.search:query
    scope: tenant
    audit: required
  - code: ai.search:index
    scope: tenant
    audit: required
  - code: ai.search:audit
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
