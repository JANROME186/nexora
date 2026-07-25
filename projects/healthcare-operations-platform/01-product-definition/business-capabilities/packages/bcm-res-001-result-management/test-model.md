---
id: HOP-TEST-BCM-RES-001
format: markdown_structured_payload
type: test-model
name: Result Management Test Model
version: 0.1.0
status: modeled
---

# Result Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-RES-001
  type: test-model
  name: Result Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-RES-001
test_cases:
- id: TST-RMG-001-01
  type: architecture
  validates_rule: RN-001
  statement: No code path in this capability issues a command against LaboratoryResult,
    Sample, Patient or Doctor.
  generatable: false
- id: TST-RMG-001-02
  type: unit
  validates_rule: RN-002
  statement: ResultSearchIndexEntry updates only in response to a recognized LaboratoryResult
    domain event; a direct write attempt is rejected.
  generatable: false
- id: TST-RMG-001-03
  type: acceptance
  validates_rule: RN-003
  statement: A laboratory technician cannot search or read results outside their laboratory
    scope.
  generatable: false
- id: TST-RMG-001-04
  type: acceptance
  validates_rule: RN-004
  statement: Every search or get-result query appends a ResultAccessAuditEntry.
  generatable: false
- id: TST-RMG-001-05
  type: contract
  validates_rule: RN-005
  statement: Unauthorized or out-of-scope actors cannot search or read results.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-RMG-001-01
  - TST-RMG-001-02
  - TST-RMG-001-03
  - TST-RMG-001-04
```
