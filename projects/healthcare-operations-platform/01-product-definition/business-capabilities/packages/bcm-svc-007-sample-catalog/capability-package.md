---
id: HOP-CAP-PKG-BCM-SVC-007
format: markdown_structured_payload
type: capability-package
name: Sample Catalog Capability Package
version: 0.1.0
status: modeled
---

# Sample Catalog Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-SVC-007
  type: capability-package
  name: Sample Catalog Capability Package
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
  id: BCM-SVC-007
  name:
    en: Sample Catalog
    es: Catálogo de Muestras
  domain: DOM-03 Diagnostic Services
  priority: High
  roadmap: MVP1
  dependency_profile: catalog
  bounded_context: catalog-test-configuration
  secondary_bounded_context: orders-samples
  primary_aggregate: TestDefinition
  aggregate_ref: AGG-006
  secondary_aggregate_ref: AGG-008
scope:
  summary: 'Defines sample types and sample requirements including sample kind, container,
    minimum volume, handling and temperature conditions used to determine what biological
    samples a test requires and how they must be collected and handled.

    '
  in_scope:
  - Sample type catalog definition.
  - Sample requirement definition per test.
  - Container, volume, handling and temperature specification.
  - Published sample requirement snapshot for collection and reception.
  out_of_scope:
  - Container inventory management (BCM-SVC-008, MVP2).
  - Sample collection execution (BCM-LAB-002).
  - Sample reception workflow (BCM-LAB-005).
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
  - BCM-SVC-008
  downstream_capabilities:
  - BCM-LAB-002
  - BCM-LAB-003
  - BCM-LAB-005
  - BCM-SVC-002
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
