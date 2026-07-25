---
id: HOP-OBS-BCM-LAB-001
format: markdown_structured_payload
type: observability-model
name: Diagnostic Order Management Observability Model
version: 0.1.0
status: modeled
---

# Diagnostic Order Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-LAB-001
  type: observability-model
  name: Diagnostic Order Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-001
  depends_on_capability: BCM-PLT-006
logs:
- event: diagnostic_order_created
  level: info
  fields:
  - orderId
  - branchId
  - actorId
  - intakeChannel
- event: order_priced
  level: info
  fields:
  - orderId
  - priceListId
  - totalAmount
  - actorId
- event: order_accepted
  level: info
  fields:
  - orderId
  - actorId
  - branchId
- event: order_cancelled
  level: warn
  fields:
  - orderId
  - actorId
  - reasonCode
- event: order_completed
  level: info
  fields:
  - orderId
  - actorId
- event: order_catalog_item_rejected
  level: warn
  fields:
  - orderId
  - testDefinitionId
  - reasonCode
metrics:
- name: diagnostic_order_created_total
  type: counter
  labels:
  - tenantId
  - branchId
  - intakeChannel
- name: diagnostic_order_accepted_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: diagnostic_order_cancelled_total
  type: counter
  labels:
  - tenantId
  - branchId
  - reasonCode
- name: diagnostic_order_lifecycle_duration_ms
  type: histogram
  labels:
  - tenantId
  - branchId
- name: diagnostic_order_line_count
  type: histogram
  labels:
  - tenantId
traces:
- span: CreateDiagnosticOrder
  child_spans:
  - CapturePatientSnapshot
  - CaptureDoctorSnapshot
  - CaptureBranchSnapshot
  - ValidateCatalogPublication
  - PersistOrder
- span: PriceDiagnosticOrder
  child_spans:
  - ResolvePriceList
  - CapturePricingSnapshot
- span: AcceptDiagnosticOrder
  child_spans:
  - ValidatePricingPresence
  - AttachClinicalNotes
  - PublishOrderAccepted
audit_events:
- DiagnosticOrderCreated
- OrderPriced
- OrderAccepted
- OrderCancelled
- OrderCompleted
alerts:
- name: HighOrderCancellationRate
  condition: diagnostic_order_cancelled_total rate exceeds threshold
  severity: warning
- name: HighCatalogItemRejectionRate
  condition: order_catalog_item_rejected rate exceeds threshold
  severity: warning
```
