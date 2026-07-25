# NEXORA — AGENT BOOTSTRAP

Load root source of truth, Nexora framework standards, then the target project folder.

## Repository Model

- `nexora-framework/` defines the reusable Nexora method.
- `projects/` contains self-contained solutions.
- `projects/healthcare-operations-platform/` is the first project and is ready for MVP development.

## Required Loading Order

1. `SOURCE_OF_TRUTH.md`
2. `PROJECT_STATE.md`
3. `nexora-framework/README.md`
4. `nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md`
5. Target project `SOURCE_OF_TRUTH.md`
6. Target project `PROJECT_BRIEF.md`
7. Target project `PROJECT_STATE.md`

## Current HOP Start Point

Implement:

`projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/`

## New Project Start Point

Create:

`projects/<project-slug>/PROJECT_BRIEF.md`

Then apply:

`nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md`
