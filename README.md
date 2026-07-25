# Nexora

Nexora is a software development and Artificial Intelligence company.

This repository defines how Nexora documents, governs and prepares software solutions for AI-agent-assisted development.

All framework and project artifacts are agent agnostic: no project may require a named coding agent, assistant, platform prompt, model vendor or runtime-specific configuration to understand or implement the solution.

Nexora follows Model Driven Product Engineering: editable models are the durable source of truth, Business Capability Packages are the primary development unit, and repetitive platform artifacts are generated or derived from models.

Nexora also follows open-source-first engineering: product stacks should prefer open source, self-hostable and standards-based technologies, with security quality gates applied during each code-changing backlog item.

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
6. Generate the required source artifacts until `PROJECT_STATE.md` has no blocking definition gaps.
7. Start implementation from the first module definition package.

For detailed step-by-step usage, analysis-agent instructions, validation gates and development-agent handoff, read:

`NEXORA_FRAMEWORK_USAGE_GUIDE.md`

## Current First Project

Healthcare Operations Platform completed the first MVP module:

`MVP-MOD-001 Platform Foundation`

Completed backlog item:

`PF-BE-001 Create backend project skeleton`

`PF-OPS-001 Create local development compose profile`

`PF-BE-002 Implement tenant, laboratory and branch commands`

`PF-BE-003 Implement user account and role assignment baseline`

`PF-BE-004 Implement append-only audit event recording`

`PF-FE-001 Create employee portal administration screens`

`PF-APP-001 Create mobile app foundation`

`PF-QA-001 Add smoke and contract tests`

`MVP-MOD-001-CLOSEOUT Validate and close the module`

`MVP-MOD-002-DEF Generate Diagnostic Catalog capability packages`

`MVP-MOD-002-BE-001 Compile Diagnostic Catalog backend outputs`

`MVP-MOD-002-BE-002 Implement Diagnostic Catalog custom business rules`

Current module status:

`MVP-MOD-001 Platform Foundation implemented and ready for functional validation`

Current commercial product status:

`HOP MVP-MOD-002 Diagnostic Catalog backend is ready for employee portal UI compilation`

Next backlog item:

`MVP-MOD-002-FE-001 Compile employee catalog UI outputs`

Commercial backlog:

`projects/healthcare-operations-platform/06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md`

Capability package index:

`projects/healthcare-operations-platform/01-product-definition/business-capabilities/packages/capability-package-index.md`

Open data ingestion contract:

`projects/healthcare-operations-platform/05-contracts/import-export/open-data-ingestion/open-data-ingestion-contract.md`

Reusable framework standard:

`nexora-framework/02-standards/standards/open-data-ingestion-standard.md`

Product marketplace standard:

`nexora-framework/02-standards/standards/product-marketplace-standard.md`

HOP product marketplace contract:

`projects/healthcare-operations-platform/05-contracts/marketplace/product-marketplace/product-marketplace-contract.md`

HOP business requirement reference template:

`projects/healthcare-operations-platform/BUSINESS_REQUIREMENT.md`

Business requirement to YAML prompt:

`projects/healthcare-operations-platform/04-requirements/prompts/business-requirement-to-yaml-prompt.md`

Business requirement version index:

`projects/healthcare-operations-platform/00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md`

Business requirement versioning standard:

`nexora-framework/02-standards/standards/business-requirement-versioning-standard.md`

Open-source-first security quality standard:

`nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md`

Project folder:

`projects/healthcare-operations-platform/`

## Agent Entry Points

- `AGENT_BOOTSTRAP.md`
- `NEXORA_FRAMEWORK_USAGE_GUIDE.md`
- `nexora-framework/README.md`
- `nexora-framework/02-standards/standards/agent-agnostic-standard.md`
- `nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md`
- `nexora-framework/03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.md`
- `nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md`
- `projects/healthcare-operations-platform/BUSINESS_REQUIREMENT.md`
- `projects/healthcare-operations-platform/PROJECT_BRIEF.md`
