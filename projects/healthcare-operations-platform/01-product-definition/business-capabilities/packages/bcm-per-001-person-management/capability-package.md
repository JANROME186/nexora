---
id: HOP-CAP-PKG-BCM-PER-001
format: markdown_structured_payload
type: capability-package
name: Person Management Capability Package
version: 0.1.0
status: modeled
---

# Person Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-PER-001
  type: capability-package
  name: Person Management Capability Package
  version: 0.1.0
  status: modeled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-09
  roadmap_group: MVP-MOD-003
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-PER-001
  name:
    en: Person Management
    es: Gestión de Personas
  domain: DOM-02 People
  priority: High
  roadmap: MVP1
  dependency_profile: master_data
  bounded_context: patient-management
  secondary_bounded_context: medical-staff
  primary_aggregate_reference: shared_person_concepts
  aggregate_ref: cross-cutting
scope:
  summary: 'Defines the shared Person master-data concepts, identifiers, natural-key
    policies and duplicate-detection services that Patient (BCM-PER-002) and Doctor
    (BCM-PER-003) capabilities specialize. Person is not an owning aggregate on its
    own; it is a common language that governs person natural keys, name model, contact
    information, personal identification documents and duplicate-detection policy
    across the patient-management and medical-staff bounded contexts.

    '
  in_scope:
  - Shared Person value objects such as PersonNaturalKey, PersonName, PersonDocument,
    PersonContact and PersonAddress.
  - Natural-key normalization rules for duplicate detection.
  - Person duplicate-detection query service used at registration time.
  - Cross-context person read model for search and merge coordination.
  - Person merge coordination policy scoped to the owning context.
  out_of_scope:
  - Patient specific clinical fields and consent (BCM-PER-002).
  - Doctor specific credentials and specialties (BCM-PER-003).
  - Company, agreement and supplier master data (BCM-PER-004..006).
  - Registration process (BCM-ATT-002) which uses this capability.
roadmap:
  module: MVP-MOD-003
  release: REL-001
  package_status: modeled
  next_backlog_item: MVP-MOD-003-BE-001
dependencies:
  required_capabilities:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-ORG-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-004
  downstream_capabilities:
  - BCM-PER-002
  - BCM-PER-003
  - BCM-PER-004
  - BCM-ATT-002
  upstream_contexts:
  - organization-management
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
