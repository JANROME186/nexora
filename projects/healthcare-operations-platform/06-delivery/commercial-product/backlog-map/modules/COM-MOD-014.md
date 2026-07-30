---
artifact:
  id: HOP-BACKLOG-MODULE-COM-MOD-014
  type: backlog-module-record
  status: active
  optimization: atomic_context
---

# COM-MOD-014 Module Backlog

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: COM-MOD-014
name: Imaging Operations
release: REL-004
priority: 130
status: module_closed
source: mvp_framework_future_module
objective: Add imaging appointment, reception, study management, DICOM/PACS integration, dictation, signature and delivery.
depends_on:
- MVP-MOD-003
- MVP-MOD-004
- MVP-MOD-007
- MVP-MOD-008
- COM-MOD-012
capabilities:
- BCM-IMG-001
- BCM-IMG-002
- BCM-IMG-003
- BCM-IMG-004
- BCM-IMG-005
- BCM-IMG-006
- BCM-IMG-007
- BCM-IMG-008
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: required
  doctor_portal: required
  mobile_app: result_view_required
  external_integrations: dicom_and_pacs_required
backlog_items:
- id: COM-MOD-014-DEF
  name: Capability package models
  status: closed
- id: COM-MOD-014-BE-001
  name: Compile imaging workflow outputs
  status: closed
- id: COM-MOD-014-INT-001
  name: Implement DICOM and PACS adapter custom boundaries
  status: closed
- id: COM-MOD-014-FE-001
  name: Compile imaging operations UI outputs
  status: closed
- id: COM-MOD-014-PORTAL-001
  name: Imaging study delivery views
  status: closed
  closed_under: HOP-HARD-APP-001
- id: COM-MOD-014-QA-001
  name: Imaging integration and report evidence
  status: closed
- id: COM-MOD-014-CLOSEOUT
  name: Module closeout and registry update
  status: closed
acceptance_summary:
- Imaging studies can be scheduled, received, managed, interpreted and delivered.
- DICOM/PACS interactions remain behind replaceable adapters.
- Imaging delivery follows the same authorization and audit model as laboratory results.
```
