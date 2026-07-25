---
id: HOP-CAP-PKG-BCM-ATT-008
format: markdown_structured_payload
type: capability-package
name: Billing Request Management Capability Package
version: 0.4.0
status: module_closed
---

# Billing Request Management Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-ATT-008
  type: capability-package
  name: Billing Request Management Capability Package
  version: 0.4.0
  status: module_closed
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-16
  roadmap_group: MVP-MOD-005
  execution_flow_stage: release
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-ATT-008
  name:
    en: Billing Request Management
    es: Facturación
  domain: DOM-04 Care Delivery
  priority: Critical
  roadmap: MVP1
  dependency_profile: revenue_cycle
  bounded_context: billing-tax
  secondary_bounded_context: cash-sales
  primary_aggregate: AGG-012 Invoice
  aggregate_alias: InvoiceRequest
  process_ref: HRP-001-P04
scope:
  summary: 'Captures provider-agnostic fiscal billing requests from paid or payable
    sales, validates fiscal profile snapshots, maintains request lifecycle and delegates
    country-specific invoice issuance to fiscal adapter implementations.

    '
  in_scope:
  - Invoice request creation from Sale.
  - Fiscal profile snapshot capture and validation.
  - Provider-agnostic billing adapter boundary.
  - Request lifecycle: requested, submitted, issued, failed, cancelled.
  out_of_scope:
  - Country-specific tax authority connector implementation.
  - Sale payment mutation (BCM-ATT-005).
  - Patient master-data mutation (BCM-PER-002).
  - Fiscal document rendering beyond adapter response capture.
roadmap:
  module: MVP-MOD-005
  release: REL-001
  package_status: module_closed
  next_backlog_item: none (module closed; see MVP-MOD-006-DEF for the next roadmap
    module)
dependencies:
  required_capabilities:
  - BCM-ATT-005
  - BCM-PER-002
  - BCM-PLT-001
  - BCM-PLT-007
  optional_capabilities:
  - BCM-PLT-004
  - BCM-RES-004
  downstream_capabilities:
  - BCM-RES-004
  upstream_contexts:
  - cash-sales
  - patient-management
  - audit-compliance
product_surfaces:
  backend: required
  employee_portal: required
  patient_portal: payment_history_later
  doctor_portal: not_required
  mobile_app: payment_receipt_later
  public_website: not_required
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
