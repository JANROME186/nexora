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

1. `00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md`
2. `BUSINESS_REQUIREMENT.md`
3. `BUSINESS_REQUIREMENT.md`
4. `PROJECT_BRIEF.md`
5. `SOURCE_OF_TRUTH.md`
6. `PROJECT_STATE.md`
7. `../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md`
8. `../../nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md`
9. `01-product-definition/business-capabilities/bcm-001/business-capability-map.md`
10. `01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md`
11. `02-domain-definition/actors/acm-001/actor-catalog.md`
12. `02-domain-definition/processes/hrp-001/healthcare-reference-processes.md`
13. `02-domain-definition/business-rules/brm-001/business-rules-catalog.md`
14. `06-delivery/mvp/healthcare-operations-platform-mvp-framework.md`
15. `06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.md`
16. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/module-definition.md`
17. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/domain-model.md`
18. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/database-migration-plan.md`
19. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/ui-screen-map.md`
20. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/security-and-audit-rules.md`
21. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/test-plan.md`
22. `06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/traceability.md`

## Development Start Point

The first implementation target is:

`06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/`

The backlog execution playbook is:

`06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.md`

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
10. Update `PROJECT_STATE.md` and `SOURCE_OF_TRUTH.md`.

## Security Quality Rule

Code-changing backlog items must apply:

`../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md`

Evidence must be written under:

`08-qa/security-quality/<backlog-item-id>/`

The evidence must include applicable tests, SAST/static analysis, dependency vulnerability checks, secrets scan, coverage and DAST when a runnable web/API surface exists. If a mandatory validation category applies but HOP lacks an executable tool/script/plugin, register or update technical debt before closure; do not treat missing tooling as informal `not applicable`.

## Business Requirement Change Rule

The active requirement version is declared in:

`00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md`

If the business updates `BUSINESS_REQUIREMENT.md`, the agent must generate an impact assessment under:

`00-intake/business-requirements/impact-assessments/`

The impact assessment must estimate impacted components, effort, timeline and cost. If no rate card exists, cost must be marked as requiring a rate card.

## Boundary Rule

Do not add new unnumbered folders at the project root.

If a new artifact does not fit the current sequence, add it under the closest numbered folder and update `SOURCE_OF_TRUTH.md`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-ODG-001
  type: ordered-development-guide
  name: Healthcare Operations Platform Ordered Development Guide
  version: 1.0.0
  status: approved
  human_readable: ORDERED_DEVELOPMENT_GUIDE.md
  machine_readable: ORDERED_DEVELOPMENT_GUIDE.md
purpose: Defines the predictable incremental execution order for agents working inside
  the project.
project_folder_order:
- step: '00'
  folder: 00-intake
  use: Understand user needs, project brief, assumptions and constraints.
- step: '01'
  folder: 01-product-definition
  use: Define product scope, capability map, dependency map and MVP boundary.
- step: '02'
  folder: 02-domain-definition
  use: Define domain, actors, processes, business rules, vocabulary and migration
    model.
- step: '03'
  folder: 03-architecture
  use: Define application, data, security, integration, AI and technology architecture.
- step: '04'
  folder: 04-requirements
  use: Define stories, UI/mobile flows, business workflows and acceptance criteria.
- step: '05'
  folder: 05-contracts
  use: Define OpenAPI, events, adapter and import/export contracts.
- step: '06'
  folder: 06-delivery
  use: Define MVP modules, implementation packages, releases and traceability.
- step: '07'
  folder: 07-implementation
  use: Place code or generated implementation assets when implementation is colocated.
- step: 08
  folder: 08-qa
  use: Define and store tests, fixtures, quality evidence and security quality gate
    evidence.
- step: 09
  folder: 09-operations
  use: Define engineering governance, deployment, observability and runbooks.
- step: '10'
  folder: 10-generated
  use: Store generated diagrams, indexes and context packages.
- step: '99'
  folder: 99-legacy
  use: Preserve old readmes or migration notes only. Do not use for new work.
agent_loading_order:
- 00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
- BUSINESS_REQUIREMENT.md
- BUSINESS_REQUIREMENT.md
- PROJECT_BRIEF.md
- SOURCE_OF_TRUTH.md
- PROJECT_STATE.md
- ../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
- ../../nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md
- 01-product-definition/business-capabilities/bcm-001/business-capability-map.md
- 01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md
- 02-domain-definition/actors/acm-001/actor-catalog.md
- 02-domain-definition/processes/hrp-001/healthcare-reference-processes.md
- 02-domain-definition/business-rules/brm-001/business-rules-catalog.md
- 06-delivery/mvp/healthcare-operations-platform-mvp-framework.md
- 06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.md
- 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/module-definition.md
- 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/domain-model.md
- 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/database-migration-plan.md
- 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/ui-screen-map.md
- 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/security-and-audit-rules.md
- 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/test-plan.md
- 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/traceability.md
development_start_point:
  first_implementation_target: 06-delivery/mvp/modules/MVP-MOD-001-platform-foundation/
  backlog_execution_playbook: 06-delivery/mvp/MVP_BACKLOG_EXECUTION_PROMPTS.md
  implementation_baseline_folder: 07-implementation/
incremental_rule:
- Resolve the latest BUSINESS_REQUIREMENT version before selecting work.
- Stop if a newer BUSINESS_REQUIREMENT version exists without accepted impact assessment.
- Select one MVP module.
- Load the module definition package from 06-delivery.
- Confirm actors, rules, processes and contracts.
- Implement only the selected module slice.
- Add or update tests in 08-qa.
- Run or document applicable open source security quality gates and write evidence
  under 08-qa/security-quality/<backlog-item-id>/.
- Add operations notes in 09-operations when runtime behavior changes.
- Update PROJECT_STATE.md and SOURCE_OF_TRUTH.md.
boundary_rule:
  project_root: Do not add new unnumbered folders at the project root.
  placement: If a new artifact does not fit the current sequence, add it under the
    closest numbered folder and update SOURCE_OF_TRUTH.md.
business_requirement_change_rule:
  index: 00-intake/business-requirements/BUSINESS_REQUIREMENT_INDEX.md
  impact_assessment_folder: 00-intake/business-requirements/impact-assessments/
  required_before_derived_changes: true
  required_estimates:
  - impacted_components
  - effort
  - timeline
  - cost_or_rate_card_required
security_quality_rule:
  standard: ../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  prompt_playbook: ../../nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md
  evidence_folder: 08-qa/security-quality/
  applies_to: code-changing backlog items
  required_checks_when_applicable:
  - tests
  - sast_or_static_analysis
  - dependency_vulnerability_scan
  - secrets_scan
  - coverage
  - dast_for_runnable_web_or_api_surfaces
  fail_on:
  - critical_or_high_vulnerability_without_accepted_risk
  - secret_detected
  - failing_tests
  - undocumented_coverage_regression
  - mandatory_proprietary_dependency_without_adr
```
