---
id: HOP-TEST-BCM-RES-007
format: markdown_structured_payload
type: test-model
name: Result Notifications Test Model
version: 0.1.0
status: modeled
---

# Result Notifications Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-RES-007
  type: test-model
  name: Result Notifications Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-007
test_cases:
- id: TST-RNT-007-01
  type: acceptance
  validates_rule: RN-001
  statement: A result_delivered notification is never composed before ResultDeliveryAuthorized
    is published.
  generatable: false
- id: TST-RNT-007-02
  type: acceptance
  validates_rule: RN-002
  statement: Every ResultFlaggedCritical event produces a result_critical ResultNotificationRequest,
    with no exception path.
  generatable: false
- id: TST-RNT-007-03
  type: architecture
  validates_rule: RN-003
  statement: This capability never calls a channel provider directly; dispatch always
    goes through BCM-PLT-003.
  generatable: false
- id: TST-RNT-007-04
  type: architecture
  validates_rule: RN-004
  statement: This capability issues no command against LaboratoryResult, Patient or
    Doctor.
  generatable: false
- id: TST-RNT-007-05
  type: contract
  validates_rule: RN-005
  statement: Unauthorized or out-of-scope actors cannot compose or read result notifications.
  generatable: true
- id: TST-RNT-007-06
  type: unit
  validates_rule: RN-006
  statement: ResultNotificationComposed carries trigger reason, result reference and
    dispatch status is tracked to a terminal state.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-RNT-007-01
  - TST-RNT-007-02
  - TST-RNT-007-03
  - TST-RNT-007-04
```
