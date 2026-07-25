---
id: HOP-CAP-PKG-BCM-ATT-001
format: markdown_structured_payload
type: capability-package
name: Appointment Scheduling Capability Package
version: 0.2.0
status: modeled
---

# Appointment Scheduling Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-ATT-001
  type: capability-package
  name: Appointment Scheduling Capability Package
  version: 0.2.0
  status: modeled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-15
  roadmap_group: COM-MOD-011
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-ATT-001
  name:
    en: Appointment Scheduling
    es: Agenda
  domain: DOM-04 Care Delivery
  priority: High
  roadmap: MVP1
  dependency_profile: care_delivery
  bounded_context: orders-samples
  secondary_bounded_context: organization-management
  primary_aggregate: DiagnosticOrder
  aggregate_ref: AGG-007
  aggregate_ownership_note: 'AppointmentSlot is a process-level entity owned by this
    capability. It does not mutate DiagnosticOrder directly; on confirmation or check-in
    it is handed off as sourceReferenceId to BCM-LAB-001 CreateDiagnosticOrder.

    '
  process_ref: HRP-001-P03
scope:
  summary: 'Manages appointment slot requests, confirmation and cancellation for a
    branch. Validates branch operational status and, when applicable, published catalog
    and preparation-instruction availability before confirming a slot. Confirmed or
    checked-in appointments become the intake reference that Reception Management
    and Admission Management use to start a diagnostic order through BCM-LAB-001.
    Governs patient self-scheduling via patient portal.

    '
  in_scope:
  - Appointment slot creation, confirmation, rescheduling and cancellation.
  - Branch operational-status and basic capacity validation at scheduling time.
  - Preparation-instruction and catalog-availability surfacing for the selected service.
  - Handoff of a confirmed appointment as an order intake reference.
  - No-show tracking.
  - Patient self-scheduling, rescheduling, and cancellation via portal.
  out_of_scope:
  - Patient and doctor master data ownership (BCM-PER-002, BCM-PER-003).
  - Diagnostic order creation and pricing (BCM-LAB-001).
  - Front-desk check-in queue management (BCM-ATT-003).
  - Detailed staff/branch schedule capacity planning (BCM-ORG-007, MVP2).
roadmap:
  module: COM-MOD-011
  release: REL-002
  package_status: compiled
  next_backlog_item: COM-MOD-011-CLOSEOUT
  package_status_correction_note: 'Corrected by COM-MOD-011-DEF: package_status was
    stale at "modeled" and module/ next_backlog_item were stale at COM-MOD-009/COM-MOD-009-BE-001
    (both already closed per capability-package-index.md and PROJECT_STATE.md).
    This capability was compiled under MVP-MOD-004 and extended for patient-portal
    self-scheduling under COM-MOD-009-PORTAL-001; module/next_backlog_item now point
    at COM-MOD-011, the current consuming module.'
dependencies:
  required_capabilities:
  - BCM-PER-002
  - BCM-ORG-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PER-003
  - BCM-SVC-001
  - BCM-SVC-005
  - BCM-ORG-007
  - BCM-PLT-003
  downstream_capabilities:
  - BCM-ATT-003
  - BCM-LAB-001
  upstream_contexts:
  - identity-access
  - organization-management
  - audit-compliance
  - patient-management
  - catalog-test-configuration
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: required
  doctor_portal: not_required
  mobile_app: required
  public_website: required
  public_website_note: Realized by COM-MOD-011-DEF from the pre-existing openapi-source.md
    future_surfaces placeholder. Reuses the existing requestAppointment operation,
    adding an anonymous public channel and RN-008; no new aggregate, schema or capability
    package created.
required_artifacts:
- capability-package.md
- business-model.md
- business-rules.md
- processes.md
- events.md
- openapi-source.md
- permissions.md
- ui-model.md
- mobile-model.md
- test-model.md
- observability-model.md
- generation-plan.md
- traceability.md
- README.md
```
