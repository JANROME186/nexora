---
id: HOP-TEST-BCM-SVC-006
format: markdown_structured_payload
type: test-model
name: Reference Range Management Test Model
version: 0.1.0
status: modeled
---

# Reference Range Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-SVC-006
  type: test-model
  name: Reference Range Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-006
test_cases:
- id: TST-SVC-006-01
  type: unit
  validates_rule: RN-001
  statement: Reject segment where normal low exceeds normal high.
  generatable: true
- id: TST-SVC-006-02
  type: acceptance
  validates_rule: RN-002
  statement: Reject critical thresholds that fall inside the normal range.
  generatable: false
- id: TST-SVC-006-03
  type: acceptance
  validates_rule: RN-003
  statement: Reject overlapping demographic segments for the same sex and condition.
  generatable: false
- id: TST-SVC-006-04
  type: acceptance
  validates_rule: RN-004
  statement: Publishing fails when the referenced analyte is not published.
  generatable: false
- id: TST-SVC-006-05
  type: regression
  validates_rule: RN-005
  statement: Editing a published range creates a new effective-dated version and preserves
    prior versions.
  generatable: false
- id: TST-SVC-006-06
  type: acceptance
  validates_rule: RN-006
  statement: Effective range resolution returns the version active at the observation
    date.
  generatable: false
- id: TST-SVC-006-07
  type: contract
  validates_rule: RN-007
  statement: Unauthorized users cannot create, update or publish ranges.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- regression_tests
- decision_table_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-SVC-006-02
  - TST-SVC-006-03
  - TST-SVC-006-04
  - TST-SVC-006-05
  - TST-SVC-006-06
```
