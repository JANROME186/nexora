# ADR-024 — Multi-Project Repository Structure

## Status

Accepted

## Context

Nexora needs a repository structure that separates the company framework from individual software solutions.

The first project, Healthcare Operations Platform, had grown at repository root. That made it harder for agents to know what belongs to Nexora as a company method and what belongs to one product.

## Decision

Adopt a multi-project repository structure:

- `nexora-framework/` contains reusable Nexora standards, recipes, templates, schemas, governance and agent guidance.
- `projects/` contains self-contained product or solution folders.
- Each project must include its own `PROJECT_BRIEF.md`, `PROJECT_STATE.yaml` and `SOURCE_OF_TRUTH.yaml`.
- Project-specific artifacts must not be placed at repository root.

## Consequences

Agents can now start a new solution by adding a new folder under `projects/` and applying the Nexora Agent-to-MVP Recipe.

Healthcare Operations Platform becomes the first project under:

`projects/healthcare-operations-platform/`

Root-level state describes the repository and framework, not the product implementation.

## Related Artifacts

- `nexora-framework/02-standards/standards/project-folder-standard.yaml`
- `nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.yaml`
- `projects/healthcare-operations-platform/PROJECT_BRIEF.md`
