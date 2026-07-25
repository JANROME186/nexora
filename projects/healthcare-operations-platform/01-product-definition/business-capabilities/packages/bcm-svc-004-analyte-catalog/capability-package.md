---
id: HOP-CAP-PKG-BCM-SVC-004
format: markdown_structured_payload
type: capability-package
name: Analyte Catalog Capability Package
version: 0.1.0
status: modeled
---

# Analyte Catalog Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-SVC-004
  type: capability-package
  name: Analyte Catalog Capability Package
  version: 0.1.0
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
  id: BCM-SVC-004
  name:
    en: Analyte Catalog
    es: Catálogo de Analitos
  domain: DOM-03 Diagnostic Services
  priority: Critical
  roadmap: MVP1
  dependency_profile: catalog
  bounded_context: catalog-test-configuration
  secondary_bounded_context: laboratory-results
  primary_aggregate: TestDefinition
  aggregate_ref: AGG-006
  secondary_aggregate_ref: AGG-009
scope:
  summary: 'Defines analytes, the atomic measurable components of a test, including
    result data type, measurement unit, decimal precision, coding and result value
    constraints. Analytes are the published language consumed by result capture, validation
    and reference range evaluation.

    '
  in_scope:
  - Analyte definition, coding, units and precision.
  - Analyte result value type and validation constraints.
  - Analyte lifecycle draft, published, deprecated.
  - Published analyte snapshot exposure for result capture and ranges.
  out_of_scope:
  - Reference range values (BCM-SVC-006).
  - Test composition (BCM-SVC-002).
  - Result capture and validation workflow (BCM-LAB-006, BCM-LAB-008).
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
  optional_capabilities: []
  downstream_capabilities:
  - BCM-LAB-006
  - BCM-RES-001
  - BCM-RES-006
  - BCM-SVC-006
  upstream_contexts:
  - organization-management
  - identity-access
  - audit-compliance
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: read_only_later
  doctor_portal: read_only_later
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
