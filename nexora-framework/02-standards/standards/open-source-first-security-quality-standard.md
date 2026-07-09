# Open Source First Security and Quality Standard

**Artifact ID:** `NXF-OSS-SEC-QUAL-001`  
**Status:** Approved  
**Machine-readable source:** `open-source-first-security-quality-standard.yaml`

## Purpose

Nexora solutions should be cost-efficient, portable, secure and maintainable from the beginning. The default technology strategy is **open source first**: use open, self-hostable, standards-based technologies unless a documented exception is approved.

The same standard requires continuous security and quality gates while the product is built, so debt is detected during each backlog item instead of being deferred to the end.

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
- Accepted risks.
- Blocking findings.
- Final decision.

Markdown evidence is the human-readable companion; YAML remains the agent-executable source.
