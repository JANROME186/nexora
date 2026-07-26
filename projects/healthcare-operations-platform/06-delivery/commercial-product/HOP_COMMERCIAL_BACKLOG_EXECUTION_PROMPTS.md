# HOP Commercial Backlog Execution Prompts

Artifact ID: `HOP-COM-PROMPTS-001`
Version: `1.0.0`
Status: `approved`

The machine-readable source is `HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md`.

## Purpose

Use this playbook after `MVP-MOD-001 Platform Foundation` to continue HOP toward a commercial product. It tells an agent how to select the next backlog item, generate capability package models, validate them, compile generated outputs, implement custom rule points and close each capability group.

Current next backlog item: `COM-MOD-014-CLOSEOUT` - Module closeout and registry update for Imaging Operations.

Previous backlog item: `COM-MOD-014-QA-001` is closed. Validated Imaging Operations integration and report evidence across backend and employee portal, removed the DICOM hardcoded-IP lint finding, corrected ImagingReportsScreen JSX markup, expanded imaging UI coverage to 249/249 passing tests, confirmed backend tests 497/497 passing with 29 skipped local-db tests, employee-portal coverage 90.85% overall / 90.87% screens, build clean and production audit clean.


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

Before feature work, the agent must review `08-qa/technical-debt/technical-debt-index.md` and
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
- Synchronized `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, this prompt file, runbooks, indexes and
  capability traceability.
- Commit hash and clean `git status --short` when commits are allowed.

If any audit item fails or is not run, the item is incomplete or blocked. The agent must keep
`next_backlog_item` unchanged and write exact remediation steps.

## Required Rule

If `BUSINESS_REQUIREMENT.md` is missing, stop. That file is requester-supplied source material and is the raw input for all product analysis.

## Required Context

Load these first:

- `../../../../AGENT_BOOTSTRAP.md`
- `../../../../PROJECT_STATE.md`
- `../../../../SOURCE_OF_TRUTH.md`
- `../../../../nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md`
- `../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md`
- `../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md`
- `../../../../nexora-framework/02-standards/standards/capability-package-standard.md`
- `../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md`
- `../../../../nexora-framework/02-standards/standards/enterprise-product-foundation-standard.md`
- `../../../../nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md`
- `../../BUSINESS_REQUIREMENT.md`
- `../../PROJECT_BRIEF.md`
- `../../SOURCE_OF_TRUTH.md`
- `../../PROJECT_STATE.md`
- `../../01-product-definition/business-capabilities/bcm-001/business-capability-map.md`
- `../../01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md`
- `../mvp/healthcare-operations-platform-mvp-framework.md`
- `HOP_COMMERCIAL_PRODUCT_BACKLOG.md`
- `HOP_QUALITY_ALIGNMENT_BACKLOG.md`
- `HOP_ENTERPRISE_FOUNDATION_ALIGNMENT_BACKLOG.md`

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
Do not manually define CRUD, DTO, controller, repository, SDK, Swagger, repetitive documentation or repetitive test artifacts as implementation work; capture them in generation-plan.md as generated outputs.
Every machine-executable artifact must be YAML when applicable, with Markdown as the human companion.
```

Expected result:

- `capability-package.md`
- `business-model.md`
- `business-rules.md`
- `processes.md`
- `events.md`
- `openapi-source.md`
- `permissions.md`
- `ui-model.md`
- `mobile-model.md`
- `test-model.md`
- `observability-model.md`
- `generation-plan.md`
- `traceability.md`

## Prompt 3: Validate Capability Package Models

Instruction:

