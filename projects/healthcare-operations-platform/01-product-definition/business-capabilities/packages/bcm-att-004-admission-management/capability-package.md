---
id: HOP-CAP-PKG-BCM-ATT-004
format: markdown_structured_payload
type: capability-package
name: Admission Management Capability Package
version: 0.1.0
status: modeled
---

# Admission Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-ATT-004
  type: capability-package
  name: Admission Management Capability Package
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
  id: BCM-ATT-004
  name:
    en: Admission Management
    es: Admisión
  domain: DOM-04 Care Delivery
  priority: High
  roadmap: MVP1
  dependency_profile: care_delivery
  bounded_context: orders-samples
  primary_aggregate: DiagnosticOrder
  aggregate_ref: AGG-007
  aggregate_ownership_note: 'AdmissionRequest is a process-level entity owned by this
    capability. It assembles catalog selection and clinical notes, then commits by
    invoking BCM-LAB-001 CreateDiagnosticOrder, PriceDiagnosticOrder and AcceptDiagnosticOrder
    commands rather than persisting order state itself.

    '
  process_ref: HRP-001-P03
scope:
  summary: 'Completes front-desk order intake: gathers catalog selection, referring
    doctor, clinical notes and consent confirmation for a reception visit that has
    confirmed identity, verifies completeness, and commits the diagnostic order through
    BCM-LAB-001. It is the gate between the front desk and clinical order acceptance.

    '
  in_scope:
  - Order-intake catalog selection from published tests and panels.
  - Referring-doctor selection and doctor-snapshot handoff data.
  - Clinical notes capture.
  - Completeness verification (required fields, sample-requirement acknowledgement,
    consent).
  - Commit of the diagnostic order through BCM-LAB-001 commands.
  - Rejection of an admission request that fails completeness checks.
  out_of_scope:
  - DiagnosticOrder aggregate mutation itself (BCM-LAB-001).
  - Reception queue and identity confirmation (BCM-ATT-003).
  - Catalog and price list definition (BCM-SVC-001/002/003/009).
  - Sample collection (BCM-LAB-002, MVP-MOD-006).
  - Cash and payment handling (BCM-ATT-005, MVP-MOD-005).
roadmap:
  module: MVP-MOD-004
  release: REL-001
  package_status: modeled
  next_backlog_item: MVP-MOD-004-BE-001
dependencies:
  required_capabilities:
  - BCM-ATT-003
  - BCM-PER-002
  - BCM-SVC-001
  - BCM-SVC-002
  - BCM-SVC-009
  - BCM-PLT-001
  - BCM-PLT-007
  - BCM-LAB-001
  optional_capabilities:
  - BCM-PER-003
  - BCM-ATT-006
  - BCM-SVC-005
  - BCM-SVC-007
  downstream_capabilities:
  - BCM-LAB-001
  - BCM-LAB-002
  upstream_contexts:
  - patient-management
  - medical-staff
  - catalog-test-configuration
  - identity-access
  - audit-compliance
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
