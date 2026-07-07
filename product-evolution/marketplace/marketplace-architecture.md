# Marketplace Architecture

**Artifact ID:** MKT-001  
**Status:** Draft  
**Version:** 0.22.0

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

## Extension Principles

1. Extensions must be isolated from core domain logic.
2. Extensions must declare capabilities, permissions, events and data access.
3. Extensions must be versioned.
4. Extensions must be installable per tenant.
5. Extensions must be auditable.
6. Extensions must support rollback.

## Marketplace Lifecycle

```text
Draft -> Review -> Certified -> Published -> Installed -> Updated -> Deprecated -> Removed
```

## MVP 1 Scope

MVP 1 does not need a full marketplace UI. It must define extension metadata and installation concepts so the platform is ready to evolve.
