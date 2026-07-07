# ADR-026 — Aggregate Ownership Governance

## Status

Approved

## Context

Nexora capabilities reference common concepts such as Patient, Order, Result, Invoice and Branch. Without explicit aggregate ownership, duplicated state and inconsistent business rules may appear.

## Decision

Nexora will maintain an official Aggregate Catalog.

Every aggregate root must have exactly one owning bounded context. Other contexts may reference the aggregate by ID, snapshot, projection, API, command or domain event, but cannot mutate its state directly.

## Consequences

- Business invariants are protected.
- Cross-context coupling is reduced.
- Agents must consult the Aggregate Catalog before generating backend, APIs, database models or migrations.
- Migration, AI and integration processes must use commands and validation paths.
