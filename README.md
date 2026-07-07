# Nexora

Nexora is a software development and Artificial Intelligence company.

This repository defines how Nexora documents, governs and prepares software solutions for AI-agent-assisted development.

All framework and project artifacts are agent agnostic: no project may require a named coding agent, assistant, platform prompt, model vendor or runtime-specific configuration to understand or implement the solution.

## Repository Structure

| Path | Purpose |
| --- | --- |
| `nexora-framework/` | Nexora company framework, documentation standards, agent recipes, templates, schemas and governance assets. |
| `projects/` | Self-contained product or solution projects. Each project has its own brief, state, source of truth and implementation readiness package. |
| `projects/healthcare-operations-platform/` | First Nexora project and current MVP-ready solution definition. |

## How New Projects Start

1. Create a folder under `projects/<project-slug>/`.
2. Add `PROJECT_BRIEF.md` with the user need and project description.
3. Apply `nexora-framework/recipes/agent-to-mvp-recipe.md`.
4. Generate the required source artifacts until `PROJECT_STATE.yaml` has no blocking definition gaps.
5. Start implementation from the first module definition package.

## Current First Project

Healthcare Operations Platform is ready to start development of:

`MVP-MOD-001 Platform Foundation`

Project folder:

`projects/healthcare-operations-platform/`

## Agent Entry Points

- `AGENT_BOOTSTRAP.md`
- `nexora-framework/README.md`
- `nexora-framework/standards/agent-agnostic-standard.md`
- `nexora-framework/recipes/agent-to-mvp-recipe.md`
- `projects/healthcare-operations-platform/PROJECT_BRIEF.md`
