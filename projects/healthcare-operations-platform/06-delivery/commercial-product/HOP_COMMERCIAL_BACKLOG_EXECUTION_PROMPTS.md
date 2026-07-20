# HOP Commercial Backlog Execution Prompts

Artifact ID: `HOP-COM-PROMPTS-001`
Version: `1.0.0`
Status: `approved`

The machine-readable source is `HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml`.

## Purpose

Use this playbook after `MVP-MOD-001 Platform Foundation` to continue HOP toward a commercial product. It tells an agent how to select the next backlog item, generate capability package models, validate them, compile generated outputs, implement custom rule points and close each capability group.

## MDPE Rule

HOP follows:

```text
Model -> Compile -> Implement Rules -> Validate -> Release
```

Do not manually write CRUD scaffolding, DTOs, controllers, repositories, SDKs, Swagger documentation, repetitive documentation, duplicate models or repetitive tests when they can be generated from models.

## Open Source And Security Quality Rule

HOP must prefer open source, self-hostable and standards-based technologies. Any mandatory proprietary dependency requires an ADR exception.

Every code-changing backlog item must write security quality evidence under:

```text
08-qa/security-quality/<backlog-item-id>/
```

Before feature work, the agent must review `08-qa/technical-debt/technical-debt-index.yaml` and
resolve or materially reduce at least one open technical-debt item unless no open debt exists.
As HOP advances, debt burn-down must become stricter: module closeout, release preparation and
commercial-readiness work must reduce multiple relevant debt items when open debt remains.

The evidence must cover applicable tests, best practices, coding standards, duplicate code,
complexity, SAST/static analysis, OWASP or equivalent secure-code checks, dependency vulnerability
checks across all severities, secrets scan, coverage, message externalization/i18n and DAST when a
runnable web/API surface exists.

Coverage target: 80% line coverage for every applicable delivered stack. If a stack is below 80% in
an intermediate iteration, the previous measured coverage becomes the lower bound and must not drop.
The full product cannot be marked complete while any stack is below 80% or any technical debt
remains open.

When a changed stack remains below 80%, the agent must target a 3 to 5 percentage point line
coverage improvement. Smaller improvements require explicit justification, maximum meaningful
in-scope tests and immediate coverage debt assigned to the next relevant backlog.

## Enterprise Product Foundation Rule

Before customer-facing portal/app work continues, HOP must satisfy the enterprise foundation
baseline:

- `es-MX` and `en-US` localization resources and language-switch mechanism.
- No new hard-coded user-visible text in web or app code.
- IAM permission mapping for every feature, menu item, API operation and sensitive action.
- Dynamic menus and actions based on the authenticated user's roles, permissions, tenant, branch and entitlements.
- Login, logout, session expiration and authenticated session context.
- Product database architecture, initialization, seed data, dictionary and normalization review.
- UX/UI look and feel baseline for web and app.
- Code documentation standard, including Javadoc for Java public/shared contracts.
- Persistence review for JPA/Hibernate, repository ports, raw SQL boundaries and migrations.
- OpenAPI/contract-first generation review for backend, frontend and app.

## Verifiable Closure Rule

An agent must not say a HOP backlog item is done, finished, closed or ready for the next backlog
until the closure audit passes. The audit requires:

- YAML parse for HOP source files outside dependency/build folders.
- Stale-pointer sweep for active/current/next backlog ids and `ready_for_next_backlog_item`.
- Evidence-state sweep for `not_executed`, `failed`, `passed_with_execution_limitation`,
  `closed_with_execution_limitation` and blocked toolchain/runtime/network states.
- `git diff --check`.
- Evidence metrics matching actual command output.
- Synchronized `PROJECT_STATE.yaml`, `SOURCE_OF_TRUTH.yaml`, this prompt file, runbooks, indexes and
  capability traceability.
- Commit hash and clean `git status --short` when commits are allowed.

If any audit item fails or is not run, the item is incomplete or blocked. The agent must keep
`next_backlog_item` unchanged and write exact remediation steps.

## Required Rule

If `BUSINESS_REQUIREMENT.md` is missing, stop. That file is requester-supplied source material and is the raw input for all product analysis.

## Required Context

Load these first:

