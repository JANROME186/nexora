---
id: HOP-BM-BCM-RES-001
format: markdown_structured_payload
type: business-model
name: Result Management Business Model
version: 0.1.0
status: modeled
---

# Result Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-RES-001
  type: business-model
  name: Result Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-001
  bounded_context: laboratory-results
  primary_aggregate: LaboratoryResult (AGG-009, owned by BCM-LAB-006; read-only)
  model_kind: read_model_projection
entities:
- id: ENT-RMG-001
  name: ResultSearchIndexEntry
  is_aggregate_root: false
  described_as: read_model_projection
  owned_by_aggregate: none (independent read projection sourced from LaboratoryResult
    events)
  description: 'Denormalized, query-optimized projection of one LaboratoryResult,
    rebuilt from domain events. Never the source of truth; LaboratoryResult (BCM-LAB-006)
    remains authoritative for all business state. This entity exists solely to support
    fast internal search and worklists without querying the aggregate''s write model
    directly.

    '
  fields:
  - name: resultId
    type: ResultId
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
  - name: orderId
    type: OrderId
    required: true
  - name: sampleId
    type: SampleId
    required: true
  - name: patientId
    type: PatientId
    required: true
  - name: analyteName
    type: string
    required: true
  - name: status
    type: enum
    values:
    - captured
    - pending_technical_validation
    - technically_validated
    - pending_medical_validation
    - medically_validated
    - released
    - amended
    required: true
  - name: criticalFlagPresent
    type: boolean
    required: true
    default: false
  - name: lastEventAt
    type: datetime
    required: true
  - name: projectionVersion
    type: integer
    required: true
    description: Monotonically increasing counter used to detect and reconcile projection
      staleness against the source aggregate.
- id: ENT-RMG-002
  name: ResultAccessAuditEntry
  is_aggregate_root: false
  described_as: process_record
  owned_by_aggregate: none
  description: Append-only record of every read of a result through this capability,
    internal or external, satisfying the "all result access is audited" requirement.
  fields:
  - name: accessId
    type: uuid
    required: true
    identifier: true
  - name: resultId
    type: ResultId
    required: true
  - name: actorId
    type: UserId
    required: false
    description: Null when the accessor is an external patient/doctor identity rather
      than an internal UserId; see BCM-RES-004 for external access auditing conventions.
  - name: actorType
    type: enum
    values:
    - internal_staff
    - patient
    - doctor
    - patient_representative
    - system
    required: true
  - name: accessedAt
    type: datetime
    required: true
  - name: accessChannel
    type: enum
    values:
    - employee_portal
    - patient_portal
    - doctor_portal
    - mobile_app
    - api
    required: true
invariants:
- id: INV-RMG-001
  statement: ResultSearchIndexEntry is never written to directly; it is rebuilt only
    from consumed LaboratoryResult domain events (ResultCaptured, ResultTechnicallyValidated,
    ResultFlaggedCritical, ResultMedicallyValidated, ResultReleased, ResultAmended).
- id: INV-RMG-002
  statement: This capability issues no command against LaboratoryResult, Sample, Patient
    or Doctor; it is read-only end to end.
- id: INV-RMG-003
  statement: Every read of a result through this capability's query surface appends
    a ResultAccessAuditEntry; a read without a corresponding audit entry is a defect.
- id: INV-RMG-004
  statement: Internal staff visibility of pre-release results is scoped to the actor's
    role and laboratory; no cross-laboratory visibility without an explicit administrative
    scope.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-003 BranchId
  - VO-ID-004 UserId
  - VO-ID-005 PatientId
  - VO-ID-007 OrderId
  - VO-ID-008 SampleId
  - VO-ID-009 ResultId
- capabilities:
  - BCM-LAB-006 LaboratoryResult aggregate (read-only event source)
  - BCM-LAB-008 Technical Validation (read-only event source)
  - BCM-LAB-009 Medical Validation (read-only event source)
  - BCM-LAB-010 Result Release (read-only event source)
- aggregate_catalog:
  - AGG-009 LaboratoryResult (this capability is not listed in forbidden_mutators
    because it never mutates the aggregate)
```
