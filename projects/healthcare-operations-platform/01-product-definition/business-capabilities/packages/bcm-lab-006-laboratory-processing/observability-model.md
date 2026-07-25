---
id: HOP-OBS-BCM-LAB-006
format: markdown_structured_payload
type: observability-model
name: Laboratory Processing Observability Model
version: 0.1.0
status: modeled
---

# Laboratory Processing Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-LAB-006
  type: observability-model
  name: Laboratory Processing Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-006
  depends_on_capability: BCM-PLT-006
logs:
- event: result_captured
  level: info
  fields:
  - resultId
  - sampleId
  - branchId
  - actorId
  - analyteId
  - captureSource
  correlation_id: sampleId
- event: processing_incident_recorded
  level: warn
  fields:
  - resultId
  - incidentType
  - actorId
  correlation_id: sampleId
- event: result_submitted_for_validation
  level: info
  fields:
  - resultId
  - actorId
  correlation_id: sampleId
- event: result_value_implausible
  level: warn
  fields:
  - resultId
  - analyteId
  - rawValue
  correlation_id: sampleId
metrics:
- name: result_captured_total
  type: counter
  labels:
  - tenantId
  - branchId
  - captureSource
- name: processing_incident_recorded_total
  type: counter
  labels:
  - tenantId
  - branchId
  - incidentType
- name: result_capture_duration_ms
  type: histogram
  labels:
  - tenantId
  - branchId
traces:
- span: CaptureResultValue
  child_spans:
  - CaptureAnalyteSnapshot
  - CaptureReferenceRangeSnapshot
  - ValidateResultPlausibility
  - PersistLaboratoryResult
- span: SubmitResultForValidation
  child_spans:
  - CheckUnresolvedIncidents
audit_events:
- ResultCaptured
- ProcessingIncidentRecorded
- ResultSubmittedForValidation
alerts:
- name: HighProcessingIncidentRate
  condition: processing_incident_recorded_total rate exceeds threshold
  severity: warning
- name: DeviceMessageIngestionStalled
  condition: no ResultCaptured with captureSource=device_message for a connected analyzer
    within expected interval
  severity: critical
```
