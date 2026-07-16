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
`zap-employee-portal.json` and `zap.yaml`.

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
