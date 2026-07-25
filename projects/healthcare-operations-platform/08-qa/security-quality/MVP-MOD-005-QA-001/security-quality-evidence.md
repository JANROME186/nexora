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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: MVP-MOD-005-QA-001-SECURITY-QUALITY
  type: security-quality-evidence
  backlog_item: MVP-MOD-005-QA-001
  status: passed
  created_date: 2026-07-16
  standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
open_source_first:
  status: passed
  proprietary_runtime_dependency_detected: false
  new_dependencies_introduced: false
  note: No new dependency was introduced. mockito-core was already a transitive test
    dependency via spring-boot-starter-test; this backlog item only added a maven-dependency-plugin
    execution to resolve its jar path for Surefire's argLine, and strengthened/added
    test assertions.
checks:
  tests: passed
  sast_or_static_analysis: passed
  dependency_vulnerability_scan: passed
  secrets_scan: passed
  coverage: passed_no_regression_frontend_improved_backend_unchanged
  message_externalization_i18n_review: not_applicable_no_user_facing_text_changed
  dast_for_runnable_web_or_api_surfaces: passed
  container_or_iac_scan_when_assets_change: not_applicable_no_container_or_iac_assets_changed
results:
  backend:
    tests_run: 105
    tests_failed: 0
    tests_skipped_default_profile: 8
    tests_skipped_local_db_profile: 0
    line_coverage_percent: 67.47
    previous_iteration_minimum_line_coverage_percent: 67.47
    coverage_regression: false
    coverage_correction_note: Originally reported as 68.66% due to a non-clean multi-run
      jacoco.exec accumulation; corrected to the clean-rebuild figure (67.47%, unchanged
      from MVP-MOD-005-BE-002) during MVP-MOD-005-CLOSEOUT. See MVP-MOD-005-QA-001-validation.md's
      coverage_measurement_correction.
    checkstyle: passed
    pmd: passed
    pmd_cpd_duplicate_code: passed
    spotbugs_find_security_bugs: passed
    duplicate_finder: passed
    maven_enforcer: passed
    cyclonedx_sbom: passed
    dependency_check_vulnerabilities: 0
    trivy_backend_vulnerabilities: 0
    trivy_backend_secrets: 0
    trivy_backend_misconfigurations: 0
    trivy_integrated_vulnerabilities: 0
    trivy_integrated_secrets: 0
    trivy_integrated_misconfigurations: 0
  frontend:
    tests_run: 33
    test_files: 17
    tests_failed: 0
    line_coverage_percent: 80.66
    previous_iteration_minimum_line_coverage_percent: 80.57
    coverage_regression: false
    eslint_errors: 0
    eslint_warnings: 17
    eslint_warnings_note: Unchanged pre-existing max-lines-per-function/complexity
      class (TD-FE-003); no frontend source file was modified in this backlog item.
    jscpd_duplication_findings: 0
    npm_audit_vulnerabilities: 0
    license_summary:
      mit: 5
      unlicensed: 1
