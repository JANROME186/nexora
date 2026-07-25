# Open Source First Security and Quality Standard

**Artifact ID:** `NXF-OSS-SEC-QUAL-001`
**Status:** Approved
**Machine-readable source:** `open-source-first-security-quality-standard.md`
**Version:** `1.5.2`

## Purpose

Nexora solutions should be cost-efficient, portable, secure and maintainable from the beginning. The default technology strategy is **open source first**: use open, self-hostable, standards-based technologies unless a documented exception is approved.

The same standard requires continuous security and quality gates while the product is built, so debt is detected during each backlog item instead of being deferred to the end.

## Technology Evolution

The initial project stack is a baseline, not a permanent constraint. During every code-changing
backlog item, dependency change, module closeout and release gate, agents must evaluate whether the
current stack still remains the best open-source-first option for security, maintainability,
portability, cost and ecosystem health.

Agents must detect:

- Unsupported or end-of-life runtimes.
- Deprecated frameworks or libraries.
- Safer or better maintained open source alternatives.
- Security-relevant major upgrades.
- License or governance changes.
- Quality tooling gaps caused by old platform versions.
- Technology choices that increase lock-in or future migration cost.

If the change is critical or blocking, it must be handled before closing the backlog item unless an
accepted risk exists. If the change is beneficial but not immediately required, the agent must create
or update a technical-debt backlog item and propose gradual remediation when normal backlog work
touches the affected component.

## Open Source First

Agents and teams must prefer open source frameworks, libraries, tools and standards with clear licenses, active maintenance, broad community support and portable runtime models.

Proprietary, paid-only or closed-source dependencies are allowed only when an ADR documents:

- Business reason.
- Open source alternatives evaluated.
- Total cost of ownership impact.
- Lock-in risk.
- Migration or exit strategy.
- Security and compliance impact.
- Approval decision.

## Required Quality Gates

Every code-changing backlog item must produce security and quality evidence under:

`08-qa/security-quality/<backlog-item-id>/`

Required checks, when applicable:

- Unit, integration and contract tests.
- SAST or static analysis for the changed stack.
- Dependency vulnerability analysis across all severities supported by the selected tool.
- Secrets scan.
- Best-practice and coding-standard analysis for the changed stack.
- Duplicate-code analysis.
- Complexity analysis.
- OWASP Top 10 or stack-equivalent secure-code analysis.
- Code coverage report.
- Message externalization, i18n and magic-string review.
- DAST when a runnable web/API surface exists.
- Container and infrastructure scan when deployment assets change.
- License review when dependencies change.
- Technology evolution review against current open source options.
- Technical-debt backlog update when non-blocking upgrade or migration findings exist.

## Missing Tooling Is Debt

Required quality categories cannot be skipped because the project lacks a script, plugin or local
tool configuration. If a changed stack requires duplicate-code, complexity, SAST/static analysis,
OWASP or stack-equivalent secure-code, dependency, secrets, coverage, i18n or any other mandatory
validation and the tool is not configured, the agent must create or update a technical-debt item
before closure.

`not_applicable_with_reason` is valid only when the product surface or runtime genuinely does not
exist or was not touched. It is not valid when the surface exists but the project has not implemented
the validation tooling.

Every missing required validation must be dispositioned as one of:

- Implemented and `passed`.
- `failed` with remediation.
- `blocked_by_environment` or `ready_for_external_validation` with exact commands.
- `technical_debt_registered_with_blocking_decision` with owner, target backlog, acceptance criteria and a blocking
  decision.

Security-sensitive gaps, including vulnerability analysis, secrets scanning and secure-code/SAST,
must be treated as blocking unless an accepted-risk disposition exists with expiration and a target
backlog.

## Vulnerability Database Refresh

Tools such as OWASP Dependency-Check may use a large local advisory database. Nexora separates two
responsibilities:

- The project operator or security reviewer refreshes the advisory database manually once per day,
  or before release-readiness validation.
- Agents execute the configured analysis against the advisory database available at execution time
  and document the database location plus freshness timestamp/date in QA and security-quality
  evidence.

Agents must not spend ordinary backlog execution time downloading or refreshing large vulnerability
databases unless a backlog explicitly assigns that operational task. A scan can close when the tool
runs successfully against the available local database and reports zero unresolved findings, with
database freshness recorded. Missing or stale advisory data is an operational prerequisite for the
human operator, not framework implementation work.

## Enterprise Stack Quality

Every changed layer must be reviewed with stack-appropriate open source tooling:

- Backend.
- Frontend web.
- Mobile app.
- API contracts.
- Infrastructure and containers.
- Integrations.
- Generated code when it is part of the delivered runtime.

The review must cover best practices, standards, duplicated code, complexity, OWASP or equivalent
secure-coding checks, dependency vulnerabilities, secrets, coverage, architecture boundaries when
defined, and message externalization/i18n.

Filtering vulnerability scans to only `HIGH` and `CRITICAL` is not enough. The scan must include all
severities supported by the tool, and every finding must be fixed, triaged or recorded as an
immediate remediation item with owner, target backlog and expiration.

## Debt First

Before any code-changing backlog item starts feature implementation, the agent must inspect:

`08-qa/technical-debt/technical-debt-index.md`

If open debt exists, at least one item must be resolved or materially reduced first. Selection order:

- Blocking or security-related debt.
- Debt affecting the stack or component being touched.
- Highest urgency, then highest risk.
- Oldest open item.

If no open debt exists, the evidence must explicitly say so. A backlog item cannot close when open
debt existed at the start and no debt item was addressed before feature work.

Debt remediation must become stricter as the product advances. Early MVP iterations may materially
reduce one relevant item, but module closeout, release preparation and commercial readiness must
reduce multiple relevant items when debt remains. A project must not be marked finished,
commercially complete, GA-ready or fully closed while any technical debt remains open.

## Coverage Policy

The default minimum line coverage target is `80%` for every applicable delivered stack. This target
does not have to block every intermediate backlog item, but it does determine final project closure:
if the project is below 80% coverage, it cannot be closed as complete.

When a stack is below 80%, the previous iteration's measured coverage becomes the hard lower bound
for the next iteration. Coverage must improve whenever feasible and must never go down. If the
target is not reached during an iteration, the evidence must record the current coverage, the prior
baseline, the delta, the reason the 80% target was not reached and the technical-debt item that will
continue the improvement.

## Messages And I18n

Applications must be multilingual-ready by design. Agents must not leave new user-visible text,
validation copy, error prose, status labels, repeated magic strings or configurable business values
hard-coded inside implementation code.

Expected patterns:

- Backend: stable domain error codes, resource bundles/message catalogs, policy/configuration
  providers, constants, enums or value objects for repeated identifiers.
- Frontend web: localization dictionaries, stable API error-code mapping, constants for routes,
  permissions, statuses, query keys and feature flags.
- Mobile app: platform localization resources such as ARB, JSON, ICU messages or equivalent.

Tests should assert stable codes and parameters instead of prose messages when possible.

## Technical-Debt Backlog

Every project should maintain technology debt under:

`08-qa/technical-debt/`

The machine-readable index is:

`08-qa/technical-debt/technical-debt-index.md`

Each debt item must include the affected components, current state, recommended target state,
reason, risk, urgency, estimated effort, cost impact, migration strategy, incremental remediation
triggers and acceptance criteria.

Technology debt should be remediated gradually. Do not rewrite stable components just because a new
tool exists; instead, split migrations into small backlog items and address them when the affected
component is already being modified. Security-critical or unsupported runtime findings may be
promoted to blocking work.

## Client Stack Market Validation

A requester-proposed stack is input, not an automatic mandate. During project analysis,
architecture definition, MVP definition, code-changing backlog work, module closeout and release
readiness, agents must validate the proposed and current stack against current open source market
practice.

The validation must compare:

- Requester-proposed stack and current project stack.
- Current stable or LTS versions from official sources.
- Current open source alternatives used by the ecosystem.
- Quality gate tooling available for the stack.
- Licensing, total cost of ownership and lock-in risk.
- Security advisories and known CVEs.
- Maintenance activity, release cadence and compatibility with deployment targets.

Agents must prefer supported stable or LTS versions. They must not adopt a new major version only
because it exists; they must evaluate stability, ecosystem support, security posture, migration cost
and compatibility with the required quality gates.

When an update is required to remove security risk that cannot be safely accepted, unsupported
runtimes, incompatible quality gates or blocking build failures, it must be handled before closing
the backlog item. Beneficial but
non-blocking updates must be registered as technical debt for gradual remediation.

