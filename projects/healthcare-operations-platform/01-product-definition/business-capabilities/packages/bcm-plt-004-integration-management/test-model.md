---
id: HOP-TEST-BCM-PLT-004
format: markdown_structured_payload
type: test-model
name: Integration Management Test Model
version: 0.1.0
status: modeled
---

# Integration Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-PLT-004
  type: test-model
  name: Integration Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PLT-004
test_cases:
- id: TST-INT-004-01
  type: architecture
  validates_rule: RN-001
  statement: No code path parses a raw external protocol payload without going through
    IntegrationAdapterPort.
  generatable: false
- id: TST-INT-004-02
  type: acceptance
  validates_rule: RN-002
  statement: A message that fails normalization is assigned a canonical error code
    and never surfaces raw provider error text to a domain module.
  generatable: false
- id: TST-INT-004-03
  type: acceptance
  validates_rule: RN-003
  statement: Reprocessing the same externalMessageId does not create a duplicate domain
    effect.
  generatable: false
- id: TST-INT-004-04
  type: acceptance
  validates_rule: RN-004
  statement: A message that exceeds the configured retry limit is dead-lettered instead
    of retried indefinitely, and no retry bypasses the owning domain's own command.
  generatable: false
- id: TST-INT-004-05
  type: architecture
  validates_rule: RN-005
  statement: Every message lifecycle transition emits an audited event carrying a
    correlation id.
  generatable: false
- id: TST-INT-004-06
  type: contract
  validates_rule: RN-006
  statement: Unauthorized or out-of-scope callers cannot manage an endpoint or read
    message status outside their tenant/laboratory.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-INT-004-01
  - TST-INT-004-02
  - TST-INT-004-03
  - TST-INT-004-04
  - TST-INT-004-05
```