```text
Validate the selected Capability Package model set.
Confirm that all required artifacts exist, YAML is parseable, Markdown companion files exist where required, capabilities trace to BCM-001, dependencies trace to BCM-002, API surfaces are classified, and security/audit/test expectations are present.
Confirm that generation-plan.md separates generated outputs from custom implementation points.
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
Before feature work, load ../../08-qa/technical-debt/technical-debt-index.md and resolve or materially reduce at least one open debt item using the framework selection order. If no open debt exists, record that explicitly in evidence.
Generate repetitive platform artifacts from the models before writing custom code.
Keep changes scoped to the selected backlog item.
Externalize new or changed user-visible text, validation copy, error prose, status labels, error codes and repeated magic values through backend message bundles, frontend/mobile localization resources, constants, configuration or policy providers as appropriate.
Enforce enterprise foundations for the selected backlog item: map features/actions/menu items/API operations to IAM permissions, use authenticated session context instead of static users, define or update dynamic menus/actions, keep database deliverables current, follow UX/UI design-system artifacts, document public/shared contracts, keep persistence decoupled and use OpenAPI/contract-first generation where applicable.
Add or update only generated tests or custom rule tests appropriate to the backend, web, mobile, portal, integration or operations scope.
Run applicable open source security quality gates for the changed stack, including tests, build, coverage, best practices, coding standards, duplicate code, complexity, SAST/static analysis, OWASP or equivalent secure-code checks, dependency vulnerability checks across all severities, secrets scan, message externalization/i18n review and DAST when a runnable surface exists.
Load `03-architecture/technology-architecture/local-toolchain-inventory.md` before executing commands. Use its executable paths, versions, working directories and generic command templates. If a listed required tool is stale or missing, update the inventory when the correct value is known; otherwise create or update technical debt before closure.
Build a required-validation matrix for every changed stack. If a required category applies but HOP lacks an executable script, plugin or tool configuration, create or update technical debt before closure. Do not use "if configured", "if scripts exist" or undocumented `not_applicable` as closure evidence when the stack or runnable surface exists.
For OWASP Dependency-Check, use the configured local advisory database. Do not refresh/download the NVD database during ordinary backlog execution; that is a manual once-per-day responsibility of the project operator or security reviewer. Record the database path and freshness timestamp/date in evidence.
If Maven, Java, Node, npm, native packages, Docker, database services, network access or audit endpoints are missing or blocked, first attempt documented remediation or request approval. If still unavailable, mark the backlog `blocked_by_environment` or `ready_for_external_validation`, keep `next_backlog_item` on the current backlog item, write exact remediation commands, and stop. Manual source review is only a compensating control and cannot replace executable gates.
Do not close the backlog item with unresolved vulnerabilities of any severity, missing duplicate/complexity/OWASP analysis, missing required validation tooling without registered technical debt, missing debt-first action, or unexternalized user-facing messages/magic strings unless an immediate accepted-risk/debt disposition exists with owner and target backlog.
Write security quality evidence under 08-qa/security-quality/<selected-backlog-item-id>/.
Write QA evidence under 08-qa and update PROJECT_STATE.md and SOURCE_OF_TRUTH.md.
Before closure, reconcile every backlog pointer and status registry that references the current or next backlog item, including PROJECT_STATE.md, SOURCE_OF_TRUTH.md, HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md, affected capability traceability.md files, local-solution-runbook.md/.md and any QA/security indexes.
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
- Updated `PROJECT_STATE.md`.
- Updated `SOURCE_OF_TRUTH.md`.
- Next module recommendation.

## Next Backlog Item

`COM-MOD-017-CLOSEOUT Marketplace readiness closeout and registry update` is closed. Module `COM-MOD-017` is `module_closed`.
Continue with:

- Module: `COM-MOD-014`
- Backlog item: `COM-MOD-014-DEF`
- Previous backlog item: `COM-MOD-017-CLOSEOUT` (closed)
- Paused functional backlog item: none
- Folder: `01-product-definition/business-capabilities/packages/` (new BCM-IMG-001..008 capability package models)

Mandatory setup for this backlog:

- Load `03-architecture/technology-architecture/local-toolchain-inventory.md` before running any build, test, coverage, SAST, dependency, DAST, Docker or database command.
- Prefer the latest compact handoff in `08-qa/handoffs/` and use targeted `rg`/line reads instead of loading complete historical registries into the prompt.
- When handing this backlog to another execution agent, generate a compact prompt with `nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py` if local Python is available.
- At closure, create `08-qa/handoffs/COM-MOD-014-DEF-summary.md` with `Status`, `Cambios Clave`, `Deuda Técnica Creada` and `Siguiente Paso`.
- Reconcile `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, this prompt file, capability traceability files and the local runbook pointers before closure.
- Preserve coverage floors: backend (84.65%), employee portal (90.68%), public website (98.61%), mobile (99.21%), patient portal (94.11%), and doctor portal (96.28%).
- Keep the work agent-agnostic; do not introduce named-agent, vendor-agent or runtime-specific dependencies.
- Before feature work, review `08-qa/technical-debt/technical-debt-index.md` and materially reduce at least one applicable open technical-debt item or record why no relevant debt can be reduced without widening scope.
- COM-MOD-014 (Imaging Operations) depends on `MVP-MOD-003`, `MVP-MOD-004`, `MVP-MOD-007`, `MVP-MOD-008` and `COM-MOD-012`, all closed.
- Model BCM-IMG-001 through BCM-IMG-008 capability packages (imaging appointment, reception, study management, DICOM/PACS integration, dictation, signature and delivery) per the standard capability package template.
- Do not advance the backlog if Maven, Java, Node, npm, Docker, database, dependency scan, coverage or static-analysis execution is blocked; request support or keep the item open with exact remediation steps.

