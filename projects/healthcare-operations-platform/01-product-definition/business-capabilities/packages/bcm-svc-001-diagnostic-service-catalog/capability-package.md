---
id: HOP-CAP-PKG-BCM-SVC-001
format: markdown_structured_payload
type: capability-package
name: Diagnostic Service Catalog Capability Package
version: 0.2.0
status: modeled
---

# Diagnostic Service Catalog Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-SVC-001
  type: capability-package
  name: Diagnostic Service Catalog Capability Package
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
  id: BCM-SVC-001
  name:
    en: Diagnostic Service Catalog
    es: Catálogo de Servicios
  domain: DOM-03 Diagnostic Services
  priority: Critical
  roadmap: MVP1
  dependency_profile: catalog
  bounded_context: catalog-test-configuration
  primary_aggregate: TestDefinition
  aggregate_ref: AGG-006
scope:
  summary: 'Defines the sellable and orderable diagnostic service offering that groups
    tests and panels into catalog entries with lifecycle, versioning and publication
    controls consumed by orders, quotations and portals.

    '
  in_scope:
  - Diagnostic service definition, categorization and taxonomy.
  - Service lifecycle from draft to published to deprecated.
  - Service to test and panel composition references.
  - Published service snapshot exposure for downstream consumers.
  out_of_scope:
  - Test analyte and reference range modeling (BCM-SVC-002, BCM-SVC-004, BCM-SVC-006).
  - Pricing (BCM-SVC-009).
  - Order creation and consumption (BCM-LAB-001).
roadmap:
  module: MVP-MOD-002
  release: REL-001
  package_status: modeled
  next_backlog_item: MVP-MOD-002-BE-001
dependencies:
  required_capabilities:
  - BCM-ORG-001
  - BCM-ORG-002
  - BCM-ORG-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-INV-001
  - BCM-QLT-004
  downstream_capabilities:
  - BCM-SVC-002
  - BCM-SVC-003
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
    future_surfaces placeholder. Reuses the existing getPublishedServiceSnapshot operation
    and PublishedServiceSnapshot schema; no new aggregate, schema or capability package
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
