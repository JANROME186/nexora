# ADR-028 — Separate Nexora Company from Healthcare Operations Platform Product

## Status

Approved

## Context

Previous repository material sometimes treated Nexora as both the company and the healthcare product.

This may confuse AI agents and future contributors.

## Decision

Nexora is officially defined as a software development and Artificial Intelligence company.

The first product developed by Nexora is the **Healthcare Operations Platform**.

The product may be commercially presented as **Nexora Healthcare Operations Platform**, but the company and product must remain conceptually separate.

## Consequences

- Company-level strategy belongs under `01-enterprise/company`.
- Product-level definition belongs under `02-platform-definition/products/healthcare-operations-platform`.
- Future products may be added without redefining Nexora.
- AI agents must not treat Nexora as only a healthcare product.
