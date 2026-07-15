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

`06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.yaml`

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
