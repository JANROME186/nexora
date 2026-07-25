---
id: HOP-OBS-BCM-PLT-009
format: markdown_structured_payload
type: observability-model
name: Workflow Engine Observability Model
version: 1.0.0
---

# Workflow Engine Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-PLT-009
  type: observability-model
  name: Workflow Engine Observability Model
  version: 1.0.0
metrics:
- name: hop_workflow_executions_total
  type: counter
  labels:
  - workflow_code
  - status
  description: Count of workflow executions by status.
- name: hop_workflow_execution_duration_seconds
  type: histogram
  labels:
  - workflow_code
  description: Execution duration in seconds.
logs:
- event: WORKFLOW_STEP_EXECUTED
  level: INFO
  attributes:
  - execution_id
  - workflow_code
  - step_id
  - status
```
