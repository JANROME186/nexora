---
id: HOP-OBS-BCM-INV-004
format: markdown_structured_payload
type: observability-model
name: Procurement Management Observability Model
version: 0.1.0
status: modeled
---

# Procurement Management Observability Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-OBS-BCM-INV-004
  type: observability-model
  name: Procurement Management Observability Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-004
  depends_on_capability: BCM-PLT-006
logs:
- event: purchase_order_submitted
  level: info
  fields:
  - purchaseOrderId
  correlation_id: purchaseOrderId
- event: purchase_order_line_received
  level: info
  fields:
  - purchaseOrderId
  - lineId
  - receivedQuantity
  correlation_id: purchaseOrderId
metrics:
- name: purchase_orders_submitted_total
  type: counter
  labels:
  - tenantId
  - branchId
- name: purchase_order_cycle_time_ms
  type: histogram
  labels:
  - tenantId
  - branchId
traces:
- span: SubmitPurchaseOrder
  child_spans:
  - ValidateLineItemsAgainstInventoryItem
- span: ReceivePurchaseOrderLine
  child_spans:
  - DelegateToStockEntriesApplyStockReceipt
audit_events:
- PurchaseOrderApproved
- PurchaseOrderCancelled
- PurchaseOrderLineReceived
alerts:
- name: PurchaseOrderStuckInSubmitted
  condition: PurchaseOrderSubmitted without an approval or cancellation within a configured
    window
  severity: medium
```
