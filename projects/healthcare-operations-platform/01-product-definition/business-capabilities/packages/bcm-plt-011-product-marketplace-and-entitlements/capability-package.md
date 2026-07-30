---
id: HOP-CAP-PKG-BCM-PLT-011
format: markdown_structured_payload
type: capability-package
name: Product Marketplace and Entitlements Capability Package
version: 1.1.0
status: compiled
---

# Product Marketplace And Entitlements Capability Package

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-CAP-PKG-BCM-PLT-011
  type: capability-package
  name: Product Marketplace and Entitlements Capability Package
  version: 1.1.0
  status: compiled
  classification: editable_model
  human_readable: README.md
  machine_readable: capability-package.md
  owner: Nexora Product Architecture Team
  created_date: 2026-07-24
  roadmap_group: COM-MOD-017
  execution_flow_stage: model
standard:
  capability_package_standard: ../../../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  marketplace_standard: ../../../../../../nexora-framework/02-standards/standards/product-marketplace-standard.md
  mdpe_standard: ../../../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  agent_agnostic_standard: ../../../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
capability:
  id: BCM-PLT-011
  name:
    en: Product Marketplace and Entitlements
    es: Marketplace de Producto y Derechos de Uso
  domain: DOM-10 Platform
  priority: High
  roadmap: MVP2
  dependency_profile: commercial_extension_foundation
  bounded_context: marketplace-entitlements
  primary_aggregate: MarketplacePackage
scope:
  summary: Defines HOP's provider-agnostic product marketplace model for publishing
    packages, commercial offers, tenant entitlements, installation lifecycle, compatibility,
    billing-adapter boundaries, support ownership and observable package consumption.
  in_scope:
  - Marketplace package catalog and version metadata.
  - Commercial offer, trial, bundle, plan and quota models.
  - Tenant entitlement grants, revocations, expiration and usage limits.
  - Package installation, activation, upgrade, rollback, suspension, uninstall and
    retirement.
  - Provider-agnostic billing event boundary and subscription-state intake.
  - Marketplace package compatibility with HOP product version, dependencies and extension
    points.
  - Entitlement-aware surfaces for backend, employee portal, public website, patient
    portal, doctor portal and mobile app.
  out_of_scope:
  - Payment-provider-specific checkout implementation.
  - Hard-coded pricing inside application source code.
  - One-off customer customization outside package models.
  - Clinical authority, IAM, audit, consent or privacy bypass.
roadmap:
  module: COM-MOD-017
  release: REL-003
  package_status: module_closed
  next_backlog_item: none (module closed; COM-MOD-017-WEB-001 public marketplace
    listing surface compiled and validated in HOP-HARD-WEB-001; TD-WEB-001 closed)
  paused_functional_backlog_item: null
dependencies:
  required_capabilities:
  - BCM-PLT-001
  - BCM-PLT-002
  - BCM-PLT-005
  - BCM-PLT-006
  - BCM-PLT-007
  - BCM-PLT-009
  commercial_dependencies:
  - COM-MOD-016
  optional_capabilities:
  - BCM-PLT-010
  - BCM-IMG-001
  - BCM-AI-001
product_surfaces:
  backend: required
  employee_portal: admin_required
  public_website: marketplace_listing_required
  patient_portal: entitlement_aware
  doctor_portal: entitlement_aware
  mobile_app: entitlement_aware
  billing_adapter: required
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
marketplace_specific_artifacts:
- marketplace-package.md
- package-manifest.md
- commercial-offer.md
- license-plan.md
- entitlement-policy.md
- compatibility.md
- installation-model.md
- upgrade-model.md
- security-review.md
- support-model.md
- telemetry-model.md
```
