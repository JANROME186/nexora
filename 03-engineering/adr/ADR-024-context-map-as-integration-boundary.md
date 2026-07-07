# ADR-024 — Context Map as Official Integration Boundary

## Status

Approved

## Context

Nexora contains multiple business capabilities that reference common concepts such as Patient, Order, Branch, Result and Invoice.

Without explicit context boundaries, agents and developers may duplicate ownership, bypass domain rules or couple implementations incorrectly.

## Decision

The Context Map is the official integration boundary between bounded contexts.

All cross-context interaction must be declared through:
- Shared Kernel
- Published Language
- Customer/Supplier
- Anti-Corruption Layer
- Event Subscription
- Conformist relationship

## Consequences

- Capabilities cannot freely access other contexts.
- APIs, events and published language become mandatory for integration.
- Migration, AI and interoperability must use anti-corruption layers.
- The Context Map Validator becomes a required quality gate.
