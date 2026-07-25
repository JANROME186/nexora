---
id: HOP-TEST-BCM-ATT-008
format: markdown_structured_payload
type: test-model
name: Billing Request Management Test Model
version: 0.1.0
status: modeled
---

# Billing Request Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-ATT-008
  type: test-model
  name: Billing Request Management Test Model
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-008
test_cases:
- id: TST-BILL-001
  type: acceptance
  validates_rule: RN-001
  statement: Creating a billing request without a valid sale is rejected.
  generatable: false
- id: TST-BILL-002
  type: acceptance
  validates_rule: RN-002
  statement: Billing request stores fiscal profile as immutable snapshot.
  generatable: false
- id: TST-BILL-003
  type: architecture
  validates_rule: RN-003
  statement: Invoice issuance occurs only through billing adapter port.
  generatable: false
- id: TST-BILL-004
  type: unit
  validates_rule: RN-004
  statement: Retry preserves adapter idempotency key and only applies to failed non-terminal
    requests.
  generatable: false
- id: TST-BILL-005
  type: unit
  validates_rule: RN-005
  statement: Issued and cancelled requests reject resubmission.
  generatable: true
test_layers:
- unit_tests
- contract_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-BILL-001
  - TST-BILL-002
  - TST-BILL-003
  - TST-BILL-004
```
