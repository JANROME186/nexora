---
id: HOP-CAP-PKG-BCM-ATT-002
format: markdown_structured_payload
type: capability-package
name: Patient Registration Capability Package
version: 0.1.0
status: modeled
---

# Patient Registration Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-ATT-002
  type: capability-package
  name: Patient Registration Capability Package
  version: 0.1.0
  status: modeled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-09
  roadmap_group: COM-MOD-009
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-ATT-002
  name:
    en: Patient Registration
    es: Registro de Pacientes
  domain: DOM-04 Care Delivery
  priority: Critical
  roadmap: MVP1
  dependency_profile: care_delivery
  bounded_context: patient-management
  primary_aggregate: Patient
  aggregate_ref: AGG-001
  process_ref: HRP-001-P03
scope:
  summary: 'Coordinates the operational registration flow that a receptionist executes
    at the front desk or that a patient portal handoff triggers. The flow consults
    Person Management duplicate detection, invokes Patient Management commands, verifies
    representative authorization when applicable and prepares the registration outcome
    for downstream reception, ordering, cashier and result modules. Governs self-registration
    on patient portal.

    '
  in_scope:
  - Registration intake data capture and validation.
  - Duplicate detection consultation and match resolution decision.
  - Registration commit through Patient Management commands.
  - Representative attachment during registration when applicable.
  - Consent capture at first registration when required by tenant policy.
  - Registration outcome record for reception and order intake.
  - Self-registration and consent signing on the Patient Portal.
  out_of_scope:
  - Aggregate ownership of Patient (owned by BCM-PER-002).
  - Doctor master data (BCM-PER-003).
  - Appointment scheduling (BCM-ATT-001).
  - Order intake (BCM-LAB-001 / MVP-MOD-004).
roadmap:
  module: COM-MOD-009
  release: REL-002
  package_status: modeled
  next_backlog_item: COM-MOD-009-BE-001
dependencies:
  required_capabilities:
  - BCM-PER-001
  - BCM-PER-002
  - BCM-PER-003
  - BCM-ORG-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-ATT-001
  - BCM-PLT-003
  - BCM-PLT-008
  downstream_capabilities:
  - BCM-ATT-003
  - BCM-LAB-001
  - BCM-ATT-005
  upstream_contexts:
  - identity-access
  - organization-management
  - audit-compliance
  - patient-management
  - medical-staff
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: required
  doctor_portal: not_required
  mobile_app: required
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
