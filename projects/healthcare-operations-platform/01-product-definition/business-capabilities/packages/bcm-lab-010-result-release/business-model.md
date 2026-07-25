---
id: HOP-BM-BCM-LAB-010
format: markdown_structured_payload
type: business-model
name: Result Release Business Model
version: 0.1.0
status: modeled
---

# Result Release Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-LAB-010
  type: business-model
  name: Result Release Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-010
  bounded_context: laboratory-results
  primary_aggregate: LaboratoryResult (AGG-009, owned by BCM-LAB-006)
  model_kind: operational_process_over_shared_aggregate
entities:
- id: ENT-RLS-001
  name: ResultReleaseWorklistEntry
  is_aggregate_root: false
  described_as: process_record
  owned_by_aggregate: LaboratoryResult
  description: Queue entry for a medically validated result pending release.
  fields:
  - name: worklistEntryId
    type: uuid
    required: true
    identifier: true
  - name: resultId
    type: ResultId
    required: true
  - name: tenantId
    type: TenantId
    required: true
  - name: laboratoryId
    type: LaboratoryId
    required: true
  - name: queueStatus
    type: enum
    values:
    - pending_release
    - released
    - amended
    required: true
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-RLS-001
  name: ReleaseEligibilityCheck
  description: Confirmation that all release preconditions are satisfied before a
    result can be released.
  fields:
  - name: medicallyValidated
    type: boolean
    required: true
  - name: linkedSampleNotRejected
    type: boolean
    required: true
  - name: checkedAt
    type: datetime
    required: true
- id: VO-RLS-002
  name: AmendmentRequest
  description: Structured request to correct a released result.
  fields:
  - name: requestedBy
    type: DoctorId
    required: true
  - name: requestedAt
    type: datetime
    required: true
  - name: amendmentReason
    type: string
    required: true
  - name: correctedValue
    type: string
    required: true
invariants:
- id: INV-RLS-001
  statement: A result cannot be released before it is medically validated; release
    of a result still pending medical validation is refused.
- id: INV-RLS-002
  statement: A result whose linked Sample was rejected cannot be released, even if
    a value was captured before rejection was recorded (BRM-001-R010).
- id: INV-RLS-003
  statement: A released result's resultValue is immutable; any post-release correction
    must be represented as a new ResultAmendment event, never as an in-place update
    to resultValue.
- id: INV-RLS-004
  statement: An amendment must reference the amended-from value and reason and must
    itself be medically authorized; an unauthorized amendment request is refused.
- id: INV-RLS-005
  statement: This capability writes only LaboratoryResult.releaseRecord and LaboratoryResult.amendments;
    it must never write resultValue, technicalValidation, criticalFlag or medicalValidation
    directly.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-006 DoctorId
  - VO-ID-009 ResultId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-LAB-006 LaboratoryResult aggregate (delegated mutation target for releaseRecord
    and amendments)
  - BCM-LAB-009 medicalValidation field (release precondition source)
```
