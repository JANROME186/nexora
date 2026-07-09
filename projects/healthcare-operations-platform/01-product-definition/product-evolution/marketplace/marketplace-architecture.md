# Marketplace Architecture

**Artifact ID:** MKT-001
**Status:** Approved
**Version:** 1.0.0

Framework standard:

`../../../../../nexora-framework/02-standards/standards/product-marketplace-standard.yaml`

Project contract:

`../../../05-contracts/marketplace/product-marketplace/product-marketplace-contract.yaml`

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
