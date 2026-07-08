# NEXORA — AGENT BOOTSTRAP

## Purpose

This file is the starting context for any AI engineering agent working in the Nexora repository.

The agent must continue from repository artifacts, not previous chat context.

## Company Definition

Nexora is a software development and Artificial Intelligence company.

Nexora is not only a healthcare product. Healthcare Operations Platform is Nexora's first project.

## Repository Model

This repository is now organized as a multi-project, agent-ready definition repository.

Root level contains:

- Nexora framework and standards.
- Repository-level state and source of truth.
- Project folders.

Project-specific work lives under:

`projects/<project-slug>/`

## Required Loading Order

1. `SOURCE_OF_TRUTH.yaml`
2. `PROJECT_STATE.yaml`
3. `nexora-framework/README.md`
4. `nexora-framework/standards/project-folder-standard.md`
5. `nexora-framework/standards/documentation-standard.md`
6. `nexora-framework/standards/agent-agnostic-standard.md`
7. `nexora-framework/project-orchestration/project-analysis-and-mvp-workflow.md`
8. `nexora-framework/recipes/agent-to-mvp-recipe.md`
9. `nexora-framework/prompts/generic-project-lifecycle-prompts.yaml`
10. `nexora-framework/prompts/auxiliary-development-prompts.yaml`
11. Target project `BUSINESS_REQUIREMENT.md`
12. Target project `SOURCE_OF_TRUTH.yaml`
13. Target project `PROJECT_BRIEF.yaml`
14. Target project `PROJECT_STATE.yaml`

## Nexora Framework

Use these files for the reusable method:

- `nexora-framework/standards/project-folder-standard.yaml`
- `nexora-framework/standards/documentation-standard.yaml`
- `nexora-framework/standards/agent-agnostic-standard.yaml`
- `nexora-framework/project-orchestration/project-analysis-and-mvp-workflow.yaml`
- `nexora-framework/recipes/agent-to-mvp-recipe.yaml`
- `nexora-framework/prompts/generic-project-lifecycle-prompts.yaml`
- `nexora-framework/prompts/auxiliary-development-prompts.yaml`
- `nexora-framework/templates/project-template/`

## First Project

Project:

`projects/healthcare-operations-platform/`

Current status:

Ready for MVP development.

Ready module:

`MVP-MOD-001 Platform Foundation`

Required project entry files:

- `projects/healthcare-operations-platform/PROJECT_BRIEF.md`
- `projects/healthcare-operations-platform/PROJECT_BRIEF.yaml`
- `projects/healthcare-operations-platform/BUSINESS_REQUIREMENT.md`
- `projects/healthcare-operations-platform/BUSINESS_REQUIREMENT.yaml`
- `projects/healthcare-operations-platform/ORDERED_DEVELOPMENT_GUIDE.md`
- `projects/healthcare-operations-platform/ORDERED_DEVELOPMENT_GUIDE.yaml`
- `projects/healthcare-operations-platform/SOURCE_OF_TRUTH.yaml`
- `projects/healthcare-operations-platform/PROJECT_STATE.yaml`

Required starting package:

- `projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/module-definition.yaml`
- `projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/domain-model.yaml`
- `projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/api-contract.openapi.yaml`
- `projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/database-migration-plan.yaml`
- `projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/ui-screen-map.yaml`
- `projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/security-and-audit-rules.yaml`
- `projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/test-plan.yaml`
- `projects/healthcare-operations-platform/06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/traceability.yaml`

## Rules

1. Preserve project boundaries.
2. Do not put project-specific files at repository root.
3. For a new project, require the requester to provide `projects/<project-slug>/BUSINESS_REQUIREMENT.md` first.
4. Structure the requirement into `PROJECT_BRIEF.md` and `PROJECT_BRIEF.yaml`.
5. Apply the Nexora Project Analysis and MVP Workflow before implementation.
6. Apply the Nexora Agent-to-MVP Recipe before implementation.
7. Update project-level `PROJECT_STATE.yaml` and `SOURCE_OF_TRUTH.yaml` for project work.
8. Update root state only for repository or framework changes.
9. Avoid provider, cloud or agent lock-in unless explicitly required.

## Immediate Task

If continuing Healthcare Operations Platform, implement:

`projects/healthcare-operations-platform` → `MVP-MOD-001 Platform Foundation`

If starting a new solution, first ensure the requester has provided:

`projects/<project-slug>/BUSINESS_REQUIREMENT.md`

This file must be supplied by the requester. Do not generate it as an agent.

Then create:

`projects/<project-slug>/PROJECT_BRIEF.md`

and:

`projects/<project-slug>/PROJECT_BRIEF.yaml`

Then run:

`nexora-framework/project-orchestration/project-analysis-and-mvp-workflow.md`

Then run:

`nexora-framework/recipes/agent-to-mvp-recipe.md`
