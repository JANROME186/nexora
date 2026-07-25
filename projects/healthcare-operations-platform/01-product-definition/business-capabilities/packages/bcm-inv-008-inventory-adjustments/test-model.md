---
id: HOP-TEST-BCM-INV-008
format: markdown_structured_payload
type: test-model
name: Inventory Adjustments Test Model
version: 0.1.0
status: modeled
---

# Inventory Adjustments Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-INV-008
  type: test-model
  name: Inventory Adjustments Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-INV-008
test_cases:
- id: TST-ADJ-008-01
  type: acceptance
  validates_rule: RN-001
  statement: An adjustment with a zero delta, or a negative delta exceeding the lot's
    remainingQuantity, is rejected.
  generatable: false
- id: TST-ADJ-008-02
  type: contract
  validates_rule: RN-002
  statement: An adjustment where requestedBy equals approvedBy is rejected.
  generatable: true
- id: TST-ADJ-008-03
  type: contract
  validates_rule: RN-003
  statement: An adjustment without a reasonCode is rejected.
  generatable: true
- id: TST-ADJ-008-04
  type: architecture
  validates_rule: RN-004
  statement: No code path outside ApplyAdjustment corrects InventoryItem.stockSummary
    or StockLot.remainingQuantity.
  generatable: false
- id: TST-ADJ-008-05
  type: contract
  validates_rule: RN-005
  statement: A caller outside the item's tenant/laboratory/branch scope cannot request,
    approve or read an adjustment.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-ADJ-008-01
  - TST-ADJ-008-04
```
