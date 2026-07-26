---
artifact:
  id: HOP-BACKLOG-MODULE-MVP-MOD-003
  type: backlog-module-record
  status: active
  optimization: atomic_context
---

# MVP-MOD-003 Module Backlog

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: MVP-MOD-003
name: People and Clinical Master Data
release: REL-001
priority: 20
status: planned
source: mvp_framework
objective: Manage patients, doctors and person records needed by order, result and portal workflows.
depends_on:
- MVP-MOD-001
capabilities:
- BCM-PER-001
- BCM-PER-002
- BCM-PER-003
- BCM-ATT-002
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: account_linking_later
  doctor_portal: account_linking_later
  mobile_app: patient_profile_later
backlog_items:
- id: MVP-MOD-003-DEF
  name: Capability package models
- id: MVP-MOD-003-BE-001
  name: Compile patient, doctor and person backend outputs
- id: MVP-MOD-003-BE-002
  name: Implement duplicate detection and portal identity custom rules
- id: MVP-MOD-003-FE-001
  name: Compile patient and doctor management UI outputs
- id: MVP-MOD-003-QA-001
  name: Master data validation and privacy evidence
- id: MVP-MOD-003-CLOSEOUT
  name: Module closeout and registry update
acceptance_summary:
- Staff can register, search and update patients and doctors.
- Patient and doctor records remain authoritative and cannot be mutated by downstream modules.
- Privacy, audit and duplicate handling rules are enforced.
```
