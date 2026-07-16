# Open Source First Security and Quality Standard

**Artifact ID:** `NXF-OSS-SEC-QUAL-001`  
**Status:** Approved  
**Machine-readable source:** `open-source-first-security-quality-standard.yaml`
**Version:** `1.5.0`

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

`08-qa/technical-debt/technical-debt-index.yaml`

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

`08-qa/technical-debt/technical-debt-index.yaml`

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
