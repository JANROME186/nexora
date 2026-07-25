---
id: HOP-PROC-BCM-PER-002
format: markdown_structured_payload
type: processes
name: Patient Management Processes
version: 0.1.0
status: modeled
---

# Patient Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-PER-002
  type: processes
  name: Patient Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-002
actors:
- id: receptionist
  actor_ref: ACT-004
  name: Receptionist
  source: ACM-001
- id: tenant-administrator
  actor_ref: ACT-002
  name: Tenant Administrator
  source: ACM-001
- id: support-analyst
  actor_ref: ACT-018
  name: Support Analyst
  source: ACM-001
processes:
- id: PRC-PAT-002-01
  name: Register patient master data
  actor: receptionist
  trigger: A new patient must be recorded before appointment or order intake.
  commands:
  - RegisterPatient
  preconditions:
  - Actor holds patient.write.
  - Duplicate detection has been consulted.
  steps:
  - Collect natural key and consult PersonDuplicateDetector.
  - Confirm no acceptable duplicate exists or resolve match with actor decision.
  - Persist Patient aggregate with active status and initial audit metadata.
  - Publish PatientRegistered event.
  outcome: PatientRegistered
  rules:
  - RN-001
  - RN-002
  - RN-010
- id: PRC-PAT-002-02
  name: Update patient master data
  actor: receptionist
  trigger: Correction or addition to patient identity, contact, address or documents.
  commands:
  - UpdatePatient
  preconditions:
  - Patient exists and is not merged or deceased when field is terminal.
  steps:
  - Load current Patient state.
  - Apply delta and validate invariants.
  - Publish PatientUpdated with change delta.
  outcome: PatientUpdated
  rules:
  - RN-004
  - RN-008
  - RN-009
- id: PRC-PAT-002-03
  name: Record patient consent
  actor: receptionist
  trigger: Patient or representative grants or revokes a consent statement.
  commands:
  - RecordPatientConsent
  - RevokePatientConsent
  steps:
  - Validate consent type and grantor identity.
  - Append consent record.
  - Publish PatientConsentRecorded or PatientConsentRevoked.
  outcome: PatientConsentRecorded
  rules:
  - RN-007
- id: PRC-PAT-002-04
  name: Manage patient representative
  actor: receptionist
  trigger: A representative must be added, updated or revoked for a patient.
  commands:
  - AttachPatientRepresentative
  - UpdatePatientRepresentative
  - RevokePatientRepresentative
  steps:
  - Validate representative identity and authorization range.
  - Persist representative record.
  - Publish PatientRepresentativeAttached, PatientRepresentativeUpdated or PatientRepresentativeRevoked.
  outcome: PatientRepresentativeAttached
  rules:
  - RN-006
- id: PRC-PAT-002-05
  name: Merge patients within patient-management
  actor: support-analyst
  trigger: Operator confirms two patient records refer to the same person.
  commands:
  - MergePatient
  preconditions:
  - Both records are within the same tenant.
  - A surviving patientId is chosen.
  steps:
  - Freeze source patient snapshot.
  - Rewire aggregate references within patient-management projections.
  - Mark source patient status merged.
  - Publish PatientMerged.
  outcome: PatientMerged
  rules:
  - RN-005
- id: PRC-PAT-002-06
  name: Deactivate patient
  actor: tenant-administrator
  trigger: A patient record must be retired without deletion.
  commands:
  - DeactivatePatient
  steps:
  - Validate that no active order references the patient (mark warning).
  - Set status inactive and freeze mutations.
  - Publish PatientDeactivated.
  outcome: PatientDeactivated
  rules:
  - RN-009
commands:
- name: RegisterPatient
  generatable: false
  custom_reason: Duplicate detection integration is a custom rule.
- name: UpdatePatient
  generatable: true
- name: RecordPatientConsent
  generatable: true
- name: RevokePatientConsent
  generatable: false
  custom_reason: Append-only revocation history requires custom rule.
- name: AttachPatientRepresentative
  generatable: true
- name: UpdatePatientRepresentative
  generatable: true
- name: RevokePatientRepresentative
  generatable: false
  custom_reason: Authorization range close-out is custom rule.
- name: MergePatient
  generatable: false
  custom_reason: Cross-projection rewiring and idempotent replay.
- name: DeactivatePatient
  generatable: true
```
