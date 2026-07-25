---
id: HOP-UI-BCM-LAB-001
format: markdown_structured_payload
type: ui-model
name: Diagnostic Order Management UI Model
version: 0.1.0
status: modeled
---

# Diagnostic Order Management Ui Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-UI-BCM-LAB-001
  type: ui-model
  name: Diagnostic Order Management UI Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-001
  target_surface: employee_portal
surfaces:
  employee_portal:
    status: required
    generatable: true
  patient_portal:
    status: not_required_yet
    generatable: not_applicable
  doctor_portal:
    status: order_request_later
    generatable: deferred
screens:
- id: SCR-ORD-001-01
  name: Order Creation
  route: /orders/new
  type: wizard
  scopes:
  - order.create
  components:
  - PatientSnapshotPicker
  - DoctorSnapshotPicker
  - CatalogItemSelector
  - OrderLineList
  - PricingSummaryPanel
  generatable: false
  custom_reason: Combines snapshot capture, published-catalog validation and pricing
    preview.
- id: SCR-ORD-001-02
  name: Order List
  route: /orders
  type: list
  scopes:
  - order.read
  components:
  - DataTable
  - StatusFilter
  - BranchFilter
  - IntakeChannelFilter
  generatable: true
- id: SCR-ORD-001-03
  name: Order Detail
  route: /orders/{orderId}
  type: detail
  scopes:
  - order.read
  components:
  - OrderSummary
  - SnapshotDetailPanel
  - OrderLineTable
  - PricingSnapshotPanel
  - OrderStatusTimeline
  - AuditTraceLink
  generatable: true
- id: SCR-ORD-001-04
  name: Order Actions
  route: /orders/{orderId}/actions
  type: action_panel
  scopes:
  - order.manage
  components:
  - PriceOrderButton
  - AcceptOrderButton
  - CancelOrderDialog
  - CompleteOrderButton
  generatable: false
  custom_reason: Enforces preconditions per action (pricing, clinical completeness,
    downstream state check).
states:
- draft
- priced
- accepted
- in_progress
- cancelled
- completed
localization:
  languages:
  - en
  - es
  default: es
```
