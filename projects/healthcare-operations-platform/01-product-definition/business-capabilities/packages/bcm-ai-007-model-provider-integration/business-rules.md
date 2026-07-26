---
id: HOP-RULES-BCM-AI-007
format: markdown_structured_payload
type: business-rules
name: Model Provider Integration Business Rules
version: 1.0.0
status: modeled
---

# Model Provider Integration Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-AI-007
  type: business-rules
  status: modeled
capability_id: BCM-AI-007
rules:
  - id: AI-PRV-001
    statement: Model Provider Integration must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-PRV-002
    statement: Model Provider Integration must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-PRV-003
    statement: Model Provider Integration must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
guardrails:
  no_autonomous_clinical_validation: true
  source_citations_required: true
  human_override_requires_reason: true
  ai_output_must_not_bypass_iam_or_audit: true
```
