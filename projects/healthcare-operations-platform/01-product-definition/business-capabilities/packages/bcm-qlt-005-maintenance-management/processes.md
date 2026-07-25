---
id: HOP-PROC-BCM-QLT-005
format: markdown_structured_payload
type: processes
name: Maintenance Management Processes
version: 0.1.0
status: modeled
---

# Maintenance Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-QLT-005
  type: processes
  name: Maintenance Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-005
actors:
- id: laboratory-technician
  actor_ref: ACT-007
  name: Laboratory Technician
  source: ACM-001
  note: 'ACM-001 does not yet define a dedicated "Equipment Technician" actor; reused
    as the closest existing role, documented as a non-blocking substitution.

    '
processes:
- id: PRC-MNT-005-01
  name: Start maintenance
  actor: laboratory-technician
  trigger: Scheduled preventive maintenance or an unplanned corrective repair begins
    on an equipment-type InventoryItem.
  commands:
  - RecordMaintenance
  preconditions:
  - InventoryItem exists and itemType is equipment.
  - maintenanceType and description are supplied.
  steps:
  - Append a new MaintenanceEvent with startedAt.
  - Publish MaintenanceScheduled for BCM-QLT-004 to transition equipment to out_of_service.
  - Publish MaintenanceRecorded.
  outcome: MaintenanceRecorded
  rules:
  - RN-001
  - RN-002
  - RN-005
- id: PRC-MNT-005-02
  name: Complete maintenance
  actor: laboratory-technician
  trigger: The maintenance work is finished and the equipment is ready for use again.
  commands:
  - CompleteMaintenance
  preconditions:
  - MaintenanceEvent exists without a completedAt.
  steps:
  - Set completedAt and downtimeMinutes.
  - Publish MaintenanceCompleted for BCM-QLT-004 to transition equipment back to available.
  outcome: MaintenanceCompleted
  rules:
  - RN-002
  - RN-004
  - RN-005
commands:
- name: RecordMaintenance
  generatable: false
  custom_reason: Delegated append mutation with cross-capability itemType validation
    and event publication.
- name: CompleteMaintenance
  generatable: false
  custom_reason: Delegated mutation with a completedAt-after-startedAt guard and event
    publication.
```
