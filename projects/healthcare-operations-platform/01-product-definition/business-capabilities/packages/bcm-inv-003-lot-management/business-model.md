---
id: HOP-BM-BCM-INV-003
format: markdown_structured_payload
type: business-model
name: Lot Management Business Model
version: 0.1.0
status: modeled
---

# Lot Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-INV-003
  type: business-model
  name: Lot Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-003
  bounded_context: inventory-procurement
  primary_aggregate: InventoryItem (AGG-013, owned by BCM-INV-001)
  model_kind: operational_process_over_shared_aggregate
entities:
- id: ENT-LOT-001
  name: StockLot
  is_aggregate_root: false
  owned_by_aggregate: InventoryItem
  description: 'A physical batch of an InventoryItem, owned and created by this capability.
    Quantity fields are updated only through the Apply* commands of BCM-INV-005/006/007/008/009,
    never written directly by this capability once received.

    '
  fields:
  - name: stockLotId
    type: uuid
    required: true
    identifier: true
  - name: inventoryItemId
    type: InventoryItemId
    required: true
  - name: tenantId
    type: TenantId
    required: true
  - name: branchId
    type: BranchId
    required: true
  - name: lotNumber
    type: string
    required: true
  - name: supplierSnapshot
    type: SupplierSnapshot
    required: false
    description: Captured supplier identity at first receipt; read-only external reference,
      never a live lookup.
  - name: manufacturedAt
    type: date
    required: false
  - name: expirationDate
    type: date
    required: true
  - name: storageCondition
    type: enum
    values:
    - refrigerated_2_8c
    - frozen_minus_20c
    - frozen_minus_70c
    - room_temperature
    - controlled_room_temperature
    - protect_from_light
    - other
    required: true
  - name: receivedQuantity
    type: decimal
    required: true
  - name: remainingQuantity
    type: decimal
    required: true
    description: Mutated only by BCM-INV-005/006/007/008/009 Apply* commands.
  - name: status
    type: enum
    values:
    - active
    - quarantined
    - expired
    - depleted
    - disposed
    required: true
  - name: audit
    type: AuditMetadata
    required: true
value_objects:
- id: VO-LOT-001
  name: SupplierSnapshot
  description: 'Minimal immutable supplier identity captured at receipt time. AGG-014
    Supplier is owned by the not-yet-modeled BCM-PER-006 Supplier Management; this
    value object is an external, ID-only reference with a display-name snapshot, never
    a live cross-context lookup.

    '
  fields:
  - name: supplierId
    type: uuid
    required: false
  - name: supplierNameSnapshot
    type: string
    required: false
  - name: capturedAt
    type: datetime
    required: true
invariants:
- id: INV-LOT-001
  statement: A StockLot must always reference a valid InventoryItem and carry a non-past
    manufacturedAt-to-expirationDate range where both are present.
- id: INV-LOT-002
  statement: remainingQuantity must never exceed receivedQuantity and must never go
    negative.
- id: INV-LOT-003
  statement: Only BCM-INV-003 may write lotNumber, expirationDate, storageCondition
    and status; only BCM-INV-005/006/007/008/009 may write receivedQuantity/remainingQuantity
    through their own Apply* commands.
- id: INV-LOT-004
  statement: A StockLot past its expirationDate must automatically transition to expired
    and be excluded from new consumption or exit selection, without requiring manual
    intervention.
- id: INV-LOT-005
  statement: A disposed StockLot is terminal; it can never transition back to active
    or quarantined.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-003 BranchId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-INV-001 InventoryItem aggregate (read-only owner reference)
  - BCM-INV-005 Stock Entries (creates the first StockLot on initial receipt via cross-capability
    coordination)
  - BCM-PER-006 Supplier Management (not yet modeled; AGG-014 Supplier remains an
    external reference only)
```
