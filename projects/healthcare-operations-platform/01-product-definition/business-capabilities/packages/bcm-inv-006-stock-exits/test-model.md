---
id: HOP-TEST-BCM-INV-006
format: markdown_structured_payload
type: test-model
name: Stock Exits Test Model
version: 0.1.0
status: modeled
---

# Stock Exits Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-INV-006
  type: test-model
  name: Stock Exits Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-006
test_cases:
- id: TST-SXT-006-01
  type: acceptance
  validates_rule: RN-001
  statement: An exit requesting more than the StockLot's remainingQuantity is rejected.
  generatable: false
- id: TST-SXT-006-02
  type: contract
  validates_rule: RN-002
  statement: A branch_transfer exit without destinationBranchId is rejected.
  generatable: true
- id: TST-SXT-006-03
  type: architecture
  validates_rule: RN-003
  statement: No code path outside ApplyStockExit decreases InventoryItem.stockSummary
    for exit purposes.
  generatable: false
- id: TST-SXT-006-04
  type: acceptance
  validates_rule: RN-004
  statement: An exit against an expired or disposed StockLot is rejected.
  generatable: false
- id: TST-SXT-006-05
  type: contract
  validates_rule: RN-005
  statement: A caller outside the item's tenant/laboratory/branch scope cannot record
    or read a stock exit.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-SXT-006-01
  - TST-SXT-006-03
  - TST-SXT-006-04
```
