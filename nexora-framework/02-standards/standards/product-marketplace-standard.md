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

- `marketplace-package.yaml`
- `package-manifest.yaml`
- `commercial-offer.yaml`
- `license-plan.yaml`
- `entitlement-policy.yaml`
- `compatibility.yaml`
- `installation-model.yaml`
- `upgrade-model.yaml`
- `security-review.yaml`
- `support-model.yaml`
- `telemetry-model.yaml`
- `traceability.yaml`
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