### Previous Backlog Item (Closed)

`COM-MOD-017-CLOSEOUT` - Marketplace readiness closeout and registry update. Marked `BCM-PLT-011` `module_closed` in `capability-package.md` and `capability-package-index.md` (moved from `active_capability_package_groups` to `completed_capability_package_groups`); added a `closeout:` section to `traceability.md`. Documentation and registry-only closeout -- no source changed; backend (484 tests, 84.65%) and employee-portal (224 tests/65 files, 90.68%) figures re-affirmed unchanged from `COM-MOD-017-QA-001`. Confirmed `TD-BE-018`/`TD-BE-019`/`TD-BE-020` closed; `TD-FE-012` re-confirmed open/non-blocking. Found and corrected two stale `technical-debt-index.md` coverage baselines (`backend_java_maven` 84.53% -> 84.65%, `frontend_typescript_web` 89.75% -> 90.68%) never synced from prior evidence. Found and registered new debt **TD-WEB-001** -- `ui-model.md`'s `PUBLIC_MARKETPLACE_LISTING` public_website surface was modeled but never compiled (`COM-MOD-017-WEB-001` never scheduled); non-blocking, outward discovery surface only. REL-003 Commercial General Availability is now fully complete (`COM-MOD-013`, `COM-MOD-016` and `COM-MOD-017` all `module_closed`). Active backlog item advanced to `COM-MOD-014-DEF`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-COM-PROMPTS-001
  type: backlog-execution-prompts
  name: HOP Commercial Backlog Execution Prompts
  version: 1.0.0
  status: approved
  human_readable: HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
  machine_readable: HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
  created_date: 2026-07-08
  owner: Nexora Product Architecture Team
rules:
- These prompts are operational instructions only; source artifacts remain authoritative.
- Agents must load YAML files before Markdown for legacy structured automation artifacts;
  agents must load compact Markdown handoffs first when a <TASK_ID>-summary.md exists.
- Agents must not continue if BUSINESS_REQUIREMENT.md is missing.
- Agents must not require a vendor-specific agent, runtime, prompt extension or configuration.
- Agents must prefer open source, self-hostable and standards-based technologies unless
  an ADR approves an exception.
- Agents must produce security quality evidence for every code-changing backlog item.
- Before feature implementation in any code-changing backlog item, agents must review
  08-qa/technical-debt/technical-debt-index.md and resolve or materially reduce
  at least one open technical-debt item, unless no open debt exists.
- As HOP advances, agents must increase technical-debt burn-down intensity; late module,
  release and commercial-readiness iterations must reduce multiple relevant debt items
  when open debt remains.
- HOP cannot be marked commercially complete, GA-ready or finally closed while any
  technical debt item remains open.
