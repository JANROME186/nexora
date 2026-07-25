---
id: HOP-PROC-BCM-LAB-001
format: markdown_structured_payload
type: processes
name: Diagnostic Order Management Processes
version: 0.1.0
status: modeled
---

# Diagnostic Order Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-LAB-001
  type: processes
  name: Diagnostic Order Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-001
actors:
- id: receptionist
  actor_ref: ACT-004
  name: Receptionist
  source: ACM-001
- id: referring-doctor
  actor_ref: ACT-011
  name: Referring Doctor
  source: ACM-001
  note: Order request handoff is order_request_later; today the receptionist creates
    orders on the doctor's behalf.
- id: branch-administrator
  actor_ref: ACT-003
  name: Branch Administrator
  source: ACM-001
processes:
- id: PRC-ORD-001-01
  name: Create diagnostic order
  actor: receptionist
  trigger: Reception or Admission has gathered patient, doctor and catalog selection
    for an order.
  commands:
  - CreateDiagnosticOrder
  preconditions:
  - Actor holds order.create.
  - Patient and, when applicable, doctor snapshots are resolvable.
  steps:
  - Capture intake channel and optional source reference (appointment, reception visit,
    admission request or quotation).
  - Capture PatientSnapshot and optional DoctorSnapshot.
  - Capture BranchSnapshot.
  - Add order lines from published catalog items as CatalogSnapshots.
  - Persist DiagnosticOrder in draft status.
  - Publish DiagnosticOrderCreated.
  outcome: DiagnosticOrderCreated
  rules:
  - RN-001
  - RN-002
  - RN-005
  - RN-008
  - RN-009
- id: PRC-ORD-001-02
  name: Price diagnostic order
  actor: receptionist
  trigger: Order lines are final and a price snapshot must be captured.
  commands:
  - PriceDiagnosticOrder
  preconditions:
  - Order has at least one order line.
  steps:
  - Resolve the applicable published price list for tenant, laboratory and branch.
  - Capture OrderPricingSnapshot with line amounts and total.
  - Transition order to priced status.
  - Publish OrderPriced.
  outcome: OrderPriced
  rules:
  - RN-003
  - RN-009
- id: PRC-ORD-001-03
  name: Accept diagnostic order
  actor: receptionist
  trigger: Admission confirms clinical notes and consent are complete.
  commands:
  - AcceptDiagnosticOrder
  preconditions:
  - Order is priced.
  - Clinical notes captured when required.
  steps:
  - Attach OrderClinicalNotes when provided.
  - Transition order to accepted status.
  - Publish OrderAccepted.
  outcome: OrderAccepted
  rules:
  - RN-003
  - RN-005
  - RN-008
- id: PRC-ORD-001-04
  name: Cancel diagnostic order
  actor: receptionist
  trigger: The order must be discarded before or during processing.
  commands:
  - CancelDiagnosticOrder
  preconditions:
  - Order is not already completed.
  steps:
  - Validate downstream sample or processing state.
  - Capture cancellation reason and override justification when applicable.
  - Transition order to cancelled status.
  - Publish OrderCancelled.
  outcome: OrderCancelled
  rules:
  - RN-006
  - RN-007
- id: PRC-ORD-001-05
  name: Complete diagnostic order
  actor: branch-administrator
  trigger: All order lines have reached a terminal clinical state in downstream modules.
  commands:
  - CompleteDiagnosticOrder
  preconditions:
  - Order is accepted and all lines are resolved by downstream modules.
  steps:
  - Transition order to completed status.
  - Publish OrderCompleted.
  outcome: OrderCompleted
  rules:
  - RN-006
commands:
- name: CreateDiagnosticOrder
  generatable: false
  custom_reason: Snapshot capture and published-catalog validation.
- name: PriceDiagnosticOrder
  generatable: false
  custom_reason: Price-list resolution and snapshot capture.
- name: AcceptDiagnosticOrder
  generatable: false
  custom_reason: Preconditions on pricing and clinical completeness.
- name: CancelDiagnosticOrder
  generatable: false
  custom_reason: Downstream state check and override justification.
- name: CompleteDiagnosticOrder
  generatable: true
```
