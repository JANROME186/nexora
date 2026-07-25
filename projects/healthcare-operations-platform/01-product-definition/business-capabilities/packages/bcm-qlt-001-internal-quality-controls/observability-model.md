---
id: HOP-OBS-BCM-QLT-001
format: markdown_structured_payload
type: observability-model
name: Internal Quality Controls Observability Model
version: 0.1.0
status: modeled
---

# Internal Quality Controls Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-QLT-001
  type: observability-model
  name: Internal Quality Controls Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-001
  depends_on_capability: BCM-PLT-006
logs:
- event: quality_control_run_recorded
  level: info
  fields:
  - qcRunId
  - testDefinitionId
  - ruleEvaluation
  - acceptanceDecision
  correlation_id: qcRunId
- event: quality_control_out_of_control
  level: critical
  fields:
  - qcRunId
  - testDefinitionId
  correlation_id: qcRunId
- event: quality_control_override_recorded
  level: warn
  fields:
  - qcRunId
  - overriddenBy
  correlation_id: qcRunId
metrics:
- name: quality_control_runs_total
  type: counter
  labels:
  - tenantId
  - branchId
  - testDefinitionId
  - ruleEvaluation
- name: quality_control_overrides_total
  type: counter
  labels:
  - tenantId
  - branchId
traces:
- span: RecordQualityControlRun
  child_spans:
  - EvaluateWestgardRules
  - ValidateControlMaterialLot
- span: OverrideAcceptanceDecision
  child_spans:
  - ValidateSupervisorScope
audit_events:
- QualityControlRunRecorded
- QualityControlOverrideRecorded
alerts:
- name: QualityControlOutOfControlDetected
  condition: quality_control_run_recorded with ruleEvaluation out_of_control
  severity: critical
- name: QualityControlOverrideRateHigh
  condition: quality_control_overrides_total rate exceeds threshold
  severity: high
```