- `../../../../AGENT_BOOTSTRAP.md`
- `../../../../PROJECT_STATE.yaml`
- `../../../../SOURCE_OF_TRUTH.yaml`
- `../../../../nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.yaml`
- `../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.yaml`
- `../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.yaml`
- `../../../../nexora-framework/02-standards/standards/capability-package-standard.yaml`
- `../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.yaml`
- `../../../../nexora-framework/02-standards/standards/enterprise-product-foundation-standard.yaml`
- `../../../../nexora-framework/05-prompts/prompts/security-quality-gate-prompts.yaml`
- `../../BUSINESS_REQUIREMENT.md`
- `../../PROJECT_BRIEF.yaml`
- `../../SOURCE_OF_TRUTH.yaml`
- `../../PROJECT_STATE.yaml`
- `../../01-product-definition/business-capabilities/bcm-001/business-capability-map.yaml`
- `../../01-product-definition/business-capabilities/bcm-002/capability-dependency-map.yaml`
- `../mvp/healthcare-operations-platform-mvp-framework.yaml`
- `HOP_COMMERCIAL_PRODUCT_BACKLOG.yaml`
- `HOP_QUALITY_ALIGNMENT_BACKLOG.yaml`
- `HOP_ENTERPRISE_FOUNDATION_ALIGNMENT_BACKLOG.yaml`

## Prompt 1: Select Next Executable Backlog Item

Instruction:

```text
Load the HOP project state and HOP commercial product backlog.
Select the next executable backlog item by dependency order.
If any dependency is incomplete, stop and report the exact blocker.
If the selected item changes code, include the required debt-first action in the execution plan.
If the selected item is a definition item, prepare the capability package modeling plan.
If the selected item is an implementation item, verify that the definition package exists first.
```

Expected result:

- Selected module.
- Selected backlog item.
- Dependency status.
- Blocking gaps, if any.
- Execution plan.

## Prompt 2: Generate Capability Package Models

Instruction:

```text
Generate the complete Capability Package model set for every capability in the selected HOP roadmap group.
Use the commercial product backlog as sequencing context, BCM-001 for capabilities, BCM-002 for dependency profiles, and the domain foundation for bounded contexts and aggregate ownership.
Place each package under 01-product-definition/business-capabilities/packages/.
Do not implement code during this step.
Do not manually define CRUD, DTO, controller, repository, SDK, Swagger, repetitive documentation or repetitive test artifacts as implementation work; capture them in generation-plan.yaml as generated outputs.
Every machine-executable artifact must be YAML when applicable, with Markdown as the human companion.
```

Expected result:

- `capability-package.yaml`
- `business-model.yaml`
- `business-rules.yaml`
- `processes.yaml`
- `events.yaml`
- `openapi-source.yaml`
- `permissions.yaml`
- `ui-model.yaml`
- `mobile-model.yaml`
- `test-model.yaml`
- `observability-model.yaml`
- `generation-plan.yaml`
- `traceability.yaml`

## Prompt 3: Validate Capability Package Models

Instruction:

```text
Validate the selected Capability Package model set.
Confirm that all required artifacts exist, YAML is parseable, Markdown companion files exist where required, capabilities trace to BCM-001, dependencies trace to BCM-002, API surfaces are classified, and security/audit/test expectations are present.
Confirm that generation-plan.yaml separates generated outputs from custom implementation points.
Scan for named-agent dependencies or vendor-specific execution requirements.
If any blocking gap exists, update the package before allowing compilation or implementation.
```

Expected result:

- Validation report.
- Missing artifacts.
- Traceability gaps.
- Agent-agnostic findings.
- Ready-for-implementation decision.

## Prompt 4: Compile and Implement Selected Backlog Item

Instruction:

