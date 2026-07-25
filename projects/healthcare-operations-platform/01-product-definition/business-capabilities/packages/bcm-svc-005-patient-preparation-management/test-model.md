---
id: HOP-TEST-BCM-SVC-005
format: markdown_structured_payload
type: test-model
name: Patient Preparation Management Test Model
version: 0.1.0
status: modeled
---

# Patient Preparation Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-SVC-005
  type: test-model
  name: Patient Preparation Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-005
test_cases:
- id: TST-SVC-005-01
  type: unit
  validates_rule: RN-001
  statement: Reject duplicate preparation code within a laboratory.
  generatable: true
- id: TST-SVC-005-02
  type: acceptance
  validates_rule: RN-002
  statement: Publishing fails when localized text is incomplete.
  generatable: true
- id: TST-SVC-005-03
  type: unit
  validates_rule: RN-003
  statement: Reject fasting preparation without a duration.
  generatable: true
- id: TST-SVC-005-04
  type: acceptance
  validates_rule: RN-004
  statement: Assigning to an unpublished test or panel is rejected.
  generatable: false
- id: TST-SVC-005-05
  type: regression
  validates_rule: RN-005
  statement: Editing a published preparation creates a new draft and preserves the
    frozen version.
  generatable: false
- id: TST-SVC-005-06
  type: contract
  validates_rule: RN-006
  statement: Unauthorized users cannot create, update or publish preparations.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- regression_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-SVC-005-04
  - TST-SVC-005-05
```
