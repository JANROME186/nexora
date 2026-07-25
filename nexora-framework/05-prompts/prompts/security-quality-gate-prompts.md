# Security and Quality Gate Prompts

**Artifact ID:** `NXF-SQ-PROMPTS-001`
**Status:** Approved
**Machine-readable source:** `security-quality-gate-prompts.md`
**Version:** `1.5.2`

Use these prompts with the Open Source First Security and Quality Standard, the Local Toolchain
Inventory Standard, the Enterprise Product Foundation Standard and the Engineering Excellence
Prioritization Standard.

## Client Proposed Stack Market Validation

When a requester, client or existing project proposes a technology stack, validate it before using it
as the implementation baseline. The agent must inventory runtimes, frameworks, package managers,
build tools, databases, deployment technology and quality tools, then compare them against current
stable or LTS versions from official sources.

The output must include:

- Stack inventory.
- Official sources checked.
- Current stable or LTS version decisions.
- Market-practice comparison.
- Required quality toolchain by stack and layer.
- Risks and gaps.
- Immediate changes to apply.
- Technical-debt items to create or update.
- Selected stack baseline.

The required toolchain must cover best practices, coding standards, duplicate code, complexity,
OWASP or stack-equivalent secure coding, dependency vulnerabilities across all severities, secrets,
coverage, architecture rules when applicable and message externalization/i18n.

For Java/Maven, consider SonarLint, SonarCloud only when hosted SaaS use is approved, JaCoCo, PMD,
PMD CPD, SpotBugs, Checkstyle, Maven Surefire Plugin, OWASP Dependency-Check, Trivy, Spotless,
Google Error Prone, ArchUnit, PIT/Pitest, CycloneDX SBOM, Maven Enforcer, Maven Versions Plugin,
License Maven Plugin or License Checker, Revapi, Duplicate Finder Maven Plugin, Find Security Bugs,
OpenRewrite and Semgrep CE according to applicability.

For TypeScript web, consider TypeScript strict mode, ESLint with `typescript-eslint`,
`eslint-plugin-security`, `eslint-plugin-sonarjs`, Semgrep CE, Vitest or Jest, Istanbul/c8 coverage,
`jscpd`, Prettier, `npm audit`, OSV-Scanner or Trivy, CycloneDX npm tools or Syft, an actively
maintained license checker, OWASP ZAP, accessibility checks and i18n literal-string linting.

For mobile TypeScript or Flutter/Dart, apply the matching mobile baseline from the standard and add
MobSF when native artifacts are available.

If a better, safer, more current or actively maintained tool replaces a framework baseline tool, the
agent must document the proposed substitution, create framework feedback and update project
technical debt before closing the analysis.

The agent must classify findings as:

- `P0`: closure blocker for changed scope.
- `P1`: technical debt with target backlog and acceptance criteria.
- `P2`: contextual/desirable, not blocking unless risk promotes it.

## Open-Source-First Assessment

Load `open-source-first-security-quality-standard.md`, review the changed technology choices, and confirm that open source, self-hostable and standards-based options are preferred. If a proprietary mandatory dependency appears, require an ADR exception before continuing.

The review must not be limited to the stack selected at project inception. Treat the original stack as
the current baseline and evaluate whether newer open source frameworks, runtimes, libraries or tools
would materially improve security, maintainability, portability, cost or ecosystem health.

## Backlog Gate

For every code-changing backlog item, load the project local toolchain inventory first, verify the
listed paths and versions for the changed stack, then run applicable checks:

- Technical-debt first action: resolve or materially reduce at least one open debt item before
  feature work, unless no debt exists.
- Debt burn-down intensity: early MVP iterations must reduce at least one relevant debt item when
  debt exists; module closeout, release preparation and late commercial iterations must reduce
  multiple relevant items or justify why only one could be safely reduced.
- Tests.
- Best-practice and coding-standard checks.
- SAST/static analysis.
- Duplicate-code checks.
- Complexity checks.
- OWASP or stack-equivalent secure-code checks.
- Dependency vulnerability scan across all severities.
- Secrets scan.
- Coverage.
- Message externalization, i18n and magic-string review.
- Enterprise product foundation review: localization, IAM permissions, dynamic menus/actions, login/session context, database deliverables, UX/UI, code documentation, persistence architecture and OpenAPI/contract-first generation.
- Contract quality.
- DAST when a runnable web/API surface exists.
- Container/IaC scan when deployment assets change.
- Technology evolution review.
- Stack-specific quality toolchain completeness review.
- Technical-debt backlog update when non-blocking modernization findings exist.

