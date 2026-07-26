---
artifact:
  id: HOP-BACKLOG-MODULE-MVP-MOD-002
  type: backlog-module-record
  status: active
  optimization: atomic_context
---

# MVP-MOD-002 Module Backlog

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: MVP-MOD-002
name: Diagnostic Catalog
release: REL-001
priority: 10
status: next
source: mvp_framework
objective: Configure diagnostic services, tests, panels, analytes, sample requirements, preparation instructions, reference
  ranges and prices.
depends_on:
- MVP-MOD-001
capabilities:
- BCM-SVC-001
- BCM-SVC-002
- BCM-SVC-003
- BCM-SVC-004
- BCM-SVC-005
- BCM-SVC-006
- BCM-SVC-007
- BCM-SVC-009
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: read_only_later
  doctor_portal: read_only_later
  mobile_app: not_required
backlog_items:
- id: MVP-MOD-002-DEF
  name: Capability package models
- id: MVP-MOD-002-BE-001
  name: Compile catalog backend outputs from capability packages
- id: MVP-MOD-002-BE-002
  name: Implement catalog custom business rules
- id: MVP-MOD-002-FE-001
  name: Compile employee catalog UI outputs from UI models
- id: MVP-MOD-002-QA-001
  name: Validate generated outputs, contracts, rules and smoke evidence
- id: MVP-MOD-002-CLOSEOUT
  name: Capability package group closeout and registry update
acceptance_summary:
- Staff can define and publish tests, panels, analytes and sample requirements.
- Orders can consume only published catalog snapshots.
- Reference ranges and prices are version-aware and auditable.
```
