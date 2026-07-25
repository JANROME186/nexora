---
id: HOP-EVT-BCM-ATT-003
format: markdown_structured_payload
type: events
name: Reception Management Events
version: 0.1.0
status: modeled
---

# Reception Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-ATT-003
  type: events
  name: Reception Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-003
domain_events:
- name: ReceptionVisitStarted
  description: A reception visit was created for a walk-in or scheduled patient.
  payload:
  - visitId
  - tenantId
  - branchId
  - patientId
  - actorId
  - intakeChannel
  audit: true
- name: ReceptionIdentityConfirmed
  description: Patient identity was confirmed for the visit.
  payload:
  - visitId
  - actorId
  - identityConfirmationMethod
  audit: true
- name: ReceptionVisitReadyForAdmission
  description: The visit is ready for order intake in Admission Management.
  payload:
  - visitId
  - patientId
  - branchId
  - actorId
  audit: true
- name: ReceptionQueueReordered
  description: The reception queue ordering was recomputed.
  payload:
  - branchId
  - reorderedAt
  audit: false
- name: ReceptionVisitAbandoned
  description: The patient left before admission.
  payload:
  - visitId
  - actorId
  audit: true
integration_events:
  published:
  - name: ReceptionVisitReadyForAdmission
    description: Signals Admission Management that a visit is ready for order intake.
    consumers:
    - orders-samples
  consumed:
  - name: AppointmentCheckedIn
    source: BCM-ATT-001
  - name: PatientRegistrationCommitted
    source: BCM-ATT-002
published_language:
- ReceptionVisitReadyForAdmission
```
