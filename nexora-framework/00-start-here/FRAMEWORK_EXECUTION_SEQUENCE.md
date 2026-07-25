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
00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md
```

## Sequence

1. `00-start-here/`
   Load vision, strategic handoff and execution sequence.

2. `01-enterprise/`
   Load Nexora company context and enterprise identity.

3. `02-standards/`
   Load project folder, documentation, agent-agnostic, MDPE, capability package, open data ingestion, product marketplace, business requirement versioning, open-source security quality, local toolchain inventory, enterprise product foundation, client stack validation, integrated local runbook, technology debt and framework feedback standards.

4. `03-orchestration/`
   Decide whether the target project needs analysis, validation or development.

5. `04-recipes/`
   Apply the Agent-to-MVP recipe when project definitions are incomplete.

6. `05-prompts/`
   Use generic prompts for analysis, validation, development, impact, security quality gates, integrated local runbook maintenance and framework feedback capture. Use auxiliary prompts only after the generic development prompt selects a project, module and slice.

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
- The project has `PROJECT_STATE.md`.
- `development_readiness.status` is `ready`.
- `blocking_definition_gaps` is empty.
- The target module package exists.
- The target module package has YAML, Markdown, OpenAPI and traceability artifacts required by the module definition.
- Open-source-first and security quality gates are defined for code-changing work.
- Enterprise product foundations are defined for localization, IAM permissions, dynamic menus, session management, database deliverables, UX/UI, code documentation, persistence architecture and contract-first generation.
- The requester-proposed or existing stack has been validated against current stable or LTS open source market practice.
- A stack-specific quality toolchain baseline exists.
- A local toolchain inventory exists for the current development machine.
- An integrated local solution runbook exists for human review and validation.
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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-EXEC-001
  type: framework-execution-sequence
  name: Nexora Framework Execution Sequence
  version: 1.0.0
  status: approved
  human_readable: FRAMEWORK_EXECUTION_SEQUENCE.md
  machine_readable: FRAMEWORK_EXECUTION_SEQUENCE.md
  owner: Nexora Engineering
purpose: Define the logical loading and execution order of the Nexora Framework.
sequence:
- step: '00'
  folder: 00-start-here
  responsibility: Load vision, strategic handoff and execution sequence.
  primary_files:
  - 00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md
  - 00-start-here/docs/vision/NEXORA_FINAL_VISION.md
  - 00-start-here/docs/vision/NEXORA_STRATEGIC_HANDOFF.md
- step: '01'
  folder: 01-enterprise
  responsibility: Load Nexora company context and enterprise identity.
- step: '02'
  folder: 02-standards
  responsibility: Load project folder, documentation, agent-agnostic, MDPE, capability
    package, ingestion, marketplace, business requirement versioning, open-source
    security quality, local toolchain inventory, enterprise product foundation, client
    stack market validation, integrated local runbook, technology debt and framework
    feedback standards.
  primary_files:
  - 02-standards/standards/project-folder-standard.md
  - 02-standards/standards/documentation-standard.md
  - 02-standards/standards/agent-agnostic-standard.md
  - 02-standards/standards/model-driven-product-engineering-standard.md
  - 02-standards/standards/capability-package-standard.md
  - 02-standards/standards/open-data-ingestion-standard.md
  - 02-standards/standards/product-marketplace-standard.md
  - 02-standards/standards/business-requirement-versioning-standard.md
  - 02-standards/standards/open-source-first-security-quality-standard.md
  - 02-standards/standards/local-toolchain-inventory-standard.md
  - 02-standards/standards/enterprise-product-foundation-standard.md
  - 02-standards/standards/integrated-local-solution-runbook-standard.md
  - 02-standards/standards/framework-feedback-continuous-improvement-standard.md
- step: '03'
  folder: 03-orchestration
  responsibility: Decide whether a target project needs analysis, validation or development.
  primary_files:
  - 03-orchestration/project-orchestration/project-analysis-and-mvp-workflow.md
- step: '04'
  folder: 04-recipes
  responsibility: Apply the Agent-to-MVP recipe when project definitions are incomplete.
  primary_files:
  - 04-recipes/recipes/agent-to-mvp-recipe.md
- step: '05'
  folder: 05-prompts
  responsibility: Provide generic and auxiliary prompts for analysis, validation,
    development, integrated local runbook maintenance and framework feedback capture.
  primary_files:
  - 05-prompts/prompts/generic-project-lifecycle-prompts.md
  - 05-prompts/prompts/auxiliary-development-prompts.md
  - 05-prompts/prompts/business-requirement-impact-prompts.md
  - 05-prompts/prompts/security-quality-gate-prompts.md
  - 05-prompts/prompts/integrated-local-runbook-prompts.md
  - 05-prompts/prompts/framework-feedback-prompts.md
- step: '06'
  folder: 06-templates
  responsibility: Provide templates for creating or completing project artifacts.
  primary_files:
  - 06-templates/templates/project-template/
- step: '07'
  folder: 07-governance
  responsibility: Provide ADRs, RFCs, roadmap, release governance and company-owned
    framework improvement backlog.
  primary_files:
  - 07-governance/framework-improvement-backlog/framework-improvement-backlog.md
- step: 08
  folder: 08-engineering
  responsibility: Provide engineering playbooks, agent roles, security, AI and DevOps
    guidance.
- step: 09
  folder: 09-specifications
  responsibility: Provide schemas, meta-models and specification assets.
- step: '10'
  folder: 10-examples
  responsibility: Provide examples for reference only.
development_gate:
  required:
  - Target project BUSINESS_REQUIREMENT.md exists.
  - Target project PROJECT_STATE.md exists.
  - development_readiness.status is ready.
  - development_readiness.blocking_definition_gaps is empty.
  - Target module package exists.
  - Target module package required artifacts exist.
  - Open-source-first and security quality gates are defined for code-changing work.
  - Enterprise product foundations are defined for localization, IAM permissions,
    dynamic menus, session management, database deliverables, UX/UI, code documentation,
    persistence architecture and contract-first generation.
  - Requester-proposed or existing stack has been validated against current stable
    or LTS open source market practice.
  - Stack-specific quality toolchain baseline is defined.
  - Local toolchain inventory is defined for the current development machine.
  - Integrated local solution runbook is defined for human review and validation.
  - Technology evolution reviews and technical-debt backlog handling are defined for
    iterative work.
  - Framework feedback capture mechanism is available for agent execution learning.
current_first_product:
  project: projects/healthcare-operations-platform/
  module: MVP-MOD-001
  module_name: Platform Foundation
```
