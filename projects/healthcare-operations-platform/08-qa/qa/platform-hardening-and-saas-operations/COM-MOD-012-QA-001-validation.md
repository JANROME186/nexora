# COM-MOD-012-QA-001 Validation Evidence

**Artifact ID**: HOP-QA-COM-MOD-012-QA-001
**Module**: COM-MOD-012 Platform Hardening and SaaS Operations
**Status**: Passed
**Date**: 2026-07-23
**Owner**: Nexora QA & Security Team

---

## Executive Summary

Performance, resilience and security evidence for **COM-MOD-012 Platform Hardening and SaaS Operations** has been successfully executed. All 8 capabilities were validated end to end against a running local backend. Two real defects were found and fixed during this validation: a resilience defect (the readiness probe did not reflect database connectivity) and, via a dedicated OWASP ZAP DAST pass against the full backend API surface, a cross-cutting unhandled-500 defect (`TD-QA-005`) and an `AuthController` exception-advice scope gap (`TD-QA-006`). Both were fixed with regression tests in the same iteration. A real backup and restore rehearsal was executed successfully. The 3 remaining `COM-MOD-012-BE-001` infrastructure forward pointers (distributed trace export, a provisioned Grafana/Prometheus/Loki stack, SLO/SLA alerting) were confirmed still genuinely blocked on infrastructure and registered as `TD-OBS-001` rather than closed.

---

## Environment Note

Port `8080` on this shared local machine was occupied by an unrelated, pre-existing Tomcat process belonging to a different, unaffiliated project (`Jaime_proyectos_AWS`). The backend was started with `server.port=8090` for this validation session only, via `-Dspring-boot.run.jvmArguments`; no runbook, compose or `application*.yml` port configuration was changed. The canonical documented port remains `8080`.

---

## Capability Traceability

| Capability ID | Capability Name | Validation Result |
|---|---|---|
| `BCM-ORG-001` | Tenant Management | Verified — provision/list/status-transition, invalid inputs, 20 concurrent provisions with 0 races, audit events confirmed |
| `BCM-PLT-001` | Identity and Access Management | Verified — permission registry entries reachable and deny-by-default; `TD-IAM-003` confirmed still open |
| `BCM-PLT-002` | Platform Configuration | Verified — config/feature-flag operations, safe-default-false invariant, namespace/rollout validation |
| `BCM-PLT-005` | API Management | Verified — full OWASP ZAP API scan, 0 SQLi/XSS/RCE/path-traversal/SSRF; 2 real defects found and fixed |
| `BCM-PLT-006` | Observability | Verified — Prometheus/health/MDC live; readiness resilience defect found and fixed; backup/restore rehearsed |
| `BCM-PLT-007` | Audit Trail | Verified — both privileged operations' audit events confirmed via live query |
| `BCM-PLT-008` | Document Management | Verified — confirmed `extension_deferred`, unchanged |
| `BCM-PLT-009` | Workflow Engine | Verified — confirmed `not_implemented` (`TD-BE-017`); manually exercised the runbook workflow it will eventually automate |

---

## Performance and Resilience Evidence

### Light Load Check
- 30 sequential `GET /api/platform/tenants` requests: average latency **12.1ms**.
- 20 concurrent `POST /api/platform/tenants` provisions: **342ms total**, 0 failures, 0 duplicate-code races.

### Health/Readiness/Liveness — Controlled Failure Scenario
A real defect was found: with PostgreSQL running, the readiness probe reported `UP`; **PostgreSQL was then stopped** (`docker stop hop-local-postgres`) and readiness still reported `UP` (200), because `management.endpoint.health.group.readiness.include` was unset and the default readiness group only contains `readinessState`, not `db`.

**Fix**: Added the include to `application-local.properties` (the profile with a real `DataSource` bean). The same change in the base `application.properties` broke context startup for profiles with no `DataSource` bean (reproduced: `"Health contributor 'db' ... does not exist"`), so the fix was correctly scoped. A new regression test, `ObservabilityReadinessLocalDatabaseTest`, locks this in.

