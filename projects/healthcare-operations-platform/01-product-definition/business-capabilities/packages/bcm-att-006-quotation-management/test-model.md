---
id: HOP-TEST-BCM-ATT-006
format: markdown_structured_payload
type: test-model
name: Quotation Management Test Model
version: 0.2.0
status: modeled
---

# Quotation Management Test Model

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-TEST-BCM-ATT-006
  type: test-model
  name: Quotation Management Test Model
  version: 0.2.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-006
test_cases:
- id: TST-QUO-006-01
  type: acceptance
  validates_rule: RN-001
  statement: Adding a quotation line for an unpublished catalog item is rejected.
  generatable: false
- id: TST-QUO-006-02
  type: acceptance
  validates_rule: RN-002
  statement: Issuing a quotation without a pricing snapshot is rejected.
  generatable: false
- id: TST-QUO-006-03
  type: acceptance
  validates_rule: RN-003
  statement: Applying a discount beyond the tenant policy limit without an override
    scope is rejected.
  generatable: false
- id: TST-QUO-006-04
  type: acceptance
  validates_rule: RN-004
  statement: Accepting an expired quotation is rejected.
  generatable: false
- id: TST-QUO-006-05
  type: architecture
  validates_rule: RN-005
  statement: Quotation conversion invokes BCM-LAB-001 CreateDiagnosticOrder rather
    than direct order persistence.
  generatable: false
- id: TST-QUO-006-06
  type: contract
  validates_rule: RN-006
  statement: Unauthorized or out-of-scope actors cannot manage quotations.
  generatable: true
- id: TST-QUO-006-07
  type: acceptance
  validates_rule: RN-007
  statement: A converted quotation cannot be re-issued or re-accepted.
  generatable: false
- id: TST-QUO-006-08
  type: unit
  validates_rule: RN-008
  statement: QuotationIssued and QuotationAccepted events carry actor, branch and
    total amount.
  generatable: true
- id: TST-QUO-006-09
  type: acceptance
  validates_rule: RN-009
  statement: An anonymous public-website quotation request captures a ProspectiveContact,
    remains draft and is rejected once the public rate-limit is exceeded.
  generatable: false
test_layers:
- contract_tests
- unit_tests
- acceptance_tests
- architecture_tests
generation_policy:
  repetitive_tests: generated
  custom_rule_tests:
  - TST-QUO-006-01
  - TST-QUO-006-02
  - TST-QUO-006-03
  - TST-QUO-006-04
  - TST-QUO-006-05
  - TST-QUO-006-07
  - TST-QUO-006-09
```
