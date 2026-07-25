---
id: HOP-TEST-BCM-SVC-001
format: markdown_structured_payload
type: test-model
name: Diagnostic Service Catalog Test Model
version: 0.1.0
status: modeled
---

# Diagnostic Service Catalog Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-SVC-001
  type: test-model
  name: Diagnostic Service Catalog Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-SVC-001
test_cases:
- id: TST-SVC-001-01
  type: unit
  validates_rule: RN-001
  statement: Reject duplicate service code within a laboratory.
  generatable: true
- id: TST-SVC-001-02
  type: acceptance
  validates_rule: RN-002
  statement: Publishing fails when no published component is referenced.
  generatable: false
- id: TST-SVC-001-03
  type: acceptance
  validates_rule: RN-002
  statement: Publishing succeeds with at least one published component.
  generatable: false
- id: TST-SVC-001-04
  type: regression
  validates_rule: RN-003
  statement: Editing a published service creates a new draft version and preserves
    the frozen version.
  generatable: false
- id: TST-SVC-001-05
  type: acceptance
  validates_rule: RN-004
  statement: Deprecated services are excluded from new order eligibility but resolve
    for history.
  generatable: false
- id: TST-SVC-001-06
  type: unit
  validates_rule: RN-005
  statement: Reject category assignment to a non-existent or inactive category.
  generatable: true
- id: TST-SVC-001-07
  type: contract
  validates_rule: RN-006
  statement: Unauthorized users cannot create, update or publish services.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- regression_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-SVC-001-02
  - TST-SVC-001-03
  - TST-SVC-001-04
  - TST-SVC-001-05
```
