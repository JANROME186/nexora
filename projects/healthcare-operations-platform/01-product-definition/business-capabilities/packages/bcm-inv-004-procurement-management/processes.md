---
id: HOP-PROC-BCM-INV-004
format: markdown_structured_payload
type: processes
name: Procurement Management Processes
version: 0.1.0
status: modeled
---

# Procurement Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-INV-004
  type: processes
  name: Procurement Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-004
actors:
- id: branch-administrator
  actor_ref: ACT-003
  name: Branch Administrator
  source: ACM-001
  note: 'ACM-001 does not yet define a dedicated "Procurement Officer" actor; this
    capability reuses Branch Administrator, the closest existing role with purchasing
    authority, documented as a non-blocking substitution.

    '
- id: tenant-administrator
  actor_ref: ACT-002
  name: Tenant Administrator
  source: ACM-001
processes:
- id: PRC-PUR-004-01
  name: Create and submit purchase order
  actor: branch-administrator
  trigger: Stock levels or a purchasing plan require replenishment from a supplier.
  commands:
  - CreatePurchaseOrder
  - SubmitPurchaseOrder
  preconditions:
  - Actor holds inventory.procurement.manage scope.
  - Every line's inventoryItemId references an existing, non-discontinued InventoryItem.
  steps:
  - Create PurchaseOrder in draft status with a captured SupplierSnapshot.
  - Validate lines against RN-001 and RN-004.
  - Transition to submitted.
  - Publish PurchaseOrderSubmitted.
  outcome: PurchaseOrderSubmitted
  rules:
  - RN-001
  - RN-004
  - RN-005
- id: PRC-PUR-004-02
  name: Approve or cancel purchase order
  actor: tenant-administrator
  trigger: A submitted purchase order needs authorization before dispatch to the supplier.
  commands:
  - ApprovePurchaseOrder
  - CancelPurchaseOrder
  preconditions:
  - PurchaseOrder status is submitted.
  steps:
  - Transition to approved or cancelled.
  - Publish PurchaseOrderApproved or PurchaseOrderCancelled.
  outcome: PurchaseOrderApproved
  rules:
  - RN-003
  - RN-005
- id: PRC-PUR-004-03
  name: Receive purchase order line
  actor: branch-administrator
  trigger: Goods for an approved purchase order line arrive.
  commands:
  - ReceivePurchaseOrderLine
  preconditions:
  - PurchaseOrder status is approved or partially_received.
  steps:
  - Delegate the physical receipt to BCM-INV-005's ApplyStockReceipt command.
  - Update the line's receivedQuantity rollup from the receipt confirmation.
  - Transition status to partially_received or received.
  - Publish PurchaseOrderLineReceived.
  outcome: PurchaseOrderLineReceived
  rules:
  - RN-002
  - RN-003
commands:
- name: CreatePurchaseOrder
  generatable: true
- name: SubmitPurchaseOrder
  generatable: false
  custom_reason: Cross-capability InventoryItem status validation (RN-001).
- name: ApprovePurchaseOrder
  generatable: true
- name: CancelPurchaseOrder
  generatable: true
- name: ReceivePurchaseOrderLine
  generatable: false
  custom_reason: Delegates to BCM-INV-005's own receipt command rather than mutating
    stock directly.
```
