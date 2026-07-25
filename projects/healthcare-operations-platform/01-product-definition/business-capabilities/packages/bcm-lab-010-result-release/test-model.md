---
id: HOP-TEST-BCM-LAB-010
format: markdown_structured_payload
type: test-model
name: Result Release Test Model
version: 0.1.0
status: modeled
---

# Result Release Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-LAB-010
  type: test-model
  name: Result Release Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-010
test_cases:
- id: TST-RLS-010-01
  type: acceptance
  validates_rule: RN-001
  statement: Releasing a result that is not medically validated is rejected.
  generatable: false
- id: TST-RLS-010-02
  type: acceptance
  validates_rule: RN-002
  statement: Releasing a result whose linked sample is rejected is rejected.
  generatable: false
- id: TST-RLS-010-03
  type: acceptance
  validates_rule: RN-003
  statement: A released result's value cannot be edited in place; correction requires
    a new amendment event.
  generatable: false
- id: TST-RLS-010-04
  type: acceptance
  validates_rule: RN-004
  statement: An amendment request without a licensed-authority actor or without a
    reason is rejected.
  generatable: false
- id: TST-RLS-010-05
  type: architecture
  validates_rule: RN-005
  statement: This capability writes only releaseRecord and amendments, never resultValue,
    technicalValidation, criticalFlag or medicalValidation.
  generatable: false
- id: TST-RLS-010-06
  type: contract
  validates_rule: RN-006
  statement: Unauthorized or out-of-scope actors cannot release or amend a result.
  generatable: true
- id: TST-RLS-010-07
  type: unit
  validates_rule: RN-007
  statement: ResultReleased and ResultAmended carry actor, result reference and, for
    amendments, the reason.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-RLS-010-01
  - TST-RLS-010-02
  - TST-RLS-010-03
  - TST-RLS-010-04
  - TST-RLS-010-05
```
