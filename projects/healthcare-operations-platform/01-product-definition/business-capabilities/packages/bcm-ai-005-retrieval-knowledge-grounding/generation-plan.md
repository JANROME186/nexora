---
id: HOP-GEN-BCM-AI-005
format: markdown_structured_payload
type: generation-plan
name: Retrieval Knowledge Grounding Generation Plan
version: 1.0.0
status: modeled
---

# Retrieval Knowledge Grounding Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-AI-005
  type: generation-plan
  status: modeled
capability_id: BCM-AI-005
generated_outputs:
  backend:
  - controller
  - service
  - repository_port
  - audit_event_mapper
  employee_portal:
  - GROUNDING_PACKAGE_ADMIN_screen
  - GROUNDING_SOURCE_REVIEW_screen
custom_implementation_points:
  - source_allowlist_policy
  - retrieval_version_lock
non_generatable_decisions:
  - safety policy thresholds
  - provider adapter implementation
  - clinical governance approval criteria
```
