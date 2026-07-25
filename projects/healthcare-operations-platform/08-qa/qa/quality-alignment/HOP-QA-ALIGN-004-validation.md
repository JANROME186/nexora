# HOP-QA-ALIGN-004 Validation

Human-readable companion for `HOP-QA-ALIGN-004-validation.md`.

All-severity dependency, filesystem, secret, misconfiguration and DAST checks were executed
against the fully running local stack and passed. **HOP-QA-ALIGN-004 is closed.**

## Result summary

| Gate | Result | Notes |
| --- | --- | --- |
| Backend dependency scan (OWASP Dependency-Check, all severities) | passed | 0 vulnerabilities, after fixing 2 CVEs (see below) |
| Frontend dependency audit (`npm audit --audit-level=low`) | passed | 0 vulnerabilities, 603 dependencies |
| Trivy filesystem/secret/misconfig scan | passed | 0 findings; Trivy upgraded 0.69.2 → 0.72.0 (closes TD-QA-002) |
| OWASP ZAP baseline scan (employee portal) | passed | 0 FAIL, 4 WARN (2 accepted-risk debt, 2 informational) |
| OWASP ZAP API scan (backend) | passed | 0 FAIL, 1 WARN (accepted-risk debt) |
| Backend tests | passed | 77 tests, 0 failures |
| Frontend tests | passed | 18 tests, 0 failures |
| Mobile tests | passed | 8 tests, 0 failures |
| Backend line coverage | passed | 66.43% (baseline 65.82%, no regression) |
| Frontend line coverage | passed | 72.89% (baseline 72.89%, no regression) |

## What DAST actually required fixing

Getting the OWASP ZAP scans running at all, and then acting on what they found, was most of this
backlog item's work:

1. **The backend had no OpenAPI document to scan.** Added `springdoc-openapi-starter-webmvc-api`
   (not the `-ui` variant, to avoid bundling swagger-ui's vulnerable DOMPurify) so
   `GET /v3/api-docs` exists.
2. **That addition introduced 2 real CVEs**, caught by the very next dependency-check run:
   `jackson-databind` (pulled by swagger-core independently of Spring Boot 4's Jackson 3.x
   management) and DOMPurify (would have shipped with `-ui`). Fixed by choosing the `-api`
   artifact and pinning the classic Jackson 2.x line via `dependencyManagement`.
3. **Docker Desktop's `--network host` does not reach the Windows host.** Switched to
   `--add-host=host.docker.internal:host-gateway` and targeted
   `http://host.docker.internal:<port>`.
4. **Vite's dev server rejected that Host header (403).** Added `allowedHosts` to
   `vite.config.ts`.
5. **ZAP's baseline scan then found 5 real missing security headers** across both surfaces
   (`X-Frame-Options`, `X-Content-Type-Options`, `Referrer-Policy`, `Permissions-Policy`,
   `Cross-Origin-Opener-Policy`, `Cross-Origin-Resource-Policy`). All fixed: a Vite dev-server
   middleware for the employee portal, a new `SecurityHeadersFilter` for the backend.
6. **ZAP's API scan found two genuine unhandled-500 defects**, both fixed:
   - `POST /api/people/persons/index/rebuild` never checked that `tenantId` was a real tenant
     before writing it into `audit_events.tenant_id` (`varchar(36)`); a long fuzzed value caused
     an unhandled `DataIntegrityViolationException`. Now validates tenant existence first and
     returns a clean 404.
   - `POST /api/platform/tenants` (and laboratories/branches, which share the same code path) had
     no max-length check on `name` against the `varchar(180)` column. Now validates length and
     returns a clean 400.

## Residual findings, formally dispositioned

- **CSP and Cross-Origin-Embedder-Policy** on the employee portal (Medium and Low risk): a
  production-strength policy would break Vite's eval-based HMR; a permissive dev-only policy would
  just fake the scanner. Deferred to the production hosting layer, which doesn't exist yet.
  Tracked as **TD-FE-005**, owner frontend/platform team, must close before first production
  deployment.
- **One malformed empty-key query parameter causing a 500** on `POST /api/platform/tenants` (Low
  risk, high confidence, no information disclosure): a Tomcat parameter-parsing edge case distinct
  from the two defects above, which are fully fixed. Tracked as **TD-QA-004**, owner backend
  platform team, next backend web-infrastructure touch.
- Two employee-portal findings (`Modern Web Application`, `Storable but Non-Cacheable Content`)
  are informational only — no risk, no action required.
- Mobile coverage baseline establishment remains blocked by how `mobile-app` reuses
  `employee-portal`'s installed toolchain (sibling `node_modules` isn't part of Node's module
  resolution chain for the coverage provider). This is pre-existing **TD-APP-002**, not part of
  this backlog item's required scope.

