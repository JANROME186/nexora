---
id: HOP-OBS-BCM-PER-002
format: markdown_structured_payload
type: observability-model
name: Patient Management Observability Model
version: 0.1.0
status: modeled
---

# Patient Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-PER-002
  type: observability-model
  name: Patient Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-002
  depends_on_capability: BCM-PLT-006
logs:
- event: patient_registered
  level: info
  fields:
  - patientId
  - tenantId
  - actorId
- event: patient_updated
  level: info
  fields:
  - patientId
  - changedFields
  - actorId
- event: patient_merged
  level: info
  fields:
  - sourcePatientId
  - survivingPatientId
  - actorId
- event: patient_consent_recorded
  level: info
  fields:
  - patientId
  - consentId
  - consentType
- event: patient_representative_attached
  level: info
  fields:
  - patientId
  - representativeId
  - actorId
- event: patient_command_rejected
  level: warn
  fields:
  - patientId
  - commandName
  - reasonCode
metrics:
- name: patient_registration_total
  type: counter
  labels:
  - tenantId
  - laboratoryId
  - outcome
- name: patient_merge_total
  type: counter
  labels:
  - tenantId
- name: patient_command_latency_ms
  type: histogram
  labels:
  - tenantId
  - commandName
- name: patient_snapshot_projection_lag_seconds
  type: histogram
  labels:
  - tenantId
traces:
- span: RegisterPatient
  child_spans:
  - InvokeDuplicateDetection
  - PersistPatient
  - PublishPatientRegistered
- span: UpdatePatient
  child_spans:
  - LoadPatient
  - ApplyDelta
  - PublishPatientUpdated
- span: MergePatient
  child_spans:
  - FreezeSnapshot
  - RewireProjections
  - PublishPatientMerged
audit_events:
- PatientRegistered
- PatientUpdated
- PatientDeactivated
- PatientMerged
- PatientConsentRecorded
- PatientConsentRevoked
- PatientRepresentativeAttached
- PatientRepresentativeUpdated
- PatientRepresentativeRevoked
alerts:
- name: HighPatientCommandRejectionRate
  condition: patient_command_rejected rate exceeds threshold
  severity: warning
- name: PatientSnapshotProjectionLagHigh
  condition: patient_snapshot_projection_lag_seconds p95 exceeds threshold
  severity: warning
```
