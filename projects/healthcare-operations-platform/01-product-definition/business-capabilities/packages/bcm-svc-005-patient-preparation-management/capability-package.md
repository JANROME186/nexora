---
id: HOP-CAP-PKG-BCM-SVC-005
format: markdown_structured_payload
type: capability-package
name: Patient Preparation Management Capability Package
version: 0.2.0
status: modeled
---

# Patient Preparation Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-SVC-005
  type: capability-package
  name: Patient Preparation Management Capability Package
  version: 0.2.0
  status: modeled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-08
  roadmap_group: MVP-MOD-002
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-SVC-005
  name:
    en: Patient Preparation Management
    es: Preparaciones del Paciente
  domain: DOM-03 Diagnostic Services
  priority: High
  roadmap: MVP1
  dependency_profile: catalog
  bounded_context: catalog-test-configuration
  primary_aggregate: TestDefinition
  aggregate_ref: AGG-006
scope:
  summary: 'Defines patient preparation instructions such as fasting, timing, medication
    restrictions and activity constraints associated with tests and panels, and publishes
    them as patient-facing guidance.

    '
  in_scope:
  - Preparation instruction definition and categorization.
  - Association of preparations to tests and panels.
  - Localized patient-facing instruction text.
  - Preparation lifecycle draft, published, deprecated.
  out_of_scope:
  - Test and panel definition (BCM-SVC-002, BCM-SVC-003).
  - Appointment scheduling delivery of instructions (BCM-ATT-001).
roadmap:
  module: MVP-MOD-002
  release: REL-001
  package_status: modeled
  next_backlog_item: MVP-MOD-002-BE-001
dependencies:
  required_capabilities:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-003
  downstream_capabilities:
  - BCM-ATT-001
  - BCM-LAB-002
  - BCM-PLT-003
  upstream_contexts:
  - organization-management
  - identity-access
  - audit-compliance
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: read_only_later
  doctor_portal: read_only_later
  mobile_app: deferred
  public_website: required
  public_website_note: Realized by COM-MOD-011-DEF from the pre-existing openapi-source.md
    future_surfaces and permissions.md POL-SVC-005-02 placeholders. Adds one new
    getPublishedPreparationSnapshot operation reusing the existing PublishedPreparationSnapshot
    schema; no new aggregate or capability package created.
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
