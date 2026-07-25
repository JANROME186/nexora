---
id: HOP-BM-BCM-RES-006
format: markdown_structured_payload
type: business-model
name: Critical Results Business Model
version: 0.1.0
status: modeled
---

# Critical Results Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-RES-006
  type: business-model
  name: Critical Results Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-006
  bounded_context: laboratory-results
  primary_aggregate: CriticalResultEscalation
  model_kind: aggregate_owner
entities:
- id: ENT-CRR-001
  name: CriticalResultEscalation
  is_aggregate_root: true
  description: 'The traceable escalation record required whenever a result is flagged
    critical. This capability owns this entity exclusively; LaboratoryResult and its
    criticalFlag remain owned by BCM-LAB-006/BCM-LAB-008 and are read here only through
    events or BCM-RES-001''s projection.

    '
  fields:
  - name: escalationId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: laboratoryId
    type: LaboratoryId
    required: true
  - name: resultId
    type: ResultId
    required: true
  - name: criticalReason
    type: string
    required: true
    description: Copied from LaboratoryResult.criticalFlag.criticalReason at escalation
      creation time.
  - name: assignedHandlerId
    type: UserId
    required: false
    description: The clinician currently responsible for acknowledging the critical
      value.
  - name: escalationTier
    type: integer
    required: true
    default: 1
    description: Increments when acknowledgement is not recorded within the deadline,
      routing to a broader or more senior audience.
  - name: acknowledgementDeadline
    type: datetime
    required: true
  - name: acknowledgedBy
    type: UserId
    required: false
  - name: acknowledgedAt
    type: datetime
    required: false
  - name: status
    type: enum
    values:
    - open
    - acknowledged
    - escalated
    - closed
    required: true
  - name: audit
    type: AuditMetadata
    required: true
invariants:
- id: INV-CRR-001
  statement: A CriticalResultEscalation must be created for every ResultFlaggedCritical
    event without exception; a critical flag without a corresponding escalation record
    is a defect.
- id: INV-CRR-002
  statement: An escalation not acknowledged by its acknowledgementDeadline must automatically
    advance to the next escalationTier and re-trigger a notification through BCM-RES-007.
- id: INV-CRR-003
  statement: An escalation transitions to closed only after acknowledgedBy and acknowledgedAt
    are both recorded; it can never be closed silently.
- id: INV-CRR-004
  statement: This capability never mutates LaboratoryResult state, including the criticalFlag
    field itself, which remains exclusively owned by BCM-LAB-008.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-004 UserId
  - VO-ID-009 ResultId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-LAB-008 Technical Validation (read-only event source: ResultFlaggedCritical)
  - BCM-RES-001 Result Management (read-only projection source)
  - BCM-RES-007 Result Notifications (delegated re-notification trigger on tier escalation)
- brm_alignment:
  - BRM-001-R013 (critical results require notification trace)
```
