---
id: HOP-BM-BCM-QLT-002
format: markdown_structured_payload
type: business-model
name: External Quality Controls Business Model
version: 0.1.0
status: modeled
---

# External Quality Controls Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-QLT-002
  type: business-model
  name: External Quality Controls Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-002
  bounded_context: external-quality-compliance
  primary_aggregate: ExternalQualityEvaluation
  model_kind: aggregate_owner
entities:
- id: ENT-EQC-001
  name: ExternalQualityEvaluation
  is_aggregate_root: true
  description: 'Primary aggregate for recording external quality control / proficiency
    testing evaluations. Tracks provider survey metadata, measured values, peer group
    statistics, calculated z-score, and performance rating.

    '
  fields:
  - name: evaluationId
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
  - name: providerName
    type: string
    required: true
    description: Name of accredited EQA provider (e.g., CAP, RIQAS, PEEC, UK NEQAS).
  - name: programCode
    type: string
    required: true
  - name: surveyCycle
    type: string
    required: true
    description: Survey round identifier (e.g., 2026-ROUND-01).
  - name: testDefinitionId
    type: uuid
    required: true
    description: Read-only reference to published TestDefinition (BCM-SVC-002).
  - name: sampleCode
    type: string
    required: true
  - name: measuredValue
    type: decimal
    required: true
  - name: submittedAt
    type: datetime
    required: true
  - name: peerGroupMean
    type: decimal
    required: false
  - name: peerGroupSd
    type: decimal
    required: false
  - name: peerGroupCount
    type: integer
    required: false
  - name: zScore
    type: decimal
    required: false
  - name: performanceRating
    type: enum
    values:
    - pending_evaluation
    - acceptable
    - warning
    - unacceptable
    required: true
  - name: evaluatedAt
    type: datetime
    required: false
  - name: capaInvestigationId
    type: uuid
    required: false
    description: Reference to triggered CAPA investigation if performance is unacceptable
      (BCM-QLT-006).
  - name: storedDocumentId
    type: uuid
    required: false
    description: Reference to stored provider evaluation report PDF (BCM-PLT-008).
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-EQC-001
  name: EqaProviderInfo
  description: Metadata of external quality evaluation provider.
  fields:
  - name: providerCode
    type: string
    required: true
  - name: accreditationNumber
    type: string
    required: false
invariants:
- id: INV-EQC-001
  statement: Performance rating cannot be 'acceptable' if absolute zScore exceeds
    2.0 (|z| > 2.0).
- id: INV-EQC-002
  statement: An evaluation with rating 'unacceptable' must reference a triggered CapaInvestigation.
- id: INV-EQC-003
  statement: TestDefinition reference is read-only and must be active in catalog at
    submission time.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-003 BranchId
  - VO-ID-004 UserId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-SVC-002 Test Catalog (read-only reference)
  - BCM-QLT-006 CAPA Management (downstream target for unacceptable evaluations)
  - BCM-PLT-008 Document Management (read-only reference to report attachment)
```
