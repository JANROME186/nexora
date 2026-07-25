---
id: HOP-CAP-PKG-BCM-ATT-003
format: markdown_structured_payload
type: capability-package
name: Reception Management Capability Package
version: 0.1.0
status: modeled
---

# Reception Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-ATT-003
  type: capability-package
  name: Reception Management Capability Package
  version: 0.1.0
  status: modeled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-15
  roadmap_group: MVP-MOD-004
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-ATT-003
  name:
    en: Reception Management
    es: Recepción
  domain: DOM-04 Care Delivery
  priority: Critical
  roadmap: MVP1
  dependency_profile: care_delivery
  bounded_context: orders-samples
  secondary_bounded_context: patient-management
  primary_aggregate: DiagnosticOrder
  aggregate_ref: AGG-007
  aggregate_ownership_note: 'ReceptionVisit is a process-level entity owned by this
    capability. It confirms patient identity at the front desk and hands off to Admission
    Management (BCM-ATT-004), which completes the order intake through BCM-LAB-001
    commands.

    '
  process_ref: HRP-001-P03
scope:
  summary: 'Coordinates the front-desk queue: confirms patient identity for walk-in
    or scheduled arrivals, links a checked-in appointment when one exists, prioritizes
    the reception queue and hands the visit to Admission Management for order intake
    completion.

    '
  in_scope:
  - Walk-in and scheduled-arrival visit intake.
  - Patient identity confirmation at the front desk.
  - Reception queue prioritization and status tracking.
  - Linking a visit to an existing confirmed/checked-in appointment.
  - Handoff of the visit to Admission Management.
  out_of_scope:
  - Patient master data ownership (BCM-PER-002).
  - Appointment slot lifecycle (BCM-ATT-001).
  - Order line composition, pricing and clinical notes (BCM-ATT-004, BCM-LAB-001).
  - Cash and payment handling (BCM-ATT-005, MVP-MOD-005).
  - Sample collection (BCM-LAB-002, MVP-MOD-006).
roadmap:
  module: MVP-MOD-004
  release: REL-001
  package_status: modeled
  next_backlog_item: MVP-MOD-004-BE-001
dependencies:
  required_capabilities:
  - BCM-PER-002
  - BCM-PER-003
  - BCM-ORG-003
  - BCM-PLT-001
  - BCM-PLT-007
  - BCM-LAB-001
  optional_capabilities:
  - BCM-ATT-001
  - BCM-ATT-006
  downstream_capabilities:
  - BCM-ATT-004
  - BCM-ATT-005
  - BCM-LAB-002
  upstream_contexts:
  - identity-access
  - organization-management
  - audit-compliance
  - patient-management
  - medical-staff
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: not_required
  doctor_portal: not_required
  mobile_app: not_required
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
