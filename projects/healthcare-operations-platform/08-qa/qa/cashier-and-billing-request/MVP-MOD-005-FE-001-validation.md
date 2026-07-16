# MVP-MOD-005-FE-001 — Cashier and Billing Request Employee Portal UI Validation

Backlog item: `MVP-MOD-005-FE-001` — Compile cashier and billing request UI outputs. Status:
**passed**.

Machine-readable companion: `MVP-MOD-005-FE-001-validation.yaml`.

## Scope

Delivers the three screens the backlog's acceptance summary names explicitly:

- **Cash Sessions** (Cashier Operations, BCM-ATT-005): open/close a cash session with counted
  amount, expected amount and the RN-004 variance reason requirement.
- **Sales** (Cashier Operations, BCM-ATT-005): create a sale from an accepted diagnostic order or
  quotation, list sales, view totals/outstanding balance/payment status, sale lines and payment
  history, register payment allocations and cancel a non-paid sale.
- **Billing Requests** (Billing Request Management, BCM-ATT-008): create a billing request from a
  paid sale, view tax lines, and submit/retry/cancel against the provider-agnostic fiscal adapter
  boundary implemented in `MVP-MOD-005-BE-002`.

## Pre-existing work in progress, corrected

This backlog item started with an uncommitted, partially built `CashSessionsScreen.tsx`,
`cashSalesApi.ts` and `types.ts` extension already in the working tree. Before reusing it, the
types were checked against the real backend contract (`CashierOperationsController`,
`BillingRequestController` and their domain records/services) — the draft had modeled `Money`
fields as flat `number`+`currency` pairs and used wrong field names and status literals (e.g.
`SaleTotals.subtotal/taxTotal/total/paidTotal/outstandingBalance` instead of the backend's
`subtotalAmount/discountAmount/totalAmount/paidAmount/outstandingAmount`, and `Sale.status "open"`
instead of the backend's `"payable"`). All cashier/billing types were corrected field-for-field
before any screen was built on top of them.

## Debt-first action

Reviewed `08-qa/technical-debt/technical-debt-index.yaml`. Selected **`TD-FE-004`** and **closed
it**: employee-portal line coverage rose from **76.51% to 80.57%**, reaching the 80% final-closure
target rather than merely reducing the gap. Achieved by shipping the new screens and API client
with matching tests from the start, plus extracting a shared `money.ts` formatting helper to keep
`jscpd` duplication at 0 findings across four now-identical `formatMoney` call sites.

`TD-FE-003` (ESLint complexity/duplicate-string warnings on unrelated pre-existing screens) and
`TD-I18N-002` (backend `code` field, full i18n-library adoption) were reviewed and are out of scope
for this UI-compilation backlog item.

## Implemented outputs

| File | Purpose |
|---|---|
| `api/types.ts` (+) | `CashSession`, `Sale`, `SaleTotals`, `SaleLine`, `PaymentAllocation`, `InvoiceRequest`, `FiscalProfileSnapshot`, `TaxLine` and request types, corrected to match the backend field-for-field |
| `api/money.ts` (new) | Shared `formatMoney(money?)` helper, replacing 1 pre-existing + 3 new duplicate copies |
| `api/cashSalesApi.ts` | Cashier Operations + Billing Request Management client |
| `components/screens/CashSessionsScreen.tsx` | Cash session console |
| `components/screens/SalesScreen.tsx` | Sale worklist + payment registration |
| `components/screens/BillingRequestsScreen.tsx` | Billing request worklist + fiscal profile + adapter response |
| `components/layout/AppShell.tsx` (+) | 3 new tabs: Cash Sessions, Sales, Billing Requests |
| `App.tsx` (+) | Screens wired into the router |
| `i18n/messages.ts` (+) | `selectCashSessionFirst`, `selectSaleFirst`, `selectBillingRequestFirst` |
| `components/screens/DiagnosticOrdersScreen.tsx` (~) | Local `formatMoney` removed in favor of the shared helper; no behavior change |
| `test/cashSalesApi.test.ts`, `test/CashSessionsScreen.test.tsx`, `test/SalesScreen.test.tsx`, `test/BillingRequestsScreen.test.tsx`, `test/AppSmoke.test.tsx` (+) | New/extended tests |

## Sale creation and totals

Source selector offers `diagnostic_order` and `quotation`, matching `Sale.SOURCE_DIAGNOSTIC_ORDER`
and `Sale.SOURCE_QUOTATION`. Sale lines are populated synchronously by the backend at creation and
rendered immediately. Sale detail shows `subtotalAmount`, `discountAmount`, `totalAmount`,
`paidAmount` and `outstandingAmount`, plus the status badge (`payable`/`partially_paid`/`paid`/
`cancelled`/`refunded`).

## Billing request lifecycle

Submit is only offered in `requested` status; retry only in `submitted` or `failed`; cancel is
hidden once status is terminal (`issued`/`cancelled`) — matching
`BillingRequestManagementService`'s own transition guards. Adapter failures do not throw: the
backend returns 200 with status `failed` and an `adapterResponseSnapshot`, rendered in the detail
view and unlocking Retry.

## Business error handling

`BILLING_SALE_REQUIRED`, `PAYMENT_EXCEEDS_OUTSTANDING_BALANCE` and `CASH_VARIANCE_REASON_REQUIRED`
(all HTTP 409) are surfaced verbatim from the backend and covered by dedicated tests. Every other
`CashSalesErrorCodes`-prefixed response is shown through the same `StatusBanner` mechanism already
used across the employee portal — no frontend-side error re-authoring or normalization was added.

## UX states

Loading, empty, error, confirmation and success states are present for all 13 new async actions.
Every success banner is rendered outside any status-gated control, so it stays visible after the
status transition it reports on — applying the lesson from the vanishing-success-banner defect
found and fixed in `MVP-MOD-004-FE-001`.

## Validation commands

| Command | Result |
|---|---|
| `npm run typecheck` | passed |
| `npm run lint` | passed — 0 errors, 17 warnings (pre-existing `max-lines-per-function`/complexity class, tracked by `TD-FE-003`); an initial draft's 3 `void`-operator uses were replaced with plain unawaited calls to satisfy `sonarjs/void-use` |
| `npm run test:coverage` | passed — 17 files, 33 tests, 0 failures; **80.57%** line coverage (floor 76.51%, no regression, meets the 80% final-closure target) |
| `npm run build` | passed |
| `npm run duplication` | passed — 0 findings |
| `npm run format:check` | passed — after `prettier --write` auto-fixed 5 files' minor formatting deviations |
| `npm run license:check` | passed — 5 MIT, 1 UNLICENSED (unchanged, no new dependency) |
| `npm audit --audit-level=low` | passed — 0 vulnerabilities |
| `git diff --check` | passed — 0 whitespace errors in hand-authored files (the 2 generated OWASP ZAP report artifacts carry tool-generated trailing whitespace identical to the already-committed `MVP-MOD-004-FE-001` ZAP reports and were left unmodified) |
| OWASP ZAP baseline | passed with disposed warnings — 0 FAIL, 4 WARN, 63 PASS; same warning set as `MVP-MOD-004-FE-001` (CSP/COEP/cache-control tracked by `TD-FE-005`, Modern Web Application informational) |
| Backend unchanged confirmation | passed — `git status` confirms no `07-implementation/backend` file touched |

## Out of scope, confirmed and dispositioned

- Backend API `code` field, full frontend i18n-library adoption — `TD-I18N-002` (pre-existing).
- ESLint complexity/duplicate-string cleanup on unrelated screens — `TD-FE-003` (pre-existing).
- Mobile coverage baseline — `TD-APP-002` (pre-existing, unrelated).

## Final validations

- **VAL-001 YAML parse** — passed.
- **VAL-002 Agent-agnostic scan** — passed, 0 forbidden matches (1 incidental `cursor: pointer` CSS
  false positive confirmed harmless).
- **VAL-003 Stale pointer scan** — passed.
- **VAL-004 No prohibited execution-limitation statuses** — passed.
- **VAL-005 `git diff --check`** — passed.

## Readiness

`MVP-MOD-005-FE-001` status: **closed**. Ready for next backlog item: **`MVP-MOD-005-QA-001`** —
Financial audit and reconciliation evidence.
