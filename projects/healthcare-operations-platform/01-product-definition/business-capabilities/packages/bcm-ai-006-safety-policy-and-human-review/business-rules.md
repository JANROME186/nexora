---
id: HOP-RULES-BCM-AI-006
format: markdown_structured_payload
type: business-rules
name: Safety Policy and Human Review Business Rules
version: 1.0.0
status: modeled
---

# Safety Policy and Human Review Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-AI-006
  type: business-rules
  status: modeled
capability_id: BCM-AI-006
rules:
  - id: AI-SAFE-001
    statement: Safety Policy and Human Review must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-SAFE-002
    statement: Safety Policy and Human Review must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-SAFE-003
    statement: Safety Policy and Human Review must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
guardrails:
  no_autonomous_clinical_validation: true
  source_citations_required: true
  human_override_requires_reason: true
  ai_output_must_not_bypass_iam_or_audit: true
```
