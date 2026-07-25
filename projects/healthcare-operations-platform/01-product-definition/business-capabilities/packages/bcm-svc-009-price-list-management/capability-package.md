---
id: HOP-CAP-PKG-BCM-SVC-009
format: markdown_structured_payload
type: capability-package
name: Price List Management Capability Package
version: 0.1.0
status: modeled
---

# Price List Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-SVC-009
  type: capability-package
  name: Price List Management Capability Package
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
  id: BCM-SVC-009
  name:
    en: Price List Management
    es: Tarifario
  domain: DOM-03 Diagnostic Services
  priority: High
  roadmap: MVP1
  dependency_profile: catalog
  bounded_context: catalog-test-configuration
  secondary_bounded_context: cash-sales
  primary_aggregate: TestDefinition
  aggregate_ref: AGG-006
  secondary_aggregate_ref: AGG-010
scope:
  summary: 'Defines version-aware price lists that assign prices to services, tests
    and panels with currency, effective dating and optional agreement segmentation.
    Published price snapshots are consumed by quotations, cashier operations and billing
    requests.

    '
  in_scope:
  - Price list definition and price entries per catalog item.
  - Currency and effective-dated price versioning.
  - Optional agreement or payer price segmentation reference.
  - Published price snapshot exposure for quotation, cash and billing.
  out_of_scope:
  - Quotation generation (BCM-ATT-006).
  - Cashier payment processing (BCM-ATT-005).
  - Fiscal invoicing (BCM-ATT-008).
  - Promotions (BCM-ATT-007, MVP2).
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
  - BCM-SVC-001
  optional_capabilities:
  - BCM-PER-005
  downstream_capabilities:
  - BCM-ATT-005
  - BCM-ATT-006
  - BCM-ATT-008
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
