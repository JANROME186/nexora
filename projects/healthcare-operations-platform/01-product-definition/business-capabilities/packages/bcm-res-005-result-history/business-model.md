---
id: HOP-BM-BCM-RES-005
format: markdown_structured_payload
type: business-model
name: Result History Business Model
version: 0.1.0
status: modeled
---

# Result History Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-RES-005
  type: business-model
  name: Result History Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-005
  bounded_context: laboratory-results
  primary_aggregate: PatientResultHistoryView
  model_kind: read_model_projection
entities:
- id: ENT-RHS-001
  name: PatientResultHistoryView
  is_aggregate_root: false
  described_as: read_model_projection
  owned_by_aggregate: none (independent read projection sourced from BCM-RES-004 delivery
    events)
  description: 'Denormalized, patient-scoped chronological projection of released,
    authorized results. Never the source of truth; LaboratoryResult (BCM-LAB-006)
    and Patient (BCM-PER-002) remain authoritative. Exists solely to support trend-aware
    multi-result history views.

    '
  fields:
  - name: historyEntryId
    type: uuid
    required: true
    identifier: true
  - name: tenantId
    type: TenantId
    required: true
  - name: patientId
    type: PatientId
    required: true
  - name: resultId
    type: ResultId
    required: true
  - name: deliveryTicketId
    type: uuid
    required: true
    description: References the authorizing BCM-RES-004 ResultDeliveryTicket; entries
      are added only after authorization.
  - name: analyteName
    type: string
    required: true
  - name: numericValue
    type: decimal
    required: false
  - name: releasedAt
    type: datetime
    required: true
  - name: trendIndicator
    type: enum
    values:
    - no_prior_value
    - stable
    - increased
    - decreased
    - significant_change
    required: false
invariants:
- id: INV-RHS-001
  statement: A PatientResultHistoryView entry is created only from an authorized ResultDeliveryAuthorized
    event (BCM-RES-004); it never includes a result the patient is not authorized
    to see.
- id: INV-RHS-002
  statement: This capability never mutates LaboratoryResult or Patient state; it is
    read-only end to end.
- id: INV-RHS-003
  statement: Trend computation compares only entries the requesting patient/doctor
    is themselves authorized to view; it never leaks a comparison value from an unauthorized
    result.
- id: INV-RHS-004
  statement: An amended result's history entry is updated only after BCM-RES-004 re-authorizes
    the amended delivery; it is never silently left showing the pre-amendment value.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-005 PatientId
  - VO-ID-009 ResultId
- capabilities:
  - BCM-RES-001 Result Management (read-only projection source)
  - BCM-RES-004 Digital Delivery (read-only event source: ResultDeliveryAuthorized,
      ResultAmended-driven re-authorization)
  - BCM-PER-002 Patient aggregate (read-only identity source)
- downstream_capabilities:
  - BCM-AI-005 (future, read-only consumer for trend-analysis assistance; never a
    writer)
  - BCM-AI-006 (future, read-only consumer; never a writer)
```
