---
id: HOP-BR-BCM-QLT-003
format: markdown_structured_payload
type: business-rules
name: Calibration Management Business Rules
version: 0.1.0
status: modeled
---

# Calibration Management Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-BR-BCM-QLT-003
  type: business-rules
  name: Calibration Management Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-003
  rule_id_pattern: RN-###
rules:
- id: RN-001
  statement: A CalibrationEvent may only target an InventoryItem whose itemType is
    equipment.
  applies_to: CalibrationEvent
  enforcement_point: command:RecordCalibration
  severity: high
  audit_required: true
  generatable: false
  custom_reason: Requires reading InventoryItem.itemType owned by a sibling capability.
  test_refs:
  - TST-CAL-003-01
- id: RN-002
  statement: A fail result must publish CalibrationFailed for BCM-QLT-004 to consume;
    this capability never writes InventoryItem.equipmentProfile directly.
  applies_to: CalibrationEvent
  enforcement_point: command:RecordCalibration
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Cross-capability event-driven coordination boundary.
  test_refs:
  - TST-CAL-003-02
- id: RN-003
  statement: Only this capability may append to InventoryItem.calibrationRecord; this
    capability never mutates any other InventoryItem field.
  applies_to: InventoryItem
  enforcement_point: architecture_boundary
  severity: critical
  audit_required: true
  generatable: false
  custom_reason: Enforces the delegated field-mutation boundary shared across COM-MOD-010.
  test_refs:
  - TST-CAL-003-03
- id: RN-004
  statement: nextDueDate must be strictly after performedAt.
  applies_to: CalibrationEvent
  enforcement_point: command:RecordCalibration
  severity: medium
  audit_required: true
  generatable: true
  test_refs:
  - TST-CAL-003-04
- id: RN-005
  statement: Calibration management commands must execute within the actor's tenant,
    laboratory and branch scope.
  applies_to: CalibrationEvent
  enforcement_point: authorization:quality.calibration.manage, authorization:quality.calibration.read
  severity: critical
  audit_required: true
  generatable: true
  test_refs:
  - TST-CAL-003-05
enforcement_summary:
  generatable_rules:
  - RN-004
  - RN-005
  custom_implementation_rules:
  - RN-001
  - RN-002
  - RN-003
```
