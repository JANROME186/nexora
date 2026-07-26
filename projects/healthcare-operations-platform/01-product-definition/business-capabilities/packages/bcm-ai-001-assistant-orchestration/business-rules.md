---
id: HOP-RULES-BCM-AI-001
format: markdown_structured_payload
type: business-rules
name: Assistant Orchestration Business Rules
version: 1.0.0
status: modeled
---

# Assistant Orchestration Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-AI-001
  type: business-rules
  status: modeled
capability_id: BCM-AI-001
rules:
  - id: AI-ORCH-001
    statement: Assistant Orchestration must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-ORCH-002
    statement: Assistant Orchestration must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
  - id: AI-ORCH-003
    statement: Assistant Orchestration must keep AI output advisory, attributable, tenant-scoped and reviewable before it affects operational records.
guardrails:
  no_autonomous_clinical_validation: true
  source_citations_required: true
  human_override_requires_reason: true
  ai_output_must_not_bypass_iam_or_audit: true
```
