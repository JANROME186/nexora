---
id: HOP-PROC-BCM-QLT-004
format: markdown_structured_payload
type: processes
name: Equipment Management Processes
version: 0.1.0
status: modeled
---

# Equipment Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-QLT-004
  type: processes
  name: Equipment Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-004
actors:
- id: branch-administrator
  actor_ref: ACT-003
  name: Branch Administrator
  source: ACM-001
  note: 'ACM-001 does not yet define a dedicated "Equipment Technician" actor; reused
    as the closest existing role with asset-management authority, documented as a
    non-blocking substitution.

    '
processes:
- id: PRC-EQP-004-01
  name: Register equipment profile
  actor: branch-administrator
  trigger: A new equipment-type InventoryItem needs asset profile detail.
  commands:
  - SetEquipmentProfile
  preconditions:
  - InventoryItem exists, is active and itemType is equipment.
  - Actor holds quality.equipment.manage scope.
  steps:
  - Set assetTag, serialNumber, manufacturer, model, installedAt, location.
  - Initialize availabilityStatus to available.
  - Publish EquipmentProfileSet.
  outcome: EquipmentProfileSet
  rules:
  - RN-001
  - RN-005
- id: PRC-EQP-004-02
  name: Change equipment availability
  actor: branch-administrator
  trigger: Manual status change or a CalibrationFailed/MaintenanceScheduled/MaintenanceCompleted
    event.
  commands:
  - ChangeEquipmentAvailability
  preconditions:
  - equipmentProfile exists and current availabilityStatus is not retired.
  steps:
  - Transition availabilityStatus per the manual request or the consumed event.
  - Publish EquipmentAvailabilityChanged.
  outcome: EquipmentAvailabilityChanged
  rules:
  - RN-002
  - RN-004
  - RN-005
commands:
- name: SetEquipmentProfile
  generatable: false
  custom_reason: Delegated single-field mutation with cross-capability itemType validation.
- name: ChangeEquipmentAvailability
  generatable: false
  custom_reason: Reacts to cross-capability events in addition to manual requests,
    with a terminal-state guard.
```
