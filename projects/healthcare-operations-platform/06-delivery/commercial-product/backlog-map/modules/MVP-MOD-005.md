---
artifact:
  id: HOP-BACKLOG-MODULE-MVP-MOD-005
  type: backlog-module-record
  status: active
  optimization: atomic_context
---

# MVP-MOD-005 Module Backlog

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: MVP-MOD-005
name: Cashier and Billing Request
release: REL-001
priority: 40
status: in_progress
source: mvp_framework
objective: Register payments, cash sessions, sales and fiscal billing requests through adapter boundaries.
depends_on:
- MVP-MOD-002
- MVP-MOD-003
- MVP-MOD-004
capabilities:
- BCM-ATT-005
- BCM-ATT-008
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: payment_history_later
  doctor_portal: not_required
  mobile_app: payment_receipt_later
backlog_items:
- id: MVP-MOD-005-DEF
  name: Capability package models
  status: closed
- id: MVP-MOD-005-BE-001
  name: Compile cash session, payment and sale backend outputs
  status: closed
- id: MVP-MOD-005-BE-002
  name: Implement billing request adapter custom boundary
  status: closed
- id: MVP-MOD-005-FE-001
  name: Compile cashier and billing request UI outputs
  status: closed
- id: MVP-MOD-005-QA-001
  name: Financial audit and reconciliation evidence
  status: closed
- id: MVP-MOD-005-CLOSEOUT
  name: Module closeout and registry update
  status: closed
acceptance_summary:
- Cashiers can open and close sessions and register payments.
- Billing requests are traceable and decoupled from country-specific fiscal adapters.
- Financial actions cannot mutate patient or clinical aggregates directly.
```
