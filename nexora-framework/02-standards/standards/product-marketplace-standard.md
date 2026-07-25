# Nexora Product Marketplace Standard

**Artifact ID:** `NXF-MKT-STD-001`
**Status:** Approved
**Version:** `1.0.0`

## Purpose

This standard defines how Nexora products expose commercial marketplace capabilities so customers can discover, buy, install, activate, update, consume and retire product functionality packages without changing the product core.

Marketplace is part of Nexora's product model. It is not a one-off customer customization layer.

## Scope

The standard applies to:

- Nexora Definition models.
- Business Capability Packages.
- Commercial product packages.
- Marketplace catalogs.
- License plans and tenant entitlements.
- Feature flags.
- Extension points.
- Billing-provider adapters.
- Installation, upgrade, rollback and retirement workflows.

It excludes hard-coded pricing, provider-specific payment logic as source-of-truth, and any package that bypasses security, audit, privacy or regulated clinical controls.

## Architecture

Marketplace capability is built from these model-level components:

| Component | Responsibility |
| --- | --- |
| Marketplace Catalog | Published package metadata, versions, offers and compatibility rules. |
| Commercial Offer | Priceable offers, bundles, trials, limits and purchasing terms. |
| License Plan | Plan-level entitlements and quotas. |
| Entitlement Service | Tenant access resolution from purchases, trials, grants and license plans. |
| Installation Service | Install, activate, update, rollback, suspend and uninstall packages per tenant. |
| Feature Flag Service | Progressive rollout, preview access, kill switches and activation states. |
| Compatibility Service | Product version, package version, dependency and extension point validation. |
| Billing Adapter | Replaceable billing, payment and subscription provider boundary. |
| Audit and Observability | Package lifecycle, entitlement, installation and consumption evidence. |

## Package Types

Marketplace packages can be:

- Capability packages.
- Integration adapters.
- UI extensions.
- Mobile extensions.
- AI extensions.
- Report templates.
- Country packs.
- Data ingestion adapters.
- Workflow templates.

Each package must map to Business Capability Packages or explicit extension points.

## Required Package Artifacts

A marketplace-ready package must include:

- `marketplace-package.md`
- `package-manifest.md`
- `commercial-offer.md`
- `license-plan.md`
- `entitlement-policy.md`
- `compatibility.md`
- `installation-model.md`
- `upgrade-model.md`
- `security-review.md`
- `support-model.md`
- `telemetry-model.md`
- `traceability.md`
- `README.md`

## Lifecycle

```text
Modeled -> Validated -> Certified -> Published -> Purchased -> Entitled -> Installed -> Activated -> Monitored -> Updated -> Suspended -> Retired
```

## Rules

1. Marketplace capability is part of the product model, not a custom integration afterthought.
2. Customers buy offers; tenants receive entitlements; installations activate capabilities.
3. Billing, payment, tax and procurement providers are replaceable adapters.
4. Entitlement checks must be centralized and must not be hard-coded in controllers, UI components or domain logic.
5. Marketplace packages must be tenant-installable, versioned, auditable and reversible where practical.
6. Marketplace packages must remain agent-agnostic, cloud-agnostic and vendor-agnostic.
7. Regulated product behavior must still pass IAM, permissions, audit, privacy, consent and clinical authority controls.

## Validation Gates

| Gate | Required Evidence |
| --- | --- |
| Source model completeness | Required package artifacts exist and map to capabilities or extension points. |
| Commercial readiness | Offer, license, entitlement, trial, quota and billing event models are defined. |
| Installation safety | Install, activation, upgrade, rollback, suspend and uninstall behavior are defined. |
| Security and compliance | Permissions, data scopes, audit events and privacy impacts are documented. |
| Operational observability | Lifecycle, entitlement, installation and consumption events are observable. |

## Agent Usage

Agents must load this standard before defining marketplace-ready product capabilities.

Agents must treat marketplace artifacts as editable models under Nexora Definition. Platform outputs are generated from those models when generators exist.

Agents must not implement marketplace packages as one-off customer customizations, depend on a specific agent, marketplace provider, cloud provider or payment provider, or activate purchased functionality without entitlement, authorization and audit checks.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-MKT-STD-001
  type: framework-standard
  name: Nexora Product Marketplace Standard
  version: 1.0.0
  status: approved
  human_readable: product-marketplace-standard.md
  machine_readable: product-marketplace-standard.md
  owner: Nexora Product Architecture
purpose: 'Define how Nexora products expose commercial marketplace capabilities so
  customers can discover, buy, install, activate, update, consume and retire product
  functionality packages without changing the product core.

  '
scope:
  applies_to:
  - Nexora Definition models
  - Business Capability Packages
  - Commercial product packages
  - Tenant entitlements
  - License plans
  - Feature flags
  - Marketplace catalogs
  - Extension points
  - Installation and upgrade workflows
  - Billing-provider adapters
  excludes:
  - Hard-coded pricing inside application code
  - Vendor-specific payment dependencies as source-of-truth
  - Agent-specific packaging instructions
  - Marketplace features that bypass security, audit, privacy or clinical controls
principles:
- Marketplace capability is part of the product model, not a custom integration afterthought.
- Commercial packages must map to Business Capability Packages or explicit extension
  points.
- Customers buy offers; tenants receive entitlements; installations activate capabilities.
- Billing, payment, tax and procurement providers are replaceable adapters.
- Marketplace packages must be tenant-installable, versioned, auditable and reversible
  where practical.