For every changed stack, the agent must write or update a required-validation matrix. Required
categories such as duplicate-code, complexity, SAST/static analysis, OWASP or stack-equivalent
secure-code checks, dependency analysis, secrets, coverage and i18n cannot be skipped because the
project lacks a script. If the product surface exists but the executable tool/script/plugin is
missing, the agent must create or update a technical-debt item with owner, target backlog,
acceptance criteria and blocking decision.

`not_applicable_with_reason` is allowed only when the surface, runtime or code type genuinely does
not exist or was not touched by the backlog item.

For tools that use a local vulnerability/advisory database, such as OWASP Dependency-Check with a
local NVD cache, the agent must execute the scanner against the database available at that moment
and record the database path plus freshness timestamp/date in evidence. The daily database refresh
is a manual responsibility of the project operator or security reviewer, not the framework or the
backlog agent. Agents should not download or refresh large advisory databases during ordinary
backlog execution unless that operational task is explicitly assigned.

If a mandatory executable gate cannot run because Maven, Java, Node, npm, a native package, Docker,
a database service, network access or an audit endpoint is missing, unsupported or blocked, the
agent must attempt documented remediation or request approval. Manual source review is only a
compensating control; it does not turn an unexecuted gate into a pass.

Write evidence under:

`08-qa/security-quality/<backlog-item-id>/`

Do not close the backlog when vulnerabilities of any severity, secure-code findings, secrets,
failing tests, missing duplicate/complexity/OWASP analysis, coverage below the previous iteration baseline,
missing required validation tooling without registered technical debt, missing enterprise foundation
evidence, missing debt-first execution, hard-coded messages/magic strings or unexecuted mandatory
gates remain unresolved. Do not use
`passed_with_execution_limitation` or `closed_with_execution_limitation` as final states. Use
`blocked_by_environment` or `ready_for_external_validation`, keep `next_backlog_item` unchanged,
and write exact remediation commands.

Before saying the backlog is complete, run the verifiable backlog closure audit:

- Parse project YAML outside dependency/build folders.
- Sweep active/current/next backlog pointers for stale ids.
- Sweep evidence and registry files for limited, failed or blocked gate states.
- Run `git diff --check`.
- Confirm evidence numbers match command output.
- Commit when allowed and confirm `git status --short` is clean.

If any audit item fails or is not run, report the backlog as incomplete or blocked, not complete.

## Technology Debt Backlog

When a modernization, migration or quality-tooling finding is beneficial but not immediately required
to complete the backlog safely, register it under:

`08-qa/technical-debt/`

Each item must describe the affected components, current state, recommended target state, risk,
urgency, effort, cost impact, migration strategy, incremental remediation triggers and acceptance
criteria. Remediation should be gradual and preferably attached to future backlog work that already
touches the affected component.

Open debt may be scheduled gradually during normal delivery, but it cannot remain open when a
project is marked finished, commercially complete or GA-ready.

## Coverage Gate

The target line coverage is `80%` for every applicable delivered stack. Intermediate backlog items
may remain below 80% only when coverage does not decrease below the previous iteration baseline and
the path to 80% is tracked as technical debt.

When a changed stack remains below 80%, the iteration must target a 3 to 5 percentage point line
coverage improvement. Smaller improvements require explicit justification, maximum meaningful
in-scope tests and immediate coverage debt assigned to the next relevant backlog.

Each evidence package must record:

- Current line coverage by stack.
- Previous iteration baseline by stack.
- Delta from the previous baseline.
- 80% target gap.
- Debt item or remediation backlog when the target is not reached.

Final project closure cannot be approved unless every applicable delivered stack reaches at least
80% line coverage.

## Module Closeout Gate

Before closing a module, aggregate backlog evidence and confirm that the module meets the required
security and quality gates. Also review the project technology debt index and confirm each item is
resolved, accepted, prioritized or explicitly deferred with rationale.

Module closeout must re-run or verify non-limited passed evidence for all required backend,
frontend, mobile, API, dependency, coverage, build and security gates, including duplicate-code,
complexity, OWASP/secure-code and message externalization reviews. Do not recommend the next
module while any required gate is `not_executed`, `passed_with_execution_limitation`,
`closed_with_execution_limitation`, `blocked_by_missing_toolchain`, `blocked_by_network` or
`blocked_by_unsupported_runtime`.

