# MVP-MOD-005-FE-001 — Security & Quality Evidence

Backlog item: `MVP-MOD-005-FE-001` — Compile cashier and billing request UI outputs. Status:
**passed**.

## Open-source-first

No new dependency was introduced. The new API client, screens and shared money-formatting helper
are pure application code built entirely on the existing employee-portal toolchain and existing
shared components (`StatusBanner`, `ConfirmDialog`, `ScopeIndicator`, `useAsyncAction`,
`i18n/messages.ts`).

## Checks

| Check | Result |
|---|---|
| Tests (33, 17 files) | passed |
| SAST / static analysis (ESLint) | passed, 0 errors |
| Dependency vulnerability scan (`npm audit`) | passed, 0 vulnerabilities |
| Coverage | passed, 80.57% (floor 76.51%, no regression, meets the 80% final-closure target) |
| Message externalization / i18n review | passed |
| DAST (OWASP ZAP baseline) | passed with disposed warnings: 0 FAIL, 4 WARN tracked/disposed |
| Container-IaC scan | not applicable (no container or IaC assets changed) |

## Pre-existing draft correction

An uncommitted, partially built `CashSessionsScreen.tsx`, `cashSalesApi.ts` and `types.ts`
extension from a prior session were found at the start of this backlog item. The draft modeled
`Money`-typed backend fields as flat `number`+`currency` pairs, used non-existent field names
(`SaleTotals.subtotal/taxTotal/total/paidTotal/outstandingBalance` instead of the backend's
`subtotalAmount/discountAmount/totalAmount/paidAmount/outstandingAmount`) and invalid status
literals (`Sale.status "open"` instead of `"payable"`; `InvoiceRequest.status`
`"pending"/"accepted"/"rejected"` instead of `"requested"/"submitted"/"issued"/"failed"/
"cancelled"`). Shipping it as-is would have produced a UI that silently failed to render real
backend responses, with no compile-time signal since the draft's types matched the draft's own
wrong shape. All cashier/billing types were rewritten field-for-field against the actual
`CashierOperationsController`/`BillingRequestController` Java source before any screen was built or
tested.

## Message externalization review

