# MVP-MOD-004-FE-001 — Front Desk Worklist and Diagnostic Order Employee Portal UI Validation

Backlog item: `MVP-MOD-004-FE-001` — Compile front desk worklist and order creation
employee-portal UI outputs. Status: **passed**.

Machine-readable companion: `MVP-MOD-004-FE-001-validation.yaml`.

## Scope

Delivers the two screens the backlog's acceptance summary names explicitly:

- **Front Desk** (Reception Management, BCM-ATT-003): the worklist queue, walk-in/scheduled visit
  start, identity confirmation, priority update, advance-to-admission and abandon.
- **Diagnostic Orders** (Diagnostic Order Management, BCM-LAB-001): order creation supporting
  walk-in, appointment, admission and quotation-conversion intake channels with a dynamic
  test/panel line editor, an order list, order detail showing the immutable patient/doctor/
  branch/pricing snapshots captured at order time, and lifecycle actions (price, accept, complete,
  cancel with the RN-005 tiered override justification).

Appointment Scheduling, Admission Management and Quotation Management do **not** get dedicated
screens in this iteration — those backend capabilities are fully implemented and tested but
reachable only via direct API today. That gap is formally registered as `TD-FE-006` (owner,
target backlog and acceptance criteria) rather than left undocumented, following the same
disposition `TD-FE-002` used after `MVP-MOD-003-FE-001`.

## Debt-first action

Reviewed `08-qa/technical-debt/technical-debt-index.yaml`. Selected **`TD-FE-004`** (raise
employee-portal coverage toward 80%): the new code shipped with matching tests from the start,
raising measured line coverage from **73.04% to 76.51%** — a 3.47-point reduction of the gap, no
regression on any stack.

An initial attempt at `TD-FE-003` (mechanically extracting 2 duplicate-string ESLint warnings in
`DiagnosticCatalogScreen.tsx` into named constants) broke TypeScript's exhaustive-switch narrowing
and was reverted rather than shipped as a regression. `TD-I18N-002` requires backend contract
changes and a new frontend dependency, both out of scope here.

## Implemented outputs

| File | Purpose |
|---|---|
| `api/frontDeskApi.ts` | Reception Management + Diagnostic Order Management client |
| `api/types.ts` (+) | `ReceptionVisit`, `DiagnosticOrder`, `OrderLine`, `Money`, 4 snapshot types |
| `components/screens/ReceptionScreen.tsx` | Front desk worklist |
| `components/screens/DiagnosticOrdersScreen.tsx` | Order creation + lifecycle (with `OrderLineEditor`/`OrderLifecycleActions` sub-components) |
| `components/layout/AppShell.tsx` (+) | 2 new tabs: Front Desk, Diagnostic Orders |
| `App.tsx` (+) | Screens wired into the router |
| `i18n/messages.ts` (+) | `selectReceptionVisitFirst`, `selectOrderFirst` |
| `styles.css` (+) | `.order-line-row` |
| `test/frontDeskApi.test.ts`, `test/ReceptionScreen.test.tsx`, `test/DiagnosticOrdersScreen.test.tsx`, `test/AppSmoke.test.tsx` (+) | New/extended tests |

## Walk-in and scheduled support

- **Reception**: intake channel selector offers `walk_in` and `scheduled`; the scheduled channel
  accepts a linked appointment id.
- **Diagnostic orders**: intake channel selector offers `walk_in`, `appointment`, `admission` and
  `quotation_conversion`, with an optional source reference id — matching the backend's
  `CreateDiagnosticOrderRequest` exactly.

## Immutable snapshots

Order detail renders `patientSnapshot` (name, document type, masked document number),
`doctorSnapshot` (or "None"), `branchSnapshot` (name), and `pricingSnapshot` (total, price list id
and version) — all read-only, never re-derived from live patient/doctor/branch state.

## Business error handling

`RECEPTION_IDENTITY_NOT_CONFIRMED` and `ORDER_CANCELLATION_OVERRIDE_REQUIRED` (both HTTP 409) are
surfaced verbatim from the backend and covered by dedicated tests. Every other
`FrontDeskErrorCodes`-prefixed response is shown through the same `StatusBanner` mechanism already
used across the employee portal.

## UX states — and a bug found and fixed

Loading, empty, error, confirmation and success states are all present. Testing surfaced a real
defect: several success banners (confirm identity, price, accept, complete, cancel) were rendered
only inside the same status-gated JSX block that the action's own success response hides — e.g.
pricing an order flips `order.status` from `draft` to `priced`, which immediately hid the "Price
order" button *and* its success banner in the same render. Fixed by moving each `StatusBanner`
outside the status-gated control so the confirmation stays visible after the transition it
reports on. This was caught by `findByText` timing out in the new tests, not by manual review.

## Validation commands

| Command | Result |
|---|---|
| `npm run typecheck` | passed |
| `npm run lint` | passed — 0 errors, 11 warnings (all `max-lines-per-function`, the same pre-existing warning class every screen in this codebase carries) |
| `npm run test:coverage` | passed — 13 files, 24 tests, 0 failures; **76.51%** line coverage (floor 73.04%, no regression) |
| `npm run build` | passed |
| `npm run duplication` | passed — 0 findings |
| `npm run format:check` | passed |
| `npm run license:check` | passed — 5 MIT, 1 UNLICENSED (unchanged, no new dependency) |
| `npm audit --audit-level=low` | passed — 0 vulnerabilities |
| OWASP ZAP baseline | passed with disposed warnings — 0 FAIL, 4 WARN, 63 PASS; CSP/COEP/cache-control warnings tracked by `TD-FE-005`, Modern Web Application informational |
| Backend unchanged confirmation | passed — `git status` confirms no `07-implementation/backend` file touched |

## Out of scope, confirmed and dispositioned

- Appointment Scheduling, Admission Management, Quotation Management screens — `TD-FE-006` (new).
- Backend API `code` field, full frontend i18n-library adoption — `TD-I18N-002` (pre-existing).
- Mobile coverage baseline — `TD-APP-002` (pre-existing, unrelated).

## Final validations

- **VAL-001 YAML parse** — passed.
- **VAL-002 Agent-agnostic scan** — passed, 0 forbidden files/folders, 0 content matches.
- **VAL-003 Stale pointer scan** — passed.
- **VAL-004 No prohibited execution-limitation statuses** — passed.
- **VAL-005 `git diff --check`** — passed.

## Readiness

`MVP-MOD-004-FE-001` status: **closed**. Ready for next backlog item: **`MVP-MOD-004-QA-001`** —
Order lifecycle and snapshot evidence.