Run the verifiable backlog/module closure audit before updating next module pointers. The audit must
include YAML parse, stale-pointer sweep, evidence-state sweep, `git diff --check`, commit hash and
clean git status.

## Final Project Closure Gate

Before marking a project finished, commercialized or GA-ready, confirm that:

- No technical debt remains open, accepted, in progress, materially reduced or partially resolved.
- Every applicable delivered stack has at least 80% line coverage.
- All security-quality evidence is non-limited and executable gates have actually run.

If any of these checks fail, create or update a release-blocking remediation backlog and do not
close the project.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-SQ-PROMPTS-001
  type: prompt-playbook
  name: Security and Quality Gate Prompts
  version: 1.5.2
  status: approved
  human_readable: security-quality-gate-prompts.md
  machine_readable: security-quality-gate-prompts.md
  owner: Nexora Engineering
purpose: Provide reusable agent-agnostic prompts for open-source-first technology
  selection and security quality gate validation.
required_context:
- nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
- nexora-framework/02-standards/standards/local-toolchain-inventory-standard.md
- nexora-framework/02-standards/standards/enterprise-product-foundation-standard.md
- nexora-framework/02-standards/standards/engineering-excellence-prioritization-standard.md
- nexora-framework/02-standards/standards/agent-agnostic-standard.md
- target_project/SOURCE_OF_TRUTH.md
- target_project/PROJECT_STATE.md
- target_project/08-qa/
prompts:
- id: PROMPT-OSS-000
  name: Client proposed stack market validation
  intent: Validate a requester-proposed or existing project stack against current
    open source market practice before accepting it as the implementation baseline.
  expected_output:
  - stack_inventory
  - official_sources_checked
  - current_stable_or_lts_version_decisions
  - market_practice_comparison
  - required_quality_toolchain_by_stack
  - required_quality_toolchain_by_layer
  - risks_and_gaps
  - immediate_changes_to_apply
  - technical_debt_items_to_create_or_update
  - selected_stack_baseline
  prompt: 'Load the Open Source First Security and Quality Standard and the Engineering
    Excellence Prioritization Standard.

    Identify the requester-proposed stack, the current project stack and every runtime,
    framework, package manager, build tool, database, deployment technology and quality
    tool that materially affects development.

    Validate each stack element against current stable or LTS versions using official
    documentation, release notes, lifecycle pages, official security advisories and
    trusted vulnerability sources. Treat blogs, tutorials and marketing claims as
    supporting context only, not authority.

    Compare the proposed stack with current open source ecosystem practice for security,
    maintainability, portability, cost, maturity, hiring risk, deployment fit and
    available quality gates.

    Define the minimum open source quality toolchain required by each stack and layer.
    The toolchain must cover best practices, coding standards, duplicated code, complexity,
    OWASP or stack-equivalent secure coding, dependency vulnerabilities across all
    severities, secrets, coverage, architecture rules when applicable and message
    externalization/i18n.

    For Java/Maven, consider SonarLint, SonarCloud only when hosted SaaS use is approved,
    JaCoCo, PMD, PMD CPD, SpotBugs, Checkstyle, Maven Surefire Plugin, OWASP Dependency-Check,
    Trivy, Spotless, Google Error Prone, ArchUnit, PIT/Pitest, CycloneDX SBOM, Maven
    Enforcer, Maven Versions Plugin, License Maven Plugin or License Checker, Revapi,
    Duplicate Finder Maven Plugin, Find Security Bugs, OpenRewrite and Semgrep CE
    according to applicability.

    For TypeScript web, consider TypeScript strict mode, ESLint with typescript-eslint,
    eslint-plugin-security, eslint-plugin-sonarjs, Semgrep CE, Vitest or Jest, Istanbul/c8
    coverage, jscpd, Prettier, npm audit, OSV-Scanner or Trivy, CycloneDX npm tools
    or Syft, license checker, OWASP ZAP, accessibility checks and i18n literal-string
    linting.

    For mobile TypeScript or Flutter/Dart, apply the matching mobile baseline from
    the standard and add MobSF when native artifacts are available.

    If a better, safer, more current or actively maintained tool replaces a framework
    baseline tool, document the proposed substitution, create framework feedback and
    update project technical debt before closing the analysis.

    Apply immediately any upgrade or replacement required to remove security findings
    that cannot be safely accepted, unsupported runtimes, incompatible quality gates
    or blocking build failures.

    For beneficial but non-blocking upgrades, migrations or tooling gaps, create or
    update technical-debt items under 08-qa/technical-debt/ with risk, effort, cost,
    affected components and gradual remediation triggers.

    Classify quality and engineering gaps as P0, P1 or P2. P0 gaps block closure for
    changed scope; P1 gaps must become technical debt with target backlog; P2 gaps
    must not block delivery unless project risk promotes them.

    Produce a selected_stack_baseline and stack_toolchain_baseline for architecture
    or backlog evidence.

    '
