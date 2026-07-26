---
id: HOP-GEN-BCM-AI-001
format: markdown_structured_payload
type: generation-plan
name: Assistant Orchestration Generation Plan
version: 1.0.0
status: modeled
---

# Assistant Orchestration Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-AI-001
  type: generation-plan
  status: modeled
capability_id: BCM-AI-001
generated_outputs:
  backend:
  - controller
  - service
  - repository_port
  - audit_event_mapper
  employee_portal:
  - AI_ASSISTANT_WORKBENCH_screen
  - AI_SESSION_REVIEW_QUEUE_screen
custom_implementation_points:
  - session_context_policy
  - human_review_checkpoint
non_generatable_decisions:
  - safety policy thresholds
  - provider adapter implementation
  - clinical governance approval criteria
```
