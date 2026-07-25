---
id: HOP-PROC-BCM-ATT-008
format: markdown_structured_payload
type: processes
name: Billing Request Management Processes
version: 0.1.0
status: modeled
---

# Billing Request Management Processes

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-PROC-BCM-ATT-008
  type: processes
  name: Billing Request Management Processes
  version: 0.1.0
  status: modeled
  classification: editable_model
  capability: BCM-ATT-008
processes:
- id: PROC-BILL-001
  name: Create billing request
  actor: cashier
  trigger: patient_requests_invoice
  steps:
  - Resolve sale from BCM-ATT-005.
  - Capture patient fiscal profile snapshot.
  - Calculate tax lines from sale totals and country-pack configuration.
  - Persist InvoiceRequested.
- id: PROC-BILL-002
  name: Submit billing request
  actor: cashier
  trigger: billing_request_ready
  steps:
  - Validate adapter configuration.
  - Submit request through provider-agnostic billing adapter port.
  - Persist adapter response snapshot.
  - Mark issued or failed.
- id: PROC-BILL-003
  name: Retry failed billing request
  actor: branch_manager
  trigger: adapter_failure_reviewed
  steps:
  - Validate non-terminal failed state.
  - Preserve idempotency key.
  - Resubmit through the same adapter boundary.
- id: PROC-BILL-004
  name: Cancel billing request
  actor: branch_manager
  trigger: fiscal_cancellation_requested
  steps:
  - Validate issued or requested state according to adapter policy.
  - Submit cancellation when external invoice exists.
  - Persist InvoiceCancelled.
```
