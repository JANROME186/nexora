---
id: HOP-TEST-BCM-INV-007
format: markdown_structured_payload
type: test-model
name: Consumption Tracking Test Model
version: 0.1.0
status: modeled
---

# Consumption Tracking Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-INV-007
  type: test-model
  name: Consumption Tracking Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-007
test_cases:
- id: TST-CNS-007-01
  type: acceptance
  validates_rule: RN-001
  statement: Consumption without an active reagentProfile.consumptionUnitRatio is
    rejected.
  generatable: false
- id: TST-CNS-007-02
  type: architecture
  validates_rule: RN-002
  statement: No code path in this capability writes AGG-009 LaboratoryResult.
  generatable: false
- id: TST-CNS-007-03
  type: architecture
  validates_rule: RN-003
  statement: No code path outside ApplyConsumption decreases InventoryItem.stockSummary
    for consumption purposes.
  generatable: false
- id: TST-CNS-007-04
  type: acceptance
  validates_rule: RN-004
  statement: A consumption against an expired or disposed StockLot is rejected.
  generatable: false
- id: TST-CNS-007-05
  type: contract
  validates_rule: RN-005
  statement: A caller outside the item's tenant/laboratory/branch scope cannot record
    or read a consumption.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-CNS-007-01
  - TST-CNS-007-02
  - TST-CNS-007-03
  - TST-CNS-007-04
```
