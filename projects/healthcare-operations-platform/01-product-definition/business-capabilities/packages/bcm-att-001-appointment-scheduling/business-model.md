---
id: HOP-BM-BCM-ATT-001
format: markdown_structured_payload
type: business-model
name: Appointment Scheduling Business Model
version: 0.1.0
status: modeled
---

# Appointment Scheduling Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-ATT-001
  type: business-model
  name: Appointment Scheduling Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-001
  bounded_context: orders-samples
  primary_aggregate: DiagnosticOrder
  model_kind: operational_process_over_master_data
entities:
- id: ENT-APT-001
  name: AppointmentSlot
  is_aggregate_root: false
  described_as: process_record
  owned_by_aggregate: DiagnosticOrder
  description: A requested, confirmed or cancelled appointment for a patient at a
    branch.
  fields:
  - name: appointmentId
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
  - name: patientId
    type: PatientId
    required: true
  - name: doctorId
    type: DoctorId
    required: false
  - name: requestedCatalogItems
    type: list[RequestedCatalogItem]
    required: true
  - name: scheduledWindow
    type: DateRange
    required: true
  - name: channel
    type: enum
    values:
    - walk_in_scheduling
    - phone
    - employee_portal
    - patient_portal_request_later
    required: true
  - name: status
    type: enum
    values:
    - requested
    - confirmed
    - checked_in
    - cancelled
    - no_show
    - completed
    required: true
  - name: linkedOrderId
    type: OrderId
    required: false
    description: Set once Reception or Admission converts the appointment into a DiagnosticOrder.
  - name: cancellationReason
    type: string
    required: false
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-APT-001
  name: RequestedCatalogItem
  description: A published test or panel selected at scheduling time, referenced by
    id only until order creation captures its snapshot.
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
- id: VO-APT-002
  name: PreparationSummary
  description: Preparation instructions surfaced to the patient for the requested
    items, sourced from BCM-SVC-005.
  fields:
  - name: testDefinitionId
    type: TestDefinitionId
    required: true
  - name: instructions
    type: string
    required: true
process_orchestration:
- id: ORC-APT-001
  name: Appointment to order handoff
  depends_on:
  - BCM-LAB-001 CreateDiagnosticOrder
  - BCM-ATT-003 check-in confirmation
  outcome:
  - AppointmentCheckedIn
  - AppointmentLinkedToOrder
invariants:
- id: INV-APT-001
  statement: An appointment cannot be confirmed for a branch that is not operationally
    active.
- id: INV-APT-002
  statement: An appointment cannot be confirmed if the requested window overlaps an
    existing confirmed appointment for the same patient at the same branch.
- id: INV-APT-003
  statement: An appointment must reference only published catalog items when items
    are selected at scheduling time.
- id: INV-APT-004
  statement: Converting an appointment into a diagnostic order must use BCM-LAB-001
    CreateDiagnosticOrder rather than direct order persistence.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-002 LaboratoryId
  - VO-ID-003 BranchId
  - VO-ID-005 PatientId
  - VO-ID-006 DoctorId
  - VO-006 DateRange
  - VO-007 AuditMetadata
- capabilities:
  - BCM-PER-002 Patient aggregate (identity reference)
  - BCM-ORG-003 Branch aggregate (operational status)
  - BCM-SVC-001/002 published TestDefinition (catalog reference)
  - BCM-SVC-005 preparation instructions
  - BCM-LAB-001 DiagnosticOrder aggregate commands (handoff target)
```
