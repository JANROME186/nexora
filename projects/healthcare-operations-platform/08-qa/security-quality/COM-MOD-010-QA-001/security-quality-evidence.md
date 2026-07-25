# COM-MOD-010-QA-001 Security Quality Evidence

Status: passed.

Integrated security and quality closure for all 13 COM-MOD-010 capability packages:

- `mvn -Pquality "-Dhop.local-db-tests=true" clean verify`: 315 tests, 0 failures/errors/skipped,
  JaCoCo line coverage 83.73% (floor 82.94%).
- `mvn -Pquality checkstyle:checkstyle pmd:pmd pmd:cpd spotbugs:spotbugs`: passed, 0 findings in
  the `inventoryquality` module.
- `mvn -Pquality org.owasp:dependency-check-maven:check`: passed, 65 dependencies, 0
  vulnerabilities, using the local advisory database dated 2026-07-20 13:56:20 -06:00 (not
  refreshed).
- `npm run quality` (employee portal): passed, 124 tests/48 files, line coverage 88.24% (floor
  87.87%).
- `npm audit --audit-level=low`: passed, 0 vulnerabilities.
- `trivy fs --scanners vuln,secret,misconfig` (all severities): passed, 0 findings.
- Secrets pattern scan: no hardcoded secrets (via Trivy `secret` scanner).
- Agent-agnostic scan: no named-agent dependency.
- YAML parse: 1105 files, 0 errors. `git diff --check`: clean.

## Coverage-gap finding and fix

A clean rebuild reproducibly measured backend coverage at 81.90% (below the 82.94% floor) with 0
backend source changes since `COM-MOD-010-BE-002`. Root cause: `COM-MOD-010-BE-002` added 4 new
JDBC repository adapters (internal quality control, calibration, equipment availability,
maintenance) without a PostgreSQL-backed integration test. Added
`InventoryQualityControlsLocalDatabaseTest.java` (3 tests) to close this real gap; corrected
coverage is 83.73%, confirmed reproducible across two clean runs.

Dependency-Check used the local advisory database available on 2026-07-20 at 13:56:20 -06:00.
Daily refresh of that database remains an operator/manual responsibility; this agent did not
trigger a refresh.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: COM-MOD-010-QA-001-SECURITY-QUALITY
type: security-quality-evidence
backlog_item: COM-MOD-010-QA-001
module: COM-MOD-010
status: passed
date: 2026-07-20
executor: agent
summary: Integrated security and quality closure evidence for all 13 COM-MOD-010 capability
  packages (BCM-INV-001..009, BCM-QLT-001/003/004/005). Backend and employee-portal
  quality gates re-run clean, OWASP Dependency-Check and Trivy report 0 vulnerabilities/secrets/misconfigurations,
  and a real backend coverage gap found during validation (81.90% vs. the 82.94% floor,
  caused by COM-MOD-010-BE-002 shipping 4 new JDBC adapters with no local-database
  test) was fixed with a new integration test, restoring coverage to 83.73%. No new
  npm or Maven dependency was introduced.
open_source_first_check:
  new_dependency_added: false
  stack_reviewed: Spring Boot 4.1 / Spring Modulith, PostgreSQL JDBC, React 18, TypeScript
    5, Vite 6, Vitest, ESLint, jscpd, Prettier, OWASP Dependency-Check, Trivy, npm
    audit
  vulnerabilities_found: 0
  license_check: passed
  notes: No runtime or development dependency change was required by this validation
    item.
checks:
- tool: Maven Enforcer + Surefire (backend, local-db tests enabled)
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  tests_run: 315
  failures: 0
  errors: 0
  skipped: 0
- tool: JaCoCo backend line coverage
  status: passed
  line_coverage_percent: 83.73
  previous_baseline_percent: 82.94
  coverage_regression: false
  correction_note: A clean rebuild first measured 81.90% with 0 backend source changes
    since BE-002 (reproduced twice), traced to 4 uncovered BE-002 JDBC adapters; fixed
    with a new local-database integration test (InventoryQualityControlsLocalDatabaseTest),
    not by inflating the measurement.
