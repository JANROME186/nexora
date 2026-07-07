# ADR-025 — Shared Kernel Governance

## Status

Approved

## Context

Nexora spans many bounded contexts. Some concepts must be reused consistently, but uncontrolled sharing can create tight coupling.

## Decision

Nexora will maintain a small governed Shared Kernel.

The Shared Kernel may include stable identifiers, value objects, audit metadata and localization primitives.

It must not contain business behavior, persistence details, provider-specific implementation or country-specific rules.

## Consequences

- Shared concepts are consistent across contexts.
- Cross-context coupling is reduced.
- Any change to shared concepts requires architecture review.
- Country-specific behavior remains outside the Shared Kernel.
