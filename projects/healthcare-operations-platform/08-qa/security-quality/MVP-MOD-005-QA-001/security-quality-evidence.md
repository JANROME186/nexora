# MVP-MOD-005-QA-001 — Security & Quality Evidence

Backlog item: `MVP-MOD-005-QA-001` — Financial audit and reconciliation evidence. Status:
**passed**.

## Open-source-first

No new dependency was introduced. `mockito-core` was already a transitive test dependency via
`spring-boot-starter-test`; this backlog item only added a `maven-dependency-plugin` execution to
resolve its jar path for Surefire's `argLine`, plus strengthened/added test assertions.

## Checks

| Check | Result |
|---|---|
| Backend tests (105, 8 skipped default / 0 skipped local-db) | passed |
| Frontend tests (33, 17 files) | passed |
| SAST / static analysis (Checkstyle, PMD, SpotBugs/Find Security Bugs, ESLint) | passed |
| Dependency vulnerability scan (OWASP Dependency-Check, `npm audit`) | passed, 0 vulnerabilities |
| Secrets scan (Trivy) | passed, 0 secrets |
| Coverage | passed — backend 67.47% (floor 67.47%, unchanged; corrected from an originally-reported 68.66% during `MVP-MOD-005-CLOSEOUT`), frontend 80.66% (floor 80.57%, improved), no regression |
| Message externalization / i18n review | not applicable — no user-facing text changed |
| DAST (OWASP ZAP API scan + baseline) | passed — 0 FAIL on both scans |
| Container-IaC scan | not applicable — no container or IaC assets changed |

## Architecture and purity review

- **Module boundary**: `cashsales/package-info.java`'s `@ApplicationModule` declaration has no
  dependency path to `peopleclinicalmasterdata`, and reaches front-desk data only through the named
  read-only `sale-source-port` interface.
- **Read-only boundary**: `FrontDeskSaleSourcePort` exposes only 4 find-methods, no mutation
  method.
- **Automated verification**: `PlatformFoundationModulithTest` ran and passed, which fails the
  build automatically if this boundary is ever violated.
- **Conclusion**: passed — CashSales does not and cannot mutate clinical, patient, order or catalog
  aggregates directly.

## Audit event traceability review

A new test (`financialActionsProduceQueryableAuditEvents`) drives the full financial chain against
the real Spring MVC layer, then queries the real `GET /api/audit/events` endpoint and confirms
`SaleCreated`, `SalePaymentRegistered`, `CashSessionOpened`, `CashSessionClosed`,
`BillingRequestCreated` and `BillingRequestSubmitted` are all independently queryable by subject id.
`BillingRequestAdapterUnitTest` additionally verifies audit recording for the adapter-failure path
via a mocked `AuditRecorder`. Result: **passed**.

## Application defects found and fixed

None.

## DAST Results

**API scan** (`zap-api-scan.py`) against `http://host.docker.internal:8080/v3/api-docs`: **0 FAIL, 0
WARN, 118 PASS**. This fulfills the DAST-for-runnable-API-surfaces gate that
`MVP-MOD-005-BE-002`'s security-quality evidence explicitly deferred to this backlog item.

**Baseline scan** against `http://host.docker.internal:5173`: 0 FAIL, 4 WARN, 63 PASS — identical to
`MVP-MOD-004-FE-001` and `MVP-MOD-005-FE-001`'s results, now confirmed with the backend also live.

Warnings disposition (baseline scan): `10038` CSP Header Not Set, `10049` Storable but
Non-Cacheable Content, and `90004` COEP Header Missing — all tracked by `TD-FE-005`; `10109` Modern
Web Application is informational SPA detection.

## Vulnerabilities found and fixed

None in code or dependencies.

## Residual findings — accepted risk

| ID | Finding | Risk | Owner | Target |
|---|---|---|---|---|
| TD-FE-005 | Production CSP, COEP and cache-control headers deferred to the production hosting layer | Medium | frontend_platform_team | production hosting/deployment backlog item |

## Technical debt

- **Closed**: `TD-BE-001` (Mockito Java-agent configuration).
- **Materially reduced (unchanged this iteration)**: `TD-BE-003` (backend coverage confirmed at
  67.47%; a 68.66% improvement figure was originally claimed here but corrected to 67.47% during
  `MVP-MOD-005-CLOSEOUT` after a clean-rebuild remeasurement).
- **Newly registered**: none.
- **Unchanged, out of scope**: `TD-BE-002`, `TD-STACK-001`, `TD-FE-003`, `TD-FE-005`, `TD-FE-006`,
  `TD-I18N-002`, `TD-APP-002`.
- **Blocking**: none.

## Local runtime validation

Full stack started (Docker Compose postgres/redis/otel-collector, backend, employee portal),
validated with every gate above plus both ZAP scans, then stopped cleanly — using only
runbook-documented commands, no dispersed or undocumented steps.

## Readiness

Security/quality status: **passed**. Ready for next backlog item:
**`MVP-MOD-005-CLOSEOUT`** — Module closeout and registry update.
