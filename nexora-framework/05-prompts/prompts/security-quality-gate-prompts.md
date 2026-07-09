# Security and Quality Gate Prompts

**Artifact ID:** `NXF-SQ-PROMPTS-001`  
**Status:** Approved  
**Machine-readable source:** `security-quality-gate-prompts.yaml`
**Version:** `1.2.0`

Use these prompts with the Open Source First Security and Quality Standard.

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
- Required quality toolchain by stack.
- Risks and gaps.
- Immediate changes to apply.
- Technical-debt items to create or update.
- Selected stack baseline.

For Java/Maven, consider SonarLint, SpotBugs, Find Security Bugs, Checkstyle, PMD, PMD CPD, JaCoCo,
OWASP Dependency-Check, Trivy, CycloneDX Maven Plugin, Maven Enforcer, License Maven Plugin,
PIT/Pitest, ArchUnit, OpenRewrite and Semgrep CE according to applicability.

## Open-Source-First Assessment

Load `open-source-first-security-quality-standard.yaml`, review the changed technology choices, and confirm that open source, self-hostable and standards-based options are preferred. If a proprietary mandatory dependency appears, require an ADR exception before continuing.

The review must not be limited to the stack selected at project inception. Treat the original stack as
the current baseline and evaluate whether newer open source frameworks, runtimes, libraries or tools
would materially improve security, maintainability, portability, cost or ecosystem health.

## Backlog Gate

For every code-changing backlog item, run or document applicable checks:

- Tests.
- SAST/static analysis.
- Dependency vulnerability scan.
- Secrets scan.
- Coverage.
- Contract quality.
- DAST when a runnable web/API surface exists.
- Container/IaC scan when deployment assets change.
- Technology evolution review.
- Stack-specific quality toolchain completeness review.
- Technical-debt backlog update when non-blocking modernization findings exist.

Write evidence under:

`08-qa/security-quality/<backlog-item-id>/`

Do not close the backlog when critical/high findings, secrets, failing tests or undocumented coverage regressions remain unresolved.

## Technology Debt Backlog

When a modernization, migration or quality-tooling finding is beneficial but not immediately required
to complete the backlog safely, register it under:

`08-qa/technical-debt/`

Each item must describe the affected components, current state, recommended target state, risk,
urgency, effort, cost impact, migration strategy, incremental remediation triggers and acceptance
criteria. Remediation should be gradual and preferably attached to future backlog work that already
touches the affected component.

## Module Closeout Gate

Before closing a module, aggregate backlog evidence and confirm that the module meets the required
security and quality gates. Also review the project technology debt index and confirm each item is
resolved, accepted, prioritized or explicitly deferred with rationale.
