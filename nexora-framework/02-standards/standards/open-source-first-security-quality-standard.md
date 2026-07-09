# Open Source First Security and Quality Standard

**Artifact ID:** `NXF-OSS-SEC-QUAL-001`  
**Status:** Approved  
**Machine-readable source:** `open-source-first-security-quality-standard.yaml`
**Version:** `1.1.0`

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

## Open Source Tooling Baseline

Preferred open source tools include:

- SAST/static analysis: Semgrep, SpotBugs, PMD, Checkstyle, Error Prone, ESLint, TypeScript strict mode, `dart analyze`, `flutter analyze`.
- DAST: OWASP ZAP baseline/API scans.
- Dependency and vulnerability analysis: OSV-Scanner, OWASP Dependency-Check, npm audit, pip-audit, Trivy, Grype.
- SBOM: Syft and CycloneDX tools.
- Coverage: JaCoCo, Vitest coverage, Istanbul, c8, Flutter coverage and lcov.
- IaC/container quality: Checkov, TFLint, Hadolint, Trivy.
- API quality: Spectral and Schemathesis.

## Fail Conditions

A backlog item must not be closed when it introduces:

- Critical or high vulnerabilities without an accepted risk.
- Secrets.
- Critical or high SAST findings without an accepted risk.
- Failing tests.
- Undocumented coverage regression.
- Proprietary dependency without an exception ADR.
- Manual edits to generated artifacts without a source model change.

## Evidence

Each evidence YAML must include:

- Backlog item and module.
- Changed components.
- Open-source-first assessment.
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
