---
id: HOP-BM-BCM-INV-009
format: markdown_structured_payload
type: business-model
name: Waste Management Business Model
version: 0.1.0
status: modeled
---

# Waste Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-INV-009
  type: business-model
  name: Waste Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-009
  bounded_context: inventory-procurement
  primary_aggregate: InventoryItem (AGG-013, owned by BCM-INV-001)
  model_kind: operational_process_over_shared_aggregate
entities:
- id: ENT-WST-001
  name: WasteRecord
  is_aggregate_root: false
  described_as: process_record
  owned_by_aggregate: InventoryItem
  description: Append-only disposal record; each row is one waste/disposal transaction
    against a StockLot.
  fields:
  - name: wasteRecordId
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
  - name: quantity
    type: decimal
    required: true
  - name: wasteReasonCode
    type: enum
    values:
    - expired
    - damaged
    - contaminated
    - recalled
    - other
    required: true
  - name: notes
    type: string
    required: false
  - name: disposedBy
    type: UserId
    required: true
  - name: disposedAt
    type: datetime
    required: true
  - name: audit
    type: AuditMetadata
    required: true
value_objects: []
invariants:
- id: INV-WST-001
  statement: quantity must be strictly greater than zero and must not exceed the referenced
    StockLot's remainingQuantity.
- id: INV-WST-002
  statement: wasteReasonCode is mandatory for every disposal.
- id: INV-WST-003
  statement: Confirming a WasteRecord is the only trigger allowed to invoke ApplyWasteDisposal
    on InventoryItem.stockSummary and the referenced StockLot.remainingQuantity/status;
    this capability never mutates any other field.
- id: INV-WST-004
  statement: When a disposal drives StockLot.remainingQuantity to zero, the lot's
    status must transition to disposed as part of the same transaction.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-003 BranchId
  - VO-ID-004 UserId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-INV-001 InventoryItem aggregate (delegated mutation target for stockSummary
    disposal decrease)
  - BCM-INV-003 Lot Management (read-only StockLot status/quantity reference; status
    transition to disposed delegated here)
  - BCM-QLT-007 Audit Management (future downstream consumer for waste-trend audits;
    not part of COM-MOD-010's roadmap group)
```
