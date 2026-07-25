---
id: HOP-CAP-PKG-BCM-INV-001
format: markdown_structured_payload
type: capability-package
name: Product Catalog Capability Package
version: 0.1.0
status: modeled
---

# Product Catalog Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-INV-001
  type: capability-package
  name: Product Catalog Capability Package
  version: 0.1.0
  status: modeled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-20
  roadmap_group: COM-MOD-010
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-INV-001
  name:
    en: Product Catalog
    es: Catálogo de Productos
  domain: DOM-08 Inventory
  priority: High
  roadmap: MVP2
  dependency_profile: inventory_quality
  bounded_context: inventory-procurement
  primary_aggregate: InventoryItem (AGG-013, owner)
  process_ref: not_yet_defined_in_HRP-001
scope:
  summary: 'Master catalog of inventory items (consumables, reagents, supplies and
    equipment) used by laboratory operations. Owns and creates the InventoryItem aggregate
    (AGG-013) — the single shared aggregate for the whole Inventory and Internal Quality
    module — establishing item identity, classification and unit of measure. Delegates
    specific named field authority to 8 sibling capabilities within the same inventory-procurement
    bounded context (reagent profile, stock lots, stock movement summary, equipment
    profile, calibration and maintenance records), mirroring the AGG-008 Sample /
    AGG-009 LaboratoryResult delegated-ownership pattern established by MVP-MOD-006/MVP-MOD-007.

    '
  in_scope:
  - 'InventoryItem aggregate (AGG-013): identity, classification, itemType, unit of
    measure, status.'
  - stockSummary rollup field placeholder (onHandQuantity, reservedQuantity, reorder
    thresholds), mutated only by delegated Apply* commands from BCM-INV-005/006/007/008/009.
  - reagentProfile and equipmentProfile field placeholders, mutated only by BCM-INV-002
    and BCM-QLT-004 respectively.
  - Item lifecycle (active, inactive, discontinued) and uniqueness of itemCode per
    tenant/laboratory/branch scope.
  out_of_scope:
  - Stock lot metadata and expiration/storage tracking (BCM-INV-003).
  - Reagent-specific test linkage (BCM-INV-002).
  - Any stock quantity movement (BCM-INV-005/006/007/008/009).
  - Equipment asset profile, calibration and maintenance detail (BCM-QLT-003/004/005).
  - Supplier master data — AGG-014 Supplier is owned by the not-yet-modeled BCM-PER-006
    Supplier Management; this package never creates or mutates Supplier records.
roadmap:
  module: COM-MOD-010
  release: REL-002
  package_status: module_closed
  next_backlog_item: none (module closed; see COM-MOD-011-DEF for the next roadmap
    module)
  paused_functional_backlog_item: null
dependencies:
  required_capabilities:
  - BCM-SVC-002
  - BCM-SVC-007
  - BCM-ORG-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-009
  - BCM-AI-001
  downstream_capabilities:
  - BCM-INV-002
  - BCM-INV-004
  upstream_contexts:
  - inventory-procurement
  - identity-access
  - audit-compliance
  - organization-management
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
