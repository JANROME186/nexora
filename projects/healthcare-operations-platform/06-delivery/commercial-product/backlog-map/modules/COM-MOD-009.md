---
artifact:
  id: HOP-BACKLOG-MODULE-COM-MOD-009
  type: backlog-module-record
  status: active
  optimization: atomic_context
---

# COM-MOD-009 Module Backlog

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: COM-MOD-009
name: Patient and Doctor Portals
release: REL-002
priority: 80
status: module_closed
source: derived_from_capability_dependency_map
objective: Turn patient and doctor digital channels into coherent commercial-facing experiences.
depends_on:
- MVP-MOD-003
- MVP-MOD-004
- MVP-MOD-007
- MVP-MOD-008
capabilities:
- BCM-PER-002
- BCM-PER-003
- BCM-ATT-001
- BCM-ATT-002
- BCM-RES-004
- BCM-RES-005
- BCM-RES-007
- BCM-PLT-001
- BCM-PLT-003
product_surfaces:
  backend: required
  employee_portal: support_required
  patient_portal: required
  doctor_portal: required
  mobile_app: required
backlog_items:
- id: COM-MOD-009-DEF
  name: Capability package models
  status: closed
- id: COM-MOD-009-BE-001
  name: Compile portal access and authorization outputs
  status: closed
- id: COM-MOD-009-PORTAL-001
  name: Compile patient portal commercial workflow
  status: closed
- id: COM-MOD-009-PORTAL-002
  name: Compile doctor portal commercial workflow
  status: closed
- id: COM-MOD-009-APP-001
  name: Patient mobile workflow
  status: closed
- id: COM-MOD-009-QA-001
  name: Channel access and privacy evidence
  status: closed
- id: COM-MOD-009-CLOSEOUT
  name: Module closeout and registry update
  status: closed
acceptance_summary:
- Patients can access profile, appointments, orders, results and notifications where authorized.
- Doctors can access referred patient results and order/status information where authorized.
- Support users can assist channel access without bypassing audit controls.
```
