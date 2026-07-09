# Nexora Framework Execution Sequence

## Purpose

This document defines the logical execution order of the Nexora Framework.

It answers:

- What is loaded first?
- What comes next?
- Which folder owns each responsibility?
- When can development start?

The machine-readable version is:

```text
00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.yaml
```

## Sequence

1. `00-start-here/`
   Load vision, strategic handoff and execution sequence.

2. `01-enterprise/`
   Load Nexora company context and enterprise identity.

3. `02-standards/`
   Load project folder, documentation, agent-agnostic, MDPE, capability package, open data ingestion, product marketplace, business requirement versioning, open-source security quality, client stack validation, technology debt and framework feedback standards.

4. `03-orchestration/`
   Decide whether the target project needs analysis, validation or development.

5. `04-recipes/`
   Apply the Agent-to-MVP recipe when project definitions are incomplete.

6. `05-prompts/`
   Use generic prompts for analysis, validation, development, impact, security quality gates and framework feedback capture. Use auxiliary prompts only after the generic development prompt selects a project, module and slice.

7. `06-templates/`
   Use templates when creating or completing required project artifacts.

8. `07-governance/`
   Use ADRs, RFCs, roadmap, release governance and the company-owned framework improvement backlog for decisions and changes.

9. `08-engineering/`
   Use engineering playbooks, agent role descriptions, security, AI and DevOps guidance.

10. `09-specifications/`
    Use schemas, meta-models and specification assets to validate structure.

11. `10-examples/`
    Use examples as references only. Examples are not source of truth for a target project.

## Development Gate

Development can start only after:

- `BUSINESS_REQUIREMENT.md` exists in the project folder.
- The project has `PROJECT_STATE.yaml`.
- `development_readiness.status` is `ready`.
- `blocking_definition_gaps` is empty.
- The target module package exists.
- The target module package has YAML, Markdown, OpenAPI and traceability artifacts required by the module definition.
- Open-source-first and security quality gates are defined for code-changing work.
- The requester-proposed or existing stack has been validated against current stable or LTS open source market practice.
- A stack-specific quality toolchain baseline exists.
- The framework feedback capture mechanism exists so agents can propose framework improvements without implementing them.

## Current First Product

The first product is:

```text
projects/healthcare-operations-platform/
```

The first approved development module is:

```text
MVP-MOD-001 Platform Foundation
```
