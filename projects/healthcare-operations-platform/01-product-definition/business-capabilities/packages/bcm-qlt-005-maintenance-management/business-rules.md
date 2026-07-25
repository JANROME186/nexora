---
id: HOP-BR-BCM-QLT-005
format: markdown_structured_payload
type: business-rules
name: Maintenance Management Business Rules
version: 0.1.0
status: modeled
---

# Maintenance Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-QLT-005
  type: business-rules
  name: Maintenance Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-005
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A MaintenanceEvent may only target an InventoryItem whose itemType is
    equipment.
  applies_to: MaintenanceEvent
  enforcement_point: command:RecordMaintenance
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires reading InventoryItem.itemType owned by a sibling capability.
  test_refs:
  - TST-MNT-005-01
- id: RN-002
  statement: Starting a maintenance event publishes MaintenanceScheduled and completing
    it publishes MaintenanceCompleted; this capability never writes InventoryItem.equipmentProfile
    directly.
  applies_to: MaintenanceEvent
  enforcement_point: command:RecordMaintenance, command:CompleteMaintenance
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-capability event-driven coordination boundary.
  test_refs:
  - TST-MNT-005-02
- id: RN-003
  statement: Only this capability may append to InventoryItem.maintenanceRecord; this
    capability never mutates any other InventoryItem field.
  applies_to: InventoryItem
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Enforces the delegated field-mutation boundary shared across COM-MOD-010.
  test_refs:
  - TST-MNT-005-03
- id: RN-004
  statement: completedAt, when present, must be after startedAt.
  applies_to: MaintenanceEvent
  enforcement_point: command:CompleteMaintenance
  severity: medium
  audit_required: true
  generatable: true
  test_refs:
  - TST-MNT-005-04
- id: RN-005
  statement: Maintenance management commands must execute within the actor's tenant,
    laboratory and branch scope.
  applies_to: MaintenanceEvent
  enforcement_point: authorization:quality.maintenance.manage, authorization:quality.maintenance.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-MNT-005-05
enforcement_summary:
  generatable_rules:
  - RN-004
  - RN-005
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
```
