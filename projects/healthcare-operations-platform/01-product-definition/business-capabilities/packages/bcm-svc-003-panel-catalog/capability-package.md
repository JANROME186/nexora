---
id: HOP-CAP-PKG-BCM-SVC-003
format: markdown_structured_payload
type: capability-package
name: Panel Catalog Capability Package
version: 0.2.0
status: modeled
---

# Panel Catalog Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-SVC-003
  type: capability-package
  name: Panel Catalog Capability Package
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
  id: BCM-SVC-003
  name:
    en: Panel Catalog
    es: Catálogo de Paneles
  domain: DOM-03 Diagnostic Services
  priority: High
  roadmap: MVP1
  dependency_profile: catalog
  bounded_context: catalog-test-configuration
  primary_aggregate: TestDefinition
  aggregate_ref: AGG-006
scope:
  summary: 'Defines panels that group multiple tests into a single orderable clinical
    set with shared preparation and sample handling, lifecycle and publication controls.

    '
  in_scope:
  - Panel definition and member test composition.
  - Panel lifecycle draft, published, deprecated.
  - Published panel snapshot exposure for orders and quotations.
  out_of_scope:
  - Individual test definition (BCM-SVC-002).
  - Service catalog grouping (BCM-SVC-001).
  - Pricing (BCM-SVC-009).
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
  - BCM-SVC-002
  optional_capabilities: []
  downstream_capabilities:
  - BCM-LAB-001
  - BCM-ATT-006
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
  public_website: required
  public_website_note: Realized by COM-MOD-011-DEF from the pre-existing openapi-source.md
    future_surfaces placeholder. Reuses the existing getPublishedPanelSnapshot operation
    and PublishedPanelSnapshot schema; no new aggregate, schema or capability package
    created.
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
