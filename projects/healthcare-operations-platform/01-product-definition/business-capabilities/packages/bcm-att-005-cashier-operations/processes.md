---
id: HOP-PROC-BCM-ATT-005
format: markdown_structured_payload
type: processes
name: Cashier Operations Processes
version: 0.1.0
status: modeled
---

# Cashier Operations Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-ATT-005
  type: processes
  name: Cashier Operations Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-005
processes:
- id: PROC-CASH-001
  name: Open cash session
  actor: cashier
  trigger: cashier_start_shift
  steps:
  - Validate branch, tenant and cashier permission.
  - Confirm no other open session exists for the same register.
  - Capture opening amount.
  - Persist CashSessionOpened audit event.
- id: PROC-CASH-002
  name: Create payable sale
  actor: cashier
  trigger: order_or_quotation_ready_for_payment
  steps:
  - Resolve accepted diagnostic order or accepted quotation.
  - Capture sale line and price snapshots.
  - Calculate sale totals.
  - Mark sale payable and emit SaleCreated.
- id: PROC-CASH-003
  name: Register payment
  actor: cashier
  trigger: patient_payment_presented
  steps:
  - Validate sale payable state and outstanding amount.
  - Validate cash session when method is cash.
  - Register provider-agnostic payment allocation.
  - Update sale payment status and emit PaymentRegistered.
- id: PROC-CASH-004
  name: Close cash session
  actor: cashier
  trigger: cashier_end_shift
  steps:
  - Calculate expected cash from session movements.
  - Capture counted cash.
  - Require variance reason when variance is non-zero.
  - Persist CashSessionClosed and CashVarianceDetected when applicable.
```
