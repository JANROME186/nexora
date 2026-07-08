# Nexora Framework

This folder defines how Nexora documents, governs and prepares software solutions for AI-agent-assisted development.

Nexora is the company and engineering organization. Product or client solutions live under `projects/`.

## Purpose

The framework gives agents and human teams a repeatable path from project description to implementation-ready MVP.

It defines:

- Repository and project folder standards.
- Documentation rules.
- Source-of-truth rules.
- Agent execution recipes.
- Project discovery and analysis workflow.
- Templates for new projects.
- Shared meta-models, schemas, playbooks and governance assets.

## Vision Sources

- `docs/vision/NEXORA_FINAL_VISION.md`
- `docs/vision/NEXORA_FINAL_VISION.yaml`
- `docs/vision/NEXORA_STRATEGIC_HANDOFF.md`
- `docs/vision/NEXORA_STRATEGIC_HANDOFF.yaml`

These files define company-level intent. They guide prioritization and roadmap decisions, but implementation starts from project source-of-truth files and approved module packages.

## Core Entry Points

- `standards/project-folder-standard.md`
- `standards/documentation-standard.md`
- `standards/agent-agnostic-standard.md`
- `project-orchestration/README.md`
- `project-orchestration/project-analysis-and-mvp-workflow.md`
- `recipes/agent-to-mvp-recipe.md`
- `prompts/generic-project-lifecycle-prompts.md`
- `prompts/generic-project-lifecycle-prompts.yaml`
- `prompts/auxiliary-development-prompts.md`
- `prompts/auxiliary-development-prompts.yaml`
- `templates/project-template/`

## Agent Project Scan

An agent must use `project-orchestration/project-analysis-and-mvp-workflow.yaml` before starting implementation.

The workflow requires the agent to:

1. Enumerate `projects/<project-slug>/`.
2. Validate whether each project has `BUSINESS_REQUIREMENT.md`, `PROJECT_BRIEF.md`, `SOURCE_OF_TRUTH.yaml` and `PROJECT_STATE.yaml`.
3. Detect whether the project has already been analyzed with the Nexora framework.
4. Apply `recipes/agent-to-mvp-recipe.yaml` when definitions are missing.
5. Leave the project folder with the MVP artifacts required for specialized subagents.

## Project Rule

Every solution must live under:

`projects/<project-slug>/`

No project-specific capability map, API contract, delivery package, UI specification or implementation artifact should be placed directly at repository root.

Every project must start from a high-level business requirement in:

`projects/<project-slug>/BUSINESS_REQUIREMENT.md`

`PROJECT_BRIEF.md` structures that requirement into a product and MVP analysis context.

## Generic Prompts

Use `prompts/generic-project-lifecycle-prompts.yaml` as the machine-readable prompt playbook for any project under `projects/`.

It defines three reusable prompts:

1. Analyze a project from `BUSINESS_REQUIREMENT.md` and generate all MVP-ready definitions.
2. Validate that the generated project complies with the Nexora framework.
3. Develop the MVP from the prepared project folder and ordered module package.

Use `prompts/auxiliary-development-prompts.yaml` only after the generic MVP development prompt has selected the project, module and backlog item.