- Agents must run stack-appropriate checks for best practices, coding standards, duplicate
  code, complexity, OWASP or equivalent secure coding, dependency vulnerabilities
  across all severities, secrets, coverage and message externalization/i18n.
- Agents must load 03-architecture/technology-architecture/local-toolchain-inventory.md
  before running build, test, quality, security or local runtime commands, and must
  use or update its tool paths instead of rediscovering them every iteration.
- Agents must use nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
  before handing a backlog to any execution agent.
- Ollama with an approved open source model is the primary Nexora Framework prompt
  orchestration runtime; missing Ollama/model is a framework bootstrap blocker, not
  an optional skip.
- Agents must use lazy loading for continuation context: prefer the latest <TASK_ID>-summary.md
    handoff, then inspect only relevant lines with rg or targeted reads; do not paste
    complete YAML/MD files into prompts unless explicitly required.
- New task handoffs must be Markdown with minimal YAML frontmatter and must stay under
  200 tokens where practical.
- New monolithic YAML task/state artifacts are discouraged; existing YAML remains
  supported until migrated through registered technical debt.
- If a required quality category applies to a changed stack but HOP lacks an executable
  script, plugin or tool configuration, the agent must create or update technical
  debt before closure; "if configured", "if scripts exist" and undocumented not-applicable
  dispositions are not valid closure evidence.
- Agents must target at least 80 percent line coverage for every applicable delivered
  stack. If a stack is below 80 percent during an intermediate iteration, the previous
  measured coverage is the hard lower bound and coverage must never decrease.
- If a changed stack remains below 80 percent line coverage, agents must target a
  3 to 5 percentage point improvement in that iteration; smaller improvements require
  explicit justification, maximum meaningful in-scope tests and immediate coverage
  debt.
- Agents must enforce enterprise product foundations before continuing customer-facing portal/app work: es-MX/en-US
    localization, language switching, IAM permission mapping, dynamic menus/actions,
    login/session context, product database deliverables, UX/UI design baseline, code
    documentation, persistence architecture and OpenAPI/contract-first generation
    review.
- Agents must externalize new or changed user-facing text, validation copy, error
  prose, status labels, error codes and repeated magic values through message catalogs,
  constants, configuration or policy providers.
- Agents must not close or advance a backlog item with passed_with_execution_limitation,
  closed_with_execution_limitation, not_executed mandatory gates, blocked toolchains,
  unsupported runtimes or blocked dependency/audit endpoints. Manual source review
  is only a compensating control and cannot replace executable tests, build, coverage,
  audit or required backend validation gates.
- Agents must not claim done, finished, closed or ready for the next backlog until
  the verifiable HOP backlog closure audit passes and is recorded in evidence or handoff.
- A valid HOP closure requires YAML parse, stale-pointer sweep, evidence-state sweep,
  git diff --check, synchronized PROJECT_STATE.md/SOURCE_OF_TRUTH.md/prompt/runbook/
  traceability pointers, matching command-output metrics, commit hash and clean git
  status.
- Agents must execute capability packages in dependency order unless an explicit mock
  strategy is documented.
- Agents must not manually create repetitive CRUD, DTO, controller, repository, SDK,
  Swagger, documentation or test artifacts when they can be generated from models.
- Agents must update project registries and QA evidence before closing any backlog
  item.
