# ADR-021: Inventory as Immutable Stock Ledger

## Status

Accepted

## Context

Inventory operations require auditability, clinical traceability and reliable stock balances across branches and warehouses.

## Decision

Nexora will model inventory changes as immutable stock movements. Current stock balances are derived from posted movements and may be materialized for performance.

## Consequences

- Posted movements cannot be edited.
- Corrections require reversal or adjustment movements.
- Auditing and compliance become easier.
- Balance recalculation must be supported.
