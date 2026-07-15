# HOP Enterprise Quality Alignment Backlog

This backlog blocks functional development until HOP is aligned with the updated Nexora enterprise quality framework.

Current functional backlog paused:

`MVP-MOD-004-FE-001`

Temporary next backlog:

`HOP-QA-ALIGN-001`

## Why This Exists

The framework now requires:

- Debt-first execution before code-changing work.
- Backend Java/Maven enterprise quality gates.
- Frontend web enterprise quality gates.
- Mobile quality baseline.
- DAST for runnable local API/UI surfaces.
- Vulnerability scans across all severities.
- Message externalization and i18n readiness.
- Toolchain evolution review.

HOP has several of these documented as debt, but not yet sequenced as mandatory work. Continuing feature delivery now would increase technical debt.

## Backlog Order

1. `HOP-QA-ALIGN-001` - Reconcile technical debt under the updated framework.
2. `HOP-QA-ALIGN-002` - Implement backend Java/Maven enterprise quality profile.
3. `HOP-QA-ALIGN-003` - Implement frontend web enterprise quality profile.
4. `HOP-QA-ALIGN-004` - Establish all-severity vulnerability, DAST and runtime security evidence.
5. `HOP-QA-ALIGN-005` - Establish message externalization and magic-string remediation baseline.
6. `HOP-QA-ALIGN-006` - Update the integrated local runbook with quality gate execution.
7. `HOP-QA-ALIGN-CLOSEOUT` - Close alignment and resume `MVP-MOD-004-FE-001`.

## Rule

Do not continue HOP functional development until `HOP-QA-ALIGN-CLOSEOUT` passes and the project state returns the next backlog pointer to `MVP-MOD-004-FE-001`.

Use the P0/P1/P2 prioritization in:

`08-qa/qa/quality-alignment/HOP-ENGINEERING-EXCELLENCE-PRIORITIZATION.yaml`

- P0 findings must be satisfied for the changed scope before resuming functionality.
- P1 findings must be registered or updated as technical debt and consumed through debt-first execution.
- P2 findings are desirable/contextual and must not block delivery unless risk promotes them.
