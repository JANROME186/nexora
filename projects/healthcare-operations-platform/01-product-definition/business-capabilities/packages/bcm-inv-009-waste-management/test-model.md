---
id: HOP-TEST-BCM-INV-009
format: markdown_structured_payload
type: test-model
name: Waste Management Test Model
version: 0.1.0
status: modeled
---

# Waste Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-INV-009
  type: test-model
  name: Waste Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-009
test_cases:
- id: TST-WST-009-01
  type: acceptance
  validates_rule: RN-001
  statement: A disposal requesting more than the StockLot's remainingQuantity is rejected.
  generatable: false
- id: TST-WST-009-02
  type: contract
  validates_rule: RN-002
  statement: A disposal without a wasteReasonCode is rejected.
  generatable: true
- id: TST-WST-009-03
  type: architecture
  validates_rule: RN-003
  statement: No code path outside ApplyWasteDisposal decreases InventoryItem.stockSummary
    for disposal purposes.
  generatable: false
- id: TST-WST-009-04
  type: acceptance
  validates_rule: RN-004
  statement: A disposal that exhausts a StockLot's remainingQuantity transitions its
    status to disposed in the same transaction.
  generatable: false
- id: TST-WST-009-05
  type: contract
  validates_rule: RN-005
  statement: A caller outside the item's tenant/laboratory/branch scope cannot record
    or read a waste disposal.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-WST-009-01
  - TST-WST-009-03
  - TST-WST-009-04
```
