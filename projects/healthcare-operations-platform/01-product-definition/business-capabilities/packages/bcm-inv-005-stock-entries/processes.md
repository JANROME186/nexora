---
id: HOP-PROC-BCM-INV-005
format: markdown_structured_payload
type: processes
name: Stock Entries Processes
version: 0.1.0
status: modeled
---

# Stock Entries Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-INV-005
  type: processes
  name: Stock Entries Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-005
actors:
- id: laboratory-technician
  actor_ref: ACT-007
  name: Laboratory Technician
  source: ACM-001
  note: 'ACM-001 does not yet define a dedicated "Inventory Clerk" actor; reused as
    the closest existing role, documented as a non-blocking substitution.

    '
processes:
- id: PRC-SEN-005-01
  name: Record stock receipt
  actor: laboratory-technician
  trigger: Goods physically arrive at the branch, either from an approved purchase
    order or a manual entry.
  commands:
  - ApplyStockReceipt
  preconditions:
  - InventoryItem exists and is not discontinued.
  - quantity is strictly greater than zero.
  - If sourceType is purchase_order_receipt, sourcePurchaseOrderLineId references
    an approved or partially_received PurchaseOrder line.
  steps:
  - If no matching StockLot exists yet, invoke BCM-INV-003's RegisterStockLot.
  - Increase StockLot.remainingQuantity and InventoryItem.stockSummary.onHandQuantity.
  - If originated from a purchase order, notify BCM-INV-004 of the received quantity.
  - Publish StockReceived.
  outcome: StockReceived
  rules:
  - RN-001
  - RN-002
  - RN-004
  - RN-005
commands:
- name: ApplyStockReceipt
  generatable: false
  custom_reason: Delegated multi-field mutation across InventoryItem.stockSummary
    and StockLot.remainingQuantity with cross-capability purchase-order validation.
```
