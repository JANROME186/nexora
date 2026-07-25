---
id: HOP-BM-BCM-QLT-004
format: markdown_structured_payload
type: business-model
name: Equipment Management Business Model
version: 0.1.0
status: modeled
---

# Equipment Management Business Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BM-BCM-QLT-004
  type: business-model
  name: Equipment Management Business Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-004
  bounded_context: inventory-procurement
  primary_aggregate: InventoryItem (AGG-013, owned by BCM-INV-001)
  model_kind: operational_process_over_shared_aggregate
entities:
- id: ENT-EQP-001
  name: EquipmentAvailabilityChange
  is_aggregate_root: false
  described_as: process_record
  owned_by_aggregate: InventoryItem
  description: Append-only record of every equipmentProfile.availabilityStatus transition,
    manual or event-driven.
  fields:
  - name: changeId
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
  - name: previousStatus
    type: enum
    values:
    - available
    - in_use
    - out_of_service
    - retired
    required: true
  - name: newStatus
    type: enum
    values:
    - available
    - in_use
    - out_of_service
    - retired
    required: true
  - name: reasonCode
    type: enum
    values:
    - routine
    - calibration_failed
    - maintenance_scheduled
    - maintenance_completed
    - decommissioned
    - other
    required: true
  - name: changedBy
    type: UserId
    required: false
    description: Null when the transition is event-driven (calibration/maintenance
      signal) rather than manual.
  - name: changedAt
    type: datetime
    required: true
  - name: audit
    type: AuditMetadata
    required: true
value_objects: []
invariants:
- id: INV-EQP-001
  statement: equipmentProfile may only be set (SetEquipmentProfile) on an InventoryItem
    whose itemType is equipment; this capability never creates the InventoryItem itself.
- id: INV-EQP-002
  statement: A retired equipmentProfile.availabilityStatus is terminal; it can never
    transition back to available, in_use or out_of_service.
- id: INV-EQP-003
  statement: Confirming an EquipmentAvailabilityChange or SetEquipmentProfile is the
    only trigger allowed to mutate InventoryItem.equipmentProfile; this capability
    never mutates any other InventoryItem field.
- id: INV-EQP-004
  statement: This capability reacts to CalibrationFailed (BCM-QLT-003) and MaintenanceScheduled/MaintenanceCompleted
    (BCM-QLT-005) events to transition availabilityStatus; neither sibling capability
    writes equipmentProfile directly.
external_references:
- shared_kernel:
  - VO-ID-001 TenantId
  - VO-ID-003 BranchId
  - VO-ID-004 UserId
  - VO-007 AuditMetadata
- capabilities:
  - BCM-INV-001 InventoryItem aggregate (delegated mutation target for equipmentProfile)
  - BCM-QLT-003 Calibration Management (event producer: CalibrationFailed)
  - BCM-QLT-005 Maintenance Management (event producer: MaintenanceScheduled/MaintenanceCompleted)
  - BCM-LAB-006 Laboratory Processing (downstream consumer of equipment availability
    for processing readiness)
```
