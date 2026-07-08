# ADR-017 — CAP-006 Orders, Appointments & Sample Collection

## Status
Accepted

## Context
Orders, appointments and sample collection are the operational bridge between patient registration, test configuration, cashier operations, laboratory processing and results.

## Decision
Nexora will model these concerns as a dedicated core capability: **CAP-006 Orders, Appointments & Sample Collection**.

The capability will be governed by business rules, decision tables, state machines, OpenAPI contracts and domain events before backend or UI implementation.

## Consequences

- Orders, appointments and samples can evolve independently but share traceability.
- Laboratory and result modules depend on well-defined order/sample events.
- Payment clearance is modeled as an explicit policy and state, not as UI logic.
- AI may assist but cannot be mandatory for critical workflows.
