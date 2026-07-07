# NEXORA — AGENT BOOTSTRAP

## Purpose

This file is the starting context for Codex or any AI engineering agent working in the Nexora repository.

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
6. `nexora-framework/recipes/agent-to-mvp-recipe.md`
7. Target project `SOURCE_OF_TRUTH.yaml`
8. Target project `PROJECT_BRIEF.md`
9. Target project `PROJECT_STATE.yaml`

## Nexora Framework

Use these files for the reusable method:

- `nexora-framework/standards/project-folder-standard.yaml`
- `nexora-framework/standards/documentation-standard.yaml`
- `nexora-framework/recipes/agent-to-mvp-recipe.yaml`
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
- `projects/healthcare-operations-platform/SOURCE_OF_TRUTH.yaml`
- `projects/healthcare-operations-platform/PROJECT_STATE.yaml`

Required starting package:

- `projects/healthcare-operations-platform/05-delivery/mvp/modules/MVP-MOD-001-platform-foundation/module-definition.yaml`
- `projects/healthcare-operations-platform/05-delivery/mvp/modules/MVP-MOD-001-platform-foundation/domain-model.md`
- `projects/healthcare-operations-platform/05-delivery/mvp/modules/MVP-MOD-001-platform-foundation/api-contract.openapi.yaml`
- `projects/healthcare-operations-platform/05-delivery/mvp/modules/MVP-MOD-001-platform-foundation/database-migration-plan.md`
- `projects/healthcare-operations-platform/05-delivery/mvp/modules/MVP-MOD-001-platform-foundation/ui-screen-map.md`
- `projects/healthcare-operations-platform/05-delivery/mvp/modules/MVP-MOD-001-platform-foundation/security-and-audit-rules.md`
- `projects/healthcare-operations-platform/05-delivery/mvp/modules/MVP-MOD-001-platform-foundation/test-plan.md`
- `projects/healthcare-operations-platform/05-delivery/mvp/modules/MVP-MOD-001-platform-foundation/traceability.yaml`

## Rules

1. Preserve project boundaries.
2. Do not put project-specific files at repository root.
3. For a new project, create `projects/<project-slug>/PROJECT_BRIEF.md` first.
4. Apply the Nexora Agent-to-MVP Recipe before implementation.
5. Update project-level `PROJECT_STATE.yaml` and `SOURCE_OF_TRUTH.yaml` for project work.
6. Update root state only for repository or framework changes.
7. Avoid provider, cloud or agent lock-in unless explicitly required.

## Immediate Task

If continuing Healthcare Operations Platform, implement:

`projects/healthcare-operations-platform` → `MVP-MOD-001 Platform Foundation`

If starting a new solution, create:

`projects/<project-slug>/PROJECT_BRIEF.md`

Then run:

`nexora-framework/recipes/agent-to-mvp-recipe.md`