required_context:
  repository_level:
  - ../../../../AGENT_BOOTSTRAP.md
  - ../../../../PROJECT_STATE.md
  - ../../../../SOURCE_OF_TRUTH.md
  - ../../../../nexora-framework/00-start-here/FRAMEWORK_EXECUTION_SEQUENCE.md
  - ../../../../nexora-framework/02-standards/standards/agent-agnostic-standard.md
  - ../../../../nexora-framework/02-standards/standards/model-driven-product-engineering-standard.md
  - ../../../../nexora-framework/02-standards/standards/capability-package-standard.md
  - ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  - ../../../../nexora-framework/02-standards/standards/context-efficient-execution-standard.md
  - ../../../../nexora-framework/02-standards/standards/enterprise-product-foundation-standard.md
  - ../../../../nexora-framework/05-prompts/prompts/context-optimized-backlog-prompts.md
  - ../../../../nexora-framework/05-prompts/prompts/security-quality-gate-prompts.md
  - ../../../../nexora-framework/04-recipes/recipes/agent-to-mvp-recipe.md
  project_level:
  - ../../BUSINESS_REQUIREMENT.md
  - ../../PROJECT_BRIEF.md
  - ../../SOURCE_OF_TRUTH.md
  - ../../PROJECT_STATE.md
  - ../../01-product-definition/business-capabilities/bcm-001/business-capability-map.md
  - ../../01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md
  - ../../02-domain-definition/domain-foundation/context-map/context-map.md
  - ../../02-domain-definition/domain-foundation/shared-kernel/shared-kernel.md
  - ../../02-domain-definition/domain-foundation/aggregates/aggregate-catalog.md
  - ../mvp/healthcare-operations-platform-mvp-framework.md
  - HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  - HOP_QUALITY_ALIGNMENT_BACKLOG.md
  - HOP_ENTERPRISE_FOUNDATION_ALIGNMENT_BACKLOG.md
prompt_sequence:
- id: HOP-COM-PROMPT-001
  name: Select next executable backlog item
  intent: Determine the next backlog item to execute using project state and commercial
    backlog dependency order.
  input:
  - ../../PROJECT_STATE.md
  - HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  expected_output:
  - selected_module_id
  - selected_backlog_item_id
  - dependency_status
  - blocking_gaps
  - execution_plan
  prompt: 'Load the HOP project state and HOP commercial product backlog.

    Prefer the latest compact handoff under ../../08-qa/handoffs/ when it exists.

    Use targeted rg/read operations for only the active backlog lines; do not preload
    whole registries into the commercial prompt.

    Select the next executable backlog item by dependency order.

    If any dependency is incomplete, stop and report the exact blocker.

    If the selected item changes code, load 08-qa/technical-debt/technical-debt-index.md
    and include the required debt-first action in the execution plan.

    If the selected item is a definition item, prepare the capability package modeling
    plan.

    If the selected item is an implementation item, verify that the definition package
    exists first.

    '
- id: HOP-COM-PROMPT-002
  name: Generate capability package models
  intent: Create all required capability package model artifacts before compilation
    or implementation.
  input:
  - HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  - ../../01-product-definition/business-capabilities/bcm-001/business-capability-map.md
  - ../../01-product-definition/business-capabilities/bcm-002/capability-dependency-map.md
  - ../../02-domain-definition/domain-foundation/context-map/context-map.md
  - ../../02-domain-definition/domain-foundation/aggregates/aggregate-catalog.md
  expected_output:
  - capability-package.md
  - business-model.md
  - business-rules.md
  - processes.md
  - events.md
  - openapi-source.md
  - permissions.md
  - ui-model.md
  - mobile-model.md
  - test-model.md
  - observability-model.md
  - generation-plan.md
  - traceability.md
  prompt: 'Generate the complete Capability Package model set for every capability
    in the selected HOP roadmap group.

    Use the commercial product backlog as sequencing context, BCM-001 for capabilities,
    BCM-002 for dependency profiles, and the domain foundation for bounded contexts
    and aggregate ownership.

    Place each package under 01-product-definition/business-capabilities/packages/.

    Do not implement code during this step.

    Do not manually define CRUD, DTO, controller, repository, SDK, Swagger, repetitive
    documentation or repetitive test artifacts as implementation work; capture them
    in generation-plan.md as generated outputs.

    Existing machine-executable YAML remains valid. New task handoffs must be Markdown
    with minimal frontmatter; new structured artifacts may use YAML only when automation
    requires it.

    '
