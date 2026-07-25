---
id: HOP-BM-BCM-QLT-003
format: markdown_structured_payload
type: business-model
name: Calibration Management Business Model
version: 0.1.0
status: modeled
---

# Calibration Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-QLT-003
  type: business-model
  name: Calibration Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-003
  bounded_context: inventory-procurement
  primary_aggregate: InventoryItem (AGG-013, owned by BCM-INV-001)
  model_kind: operational_process_over_shared_aggregate
entities:
- id: ENT-CAL-001
  name: CalibrationEvent
  is_aggregate_root: false
  described_as: process_record
  owned_by_aggregate: InventoryItem
  description: Append-only calibration record for an equipment-type InventoryItem.
  fields:
  - name: calibrationEventId
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
  - name: calibrationStandardRef
    type: string
    required: true
  - name: performedBy
    type: UserId
    required: true
  - name: performedAt
    type: datetime
    required: true
  - name: result
    type: enum
    values:
    - pass
    - fail
    - adjusted
    required: true
  - name: nextDueDate
    type: date
    required: true
  - name: certificateReference
    type: string
    required: false
    description: Optional reference to a stored document (BCM-PLT-008 Document Management,
      not part of COM-MOD-010).
  - name: audit
    type: AuditMetadata
    required: true
value_objects: []
invariants:
- id: INV-CAL-001
  statement: A CalibrationEvent may only target an InventoryItem whose itemType is
    equipment.
- id: INV-CAL-002
  statement: A fail result must publish CalibrationFailed; this capability never writes
    InventoryItem.equipmentProfile itself, only calibrationRecord.
- id: INV-CAL-003
  statement: Confirming a CalibrationEvent is the only trigger allowed to append to
    InventoryItem.calibrationRecord; this capability never mutates any other InventoryItem
    field.
- id: INV-CAL-004
  statement: nextDueDate must be strictly after performedAt.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-003 BranchId
  - VO-ID-004 UserId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-INV-001 InventoryItem aggregate (delegated mutation target for calibrationRecord)
  - BCM-QLT-004 Equipment Management (consumes CalibrationFailed to transition availabilityStatus)
  - BCM-LAB-006 Laboratory Processing (downstream consumer of calibration currency
    for processing readiness)
```
