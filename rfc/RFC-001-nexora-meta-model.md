# RFC-001 Nexora Meta Model

## Summary

Introduce the Nexora Meta Model as the computable representation of the product specification.

## Motivation

Nexora will contain thousands of related artifacts. A meta model allows the project to maintain traceability, automate validation and support AI-assisted engineering without depending on a specific agent.

## Proposal

Define artifact types, ID conventions, schemas, source-of-truth mappings and dual-format documentation.

## Initial Scope

- Business capabilities.
- Business processes.
- Business rules.
- Domains.
- Entities.
- Events.
- User stories.
- API contracts.
- UI/mobile screens.
- QA tests.
- Traceability records.

## Open Questions

- Which validations should block pull requests?
- Which artifacts should be generated automatically?
- Should the knowledge graph be stored only as YAML or also exported to a graph database later?