- id: HOP-COM-PROMPT-003
  name: Validate capability package models
  intent: Confirm that generated capability packages satisfy MDPE, traceability and
    agent-agnostic requirements.
  input:
  - selected_capability_package_folders
  - ../../SOURCE_OF_TRUTH.md
  - HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  expected_output:
  - validation_report
  - missing_artifacts
  - traceability_gaps
  - agent_agnostic_findings
  - ready_for_compilation_decision
  prompt: 'Validate the selected Capability Package model set.

    Confirm that all required artifacts exist, YAML is parseable, Markdown companion
    files exist where required, capabilities trace to BCM-001, dependencies trace
    to BCM-002, API surfaces are classified, and security/audit/test expectations
    are present.

    Confirm that generation-plan.md separates generated outputs from custom implementation
    points.

    Scan for named-agent dependencies or vendor-specific execution requirements.

    If any blocking gap exists, update the package before allowing compilation or
    implementation.

    '
- id: HOP-COM-PROMPT-004
  name: Compile and implement selected backlog item
  intent: Compile generated outputs and implement only custom rule points for one
    backlog item at a time.
  input:
  - selected_backlog_item
  - selected_capability_package_folders
  - ../../07-implementation
  - ../../08-qa
  expected_output:
  - code_changes
  - tests
  - security_quality_evidence_yaml
  - security_quality_evidence_md
  - qa_evidence_yaml
  - qa_evidence_md
  - registry_updates
  prompt: 'Implement only the selected backlog item.

    If preparing work for another execution agent, first generate a compact prompt
    using the mandatory local Python/Ollama context orchestrator.

    Load the selected Capability Package models first and follow existing project
    implementation patterns.

    Before feature work, load ../../08-qa/technical-debt/technical-debt-index.md
    and resolve or materially reduce at least one open debt item using the framework
    selection order. If no open debt exists, record that explicitly in evidence.

    Determine whether the current HOP phase requires higher debt burn-down intensity.
    Early MVP work reduces at least one relevant debt item; module closeout, release
    preparation and late commercial work must reduce multiple relevant items when
    open debt remains.

    Generate repetitive platform artifacts from the models before writing custom code.

    Keep changes scoped to the selected backlog item.

    Externalize new or changed user-visible text, validation copy, error prose, status
    labels, error codes and repeated magic values through backend message bundles,
    frontend/mobile localization resources, constants, configuration or policy providers
    as appropriate.

    Add or update only generated tests or custom rule tests appropriate to the backend,
    web, mobile, portal, integration or operations scope.

    Run or document applicable open source security quality gates for the changed
    stack, including best practices, coding standards, duplicate code, complexity,
    SAST/static analysis, OWASP or equivalent secure-code rules, dependency vulnerability
    checks across all severities, secrets scan, coverage, message externalization/i18n
    review and DAST when a runnable surface exists.

    Load 03-architecture/technology-architecture/local-toolchain-inventory.md before
    executing commands. Use its executable paths, versions, working directories and
    generic command templates. If a listed required tool is stale or missing, update
    the inventory when the correct value is known; otherwise create or update technical
    debt before closure.

    Build a required-validation matrix for every changed stack. If a required category
    lacks an executable HOP script, plugin or tool configuration, create or update
    a technical-debt item under 08-qa/technical-debt/ with owner, target backlog,
    acceptance criteria and blocking decision before closure.

    Do not record missing duplicate-code, complexity, SAST/static analysis, OWASP/secure-code,
    dependency, secrets, coverage, i18n or DAST tooling as merely not applicable when
    the stack or runnable surface exists.

    For OWASP Dependency-Check, use the configured local advisory database. Do not
    refresh/download the NVD database during ordinary backlog execution; that is a
    manual once-per-day responsibility of the project operator or security reviewer.
    Record the database path and freshness timestamp/date in evidence.

    Record current line coverage, previous iteration line coverage baseline and the
    80 percent target for every changed stack. If current coverage is below 80 percent,
    it must not decrease below the previous baseline and the target gap must remain
    tracked as technical debt.

    Mandatory executable gates must actually run before closure. If Maven, Java, Node,
    npm,

    native packages, Docker, database services, network access or audit endpoints
    are missing or

    blocked, first attempt documented remediation or request approval. If still unavailable,
    mark

    the backlog blocked_by_environment or ready_for_external_validation, keep next_backlog_item
    on

    the current backlog item, write exact remediation commands, and stop. Do not use
    manual source

    review to convert an unexecuted mandatory gate into a pass.

    Do not close the backlog item with unresolved vulnerabilities of any severity,
    missing duplicate/complexity/OWASP analysis, missing required validation tooling
    without registered technical debt, coverage below the previous iteration baseline,
    missing debt-first action, or unexternalized user-facing messages/magic strings
    unless an immediate accepted-risk/debt disposition exists with owner and target
    backlog.

    Write security quality evidence under 08-qa/security-quality/<selected-backlog-item-id>/.

    Write QA evidence under 08-qa and update PROJECT_STATE.md and SOURCE_OF_TRUTH.md.

    Write 08-qa/handoffs/<selected-backlog-item-id>-summary.md with Status, Cambios
    Clave, Deuda Técnica Creada y Siguiente Paso. Keep it compact and do not duplicate
    the full execution log.

    Before closure, reconcile every backlog pointer and status registry that references
    the

    current or next backlog item, including PROJECT_STATE.md, SOURCE_OF_TRUTH.md,

    HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md, affected capability traceability.md
    files,

    local-solution-runbook.md/.md and any QA/security indexes. The current backlog
    item must

    be consistently closed, and the next_backlog_item pointer must match PROJECT_STATE.md.

    Run the verifiable HOP backlog closure audit before marking the item closed:

    - parse all HOP YAML outside dependency/build folders;

    - search stale active/current/next backlog ids, previous next-backlog ids and
    ready_for_next_backlog_item mismatches;

    - search changed evidence and registries for not_executed, failed, passed_with_execution_limitation,
    closed_with_execution_limitation, blocked_by_missing_toolchain, blocked_by_network
    and blocked_by_unsupported_runtime;

    - run git diff --check;

    - confirm tests, coverage, vulnerability and static-analysis numbers recorded
    in evidence match actual command output;

    - commit when all gates pass and commits are allowed;

    - confirm git status --short is clean after commit.

    If any audit item fails or is not run, do not claim completion, do not advance
    next_backlog_item, and report the item as incomplete or blocked with exact remediation
    steps.

    Stop before starting the next backlog item.

    '