- id: PROMPT-OSS-001
  name: Open-source-first technology assessment
  intent: Confirm that new or changed technology choices prefer open source, portable
    and standards-based options.
  expected_output:
  - selected_technologies
  - open_source_assessment
  - technology_evolution_review
  - license_summary
  - proprietary_exceptions_required
  - lock_in_risks
  - technical_debt_items_to_create_or_update
  - recommendation
  prompt: 'Load the Open Source First Security and Quality Standard and the Engineering
    Excellence Prioritization Standard.

    Review the changed architecture, dependencies, runtime tools and implementation
    choices.

    If the requester or existing project proposes a stack, first run the client proposed
    stack market validation prompt and use its selected_stack_baseline as input.

    Prefer open source, self-hostable, standards-based technologies.

    Do not limit the review to the technology stack chosen at project inception. Treat
    the original stack as the current baseline and evaluate whether newer open source
    frameworks, runtimes, libraries or quality tools materially improve security,
    maintainability, portability, cost or ecosystem health.

    If a proprietary or paid-only dependency is introduced as mandatory, stop unless
    an ADR exception exists or is created.

    If a beneficial upgrade or migration is identified but is not required to safely
    finish the current backlog item, create or update a technical-debt backlog item
    under 08-qa/technical-debt/ with impact, risk, effort, estimated cost, affected
    components and incremental remediation triggers.

    Produce the open_source_first_assessment section for the backlog security-quality
    evidence.

    '
