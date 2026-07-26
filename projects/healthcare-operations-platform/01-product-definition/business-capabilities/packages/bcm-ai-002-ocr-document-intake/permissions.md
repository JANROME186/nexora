---
id: HOP-PERM-BCM-AI-002
format: markdown_structured_payload
type: permissions
name: OCR Document Intake Permissions
version: 1.0.0
status: modeled
---

# OCR Document Intake Permissions

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PERM-BCM-AI-002
  type: permissions
  status: modeled
capability_id: BCM-AI-002
permissions:
  - code: ai.ocr:request
    scope: tenant
    audit: required
  - code: ai.ocr:correct
    scope: tenant
    audit: required
  - code: ai.ocr:audit
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
