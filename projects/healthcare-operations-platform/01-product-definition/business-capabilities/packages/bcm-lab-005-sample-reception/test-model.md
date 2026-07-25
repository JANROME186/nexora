---
id: HOP-TEST-BCM-LAB-005
format: markdown_structured_payload
type: test-model
name: Sample Reception Test Model
version: 0.1.0
status: modeled
---

# Sample Reception Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-LAB-005
  type: test-model
  name: Sample Reception Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-LAB-005
test_cases:
- id: TST-RCP-005-01
  type: acceptance
  validates_rule: RN-001
  statement: Receiving an unlabeled sample is rejected, not accepted.
  generatable: false
- id: TST-RCP-005-02
  type: acceptance
  validates_rule: RN-002
  statement: A sample with visible hemolysis, an unintact container, insufficient
    volume or a transport window violation is rejected with a matching reason code.
  generatable: false
- id: TST-RCP-005-03
  type: acceptance
  validates_rule: RN-003
  statement: A rejected sample cannot be received or moved to in_process.
  generatable: false
- id: TST-RCP-005-04
  type: acceptance
  validates_rule: RN-004
  statement: Disposing a sample preserves prior collection, labeling, reception and
    rejection evidence.
  generatable: false
- id: TST-RCP-005-05
  type: architecture
  validates_rule: RN-005
  statement: This capability writes only receptionRecord, rejection-at-reception and
    disposal fields, never collectionData or labelInfo.
  generatable: false
- id: TST-RCP-005-06
  type: contract
  validates_rule: RN-006
  statement: Unauthorized or out-of-scope actors cannot receive, reject or dispose
    a sample.
  generatable: true
- id: TST-RCP-005-07
  type: unit
  validates_rule: RN-007
  statement: SampleReceived and SampleRejected carry actor, branch, sample reference
    and condition-check outcome.
  generatable: true
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-RCP-005-01
  - TST-RCP-005-02
  - TST-RCP-005-03
  - TST-RCP-005-04
  - TST-RCP-005-05
```
