# MVP-MOD-007 Closeout

Status: `passed`

`MVP-MOD-007 Results and Digital Delivery` is closed. The module delivered the modeled capability
packages (`BCM-RES-001/002/004/005/006/007`, `BCM-PLT-003`, `BCM-PLT-008`), backend result-report
and document-generation outputs, the digital-delivery/notification/critical-result custom rules,
the employee-portal result-delivery UI, the patient/doctor released-result portal views, the
mobile result view and notification baseline, and integrated result-access/PDF/notification
validation.

## Two real registry gaps found and corrected during this closeout

**Employee-portal coverage regression.** `MVP-MOD-007-PORTAL-001` expanded
`laboratoryOperationsApi.ts` and `laboratoryResultMapper.ts` (shared with the employee portal)
without adding a dedicated unit test for the API module, silently dropping employee-portal
coverage from the 84.44% floor to **84.03%** -- unnoticed because that backlog item's own scope
was the patient/doctor portals, not the employee portal. Added
`src/test/laboratoryOperationsApi.test.ts` (mirroring the existing
`cashSalesApi.test.ts`/`frontDeskApi.test.ts` pattern) covering all 18 exported functions,
restoring coverage to **85.50%**, above the previous floor.

**Patient-portal and doctor-portal coverage never measured.** `MVP-MOD-007-PORTAL-001` closed both
stacks with only an application smoke test each and never recorded a coverage percentage anywhere.
Measured both for the first time: patient-portal **41.93%**, doctor-portal **40.62%**. Registered
`TD-FE-008`/`TD-FE-009` to track and raise these toward the 80% target under the future
`COM-MOD-009` expansion, without blocking this closeout -- the same pattern used when
`TD-FE-004`/`TD-APP-002` were opened for employee-portal/mobile at their own inception.

## Technical debt closed by this closeout

**`TD-BE-010`** (diagnostic order cancellation used order-status as a proxy for downstream
sample/processing state) is now **closed** with a real code change, not just a registry note:
`frontdeskcaredelivery`'s Spring Modulith `allowedDependencies` was extended to
`laboratoryworkflow::sample-read-port`, and `DiagnosticOrderManagementService.cancel()` now calls
`SampleReadPort#hasActiveSampleForOrder(orderId, tenantId)` (already implemented by
`OrderSamplesService` since `MVP-MOD-006-BE-002`) as the primary clinically-engaged trigger. The
original order-status tier is retained only as a fallback for orders without a linked sample
record, so no existing compensating control is lost. A new test,
`FrontDeskCareDeliveryApiTest.diagnosticOrderCancellationRequiresOverrideOnceARealSampleIsCollectedRegardlessOfOrderStatus`,
collects a real Sample against a "priced" (not yet accepted) order and proves cancellation is
rejected without an override and accepted with one -- the exact scenario the old order-status-only
check would have missed.

## Validation (re-executed clean for this closeout)

- Backend quality profile (`mvn -Pquality "-Dhop.local-db-tests=true" clean verify`): 211 tests
  (210 pre-existing + 1 new), 0 failures, 0 errors, 0 skipped, JaCoCo line coverage **78.51%**, at
  or above the 78.42% floor.
- Backend static analysis (`spotbugs:spotbugs`, `pmd:pmd`, `pmd:cpd`, explicitly invoked): only
  pre-existing, repo-wide findings unrelated to this closeout's change (tracked by `TD-BE-002`).
- OWASP Dependency-Check: 0 vulnerabilities.
- Trivy integrated scan (`07-implementation`, backend + all four frontend/mobile stacks): 0
  vulnerabilities, 0 secrets, 0 misconfigurations.
- Employee portal `npm run quality`: 89 tests, 0 failures, line coverage **85.50%**, 0 ESLint
  errors, 0 `jscpd` findings. `npm audit --audit-level=low`: 0 vulnerabilities.
- Patient portal `npm run quality`: 1 test, 0 failures, line coverage **41.93%** (first
  measurement). `npm audit --audit-level=low`: 0 vulnerabilities.
- Doctor portal `npm run quality`: 1 test, 0 failures, line coverage **40.62%** (first
  measurement). `npm audit --audit-level=low`: 0 vulnerabilities.
- Mobile app `npm run quality`: 31 tests, 0 failures, line coverage **98.87%**, matching the
  required floor exactly. `npm audit --audit-level=low`: 0 vulnerabilities.

## Acceptance summary validation

| Requirement | Status |
|---|---|
| Released results can generate a PDF report | passed (`ResultReportService` produces `application/pdf` content; `ResultReportServiceTest`, 4 tests) |
| Patients and doctors see only authorized released results | passed (permission-filtered, authorized-only access re-confirmed from `MVP-MOD-007-PORTAL-001` evidence) |
| Critical results trigger traceable notification workflows | passed (`CriticalResultEscalationServiceTest`, 5 tests; `ResultNotificationServiceTest`/`NotificationManagementServiceTest`, 3 tests each) |

## Debt-first review

Unlike prior `CLOSEOUT` items in this project (registry-consolidation only), this closeout required
a real code change to close `TD-BE-010`, which had been blocked on `MVP-MOD-006-BE-002` (now
closed). The technical-debt index was fully reviewed: 1 item closed by this backlog item
(`TD-BE-010`), 2 new items registered (`TD-FE-008`, `TD-FE-009`), 19 items remain open
project-wide in total (17 of them unrelated to this module's capabilities), and are correctly left
for the backlog items whose scope they belong to.

## Registry consistency sweep

Found and corrected: the two coverage gaps above, `TD-BE-010`'s closure, a stale mobile-coverage
figure in `technical-debt-index.yaml` (97.15% instead of the 98.87% `MVP-MOD-007-APP-001` had
already measured), and moved every `MVP-MOD-007`-referencing active/current/next pointer (project
and root `PROJECT_STATE.yaml`, `SOURCE_OF_TRUTH.yaml`, the commercial backlog and execution
prompts -- including the stale `MD` companion the closeout brief flagged up front -- the capability
package index, all 9 impacted capability traceability files, and the local runbook) forward to
`MVP-MOD-008-DEF`.

## Boundaries -- HOP is not commercially complete or GA-ready

- Backend coverage (78.51%) remains below the 80% final-closure target (`TD-BE-003`).
- Employee portal (85.50%) and mobile (98.87%) coverage already meet the 80% target but must not regress.
- Patient-portal (41.93%) and doctor-portal (40.62%) coverage remain below the 80% target
  (`TD-FE-008`/`TD-FE-009`).
- 19 technical-debt items remain open project-wide; final HOP closure requires all of them closed.
- `MVP-MOD-008` and later releases remain planned within `REL-001` and beyond, before any
  `REL-002`/`REL-003`/`REL-004` commercial-beta/GA/expansion work begins.

The module is ready for the next backlog item: **`MVP-MOD-008-DEF`** (Integration and Migration
Readiness capability package models).