## Technical debt disposition

- **Closed:** TD-QA-001 (DAST now runs), TD-QA-002 (Trivy upgraded).
- **New:** TD-FE-005 (CSP/COEP), TD-QA-004 (malformed-parameter 500).
- **Unchanged:** TD-APP-002 (mobile coverage baseline, out of this item's scope).

## Readiness decision

HOP-QA-ALIGN-004 is **closed**. The next backlog item is HOP-QA-ALIGN-005 (message
externalization and magic-string remediation baseline). MVP-MOD-004-FE-001 remains paused until
HOP-QA-ALIGN-CLOSEOUT.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-ALIGN-004-VALIDATION
  type: qa-validation-evidence
  name: All-Severity Vulnerability and Runtime Security Evidence
  version: 2.0.0
  status: passed
  created_date: 2026-07-15
  updated_date: 2026-07-16
  human_readable: HOP-QA-ALIGN-004-validation.md
  machine_readable: HOP-QA-ALIGN-004-validation.md
backlog_item:
  id: HOP-QA-ALIGN-004
  name: Establish all-severity vulnerability, DAST and runtime security evidence
  result: completed
  functional_development_blocker_removed_for_this_item: true
scope:
  components:
  - 07-implementation/backend
  - 07-implementation/employee-portal
  - 07-implementation/mobile-app
  - 07-implementation
  debt_items_closed:
  - TD-QA-001
  - TD-QA-002
  debt_items_newly_registered:
  - TD-FE-005
  - TD-QA-004
  debt_items_materially_reduced_previously:
  - TD-QA-003
environment:
  local_stack_started_per_runbook: true
  infrastructure: docker compose --env-file .env -f compose.local.json up -d (postgres,
    redis, otel-collector; all healthy)
  backend: mvn --settings .mvn/settings.xml spring-boot:run "-Dspring-boot.run.profiles=local"
    (http://localhost:8080)
  employee_portal: npm run dev -- --host 127.0.0.1 (http://localhost:5173)
  docker_desktop_networking_note: OWASP ZAP runs in a Linux container under Docker
    Desktop's WSL2 backend, where --network host does not reach the Windows host's
    loopback interface. The ZAP containers were run with --add-host=host.docker.internal:host-gateway
    and targeted the services at http://host.docker.internal:<port> instead.
passed_checks:
- backend_dependency_check_all_severities
- frontend_npm_audit_all_severities
- integrated_trivy_all_severity_filesystem_vulnerability_scan
- integrated_trivy_secret_scan
- integrated_trivy_misconfiguration_scan
- owasp_zap_baseline_scan_employee_portal_zero_fail
- owasp_zap_api_scan_backend_zero_fail
- backend_test_suite_all_77_tests
- frontend_test_suite_all_18_tests
- mobile_test_suite_all_8_tests
- backend_line_coverage_at_or_above_previous_baseline
- frontend_line_coverage_at_or_above_previous_baseline
validation_commands:
- id: backend_dependency_check_all_severities
  working_directory: 07-implementation/backend
  command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Pquality
    org.owasp:dependency-check-maven:check
  result: passed
  vulnerabilities: 0
  remediation_applied: 'Two CVEs were found on the first run, both introduced by adding
    springdoc-openapi so /v3/api-docs could be scanned by OWASP ZAP (see owasp_zap_api_scan_backend
    below): CVE-2026-54515 in jackson-databind 2.21.4 (pulled by io.swagger.core.v3:swagger-core-jakarta,
    a springdoc transitive dependency, independently of Spring Boot''s managed Jackson
    3.x jackson-bom) and six DOMPurify CVEs (CVE-2026-41238/41239/41240/49458/49459/49978)
    bundled inside springdoc-openapi-starter-webmvc-ui''s swagger-ui static assets.
    Fixed by (1) switching to springdoc-openapi-starter-webmvc-api, which exposes
    only the /v3/api-docs JSON endpoint without bundling swagger-ui/DOMPurify at all,
    and (2) pinning jackson-databind/jackson-core to 2.22.1 and jackson-annotations
    to 2.22 via dependencyManagement, which resolves swagger-core''s classic-Jackson-2.x
    dependency to patched versions. Re-run after the fix: 0 vulnerabilities.'
- id: frontend_dependency_audit_all_severities
  working_directory: 07-implementation/employee-portal
  command: npm audit --audit-level=low
  result: passed
  vulnerabilities: 0
  dependencies_scanned: 603
- id: trivy_all_severities
  working_directory: 07-implementation
  command: trivy fs --scanners vuln,secret,misconfig --exit-code 1 --no-progress --skip-dirs
    "backend/.m2,backend/target,employee-portal/node_modules,employee-portal/dist,mobile-app/node_modules"
    .
  tool_version: 0.72.0
  result: passed
  vulnerabilities: 0
  secrets: 0
  misconfigurations: 0
  remediation_applied: Upgraded Trivy from 0.69.2 to 0.72.0 via chocolatey before
    this run (closes TD-QA-002).
- id: owasp_zap_baseline_scan_employee_portal
  working_directory: 07-implementation
  command: docker run --rm --add-host=host.docker.internal:host-gateway -v "<repo>/08-qa/security-quality/HOP-QA-ALIGN-004:/zap/wrk"
    ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t http://host.docker.internal:5173
    -r zap-employee-portal.html -J zap-employee-portal.json -m 2
  tool_version: OWASP ZAP stable (ghcr.io/zaproxy/zaproxy:stable, pulled 2026-07-16)
  result: passed
  fail_new: 0
  warn_new: 4
  pass: 63
  report_html: zap-employee-portal.html
  report_json: zap-employee-portal.json
  remediation_applied: 'First run failed to reach the target at all: --network host
    does not bridge to the Windows host under Docker Desktop/WSL2, so host.docker.internal
    + --add-host=host.docker.internal:host-gateway was used instead; that then hit
    HTTP 403 because Vite''s dev-server Host-header protection does not allow arbitrary
    Host values, fixed by adding allowedHosts to vite.config.ts. Once reachable, added
    a small Vite dev-server middleware (securityHeadersPlugin in vite.config.ts) that
    sets X-Frame-Options, X-Content-Type-Options, Referrer-Policy, Permissions-Policy,
    Cross-Origin-Opener-Policy and Cross-Origin-Resource-Policy; all six now PASS.'
  residual_warn_findings:
  - rule: 10038 Content Security Policy (CSP) Header Not Set
    risk: medium
    disposition: accepted_risk_technical_debt
    tracked_by: TD-FE-005
  - rule: 90004 Cross-Origin-Embedder-Policy Header Missing or Invalid
    risk: low
    disposition: accepted_risk_technical_debt
    tracked_by: TD-FE-005
  - rule: 10109 Modern Web Application
    risk: informational
    disposition: informational_no_action_required
  - rule: 10049 Storable but Non-Cacheable Content
    risk: informational
    disposition: informational_no_action_required
- id: owasp_zap_api_scan_backend
  working_directory: 07-implementation
  command: docker run --rm --add-host=host.docker.internal:host-gateway -v "<repo>/08-qa/security-quality/HOP-QA-ALIGN-004:/zap/wrk"
    ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py -t http://host.docker.internal:8080/v3/api-docs
    -f openapi -r zap-backend-api.html -J zap-backend-api.json
  tool_version: OWASP ZAP stable (ghcr.io/zaproxy/zaproxy:stable, pulled 2026-07-16)
  result: passed
  fail_new: 0
  warn_new: 1
  pass: 118
  urls_imported_from_openapi: 157
  report_html: zap-backend-api.html
  report_json: zap-backend-api.json
  remediation_applied: 'The backend did not expose an OpenAPI document at all before
    this backlog item (no springdoc-openapi dependency existed), so QA-010 could not
    target anything; adding springdoc-openapi-starter-webmvc-api made GET /v3/api-docs
    return 200. The scan then found two distinct unhandled-500 defects, both fixed
    directly: (1) POST /api/people/persons/index/rebuild accepted any caller-supplied
    tenantId without checking it against an existing tenant, and reused it as the
    audit_events.tenant_id column value (varchar(36)); a long fuzzed tenantId caused
    an unhandled DataIntegrityViolationException. Fixed in PersonManagementService.rebuildIndex
    by validating TenantDirectory.tenantExists(tenant) first, now returning a clean
    404. (2) POST /api/platform/tenants (and createLaboratory/createBranch, sharing
    the same code path) had no max-length validation on the name field, which maps
    to organization.tenants/laboratories/ branches.name (varchar(180)); a long fuzzed
    name caused the same class of unhandled DataIntegrityViolationException. Fixed
    in OrganizationManagementService by adding requiredNameText(value, message), enforcing
    the 180-character bound and returning a clean 400. Both fixes also close the associated
    X-Content-Type-Options-missing and Cross-Origin-Resource-Policy-missing findings
    platform-wide via a new SecurityHeadersFilter (com.nexora.hop.platformfoundation.SecurityHeadersFilter)
    applied to every backend response.'
  residual_warn_findings:
  - rule: 100000 A Server Error response code was returned by the server
    endpoint: POST /api/platform/tenants
    risk: low
    confidence: high
    disposition: accepted_risk_technical_debt
    tracked_by: TD-QA-004
    note: Triggered only by a deliberately malformed empty-key query/form parameter
      (Tomcat's org.apache.tomcat.util.http.InvalidParameterException); response body
      leaks no stack trace or internal detail. Distinct from the two DataIntegrityViolationException
      defects above, which are fully fixed.
- id: backend_test_suite
  working_directory: 07-implementation/backend
  command: mvn --settings .mvn/settings.xml test
  result: passed
  detail: 77 tests run, 0 failures, 0 errors, 7 skipped (local-db-only tests skipped
    without a running Postgres).
- id: backend_test_suite_against_postgres
  working_directory: 07-implementation/backend
  command: mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
  result: passed
  detail: 77 tests run, 0 failures, 0 errors, 0 skipped against local PostgreSQL 16.
- id: backend_coverage
  working_directory: 07-implementation/backend
  command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Pquality
    test jacoco:report
  result: passed
  line_coverage_percent: 66.43
  previous_baseline_percent: 65.82
  regression: false
- id: frontend_test_suite_and_coverage
  working_directory: 07-implementation/employee-portal
  command: npm run test -- --coverage --run
  result: passed
  detail: 10 test files, 18 tests, 0 failures.
  line_coverage_percent: 72.89
  previous_baseline_percent: 72.89
  regression: false
- id: mobile_test_suite
  working_directory: 07-implementation/mobile-app
  command: npm test
  result: passed
  detail: 5 test files, 8 tests, 0 failures.
- id: mobile_coverage_baseline
  result: blocked_registered_as_debt
  detail: mobile-app has no coverage dependency of its own; its package.json scripts
    reuse employee-portal's installed vitest binaries via a relative path (..\employee-portal\node_modules\.bin\vitest.cmd),
    which works for running tests (Node resolves the invoked binary's own file directly)
    but not for the @vitest/coverage-v8 provider, which vite-node resolves as a bare
    module specifier relative to mobile-app's own (non-existent) node_modules tree;
    sibling-directory node_modules are not part of Node's module resolution chain.
    Establishing a real baseline requires giving mobile-app its own installed toolchain
    (or an equivalent workspace/monorepo resolution setup), which is already tracked
    as TD-APP-002 and outside HOP-QA-ALIGN-004's required_tool_categories (which do
    not list a mobile scan). Not attempted further to avoid an ad hoc filesystem-level
    workaround (e.g., a node_modules directory junction) that would not be reproducible
    for another developer without undocumented manual steps.
  tracked_by: TD-APP-002
new_shared_infrastructure:
- component: SecurityHeadersFilter
  location: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/SecurityHeadersFilter.java
  description: 'OncePerRequestFilter setting X-Content-Type-Options: nosniff and Cross-Origin-Resource-Policy:
    same-origin on every backend response. Placed at the application root package
    (sibling to PlatformFoundationApplication) because it is cross-cutting platform
    infrastructure, not owned by any single business module.'
- component: springdoc-openapi-starter-webmvc-api
  location: 07-implementation/backend/pom.xml
  version: 3.0.3
  description: 'New dependency exposing GET /v3/api-docs so the backend has a live,
    accurate OpenAPI document reflecting the actual Spring MVC routes, required as
    the ZAP API scan target (QA-010). Deliberately the -api artifact (not -ui): it
    does not bundle swagger-ui, avoiding the DOMPurify CVEs that artifact would otherwise
    introduce.'
- component: jackson-classic dependency pins
  location: 07-implementation/backend/pom.xml
  description: dependencyManagement entries pinning com.fasterxml.jackson.core:jackson-databind/jackson-core
    to 2.22.1 and jackson-annotations to 2.22, resolving the classic-Jackson-2.x line
    that io.swagger.core.v3:swagger-core-jakarta (a springdoc transitive dependency)
    depends on independently of Spring Boot 4's managed Jackson 3.x jackson-bom.
- component: employee-portal security headers dev middleware
  location: 07-implementation/employee-portal/vite.config.ts
  description: securityHeadersPlugin sets X-Frame-Options, X-Content-Type-Options,
    Referrer-Policy, Permissions-Policy, Cross-Origin-Opener-Policy and Cross-Origin-Resource-Policy
    on every dev server response. allowedHosts was also extended to include host.docker.internal
    so a containerized DAST scanner can reach the dev server from inside Docker Desktop's
    Linux VM.
bounded_context_and_aggregate_ownership: No aggregate ownership changed. PersonManagementService
  gained a TenantDirectory read dependency (existing cross-module read-port pattern,
  mirroring PatientManagementService and DoctorManagementService, which already inject
  TenantDirectory the same way) rather than a new write dependency. OrganizationManagementService's
  validation change is entirely internal to that module. No OpenAPI contract for any
  HOP capability package changed; springdoc-openapi renders the contract that already
  exists in the compiled Spring MVC routes, it does not define a new one.
model_gaps_identified: []
out_of_scope_confirmed:
- Mobile coverage baseline establishment (TD-APP-002, pre-existing, not in this backlog's
  required_tool_categories).
- Production hosting-layer CSP/COEP hardening for the employee portal (TD-FE-005,
  requires a production deployment target that does not exist yet).
- Deeper Tomcat/servlet-container parameter-parsing hardening for malformed empty-key
  parameters (TD-QA-004, Low risk, no information disclosure).
- HOP-QA-ALIGN-005 (message externalization and magic-string baseline) and HOP-QA-ALIGN-CLOSEOUT
  remain separate, subsequent backlog items.
validations:
- id: VAL-001
  name: YAML repository files remain parseable
  method: Full-project YAML parse (framework and project trees) including this evidence
    file and every touched technical-debt YAML.
  result: passed
- id: VAL-002
  name: Agent-agnostic scan
  method: Directory scan for .claude/.cursor/.copilot/.windsurf/.aider* files or folders
    (tracked and untracked), plus a content grep of every file touched by this backlog
    item for claude|anthropic|copilot|cursor|chatgpt|openai|gemini|codex|windsurf|aider
    (case-insensitive).
  result: passed
  detail: 0 forbidden files/folders found; 0 content matches in any file touched by
    this backlog item. The pattern-list words above are written out only in this evidence
    file and its companion security-quality-evidence.md as documentation of what
    the scan searched for.
- id: VAL-003
  name: Stale pointer scan
  method: Verified every relative file-path reference newly added in TD-FE-005, TD-QA-004,
    technical-debt-index.md and this evidence file resolves to an existing repository
    path.
  result: passed
- id: VAL-004
  name: No prohibited execution-limitation statuses
  method: Grepped this evidence file, its security-quality companion, PROJECT_STATE.md,
    SOURCE_OF_TRUTH.md and the runbook for passed_with_execution_limitation, closed_with_execution_limitation
    and not_executed on any mandatory gate.
  result: passed
  detail: 0 matches. Every mandatory gate (dependency scans, Trivy, both ZAP scans,
    backend and frontend test/coverage suites) executed to completion.
- id: VAL-005
  name: git diff --check
  method: git diff --check across every file touched by this backlog item.
  result: passed
blocking_gaps: []
readiness:
  hop_qa_align_004_status: closed
  ready_for_next_backlog_item: HOP-QA-ALIGN-005
  next_backlog_item_name: Establish message externalization and magic-string remediation
    baseline
  rationale: 'Every required_tool_category for HOP-QA-ALIGN-004 executed successfully
    with all-severity scope: backend and frontend dependency scans, Trivy filesystem/secret/misconfig
    scan, and both the OWASP ZAP baseline scan (employee portal) and API scan (backend,
    now possible because springdoc-openapi exposes a live OpenAPI document). Every
    finding was either fixed (six dependency CVEs, two unhandled-500 defects, five
    missing security headers across both surfaces) or given a formal accepted-risk
    technical-debt disposition with owner, target backlog and expiration (TD-FE-005,
    TD-QA-004). TD-QA-001 and TD-QA-002, the two debt-first candidates for this backlog
    item, are both closed. Backend and frontend line coverage were re-measured and
    neither dropped below its previous baseline (backend 66.43% >= 65.82%; frontend
    72.89% >= 72.89%). No mandatory gate was left not_executed or closed with an execution
    limitation.'
```