- **New repeated strings centralized** in `i18n/messages.ts`: `selectCashSessionFirst` ("Select a
  cash session first.", 1 site), `selectSaleFirst` ("Select a sale first.", 3 sites),
  `selectBillingRequestFirst` ("Select a billing request first.", 3 sites).
- Backend business-error prose (`BILLING_SALE_REQUIRED`, `PAYMENT_EXCEEDS_OUTSTANDING_BALANCE`,
  `CASH_VARIANCE_REASON_REQUIRED`, etc.) is rendered as-is, not re-authored on the frontend,
  consistent with every other employee-portal screen and the HOP-QA-ALIGN-005 baseline.
- Single-occurrence UI copy (headings, labels, hints) remains inline, consistent with the
  HOP-QA-ALIGN-005 closure rule and the broader scope tracked by `TD-I18N-002`.

## Application defects found and fixed

None. (Compare to `MVP-MOD-004-FE-001`, where a vanishing-success-banner defect was found and
fixed — this backlog item's screens were built with success banners outside status-gated controls
from the start, applying that lesson.)

## DAST Results

OWASP ZAP baseline was executed against the local employee portal at
`http://host.docker.internal:5173`. Reports were generated as `zap-employee-portal.html`,
`zap-employee-portal.json` and `zap.md`.

Summary: 0 FAIL, 4 WARN, 63 PASS — identical to `MVP-MOD-004-FE-001`'s result, confirming the new
screens introduced no new DAST-detectable finding.

Warnings disposition:

- `10038` Content Security Policy Header Not Set: tracked by `TD-FE-005`.
- `10049` Storable but Non-Cacheable Content: tracked by `TD-FE-005`.
- `10109` Modern Web Application: informational SPA detection, no debt required.
- `90004` Cross-Origin-Embedder-Policy Header Missing or Invalid: tracked by `TD-FE-005`.

## Vulnerabilities found and fixed

None in code or dependencies. DAST produced 0 FAIL findings; warning-level hosting header findings
are disposed through `TD-FE-005`.

## Residual findings — accepted risk

| ID | Finding | Risk | Owner | Target |
|---|---|---|---|---|
| TD-FE-005 | Production CSP, COEP and cache-control headers deferred to the production hosting layer | Medium | frontend_platform_team | production hosting/deployment backlog item |

## Technical debt

- **Closed**: `TD-FE-004` (coverage 76.51% → 80.57%, reaching the 80% final-closure target).
- **Newly registered**: none.
- **Unchanged, out of scope**: `TD-FE-003`, `TD-FE-005`, `TD-I18N-002`, `TD-APP-002`.
- **Blocking**: none.

## Readiness

Security/quality status: **passed**. Ready for next backlog item: **`MVP-MOD-005-QA-001`** —
Financial audit and reconciliation evidence.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: MVP-MOD-005-FE-001-SECURITY-QUALITY
  type: security-quality-evidence
  backlog_item: MVP-MOD-005-FE-001
  status: passed
  created_date: 2026-07-16
  standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
open_source_first:
  status: passed
  proprietary_runtime_dependency_detected: false
  new_dependencies_introduced: false
  note: No new dependency was introduced. The new API client, screens and shared money-formatting
    helper are pure application code built entirely on the existing employee-portal
    toolchain (React, TypeScript, Vitest, ESLint, Prettier) and existing shared components
    (StatusBanner, ConfirmDialog, ScopeIndicator, useAsyncAction, i18n/messages.ts).
checks:
  tests: passed
  sast_or_static_analysis: passed
  dependency_vulnerability_scan: passed
  secrets_scan: not_applicable_no_secrets_involved
  coverage: passed
  message_externalization_i18n_review: passed
  dast_for_runnable_web_or_api_surfaces: passed_with_disposed_warnings
  container_or_iac_scan_when_assets_change: not_applicable_no_assets_changed
results:
  tests_run: 33
  tests_failed: 0
  test_files: 17
  line_coverage_percent: 80.57
  previous_iteration_minimum_line_coverage_percent: 76.51
  coverage_regression: false
  final_closure_target_percent: 80
  final_closure_target_met: true
  eslint_errors: 0
  eslint_warnings: 17
  eslint_warnings_note: All 17 warnings are max-lines-per-function (or, on 2 screens,
    complexity/cognitive-complexity) on screen components — the same warning class
    every screen component in this codebase already carries, tracked collectively
    by TD-FE-003. An initial draft used the "void" operator on 3 intentionally-unawaited
    async calls (linesAction.run, paymentsAction.run, taxLinesAction.run); sonarjs/void-use
    flagged these as errors, so they were changed to plain unawaited invocations (the
    same pattern already accepted elsewhere in this codebase, e.g. PersonSearchScreen.tsx's
    `onClick={() => rebuildAction.run()}`) before this evidence was recorded, leaving
    0 errors.
  jscpd_duplication_findings: 0
  jscpd_note: A shared api/money.ts formatMoney() helper was extracted (replacing
    1 pre-existing duplicate in DiagnosticOrdersScreen.tsx plus 3 new call sites in
    the screens this backlog item adds) so 4 identical 4-line function bodies did
    not become a duplication finding.
  npm_audit_vulnerabilities: 0
  license_summary:
    mit: 5
    unlicensed: 1
  license_summary_note: Unchanged from the pre-existing baseline; no new dependency
    added.
pre_existing_draft_correction: 'An uncommitted, partially built CashSessionsScreen.tsx,
  cashSalesApi.ts and types.ts extension from a prior session were found in the working
  tree at the start of this backlog item. The types.ts draft modeled Money-typed backend
  fields (CashSession.openingAmount, Sale.totals.*, PaymentAllocation.amount) as flat
  number+currency pairs, used non-existent field names (SaleTotals.subtotal/taxTotal/total/paidTotal/outstandingBalance
  instead of the backend''s subtotalAmount/discountAmount/totalAmount/paidAmount/outstandingAmount)
  and invalid status literals (Sale.status "open" instead of the backend''s "payable";
  InvoiceRequest.status "pending"/"accepted"/"rejected" instead of the backend''s
  "requested"/"submitted"/"issued"/ "failed"/"cancelled"). This was a correctness/security-relevant
  finding: shipping it as-is would have produced a UI that silently failed to render
  real backend responses (undefined property access on the mismatched field names)
  with no compile-time signal, since the draft''s own type definitions matched the
  draft''s own (wrong) shape. All cashier/billing types were rewritten to match CashierOperationsController,
  BillingRequestController and their domain records field-for-field, verified against
  the actual Java source, before any screen was built or tested against them.'
message_externalization_review:
  new_repeated_strings_centralized:
  - id: selectCashSessionFirst
    value: Select a cash session first.
    occurrences: 1
    location: employee-portal/src/i18n/messages.ts
  - id: selectSaleFirst
    value: Select a sale first.
    occurrences: 3
    location: employee-portal/src/i18n/messages.ts
  - id: selectBillingRequestFirst
    value: Select a billing request first.
    occurrences: 3
    location: employee-portal/src/i18n/messages.ts
  backend_business_error_prose: Not re-authored on the frontend. Every CashSalesErrorCodes-prefixed
    message (e.g. BILLING_SALE_REQUIRED, PAYMENT_EXCEEDS_OUTSTANDING_BALANCE, CASH_VARIANCE_REASON_REQUIRED)
    is rendered as-is via StatusBanner, consistent with every other employee-portal
    screen; the backend is the single source of truth for that prose (HOP-QA-ALIGN-005
    baseline).
  single_occurrence_ui_copy: Headings, button labels and field hints introduced by
    this backlog item are single-occurrence within their own screen and remain inline,
    consistent with the HOP-QA-ALIGN-005 closure rule (only new/changed repeated content
    must be externalized) and the broader deferred scope tracked by TD-I18N-002.
application_defects_found_and_fixed: []
vulnerabilities_found_and_fixed: []
dast_results:
  tool: OWASP ZAP baseline scan
  command: docker run --rm --add-host=host.docker.internal:host-gateway -v "<repo>/projects/healthcare-operations-platform/08-qa/security-quality/MVP-MOD-005-FE-001:/zap/wrk"
    ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t http://host.docker.internal:5173
    -r zap-employee-portal.html -J zap-employee-portal.json -m 2
  target: http://host.docker.internal:5173
  generated_artifacts:
  - zap-employee-portal.html
  - zap-employee-portal.json
  - zap.md
  summary:
    fail_new: 0
    fail_in_progress: 0
    warn_new: 4
    warn_in_progress: 0
    info: 0
    pass: 63
  warning_disposition:
  - rule_id: 10038
    name: Content Security Policy Header Not Set
    disposition: tracked_existing_debt
    technical_debt: TD-FE-005
  - rule_id: 10049
    name: Storable but Non-Cacheable Content
    disposition: tracked_existing_debt
    technical_debt: TD-FE-005
  - rule_id: 10109
    name: Modern Web Application
    disposition: informational_spa_detection
    technical_debt: none
  - rule_id: 90004
    name: Cross-Origin-Embedder-Policy Header Missing or Invalid
    disposition: tracked_existing_debt
    technical_debt: TD-FE-005
  identical_to_prior_scan: Same 0 FAIL / 4 WARN / 63 PASS result as MVP-MOD-004-FE-001's
    baseline scan, confirming the new screens introduced no new DAST-detectable finding.
residual_findings_accepted_risk:
- id: TD-FE-005
  finding: Employee portal production CSP, COEP and cache-control headers remain deferred
    to the production hosting layer.
  risk_level: medium
  owner: frontend_platform_team
  target_backlog: production_hosting_and_deployment_backlog_item_not_yet_scheduled
technical_debt:
  closed:
  - TD-FE-004
  materially_reduced: []
  newly_registered: []
  unchanged_out_of_scope:
  - TD-FE-003
  - TD-FE-005
  - TD-I18N-002
  - TD-APP-002
  blocking: []
exceptions: []
readiness:
  security_quality_status: passed
  ready_for_next_backlog_item: MVP-MOD-005-QA-001
  next_required_focus:
  - Financial audit and reconciliation evidence (MVP-MOD-005-QA-001).
  - 'Continue debt-first execution: address at least one relevant open technical-debt
    item per code-changing iteration.'
  - Frontend coverage now meets the 80% final-closure target (80.57%); keep it from
    regressing in future frontend-touching iterations.
```
