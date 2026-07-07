# ADR-004 Human and Machine Readable Specification

## Status

Accepted

## Context

Nexora must be understandable by humans and consumable by AI agents, validators, generators and automation pipelines.

Markdown alone is excellent for collaboration, but insufficient for deterministic automation.

## Decision

Every major artifact must have a human-readable representation and, where applicable, a machine-readable representation.

Markdown will be used for human collaboration. YAML or JSON will be used for automation, validation and knowledge graph construction.

## Consequences

- Artifacts become traceable and computable.
- AI agents can load precise context.
- Validation pipelines can detect inconsistencies.
- More discipline is required when creating or changing artifacts.
