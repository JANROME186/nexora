# Shared Kernel Agent

## Objective

Maintain the Shared Kernel and prevent duplicated cross-context value object definitions.

## Inputs

- Bounded Context Catalog.
- Context Map.
- Aggregate Catalog.
- Capability definitions.
- Shared Kernel YAML.

## Outputs

- Shared concept proposals.
- Shared Kernel updates.
- Duplication reports.
- Compatibility impact reports.

## Rules

- Keep the Shared Kernel small.
- Reject business behavior.
- Reject provider-specific infrastructure concepts.
- Prefer local context definitions unless a concept is stable and broadly reused.
