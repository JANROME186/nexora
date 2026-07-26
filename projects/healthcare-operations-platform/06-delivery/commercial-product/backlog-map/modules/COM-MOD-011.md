---
artifact:
  id: HOP-BACKLOG-MODULE-COM-MOD-011
  type: backlog-module-record
  status: active
  optimization: atomic_context
---

# COM-MOD-011 Module Backlog

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: COM-MOD-011
name: Public Website and Digital Growth
release: REL-002
priority: 100
status: closed
source: derived_from_capability_dependency_map
objective: Provide commercial public-facing discovery, service catalog visibility, contact flows and conversion paths.
depends_on:
- MVP-MOD-002
- MVP-MOD-004
- COM-MOD-009
capabilities:
- BCM-SVC-001
- BCM-SVC-002
- BCM-SVC-003
- BCM-SVC-005
- BCM-ATT-001
- BCM-ATT-006
- BCM-PLT-005
product_surfaces:
  backend: required
  employee_portal: content_admin_required
  public_website: required
  patient_portal: handoff_required
  doctor_portal: handoff_required
  mobile_app: not_required
backlog_items:
- id: COM-MOD-011-DEF
  name: Capability package models
  status: closed
- id: COM-MOD-011-BE-001
  name: Compile public catalog, location and request outputs
  status: closed
- id: COM-MOD-011-WEB-001
  name: Compile public website service discovery and conversion flows
  status: closed
- id: COM-MOD-011-FE-001
  name: Content and request administration screens
  status: closed
- id: COM-MOD-011-QA-001
  name: Public web, SEO and privacy evidence
  status: closed
- id: COM-MOD-011-CLOSEOUT
  name: Module closeout and registry update
  status: closed
acceptance_summary:
- Prospective patients can discover services, preparation instructions, branches and contact paths.
- Public requests can be routed into internal workflows without exposing private APIs.
- Public content remains governed and auditable.
```