- id: PROMPT-SQ-001
  name: Backlog security and quality gate
  intent: Run or document all security and quality checks required for a code-changing
    backlog item.
  expected_output:
  - security-quality-evidence.md
  - security-quality-evidence.md
  - pass_fail_decision
  - blocking_findings
  - accepted_risks
  - technical_debt_first_action
  - duplicate_code_summary
  - complexity_summary
  - owasp_or_secure_code_summary
  - message_externalization_summary
  - technical_debt_backlog_updates
  prompt: 'For the selected backlog item, identify changed backend, web, mobile, API,
    dependency, container and deployment surfaces.

    Before implementing feature code, load 08-qa/technical-debt/technical-debt-index.md.
    If open debt exists, select at least one item by the standard''s selection order
    and resolve or materially reduce it before feature work. If no open debt exists,
    record that explicitly in evidence.

    Determine the project''s current phase and debt-remediation intensity. Early MVP
    iterations must reduce at least one relevant debt item when debt exists; module
    closeout, release preparation and later commercial iterations must reduce multiple
    relevant debt items or justify why only one could be safely reduced.

    Load the project local toolchain inventory before executing commands. Use its
    executable paths, versions, working directories and generic command templates
    instead of rediscovering tools. If the inventory is missing, stale or incomplete
    for a required tool, update it when the correct value is known or register technical
    debt before closure.

    Probe the required toolchain first (runtime versions, build tools, package managers,
    native build binaries, Docker/database services and command paths) and compare
    it with the project runbook and stack baseline.

    Run applicable open source checks for tests, best practices, coding standards,
    duplicate code, complexity, SAST/static analysis, OWASP or equivalent secure-code
    rules, dependency vulnerabilities across all severities, secrets, coverage, contract
    quality and DAST when a runnable surface exists.

    For every changed stack, build an explicit required-validation matrix with one
    row per required category: tests, best practices, coding standards, duplicate
    code, complexity, SAST/static analysis, OWASP or equivalent secure-code checks,
    dependency vulnerabilities, secrets, coverage, i18n/message externalization, contract
    checks, DAST when runnable, container/IaC checks when changed and license/SBOM
    when dependencies or releasable artifacts change.

    Do not write "if scripts exist", "if configured" or "not applicable" for a required
    category when the stack or surface exists. If the project lacks the executable
    tool, script, plugin or configuration, create or update a technical-debt item
    under 08-qa/technical-debt/ before closure. The debt must include the missing
    validation category, expected open source tool or equivalent, affected stack/component,
    risk, owner, target backlog, acceptance criteria and whether the gap blocks the
    current backlog.

    Use not_applicable_with_reason only when the product surface, runtime or changed
    code type genuinely does not exist or was not touched by the backlog item.

    For tools that use a local vulnerability/advisory database (for example OWASP
    Dependency-Check with a local NVD cache), do not refresh or download that database
    during ordinary backlog execution unless the backlog explicitly assigns that operational
    task. Execute the scanner against the locally available database, record the database
    path and freshness timestamp/date in evidence, and report stale/missing database
    refresh as an operator prerequisite. The daily database refresh is a manual responsibility
    of the project operator or security reviewer, not the framework or backlog agent.

    For each changed stack, record current line coverage, previous iteration coverage
    baseline and target coverage. The target is 80%. If current coverage is below
    80%, it may remain below target only for an intermediate iteration when it does
    not decrease below the previous baseline and a technical-debt item tracks the
    path to 80%.

    If a changed stack remains below 80%, target a 3 to 5 percentage point line-coverage
    improvement in the current iteration. If that is not feasible for the slice, document
    why, add the maximum meaningful in-scope tests, preserve the previous baseline
    and register immediate coverage debt for the next relevant backlog.

    If a mandatory executable gate cannot run because Maven, Java, Node, npm, a native
    package, Docker, database service, network endpoint or equivalent toolchain dependency
    is missing, unsupported or blocked, attempt documented remediation or request
    approval for the required install/network access. Do not replace the gate with
    manual source review.

    Confirm the stack-specific quality toolchain is complete for the changed surface.
    For Java/Maven changes, evaluate the Java baseline tools from the standard and
    run, add, document as not applicable or register technical debt for each relevant
    category.

    Review new or changed user-visible messages, validation copy, error prose, status
    labels, domain error codes and repeated magic strings or values. Externalize them
    through the stack''s message catalog, constants, configuration or policy provider,
    or register immediate technical debt with owner and target backlog.

    Validate enterprise product foundations for the changed scope: es-MX/en-US localization,
    language switching when UI/app is touched, IAM permission mapping, dynamic menus/actions,
    authenticated session context, product database deliverables, UX/UI design-system
    alignment, stack-appropriate code documentation, persistence architecture and
    OpenAPI/contract-first generation review.

    Run a technology evolution review even when the backlog item does not introduce
    a new stack. Compare the touched components and dependencies against current open
    source, security, maintenance and ecosystem signals.

    Do not ignore low, medium, informational or unknown vulnerability findings. Fix
    them or provide accepted-risk disposition with an immediate target backlog according
    to the standard.

    Do not block normal delivery for non-security modernization findings that are
    not required to safely complete the current slice. Instead, register them as gradual
    technical debt under 08-qa/technical-debt/ and link them to future backlog work
    that touches the affected components.

    Use the project''s existing toolchain first. Add lightweight open source tools
    only when they fit the stack and can run reproducibly without vendor lock-in.

    Write evidence under 08-qa/security-quality/<backlog-item-id>/.

    Do not close the backlog item when vulnerabilities of any severity, secure-code
    findings, secrets, failing tests, missing duplicate/complexity/OWASP analysis,
    missing required validation tooling without a registered technical-debt item and
    blocking decision, coverage below the previous iteration baseline, unresolved
    hard-coded messages/magic strings, missing enterprise foundation evidence, missing
    debt-first action or unexecuted mandatory gates remain unresolved.

    Do not use passed_with_execution_limitation or closed_with_execution_limitation
    as final states. Use blocked_by_environment or ready_for_external_validation,
    keep next_backlog_item unchanged, and write exact remediation commands when required
    gates cannot run in the current environment.

    Before saying the backlog is complete, run the verifiable backlog closure audit:
    parse project YAML, sweep stale backlog pointers, sweep evidence/registry files
    for limited or blocked gate states, run git diff --check, confirm evidence numbers
    match command output, commit if allowed, and confirm git status --short is clean.
    If any audit item fails or is not run, report the backlog as incomplete or blocked,
    not complete.

    '
