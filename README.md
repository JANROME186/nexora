# Nexora

Nexora is a software development and Artificial Intelligence company.

This repository defines how Nexora documents, governs and prepares software solutions for AI-agent-assisted development.

All framework and project artifacts are agent agnostic: no project may require a named coding agent, assistant, platform prompt, model vendor or runtime-specific configuration to understand or implement the solution.

Agents must use the Nexora framework to scan `projects/`, validate whether each project has already been analyzed, and complete missing definitions before handing work to specialized subagents.

## Repository Structure

| Path | Purpose |
| --- | --- |
| `nexora-framework/` | Nexora company framework, documentation standards, agent recipes, templates, schemas and governance assets. |
| `projects/` | Self-contained product or solution projects. Each project has its own brief, state, source of truth and implementation readiness package. |
| `projects/healthcare-operations-platform/` | First Nexora project and current MVP implementation target. |

## How New Projects Start

1. Create a folder under `projects/<project-slug>/`.
2. The requester provides `BUSINESS_REQUIREMENT.md` with the high-level business requirement.
3. Structure it into `PROJECT_BRIEF.md`.
4. Apply `nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.md`.
5. Apply `nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md`.
6. Generate the required source artifacts until `PROJECT_STATE.yaml` has no blocking definition gaps.
7. Start implementation from the first module definition package.

For detailed step-by-step usage, analysis-agent instructions, validation gates and development-agent handoff, read:

`NEXORA_FRAMEWORK_USAGE_GUIDE.md`

## Current First Project

Healthcare Operations Platform has started development of:

`MVP-MOD-001 Platform Foundation`

Completed backlog item:

`PF-BE-001 Create backend project skeleton`

`PF-OPS-001 Create local development compose profile`

`PF-BE-002 Implement tenant, laboratory and branch commands`

`PF-BE-003 Implement user account and role assignment baseline`

`PF-BE-004 Implement append-only audit event recording`

`PF-FE-001 Create employee portal administration screens`

Current backlog item:

`PF-APP-001 Create mobile app foundation`

Project folder:

`projects/healthcare-operations-platform/`

## Agent Entry Points

- `AGENT_BOOTSTRAP.md`
- `NEXORA_FRAMEWORK_USAGE_GUIDE.md`
- `nexora-framework/README.md`
- `nexora-framework/02-standards/standards/agent-agnostic-standard.md`
- `nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.md`
- `nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md`
- `projects/healthcare-operations-platform/BUSINESS_REQUIREMENT.md`
- `projects/healthcare-operations-platform/PROJECT_BRIEF.md`
