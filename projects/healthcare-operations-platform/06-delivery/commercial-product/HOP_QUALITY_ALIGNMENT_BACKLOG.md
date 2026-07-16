# HOP Enterprise Quality Alignment Backlog

This backlog blocks functional development until HOP is aligned with the updated Nexora enterprise quality framework.

Current functional backlog paused:

`MVP-MOD-004-FE-001`

Temporary next backlog:

`HOP-QA-ALIGN-005`

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
- Technical-debt burn-down that becomes stricter as the product advances.
- 80% line coverage as the final product closure threshold.
- Previous iteration coverage as the lower bound whenever a stack is still below 80%.

HOP has several of these documented as debt, but not yet sequenced as mandatory work. Continuing feature delivery now would increase technical debt.

## Backlog Order

1. `HOP-QA-ALIGN-001` - Closed. Technical debt was reconciled under the updated framework.
2. `HOP-QA-ALIGN-002` - Closed with residual P1 debt. Backend Java/Maven enterprise quality profile is implemented and executable.
3. `HOP-QA-ALIGN-003` - Closed with residual P1 debt. Frontend web and mobile foundation quality profiles are implemented and executable.
4. `HOP-QA-ALIGN-004` - Closed. All-severity vulnerability evidence passes; OWASP ZAP DAST baseline and API scans executed with 0 FAIL-NEW; residual findings dispositioned as accepted-risk technical debt (`TD-FE-005`, `TD-QA-004`).
5. `HOP-QA-ALIGN-005` - Establish message externalization and magic-string remediation baseline.
6. `HOP-QA-ALIGN-006` - Closed. Integrated local runbook now includes backend, frontend, mobile, Trivy and OWASP ZAP quality gate execution.
7. `HOP-QA-ALIGN-CLOSEOUT` - Close alignment and resume `MVP-MOD-004-FE-001`.

## Rule

Do not continue HOP functional development until `HOP-QA-ALIGN-CLOSEOUT` passes and the project state returns the next backlog pointer to `MVP-MOD-004-FE-001`.

`HOP-QA-ALIGN-CLOSEOUT` only allows functional development to resume. It does not mark the full HOP
product complete. Final commercial or GA closure requires:

- No open technical debt.
- At least 80% line coverage for every applicable delivered stack.
- No stack coverage below its previous measured iteration baseline.

Use the P0/P1/P2 prioritization in:

`08-qa/qa/quality-alignment/HOP-ENGINEERING-EXCELLENCE-PRIORITIZATION.yaml`

- P0 findings must be satisfied for the changed scope before resuming functionality.
- P1 findings must be registered or updated as technical debt and consumed through debt-first execution.
- P2 findings are desirable/contextual and must not block delivery unless risk promotes them.
