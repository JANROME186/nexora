---
id: HOP-RULES-BCM-AI-008
format: markdown_structured_payload
type: business-rules
name: AI Audit and Evaluation Business Rules
version: 1.0.0
status: modeled
---

# AI Audit and Evaluation Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-AI-008
  type: business-rules
  status: modeled
capability_id: BCM-AI-008
rules:
  - id: AI-EVAL-001
    statement: AI Audit and Evaluation must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-EVAL-002
    statement: AI Audit and Evaluation must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-EVAL-003
    statement: AI Audit and Evaluation must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
guardrails:
  no_autonomous_clinical_validation: true
  source_citations_required: true
  human_override_requires_reason: true
  ai_output_must_not_bypass_iam_or_audit: true
```
