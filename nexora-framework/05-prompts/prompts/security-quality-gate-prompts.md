# Security and Quality Gate Prompts

**Artifact ID:** `NXF-SQ-PROMPTS-001`  
**Status:** Approved  
**Machine-readable source:** `security-quality-gate-prompts.yaml`
**Version:** `1.4.0`

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

## Open-Source-First Assessment

Load `open-source-first-security-quality-standard.yaml`, review the changed technology choices, and confirm that open source, self-hostable and standards-based options are preferred. If a proprietary mandatory dependency appears, require an ADR exception before continuing.

The review must not be limited to the stack selected at project inception. Treat the original stack as
the current baseline and evaluate whether newer open source frameworks, runtimes, libraries or tools
would materially improve security, maintainability, portability, cost or ecosystem health.

## Backlog Gate

For every code-changing backlog item, probe the required toolchain first and then run applicable
checks:

- Technical-debt first action: resolve or materially reduce at least one open debt item before
  feature work, unless no debt exists.
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
- Contract quality.
- DAST when a runnable web/API surface exists.
- Container/IaC scan when deployment assets change.
- Technology evolution review.
- Stack-specific quality toolchain completeness review.
- Technical-debt backlog update when non-blocking modernization findings exist.

If a mandatory executable gate cannot run because Maven, Java, Node, npm, a native package, Docker,
a database service, network access or an audit endpoint is missing, unsupported or blocked, the
agent must attempt documented remediation or request approval. Manual source review is only a
compensating control; it does not turn an unexecuted gate into a pass.

Write evidence under:

`08-qa/security-quality/<backlog-item-id>/`

Do not close the backlog when vulnerabilities of any severity, secure-code findings, secrets,
failing tests, missing duplicate/complexity/OWASP analysis, undocumented coverage regressions,
missing debt-first execution, hard-coded messages/magic strings or unexecuted mandatory gates remain
unresolved. Do not use
`passed_with_execution_limitation` or `closed_with_execution_limitation` as final states. Use
`blocked_by_environment` or `ready_for_external_validation`, keep `next_backlog_item` unchanged,
and write exact remediation commands.

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

Module closeout must re-run or verify non-limited passed evidence for all required backend,
frontend, mobile, API, dependency, coverage, build and security gates, including duplicate-code,
complexity, OWASP/secure-code and message externalization reviews. Do not recommend the next
module while any required gate is `not_executed`, `passed_with_execution_limitation`,
`closed_with_execution_limitation`, `blocked_by_missing_toolchain`, `blocked_by_network` or
`blocked_by_unsupported_runtime`.
