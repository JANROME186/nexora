---
id: HOP-TEST-BCM-PLT-003
format: markdown_structured_payload
type: test-model
name: Notification Management Test Model
version: 0.1.0
status: modeled
---

# Notification Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-PLT-003
  type: test-model
  name: Notification Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-003
test_cases:
- id: TST-NOT-003-01
  type: acceptance
  validates_rule: RN-001
  statement: A normal-priority notification to a channel the recipient opted out of
    is suppressed.
  generatable: false
- id: TST-NOT-003-02
  type: architecture
  validates_rule: RN-002
  statement: No code path dispatches through a channel provider directly, bypassing
    NotificationProviderPort.
  generatable: false
- id: TST-NOT-003-03
  type: architecture
  validates_rule: RN-003
  statement: This capability never composes or decides notification content; it dispatches
    only the supplied template reference and parameters.
  generatable: false
- id: TST-NOT-003-04
  type: acceptance
  validates_rule: RN-004
  statement: A notification that fails dispatch is retried up to the configured maximum
    before transitioning to failed.
  generatable: false
- id: TST-NOT-003-05
  type: architecture
  validates_rule: RN-005
  statement: This capability never issues a command against LaboratoryResult, Patient,
    Doctor or Sample.
  generatable: false
- id: TST-NOT-003-06
  type: contract
  validates_rule: RN-006
  statement: Unauthorized or out-of-scope callers cannot submit or read notification
    requests.
  generatable: true
- id: TST-NOT-003-07
  type: unit
  validates_rule: RN-007
  statement: NotificationDispatched and NotificationDeliveryFailed carry channel,
    attempt number and provider reference when available.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-NOT-003-01
  - TST-NOT-003-02
  - TST-NOT-003-03
  - TST-NOT-003-04
  - TST-NOT-003-05
```
