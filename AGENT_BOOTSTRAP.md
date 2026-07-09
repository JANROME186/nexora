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
4. `nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.yaml`
5. `nexora-framework/02-standards/standards/project-folder-standard.md`
6. `nexora-framework/02-standards/standards/documentation-standard.md`
7. `nexora-framework/02-standards/standards/agent-agnostic-standard.md`
8. `nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md`
9. `nexora-framework/02-standards/standards/capability-package-standard.md`
10. `nexora-framework/02-standards/standards/open-data-ingestion-standard.md`
11. `nexora-framework/02-standards/standards/product-marketplace-standard.md`
12. `nexora-framework/02-standards/standards/business-requirement-versioning-standard.md`
13. `nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md`
14. `nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.md`
15. `nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md`
16. `nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.yaml`
17. `nexora-framework/05-prompts/prompts/business-requirement-impact-prompts.yaml`
18. `nexora-framework/05-prompts/prompts/security-quality-gate-prompts.yaml`
19. `nexora-framework/05-prompts/prompts/auxiliary-development-prompts.yaml`
20. Target project `00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml` if present
21. Target project `BUSINESS_REQUIREMENT.md`
22. Target project `SOURCE_OF_TRUTH.yaml`
23. Target project `PROJECT_BRIEF.yaml`
24. Target project `PROJECT_STATE.yaml`

## Nexora Framework

Use these files for the reusable method:

- `nexora-framework/00-start-here/docs/vision/NEXORA_FINAL_VISION.yaml`
- `nexora-framework/00-start-here/docs/vision/NEXORA_STRATEGIC_HANDOFF.yaml`
- `nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.yaml`
- `nexora-framework/02-standards/standards/project-folder-standard.yaml`
- `nexora-framework/02-standards/standards/documentation-standard.yaml`
- `nexora-framework/02-standards/standards/agent-agnostic-standard.yaml`
- `nexora-framework/02-standards/standards/model-driven-product-engineering-standard.yaml`
- `nexora-framework/02-standards/standards/capability-package-standard.yaml`
- `nexora-framework/02-standards/standards/open-data-ingestion-standard.yaml`
- `nexora-framework/02-standards/standards/product-marketplace-standard.yaml`
- `nexora-framework/02-standards/standards/business-requirement-versioning-standard.yaml`
- `nexora-framework/02-standards/standards/open-source-first-security-quality-standard.yaml`
- `nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.yaml`
- `nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.yaml`
- `nexora-framework/05-prompts/prompts/generic-project-lifecycle-prompts.yaml`
- `nexora-framework/05-prompts/prompts/business-requirement-impact-prompts.yaml`
- `nexora-framework/05-prompts/prompts/security-quality-gate-prompts.yaml`
- `nexora-framework/05-prompts/prompts/auxiliary-development-prompts.yaml`
- `nexora-framework/06-templates/templates/project-template/`

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
10. Prefer open source, self-hostable and standards-based technologies.
11. For code-changing work, produce security quality evidence with applicable tests, SAST/static analysis, dependency vulnerability checks, secrets scan and coverage.

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

`nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.md`

Then run:

`nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md`
