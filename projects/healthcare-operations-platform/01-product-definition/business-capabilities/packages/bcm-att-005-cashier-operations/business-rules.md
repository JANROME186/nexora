---
id: HOP-RULES-BCM-ATT-005
format: markdown_structured_payload
type: business-rules
name: Cashier Operations Business Rules
version: 0.1.0
status: modeled
---

# Cashier Operations Business Rules

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RULES-BCM-ATT-005
  type: business-rules
  name: Cashier Operations Business Rules
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-005
rules:
- id: RN-001
  statement: A sale may be created only from an accepted diagnostic order or an accepted
    quotation.
  applies_to: Sale
  enforcement_point: createSale
  severity: critical
  audit_required: true
  test_refs:
  - TST-CASH-001
- id: RN-002
  statement: Cash payments require an open cash session in the same branch and tenant.
  applies_to: PaymentAllocation
  enforcement_point: registerPayment
  severity: critical
  audit_required: true
  test_refs:
  - TST-CASH-002
- id: RN-003
  statement: Payment allocations cannot exceed the outstanding sale balance unless
    tenant overpayment policy allows it.
  applies_to: SaleTotals
  enforcement_point: registerPayment
  severity: high
  audit_required: true
  test_refs:
  - TST-CASH-003
- id: RN-004
  statement: Closing a cash session requires expected amount, counted amount and variance
    reason when variance is non-zero.
  applies_to: CashRegister
  enforcement_point: closeCashSession
  severity: high
  audit_required: true
  test_refs:
  - TST-CASH-004
- id: RN-005
  statement: Cashier operations may request billing but must not issue fiscal invoices
    directly.
  applies_to: Sale
  enforcement_point: requestBilling
  severity: critical
  audit_required: true
  test_refs:
  - TST-CASH-005
- id: RN-006
  statement: Cancelled or refunded sales cannot receive new payment allocations.
  applies_to: Sale
  enforcement_point: registerPayment
  severity: high
  audit_required: true
  test_refs:
  - TST-CASH-006
```
