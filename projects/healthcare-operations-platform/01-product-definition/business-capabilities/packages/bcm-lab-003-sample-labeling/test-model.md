---
id: HOP-TEST-BCM-LAB-003
format: markdown_structured_payload
type: test-model
name: Sample Labeling Test Model
version: 0.1.0
status: modeled
---

# Sample Labeling Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-LAB-003
  type: test-model
  name: Sample Labeling Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-003
test_cases:
- id: TST-LBL-003-01
  type: acceptance
  validates_rule: RN-001
  statement: Printing a label against a rejected or disposed sample is rejected.
  generatable: false
- id: TST-LBL-003-02
  type: acceptance
  validates_rule: RN-002
  statement: Confirming a label with a mismatched barcode is rejected.
  generatable: false
- id: TST-LBL-003-03
  type: architecture
  validates_rule: RN-003
  statement: This capability writes only Sample.labelInfo, never any other Sample
    field.
  generatable: false
- id: TST-LBL-003-04
  type: contract
  validates_rule: RN-004
  statement: Unauthorized or out-of-scope actors cannot print, confirm or reprint
    a label.
  generatable: true
- id: TST-LBL-003-05
  type: acceptance
  validates_rule: RN-005
  statement: Relabeling a received or in_process sample without an override reason
    is rejected.
  generatable: false
- id: TST-LBL-003-06
  type: unit
  validates_rule: RN-006
  statement: SpecimenLabelPrinted and SpecimenLabelAssigned carry actor, branch, sample
    reference and print attempts.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-LBL-003-01
  - TST-LBL-003-02
  - TST-LBL-003-03
  - TST-LBL-003-05
```