- id: HOP-COM-PROMPT-005
  name: Close module
  intent: Validate and close a completed module before moving to the next dependency.
  input:
  - selected_module_folder
  - ../../PROJECT_STATE.md
  - ../../SOURCE_OF_TRUTH.md
  - ../../08-qa
  expected_output:
  - closeout_evidence_yaml
  - closeout_evidence_md
  - updated_project_state
  - updated_source_of_truth
  - next_module_recommendation
  prompt: 'Close the selected HOP module.

    Validate all backlog items for the module, confirm all required tests and QA evidence
    are present, confirm traceability is complete, and update project state and source
    registries.

    Re-run or verify non-limited passed evidence for all required backend, frontend,
    mobile,

    contract, dependency, coverage, build and security gates, including debt-first
    execution,

    best-practice/standards, duplicate-code, complexity, OWASP/secure-code and message

    externalization/i18n reviews. Do not close the module or recommend

    the next module while any required gate is not_executed, passed_with_execution_limitation,

    closed_with_execution_limitation, blocked_by_missing_toolchain, blocked_by_network
    or

    blocked_by_unsupported_runtime.

    Confirm coverage did not decrease below the previous measured baseline for any
    applicable stack.

    Final HOP product closure is not allowed unless every applicable stack reaches
    at least 80

    percent line coverage and every technical-debt item is closed.

    Recommend the next module only after closeout evidence is written and all mandatory
    executable

    gates have passed or are explicitly not applicable.

    '
