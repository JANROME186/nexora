---
artifact:
  id: HOP-BACKLOG-MODULE-MVP-MOD-004
  type: backlog-module-record
  status: active
  optimization: atomic_context
---

# MVP-MOD-004 Module Backlog

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: MVP-MOD-004
name: Front Desk and Care Delivery
release: REL-001
priority: 30
status: in_progress
source: mvp_framework
objective: Support appointments, reception, admission, quotations and diagnostic order intake.
depends_on:
- MVP-MOD-002
- MVP-MOD-003
capabilities:
- BCM-ATT-001
- BCM-ATT-003
- BCM-ATT-004
- BCM-ATT-006
- BCM-LAB-001
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: appointment_request_later
  doctor_portal: order_request_later
  mobile_app: check_in_later
backlog_items:
- id: MVP-MOD-004-DEF
  name: Capability package models
- id: MVP-MOD-004-BE-001
  name: Compile appointment, reception and order backend outputs
- id: MVP-MOD-004-BE-002
  name: Implement quote calculation and order lifecycle custom rules
- id: MVP-MOD-004-FE-001
  name: Compile front desk worklist and order creation UI outputs
- id: MVP-MOD-004-QA-001
  name: Order lifecycle and snapshot evidence
- id: MVP-MOD-004-CLOSEOUT
  name: Module closeout and registry update
acceptance_summary:
- Staff can create walk-in and scheduled diagnostic orders.
- Orders reference immutable patient, doctor, branch, catalog and price snapshots.
- Order lifecycle changes are controlled and auditable.
```
