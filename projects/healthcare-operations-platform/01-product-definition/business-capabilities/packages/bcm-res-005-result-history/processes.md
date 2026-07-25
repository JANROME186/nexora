---
id: HOP-PROC-BCM-RES-005
format: markdown_structured_payload
type: processes
name: Result History Processes
version: 0.1.0
status: modeled
---

# Result History Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-RES-005
  type: processes
  name: Result History Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-005
actors:
- id: patient
  actor_ref: ACT-012
  name: Patient
  source: ACM-001
- id: referring-doctor
  actor_ref: ACT-011
  name: Referring Doctor
  source: ACM-001
processes:
- id: PRC-RHS-005-01
  name: Project history entry
  actor: system
  trigger: ResultDeliveryAuthorized is published by BCM-RES-004.
  commands:
  - none (event-driven projection update only)
  preconditions:
  - Event is a recognized, authorized delivery.
  steps:
  - Create or update the PatientResultHistoryView entry.
  - Compute trendIndicator against the patient's prior entry for the same analyte,
    when authorized.
  outcome: ResultHistoryEntryProjected
  rules:
  - RN-001
  - RN-003
- id: PRC-RHS-005-02
  name: View result history
  actor: patient
  trigger: A patient or referring doctor requests their chronological result history.
  commands:
  - none (query only)
  preconditions:
  - Actor holds history.view.
  steps:
  - Apply recipient-scoped authorization filter.
  - Return the chronological history with trend indicators.
  - Record the query for audit.
  outcome: ResultHistoryViewed
  rules:
  - RN-005
  - RN-006
- id: PRC-RHS-005-03
  name: Update history entry after amendment
  actor: system
  trigger: ResultDeliveryWithheld is published, followed by a subsequent ResultDeliveryAuthorized
    for the amended result.
  commands:
  - none (event-driven projection update only)
  preconditions:
  - A prior PatientResultHistoryView entry exists for the resultId.
  steps:
  - Mark the existing entry as superseded pending re-authorization.
  - Update to the amended value only once re-authorized.
  outcome: ResultHistoryEntryUpdated
  rules:
  - RN-004
commands: []
```
