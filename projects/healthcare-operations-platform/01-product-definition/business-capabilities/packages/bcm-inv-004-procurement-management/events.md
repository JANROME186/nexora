---
id: HOP-EVT-BCM-INV-004
format: markdown_structured_payload
type: events
name: Procurement Management Events
version: 0.1.0
status: modeled
---

# Procurement Management Events

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-EVT-BCM-INV-004
  type: events
  name: Procurement Management Events
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-004
domain_events:
- name: PurchaseOrderSubmitted
  description: A purchase order was submitted for approval.
  payload:
  - purchaseOrderId
  - supplierReference
  audit: true
- name: PurchaseOrderApproved
  description: A purchase order was approved for dispatch to the supplier.
  payload:
  - purchaseOrderId
  audit: true
- name: PurchaseOrderCancelled
  description: A purchase order was cancelled.
  payload:
  - purchaseOrderId
  audit: true
- name: PurchaseOrderLineReceived
  description: A purchase order line's goods were received, delegating stock receipt
    to BCM-INV-005.
  payload:
  - purchaseOrderId
  - lineId
  - receivedQuantity
  audit: true
integration_events:
  published:
  - name: PurchaseOrderLineReceived
    description: Triggers BCM-INV-005 to record a stock receipt against the referenced
      InventoryItem.
    consumers:
    - inventory-procurement
  consumed: []
published_language: []
```
