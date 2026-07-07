# Nexora Project Analysis and MVP Workflow

## What This Workflow Does

An agent uses this workflow to inspect the `projects/` folder, determine whether each project has already been analyzed with the Nexora framework, and complete missing definitions until the project is ready for MVP implementation.

The workflow starts from a high-level business requirement and ends with a project folder that specialized subagents can use without chat history.

## Required Project Input

Every project must have:

- `BUSINESS_REQUIREMENT.md`: high-level business need and user context.
- `PROJECT_BRIEF.md`: structured summary refined from the business requirement.
- `SOURCE_OF_TRUTH.yaml`: authoritative artifact registry.
- `PROJECT_STATE.yaml`: readiness state and blocking gaps.

If `BUSINESS_REQUIREMENT.md` is missing, the project is not ready to analyze.

## Agent Procedure

1. Load the Nexora framework files listed in `project-analysis-and-mvp-workflow.yaml`.
2. Enumerate `projects/<project-slug>/`.
3. For each project, load its business requirement, brief, state and source of truth.
4. Decide whether the project is already analyzed.
5. If it is not analyzed, apply `nexora-framework/recipes/agent-to-mvp-recipe.yaml`.
6. Produce source artifacts first.
7. Create the MVP delivery framework and first module package.
8. Update `SOURCE_OF_TRUTH.yaml` and `PROJECT_STATE.yaml`.
9. Handoff module packages to specialized subagents.

## MVP Readiness

A project is ready for development when:

- The business requirement and project brief are complete.
- Product, domain, architecture, requirements, contracts and QA definitions exist.
- MVP modules are ordered incrementally.
- The first module has a complete implementation package.
- `PROJECT_STATE.yaml` has `development_readiness.status: ready`.
- `blocking_definition_gaps` is empty.

## Subagent Handoff

Specialized subagents implement only from repository artifacts. A handoff must include the module id, source paths, capability ids, domain ownership, contracts, UI surfaces, security rules, tests and traceability.
