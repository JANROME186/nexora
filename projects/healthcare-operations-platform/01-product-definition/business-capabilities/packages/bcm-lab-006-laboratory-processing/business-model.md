---
id: HOP-BM-BCM-LAB-006
format: markdown_structured_payload
type: business-model
name: Laboratory Processing Business Model
version: 0.1.0
status: modeled
---

# Laboratory Processing Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-LAB-006
  type: business-model
  name: Laboratory Processing Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-006
  bounded_context: laboratory-results
  primary_aggregate: LaboratoryResult
  model_kind: aggregate_owner
entities:
- id: ENT-LPR-001
  name: LaboratoryResult
  is_aggregate_root: true
  aggregate_ref: AGG-009
  description: 'The laboratory result aggregate. This capability creates it and captures
    its value; BCM-LAB-008, BCM-LAB-009 and BCM-LAB-010 hold delegated authority to
    mutate specific named fields (technicalValidation, medicalValidation, releaseRecord
    and amendments) through their own commands, never through direct persistence access.

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
  - name: analyteSnapshot
    type: AnalyteSnapshot
    required: true
  - name: referenceRangeSnapshot
    type: ReferenceRangeSnapshot
    required: true
  - name: resultValue
    type: ResultValue
    required: true
  - name: captureSource
    type: enum
    values:
    - manual_entry
    - device_message
    required: true
  - name: processingIncidents
    type: list[ProcessingIncident]
    required: true
  - name: technicalValidation
    type: TechnicalValidationRecord
    required: false
    description: Owned by BCM-LAB-008; null until PerformTechnicalValidation executes.
  - name: criticalFlag
    type: CriticalResultFlag
    required: false
    description: Owned by BCM-LAB-008; null unless FlagCriticalResult executes.
  - name: medicalValidation
    type: MedicalValidationRecord
    required: false
    description: Owned by BCM-LAB-009; null until PerformMedicalValidation executes.
  - name: releaseRecord
    type: ResultReleaseRecord
    required: false
    description: Owned by BCM-LAB-010; null until ReleaseResult executes.
  - name: amendments
    type: list[ResultAmendment]
    required: true
    description: Owned by BCM-LAB-010.
  - name: status
    type: ResultStatus
    required: true
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-LPR-001
  name: AnalyteSnapshot
  description: Immutable copy of the published analyte/test-definition captured at
    result-capture time from BCM-SVC-004.
  fields:
  - name: testDefinitionId
    type: TestDefinitionId
    required: true
  - name: analyteId
    type: uuid
    required: true
  - name: publishedVersion
    type: integer
    required: true
  - name: name
    type: string
    required: true
  - name: unit
    type: string
    required: true
  - name: method
    type: string
    required: false
  - name: capturedAt
    type: datetime
    required: true
- id: VO-LPR-002
  name: ReferenceRangeSnapshot
  description: Immutable copy of the applicable reference range captured at result-capture
    time from BCM-SVC-006.
  fields:
  - name: referenceRangeId
    type: uuid
    required: true
  - name: publishedVersion
    type: integer
    required: true
  - name: lowValue
    type: string
    required: false
  - name: highValue
    type: string
    required: false
  - name: criticalLowValue
    type: string
    required: false
  - name: criticalHighValue
    type: string
    required: false
  - name: capturedAt
    type: datetime
    required: true
- id: VO-LPR-003
  name: ResultValue
  description: The captured analytical value with unit, method and normalization metadata.
  fields:
  - name: rawValue
    type: string
    required: true
  - name: numericValue
    type: decimal
    required: false
  - name: unit
    type: string
    required: true
  - name: method
    type: string
    required: false
  - name: capturedAt
    type: datetime
    required: true
  - name: capturedBy
    type: UserId
    required: false
    description: Null when captureSource is device_message.
  - name: deviceReference
    type: string
    required: false
    description: Normalized device/message reference when captureSource is device_message.
