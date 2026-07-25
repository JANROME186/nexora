---
id: HOP-CAP-PKG-BCM-QLT-001
format: markdown_structured_payload
type: capability-package
name: Internal Quality Controls Capability Package
version: 0.1.0
status: modeled
---

# Internal Quality Controls Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-QLT-001
  type: capability-package
  name: Internal Quality Controls Capability Package
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
  id: BCM-QLT-001
  name:
    en: Internal Quality Controls
    es: Controles Internos
  domain: DOM-09 Quality
  priority: Critical
  roadmap: MVP2
  dependency_profile: inventory_quality
  bounded_context: inventory-procurement
  primary_aggregate: QualityControlRun (new entity owned by this capability)
  process_ref: not_yet_defined_in_HRP-001
scope:
  summary: 'Records internal quality control runs performed with control-material
    reagent lots (owned by BCM-INV-001/BCM-INV-003), evaluates acceptance/rejection
    using Westgard-style rules and links the outcome, read-only, to the patient results
    it validates (AGG-009 LaboratoryResult, owned by laboratory-results). Introduces
    QualityControlRun as a new, non-duplicating entity — it never mutates InventoryItem,
    StockLot or LaboratoryResult. Out-of-control outcomes are surfaced to BCM-LAB-008
    Technical Validation (a capability outside COM-MOD-010, already closed under MVP-MOD-006)
    as a downstream signal; enforcing a release block based on that signal remains
    that capability''s own responsibility, not this one''s.

    '
  in_scope:
  - 'QualityControlRun: measured value, expected range, Westgard-style rule evaluation,
    acceptance decision.'
  - Read-only reference to the control-material StockLot and any linked LaboratoryResult
    batch.
  out_of_scope:
  - InventoryItem/StockLot mutation (BCM-INV-001/003; this capability only reads the
    control-material lot).
  - LaboratoryResult mutation of any kind (owned exclusively by laboratory-results/BCM-LAB-006/008/009/010).
  - External quality control / proficiency testing (BCM-QLT-002, roadmap MVP3, not
    part of COM-MOD-010).
roadmap:
  module: COM-MOD-010
  release: REL-002
  package_status: module_closed
  next_backlog_item: none (module closed; see COM-MOD-011-DEF for the next roadmap
    module)
  paused_functional_backlog_item: null
dependencies:
  required_capabilities:
  - BCM-INV-001
  - BCM-INV-003
  - BCM-INV-007
  - BCM-SVC-002
  - BCM-SVC-007
  - BCM-ORG-003
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-009
  - BCM-AI-001
  downstream_capabilities:
  - BCM-LAB-008
  upstream_contexts:
  - inventory-procurement
  - laboratory-results
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