- Entitlement checks must be centralized and must not be hard-coded in controllers,
  UI components or domain logic.
- Marketplace packages must remain agent-agnostic, cloud-agnostic and vendor-agnostic.
- Regulated product behavior must never be enabled only by purchase; permissions,
  compliance, audit and clinical authority still apply.
marketplace_architecture:
  actors:
  - id: package_publisher
    description: Creates and submits package models for certification and publication.
  - id: marketplace_operator
    description: Reviews, certifies, publishes, suspends or retires packages.
  - id: customer_buyer
    description: Selects commercial offers for a customer organization.
  - id: tenant_administrator
    description: Installs, activates, configures or removes packages for a tenant.
  - id: end_user
    description: Consumes activated capabilities through authorized product surfaces.
  components:
  - id: marketplace_catalog
    responsibility: Stores published package metadata, versions, offers and compatibility
      rules.
  - id: commercial_offer
    responsibility: Defines priceable offers, bundles, trials, limits and purchasing
      terms.
  - id: license_plan
    responsibility: Defines plan-level entitlements and quotas.
  - id: entitlement_service
    responsibility: Resolves tenant capability access from purchases, trials, grants
      and license plans.
  - id: installation_service
    responsibility: Installs, activates, updates, rolls back, suspends and uninstalls
      packages per tenant.
  - id: feature_flag_service
    responsibility: Controls progressive rollout, preview access, kill switches and
      activation states.
  - id: compatibility_service
    responsibility: Validates product version, package version, dependencies and extension
      point compatibility.
  - id: billing_adapter
    responsibility: Publishes billable events and consumes subscription/payment state
      through replaceable providers.
  - id: audit_observability
    responsibility: Captures package lifecycle, entitlement, installation and consumption
      events.
package_types:
- id: capability_package
  description: Adds or extends one or more Business Capabilities.
- id: integration_adapter
  description: Adds an external system connector behind an anti-corruption boundary.
- id: ui_extension
  description: Adds product surface components through declared extension points.
- id: mobile_extension
  description: Adds mobile app capability through declared mobile extension points.
- id: ai_extension
  description: Adds provider-agnostic AI overlay capabilities with safety controls.
- id: report_template
  description: Adds generated reports, dashboards or document templates.
- id: country_pack
  description: Adds country-specific fiscal, regulatory, language or operational behavior.
- id: data_ingestion_adapter
  description: Adds legacy data import mappings for simple open formats.
- id: workflow_template
  description: Adds configurable workflow definitions using approved process models.
required_package_artifacts:
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
- traceability.md
- README.md
package_lifecycle:
- modeled
- validated
- certified
- published
- purchased
- entitled
- installed
- activated
- monitored
- updated
- suspended
- retired
execution_flow:
- model_package
- validate_package
- certify_package
- publish_offer
- purchase_offer
- grant_entitlement
- install_package
- activate_features
- monitor_consumption
- update_or_rollback
- retire_package
modeling_rules:
- Every package must declare capabilities, extension points, permissions, events,
  data access, tenant scopes and operational dependencies.
- Every package must declare whether it is free, paid, trial, bundled, private, partner-only
  or internal.
- Every package must define compatibility against product version, platform services,
  APIs, data model version and required packages.
- Every package must define install, upgrade, rollback, suspend and uninstall behavior.
- Every package must declare generated outputs and custom implementation points.
- Every package must declare support ownership, support tier, telemetry and customer-facing
  documentation.
- Every protected capability must be checked through entitlement and authorization
  policy before execution.
- Billing-provider events must not become the source of truth for clinical, financial
  or operational domain state.
validation_gates:
- id: MKT-GATE-001
  name: Source model completeness
  criteria:
  - Required package artifacts exist.
  - Package maps to Business Capability Packages or approved extension points.
  - Human-readable Markdown and machine-readable YAML companions exist where applicable.
- id: MKT-GATE-002
  name: Commercial readiness
  criteria:
  - Offer, license, entitlement, trial, quota and billing event model are defined.
  - Purchase and entitlement flows are separated.
  - Customer onboarding and support expectations are defined.
- id: MKT-GATE-003
  name: Installation safety
  criteria:
  - Install, activation, upgrade, rollback, suspend and uninstall behavior are defined.
  - Compatibility and dependency checks are executable before activation.
  - Data migrations are reversible where practical or explicitly migration-bound.
- id: MKT-GATE-004
  name: Security and compliance
  criteria:
  - Package permissions, data scopes, audit events and privacy impacts are documented.
  - Package cannot bypass IAM, audit, retention, consent or regulated clinical controls.
- id: MKT-GATE-005
  name: Operational observability
  criteria:
  - Lifecycle, entitlement, installation and consumption events are observable.
  - Failure, rollback and support runbooks are defined.
agent_usage:
  required_behavior:
  - Load this standard before defining marketplace-ready product capabilities.
  - Treat marketplace artifacts as editable models under Nexora Definition.
  - Generate platform outputs from package models where generators exist.
  - Preserve provider-agnostic billing, payment and subscription boundaries.
  prohibited_behavior:
  - Do not implement marketplace packages as one-off customer customizations.
  - Do not depend on a specific agent, marketplace provider, cloud provider or payment
    provider.
  - Do not activate purchased functionality without entitlement, authorization and
    audit checks.
```