```text
Implement only the selected backlog item.
Load the selected Capability Package models first and follow existing project implementation patterns.
Before feature work, load ../../08-qa/technical-debt/technical-debt-index.yaml and resolve or materially reduce at least one open debt item using the framework selection order. If no open debt exists, record that explicitly in evidence.
Generate repetitive platform artifacts from the models before writing custom code.
Keep changes scoped to the selected backlog item.
Externalize new or changed user-visible text, validation copy, error prose, status labels, error codes and repeated magic values through backend message bundles, frontend/mobile localization resources, constants, configuration or policy providers as appropriate.
Enforce enterprise foundations for the selected backlog item: map features/actions/menu items/API operations to IAM permissions, use authenticated session context instead of static users, define or update dynamic menus/actions, keep database deliverables current, follow UX/UI design-system artifacts, document public/shared contracts, keep persistence decoupled and use OpenAPI/contract-first generation where applicable.
Add or update only generated tests or custom rule tests appropriate to the backend, web, mobile, portal, integration or operations scope.
Run applicable open source security quality gates for the changed stack, including tests, build, coverage, best practices, coding standards, duplicate code, complexity, SAST/static analysis, OWASP or equivalent secure-code checks, dependency vulnerability checks across all severities, secrets scan, message externalization/i18n review and DAST when a runnable surface exists.
If Maven, Java, Node, npm, native packages, Docker, database services, network access or audit endpoints are missing or blocked, first attempt documented remediation or request approval. If still unavailable, mark the backlog `blocked_by_environment` or `ready_for_external_validation`, keep `next_backlog_item` on the current backlog item, write exact remediation commands, and stop. Manual source review is only a compensating control and cannot replace executable gates.
Do not close the backlog item with unresolved vulnerabilities of any severity, missing duplicate/complexity/OWASP analysis, missing debt-first action, or unexternalized user-facing messages/magic strings unless an immediate accepted-risk/debt disposition exists with owner and target backlog.
Write security quality evidence under 08-qa/security-quality/<selected-backlog-item-id>/.
Write QA evidence under 08-qa and update PROJECT_STATE.yaml and SOURCE_OF_TRUTH.yaml.
Before closure, reconcile every backlog pointer and status registry that references the current or next backlog item, including PROJECT_STATE.yaml, SOURCE_OF_TRUTH.yaml, HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml, affected capability traceability.yaml files, local-solution-runbook.yaml/.md and any QA/security indexes.
Run the verifiable HOP backlog closure audit before marking the item closed: parse YAML, sweep stale active/current/next backlog ids, sweep evidence and registries for limited/blocked/failing gate states, run `git diff --check`, confirm evidence metrics match command output, commit when allowed and confirm `git status --short` is clean. If any audit item fails or is not run, do not claim completion, do not advance `next_backlog_item`, and report the item as incomplete or blocked.
Stop before starting the next backlog item.
```

Expected result:

- Code changes.
- Tests.
- Security quality evidence in YAML and Markdown.
- QA evidence in YAML and Markdown.
- Registry updates.

## Prompt 5: Close Module

Instruction:

```text
Close the selected HOP module.
Validate all backlog items for the module, confirm all required tests and QA evidence are present, confirm traceability is complete, and update project state and source registries.
Re-run or verify non-limited passed evidence for all required backend, frontend, mobile, contract, dependency, coverage, build and security gates, including debt-first execution, best-practice/standards, duplicate-code, complexity, OWASP/secure-code and message externalization/i18n reviews. Do not close the module or recommend the next module while any required gate is not executed, limited by the environment, blocked by missing toolchain, blocked by network or blocked by unsupported runtime.
Recommend the next module only after closeout evidence is written and all mandatory executable gates have passed or are explicitly not applicable.
```

Expected result:

- Closeout evidence in YAML and Markdown.
- Updated `PROJECT_STATE.yaml`.
- Updated `SOURCE_OF_TRUTH.yaml`.
- Next module recommendation.

## Next Backlog Item

`COM-MOD-009-PORTAL-002 Doctor portal commercial workflow` is closed.
Continue with:

- Module: `COM-MOD-009`
- Backlog item: `COM-MOD-009-APP-001`
- Previous backlog item: `COM-MOD-009-PORTAL-002` (closed)
- Paused functional backlog item: none
- Folder: `07-implementation/mobile-app/`

Mandatory setup for this backlog:

- Compile patient mobile workflow.
- Preserve the employee portal coverage floor of 86.47%, the mobile TypeScript foundation floor of 98.87%, the patient-portal floor of 89.58% and the doctor-portal floor of 89.86%; the backend Java/Maven floor is 80.60% — do not regress below it if backend code is touched.
- Keep the work agent-agnostic; do not introduce named-agent, vendor-agent or runtime-specific dependencies.
- Do not advance the backlog pointer if Node, npm, Docker, dependency, vulnerability, coverage, build or static-analysis gates cannot run.
- Before commit, reconcile `PROJECT_STATE.yaml`, `SOURCE_OF_TRUTH.yaml`, this prompt file, affected capability traceability files and the local runbook pointers.

### Previous Backlog Item (Closed)

`COM-MOD-009-PORTAL-002` — Doctor portal commercial workflow. Rebuilt login, permission-filtered
dynamic navigation, referred-patients/results/orders/notifications views, localization switcher,
and explicit loading/empty/error/no-permission/session-expired states, replacing a stale
employee-portal-domain scaffold; added backend least-privilege enforcement (doctorId-filtered
diagnostic orders, a new ReferringDoctorAuthorizationPort Spring Modulith named interface, 3 new
interceptor self-access blocks); raised doctor portal Vitest coverage to 89.86%, closing
TD-FE-009.
