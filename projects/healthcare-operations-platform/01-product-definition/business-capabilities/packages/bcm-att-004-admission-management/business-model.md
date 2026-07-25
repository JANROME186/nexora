---
id: HOP-BM-BCM-ATT-004
format: markdown_structured_payload
type: business-model
name: Admission Management Business Model
version: 0.1.0
status: modeled
---

# Admission Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-ATT-004
  type: business-model
  name: Admission Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-004
  bounded_context: orders-samples
  primary_aggregate: DiagnosticOrder
  model_kind: operational_process_over_master_data
entities:
- id: ENT-ADM-001
  name: AdmissionRequest
  is_aggregate_root: false
  described_as: process_record
  owned_by_aggregate: DiagnosticOrder
  description: Structured order-intake draft assembled at the front desk prior to
    committing a DiagnosticOrder.
  fields:
  - name: admissionId
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
  - name: visitId
    type: uuid
    required: true
    description: Reference to the originating ReceptionVisit.
  - name: patientId
    type: PatientId
    required: true
  - name: doctorId
    type: DoctorId
    required: false
  - name: catalogSelection
    type: list[AdmissionCatalogSelection]
    required: true
  - name: clinicalNotesDraft
    type: string
    required: false
  - name: consentConfirmed
    type: boolean
    required: true
    default: false
  - name: sampleRequirementsAcknowledged
    type: boolean
    required: true
    default: false
  - name: admissionStatus
    type: enum
    values:
    - draft
    - ready_for_order
    - order_created
    - rejected
    required: true
  - name: createdOrderId
    type: OrderId
    required: false
  - name: rejectionReason
    type: string
    required: false
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-ADM-001
  name: AdmissionCatalogSelection
  description: A published test or panel selected for the admission, prior to becoming
    an order line CatalogSnapshot.
  fields:
  - name: testDefinitionId
    type: TestDefinitionId
    required: true
  - name: kind
    type: enum
    values:
    - test
    - panel
    required: true
  - name: quantity
    type: integer
    required: true
    default: 1
process_orchestration:
- id: ORC-ADM-001
  name: Admission to order commit
  depends_on:
  - BCM-LAB-001 CreateDiagnosticOrder, PriceDiagnosticOrder, AcceptDiagnosticOrder
  outcome:
  - DiagnosticOrderCreated
  - OrderPriced
  - OrderAccepted
invariants:
- id: INV-ADM-001
  statement: An admission request cannot reach ready_for_order status unless catalogSelection
    is non-empty and every item is published.
- id: INV-ADM-002
  statement: An admission request cannot commit to an order without consentConfirmed
    and sampleRequirementsAcknowledged both being true when the tenant requires them.
- id: INV-ADM-003
  statement: Committing an admission request must invoke BCM-LAB-001 aggregate commands
    rather than direct order persistence.
- id: INV-ADM-004
  statement: An admission request can only originate from a ReceptionVisit with identityConfirmed
    true.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-003 BranchId
  - VO-ID-005 PatientId
  - VO-ID-006 DoctorId
  - VO-ID-007 OrderId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-ATT-003 ReceptionVisit (originating reference)
  - BCM-SVC-001/002 published TestDefinition (catalog validation)
  - BCM-SVC-009 published price list (pricing trigger)
  - BCM-LAB-001 DiagnosticOrder aggregate commands (commit target)
```
