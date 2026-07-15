# Open Source First Security and Quality Standard

**Artifact ID:** `NXF-OSS-SEC-QUAL-001`  
**Status:** Approved  
**Machine-readable source:** `open-source-first-security-quality-standard.yaml`
**Version:** `1.2.0`

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
- Dependency vulnerability analysis.
- Secrets scan.
- Code coverage report.
- DAST when a runnable web/API surface exists.
- Container and infrastructure scan when deployment assets change.
- License review when dependencies change.
- Technology evolution review against current open source options.
- Technical-debt backlog update when non-blocking upgrade or migration findings exist.

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

When an update is required to remove critical/high risk, unsupported runtimes, incompatible quality
gates or blocking build failures, it must be handled before closing the backlog item. Beneficial but
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

## Open Source Tooling Baseline

Preferred open source tools include:

- SAST/static analysis: Semgrep, SpotBugs, PMD, Checkstyle, Error Prone, ESLint, TypeScript strict mode, `dart analyze`, `flutter analyze`.
- DAST: OWASP ZAP baseline/API scans.
- Dependency and vulnerability analysis: OSV-Scanner, OWASP Dependency-Check, npm audit, pip-audit, Trivy, Grype.
- SBOM: Syft and CycloneDX tools.
- Coverage: JaCoCo, Vitest coverage, Istanbul, c8, Flutter coverage and lcov.
- IaC/container quality: Checkov, TFLint, Hadolint, Trivy.
- API quality: Spectral and Schemathesis.

### Java Maven Baseline

For Java/Maven services, the framework expects a stack-specific quality toolchain similar to:

| Category | Open source tool | Main use |
|---|---|---|
| IDE feedback | SonarLint | Real-time review in common IDEs. |
| Quality / bugs | SpotBugs | Bytecode bug analysis. |
| Code security | Find Security Bugs | SpotBugs rules for injection, weak cryptography and related security issues. |
| Style | Checkstyle | Team conventions, formatting and structural rules. |
| Static analysis | PMD | Bad practices, dead code and complexity. |
| Duplication | PMD CPD | Copy/paste detection. |
| Coverage | JaCoCo | Unit and integration test coverage. |
| Dependency vulnerabilities | OWASP Dependency-Check | CVE detection in Maven dependencies. |
| Containers / filesystem / secrets | Trivy | CVEs, SBOM, licenses, misconfigurations and secrets. |
| SBOM | CycloneDX Maven Plugin | Direct and transitive dependency inventory. |
| Build rules | Maven Enforcer | Java/Maven versions, duplicate dependencies and disallowed dependencies. |
| Licenses | License Maven Plugin | Header and dependency license policy. |
| Advanced testing | PIT / Pitest | Mutation testing for test effectiveness. |
| Architecture | ArchUnit | Architecture rules for layers, packages, DDD or hexagonal boundaries. |
| Refactor / technical debt | OpenRewrite | Automated migrations and refactors. |
| Additional SAST | Semgrep CE | Security and bug rules for Java in CI/CD. |

This table is a baseline, not a universal command to install every tool immediately. If a tool is
not safe or practical to adopt during the active backlog item, the agent must document the reason and
register technical debt unless the missing tool blocks release or hides critical/high risk.

## Fail Conditions

A backlog item must not be closed when it introduces:

- Critical or high vulnerabilities without an accepted risk.
- Secrets.
- Critical or high SAST findings without an accepted risk.
- Failing tests.
- Undocumented coverage regression.
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
- Tools and commands run.
- Results.
- Coverage summary.
- Dependency vulnerability summary.
- SAST summary.
- DAST summary when applicable.
- Secrets scan summary.
- License summary.
- Technology evolution review.
- Technical-debt items created or updated.
- Accepted risks.
- Blocking findings.
- Final decision.

Markdown evidence is the human-readable companion; YAML remains the agent-executable source.
