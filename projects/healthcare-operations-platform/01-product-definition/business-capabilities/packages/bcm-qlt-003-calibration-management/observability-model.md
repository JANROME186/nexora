---
id: HOP-OBS-BCM-QLT-003
format: markdown_structured_payload
type: observability-model
name: Calibration Management Observability Model
version: 0.1.0
status: modeled
---

# Calibration Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-QLT-003
  type: observability-model
  name: Calibration Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-003
  depends_on_capability: BCM-PLT-006
logs:
- event: calibration_recorded
  level: info
  fields:
  - calibrationEventId
  - inventoryItemId
  - result
  - nextDueDate
  correlation_id: calibrationEventId
- event: calibration_failed
  level: critical
  fields:
  - calibrationEventId
  - inventoryItemId
  correlation_id: calibrationEventId
metrics:
- name: calibrations_recorded_total
  type: counter
  labels:
  - tenantId
  - branchId
  - result
- name: calibrations_due_gauge
  type: gauge
  labels:
  - tenantId
  - branchId
traces:
- span: RecordCalibration
  child_spans:
  - ValidateItemTypeEligibility
  - PublishCalibrationFailedIfApplicable
audit_events:
- CalibrationRecorded
- CalibrationFailed
alerts:
- name: CalibrationOverdue
  condition: calibrations_due_gauge exceeds configured lead-time threshold without
    a new CalibrationRecorded
  severity: high
- name: CalibrationFailedDetected
  condition: CalibrationFailed occurs
  severity: critical
```
