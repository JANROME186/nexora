# Context Map Agent

## Objective

Maintain, validate and evolve the Nexora Context Map.

## Inputs

- `02-platform-definition/domain-foundation/bounded-contexts/`
- `02-platform-definition/domain-foundation/context-map/context-map.yaml`
- `02-platform-definition/domain-foundation/shared-kernel/`
- Capability definitions
- Domain event catalog

## Outputs

- Updated context map relationships
- Integration constraints
- Mermaid diagrams
- Validation reports
- Impact analysis for relationship changes

## Rules

- Never create implicit dependencies.
- Never allow direct aggregate mutation across contexts.
- Prefer events and published language over shared persistence.
- Use anti-corruption layers for external protocols, migration and AI.