## Quality Gate Execution Policy

Executable gates must actually run before a backlog item, module validation, module closeout or
release gate is marked closed. Manual source review, contract cross-checking and code inspection are
valid compensating controls, but they do not replace tests, builds, coverage, dependency audits or
vulnerability scans for runnable code.

Before closing code-changing work, agents must verify required runtime versions and command paths
against the project runbook and stack baseline. Examples include Java, Maven, Node, npm, Docker,
Flutter and database services. If a tool is missing, unsupported or too old, the agent must first
attempt project-local remediation using documented wrappers, checked-in settings, package
installation, containerized services or approved toolchain paths. If remediation needs network or
elevated permissions, the agent must request approval instead of downgrading validation.

Allowed final gate states are:

- `passed`
- `not_applicable_with_reason`
- `technical_debt_registered_with_blocking_decision` for missing non-blocking tooling gaps with
  owner, target backlog and acceptance criteria

The following states are not allowed for closure:

- `not_executed`
- `blocked_by_missing_toolchain`
- `blocked_by_network`
- `blocked_by_unsupported_runtime`
- `failed`
- `passed_with_execution_limitation`
- `closed_with_execution_limitation`

If the execution environment cannot satisfy the stack baseline, the agent must mark the work
`blocked_by_environment` or `ready_for_external_validation`, keep the active backlog pointer on the
current item, write exact remediation commands, and stop. A later agent or CI runner may close the
item only after executing the missing gates and updating the evidence to `passed`.

Module validation and closeout must not rely on implementation evidence that is itself limited. Any
missing Maven, Java, Node, native package, audit endpoint, Docker or database dependency is a
blocking environment issue until resolved in a compatible local or CI environment.

## Verifiable Backlog Closure

Agents must not say a backlog item is done, finished, completed, closed or ready for the next
backlog unless the repository proves it. Before closure, the agent must run a pre-closure audit and
record the result in QA/security evidence or the final handoff.

The mandatory pre-closure audit is:

