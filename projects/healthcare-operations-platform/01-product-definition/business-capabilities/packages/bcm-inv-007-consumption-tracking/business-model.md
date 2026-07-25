---
id: HOP-BM-BCM-INV-007
format: markdown_structured_payload
type: business-model
name: Consumption Tracking Business Model
version: 0.1.0
status: modeled
---

# Consumption Tracking Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-INV-007
  type: business-model
  name: Consumption Tracking Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-007
  bounded_context: inventory-procurement
  primary_aggregate: InventoryItem (AGG-013, owned by BCM-INV-001)
  model_kind: operational_process_over_shared_aggregate
entities:
- id: ENT-CNS-001
  name: ConsumptionRecord
  is_aggregate_root: false
  described_as: process_record
  owned_by_aggregate: InventoryItem
  description: Append-only consumption record; each row is one test-performance-driven
    stock decrement.
  fields:
  - name: consumptionId
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
  - name: linkedLaboratoryResultId
    type: uuid
    required: false
    description: Read-only reference to AGG-009 LaboratoryResult; never mutated by
      this capability.
  - name: performedBy
    type: UserId
    required: true
  - name: consumedAt
    type: datetime
    required: true
  - name: audit
    type: AuditMetadata
    required: true
value_objects: []
invariants:
- id: INV-CNS-001
  statement: quantity is derived from BCM-INV-002's reagentProfile.consumptionUnitRatio
    and must be strictly greater than zero.
- id: INV-CNS-002
  statement: This capability never mutates AGG-009 LaboratoryResult; linkedLaboratoryResultId
    is a read-only reference captured for traceability only.
- id: INV-CNS-003
  statement: Confirming a ConsumptionRecord is the only trigger allowed to invoke
    ApplyConsumption on InventoryItem.stockSummary and the referenced StockLot.remainingQuantity;
    this capability never mutates any other field.
- id: INV-CNS-004
  statement: A consumption against an expired or disposed StockLot is rejected.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-003 BranchId
  - VO-ID-004 UserId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-INV-001 InventoryItem aggregate (delegated mutation target for stockSummary
    decrease)
  - BCM-INV-002 Reagent Management (read-only consumptionUnitRatio source)
  - BCM-INV-003 Lot Management (read-only StockLot status/quantity reference)
  - BCM-QLT-001 Internal Quality Controls (downstream consumer of the control-material
    lot used)
- aggregate_catalog:
  - AGG-009 LaboratoryResult (read-only reference only; forbidden_mutators already
    exclude this bounded context by construction)
```
