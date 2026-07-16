# MVP-MOD-005-QA-001 — Financial Audit and Reconciliation Evidence

Backlog item: `MVP-MOD-005-QA-001` — Financial audit and reconciliation evidence. Status:
**passed**.

Machine-readable companion: `MVP-MOD-005-QA-001-validation.yaml`.

## Scope

Integrated validation of the full `MVP-MOD-005 Cashier and Billing Request` module: cash session
open/close and variance handling, sale creation from an accepted diagnostic order or accepted
quotation, sale totals/outstanding balance/payment status, payment allocation and its
outstanding-balance guard, sale cancellation, billing request creation from a paid sale and its
paid-sale guard, tax lines, submit/retry/cancel against the fiscal adapter (including simulated
adapter failure/retry), audit events for critical financial actions, module-boundary purity, and
confirmation that the `MVP-MOD-005-FE-001` employee-portal UI can drive the complete financial
flow, with the integrated local runbook able to start, validate and stop the full solution.

## Debt-first action

Reviewed `08-qa/technical-debt/technical-debt-index.yaml`. Selected and **closed `TD-BE-001`**
(Mockito dynamic self-attach / future-JDK compatibility): added a `maven-dependency-plugin`
`properties` execution to resolve `${org.mockito:mockito-core:jar}` and configured Surefire's
`argLine` to load Mockito as a supported `-javaagent`, verified by a full backend run (105 tests, 0
failures) with no self-attach warning.

As a side effect of the new test assertions, **`TD-BE-003`** (backend coverage) advanced from
67.47% to 68.66% — a 1.19-point reduction of the gap to the 80% target, no regression.

`TD-BE-002` and `TD-STACK-001` were reviewed and are out of scope for a validation-only backlog
item.

## What changed

| File | Purpose |
|---|---|
| `backend/pom.xml` | Mockito Java-agent configuration (closes `TD-BE-001`) |
| `backend/.../cashsales/CashSalesApiTest.java` | 3 conflict-path tests now assert the exact backend error code (`CASH_VARIANCE_REASON_REQUIRED`, `PAYMENT_EXCEEDS_OUTSTANDING_BALANCE`, `BILLING_SALE_REQUIRED`) in the response `detail` field; new `financialActionsProduceQueryableAuditEvents` test drives the full financial chain and queries the real `/api/audit/events` endpoint |
| `09-operations/runbooks/local-solution-runbook.yaml` | Added `SMOKE-010` (Cashier and Billing Request employee-portal screens) |
| `09-operations/runbooks/local-solution-runbook.md` | Added smoke items 8–9, syncing the Markdown companion with the YAML (item 8 for the pre-existing `SMOKE-009` backend baseline had never been mirrored) |

## Architecture and purity validation

- **Module boundary**: `cashsales/package-info.java` declares
  `@ApplicationModule(allowedDependencies = {"auditcompliance", "catalogtestconfiguration",
  "frontdeskcaredelivery::sale-source-port"})` — CashSales cannot depend on
  `peopleclinicalmasterdata` at all, and its only path into front-desk data is the named read-only
  port.
- **Read-only port**: `FrontDeskSaleSourcePort` exposes only `findOrderById`, `findOrderLines`,
  `findQuotationById`, `findQuotationLines` — no mutation method exists, and its Javadoc states
  explicitly it does not allow CashSales to mutate any front-desk aggregate.
- **Automated verification**: `PlatformFoundationModulithTest` (Spring Modulith's
  `ApplicationModules.of(...).verify()`) runs in every backend test pass and passed — it would fail
  the build if this boundary were ever violated.
- **Conclusion**: CashSales does not and structurally cannot mutate clinical, patient, order or
  catalog aggregates directly.

## Functional scenario coverage

