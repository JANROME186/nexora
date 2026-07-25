# HOP Quality Alignment Gap Analysis

HOP cannot continue with `MVP-MOD-004-FE-001` until it is aligned with the updated Nexora enterprise quality framework.

The project has good functional progress, but the framework now requires stronger gates before new code-changing work:

- Debt-first execution.
- Backend Java/Maven quality toolchain.
- Frontend web quality toolchain.
- Mobile quality baseline.
- DAST for runnable API/UI surfaces.
- Vulnerability scans across all severities.
- Message externalization and i18n readiness.
- Toolchain evolution review.

## Decision

`can_continue_functional_development: false`

The next executable backlog must be the quality alignment backlog:

`06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.md`

Only after `HOP-QA-ALIGN-CLOSEOUT` passes should the project return to:

`MVP-MOD-004-FE-001`

## Highest Priority Gaps

1. Backend quality gates are documented but not implemented as Maven gates.
2. Frontend quality gates are incomplete for lint, secure-code, duplication, complexity, accessibility and i18n.
3. DAST remains open as `TD-QA-001`.
4. Historical vulnerability scans used `HIGH/CRITICAL` filtering; the new framework requires all severities.
5. No project-wide message externalization or magic-string evidence exists.
6. Open technical debt is not yet sequenced as mandatory before feature work.

## Required Outcome

Create and execute an intermediate quality backlog before functional development continues. Each item must produce YAML and Markdown evidence and update the relevant technical-debt entries.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-QUALITY-ALIGNMENT-GAP-001
  type: framework-alignment-gap-analysis
  name: HOP Enterprise Quality Alignment Gap Analysis
  version: 1.0.0
  status: active_blocking
  created_date: 2026-07-15
  human_readable: HOP-QUALITY-ALIGNMENT-GAP-ANALYSIS.md
  machine_readable: HOP-QUALITY-ALIGNMENT-GAP-ANALYSIS.md
  framework_standard: ../../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  project_stack_baseline: ../../../03-architecture/technology-architecture/stack-quality-toolchain-baseline.md
  engineering_excellence_prioritization: HOP-ENGINEERING-EXCELLENCE-PRIORITIZATION.md
purpose: 'Analyze Healthcare Operations Platform against the updated Nexora enterprise
  quality framework and identify blocking gaps that must be handled before continuing
  functional backlog development.

  '
decision:
  can_continue_functional_development: false
  blocked_next_functional_backlog_item: MVP-MOD-004-FE-001
  required_intermediate_backlog: ../../../06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.md
  reason: 'HOP has implemented functional MVP-MOD-004 backend work, but the updated
    framework requires enterprise quality gates, debt-first execution, all-severity
    vulnerability scanning, message externalization and stack-specific quality toolchains
    before additional code-changing work.

    '
framework_requirements_checked:
- id: NXF-QUALITY-001
  requirement: Debt-first execution before code-changing backlog items.
  status: not_implemented_as_blocking_sequence
  evidence: 08-qa/technical-debt/technical-debt-index.md contains open debt, but
    PROJECT_STATE.md still points directly to MVP-MOD-004-FE-001.
  impact: Functional development could continue while known debt remains passive.
- id: NXF-QUALITY-002
  requirement: Backend Java/Maven quality toolchain for best practices, standards,
    duplication, complexity, secure code, coverage, supply chain and compatibility.
  status: partially_documented_not_implemented
  evidence: stack-quality-toolchain-baseline.md lists required tools, but backend
    pom/build gates do not yet enforce SpotBugs, Find Security Bugs, PMD, PMD CPD,
    Checkstyle, Spotless, JaCoCo, OWASP Dependency-Check, CycloneDX, Maven Enforcer,
    Maven Versions, License validation, Duplicate Finder, Revapi or Error Prone.
  impact: Backend quality debt remains open and future backend changes cannot close
    under the updated framework.
