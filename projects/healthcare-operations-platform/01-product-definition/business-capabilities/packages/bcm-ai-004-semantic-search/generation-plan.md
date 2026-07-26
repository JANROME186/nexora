---
id: HOP-GEN-BCM-AI-004
format: markdown_structured_payload
type: generation-plan
name: Semantic Search Generation Plan
version: 1.0.0
status: modeled
---

# Semantic Search Generation Plan

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-GEN-BCM-AI-004
  type: generation-plan
  status: modeled
capability_id: BCM-AI-004
generated_outputs:
  backend:
  - controller
  - service
  - repository_port
  - audit_event_mapper
  employee_portal:
  - SEMANTIC_SEARCH_WORKBENCH_screen
  - SEMANTIC_INDEX_ADMIN_screen
custom_implementation_points:
  - tenant_index_filter
  - rank_explanation_policy
non_generatable_decisions:
  - safety policy thresholds
  - provider adapter implementation
  - clinical governance approval criteria
```