architecture_and_purity_review:
  module_boundary: cashsales/package-info.java declares @ApplicationModule(allowedDependencies
    = {"auditcompliance", "catalogtestconfiguration", "frontdeskcaredelivery::sale-source-port"}).
    CashSales has no dependency path to peopleclinicalmasterdata (patient/doctor aggregates),
    and its only path into front-desk data is the named read-only sale-source-port
    interface.
  read_only_boundary: FrontDeskSaleSourcePort exposes only findOrderById, findOrderLines,
    findQuotationById and findQuotationLines; no mutation method exists; Javadoc states
    explicitly that CashSales cannot mutate any front-desk aggregate through this
    port.
  automated_verification: PlatformFoundationModulithTest (Spring Modulith's ApplicationModules.of(...).verify())
    ran and passed in every backend test execution for this backlog item, which fails
    the build automatically if this declared boundary is ever violated.
  conclusion: passed. CashSales does not and structurally cannot mutate clinical,
    patient, order or catalog aggregates directly.
audit_event_traceability_review:
  method: New financialActionsProduceQueryableAuditEvents test drives sale creation,
    cash session open/close, payment registration, billing request creation and submit
    against the real Spring MVC layer, then queries the real GET /api/audit/events
    endpoint by subjectId.
  events_confirmed_queryable:
  - SaleCreated (subjectId = saleId)
  - SalePaymentRegistered (subjectId = saleId)
  - CashSessionOpened (subjectId = sessionId)
  - CashSessionClosed (subjectId = sessionId)
  - BillingRequestCreated (subjectId = invoiceRequestId)
  - BillingRequestSubmitted (subjectId = invoiceRequestId)
  additional_mocked_verification: BillingRequestAdapterUnitTest independently verifies
    verify(auditRecorder).recordSystemEvent(...) for BillingRequestSubmitted, BillingRequestSubmitFailed,
    BillingRequestRetried and BillingRequestCancelled against a mocked AuditRecorder,
    covering the adapter-failure path that is impractical to trigger through the real
    deterministic local adapter.
  result: passed
application_defects_found_and_fixed: []
vulnerabilities_found_and_fixed: []
dast_results:
  api_scan:
    tool: OWASP ZAP API scan (zap-api-scan.py)
    command: docker run --rm --add-host=host.docker.internal:host-gateway -v "<repo>/projects/healthcare-operations-platform/08-qa/security-quality/MVP-MOD-005-QA-001:/zap/wrk"
      ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py -t http://host.docker.internal:8080/v3/api-docs
      -f openapi -r zap-backend-api.html -J zap-backend-api.json
    target: http://host.docker.internal:8080/v3/api-docs
    generated_artifacts:
    - zap-backend-api.html
    - zap-backend-api.json
    summary:
      fail_new: 0
      fail_in_progress: 0
      warn_new: 0
      warn_in_progress: 0
      info: 0
      pass: 118
    disposition: 'Fulfills the DAST-for-runnable-API-surfaces gate that MVP-MOD-005-BE-002''s
      security-quality evidence explicitly deferred to this backlog item''s integrated
      runtime validation (dast_for_runnable_web_or_api_surfaces: deferred_to_MVP_MOD_005_QA_001_integrated_runtime_validation).'
  baseline_scan:
    tool: OWASP ZAP baseline scan
    command: docker run --rm --add-host=host.docker.internal:host-gateway -v "<repo>/projects/healthcare-operations-platform/08-qa/security-quality/MVP-MOD-005-QA-001:/zap/wrk"
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
    identical_to_prior_scans: Same 0 FAIL / 4 WARN / 63 PASS result as MVP-MOD-004-FE-001
      and MVP-MOD-005-FE-001, now confirmed with the backend also live (integrated
      conditions), no new finding.
residual_findings_accepted_risk:
- id: TD-FE-005
  finding: Employee portal production CSP, COEP and cache-control headers remain deferred
    to the production hosting layer.
  risk_level: medium
  owner: frontend_platform_team
  target_backlog: production_hosting_and_deployment_backlog_item_not_yet_scheduled
technical_debt:
  closed:
  - TD-BE-001
  materially_reduced:
  - TD-BE-003
  newly_registered: []
  unchanged_out_of_scope:
  - TD-BE-002
  - TD-STACK-001
  - TD-FE-003
  - TD-FE-005
  - TD-FE-006
  - TD-I18N-002
  - TD-APP-002
  blocking: []
local_runtime_validation:
  docker_compose_started: true
  docker_compose_services:
  - postgres
  - redis
  - otel-collector
  backend_started: true
  employee_portal_started: true
  all_services_stopped_after_validation: true
  startup_and_shutdown_used_only_runbook_documented_commands: true
exceptions: []
readiness:
  security_quality_status: passed
  ready_for_next_backlog_item: MVP-MOD-005-CLOSEOUT
  next_required_focus:
  - Module closeout and registry update (MVP-MOD-005-CLOSEOUT).
  - Continue raising backend coverage toward the 80% final-closure target without
    dropping below 67.47% (corrected during MVP-MOD-005-CLOSEOUT).
  - Continue raising frontend coverage toward the 80% final-closure target without
    dropping below 80.66% (target already met; keep it from regressing).
```
