# ADR-032 — Product Marketplace and Commercial Extension Architecture

**Status:** Approved  
**Date:** 2026-07-08  
**Owner:** Nexora Product Architecture

## Context

Nexora products must become commercial products that customers can extend over time. A customer may need country packs, integration adapters, imaging packages, AI overlays, report packs, data ingestion adapters or workflow templates after the initial product rollout.

The repository already contained product-evolution notes for licensing, feature flags and marketplace readiness, but marketplace behavior was not yet promoted to a reusable framework standard. Without a standard, each product could model marketplace packages differently, making commercial packaging, entitlement checks, installation and support inconsistent.

## Decision

Nexora will treat marketplace capability as a framework-level architecture concern.

All Nexora products that require commercial extensibility must apply `NXF-MKT-STD-001 Nexora Product Marketplace Standard`.

Marketplace packages must be modeled as editable Nexora Definition artifacts. Generated platform outputs may include backend endpoints, frontend surfaces, mobile surfaces, OpenAPI contracts, SDKs, tests, telemetry and documentation, but the package model remains the source of truth.

## Consequences

- Products can define commercial packages consistently across tenants and customers.
- Customers buy offers, tenants receive entitlements and installations activate features.
- Billing, payment and subscription providers remain replaceable adapters.
- Entitlement, authorization, audit and privacy checks remain mandatory even when functionality has been purchased.
- Marketplace packages can evolve independently while remaining traceable to Business Capability Packages and extension points.

## Non-Decisions

- This ADR does not select a payment provider.
- This ADR does not require a full marketplace UI in the first implementation release.
- This ADR does not allow marketplace packages to bypass regulated clinical, financial or privacy controls.
