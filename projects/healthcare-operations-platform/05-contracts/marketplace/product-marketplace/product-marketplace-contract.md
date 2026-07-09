# HOP Product Marketplace and Entitlements Contract

**Artifact ID:** `HOP-MKT-001`  
**Capability:** `BCM-PLT-011 Product Marketplace and Entitlements`  
**Commercial module:** `COM-MOD-017 Product Marketplace and Extension Packaging`  
**Status:** Approved

## Purpose

This contract defines how Healthcare Operations Platform exposes commercial marketplace packages so customers can buy, install, activate, consume and update optional functionality without custom product forks.

It applies the Nexora Product Marketplace Standard:

`../../../../nexora-framework/02-standards/standards/product-marketplace-standard.yaml`

## Package Types

HOP marketplace packages may include:

- Laboratory capability extensions.
- Imaging packages.
- AI overlay packages.
- Country fiscal packs.
- Integration adapters.
- Report template packs.
- Data ingestion adapters.
- Workflow template packs.

## Commercial Objects

| Object | Purpose |
| --- | --- |
| Marketplace Package | Versioned package published to the HOP marketplace catalog. |
| Commercial Offer | Customer-facing offer that can be purchased, bundled, trialed or privately assigned. |
| Tenant Entitlement | Tenant-scoped permission to install or consume a package or capability. |
| Package Installation | Tenant-specific installed package instance and activation state. |

## Lifecycle

```text
Publish -> Certify -> Purchase -> Entitle -> Install -> Activate -> Suspend/Upgrade/Rollback/Uninstall -> Retire
```

## Guardrails

Marketplace purchase does not grant role permissions by itself.

Tenant entitlement must be evaluated together with IAM authorization before capability execution.

Clinical validation, result release and financial audit controls cannot be weakened by a package.

Country fiscal packs must remain adapter-based and cannot mutate core billing state directly.

AI packages must follow HOP AI platform guardrails and human-control rules.

Data ingestion adapters must follow the Open Data Ingestion Standard and produce validation and reconciliation reports.

Package lifecycle operations must emit audit events and observability telemetry.

## Definition Of Ready

- Nexora Product Marketplace Standard is loaded.
- `BCM-PLT-011` exists in BCM-001 and BCM-002.
- `COM-MOD-017` is present in the commercial backlog.
- Marketplace package models exist for the target package.
- Compatibility, entitlement, installation and rollback models are complete.

## Definition Of Done

- Published package can be discovered in the catalog.
- Offer can be purchased or granted through a provider-agnostic boundary.
- Tenant entitlement gates installation and runtime consumption.
- Package can be activated, suspended, upgraded and retired with audit evidence.
- Package behavior is observable and supportable.