- Confirm the selected backlog id matches `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, backlog
  prompt files and capability traceability.
- Confirm every mandatory executable gate for every changed stack ran, or that prior non-limited
  evidence is explicitly referenced.
- Confirm no required gate is `not_executed`, `failed`, `blocked_by_missing_toolchain`,
  `blocked_by_network`, `blocked_by_unsupported_runtime`, `passed_with_execution_limitation` or
  `closed_with_execution_limitation`.
- Confirm measured coverage did not drop below the previous baseline.
- Confirm vulnerabilities, secrets and secure-code findings across all supported severities are
  fixed or have accepted-risk/debt disposition with owner, target backlog and acceptance criteria.
- Confirm technical debt was resolved or materially reduced before feature work unless no debt
  existed at iteration start.
- Confirm QA and security-quality evidence exists in YAML and Markdown when applicable, and that
  reported numbers match command output.
- Confirm `PROJECT_STATE.md`, `SOURCE_OF_TRUTH.md`, indexes, runbooks, prompt pointers and
  capability traceability are synchronized.
- Parse YAML, run stale-pointer/evidence-state sweeps, run `git diff --check`, commit when allowed,
  and confirm `git status --short` is clean after commit.

A handoff summary that claims completion but lacks command results, evidence paths, pointer
synchronization result, clean git status and commit hash is not valid closure evidence. The next
reviewer must treat it as unverified.

Final project closure is stricter than iteration closure. It requires no open technical debt and at
least 80% line coverage for each applicable delivered stack. Exceptions can be accepted during an
iteration, but not for final project closure.

## Open Source Tooling Baseline

Preferred open source tools include:

- SAST/static analysis: Semgrep, SpotBugs, PMD, Checkstyle, Error Prone, ESLint, TypeScript strict mode, `dart analyze`, `flutter analyze`.
- DAST: OWASP ZAP baseline/API scans.
- Dependency and vulnerability analysis: OSV-Scanner, OWASP Dependency-Check, npm audit, pip-audit, Trivy, Grype.
- SBOM: Syft and CycloneDX tools.
- Coverage: JaCoCo, Vitest coverage, Istanbul, c8, Flutter coverage and lcov.
- IaC/container quality: Checkov, TFLint, Hadolint, Trivy.
- API quality: Spectral and Schemathesis.

## Toolchain Evolution

Tool baselines are living standards. During analysis, backlog work, module closeout and release
readiness, agents must compare the current baseline against official tool status, ecosystem
practice, license/hosting changes and runtime compatibility.

If an agent detects a new tool, a better replacement, a deprecated tool or a license/hosting change,
it must document:

- Affected stack.
- Current baseline tool.
- Proposed new or replacement tool.
- Reason for the change.
- License and hosting model.
- Migration impact.
- Project technical-debt items to update.
- Framework feedback or framework backlog item for Nexora to review.

Hosted tools such as SonarCloud may be used only when approved by the organization and must not
become mandatory for local development unless an exception is documented.

### Java Maven Baseline

For Java/Maven services, the framework expects a stack-specific quality toolchain similar to:

| Category | Open source tool | Main use |
|---|---|---|
| IDE feedback | SonarLint | Real-time review in common IDEs. |
| Centralized quality analysis | SonarCloud | Hosted quality/security hotspot analysis when SaaS use is approved; not mandatory for local development. |
| Test execution | Maven Surefire Plugin | Unit test execution and reporting. |
| Quality / bugs | SpotBugs | Bytecode bug analysis. |
| Code security | Find Security Bugs | SpotBugs rules for injection, weak cryptography and related security issues. |
| Style | Checkstyle | Team conventions, formatting and structural rules. |
| Formatting | Spotless | Reproducible formatting and import hygiene. |
| Static analysis | PMD | Bad practices, dead code and complexity. |
| Duplication | PMD CPD | Copy/paste detection. |
| Duplicate dependencies/classes | Duplicate Finder Maven Plugin | Duplicate classes and resource conflict detection. |
| Coverage | JaCoCo | Unit and integration test coverage. |
| Dependency vulnerabilities | OWASP Dependency-Check | CVE detection in Maven dependencies. |
| Containers / filesystem / secrets | Trivy | CVEs, SBOM, licenses, misconfigurations and secrets. |
| SBOM | CycloneDX Maven Plugin | Direct and transitive dependency inventory. |
| Build rules | Maven Enforcer | Java/Maven versions, duplicate dependencies and disallowed dependencies. |
| Dependency currency | Maven Versions Plugin | Dependency, plugin and parent version drift detection. |
| Licenses | License Maven Plugin / License Checker | Header and dependency license policy. |
| Advanced testing | PIT / Pitest | Mutation testing for test effectiveness. |
| Architecture | ArchUnit | Architecture rules for layers, packages, DDD or hexagonal boundaries. |
| Refactor / technical debt | OpenRewrite | Automated migrations and refactors. |
| API compatibility | Revapi | Binary/source compatibility checks for public Java APIs. |
| Compiler static analysis | Google Error Prone | Compile-time Java bug pattern detection. |
| Additional SAST | Semgrep CE | Security and bug rules for Java in CI/CD. |

This table is a baseline, not a universal command to install every tool immediately. If a tool is
not safe or practical to adopt during the active backlog item, the agent must document the reason and
register technical debt unless the missing tool blocks release or hides unresolved security risk.

### TypeScript Web Baseline

For TypeScript web applications, the framework expects:

- TypeScript strict mode.
- Vitest or Jest for tests.
- Vitest coverage, Istanbul or c8 for coverage.
- ESLint with `typescript-eslint`.
- `eslint-plugin-security`, `eslint-plugin-sonarjs` and Semgrep CE for secure-code and complexity checks.
- `jscpd` for duplicated code.
- Prettier for formatting.
- `npm audit`, OSV-Scanner or Trivy for dependency vulnerabilities across all severities.
- CycloneDX npm tools or Syft for SBOM.
- An actively maintained license checker.
- OWASP ZAP and accessibility checks when a runnable UI exists.
- i18n/message catalog tooling such as i18next or react-intl plus literal-string lint rules.

### Mobile Baselines

For TypeScript mobile applications such as React Native or Expo, use the TypeScript web baseline
adapted to mobile, plus React Native Testing Library and MobSF when native artifacts are available.

For Flutter/Dart applications, use `dart analyze`, `flutter analyze`, `dart format`,
`flutter test --coverage`, lcov, OSV-Scanner or Trivy, Flutter `gen-l10n` with ARB resources, and
MobSF when native artifacts are available.

### Infrastructure Baseline

For containers and infrastructure, use Trivy or Grype, Checkov, TFLint, Hadolint, Syft or CycloneDX
tools, and all-severity vulnerability/misconfiguration scanning.

## Fail Conditions

A backlog item must not be closed when it introduces:

- New or unresolved vulnerabilities of any severity without remediation, accepted risk and an
  immediate target backlog.
- Secrets.
- SAST or secure-code findings without disposition.
- Failing tests.
- Undocumented coverage regression.
- Missing best-practice, coding-standard, duplicate-code, complexity or OWASP/secure-code analysis
  for the changed stack.
- Open technical debt at iteration start when no debt item was resolved or materially reduced before
  feature work.
- New hard-coded user-facing messages, validation copy, error prose, status labels, configurable
  values or repeated magic strings that are not externalized or dispositioned.
- Required executable quality gates that were not actually run.
- Missing or unsupported required toolchains, runtimes, native build binaries or audit endpoints.
- Missing required quality validation tooling or scripts without a registered technical-debt item
  and explicit blocking/non-blocking decision.
- Evidence marked `passed_with_execution_limitation` or `closed_with_execution_limitation`.
- Proprietary dependency without an exception ADR.
- Manual edits to generated artifacts without a source model change.

## Evidence

Each evidence YAML must include:

- Backlog item and module.
- Changed components.
- Open-source-first assessment.
- Client stack market validation when applicable.
- Stack-specific quality toolchain baseline.
- Technical-debt first action.
- Tools and commands run.
- Results.
- Coverage summary.
- Dependency vulnerability summary.
- SAST summary.
- DAST summary when applicable.
- Secrets scan summary.
- Duplicate-code summary.
- Complexity summary.
- OWASP or secure-code summary.
- Message externalization summary.
- License summary.
- Technology evolution review.
- Technical-debt items created or updated.
- Accepted risks.
- Blocking findings.
- Final decision.

Markdown evidence is the human-readable companion; YAML remains the agent-executable source.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-OSS-SEC-QUAL-001
  type: framework-standard
  name: Open Source First Security and Quality Standard
  version: 1.5.2
  status: approved
  human_readable: open-source-first-security-quality-standard.md
  machine_readable: open-source-first-security-quality-standard.md
  owner: Nexora Engineering
purpose: 'Ensure Nexora solutions prefer open source technologies and continuously
  apply security, vulnerability, dependency, coverage and engineering quality gates
  as the product is built, reducing cost, lock-in and technical debt.

  '
technology_evolution_policy:
  default_position: 'Quality, vulnerability, dependency, framework and technology
    reviews are not permanently constrained by the technology stack selected at project
    inception. Agents must evaluate the current implementation against the best available
    open source, secure, maintainable and commercially viable options at the time
    each backlog item is executed.

    '
  review_frequency:
  - every_code_changing_backlog_item
  - every_dependency_change
  - every_module_closeout
  - every_release_readiness_gate
  - whenever an unresolved vulnerability, unsupported runtime, deprecated framework
    or major ecosystem shift is detected
  agents_must_detect:
  - unsupported_or_end_of_life_runtime
  - deprecated_framework_or_library
  - safer_open_source_alternative
  - lower_cost_or_more_portable_open_source_alternative
  - security_relevant_major_upgrade
  - license_or_governance_change
  - ecosystem_shift_that_changes_maintainability_or_hiring_risk
  - platform_or_language_version_that_blocks_quality_tools
  - architectural_lock_in_that_can_be_reduced_by_adapters_or_migration
  decision_rule: 'Do not force immediate migration during unrelated work unless the
    finding is blocking, critical or necessary to complete the current backlog safely.
    When migration is beneficial but not immediately required, create or update a
    technical-debt backlog item with impact, risk, effort, affected components, recommended
    target state and gradual remediation plan.

    '
  source_stack_is_baseline_not_prison: true
  preferred_behavior: 'Address technology debt incrementally when normal backlog work
    touches the affected component, dependency boundary or runtime surface.

    '
client_stack_market_validation_policy:
  purpose: 'Ensure any technology stack proposed by a requester, client, prior project
    or initial architecture is validated against current open source market practice
    before implementation and during iterative backlog execution.

    '
  default_position: 'A client-proposed stack is input, not an automatic mandate. Agents
    must preserve valid business constraints, but must evaluate whether the proposed
    runtime, framework, libraries, build tools and quality tooling remain secure,
    actively maintained, cost-effective and aligned with current ecosystem practice
    at execution time.

    '
  required_when:
  - new_project_analysis
  - technology_architecture_definition
  - mvp_definition
  - every_code_changing_backlog_item
  - dependency_or_runtime_change
  - module_closeout
  - release_readiness
  agents_must_compare:
  - requester_proposed_stack
  - current_project_stack
  - current_stable_or_lts_versions_from_official_sources
  - current_open_source_alternatives_used_by_the_ecosystem
  - required_quality_gate_tooling_for_each_stack
  - licensing_and_total_cost_of_ownership
  - security_advisories_and_known_cves
  - maintenance_activity_and_release_cadence
  - compatibility_with_deployment_targets_and_team_skills
  evidence_must_include:
  - stack_inventory
  - official_sources_checked
  - current_stable_or_lts_version_decisions
  - risks_found
  - rejected_or_deferred_alternatives
  - selected_stack_baseline
  - quality_toolchain_baseline
  - immediate_changes_applied
  - technical_debt_items_created_or_updated
  - decision_rationale
  version_selection_rule: 'Prefer current stable or LTS versions that are supported
    by official maintainers and compatible with required quality gates. Do not adopt
    a newer major version only because it exists; evaluate stability, ecosystem support,
    security posture, migration cost and compatibility.

    '
  source_policy:
    preferred_sources:
    - official_project_documentation
    - official_release_notes
    - official_security_advisories
    - package_registry_metadata
    - foundation_or_vendor_lifecycle_pages
    - national_vulnerability_database_or_osv_sources
    avoid_as_authoritative:
    - unsourced_blog_posts
    - marketing_claims
    - outdated_tutorials
    - isolated_social_media_opinions
  decision_rule: 'Apply immediately any update required to remove security findings
    that cannot be safely accepted, unsupported runtimes, incompatible quality gates
    or blocking build failures. For beneficial but non-blocking improvements, create
    or update technical-debt backlog items and plan gradual remediation when the affected
    components are touched.

    '
scope:
  applies_to:
  - new_project_analysis
  - architecture_definition
  - capability_package_definition
  - backlog_implementation
  - module_closeout
  - release_readiness
  - commercial_general_availability
  project_types:
  - web
  - backend
  - mobile
  - api
  - data
  - ai
  - integration
  - deployment
quality_gate_execution_policy:
  purpose: 'Prevent backlog, module or release closure when required executable quality
    gates were not actually run. Source review and manual traceability checks are
    useful compensating controls, but they do not replace tests, builds, coverage,
    dependency audits or vulnerability scans for runnable code.

    '
  required_before_code_changing_closure:
    environment_probe:
    - Verify required runtime versions before validation (for example Java, Maven,
      Node, npm, Docker, Flutter or database services according to the stack baseline
      and local runbook).
    - Compare detected versions with the project runbook and stack toolchain baseline.
    - If a required runtime or build tool is missing, unsupported or too old, attempt
      project-local remediation first (for example documented wrapper, checked-in
      settings, package installation, containerized service or approved toolchain
      path).
    - If remediation needs network or elevated permissions, request approval instead
      of silently downgrading validation.
    mandatory_gate_results:
      allowed_to_close:
      - passed
      - not_applicable_with_reason
      - technical_debt_registered_with_blocking_decision
      not_allowed_to_close:
      - not_executed
      - blocked_by_missing_toolchain
      - blocked_by_network
      - blocked_by_unsupported_runtime
      - failed
      - passed_with_execution_limitation
      - closed_with_execution_limitation
    manual_review_rule: 'Manual source review, contract cross-checking or code inspection
      may be recorded as a compensating control only. It cannot convert a mandatory
      executable gate to passed and cannot justify advancing the backlog pointer.

      '
    closure_rule: 'A code-changing backlog item, validation backlog item, module closeout
      or release gate must not be marked closed, passed or ready for next backlog
      when a mandatory executable gate did not run. Mark the work blocked_by_environment
      or ready_for_external_validation, keep the active backlog pointer on the current
      item, and record exact remediation steps.

      '
    external_validation_rule: 'If the current execution environment cannot satisfy
      the stack baseline, the agent must produce a precise external validation command
      list and stop. The next agent or CI runner may close the item only after executing
      the missing gates and updating the evidence from blocked/limited to passed.

      '
  module_validation_and_closeout:
  - Module validation must re-run or verify successful execution evidence for every
    required backend, frontend, mobile, API, dependency and build gate touched by
    the module.
  - Module closeout must not rely on implementation backlog evidence that is itself
    passed_with_execution_limitation or closed_with_execution_limitation.
  - Any missing Maven, Java, Node, native package, audit endpoint, Docker or database
    dependency is a blocking environment issue until resolved in a compatible local
    or CI environment.
  evidence_requirements:
  - Record detected runtime/tool versions and command paths for executed gates.
  - Record exact commands executed, working directory, result and numeric summaries
    (tests, failures, errors, skipped, coverage and vulnerabilities).
  - For vulnerability tools that use a local advisory database, record the database
    freshness timestamp or the manually supplied update date.
  - Record not_applicable gates with a reason tied to changed surface.
  - Do not use passed_with_execution_limitation or closed_with_execution_limitation
    as final closure states.
  - Use blocked_by_environment or ready_for_external_validation for incomplete mandatory
    gates.
  missing_required_tooling_policy:
    purpose: 'Prevent agents from treating absent scripts or absent stack-quality
      tools as harmless comments when the changed product surface requires that validation.

      '
    rule: 'If a required quality category applies to a changed stack or runnable surface
      but the project lacks the executable tool, script or configuration to run it,
      the agent must create or update a technical-debt item before closure. The evidence
      must identify the missing category, expected open source tool or equivalent,
      affected component, risk, target backlog, owner, acceptance criteria and whether
      the gap blocks the current item.

      '
    not_applicable_boundary: 'not_applicable_with_reason is allowed only when the
      product surface, code type or runtime genuinely does not exist or was not touched
      by the backlog item. It is not allowed when the product surface exists but the
      validation tooling has not been implemented.

      '
    closure_effect: 'A missing required toolchain category is a failed gate until
      it is either implemented and executed, promoted to blocked_by_environment or
      registered as technical debt with immediate or gradual remediation according
      to risk. Security-sensitive categories, vulnerability analysis, secrets scans
      and secure-code/SAST gaps must be treated as blocking unless an accepted-risk
      disposition exists with expiration and target backlog.

      '
  local_vulnerability_database_policy:
    purpose: 'Keep vulnerability scans reproducible and fast without making agents
      responsible for refreshing large advisory databases during each backlog execution.

      '
    applies_to:
    - OWASP Dependency-Check
    - tools_using_local_nvd_osv_or_equivalent_vulnerability_databases
    daily_manual_refresh_required: true
    refresh_frequency: once_per_day_or_before_release_readiness
    responsible_party: project_operator_or_security_reviewer
    agent_responsibility:
    - Execute the configured vulnerability analysis against the local advisory database
      available at execution time.
    - Record the database location and freshness timestamp/date in QA and security-quality
      evidence.
    - Report stale or missing vulnerability data as an operational prerequisite for
      the human operator, not as a framework implementation task.
    - Do not spend backlog execution time downloading or refreshing large vulnerability
      databases unless the backlog explicitly assigns that operational task.
    agent_must_not:
    - Treat advisory database refresh as feature work.
    - Modify framework rules to bypass dependency vulnerability analysis.
    - Claim a scan passed without actually running the scanner against the currently
      available local database.
    closure_rule: 'A dependency vulnerability gate may close when the scanner executes
      successfully against the locally available advisory database, reports zero unresolved
      findings or accepted-risk dispositions, and records the advisory database freshness.
      The freshness update itself is a manual operational responsibility and is not
      performed by the framework or by ordinary backlog agents.

      '
verifiable_backlog_closure_policy:
  purpose: 'Prevent agents from declaring backlog work finished when the repository,
    evidence, gates or backlog pointers still prove otherwise.

    '
  closure_language_rule: 'Agents must not use final language such as done, finished,
    completed, closed, ready for next backlog or equivalent unless every item in the
    closure checklist is satisfied and evidenced. If any item is missing, the correct
    final state is blocked_by_environment, ready_for_external_validation, failed_validation
    or incomplete, and the active backlog pointer must remain on the current item.

    '
  mandatory_pre_closure_checklist:
  - Confirm selected backlog item id matches PROJECT_STATE.md, source of truth,
    backlog prompt file and capability traceability files.
  - Confirm all mandatory executable gates for every changed stack ran in the current
    execution or have prior non-limited evidence explicitly referenced.
  - Confirm every gate has final state passed, not_applicable_with_reason for absent
    or untouched surfaces only, or technical_debt_registered_with_blocking_decision
    for missing non-blocking tooling gaps.
  - Confirm no required gate is not_executed, failed, blocked_by_missing_toolchain,
    blocked_by_network, blocked_by_unsupported_runtime, passed_with_execution_limitation
    or closed_with_execution_limitation.
  - Confirm coverage for every changed stack is measured and is not below the previous
    measured baseline.
  - Confirm all vulnerability, secret and secure-code findings across every supported
    severity are fixed or have accepted-risk or technical-debt disposition with owner,
    target backlog and acceptance criteria.
  - Confirm at least one relevant open technical-debt item was resolved or materially
    reduced before feature work, unless no open debt existed at iteration start.
  - Confirm QA evidence and security-quality evidence exist in both YAML and Markdown
    when applicable, and that their numbers match command output.
  - Confirm PROJECT_STATE.md, SOURCE_OF_TRUTH.md, security-quality index, technical-debt
    index, runbooks, prompt pointers and capability traceability files are synchronized.
  - Confirm generated YAML parses successfully outside dependency/build folders.
  - Confirm stale pointer search found no active/current/next references to the just-closed
    backlog item after advancing.
  - Confirm repository whitespace validation passes.
  - Confirm git status is clean after commit, unless the task explicitly forbids committing;
    if committing is forbidden, report the dirty files and do not claim repository-ready
    closure.
  mandatory_validation_commands:
    yaml_parse: 'Parse every project YAML file outside generated dependency/build
      folders.

      '
    stale_pointer_sweep: 'Search active_backlog_item, current_backlog_item, next_backlog_item,
      current_active_backlog_item, ready_for_next_backlog_item and human prompt sections
      for stale current or previous backlog ids.

      '
    evidence_state_sweep: 'Search changed evidence and registry files for not_executed,
      failed, passed_with_execution_limitation, closed_with_execution_limitation,
      blocked_by_missing_toolchain, blocked_by_network and blocked_by_unsupported_runtime.

      '
    whitespace_check: git diff --check
    repository_cleanliness: git status --short
    commit_confirmation: git log -1 --oneline
  evidence_required_fields:
  - selected_backlog_item
  - final_backlog_status
  - next_backlog_item
  - commands_executed
  - command_results
  - coverage_before
  - coverage_after
  - technical_debt_burn_down
  - residual_findings
  - accepted_risks_or_debt_items
  - stale_pointer_sweep_result
  - yaml_parse_result
  - git_diff_check_result
  - git_status_after_commit
  - commit_hash
  agent_handoff_rule: 'A handoff summary that says work is complete but lacks executable
    command results, evidence paths, pointer synchronization result, clean git status
    and commit hash is not valid closure evidence. The next reviewer must treat it
    as unverified and run the closure checklist before accepting it.

    '
enterprise_stack_quality_policy:
  purpose: 'Ensure every technology stack and product layer is analyzed with enterprise
    quality, security and maintainability expectations before development continues.

    '
  applies_to_layers:
  - backend
  - frontend_web
  - mobile_app
  - api_contracts
  - infrastructure
  - integration
  - generated_code
  required_analysis_categories_for_each_changed_stack:
  - best_practices
  - coding_standards_and_style
  - duplicate_code
  - cyclomatic_or_cognitive_complexity
  - owasp_top_10_or_stack_equivalent
  - secure_coding
  - dependency_vulnerabilities_all_severities
  - secrets
  - license_and_open_source_policy_when_dependencies_change
  - coverage
  - architecture_boundaries_when_architecture_rules_exist
  - message_externalization_and_i18n
  missing_tooling_disposition:
    required: true
    rule: 'Each required analysis category must have one of these dispositions in
      security-quality evidence: passed, failed, blocked_by_environment, ready_for_external_validation,
      or technical_debt_registered_with_blocking_decision. Do not leave any required
      category as undocumented, "to evaluate", "if configured" or "not applicable"
      when the changed stack exists and lacks tooling.

      '
    technical_debt_required_fields:
    - missing_validation_category
    - expected_tool_or_equivalent
    - affected_stack
    - affected_component
    - source_backlog_item
    - risk_level
    - urgency
    - target_backlog
    - owner_or_responsible_role
    - acceptance_criteria
    - closure_blocking_decision
  required_tool_categories:
    backend:
    - unit_and_integration_tests
    - style_or_lint
    - static_analysis
    - sast_secure_code
    - duplicate_code_detector
    - complexity_detector
    - dependency_vulnerability_scan_all_severities
    - secrets_scan
    - coverage
    - architecture_rules_when_available
    - i18n_or_message_catalog_validation
    frontend_web:
    - typecheck
    - lint_or_static_analysis
    - sast_secure_code
    - duplicate_code_detector
    - complexity_detector
    - dependency_vulnerability_scan_all_severities
    - secrets_scan
    - coverage
    - production_build
    - accessibility_when_ui_changes
    - i18n_literal_scan
    mobile_app:
    - analyzer_or_lint
    - unit_widget_or_component_tests
    - sast_secure_code
    - duplicate_code_detector
    - complexity_detector
    - dependency_vulnerability_scan_all_severities
    - secrets_scan
    - coverage
    - localization_resource_validation
    infrastructure:
    - infrastructure_static_analysis
    - container_scan_all_severities
    - secrets_scan
    - misconfiguration_scan
    - sbom_when_releasable
  vulnerability_policy:
    severity_scope: all
    required_scan_configuration: 'Vulnerability and dependency scans must include
      all severities supported by the tool. Filtering only HIGH or CRITICAL is insufficient
      for backlog closure.

      '
    closure_rule: 'A backlog item must not close with unresolved known vulnerabilities
      of any severity introduced or exposed by the changed stack. If remediation is
      too large for the current slice, create an immediate technical-debt item, mark
      it as next-priority remediation, notify it in the handoff and do not hide the
      risk from module or release closeout.

      '
    accepted_risk_required_for_any_unresolved_vulnerability: true
    accepted_risk_must_include:
    - vulnerability_id_or_tool_finding
    - affected_component
    - severity
    - exploitability_or_reachability
    - business_impact
    - reason_not_fixed_now
    - target_backlog_item
    - owner_or_responsible_role
    - expiration_date
  message_externalization_policy:
    purpose: 'Keep business messages, UI labels, validation text, error descriptions,
      error codes and configurable values decoupled from implementation code so products
      are multilingual, modular and easier to evolve.

      '
    applies_to:
    - user_visible_text
    - validation_messages
    - error_titles_and_descriptions
    - domain_error_codes
    - audit_or_notification_templates
    - status_labels
    - configurable_business_thresholds
    - repeated_magic_strings_or_numbers
    backend_expectations:
    - Use stable domain error codes and message/resource bundles or equivalent message
      catalogs.
    - Return API error codes and parameters; avoid asserting prose messages in tests.
    - Keep tenant-configurable values in configuration, rule models or policy providers.
    - Use constants, value objects or enums for repeated technical identifiers.
    frontend_web_expectations:
    - Use localization dictionaries or message catalogs for visible text and validation
      copy.
    - Keep API error mapping by stable codes, not hard-coded prose.
    - Avoid magic strings for routes, permissions, statuses, query keys and feature
      flags.
    mobile_app_expectations:
    - Use platform localization resources such as ARB, JSON, ICU messages or equivalent.
    - Keep shared status/error mappings aligned with canonical API error codes.
    closure_rule: 'New or changed user-visible messages, validation text, error prose,
      status labels or repeated magic strings must be externalized or explicitly registered
      as immediate technical debt before the backlog can close.

      '
technical_debt_first_execution_policy:
  purpose: 'Prevent technical debt from accumulating as passive documentation while
    normal feature development continues.

    '
  rule: 'Before implementing any code-changing backlog item, the agent must inspect
    08-qa/technical-debt/technical-debt-index.md and select at least one open technical-debt
    item to resolve or materially reduce first.

    '
  selection_order:
  - blocking_or_security_related_items
  - items_affecting_the_stack_or_component_touched_by_the_selected_backlog
  - highest_urgency_then_highest_risk
  - oldest_open_item
  no_open_debt_rule: 'If no open technical-debt item exists, evidence must explicitly
    record that the index was reviewed and no debt-first action was required.

    '
  evidence_must_include:
  - technical_debt_index_reviewed
  - selected_technical_debt_item
  - reason_selected
  - action_taken_before_feature_work
  - validation_evidence
  - resulting_status
  closure_rule: 'A code-changing backlog item must not close unless at least one existing
    technical-debt item was resolved or materially reduced first, or the project had
    no open technical debt at iteration start.

    '
open_source_first_policy:
  default_position: Prefer open source, self-hostable, standards-based technologies.
  accepted_license_families:
  - Apache-2.0
  - MIT
  - BSD-2-Clause
  - BSD-3-Clause
  - MPL-2.0
  - EPL-2.0
  - PostgreSQL
  restricted_license_families:
  - AGPL
  - SSPL
  - BUSL
  - Commons-Clause
  - custom-commercial-source-available
  proprietary_exception_required: true
  proprietary_exception_file: 07-governance/adr/<adr-id>-technology-exception.md
  proprietary_exception_must_include:
  - business_reason
  - open_source_alternatives_evaluated
  - total_cost_of_ownership_impact
  - lock_in_risk
  - migration_exit_strategy
  - security_and_compliance_impact
  - approval_decision
  selection_criteria:
  - mature_open_source_project
  - active_maintenance
  - clear_license
  - broad_community_or_foundation_backing
  - standards_based_interfaces
  - self_hostable_or_portable_runtime
  - automation_friendly
  - observable_and_testable
  - compatible_with_agent_agnostic_execution
  lock_in_controls:
  - Prefer open standards and replaceable adapters.
  - Keep provider-specific code behind ports, adapters or configuration boundaries.
  - Do not make a proprietary service the only way to run, test or develop the product
    unless approved by ADR.
  - Prefer portable Docker, OCI, OpenAPI, SQL, OAuth2/OIDC, OpenTelemetry, Prometheus
    and standard CI primitives.
quality_toolchain_evolution_policy:
  purpose: 'Keep the framework''s stack quality toolchains current as open source
    ecosystems evolve.

    '
  rule: 'During architecture analysis, backlog execution, module closeout and release
    readiness, agents must compare the framework toolchain baseline with the current
    market and official project status for the selected stack.

    '
  agents_must_detect:
  - tools_that_are_deprecated_or_unmaintained
  - tools_that_have_safer_or_more_capable_replacements
  - tools_that_changed_license_or_hosting_model
  - tools_that_no_longer_support_the_project_runtime
  - new_open_source_tools_that_cover_required_gate_categories_better
  - overlapping_tools_that_should_be_consolidated
  update_required_when:
  - a baseline tool becomes unsupported_or_incompatible
  - a new tool materially improves security_quality_or_portability
  - a hosted_or_proprietary_tool_is_being_made_mandatory_without_exception
  - a stack_specific_gate_category_has_no_working_tool
  required_documentation:
  - affected_stack
  - current_baseline_tool
  - proposed_new_or_replacement_tool
  - reason_for_change
  - license_and_hosting_model
  - migration_impact
  - project_debt_items_to_update
  - framework_feedback_or_framework_backlog_item
  closure_rule: 'If a baseline tool cannot run or should be replaced, the agent must
    document the finding, create project technical debt when it affects the current
    product, and create framework feedback so Nexora can update the central framework
    baseline.

    '
recommended_open_source_toolchain:
  ide_feedback:
    java:
    - SonarLint
    - SonarQube Community Build when centralized analysis is needed
    - SonarCloud only when hosted SaaS use is approved and not mandatory for local
      development
  sast:
    generic:
    - Semgrep
    - Gitleaks
    java:
    - SpotBugs
    - Find Security Bugs
    - PMD
    - Checkstyle
    - Error Prone
    - Google Error Prone
    - Revapi
    javascript_typescript:
    - ESLint
    - TypeScript strict mode
    - eslint-plugin-security
    - eslint-plugin-sonarjs
    - Semgrep
    flutter_dart:
    - dart analyze
    - flutter analyze
  dast:
    web_api:
    - OWASP ZAP baseline scan
    - OWASP ZAP API scan
  dependency_and_supply_chain:
    dependency_vulnerability:
    - OSV-Scanner
    - OWASP Dependency-Check
    - npm audit when Node package managers are used
    - pip-audit when Python is used
    container_and_filesystem:
    - Trivy
    - Grype
    sbom:
    - Syft
    - CycloneDX Maven Plugin
    - CycloneDX npm tools
    build_rules:
      java:
      - Maven Enforcer
      - Maven Versions Plugin
      - Maven Surefire Plugin
      - Duplicate Finder Maven Plugin
    license_review:
      java:
      - License Maven Plugin
      - License Checker when compatible with the stack
      javascript_typescript:
      - license-checker-rseidelsohn or equivalent actively maintained license checker
  code_coverage:
    java:
    - JaCoCo
    javascript_typescript:
    - Vitest coverage
    - Istanbul
    - c8
    flutter_dart:
    - flutter test --coverage
    - lcov
  infrastructure_as_code:
  - Checkov
  - TFLint
  - Hadolint
  api_and_contract_quality:
  - Spectral
  - Schemathesis
  observability_quality:
  - OpenTelemetry
  - Prometheus compatible metrics
  - Grafana compatible dashboards
  architecture_quality:
    java:
    - ArchUnit
  mutation_testing:
    java:
    - PIT
    - Pitest
  refactor_and_modernization:
    java:
    - OpenRewrite
  duplication:
    java:
    - PMD CPD
    - Duplicate Finder Maven Plugin
    javascript_typescript:
    - jscpd
    flutter_dart:
    - jscpd when configured for Dart or an equivalent Dart duplication tool
stack_quality_toolchain_baselines:
  java_maven:
    description: Baseline open source quality toolchain for Java services built with
      Maven.
    required_or_recommended_tools:
    - category: IDE
      tool: SonarLint
      primary_use: Real-time developer feedback in common IDEs.
      gate_level: recommended_local
      default_status: recommended
    - category: Centralized quality analysis
      tool: SonarCloud
      primary_use: Hosted quality, maintainability and security hotspot analysis when
        the organization approves SaaS usage.
      gate_level: optional_with_hosted_service_approval
      default_status: candidate_not_mandatory
      note: Prefer SonarQube Community Build or local open source gates when self-hosting
        and portability are required.
    - category: Test execution
      tool: Maven Surefire Plugin
      primary_use: Unit test execution and test result reporting during Maven builds.
      gate_level: required_for_java_maven_projects
      default_status: required
    - category: Quality and bugs
      tool: SpotBugs
      primary_use: Bytecode bug analysis.
      gate_level: required_when_java_code_changes
      default_status: required
    - category: Code security
      tool: Find Security Bugs
      primary_use: SpotBugs plugin for SQL injection, weak cryptography, command injection
        and related Java security issues.
      gate_level: required_when_java_code_changes
      default_status: required
    - category: Style
      tool: Checkstyle
      primary_use: Team conventions, formatting and structural rules.
      gate_level: required_when_java_code_changes
      default_status: required
    - category: Formatting
      tool: Spotless
      primary_use: Reproducible formatting and import hygiene through Maven.
      gate_level: required_when_java_code_changes
      default_status: required
    - category: Static analysis
      tool: PMD
      primary_use: Bad practices, dead code and complexity.
      gate_level: required_when_java_code_changes
      default_status: required
    - category: Duplication
      tool: PMD CPD
      primary_use: Copy and paste detection for duplicated code.
      gate_level: required_when_java_code_changes
      default_status: required
    - category: Duplicate dependencies and classes
      tool: Duplicate Finder Maven Plugin
      primary_use: Detect duplicate classes and conflicting resources across dependency
        graphs.
      gate_level: required_when_dependencies_change
      default_status: required
    - category: Coverage
      tool: JaCoCo
      primary_use: Unit and integration test coverage.
      gate_level: required_when_java_code_changes
      default_status: required
    - category: Dependency vulnerabilities
      tool: OWASP Dependency-Check
      primary_use: CVE detection in Maven dependencies.
      gate_level: required_when_dependencies_change
      default_status: required
    - category: Containers, filesystem and secrets
      tool: Trivy
      primary_use: CVEs, SBOM, licenses, misconfigurations and secrets.
      gate_level: required_for_release_and_when_runtime_assets_change
      default_status: required
    - category: SBOM
      tool: CycloneDX Maven Plugin
      primary_use: Direct and transitive dependency inventory.
      gate_level: required_for_release
      default_status: required_for_release
    - category: Build rules
      tool: Maven Enforcer
      primary_use: Block unsupported Java/Maven versions, duplicate dependencies and
        disallowed dependencies.
      gate_level: required_for_java_maven_projects
      default_status: required
    - category: Dependency currency
      tool: Maven Versions Plugin
      primary_use: Detect dependency, plugin and parent version drift for technical-debt
        planning.
      gate_level: required_for_technology_evolution_review
      default_status: required
    - category: Licenses
      tool: License Maven Plugin or License Checker
      primary_use: Validate license headers and dependency license policy.
      gate_level: required_when_dependencies_change
      default_status: required
    - category: Advanced tests
      tool: PIT or Pitest
      primary_use: Mutation testing to measure test effectiveness.
      gate_level: recommended_for_core_domain_or_high_risk_modules
      default_status: recommended_for_high_risk_code
    - category: Architecture
      tool: ArchUnit
      primary_use: Validate architecture rules for layers, packages, DDD boundaries
        or hexagonal architecture.
      gate_level: required_when_architecture_rules_exist
      default_status: required
    - category: Refactor and technical debt
      tool: OpenRewrite
      primary_use: Automate migrations and safe refactors through Maven.
      gate_level: recommended_for_modernization_debt
      default_status: recommended
    - category: API compatibility
      tool: Revapi
      primary_use: Detect binary and source compatibility breaks in public Java APIs.
      gate_level: required_when_public_java_api_or_sdk_changes
      default_status: conditional_required
    - category: Compiler static analysis
      tool: Google Error Prone
      primary_use: Compile-time bug pattern detection for Java.
      gate_level: recommended_when_compatible_with_project_jdk_and_build
      default_status: recommended
    - category: Additional SAST
      tool: Semgrep CE
      primary_use: Security and bug rules for Java in local or CI quality gates.
      gate_level: recommended_defense_in_depth
      default_status: recommended
    minimum_evidence:
    - java_and_maven_versions_selected_from_current_supported_stable_or_lts_sources
    - maven_dependency_tree_or_equivalent_dependency_inventory
    - unit_or_integration_test_results
    - coverage_report
    - static_analysis_results
    - dependency_vulnerability_results
    - secrets_or_filesystem_scan_results
    - sbom_for_release_or_release_candidate
    - architecture_test_results_when_architecture_rules_exist
    - technical_debt_items_for_unavailable_or_deferred_tools
    adoption_rule: 'Use the project-local build system where practical. If a tool
      cannot be added safely during the active backlog item, document the reason and
      register gradual technical debt unless the missing tool blocks release or hides
      unresolved security risk.

      '
  javascript_typescript_web:
    description: Baseline open source quality toolchain for TypeScript web applications.
    required_or_recommended_tools:
    - category: Type checking
      tool: TypeScript strict mode
      primary_use: Compile-time type safety for application and shared model code.
      gate_level: required_when_typescript_code_changes
      default_status: required
    - category: Test execution
      tool: Vitest or Jest
      primary_use: Unit and component test execution.
      gate_level: required_when_frontend_code_changes
      default_status: required
    - category: Coverage
      tool: Vitest coverage, Istanbul or c8
      primary_use: Frontend unit and component coverage reporting.
      gate_level: required_when_frontend_code_changes
      default_status: required
    - category: Standards and best practices
      tool: ESLint with typescript-eslint
      primary_use: TypeScript, React and project convention enforcement.
      gate_level: required_when_frontend_code_changes
      default_status: required
    - category: Secure coding
      tool: eslint-plugin-security and Semgrep CE
      primary_use: JavaScript/TypeScript secure-code and bug pattern checks.
      gate_level: required_when_frontend_code_changes
      default_status: required
    - category: Complexity
      tool: ESLint complexity rules or eslint-plugin-sonarjs
      primary_use: Cyclomatic and cognitive complexity control.
      gate_level: required_when_frontend_code_changes
      default_status: required
    - category: Duplication
      tool: jscpd
      primary_use: Copy/paste detection across TypeScript, JavaScript, CSS and test
        code.
      gate_level: required_when_frontend_code_changes
      default_status: required
    - category: Formatting
      tool: Prettier
      primary_use: Reproducible formatting for source, tests and configuration.
      gate_level: required_when_frontend_code_changes
      default_status: required
    - category: Dependency vulnerabilities
      tool: npm audit, OSV-Scanner or Trivy
      primary_use: Direct and transitive package vulnerability detection across all
        severities.
      gate_level: required_when_dependencies_change
      default_status: required
    - category: SBOM
      tool: CycloneDX npm tools or Syft
      primary_use: Package inventory for releasable web applications.
      gate_level: required_for_release
      default_status: required_for_release
    - category: Licenses
      tool: license-checker-rseidelsohn or equivalent actively maintained license
        checker
      primary_use: Dependency license policy validation.
      gate_level: required_when_dependencies_change
      default_status: required
    - category: DAST and accessibility
      tool: OWASP ZAP plus axe-core or Playwright accessibility checks
      primary_use: Runtime security and accessibility checks for runnable web surfaces.
      gate_level: required_when_runnable_surface_exists
      default_status: required
    - category: Message externalization
      tool: i18next, react-intl or equivalent plus literal-string lint rules
      primary_use: Localization readiness and prevention of hard-coded user-visible
        text.
      gate_level: required_when_frontend_code_changes
      default_status: required
    minimum_evidence:
    - node_and_package_manager_versions
    - typecheck_results
    - lint_static_analysis_results
    - duplicate_code_results
    - complexity_results
    - unit_component_test_results
    - coverage_report
    - dependency_vulnerability_results_all_severities
    - production_build_results
    - message_externalization_results
    - dast_or_accessibility_results_when_applicable
  mobile_typescript:
    description: Baseline open source quality toolchain for TypeScript mobile applications
      such as React Native or Expo.
    required_or_recommended_tools:
    - category: Type checking
      tool: TypeScript strict mode
      primary_use: Compile-time type safety for mobile code and shared contracts.
      gate_level: required_when_mobile_code_changes
      default_status: required
    - category: Standards and secure coding
      tool: ESLint, typescript-eslint, eslint-plugin-security and Semgrep CE
      primary_use: Mobile TypeScript style, bug and secure-code checks.
      gate_level: required_when_mobile_code_changes
      default_status: required
    - category: Duplication and complexity
      tool: jscpd plus ESLint complexity or sonarjs rules
      primary_use: Detect duplicated mobile code and complexity hotspots.
      gate_level: required_when_mobile_code_changes
      default_status: required
    - category: Tests and coverage
      tool: Jest, React Native Testing Library and Istanbul coverage
      primary_use: Unit, component and coverage checks for mobile UI and logic.
      gate_level: required_when_mobile_code_changes
      default_status: required
    - category: Dependency vulnerabilities
      tool: npm audit, OSV-Scanner or Trivy
      primary_use: Package vulnerability detection across all severities.
      gate_level: required_when_dependencies_change
      default_status: required
    - category: Mobile security
      tool: MobSF when native build artifacts are available
      primary_use: Static and dynamic mobile security analysis.
      gate_level: recommended_for_native_release_candidates
      default_status: recommended
    - category: Message externalization
      tool: i18next, react-intl or platform localization resources
      primary_use: Multilanguage support and hard-coded literal prevention.
      gate_level: required_when_mobile_code_changes
      default_status: required
  flutter_dart:
    description: Baseline open source quality toolchain for Flutter and Dart applications.
    required_or_recommended_tools:
    - category: Analyzer and standards
      tool: dart analyze, flutter analyze and package:lints or flutter_lints
      primary_use: Dart and Flutter correctness, style and best-practice checks.
      gate_level: required_when_flutter_code_changes
      default_status: required
    - category: Formatting
      tool: dart format
      primary_use: Reproducible Dart formatting.
      gate_level: required_when_flutter_code_changes
      default_status: required
    - category: Tests and coverage
      tool: flutter test --coverage and lcov
      primary_use: Unit, widget and coverage reporting.
      gate_level: required_when_flutter_code_changes
      default_status: required
    - category: Dependency vulnerabilities
      tool: OSV-Scanner or Trivy
      primary_use: Pub dependency vulnerability detection across all severities.
      gate_level: required_when_dependencies_change
      default_status: required
    - category: Duplication and complexity
      tool: jscpd when configured for Dart or equivalent maintained Dart analysis
      primary_use: Detect duplicated Dart code and complexity hotspots.
      gate_level: required_when_flutter_code_changes
      default_status: required
    - category: Message externalization
      tool: Flutter gen-l10n with ARB files
      primary_use: Localization resource generation and validation.
      gate_level: required_when_flutter_code_changes
      default_status: required
    - category: Mobile security
      tool: MobSF when native artifacts are available
      primary_use: Static and dynamic security analysis for Android/iOS builds.
      gate_level: recommended_for_native_release_candidates
      default_status: recommended
  container_infrastructure:
    description: Baseline open source quality toolchain for container, Docker and
      infrastructure assets.
    required_or_recommended_tools:
    - category: Container and filesystem vulnerabilities
      tool: Trivy or Grype
      primary_use: Image, filesystem, dependency, license, secret and misconfiguration
        scanning across all severities.
      gate_level: required_when_runtime_or_container_assets_change
      default_status: required
    - category: Infrastructure as code
      tool: Checkov and TFLint
      primary_use: Terraform and IaC misconfiguration checks.
      gate_level: required_when_iac_changes
      default_status: required
    - category: Dockerfile quality
      tool: Hadolint
      primary_use: Dockerfile standards and best-practice checks.
      gate_level: required_when_dockerfile_changes
      default_status: required
    - category: SBOM
      tool: Syft or CycloneDX tools
      primary_use: Runtime and deployment bill of materials.
      gate_level: required_for_release
      default_status: required_for_release
quality_gates:
  backlog_item_gate:
    required_for_every_code_backlog:
    - technical_debt_first_action
    - client_stack_market_validation_when_stack_or_dependencies_are_relevant
    - unit_tests
    - relevant_integration_or_contract_tests
    - dependency_vulnerability_scan
    - secrets_scan
    - sast_or_static_analysis_for_changed_stack
    - best_practices_and_standards_scan_for_changed_stack
    - duplicate_code_scan_for_changed_stack
    - complexity_scan_for_changed_stack
    - owasp_or_secure_code_scan_for_changed_stack
    - coverage_report_for_changed_stack
    - message_externalization_and_magic_string_review
    - technology_evolution_review
    - technical_debt_backlog_update_when_findings_exist
    - qa_evidence_yaml
    - qa_evidence_md_when_human_review_is_needed
    fail_conditions:
    - new_or_unresolved_vulnerability_of_any_severity_without_accepted_risk_and_immediate_remediation_plan
    - secret_detected
    - sast_or_secure_code_finding_without_disposition
    - tests_failing
    - coverage_decreases_below_previous_iteration_baseline
    - missing_duplicate_code_or_complexity_analysis_for_changed_stack
    - new_user_visible_text_error_message_or_magic_string_not_externalized_or_dispositioned
    - no_existing_technical_debt_item_resolved_or_materially_reduced_when_open_debt_exists
    - generated_artifacts_modified_without_source_model_change
    - proprietary_dependency_without_exception_adr
  module_closeout_gate:
    required:
    - stack_baseline_reviewed_against_current_market_and_official_versions
    - all_backlog_item_gates_passed
    - aggregate_coverage_report
    - dependency_and_container_scan_when_applicable
    - dast_baseline_when_web_api_or_ui_is_runnable
    - openapi_contract_validation_when_api_changed
    - threat_model_or_security_notes_for_security_sensitive_module
    - accepted_risk_register_updated
    - technical_debt_burndown_plan_updated
    - coverage_not_below_previous_iteration_baseline
  release_gate:
    required:
    - client_or_project_stack_validated_against_current_stable_versions
    - sast_passed
    - dast_passed_or_not_applicable_with_reason
    - dependency_scan_passed
    - container_scan_passed_when_containers_exist
    - sbom_generated
    - coverage_threshold_met
    - known_vulnerabilities_reviewed
    - license_review_passed
    - technology_debt_register_reviewed
    - no_open_technical_debt
    - security_quality_evidence_registered
    fail_conditions:
    - open_technical_debt_exists
    - aggregate_line_coverage_below_80_percent
    - any_stack_coverage_below_previous_iteration_baseline
technical_debt_backlog_policy:
  root_folder: 08-qa/technical-debt/
  index_file: 08-qa/technical-debt/technical-debt-index.md
  human_readable_index: 08-qa/technical-debt/README.md
  item_path_pattern: 08-qa/technical-debt/<debt-id>.yaml
  debt_id_pattern: TD-<component-or-area>-<sequence>
  required_when:
  - technology_review_finds_non_blocking_migration_or_upgrade
  - dependency_or_framework_is_outdated_but_not_immediate_blocker
  - security_or_quality_tooling_gap_needs_gradual_remediation
  - stack_migration_is_recommended_but_must_be_coordinated_with_touched_components
  - coverage_below_80_percent_or_below_stack_target
  - coverage_does_not_improve_when_below_80_percent
  - required_quality_validation_category_has_no_executable_tool_or_script
  - required_stack_quality_tooling_gap_is_found_during_backlog_execution
  debt_item_must_include:
  - id
  - title
  - source_backlog_item
  - detection_date
  - affected_components
  - current_state
  - recommended_target_state
  - reason
  - risk_level
  - urgency
  - estimated_effort
  - estimated_cost_impact
  - migration_strategy
  - incremental_remediation_triggers
  - dependencies_or_prerequisites
  - acceptance_criteria
  - owner_or_responsible_role
  - status
  statuses:
  - proposed
  - accepted
  - in_progress
  - partially_resolved
  - resolved
  - rejected_with_reason
  remediation_rules:
  - Do not rewrite stable components solely to chase novelty.
  - Prefer opportunistic remediation when the affected component is already being
    modified.
  - Break broad migrations into small backlog items with explicit test and rollback
    strategy.
  - Security-critical or unsupported runtime findings may be promoted to blocking
    backlog items.
  - Every resolved debt item must reference validation evidence and the backlog item
    that resolved it.
  - A project must not be marked finished, commercially complete or generally available
    while any technical-debt item remains open, accepted, in_progress or partially_resolved.
  - Every code-changing iteration must reduce at least one open technical-debt item
    before feature work when debt exists.
  - Debt remediation intensity must increase as the project advances: early MVP iterations
      may reduce one item, module closeout should reduce multiple relevant items when
      debt exists, and release/GA preparation must burn down all remaining debt.
  - Technical debt may be deferred only with an owner, target backlog, acceptance
    criteria and expiration; deferral does not permit final project closure.
default_thresholds:
  vulnerability_policy:
    critical: fail
    high: fail
    medium: fail_unless_fixed_or_immediate_accepted_risk_with_target_backlog
    low: fail_unless_fixed_or_immediate_accepted_risk_with_target_backlog
    informational: review_and_disposition_required
    unknown: fail_until_triaged
  sast_policy:
    critical: fail
    high: fail
    medium: fail_unless_dispositioned_by_module_risk
    low: review_and_disposition_required
  coverage_policy:
    minimum_line_coverage_target: 80
    mvp_minimum_line_coverage_target: 80
    commercial_ga_line_coverage_target: 80
    branch_coverage_target: project_defined
    changed_code_must_not_reduce_existing_coverage: true
    previous_iteration_coverage_is_hard_floor: true
    below_80_requires_improvement_each_iteration: true
    below_80_minimum_improvement_per_relevant_iteration_percentage_points: 3
    below_80_preferred_improvement_per_relevant_iteration_percentage_points: 5
    final_project_closure_requires_minimum_line_coverage: 80
    coverage_exception_allowed_for_iteration: true
    coverage_exception_allowed_for_final_project_closure: false
    rule: 'Coverage below 80% does not automatically block an intermediate backlog
      item when debt is recorded and the previous iteration baseline is preserved
      or improved. Coverage must never decrease below the previous iteration baseline.
      Final project closure, release readiness and commercial GA require at least
      80% line coverage for each applicable delivered stack, unless a stack is explicitly
      not applicable. When a relevant changed stack remains below 80%, the iteration
      must target a 3 to 5 percentage point line-coverage improvement. A smaller improvement
      requires explicit justification, maximum meaningful in-scope tests and an immediate
      coverage debt item assigned to the next relevant backlog; a symbolic 0.01 point
      increase is not sufficient quality progress by itself.

      '
  license_policy:
    restricted_license_requires_review: true
    proprietary_dependency_requires_exception_adr: true
evidence_requirements:
  root_folder: 08-qa/security-quality/
  per_backlog_path_pattern: 08-qa/security-quality/<backlog-item-id>/
  required_files:
  - security-quality-evidence.md
  - security-quality-evidence.md
  evidence_yaml_must_include:
  - backlog_item_id
  - module_id
  - changed_components
  - open_source_first_assessment
  - client_stack_market_validation_when_applicable
  - stack_toolchain_baseline
  - technical_debt_first_action
  - tools_run
  - commands_or_equivalent_steps
  - results
  - coverage_summary
  - dependency_vulnerability_summary
  - sast_summary
  - dast_summary_when_applicable
  - secrets_scan_summary
  - duplicate_code_summary
  - complexity_summary
  - owasp_or_secure_code_summary
  - message_externalization_summary
  - license_summary
  - technology_evolution_review
  - technical_debt_items_created_or_updated
  - accepted_risks
  - blocking_findings
  - decision
agent_rules:
- Agents must prefer open source libraries, frameworks, tools and standards unless
  the requester explicitly requires otherwise or an ADR approves an exception.
- Agents must not introduce proprietary runtimes, hosted services, paid-only SDKs
  or closed-source dependencies as mandatory product dependencies without an exception
  ADR.
- Agents must add or update quality-gate evidence for every backlog item that changes
  code, dependencies, runtime configuration, APIs or deployment assets.
- Agents must validate requester-proposed stacks against current stable or LTS versions,
  official lifecycle information, security advisories, ecosystem health and required
  quality gates before accepting them as the implementation baseline.
- Agents must define a stack-specific open source quality toolchain during architecture
  analysis and revisit it during backlog work, module closeout and release readiness.
- Agents must run the security and quality gates that are applicable to the changed
  stack. If a mandatory executable gate cannot run because a tool, runtime, native
  binary, network endpoint or service is unavailable, the backlog item must remain
  blocked_by_environment or ready_for_external_validation; manual review is only a
  compensating control and must not be recorded as a passed gate.
- Agents must include best-practice, coding-standard, duplicate-code, complexity,
  OWASP or equivalent secure-code checks for every changed technology stack and product
  layer.
- Agents must scan dependency and vulnerability findings across all severities supported
  by the selected tool. Filtering only HIGH or CRITICAL is insufficient for backlog
  closure.
- Agents must address at least one existing technical-debt item before feature implementation
  in every code-changing iteration, unless the project has no open technical debt.
- Agents must increase technical-debt remediation as the project advances; late-stage
  iterations, module closeouts and release preparation must reduce more debt than
  early exploratory iterations when open debt remains.
- Agents must not mark a project finished, release-ready for GA or commercially complete
  while any project technical debt remains open.
- Agents must enforce 80% line coverage as the final project closure threshold for
  every applicable delivered stack.
- Agents must use the previous iteration's measured coverage as the minimum allowed
  baseline whenever current coverage is below 80%; coverage may improve gradually
  but must never decrease.
- Agents must externalize user-visible messages, validation copy, error prose, stable
  error codes and repeated magic values through stack-appropriate message catalogs,
  constants, configuration or policy providers.
- Agents must not treat the initial project stack as permanently fixed. Each code-changing
  iteration must evaluate whether newer open source frameworks, libraries, runtimes
  or tools are materially safer, better maintained, more portable or more cost-effective.
- Agents must create or update technical-debt backlog items for beneficial technology
  upgrades or migrations that are not safe or necessary to execute immediately.
- Agents must recommend gradual remediation of technology debt when future backlog
  work touches the affected components.
- Agents must fail the backlog item when vulnerability or secure-code findings of
  any severity are introduced without remediation, accepted-risk disposition and immediate
  target backlog.
- Agents must not advance next_backlog_item, module closeout or release readiness
  when any mandatory executable gate is not_executed, blocked_by_missing_toolchain,
  blocked_by_network, blocked_by_unsupported_runtime, passed_with_execution_limitation
  or closed_with_execution_limitation.
- Agents must keep quality tooling project-local, reproducible and agent agnostic.
- Agents must keep generated security and quality evidence in YAML first, with Markdown
  as the human companion.
non_goals:
- This standard does not require one specific CI/CD vendor.
- This standard does not require proprietary security platforms.
- This standard does not replace domain security, IAM, privacy or regulatory requirements.
- This standard does not force every tool on every project; applicability depends
  on the stack and changed surface.
- This standard does not require immediate migration to every new framework or version;
  it requires explicit detection, documentation, prioritization and gradual remediation.
```
