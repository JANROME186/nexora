---
id: HOP-TEST-BCM-PER-001
format: markdown_structured_payload
type: test-model
name: Person Management Test Model
version: 0.1.0
status: modeled
---

# Person Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-PER-001
  type: test-model
  name: Person Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-PER-001
test_cases:
- id: TST-PER-001-01
  type: unit
  validates_rule: RN-001
  statement: Natural-key normalization removes diacritics and casing before comparison.
  generatable: true
- id: TST-PER-001-02
  type: acceptance
  validates_rule: RN-002
  statement: Registering a Patient with a document number already used by a Doctor
    within the tenant is rejected.
  generatable: false
- id: TST-PER-001-03
  type: acceptance
  validates_rule: RN-002
  statement: Registering a Doctor with a document number already used by a Patient
    within the tenant is rejected.
  generatable: false
- id: TST-PER-001-04
  type: acceptance
  validates_rule: RN-003
  statement: Duplicate detection returns ranked candidates with confidence score above
    configured threshold.
  generatable: false
- id: TST-PER-001-05
  type: regression
  validates_rule: RN-004
  statement: Direct persistence writes to PersonSearchIndex outside projection are
    prevented.
  generatable: false
- id: TST-PER-001-06
  type: contract
  validates_rule: RN-005
  statement: Unauthorized actors cannot execute person search or duplicate detection.
  generatable: true
- id: TST-PER-001-07
  type: unit
  validates_rule: RN-006
  statement: Duplicate detection triggered by a registration command produces an audit
    event.
  generatable: true
- id: TST-PER-001-08
  type: unit
  validates_rule: RN-007
  statement: National identifier hashing is deterministic and one-way.
  generatable: false
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- regression_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-PER-001-02
  - TST-PER-001-03
  - TST-PER-001-04
  - TST-PER-001-05
  - TST-PER-001-08
```
