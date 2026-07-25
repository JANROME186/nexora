---
id: HOP-TEST-BCM-SVC-002
format: markdown_structured_payload
type: test-model
name: Test Catalog Test Model
version: 0.1.0
status: modeled
---

# Test Catalog Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-SVC-002
  type: test-model
  name: Test Catalog Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-002
test_cases:
- id: TST-SVC-002-01
  type: unit
  validates_rule: RN-001
  statement: Reject duplicate test code within a laboratory.
  generatable: true
- id: TST-SVC-002-02
  type: unit
  validates_rule: RN-002
  statement: Reject numeric test without a measurement unit.
  generatable: true
- id: TST-SVC-002-03
  type: acceptance
  validates_rule: RN-003
  statement: Publishing fails without a linked sample requirement.
  generatable: false
- id: TST-SVC-002-04
  type: regression
  validates_rule: RN-004
  statement: Editing a published test creates a new draft and preserves the frozen
    version.
  generatable: false
- id: TST-SVC-002-05
  type: acceptance
  validates_rule: RN-005
  statement: Publishing fails when a linked analyte is not published.
  generatable: false
- id: TST-SVC-002-06
  type: contract
  validates_rule: RN-006
  statement: Unauthorized users cannot create, update or publish tests.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- regression_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-SVC-002-03
  - TST-SVC-002-04
  - TST-SVC-002-05
```
