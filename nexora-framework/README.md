# Nexora Framework

This folder defines how Nexora documents, governs and prepares software solutions for AI-agent-assisted development.

Nexora is the company and engineering organization. Product or client solutions live under `projects/`.

## Start Here

The framework is intentionally ordered by numbered folders.

Begin with:

- `00-start-here/README.md`
- `00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md`
- `00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.yaml`

The short sequence is:

1. `00-start-here/` - Understand vision, handoff and execution order.
2. `01-enterprise/` - Load Nexora company context.
3. `02-standards/` - Load documentation, project folder and agent-agnostic standards.
4. `03-orchestration/` - Decide whether a project needs analysis, validation or implementation.
5. `04-recipes/` - Apply the Agent-to-MVP recipe when definitions are missing.
6. `05-prompts/` - Use generic and auxiliary prompts for analysis, validation and development.
7. `06-templates/` - Use templates when creating or completing a project.
8. `07-governance/` - Use ADRs, RFCs, roadmap and release governance.
9. `08-engineering/` - Use engineering playbooks, agents and operational guidance.
10. `09-specifications/` - Use schemas, meta-models and specification assets.
11. `10-examples/` - Use examples as references only.

## Purpose

The framework gives agents and human teams a repeatable path from project description to implementation-ready MVP.

It defines:

- Repository and project folder standards.
- Documentation rules.
- Source-of-truth rules.
- Project discovery and analysis workflow.
- Agent execution recipes.
- Generic and auxiliary prompts.
- Templates for new projects.
- Shared meta-models, schemas, playbooks and governance assets.

## Core Entry Points

- `00-start-here/docs/vision/NEXORA_FINAL_VISION.yaml`
- `00-start-here/docs/vision/NEXORA_STRATEGIC_HANDOFF.yaml`
- `02-standards/standards/project-folder-standard.yaml`
- `02-standards/standards/documentation-standard.yaml`
- `02-standards/standards/agent-agnostic-standard.yaml`
- `03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.yaml`
- `04-recipes/recipes/agent-to-mvp-recipe.yaml`
- `05-prompts/prompts/generic-project-lifecycle-prompts.yaml`
- `05-prompts/prompts/auxiliary-development-prompts.yaml`
- `06-templates/templates/project-template/`

## Execution Logic

For any project under `projects/<project-slug>/`:

1. Confirm `BUSINESS_REQUIREMENT.md` exists.
2. Load the framework sequence from `00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.yaml`.
3. Apply the orchestration workflow.
4. If analysis is incomplete, apply the Agent-to-MVP recipe.
5. Validate the project against the framework.
6. Start development only from the approved project state and module package.

## Project Rule

Every solution must live under:

`projects/<project-slug>/`

No project-specific capability map, API contract, delivery package, UI specification or implementation artifact should be placed directly at repository root.

Every project must start from a high-level business requirement in:

`projects/<project-slug>/BUSINESS_REQUIREMENT.md`

`PROJECT_BRIEF.md` and `PROJECT_BRIEF.yaml` structure that requirement into a product and MVP analysis context.
