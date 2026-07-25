---
id: HOP-BM-BCM-INV-008
format: markdown_structured_payload
type: business-model
name: Inventory Adjustments Business Model
version: 0.1.0
status: modeled
---

# Inventory Adjustments Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-INV-008
  type: business-model
  name: Inventory Adjustments Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-008
  bounded_context: inventory-procurement
  primary_aggregate: InventoryItem (AGG-013, owned by BCM-INV-001)
  model_kind: operational_process_over_shared_aggregate
entities:
- id: ENT-ADJ-001
  name: AdjustmentRecord
  is_aggregate_root: false
  described_as: process_record
  owned_by_aggregate: InventoryItem
  description: Append-only correction record; each row is one signed stock quantity
    adjustment.
  fields:
  - name: adjustmentId
    type: uuid
    required: true
    identifier: true
  - name: inventoryItemId
    type: InventoryItemId
    required: true
  - name: stockLotId
    type: uuid
    required: true
  - name: tenantId
    type: TenantId
    required: true
  - name: branchId
    type: BranchId
    required: true
  - name: quantityDelta
    type: decimal
    required: true
    description: Positive or negative correction amount; zero is rejected.
  - name: reasonCode
    type: enum
    values:
    - physical_count_variance
    - data_entry_correction
    - system_migration_correction
    - other
    required: true
  - name: notes
    type: string
    required: false
  - name: requestedBy
    type: UserId
    required: true
  - name: approvedBy
    type: UserId
    required: true
  - name: adjustedAt
    type: datetime
    required: true
  - name: audit
    type: AuditMetadata
    required: true
value_objects: []
invariants:
- id: INV-ADJ-001
  statement: quantityDelta must not be zero and must never drive StockLot.remainingQuantity
    or InventoryItem.stockSummary.onHandQuantity negative.
- id: INV-ADJ-002
  statement: Every AdjustmentRecord must carry both a requestedBy and an approvedBy
    actor, which must be different users.
- id: INV-ADJ-003
  statement: Confirming an AdjustmentRecord is the only trigger allowed to invoke
    ApplyAdjustment on InventoryItem.stockSummary and the referenced StockLot.remainingQuantity;
    this capability never mutates any other field.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-003 BranchId
  - VO-ID-004 UserId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-INV-001 InventoryItem aggregate (delegated mutation target for stockSummary
    correction)
  - BCM-INV-003 Lot Management (read-only StockLot status/quantity reference)
  - BCM-PLT-007 Audit and Compliance (mandatory audit sink for every adjustment)
```