validation_commands:
  yaml_parse:
    intent: Validate YAML syntax for source artifacts.
    command_template: Parse all YAML files outside generated dependency folders and
      fail on syntax errors.
  registry_references:
    intent: Confirm source registries point to existing files or folders.
    command_template: Validate SOURCE_OF_TRUTH.md and PROJECT_MANIFEST.md references.
  agent_agnostic_scan:
    intent: Confirm no artifact requires a specific named agent or vendor runtime.
    command_template: Scan source artifacts for named-agent or vendor-specific requirements
      and resolve findings.
  security_quality_gate:
    intent: Confirm open-source-first and security quality checks were run or documented
      for code-changing work.
    command_template: Load 03-architecture/technology-architecture/local-toolchain-inventory.md,
      verify required tool paths, then run applicable open source tests, build, coverage,
      best-practice and standards checks, duplicate-code checks, complexity checks,
      SAST/static analysis, OWASP/secure-code checks, dependency vulnerability checks
      across all severities, secrets scan, message externalization/i18n review and
      DAST where applicable. Build a required-validation matrix for every changed
      stack; if a required category lacks an executable HOP script, plugin or tool
      configuration, create or update technical debt with owner, target backlog, acceptance
      criteria and blocking decision before closure. Do not use 'if configured' or
      'if scripts exist' as a closure condition. For OWASP Dependency-Check or equivalent
      local-advisory-database tools, run the scanner against the local database available
      at execution time and record freshness; do not refresh/download the database
      unless explicitly assigned. Address at least one open technical-debt item before
      feature work unless no open debt exists; increase debt burn-down intensity as
      the project advances. Coverage target is 80 percent; if below target, do not
      allow coverage to drop below the previous measured baseline. If mandatory executable
      gates cannot run, stop as blocked_by_environment or ready_for_external_validation
      and do not advance next_backlog_item.
  verifiable_closure_audit:
    intent: Prevent false backlog closure and stale handoffs.
    command_template: Before claiming completion, parse HOP YAML, sweep stale backlog
      pointers, sweep evidence/registries for limited or blocked gate states, run
      git diff --check, confirm evidence metrics match command output, commit when
      allowed, and verify git status --short is clean. If any item fails or is not
      run, keep next_backlog_item unchanged and report incomplete or blocked.
  git_whitespace:
    intent: Confirm no whitespace errors before commit.
    command_template: Run repository whitespace validation before closing the item.
  module_id: COM-MOD-014
  backlog_item_id: COM-MOD-014-QA-001
  name: Imaging integration and report evidence
  expected_folder: 07-implementation/employee-portal/ and 07-implementation/backend/
  required_debt_first_action: none
  coverage_floor:
    backend_java_maven_line_coverage_percent_if_backend_is_touched: 84.65
    frontend_typescript_web_line_coverage_percent: 90.68
    mobile_typescript_foundation_line_coverage_percent: 99.21
    patient_portal_typescript_web_line_coverage_percent: 94.11
    doctor_portal_typescript_web_line_coverage_percent: 96.28
    public_website_typescript_web_line_coverage_percent: 98.61
    final_target_percent: 80
  mandatory_execution_notes:
  - Resume functional work only from the compact generated prompt and COM-MOD-014-FE-001
    handoff; do not preload broad YAML registries.
  - Keep execution agent-agnostic and preserve the open-source-first stack and quality
    gates.
  - Address or reduce at least one applicable technical-debt item before feature work.
  - Preserve backend coverage at or above 84.65% and employee-portal coverage at or
    above 90.68%; keep final project target at 80% or higher.
  - Generate QA/security evidence, update SOURCE_OF_TRUTH, PROJECT_STATE, product
    backlog and execution prompts, and commit only when validation passes.
  previous_backlog_item:
    backlog_item_id: COM-MOD-014-QA-001
    status: closed
    summary: Validated Imaging Operations integration and report evidence across backend and employee portal, fixed the DICOM hardcoded-IP lint finding, corrected ImagingReportsScreen JSX markup, expanded imaging UI tests to 249/249 passing, confirmed backend tests 497/497 passing with 29 skipped local-db tests, employee-portal coverage 90.85% overall / 90.87% screens, build clean and production audit clean.

```
