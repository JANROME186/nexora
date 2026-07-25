---
id: HOP-BM-BCM-ATT-002
format: markdown_structured_payload
type: business-model
name: Patient Registration Business Model
version: 0.1.0
status: modeled
---

# Patient Registration Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-ATT-002
  type: business-model
  name: Patient Registration Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-002
  bounded_context: patient-management
  primary_aggregate: Patient
  model_kind: operational_process_over_master_data
entities:
- id: ENT-REG-001
  name: PatientRegistrationRequest
  is_aggregate_root: false
  described_as: process_record
  owned_by_aggregate: Patient
  description: Structured intake captured by the receptionist prior to committing
    a Patient aggregate change.
  fields:
  - name: registrationRequestId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: laboratoryId
    type: LaboratoryId
    required: true
  - name: branchId
    type: BranchId
    required: true
  - name: intakeChannel
    type: enum
    values:
    - walk_in
    - appointment
    - portal_handoff
    - migration_import
    required: true
  - name: candidatePatientId
    type: PatientId
    required: false
  - name: registrationKind
    type: enum
    values:
    - new_patient
    - existing_patient_confirmation
    - representative_registration
    required: true
  - name: naturalKey
    type: PersonNaturalKey
    required: true
  - name: draftPatientData
    type: DraftPatientData
    required: true
  - name: draftRepresentativeData
    type: DraftRepresentativeData
    required: false
  - name: consentSelections
    type: list[ConsentSelection]
    required: false
  - name: outcome
    type: enum
    values:
    - pending
    - committed
    - cancelled
    - rejected
    required: true
  - name: outcomePatientId
    type: PatientId
    required: false
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-REG-001
  name: DraftPatientData
  description: Draft patient data captured during registration intake.
  references:
  - PersonName
  - PersonDocument
  - PersonContact
  - Address
  - preferredLocale
  - birthDate
  - sexAtBirth
  - patientCode
- id: VO-REG-002
  name: DraftRepresentativeData
  description: Draft representative data captured during registration intake.
  references:
  - relationship
  - PersonName
  - PersonDocument
  - contact
  - authorizationValidRange
- id: VO-REG-003
  name: ConsentSelection
  description: Consent statements collected at first registration or renewal.
  fields:
  - name: consentType
    type: enum
    values:
    - data_processing
    - portal_access
    - notification_channel
    - marketing
    - research
    required: true
  - name: granted
    type: boolean
    required: true
  - name: grantedBy
    type: enum
    values:
    - patient
    - representative
    required: true
process_orchestration:
- id: ORC-REG-001
  name: Registration orchestration outcome
  depends_on:
  - BCM-PER-001 duplicate detection
  - BCM-PER-002 patient commands
  outcome:
  - PatientRegistrationCommitted
  - PatientRegistrationCancelled
invariants:
- id: INV-REG-001
  statement: A registration request must consult duplicate detection before committing
    a new patient.
- id: INV-REG-002
  statement: Committing a registration must use BCM-PER-002 aggregate commands rather
    than direct persistence.
- id: INV-REG-003
  statement: A representative registration must attach a PatientRepresentative during
    commit.
- id: INV-REG-004
  statement: A registration cannot be committed if branch, laboratory and tenant scope
    are not aligned with the actor.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-003 BranchId
  - VO-ID-005 PatientId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-PER-001 PersonDuplicateDetector
  - BCM-PER-002 Patient aggregate commands
```
