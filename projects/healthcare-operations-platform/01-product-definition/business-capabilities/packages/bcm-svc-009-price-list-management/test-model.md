---
id: HOP-TEST-BCM-SVC-009
format: markdown_structured_payload
type: test-model
name: Price List Management Test Model
version: 0.1.0
status: modeled
---

# Price List Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-SVC-009
  type: test-model
  name: Price List Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-009
test_cases:
- id: TST-SVC-009-01
  type: unit
  validates_rule: RN-001
  statement: Reject duplicate price list code within a laboratory.
  generatable: true
- id: TST-SVC-009-02
  type: unit
  validates_rule: RN-002
  statement: Reject negative price or currency mismatch on a price entry.
  generatable: true
- id: TST-SVC-009-03
  type: acceptance
  validates_rule: RN-003
  statement: Publishing fails when a referenced catalog item is not published.
  generatable: false
- id: TST-SVC-009-04
  type: regression
  validates_rule: RN-004
  statement: Editing a published price list creates a new effective-dated version
    and preserves prior versions.
  generatable: false
- id: TST-SVC-009-05
  type: acceptance
  validates_rule: RN-005
  statement: Publishing fails when effective-dated price lists overlap for the same
    scope and currency.
  generatable: false
- id: TST-SVC-009-06
  type: acceptance
  validates_rule: RN-006
  statement: Effective price resolution returns the version active at the sale date.
  generatable: false
- id: TST-SVC-009-07
  type: contract
  validates_rule: RN-007
  statement: Unauthorized users cannot create, update or publish price lists.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- regression_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-SVC-009-03
  - TST-SVC-009-04
  - TST-SVC-009-05
  - TST-SVC-009-06
```
