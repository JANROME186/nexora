---
id: HOP-RULES-BCM-AI-003
format: markdown_structured_payload
type: business-rules
name: Result and Case Summaries Business Rules
version: 1.0.0
status: modeled
---

# Result and Case Summaries Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-AI-003
  type: business-rules
  status: modeled
capability_id: BCM-AI-003
rules:
  - id: AI-SUM-001
    statement: Result and Case Summaries must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-SUM-002
    statement: Result and Case Summaries must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-SUM-003
    statement: Result and Case Summaries must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
guardrails:
  no_autonomous_clinical_validation: true
  source_citations_required: true
  human_override_requires_reason: true
  ai_output_must_not_bypass_iam_or_audit: true
```
