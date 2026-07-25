# Marketplace Architecture

**Artifact ID:** MKT-001
**Status:** Approved
**Version:** 1.0.0

Framework standard:

`../../../../../nexora-framework/02-standards/standards/product-marketplace-standard.md`

Project contract:

`../../../05-contracts/marketplace/product-marketplace/product-marketplace-contract.md`

## Purpose

The Nexora Marketplace allows the platform to evolve through extensions without modifying the core product for every customer, country, provider or healthcare vertical.

## Marketplace Extension Types

| Type | Examples |
|---|---|
| Connector | SAT México, DIAN Colombia, laboratory device connectors, payment gateways. |
| Country Pack | Mexico, Colombia, Peru, Chile. |
| Healthcare Pack | Clinical Lab, Imaging, Pathology, Blood Bank, Veterinary. |
| AI Pack | OCR, result explanation, inventory prediction, appointment assistant. |
| Report Pack | Regulatory reports, executive dashboards, branch scorecards. |
| Workflow Pack | Sample routing, critical results, corporate billing. |
| Data Ingestion Adapter | Legacy import mapping, validation and reconciliation packages. |
| Capability Package | Optional product capabilities modeled as Business Capability Packages. |

## Extension Principles

1. Extensions must be isolated from core domain logic.
2. Extensions must declare capabilities, permissions, events and data access.
3. Extensions must be versioned.
4. Extensions must be installable per tenant.
5. Extensions must be auditable.
6. Extensions must support rollback.
7. Customers buy offers, tenants receive entitlements and installations activate packages.
8. Marketplace packages must remain provider-agnostic and agent-agnostic.

## Marketplace Lifecycle

```text
Modeled -> Validated -> Certified -> Published -> Purchased -> Entitled -> Installed -> Activated -> Updated -> Suspended -> Deprecated -> Removed
```

## Commercial Model

Marketplace uses these commercial objects:

- Marketplace package.
- Commercial offer.
- License plan.
- Tenant entitlement.
- Package installation.

Package consumption must be gated by centralized entitlement checks, IAM authorization, compatibility validation, audit events and rollback or migration-bound uninstall behavior.

## MVP 1 Scope

MVP 1 does not need a full marketplace UI. It must define extension metadata and installation concepts so the platform is ready to evolve.

## Commercial GA Scope

Commercial GA must support package catalog publication, offer and entitlement models, tenant installation and activation, runtime entitlement checks, lifecycle events, billing events and operational observability.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: MKT-001
name: Marketplace Architecture
type: marketplace-architecture
version: 1.0.0
status: approved
framework_standard: ../../../../../nexora-framework/02-standards/standards/product-marketplace-standard.md
project_contract: ../../../05-contracts/marketplace/product-marketplace/product-marketplace-contract.md
extension_types:
- connector
- country-pack
- healthcare-pack
- ai-pack
- report-pack
- workflow-pack
- data-ingestion-adapter
- capability-package
principles:
- Extensions must be isolated from core domain logic.
- Extensions must declare capabilities, permissions, events and data access.
- Extensions must be versioned.
- Extensions must be installable per tenant.
- Extensions must be auditable.
- Extensions must support rollback.
- Customers buy offers, tenants receive entitlements and installations activate packages.
- Marketplace packages must remain provider-agnostic and agent-agnostic.
lifecycle:
- modeled
- validated
- certified
- published
- purchased
- entitled
- installed
- activated
- updated
- suspended
- deprecated
- removed
commercial_model:
  objects:
  - marketplace_package
  - commercial_offer
  - license_plan
  - tenant_entitlement
  - package_installation
  required_controls:
  - centralized_entitlement_check
  - iam_authorization_check
  - audit_events
  - compatibility_validation
  - rollback_or_migration_bound_uninstall
mvp1_scope:
- Define extension metadata.
- Define tenant installation concept.
- Defer marketplace UI.
commercial_ga_scope:
- Publish package catalog.
- Define offer and entitlement models.
- Install and activate packages per tenant.
- Gate package consumption with entitlement and authorization checks.
- Emit lifecycle, billing and observability events.
```
