---
id: HOP-BM-BCM-LAB-001
format: markdown_structured_payload
type: business-model
name: Diagnostic Order Management Business Model
version: 0.1.0
status: modeled
---

# Diagnostic Order Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-LAB-001
  type: business-model
  name: Diagnostic Order Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-001
  bounded_context: orders-samples
  primary_aggregate: DiagnosticOrder
  model_kind: aggregate_owner
entities:
- id: ENT-ORD-001
  name: DiagnosticOrder
  is_aggregate_root: true
  aggregate_ref: AGG-007
  description: 'The diagnostic order aggregate. Every mutation (create, price, accept,
    cancel, complete) is a command handled exclusively by this capability.

    '
  fields:
  - name: orderId
    type: OrderId
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
    - admission
    - quotation_conversion
    - portal_request_later
    required: true
  - name: sourceReferenceId
    type: uuid
    required: false
    description: Optional reference to the originating AppointmentSlot, ReceptionVisit,
      AdmissionRequest or QuotationRequest.
  - name: patientSnapshot
    type: PatientSnapshot
    required: true
  - name: doctorSnapshot
    type: DoctorSnapshot
    required: false
  - name: branchSnapshot
    type: BranchSnapshot
    required: true
  - name: orderLines
    type: list[OrderLine]
    required: true
  - name: clinicalNotes
    type: OrderClinicalNotes
    required: false
  - name: pricingSnapshot
    type: OrderPricingSnapshot
    required: true
  - name: status
    type: OrderStatus
    required: true
  - name: cancellationReason
    type: string
    required: false
  - name: audit
    type: AuditMetadata
    required: true
- id: ENT-ORD-002
  name: OrderLine
  is_aggregate_root: false
  owned_by_aggregate: DiagnosticOrder
  description: One catalog item (test or panel) requested within the order, captured
    as an immutable catalog snapshot.
  fields:
  - name: orderLineId
    type: uuid
    required: true
    identifier: true
  - name: catalogSnapshot
    type: CatalogSnapshot
    required: true
  - name: quantity
    type: integer
    required: true
    default: 1
  - name: lineStatus
    type: enum
    values:
    - pending
    - accepted
    - cancelled
    - completed
    required: true
value_objects:
- id: VO-ORD-001
  name: PatientSnapshot
  description: 'Immutable copy of patient identity fields captured at order time from
    BCM-PER-002. Never re-read live to mutate the order; order pages resolve display
    data from this snapshot.

    '
  fields:
  - name: patientId
    type: PatientId
    required: true
  - name: sourceVersion
    type: integer
    required: true
    description: Version of the Patient aggregate the snapshot was taken from.
  - name: name
    type: PersonName
    required: true
  - name: document
    type: PersonDocument
    required: true
  - name: birthDate
    type: date
    required: true
  - name: capturedAt
    type: datetime
    required: true
- id: VO-ORD-002
  name: DoctorSnapshot
  description: Immutable copy of referring-doctor identity fields captured at order
    time from BCM-PER-003.
  fields:
  - name: doctorId
    type: DoctorId
    required: true
  - name: sourceVersion
    type: integer
    required: true
  - name: name
    type: PersonName
    required: true
  - name: licenseNumber
    type: string
    required: true
  - name: capturedAt
    type: datetime
    required: true
- id: VO-ORD-003
  name: BranchSnapshot
  description: Immutable copy of branch identity and operational fields captured at
    order time from BCM-ORG-003.
  fields:
  - name: branchId
    type: BranchId
    required: true
  - name: sourceVersion
    type: integer
    required: true
  - name: name
    type: string
    required: true
  - name: capturedAt
    type: datetime
    required: true
- id: VO-ORD-004
  name: CatalogSnapshot
  description: Immutable copy of a published TestDefinition or panel captured at order
    time from BCM-SVC-001/002/003.
  fields:
  - name: testDefinitionId
    type: TestDefinitionId
    required: true
  - name: publishedVersion
    type: integer
    required: true
  - name: name
    type: string
    required: true
  - name: kind
    type: enum
    values:
    - test
    - panel
    required: true
  - name: sampleRequirementRef
    type: uuid
    required: false
  - name: capturedAt
    type: datetime
    required: true
- id: VO-ORD-005
  name: OrderPricingSnapshot
  description: Immutable copy of the price-list entries applied to the order, captured
    at order time from BCM-SVC-009.
  fields:
  - name: priceListId
    type: uuid
    required: true
  - name: priceListVersion
    type: integer
    required: true
  - name: lineAmounts
    type: list[Money]
    required: true
  - name: totalAmount
    type: Money
    required: true
  - name: capturedAt
    type: datetime
    required: true
- id: VO-ORD-006
  name: OrderClinicalNotes
  description: Free-text and structured clinical context captured at admission time.
  fields:
  - name: notes
    type: string
    required: false
  - name: diagnosisHints
    type: list[string]
    required: false
- id: VO-ORD-007
  name: OrderStatus
  description: Order lifecycle state.
  values:
  - draft
  - priced
  - accepted
  - in_progress
  - cancelled
  - completed
invariants:
- id: INV-ORD-001
  statement: A DiagnosticOrder must always carry a PatientSnapshot, BranchSnapshot
    and OrderPricingSnapshot; it never resolves these by live lookup at read time.
- id: INV-ORD-002
  statement: An order line may reference only a CatalogSnapshot taken from a published
    TestDefinition or panel version.
- id: INV-ORD-003
  statement: An order cannot transition to accepted without a pricing snapshot and
    at least one order line.
- id: INV-ORD-004
  statement: Only this capability may mutate DiagnosticOrder state; all other capabilities
    orchestrate through its commands.
- id: INV-ORD-005
  statement: A cancelled or completed order is immutable except for append-only audit
    and amendment events.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-003 BranchId
  - VO-ID-005 PatientId
  - VO-ID-006 DoctorId
  - VO-ID-007 OrderId
  - VO-001 Money
  - VO-002 PersonName
  - VO-007 AuditMetadata
- capabilities:
  - BCM-PER-002 Patient aggregate (read-only snapshot source)
  - BCM-PER-003 Doctor aggregate (read-only snapshot source)
  - BCM-ORG-003 Branch aggregate (read-only snapshot source)
  - BCM-SVC-001 / BCM-SVC-002 / BCM-SVC-003 published TestDefinition (read-only snapshot
    source)
  - BCM-SVC-009 published price list (read-only snapshot source)
```
