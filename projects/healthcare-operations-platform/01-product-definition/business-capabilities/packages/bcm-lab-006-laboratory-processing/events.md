---
id: HOP-EVT-BCM-LAB-006
format: markdown_structured_payload
type: events
name: Laboratory Processing Events
version: 0.1.0
status: modeled
---

# Laboratory Processing Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-LAB-006
  type: events
  name: Laboratory Processing Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-006
domain_events:
- name: ResultCaptured
  description: A result value was captured and the LaboratoryResult aggregate created.
  payload:
  - resultId
  - orderId
  - sampleId
  - tenantId
  - branchId
  - actorId
  - analyteId
  - captureSource
  audit: true
- name: ProcessingIncidentRecorded
  description: A processing incident was recorded against a captured result.
  payload:
  - resultId
  - incidentType
  - actorId
  - recordedAt
  audit: true
- name: ResultSubmittedForValidation
  description: The result was submitted for technical validation.
  payload:
  - resultId
  - actorId
  - submittedAt
  audit: true
integration_events:
  published:
  - name: ResultCaptured
    description: Signals technical validation that a result is ready for review.
    consumers:
    - laboratory-results
  - name: ResultSubmittedForValidation
    description: Signals the technical validation worklist.
    consumers:
    - laboratory-results
  consumed:
  - name: SampleReceived
    source: BCM-LAB-005
  - name: TestDefinitionPublished
    source: BCM-SVC-004
  - name: ReferenceRangeUpdated
    source: BCM-SVC-006
published_language:
- ResultCaptured
- ResultSubmittedForValidation
```