- id: PROMPT-SQ-002
  name: Module closeout security and quality gate
  intent: Validate aggregate module quality before moving to the next module.
  expected_output:
  - aggregate_security_quality_report
  - module_closeout_decision
  - release_blockers
  - unresolved_technology_debt_summary
  - debt_first_execution_summary
  prompt: 'Before closing a module, aggregate all backlog security-quality evidence
    for the module.

    Confirm SAST/static analysis, best-practice and standards checks, duplicate-code
    checks, complexity checks, OWASP or secure-code checks, dependency scans across
    all severities, coverage, DAST where applicable, tests, contract checks, SBOM,
    license review, stack market validation, message externalization/i18n review and
    technology evolution reviews are present or explicitly not applicable with reasons.

    For every applicable category that is missing because no executable tool, script,
    plugin or configuration exists, confirm a technical-debt item exists with owner,
    target backlog and acceptance criteria. Do not accept "not configured" or "if
    script exists" as closure evidence.

    Confirm each code-changing iteration addressed at least one existing technical-debt
    item before feature work, unless no open debt existed at that iteration start.

    Confirm debt burn-down intensity is appropriate for the project phase. The closer
    the product is to release or project completion, the more open debt must be resolved
    during the iteration or closeout. Do not close a module when debt is merely accumulating
    without a scheduled burn-down plan.

    Confirm aggregate and per-stack coverage. The target is 80% line coverage. If
    a stack is below 80%, confirm the current value is not lower than the previous
    iteration baseline and that the technical-debt backlog contains a path to reach
    80%.

    If a stack remains below 80%, confirm relevant iterations targeted a 3 to 5 percentage
    point improvement or contain explicit justification and immediate coverage debt.

    Confirm enterprise product foundations remain satisfied for module scope: localization,
    IAM permission mapping, dynamic menus/actions, login/session context, product
    database deliverables, UX/UI, code documentation, persistence architecture and
    contract-first generation review.

    Re-run required executable gates or verify that each has already passed in non-limited
    evidence. Do not accept passed_with_execution_limitation, closed_with_execution_limitation,
    not_executed, blocked_by_missing_toolchain, blocked_by_network or blocked_by_unsupported_runtime
    as closure evidence.

    Review 08-qa/technical-debt/ and confirm all technology debt discovered during
    the module is accepted, prioritized, resolved or explicitly scheduled for immediate
    or gradual remediation according to risk.

    Update module closeout QA evidence and PROJECT_STATE.md.

    Do not recommend the next module when unresolved vulnerabilities of any severity
    lack disposition, when secure-code findings remain open, when debt-first execution
    is missing, when coverage decreased below the previous baseline, or when any required
    executable gate has not actually run.

    Run the verifiable backlog/module closure audit before updating next module pointers.
    The audit must include YAML parse, stale-pointer sweep, evidence-state sweep,
    git diff --check, commit hash and clean git status.

    '
- id: PROMPT-SQ-004
  name: Final project debt and coverage closure gate
  intent: Validate that a project can be marked finished, commercialized or GA-ready.
  expected_output:
  - final_debt_register_status
  - final_coverage_report_by_stack
  - release_readiness_decision
  - blockers
  prompt: 'Load the Open Source First Security and Quality Standard, PROJECT_STATE.md,
    SOURCE_OF_TRUTH.md, the stack quality baseline, all security-quality evidence
    and 08-qa/technical-debt/technical-debt-index.md.

    Confirm there are no open, accepted, in_progress, partially_resolved or materially_reduced
    technical-debt items. A project cannot be marked finished, commercially complete
    or GA-ready while technical debt remains open.

    Confirm every applicable delivered stack has at least 80% line coverage. Include
    backend, frontend web, mobile app and other executable stacks when applicable.

    If coverage is below 80% or technical debt remains open, stop and create or update
    a release-blocking remediation backlog. Do not mark the project closed.

    If all debt is closed and coverage is at least 80% for every applicable stack,
    write final closure evidence and update PROJECT_STATE.md.

    '
- id: PROMPT-SQ-003
  name: Technology debt backlog update
  intent: Convert non-blocking modernization, migration or quality-tooling findings
    into gradual technical debt backlog items.
  expected_output:
  - technical-debt-index.md
  - technical_debt_item_yaml
  - remediation_triggers
  - backlog_recommendation
  prompt: 'Load the Open Source First Security and Quality Standard, the Engineering
    Excellence Prioritization Standard and the target project technical debt index.

    For each non-blocking finding related to outdated dependencies, deprecated frameworks,
    unsupported runtimes, quality tooling gaps, lock-in risk or recommended open source
    migration, create or update a debt item under 08-qa/technical-debt/.

    Include affected components, current state, recommended target state, reason,
    risk, urgency, estimated effort, estimated cost impact, migration strategy, incremental
    remediation triggers, dependencies and acceptance criteria.

    Prefer gradual remediation tied to normal backlog work that already touches the
    affected components.

    Promote the debt item to blocking when the finding has no accepted-risk disposition,
    prevents required quality gates from running, or involves an unsupported runtime
    that makes continued development unsafe.

    '
```
