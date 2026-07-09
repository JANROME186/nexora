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
3. `02-standards/` - Load documentation, project folder, agent-agnostic, open-source-first, stack validation, security quality, integrated local runbook and framework feedback standards.
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
- `02-standards/standards/model-driven-product-engineering-standard.yaml`
- `02-standards/standards/capability-package-standard.yaml`
- `02-standards/standards/open-data-ingestion-standard.yaml`
- `02-standards/standards/product-marketplace-standard.yaml`
- `02-standards/standards/business-requirement-versioning-standard.yaml`
- `02-standards/standards/open-source-first-security-quality-standard.yaml`
- `02-standards/standards/integrated-local-solution-runbook-standard.yaml`
- `02-standards/standards/framework-feedback-continuous-improvement-standard.yaml`
- `03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.yaml`
- `04-recipes/recipes/agent-to-mvp-recipe.yaml`
- `05-prompts/prompts/generic-project-lifecycle-prompts.yaml`
- `05-prompts/prompts/auxiliary-development-prompts.yaml`
- `05-prompts/prompts/security-quality-gate-prompts.yaml`
- `05-prompts/prompts/integrated-local-runbook-prompts.yaml`
- `05-prompts/prompts/framework-feedback-prompts.yaml`
- `07-governance/framework-improvement-backlog/framework-improvement-backlog.yaml`
- `06-templates/templates/project-template/`

## Execution Logic

For any project under `projects/<project-slug>/`:

1. Confirm `BUSINESS_REQUIREMENT.md` exists.
2. Resolve the latest business requirement version using the business requirement versioning standard.
3. Load the framework sequence from `00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.yaml`.
4. Apply the orchestration workflow.
5. If analysis is incomplete, apply the Agent-to-MVP recipe.
6. If the business requirement changed, generate an impact assessment before modifying derived artifacts.
7. Apply the open-source-first, client stack market validation, technology evolution, security quality and technical-debt standards to technology choices and code-changing work.
8. Maintain the integrated local solution runbook so human reviewers can start and validate the complete local solution from one guide.
9. Capture framework feedback when execution reveals reusable improvements.
10. Validate the project against the framework.
11. Start development only from the approved project state and module package.

During every code-changing backlog item, the agent must treat the initial technology stack as the
current baseline, not as a permanent constraint. If newer open source frameworks, runtimes,
dependencies or quality tools would materially improve security, maintainability, portability or
cost, the agent must either apply the change when it is required for safe delivery or register it as
technical debt under `08-qa/technical-debt/` for gradual remediation.

When a client or requester proposes a stack, the agent must validate it before implementation:
compare it with current stable or LTS versions from official sources, current open source ecosystem
practice, security advisories, licensing, maintenance activity, cost and required quality gates. The
result must define both the selected stack baseline and a stack-specific quality toolchain baseline.

When project execution reveals ambiguity, missing templates, missing prompts, repeated manual work or
other reusable framework improvements, agents must create project-local feedback under
`08-qa/framework-feedback/`. Reusable items may be proposed in
`07-governance/framework-improvement-backlog/`, but implementation is owned and prioritized by
Nexora.

Every project with runnable implementation must maintain an integrated local solution runbook at
`09-operations/runbooks/local-solution-runbook.yaml` and
`09-operations/runbooks/local-solution-runbook.md`. Component READMEs may explain individual
services, but the integrated runbook is the reviewer-facing path for starting infrastructure,
backend, web surfaces, mobile validation, smoke checks, quality gates and shutdown steps.

## Project Rule

Every solution must live under:

`projects/<project-slug>/`

No project-specific capability map, API contract, delivery package, UI specification or implementation artifact should be placed directly at repository root.

Every project must start from a high-level business requirement in:

`projects/<project-slug>/BUSINESS_REQUIREMENT.md`

`PROJECT_BRIEF.md` and `PROJECT_BRIEF.yaml` structure that requirement into a product and MVP analysis context.
