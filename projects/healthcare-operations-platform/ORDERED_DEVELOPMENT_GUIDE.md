# Healthcare Operations Platform — Ordered Development Guide

## Purpose

This guide tells any agent how to work inside this project in a predictable, incremental order.

The project root intentionally contains only numbered folders plus project control files. Agents must use the numbers as the execution sequence.

## Project Folder Order

| Step | Folder | Use |
| --- | --- | --- |
| 00 | `00-intake` | Understand user needs, project brief, assumptions and constraints. |
| 01 | `01-product-definition` | Define product scope, capability map, dependency map and MVP boundary. |
| 02 | `02-domain-definition` | Define domain, actors, processes, business rules, vocabulary and migration model. |
| 03 | `03-architecture` | Define application, data, security, integration, AI and technology architecture. |
| 04 | `04-requirements` | Define stories, UI/mobile flows, business workflows and acceptance criteria. |
| 05 | `05-contracts` | Define OpenAPI, events, adapter and import/export contracts. |
| 06 | `06-delivery` | Define MVP modules, implementation packages, releases and traceability. |
| 07 | `07-implementation` | Place code or generated implementation assets when implementation is colocated. |
| 08 | `08-qa` | Define and store tests, fixtures, quality evidence and security quality gate evidence. |
| 09 | `09-operations` | Define engineering governance, deployment, observability and runbooks. |
| 10 | `10-generated` | Store generated diagrams, indexes and context packages. |
| 99 | `99-legacy` | Preserve old readmes or migration notes only. Do not use for new work. |

## Agent Loading Order

1. `00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml`
2. `BUSINESS_REQUIREMENT.md`
3. `BUSINESS_REQUIREMENT.yaml`
4. `PROJECT_BRIEF.yaml`
5. `SOURCE_OF_TRUTH.yaml`
6. `PROJECT_STATE.yaml`
7. `../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.yaml`
8. `../../nexora-framework/05-prompts/prompts/security-quality-gate-prompts.yaml`
9. `01-product-definition/business-capabilities/bcm-001/business-capability-map.yaml`
10. `01-product-definition/business-capabilities/bcm-002/capability-dependency-map.yaml`
11. `02-domain-definition/actors/acm-001/actor-catalog.yaml`
12. `02-domain-definition/processes/hrp-001/healthcare-reference-processes.yaml`
13. `02-domain-definition/business-rules/brm-001/business-rules-catalog.yaml`
14. `06-delivery/mvp/healthcare-operations-platform-mvp-framework.yaml`
15. `06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.yaml`
16. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/module-definition.yaml`
17. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/domain-model.yaml`
18. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/database-migration-plan.yaml`
19. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/ui-screen-map.yaml`
20. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/security-and-audit-rules.yaml`
21. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/test-plan.yaml`
22. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/traceability.yaml`

## Development Start Point

The first implementation target is:

`06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/`

The backlog execution playbook is:

`06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.yaml`

Start by creating the implementation baseline in:

`07-implementation/`

## Incremental Rule

Each implementation iteration must:

1. Resolve the latest `BUSINESS_REQUIREMENT` version.
2. Stop if a newer business requirement version exists without accepted impact assessment.
3. Select one MVP module.
4. Load the module definition package from `06-delivery`.
5. Confirm actors, rules, processes and contracts.
6. Implement only the selected module slice.
7. Add or update tests in `08-qa`.
8. Run or document applicable open source security quality gates and write evidence under `08-qa/security-quality/<backlog-item-id>/`.
9. Add operations notes in `09-operations` when runtime behavior changes.
10. Update `PROJECT_STATE.yaml` and `SOURCE_OF_TRUTH.yaml`.

## Security Quality Rule

Code-changing backlog items must apply:

`../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.yaml`

Evidence must be written under:

`08-qa/security-quality/<backlog-item-id>/`

The evidence must include applicable tests, SAST/static analysis, dependency vulnerability checks, secrets scan, coverage and DAST when a runnable web/API surface exists. If a mandatory validation category applies but HOP lacks an executable tool/script/plugin, register or update technical debt before closure; do not treat missing tooling as informal `not applicable`.

## Business Requirement Change Rule

The active requirement version is declared in:

`00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.yaml`

If the business updates `BUSINESS_REQUIREMENT.md`, the agent must generate an impact assessment under:

`00-intake/business-requirements/impact-assessments/`

The impact assessment must estimate impacted components, effort, timeline and cost. If no rate card exists, cost must be marked as requiring a rate card.

## Boundary Rule

Do not add new unnumbered folders at the project root.

If a new artifact does not fit the current sequence, add it under the closest numbered folder and update `SOURCE_OF_TRUTH.yaml`.
