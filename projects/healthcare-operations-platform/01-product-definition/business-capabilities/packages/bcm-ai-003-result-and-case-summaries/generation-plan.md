---
id: HOP-GEN-BCM-AI-003
format: markdown_structured_payload
type: generation-plan
name: Result and Case Summaries Generation Plan
version: 1.0.0
status: modeled
---

# Result and Case Summaries Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-AI-003
  type: generation-plan
  status: modeled
capability_id: BCM-AI-003
generated_outputs:
  backend:
  - controller
  - service
  - repository_port
  - audit_event_mapper
  employee_portal:
  - SUMMARY_REQUEST_PANEL_screen
  - SUMMARY_HUMAN_REVIEW_screen
custom_implementation_points:
  - clinical_guardrail_prompt
  - source_citation_required
non_generatable_decisions:
  - safety policy thresholds
  - provider adapter implementation
  - clinical governance approval criteria
```
