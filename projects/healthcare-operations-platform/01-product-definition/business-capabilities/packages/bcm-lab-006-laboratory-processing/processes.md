---
id: HOP-PROC-BCM-LAB-006
format: markdown_structured_payload
type: processes
name: Laboratory Processing Processes
version: 0.1.0
status: modeled
---

# Laboratory Processing Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-LAB-006
  type: processes
  name: Laboratory Processing Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-006
actors:
- id: laboratory-technician
  actor_ref: ACT-007
  name: Laboratory Technician
  source: ACM-001
- id: laboratory-device
  actor_ref: ACT-015
  name: Laboratory Device
  source: ACM-001
  note: Submits normalized device result messages; raw protocol normalization is BCM-PLT-004's
    responsibility.
processes:
- id: PRC-LPR-006-01
  name: Capture result value
  actor: laboratory-technician
  trigger: A received sample has been analyzed, manually or by an analyzer.
  commands:
  - CaptureResultValue
  preconditions:
  - Sample is in received status.
  - Actor holds result.capture, or the source is a normalized device message.
  steps:
  - Capture AnalyteSnapshot and ReferenceRangeSnapshot.
  - Record ResultValue from manual entry or normalized device message.
  - Create the LaboratoryResult aggregate in captured status.
  - Publish ResultCaptured.
  outcome: ResultCaptured
  rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-004
  - RN-007
  - RN-008
- id: PRC-LPR-006-02
  name: Record processing incident
  actor: laboratory-technician
  trigger: An equipment error, repeat requirement or contamination is observed during
    processing.
  commands:
  - RecordProcessingIncident
  preconditions:
  - LaboratoryResult exists in captured status.
  steps:
  - Capture ProcessingIncident with type, notes, actor and timestamp.
  outcome: ProcessingIncidentRecorded
  rules:
  - RN-005
- id: PRC-LPR-006-03
  name: Submit result for validation
  actor: laboratory-technician
  trigger: Result capture is complete and any relevant processing incidents are recorded.
  commands:
  - SubmitResultForValidation
  preconditions:
  - LaboratoryResult is in captured status.
  - Reliability-affecting incidents, if any, are recorded.
  steps:
  - Transition LaboratoryResult to pending_technical_validation.
  - Publish ResultSubmittedForValidation.
  outcome: ResultSubmittedForValidation
  rules:
  - RN-005
  - RN-006
commands:
- name: CaptureResultValue
  generatable: false
  custom_reason: Multi-source snapshot capture and device-message boundary enforcement.
- name: RecordProcessingIncident
  generatable: true
- name: SubmitResultForValidation
  generatable: false
  custom_reason: Incident-reliability judgment before allowing submission.
```
