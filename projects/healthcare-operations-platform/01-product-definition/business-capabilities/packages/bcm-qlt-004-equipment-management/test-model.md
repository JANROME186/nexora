---
id: HOP-TEST-BCM-QLT-004
format: markdown_structured_payload
type: test-model
name: Equipment Management Test Model
version: 0.1.0
status: modeled
---

# Equipment Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-QLT-004
  type: test-model
  name: Equipment Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-QLT-004
test_cases:
- id: TST-EQP-004-01
  type: acceptance
  validates_rule: RN-001
  statement: Setting an equipment profile on an InventoryItem with itemType reagent
    is rejected.
  generatable: false
- id: TST-EQP-004-02
  type: contract
  validates_rule: RN-002
  statement: A retired equipment item rejects a transition back to available, in_use
    or out_of_service.
  generatable: true
- id: TST-EQP-004-03
  type: architecture
  validates_rule: RN-003
  statement: No code path outside this capability writes InventoryItem.equipmentProfile.
  generatable: false
- id: TST-EQP-004-04
  type: acceptance
  validates_rule: RN-004
  statement: A CalibrationFailed event automatically transitions availabilityStatus
    to out_of_service.
  generatable: false
- id: TST-EQP-004-05
  type: contract
  validates_rule: RN-005
  statement: A caller outside the item's tenant/laboratory/branch scope cannot manage
    or read its equipment profile.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-EQP-004-01
  - TST-EQP-004-03
  - TST-EQP-004-04
```
