---
id: HOP-TEST-BCM-RES-005
format: markdown_structured_payload
type: test-model
name: Result History Test Model
version: 0.1.0
status: modeled
---

# Result History Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-RES-005
  type: test-model
  name: Result History Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-005
test_cases:
- id: TST-RHS-005-01
  type: acceptance
  validates_rule: RN-001
  statement: A history entry is never created for a result the recipient is not authorized
    to view.
  generatable: false
- id: TST-RHS-005-02
  type: architecture
  validates_rule: RN-002
  statement: This capability issues no command against LaboratoryResult or Patient.
  generatable: false
- id: TST-RHS-005-03
  type: acceptance
  validates_rule: RN-003
  statement: Trend computation never exposes another patient's value in a comparison.
  generatable: false
- id: TST-RHS-005-04
  type: acceptance
  validates_rule: RN-004
  statement: An amended result's history entry is not updated until BCM-RES-004 re-authorizes
    the amended delivery.
  generatable: false
- id: TST-RHS-005-05
  type: contract
  validates_rule: RN-005
  statement: A patient, representative or doctor cannot view history outside their
    authorized scope.
  generatable: true
- id: TST-RHS-005-06
  type: unit
  validates_rule: RN-006
  statement: Every history query is recorded with recipient, patient scope and timestamp.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-RHS-005-01
  - TST-RHS-005-02
  - TST-RHS-005-03
  - TST-RHS-005-04
```
