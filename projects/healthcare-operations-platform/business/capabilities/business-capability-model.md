---
id: BCM-001
name: Nexora Business Capability Model
version: 0.1.0
status: Draft
owner: Business Architecture
---

# Business Capability Model

Nexora is organized around reusable business capabilities. A capability can be consumed by web, mobile, public APIs, AI agents, automations and integrations without duplicating business rules.

## Level 1 Capabilities

| ID | Capability | Type | MVP |
|---|---|---|---|
| CAP-001 | Patient Management | Core | MVP 1 |
| CAP-002 | Doctor Management | Core | MVP 1 |
| CAP-003 | Organization and Branch Management | Core | MVP 1 |
| CAP-004 | Identity and Access Management | Core | MVP 1 |
| CAP-005 | Catalog Management | Supporting | MVP 1 |
| CAP-006 | Test and Study Configuration | Core | MVP 1 |
| CAP-007 | Order Management | Core | MVP 1 |
| CAP-008 | Sample Collection | Core | MVP 1 |
| CAP-009 | Result Management | Core | MVP 1 |
| CAP-010 | Cashier and Sales Management | Core | MVP 1 |
| CAP-011 | Billing and Tax Compliance | Supporting | MVP 1 |
| CAP-012 | Patient Portal | Channel | MVP 1 |
| CAP-013 | Doctor Portal | Channel | MVP 1 |
| CAP-014 | Notifications | Supporting | MVP 1 |
| CAP-015 | Audit and Security | Foundation | MVP 1 |
| CAP-016 | Inventory Management | Core | MVP 2 |
| CAP-017 | Supplier Management | Supporting | MVP 2 |
| CAP-018 | Appointment Scheduling | Core | MVP 2 |
| CAP-019 | Laboratory Equipment Interfaces | Differentiating | MVP 2 |
| CAP-020 | Imaging Operations | Core | MVP 3 |
| CAP-021 | DICOM/PACS Management | Differentiating | MVP 3 |
| CAP-022 | AI Assistance | Differentiating | MVP 3 |
| CAP-023 | Workflow Automation | Differentiating | MVP 4 |
| CAP-024 | Analytics and BI | Differentiating | MVP 4 |
| CAP-025 | Marketplace and Extensions | Differentiating | MVP 5 |

## Capability Design Principles

1. Capabilities own business rules.
2. Channels must not duplicate domain logic.
3. APIs expose capabilities; they do not define the business model.
4. Capabilities are deployable as part of a modular monolith, microservice or serverless unit depending on deployment profile.
5. Each capability must define traceability to processes, events, entities, OpenAPI contracts, UI, mobile and QA.
