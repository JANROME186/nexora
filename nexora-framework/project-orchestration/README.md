# Nexora Project Orchestration

## Purpose

This folder defines how an agent must inspect every solution under `projects/`, decide whether each project has already been analyzed with the Nexora framework, and complete missing definition artifacts before implementation starts.

The orchestration layer is project-agnostic. It does not define healthcare, finance, retail or any other domain by itself. It defines the repeatable path that turns a high-level business requirement into an MVP-ready project folder.

## Required Agent Behavior

1. Load repository-level context:
   - `AGENT_BOOTSTRAP.md`
   - `SOURCE_OF_TRUTH.yaml`
   - `PROJECT_STATE.yaml`
   - `nexora-framework/README.md`
   - `nexora-framework/standards/project-folder-standard.yaml`
   - `nexora-framework/standards/documentation-standard.yaml`
   - `nexora-framework/standards/agent-agnostic-standard.yaml`
   - `nexora-framework/project-orchestration/project-analysis-and-mvp-workflow.yaml`
2. Enumerate folders under `projects/`.
3. For each `projects/<project-slug>/`, load:
   - `BUSINESS_REQUIREMENT.md`
   - `PROJECT_BRIEF.md`
   - `SOURCE_OF_TRUTH.yaml`
   - `PROJECT_STATE.yaml`
4. If `BUSINESS_REQUIREMENT.md` is missing, stop that project and create a blocking gap.
5. If the project has not been analyzed, apply `nexora-framework/recipes/agent-to-mvp-recipe.yaml`.
6. Complete source artifacts before generated artifacts.
7. Mark the project ready only when the first MVP module has a complete definition package.

## Project Analysis Result

After analysis, each project must contain enough definition for specialized subagents to implement independently:

- High-level business requirement.
- Structured project brief.
- Product and capability definition.
- Domain foundation.
- Architecture baseline.
- Requirements, contracts and QA expectations.
- MVP delivery framework.
- First module implementation package.
- Source-of-truth registry and project state with no blocking definition gaps.

## Specialized Subagent Handoff

Specialized subagents must receive only repository artifacts as context. They should not require chat history or platform-specific configuration.

The handoff package for implementation is:

- Target module id.
- Source artifacts consulted.
- Capability ids and dependency profile.
- Bounded context and aggregate ownership.
- API contracts.
- UI, mobile or integration surfaces.
- Security, audit and privacy rules.
- Test plan.
- Traceability file.