| Scenario | Test | Result |
|---|---|---|
| Cash session open/close | `cashierCanListSaleLinesPaymentsSessionsAndCloseBalancedSession` | passed |
| Variance handling / `CASH_VARIANCE_REASON_REQUIRED` | `cashierRejectsOverpaymentAndRequiresVarianceReasonOnClose` (strengthened) | passed |
| Sale from accepted diagnostic order | multiple | passed |
| Sale from accepted quotation | `saleCanBeCreatedFromAcceptedQuotationAndCancelledBeforePayment` | passed |
| Totals / outstanding balance / paid status | `partialPaymentKeepsSalePayableForRemainingBalance`, etc. | passed |
| Payment allocation | multiple | passed |
| `PAYMENT_EXCEEDS_OUTSTANDING_BALANCE` | `cashierRejectsOverpaymentAndRequiresVarianceReasonOnClose` (strengthened) | passed |
| Sale cancellation | `saleCanBeCreatedFromAcceptedQuotationAndCancelledBeforePayment` | passed |
| Billing request from paid sale | `paidSaleCanCreateBillingRequestAndSubmitViaFiscalAdapter` | passed |
| `BILLING_SALE_REQUIRED` | `salesAndBillingRejectInvalidLifecycleTransitions` (strengthened) | passed |
| Tax lines | `paidSaleCanCreateBillingRequestAndSubmitViaFiscalAdapter` | passed |
| Submit/retry/cancel vs fiscal adapter | `billingRequestFullLifecycleSubmitRetryIssued`, etc. | passed |
| Simulated adapter failure/retry | `submitWithRetryableAdapterExceptionSetsFailedStatus`, `submitWithTerminalAdapterExceptionSetsFailedStatus`, `retryOnFailedStatusTransitionsToIssued` | passed |
| Audit events for financial actions | new `financialActionsProduceQueryableAuditEvents` (real `/api/audit/events` query) + mocked `verify(auditRecorder)` assertions | passed |
| No direct mutation of clinical/patient/order/catalog | `PlatformFoundationModulithTest` + architecture review | passed |
| FE-001 UI drives the complete flow | screen tests + live runbook smoke walkthrough (`SMOKE-010`) | passed |

## Validation commands

| Command | Result |
|---|---|
| `mvn -Dtest=CashSalesApiTest,BillingRequestAdapterUnitTest test` | passed — 26 tests (up from 25), 0 failures |
| Backend quality profile (checkstyle/pmd/spotbugs/cyclonedx/duplicate-finder) | passed — 105 tests, 0 failures, 8 skipped; **68.66%** line coverage (floor 67.47%, no regression) |
| Backend local PostgreSQL tests (`-Dhop.local-db-tests=true`) | passed — 105 tests, **0 skipped** |
| OWASP Dependency-Check (backend) | passed — 0 vulnerabilities |
| Trivy (backend) | passed — 0 vulnerabilities/secrets/misconfigurations |
| Trivy (integrated, full `07-implementation`) | passed — 0 vulnerabilities/secrets/misconfigurations |
| `npm run typecheck` / `lint` / `test:coverage` / `build` / `duplication` / `format:check` / `license:check` | all passed — 33 tests, **80.66%** line coverage (floor 80.57%, no regression) |
| `npm audit --audit-level=low` | passed — 0 vulnerabilities |
| OWASP ZAP API scan vs backend OpenAPI | passed — **0 FAIL, 0 WARN, 118 PASS** (fulfills the DAST gate `MVP-MOD-005-BE-002` deferred to this backlog item) |
| OWASP ZAP baseline vs employee portal | passed with disposed warnings — 0 FAIL, 4 WARN (same as `FE-001`), 63 PASS |
| End-to-end local runtime walkthrough | passed — full stack started/validated/stopped using only runbook-documented commands |
| `git diff --check` | passed — 0 whitespace errors in hand-authored files |

## Out of scope, confirmed and dispositioned

- `TD-BE-002`, `TD-STACK-001` — reviewed, out of scope for a validation-only backlog item.
- `TD-FE-003`, `TD-FE-005`, `TD-FE-006`, `TD-I18N-002`, `TD-APP-002` — pre-existing, unrelated (no
  frontend code changed).

## Final validations

- **VAL-001 YAML parse** — passed.
- **VAL-002 Agent-agnostic scan** — passed, 0 forbidden matches.
- **VAL-003 Secrets scan** — passed, 0 secrets (Trivy integrated scan + manual review of new ZAP/Trivy artifacts).
- **VAL-004 Stale pointer scan** — passed.
- **VAL-005 No prohibited execution-limitation statuses** — passed.
- **VAL-006 `git diff --check`** — passed.

## Readiness

`MVP-MOD-005-QA-001` status: **closed**. Ready for next backlog item:
**`MVP-MOD-005-CLOSEOUT`** — Module closeout and registry update.