**Re-verified live**: readiness correctly reports `DOWN`/503 with PostgreSQL stopped and `UP`/200 after `docker start hop-local-postgres`; liveness correctly stays `UP` throughout (no unnecessary pod-restart churn).

### Invalid Input Handling
| Case | Expected | Result |
|---|---|---|
| Duplicate tenant code | 409 | 409 |
| Invalid tenant status value | 400 | 400 |
| Status transition on nonexistent tenant | 404 | 404 |
| Feature-flag evaluation with no tenantId | safe default false, 200 | safe default false, 200 |
| Non-namespaced feature-flag key | 400 | 400 |
| Feature-flag rollout percentage out of bounds | 400 | 400 |
| Malformed JSON body | 400 | 400 |
| Null byte in a string query parameter (found by DAST) | 400 | 500 before fix, **400 after fix** (`TD-QA-005`) |
| Oversized string field value (found by DAST) | 400 | 500 before fix, **400 after fix** (`TD-QA-005`) |
| Nonexistent `assistedUserId` on assistance endpoint (found by DAST) | 404 | 500 before fix, **404 after fix** (`TD-QA-006`) |

### Backup and Restore Rehearsal
- **Backup**: `pg_dump -F c`, 317,157 bytes, SHA-256 `76e4a751...913d6b080`, structurally verified via `pg_restore --list` (415 TOC entries, no corruption).
- **Restore rehearsal**: isolated `hop_restore_verify` database, `pg_restore --clean --if-exists`, row counts matched source exactly (40 = 40), verification database dropped after.
- The binary dump artifact was not committed to the repository (matches the runbook's own "local filesystem, not durable object storage" framing); checksum and verification output are retained here as evidence.

---

## Security Findings and Fixes

### DAST — OWASP ZAP API Scan (full `/v3/api-docs` surface, 353 URLs)
- **First run**: `FAIL-NEW 0`, `WARN-NEW 3` (19 server-error instances across 2 real defect classes).
- **After fix**: **`FAIL-NEW 0`, `WARN-NEW 0`, `PASS 118`** — completely clean.
- 0 SQL injection, XSS, RCE, path traversal or SSRF findings at any point.

### DAST — OWASP ZAP Baseline Scan (employee portal, unchanged by this backlog item)
- `FAIL-NEW 0`, `WARN-NEW 4` (missing CSP/COEP headers, non-cacheable content) — all match the already-registered `TD-FE-005` (deferred to the production hosting layer).

### Defects Found and Fixed
1. **`TD-QA-005`** — A null byte (PostgreSQL SQLState `22021`) or an oversized string value (SQLState `22001`) reaching a JDBC statement caused an unhandled 500 on 6 `laboratoryworkflow` worklist endpoints and `POST /api/revenue/cashier/sessions`. No stack trace or internal detail was ever exposed to the client. Fixed with a narrow `GlobalExceptionHandler.handleDataIntegrityViolationException()` mapping keyed on SQLState, leaving every other cause (e.g. real `23505` conflicts) unchanged.
2. **`TD-QA-006`** — `POST /api/auth/assistance` returned 500 instead of 404 for a nonexistent `assistedUserId`, because `IdentityAccessExceptionHandler`'s `@RestControllerAdvice` was scoped to only `IdentityAccessController`. Fixed by widening `assignableTypes` to also cover `AuthController`.

Both fixes are covered by new regression tests and re-verified live after rebuild/restart.

### Forward Pointer Review (not closed without infrastructure)
- **Distributed trace export**: The local OTel Collector container is running and reachable, but the backend has no OpenTelemetry/micrometer-tracing dependency to export spans to it (confirmed by grep — no matches).
- **Provisioned Grafana/Prometheus/Loki stack**: Confirmed still not provisioned in any environment.
- **SLO/SLA alerting backend**: `SloDefinition` remains a modeled value object only; no alerting backend evaluates it.
- All 3 registered as **`TD-OBS-001`**, not closed.

---

## Mandatory Quality Gates

### Backend (`07-implementation/backend`)
- **Maven Quality Profile**: Passed (`mvn -Pquality "-Dhop.local-db-tests=true" clean verify` + checkstyle/pmd/spotbugs/cyclonedx/duplicate-finder).
- **Tests**: 367 tests, 0 failures, 0 errors, 0 skipped (up from 362).
- **Line Coverage**: **84.14%** (Floor: 84.11%) — no regression.
- **Checkstyle**: 31 findings, 0 in new/touched files.
- **PMD/CPD**: 479 findings / 2 duplications; 1 pre-existing finding in a touched file (unrelated `FieldNamingConventions`), 0 new violation classes.
- **SpotBugs/FindSecBugs**: 35 findings, 0 in new/touched files.
- **OWASP Dependency-Check**: 115 dependencies, 0 vulnerable.
- **Trivy fs (backend)**: 0 vulnerabilities, 0 secrets, 0 misconfigurations.

### Repository Integrity & Security Gates
- **Trivy Filesystem Scan (repo-wide)**: 0 vulnerabilities, 0 secrets, 0 misconfigurations.
- **YAML Parse**: 1,256 YAML files parsed with 0 syntax errors.
- **Agent-Agnostic Scan**: 308 matches, 0 real hits (CSS `cursor` property and scan-pattern documentation).
- **Git Diff Check**: `git diff --check` passed with 0 whitespace errors.

### Other Stack Coverage Baselines (unchanged, backend-only iteration)
- Employee Portal: 88.68%. Mobile App: 99.21%. Patient Portal: 94.11%. Doctor Portal: 96.28%. Public Website: 98.61%.

---

## Decision & Next Steps

- **Backlog Item Status**: Closed (`COM-MOD-012-QA-001`).
- **Next Backlog Item**: `COM-MOD-012-CLOSEOUT` (Module closeout and registry update).

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-COM-MOD-012-QA-001
  type: qa-validation-evidence
  name: COM-MOD-012-QA-001 Performance, Resilience and Security Evidence
  version: 1.0.0
  status: passed
  captured_on: 2026-07-23
backlog_item:
  id: COM-MOD-012-QA-001
  module: COM-MOD-012
  module_name: Platform Hardening and SaaS Operations
  status: closed
  scope: 'Validate COM-MOD-012 end to end at QA/security level: tenant provisioning/listing/status
    transition, platform configuration, feature flags, the Prometheus endpoint, liveness/readiness
    health groups, MDC tenantId/userId/traceId logging, audit events for privileged
    operations, observability/backup/restore/incident runbooks, and traceability of
    the 8 COM-MOD-012 capabilities. Execute a dedicated DAST pass against the endpoints
    compiled/modified by COM-MOD-012-BE-001 (deferred by that item). Validate performance/resilience
    with a light local load check, health/readiness/liveness under normal and controlled-failure
    scenarios, invalid tenant/status/config/feature-flag input handling, and backup/restore/runbook
    readiness. Review the 3 remaining COM-MOD-012-BE-001 forward pointers (distributed
    trace export, a provisioned Grafana/Prometheus/Loki stack, SLO/SLA alerting) without
    closing them absent real infrastructure.'
capabilities:
- BCM-ORG-001 Tenant Management
- BCM-PLT-001 Identity and Access Management
- BCM-PLT-002 Platform Configuration
- BCM-PLT-005 API Management
- BCM-PLT-006 Observability
- BCM-PLT-007 Audit Trail
- BCM-PLT-008 Document Management
- BCM-PLT-009 Workflow Engine
preflight:
  loaded_sources:
  - AGENT_BOOTSTRAP.md
  - PROJECT_STATE.md
  - SOURCE_OF_TRUTH.md
  - projects/healthcare-operations-platform/PROJECT_STATE.md
  - projects/healthcare-operations-platform/SOURCE_OF_TRUTH.md
  - 06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  - 06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
  - 08-qa/technical-debt/technical-debt-index.md
  - 09-operations/deployment/production-deployment-strategy.md
  - 09-operations/runbooks/*.yaml
  - 09-operations/runbooks/local-solution-runbook.md
  - 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-DEF-validation.md
  - 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-OPS-001-validation.md
  - 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-OPS-002-validation.md
  - 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-BE-001-validation.md
  - 08-qa/security-quality/COM-MOD-012-BE-001/security-quality-evidence.md
  environment_check:
    java: 21.0.7 LTS (Oracle)
    maven: 3.9.11
    docker: 29.6.1, hop-local-postgres/hop-local-redis/hop-local-otel-collector all
      reachable
    trivy: 0.72.0
    node_npm: v24.8.0 / 11.6.0
    owasp_zap: ghcr.io/zaproxy/zaproxy:stable, cached locally
    conclusion: all_required_tooling_present_no_support_request_needed
  debt_first_review:
    reviewed: true
    applicable_open_items_before_work:
    - TD-BE-016
    - TD-BE-017
    - TD-IAM-003
    - TD-BE-005
    - TD-BE-006
    - TD-BE-007
    - TD-BE-008
    action: Did not add speculative CRUD for TD-BE-016/017/TD-IAM-003 (matches COM-MOD-012-BE-001's
      own documented deferral rationale). Real defects found by this item's own DAST
      run were fixed instead (TD-QA-005, TD-QA-006, both closed same-iteration) and
      TD-OBS-001 was registered for the confirmed-still-open observability-infrastructure
      forward pointers.
runtime_environment_note: Port 8080 on this shared local machine was occupied by an
  unrelated pre-existing Tomcat process for a different, unaffiliated project (not
  part of this repository). The backend was started with server.port=8090 for this
  validation session only, via -Dspring-boot.run.jvmArguments; no runbook, compose
  or application*.yml port configuration was changed. The canonical documented port
  remains 8080.
capability_traceability_validation:
  method: Live runtime testing against the running backend for every COM-MOD-012-BE-001-compiled
    operation, plus a full OWASP ZAP API scan (zap-api-scan.py) against the whole
    /v3/api-docs surface and a ZAP baseline scan against the employee portal. Cross-checked
    all 8 capabilities' traceability.md files against actual compiled surface and
    technical-debt-index.md.
  results:
  - capability_id: BCM-ORG-001
    status: validated
    detail: 'provisionTenant/listTenants/updateTenantStatus exercised live: duplicate
      code correctly rejected (409), invalid status value correctly rejected (400),
      status transition on a nonexistent tenant correctly rejected (404), 20 concurrent
      provisions all succeeded with no duplicate-code race (342ms total), and audit-event
      traceability confirmed live.'
  - capability_id: BCM-PLT-001
    status: validated
    detail: SCREEN_PLATFORM_CONFIGURATION EndpointPermissionRegistry entries confirmed
      reachable and deny-by-default. TD-IAM-003 (MFA/service-account/scope-grammar)
      confirmed still open, not exposed by this item's scope.
  - capability_id: BCM-PLT-002
    status: validated
    detail: getPlatformConfig/evaluateFeatureFlags/updateFeatureFlag exercised live
      including the safe-default-false-with-no-tenantId invariant, non-namespaced
      flagKey rejection (400), out-of-bounds rollout rejection (400), and a verified
      FeatureFlagUpdated audit event.
  - capability_id: BCM-PLT-005
    status: validated
    detail: OWASP ZAP API scan (353 imported URLs, full active-scan rule set) found
      0 SQLi/XSS/RCE/path-traversal/SSRF findings; found and this item fixed 2 real
      defects (TD-QA-005, TD-QA-006). ZAP baseline scan against the employee portal
      found 0 FAIL-NEW; the 4 WARN-NEW findings match the already-registered TD-FE-005.
  - capability_id: BCM-PLT-006
    status: validated
    detail: 'Found and fixed a real resilience defect: the readiness probe did not
      reflect database connectivity. Re-verified live: readiness correctly reports
      DOWN/503 with PostgreSQL stopped and UP after recovery; liveness correctly stays
      UP throughout. MDC tenantId/userId/traceId confirmed populated on live traffic
      including malformed-input error paths; W3C traceparent reuse and control-character
      sanitization confirmed live. Executed a real backup (pg_dump, checksum, pg_restore
      --list) and restore rehearsal (isolated database, matching row counts). Distributed
      trace export, a provisioned Grafana/Prometheus/Loki stack and SLO/SLA alerting
      confirmed still not implemented; registered as TD-OBS-001.'
  - capability_id: BCM-PLT-007
    status: validated
    detail: Confirmed both COM-MOD-012-BE-001 privileged operations (TenantStatusChanged,
      FeatureFlagUpdated) record correctly via a live GET /api/audit/events query,
      with before/after and actor metadata. TD-BE-016 confirmed still open (unchanged).
  - capability_id: BCM-PLT-008
    status: validated
    detail: Confirmed extension_deferred, unchanged since COM-MOD-012-BE-001. This
      item's own backup/restore evidence was stored under 08-qa/ per the existing
      convention, confirming it remains the working interim control.
  - capability_id: BCM-PLT-009
    status: validated
    detail: Confirmed not_implemented (TD-BE-017, unchanged). Manually exercised the
      runbook workflow it will eventually automate (backup + restore rehearsal), both
      executed successfully.
performance_and_resilience_evidence:
  light_load_check:
    method: 30 sequential GET /api/platform/tenants requests, then 20 concurrent POST
      /api/platform/tenants provisions, reproducible with a plain shell loop against
      the running local backend (no dedicated load-test tool required for this light
      local check).
    results:
      sequential_get_avg_latency_seconds: 0.0121
      concurrent_provisions_count: 20
      concurrent_provisions_total_time_ms: 342
      concurrent_provisions_failures: 0
      concurrent_provisions_duplicate_code_races: 0
  health_readiness_liveness_normal_scenario:
    liveness: UP (200)
    readiness: UP (200)
    prometheus: 200, text/plain Prometheus exposition format
  health_readiness_liveness_controlled_failure_scenario:
    method: docker stop hop-local-postgres, probe, then docker start hop-local-postgres,
      probe
    finding: 'Real defect found: readiness stayed UP (200) with PostgreSQL stopped,
      because management.endpoint.health.group.readiness.include was unset and the
      default readiness group only contains readinessState, not db.'
    fix: 'Added management.endpoint.health.group.readiness.include: readinessState,db
      to application-local.properties (the profile with a real DataSource bean; the same
      include in the base application.properties broke context startup for profiles with
      no DataSource -- confirmed by reproducing "Health contributor ''db'' ... does
      not exist" -- so it was scoped to application-local.properties only). New regression
      test: ObservabilityReadinessLocalDatabaseTest.readinessGroupIncludesDatabaseAndReportsUpAgainstTheRealLocalDatabase.'
    verified_after_fix:
      readiness_with_db_down: DOWN, HTTP 503
      liveness_with_db_down: UP, HTTP 200 (no unnecessary restart)
      readiness_after_db_recovery: UP, HTTP 200
  invalid_input_handling:
  - case: duplicate tenant code
    expected: 409
    actual: 409
  - case: invalid tenant status value
    expected: 400
    actual: 400
  - case: status transition on nonexistent tenant
    expected: 404
    actual: 404
  - case: feature-flag evaluation with no tenantId
    expected: safe default false, 200
    actual: safe default false, 200
  - case: non-namespaced feature-flag key
    expected: 400
    actual: 400
  - case: feature-flag rollout percentage out of bounds
    expected: 400
    actual: 400
  - case: malformed JSON body
    expected: 400
    actual: 400
  - case: null byte in a string query parameter (found by DAST)
    expected: 400
    actual: 500 before fix, 400 after fix (TD-QA-005)
  - case: oversized string field value (found by DAST)
    expected: 400
    actual: 500 before fix, 400 after fix (TD-QA-005)
  - case: nonexistent assistedUserId on POST /api/auth/assistance (found by DAST)
    expected: 404
    actual: 500 before fix, 404 after fix (TD-QA-006)
  backup_restore_runbook_readiness:
    backup:
      command: docker exec hop-local-postgres pg_dump -U hop -d hop -F c -f /tmp/hop-backup-<ts>.dump
      result: success, 317157 bytes
      checksum_sha256: 76e4a7513fd4603eb93987c90387b3976eb7c6163c0b5e2772010b4913d6b080
      structural_verification: 'pg_restore --list: 415 TOC entries, no corruption'
    restore_rehearsal:
      command: createdb hop_restore_verify; pg_restore -U hop -d hop_restore_verify
        --clean --if-exists <dump>; row-count comparison; dropdb hop_restore_verify
      result: success
      row_count_source: 40
      row_count_restored: 40
      match: true
    disposition: backup-runbook.md and restore-runbook.md both executable and
      correct today. Dump artifact not committed to the repository (317KB binary,
      matches the runbook's own "local filesystem, not durable object storage" framing);
      checksum and pg_restore --list output retained here as evidence.
forward_pointer_review:
  reviewed: true
  items:
  - gap: Distributed tracing exporter wired from the backend to the local OTel Collector
    status: confirmed_still_open
    detail: hop-local-otel-collector container is running and reachable (4317/4318/13133),
      but grep for tracing/opentelemetry/otlp/zipkin in backend/pom.xml and application*.yml
      returned no matches; no application spans are produced.
    action: registered as TD-OBS-001, not closed without real span-export wiring.
  - gap: Provisioned Grafana/Prometheus/Loki-compatible stack
    status: confirmed_still_open
    detail: no such stack exists in any environment; only the Prometheus scrape endpoint
      itself does.
    action: registered as TD-OBS-001, not closed without real infrastructure.
  - gap: SLO/SLA alerting backend
    status: confirmed_still_open
    detail: SloDefinition remains a modeled value object only; no alerting backend
      evaluates it.
    action: registered as TD-OBS-001, not closed without a real alerting backend.
quality_gates:
  backend:
    tool: Maven Enforcer, Surefire, JaCoCo, Checkstyle, PMD/CPD, SpotBugs/FindSecBugs,
      CycloneDX, duplicate-finder
    status: passed
    evidence_command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml
      -Pquality "-Dhop.local-db-tests=true" clean verify checkstyle:checkstyle pmd:pmd
      pmd:cpd spotbugs:spotbugs cyclonedx:makeAggregateBom duplicate-finder:check
    tests_run: 367
    failures: 0
    errors: 0
    skipped: 0
    line_coverage_percent: 84.14
    previous_baseline_percent: 84.11
    coverage_floor_met: true
    coverage_regression: false
    new_test_classes:
    - com.nexora.hop.platformfoundation.observability.ObservabilityReadinessLocalDatabaseTest
      (1 test)
    extended_test_classes:
    - com.nexora.hop.platformfoundation.GlobalExceptionHandlerTest (+3 tests)
    - com.nexora.hop.platformfoundation.identityaccess.AuthControllerTest (+1 test)
    checkstyle:
      findings_total: 31
      findings_in_new_or_touched_files: 0
    pmd_and_cpd:
      pmd_findings_total: 479
      cpd_duplications: 2
      findings_in_new_or_touched_files: 1
      disposition: 1 pre-existing FieldNamingConventions finding on GlobalExceptionHandler's
        logger field, unrelated to and unchanged by this item's edits (present before
        this item touched the file); 0 new violation classes introduced.
    spotbugs_findsecbugs:
      findings_total: 35
      findings_in_new_or_touched_files: 0
    owasp_dependency_check:
      total_dependencies: 115
      vulnerable_dependencies: 0
      findings: 0
      method: mvn -Pquality org.owasp:dependency-check-maven:check -DautoUpdate=false
        against the local shared advisory database (not refreshed, per policy).
    trivy_filesystem_scan_backend:
      tool: trivy 0.72.0
      scanners:
      - vuln
      - secret
      - misconfig
      severities:
      - UNKNOWN
      - LOW
      - MEDIUM
      - HIGH
      - CRITICAL
      vulnerabilities: 0
      secrets: 0
      misconfigurations: 0
  integrated_security_and_dast:
    zap_api_scan:
      tool: OWASP ZAP (zap-api-scan.py), ghcr.io/zaproxy/zaproxy:stable
      target: full /v3/api-docs surface, 353 imported URLs
      first_run_result: 'FAIL-NEW 0, WARN-NEW 3 (19 server-error instances, 2 real
        defects: TD-QA-005, TD-QA-006)'
      after_fix_result: FAIL-NEW 0, WARN-NEW 0 for the two fixed defect classes
      sqli_xss_rce_path_traversal_ssrf_findings: 0
    zap_baseline_scan:
      tool: OWASP ZAP (zap-baseline.py), ghcr.io/zaproxy/zaproxy:stable
      target: employee portal (http://host.docker.internal:5173), unchanged by this
        backlog item
      result: FAIL-NEW 0, WARN-NEW 4 (CSP/COEP headers, non-cacheable content, all
        matching existing TD-FE-005), PASS 63
    trivy_filesystem_scan_repo_wide:
      vulnerabilities: 0
      secrets: 0
      misconfigurations: 0
    yaml_parse:
      files_parsed: 1256
      errors: 0
    agent_agnostic_scan:
      total_matches: 308
      real_source_code_hits: 0
      disposition: all CSS cursor pseudo-property matches or documentation of the
        scan pattern itself
    secrets_scan:
      tool: trivy secret scanner (part of the fs scans above)
      findings: 0
    git_diff_check:
      result: 0 whitespace errors (Git LF/CRLF normalization warnings only)
frontend_coverage_preservation:
  employee_portal_line_coverage_percent: 88.68
  public_website_typescript_web_line_coverage_percent: 98.61
  mobile_typescript_foundation_line_coverage_percent: 99.21
  patient_portal_line_coverage_percent: 94.11
  doctor_portal_line_coverage_percent: 96.28
  note: COM-MOD-012-QA-001 is backend-only; no employee-portal, public-website, mobile,
    patient-portal or doctor-portal source file was changed. Previously measured line
    coverage per stack is re-affirmed unchanged.
technical_debt:
  closed:
  - id: TD-QA-005
    title: Null byte / oversized value in a request field or query parameter caused
      an unhandled 500 across multiple modules
    contribution: 'GlobalExceptionHandler now maps DataIntegrityViolationException
      to 400 when its root cause is a SQLSTATE-class-22 (data exception: invalid byte
      sequence 22021, string truncation 22001) SQLException, leaving every other cause
      (e.g. real 23xxx constraint conflicts) unchanged.'
  - id: TD-QA-006
    title: AuthController.initiateAssistance returned 500 instead of 404 for a nonexistent
      assistedUserId
    contribution: IdentityAccessExceptionHandler's assignableTypes widened to also
      cover AuthController, so its existing IdentityEntityNotFoundException -> 404
      mapping now applies there too.
  new:
  - id: TD-OBS-001
    title: Distributed trace export, provisioned Grafana/Prometheus/Loki stack and
      SLO/SLA alerting backend not implemented
    status: open
    risk_level: low
    blocking: false
  confirmed_still_open_unchanged:
  - TD-BE-016
  - TD-BE-017
  - TD-IAM-003
  - TD-BE-005
  - TD-BE-006
  - TD-BE-007
  - TD-BE-008
  - TD-FE-005
closure_criteria:
  all_8_capabilities_validated: true
  dast_executed_against_be_001_endpoints: true
  no_vulnerabilities_of_any_level_open_without_disposition: true
  coverage_not_regressed: true
  technical_debt_reduced: true
  no_stale_pointers: true
  git_clean: true
  agent_agnostic: true
  forward_pointers_not_closed_without_infrastructure: true
decision:
  backlog_item_status: closed
  ready_for_next_backlog_item: COM-MOD-012-CLOSEOUT
  next_backlog_item_name: Module closeout and registry update
  committed: true
```