- tool: Checkstyle
  status: passed
  evidence_command: mvn -Pquality checkstyle:checkstyle
  findings_in_scope_module: 0
  findings_total: 31
  note: All 31 findings are pre-existing, in the unrelated resultsanddigitaldelivery
    module.
- tool: PMD + CPD
  status: passed
  evidence_command: mvn -Pquality pmd:pmd pmd:cpd
  findings: 0
- tool: SpotBugs + FindSecBugs
  status: passed
  evidence_command: mvn -Pquality spotbugs:spotbugs
  findings_in_scope_module: 0
  findings_total: 1
  note: Pre-existing finding unrelated to inventoryquality.
- tool: CycloneDX SBOM
  status: passed
  evidence_command: mvn -Pquality cyclonedx:makeAggregateBom
  components: 103
- tool: duplicate-finder
  status: passed
  evidence_command: mvn -Pquality duplicate-finder:check
- tool: OWASP Dependency-Check (Java + Maven)
  status: passed
  evidence_command: mvn -Pquality org.owasp:dependency-check-maven:check
  dependencies_scanned: 65
  vulnerabilities_found: 0
  local_advisory_database:
    data_directory: C:/Documents/Proyectos/Laboratorio/dependency-check-data
    auto_update_during_agent_execution: false
    nvd_api_last_checked: '2026-07-20T13:56:20-06:00'
- tool: TypeScript
  status: passed
  evidence_command: npm run typecheck
- tool: ESLint + security + sonarjs
  status: passed_with_non_blocking_warnings_registered
  evidence_command: npm run lint
  errors: 0
  warnings: 32
  debt: pre-existing screen-size/duplicate-string warnings only (tracked by TD-FE-010);
    no file touched by this backlog item.
- tool: Vitest + V8 coverage
  status: passed
  tests_run: 124
  test_files: 48
  failures: 0
  line_coverage_percent: 88.24
  previous_baseline_percent: 87.87
- tool: Vite build
  status: passed
  evidence_command: npm run build
- tool: jscpd
  status: passed
  evidence_command: npm run duplication
  clones_found: 0
- tool: Prettier
  status: passed
  evidence_command: npm run format:check
- tool: license-checker-rseidelsohn
  status: passed
  evidence_command: npm run license:check
  result: MIT 5, UNLICENSED 1 (unchanged)
- tool: npm audit
  status: passed
  evidence_command: npm audit --audit-level=low
  vulnerabilities_found: 0
- tool: Trivy fs (vuln, secret, misconfig)
  status: passed
  evidence_command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
    --exit-code 0 --no-progress .
  severities_scanned: UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
  vulnerabilities_found: 0
  secrets_found: 0
  misconfigurations_found: 0
- tool: Secrets scan
  status: passed
  note: Covered by the Trivy `secret` scanner above.
- tool: Agent-agnostic scan
  status: passed
  result: no agent/vendor references found
- tool: YAML parse
  status: passed
  files_parsed: 1105
- tool: git diff --check
  status: passed
  notes: no whitespace errors
secure_code_review:
  authorization_boundary: All 27 REST operations across the 13 capability packages
    remain behind the existing HopAuthorizationInterceptor / EndpointPermissionRegistry
    coarse-grained boundary; re-verified with a new end-to-end integration test for
    the 4 previously-untested BCM-QLT JDBC adapters.
  tenant_isolation: Scope-mismatch guards (STOCK_EXIT_SCOPE_MISMATCH, PROCUREMENT_SCOPE_MISMATCH,
    CONSUMPTION_SCOPE_MISMATCH, ADJUSTMENT_SCOPE_MISMATCH, WASTE_SCOPE_MISMATCH, QC_SCOPE_MISMATCH)
    re-verified against a real PostgreSQL database.
  dast: No new externally-reachable browser or unauthenticated API surface was introduced
    beyond the HOP-QA-ALIGN-004 ZAP baseline; consistent with the COM-MOD-009-QA-001
    precedent, DAST is recorded as passed_in_prior_zap_scans rather than re-run for
    this validation-only item.
decision:
  security_quality_status: passed
  closed_debt: []
  reduced_debt:
  - TD-BE-003
  created_debt: []
  ready_for_next_backlog_item: COM-MOD-010-CLOSEOUT
```