- id: NXF-QUALITY-003
  requirement: Frontend web quality toolchain for typecheck, lint, secure code, duplication,
    complexity, coverage, build, dependency vulnerabilities, accessibility and i18n.
  status: partially_implemented_with_gaps
  evidence: Previous frontend evidence includes typecheck and some tests, but HOP
    lacks a blocking baseline for ESLint/typescript-eslint, eslint-plugin-security,
    sonarjs/complexity, jscpd, i18n literal checks, accessibility checks and all-severity
    vulnerability evidence.
  impact: MVP-MOD-004-FE-001 would add UI code before the web quality baseline is
    complete.
- id: NXF-QUALITY-004
  requirement: Mobile stack quality baseline.
  status: foundation_only_gap_open
  evidence: mobile-app is foundation-only and stack-quality-toolchain-baseline.md
    records mobile gaps.
  impact: Future mobile work needs a defined executable quality baseline before implementation.
- id: NXF-QUALITY-005
  requirement: DAST for runnable web/API surfaces.
  status: open_technical_debt
  evidence: TD-QA-001 remains open; earlier evidence used manual smoke as compensating
    control.
  impact: Integrated runtime security cannot be considered enterprise-ready.
- id: NXF-QUALITY-006
  requirement: Vulnerability scans across all severities.
  status: historical_evidence_incomplete
  evidence: PROJECT_STATE.md and older evidence mention Trivy filtered to HIGH/CRITICAL.
  impact: Closure evidence must be refreshed with all-severity scanning.
- id: NXF-QUALITY-007
  requirement: Externalized messages, i18n readiness, error codes and no hard-coded
    magic values.
  status: not_systematically_validated
  evidence: No project-wide message externalization inventory or literal/magic-string
    scan evidence exists for backend, frontend or mobile.
  impact: UI/backend messages can become coupled to code and hard to localize.
- id: NXF-QUALITY-008
  requirement: Toolchain evolution review and framework feedback when substitutions
    are found.
  status: documented_not_operationalized
  evidence: Framework and HOP baseline now require this, but HOP has no executed alignment
    backlog item capturing current tool decisions.
  impact: Stack/tooling drift may accumulate without a controlled decision trail.
existing_technical_debt_to_prioritize:
  immediate_quality_items:
  - TD-BE-002
  - TD-BE-003
  - TD-BE-004
  - TD-QA-001
  - TD-QA-002
  - TD-QA-003
  - TD-STACK-001
  - TD-FE-003
  - TD-APP-001
  - TD-I18N-001
  product_quality_items:
  - TD-BE-001
  - TD-BE-005
  - TD-BE-006
  - TD-BE-007
  - TD-BE-008
  - TD-BE-009
  - TD-BE-010
  - TD-FE-002
  - TD-DEF-001
  - TD-DEF-002
required_backlog_strategy:
  sequencing_rule: 'Execute HOP quality alignment backlog items before MVP-MOD-004-FE-001.
    Each item must resolve or materially reduce at least one open technical-debt item
    and produce YAML and Markdown evidence.

    '
  minimum_exit_criteria:
  - Engineering excellence findings are classified as P0, P1 or P2.
  - Backend Java/Maven quality profile exists or every missing tool has an immediate
    technical-debt item with owner, target backlog and accepted rationale.
  - Frontend web quality profile exists or every missing tool has an immediate technical-debt
    item with owner, target backlog and accepted rationale.
  - All-severity vulnerability scans are documented for current backend, frontend,
    mobile and filesystem/container surfaces where applicable.
  - DAST can run against local runnable API/UI or remains explicitly blocked with
    exact Docker-based remediation commands and priority.
  - Message externalization and magic-string findings are inventoried and either remediated
    or registered as immediate debt.
  - PROJECT_STATE.md points back to MVP-MOD-004-FE-001 only after alignment closeout
    passes.
resulting_backlog:
  file: ../../../06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.md
  first_item: HOP-QA-ALIGN-001
  final_item: HOP-QA-ALIGN-CLOSEOUT
```
