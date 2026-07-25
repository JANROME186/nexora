---
id: HOP-TEST-BCM-QLT-003
format: markdown_structured_payload
type: test-model
name: Calibration Management Test Model
version: 0.1.0
status: modeled
---

# Calibration Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-QLT-003
  type: test-model
  name: Calibration Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-003
test_cases:
- id: TST-CAL-003-01
  type: acceptance
  validates_rule: RN-001
  statement: Recording a calibration against an InventoryItem with itemType reagent
    is rejected.
  generatable: false
- id: TST-CAL-003-02
  type: acceptance
  validates_rule: RN-002
  statement: A fail result publishes CalibrationFailed without this capability writing
    InventoryItem.equipmentProfile.
  generatable: false
- id: TST-CAL-003-03
  type: architecture
  validates_rule: RN-003
  statement: No code path outside this capability appends to InventoryItem.calibrationRecord.
  generatable: false
- id: TST-CAL-003-04
  type: contract
  validates_rule: RN-004
  statement: Recording a calibration with nextDueDate on or before performedAt is
    rejected.
  generatable: true
- id: TST-CAL-003-05
  type: contract
  validates_rule: RN-005
  statement: A caller outside the item's tenant/laboratory/branch scope cannot record
    or read a calibration event.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-CAL-003-01
  - TST-CAL-003-02
  - TST-CAL-003-03
```