- id: VO-LPR-004
  name: ProcessingIncident
  description: A recorded processing exception (equipment error, repeat required,
    dilution required, contamination suspected).
  fields:
  - name: incidentType
    type: enum
    values:
    - equipment_error
    - repeat_required
    - dilution_required
    - contamination_suspected
    - other
    required: true
  - name: notes
    type: string
    required: false
  - name: recordedBy
    type: UserId
    required: true
  - name: recordedAt
    type: datetime
    required: true
- id: VO-LPR-005
  name: TechnicalValidationRecord
  description: Owned and mutated exclusively by BCM-LAB-008; modeled here only as
    a field placeholder on the shared aggregate.
  fields:
  - name: validatedBy
    type: UserId
    required: true
  - name: validatedAt
    type: datetime
    required: true
- id: VO-LPR-006
  name: CriticalResultFlag
  description: Owned and mutated exclusively by BCM-LAB-008; modeled here only as
    a field placeholder on the shared aggregate.
  fields:
  - name: flaggedBy
    type: UserId
    required: true
  - name: flaggedAt
    type: datetime
    required: true
  - name: criticalReason
    type: string
    required: true
- id: VO-LPR-007
  name: MedicalValidationRecord
  description: Owned and mutated exclusively by BCM-LAB-009; modeled here only as
    a field placeholder on the shared aggregate.
  fields:
  - name: validatedBy
    type: DoctorId
    required: true
  - name: validatedAt
    type: datetime
    required: true
- id: VO-LPR-008
  name: ResultReleaseRecord
  description: Owned and mutated exclusively by BCM-LAB-010; modeled here only as
    a field placeholder on the shared aggregate.
  fields:
  - name: releasedBy
    type: DoctorId
    required: true
  - name: releasedAt
    type: datetime
    required: true
- id: VO-LPR-009
  name: ResultAmendment
  description: Owned and mutated exclusively by BCM-LAB-010; modeled here only as
    a field placeholder on the shared aggregate.
  fields:
  - name: amendedBy
    type: DoctorId
    required: true
  - name: amendedAt
    type: datetime
    required: true
  - name: amendmentReason
    type: string
    required: true
  - name: previousValue
    type: ResultValue
    required: true
- id: VO-LPR-010
  name: ResultStatus
  description: Result lifecycle state.
  values:
  - captured
  - pending_technical_validation
  - technically_validated
  - pending_medical_validation
  - medically_validated
  - released
  - amended
invariants:
- id: INV-LPR-001
  statement: A LaboratoryResult cannot be created without a received Sample, an AnalyteSnapshot
    and a ReferenceRangeSnapshot captured at a single consistent point in time.
- id: INV-LPR-002
  statement: A LaboratoryResult cannot exist for a rejected Sample; capture against
    a rejected sample is refused.
- id: INV-LPR-003
  statement: Only BCM-LAB-006, BCM-LAB-008, BCM-LAB-009 and BCM-LAB-010 may mutate
    LaboratoryResult state, each restricted to its own named field set; AI capabilities
    may read but must never validate, release or amend (BRM-001-R017).
- id: INV-LPR-004
  statement: A released result is immutable except through an explicit ResultAmendment
    event; in-place mutation of a released resultValue is forbidden.
- id: INV-LPR-005
  statement: Device-sourced result values must reference a normalized message from
    BCM-PLT-004; this capability never parses raw ASTM/HL7 payloads directly.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-003 BranchId
  - VO-ID-004 UserId
  - VO-ID-006 DoctorId
  - VO-ID-007 OrderId
  - VO-ID-008 SampleId
  - VO-ID-009 ResultId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-LAB-005 Sample aggregate (read-only received-sample source)
  - BCM-SVC-004 published Analyte/TestDefinition (read-only snapshot source)
  - BCM-SVC-006 published ReferenceRange (read-only snapshot source)
  - BCM-PLT-004 normalized device message envelope (read-only ingestion source, later
    MVP-MOD-008 scope)
- aggregate_catalog:
  - AGG-009 LaboratoryResult (forbidden_mutators: orders-samples, billing-tax)
```
