---
id: HOP-RULES-BCM-AI-002
format: markdown_structured_payload
type: business-rules
name: OCR Document Intake Business Rules
version: 1.0.0
status: modeled
---

# OCR Document Intake Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-AI-002
  type: business-rules
  status: modeled
capability_id: BCM-AI-002
rules:
  - id: AI-OCR-001
    statement: OCR Document Intake must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-OCR-002
    statement: OCR Document Intake must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-OCR-003
    statement: OCR Document Intake must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
guardrails:
  no_autonomous_clinical_validation: true
  source_citations_required: true
  human_override_requires_reason: true
  ai_output_must_not_bypass_iam_or_audit: true
```
