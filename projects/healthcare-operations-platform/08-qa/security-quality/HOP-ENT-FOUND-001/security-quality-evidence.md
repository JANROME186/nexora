# HOP-ENT-FOUND-001 Security and Quality Evidence

Machine-readable source: `security-quality-evidence.md`.

## Open source first

Only open source dependencies were introduced: Spring Security for backend request-time
authorization and local mobile quality dev tooling. `react-i18next`/FormatJS and
JPA/Hibernate/OpenAPI Generator/MapStruct/Lombok were each evaluated and deliberately deferred with
stated reasons — see the localization and persistence review documents under `03-architecture/`.

## Debt-first action

Closed `TD-BE-009`, `TD-IAM-001` and `TD-APP-002`; materially reduced `TD-I18N-002` and
`TD-IAM-002`; updated `TD-BE-003`'s baseline; kept residual gaps explicit rather than undocumented.

## Results

| Stack | Tests | Coverage | Result |
|---|---|---|---|
| Backend | 191, 0 failures/errors | 76.99% → **77.92%** | BUILD SUCCESS |
| Employee portal | 87, 0 failures | 83.98% → **84.44%** | all 7 gates passed |
| Mobile app | 17, 0 failures | **97.15%** | all configured gates passed |

Trivy: 0 vulnerabilities, no secret findings reported, no misconfiguration targets detected.
`npm audit`: 0 vulnerabilities. 878 repository
YAML files parse. Agent-agnostic scan: 1 confirmed false positive (`cursor: pointer`).

## Secure coding

The one genuine hardcoded-actor finding (a `"system"` literal standing in for a real
user-initiated action) was **fixed in code**. Corrective closure also added Spring Security,
request-time authentication/authorization through `HopAuthorizationInterceptor`, API/action
permission mapping and 401/403 tests. `TD-IAM-001` is closed for the local-development baseline;
production OIDC/IdP hardening remains productization work.

## DAST

No new DAST scan was required for this corrective closure because no new HTTP endpoint was added.
Existing OWASP ZAP baseline from `HOP-QA-ALIGN-004` remains the current DAST evidence; request-time
authorization behavior is covered by focused backend tests.

## Decision

**Passed.** No blocking findings, no accepted risks required.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-ENT-FOUND-001-SEC
  type: security-quality-evidence
  name: HOP-ENT-FOUND-001 enterprise foundation alignment security and quality evidence
  version: 1.0.0
  status: passed
  created_date: 2026-07-17
  owner: Nexora Engineering
scope:
  backlog_item: HOP-ENT-FOUND-001
  module: HOP-ENTERPRISE-FOUNDATION-ALIGNMENT
  changed_stack: backend_java_maven_and_frontend_typescript_web_and_mobile_typescript
  implementation_paths:
  - 07-implementation/backend
  - 07-implementation/employee-portal
  - 07-implementation/mobile-app
  result: closed
open_source_first_assessment:
  new_dependencies_introduced:
  - backend: spring-boot-starter-security (open source, Apache-2.0)
  - mobile-app: local devDependencies for Vitest/coverage, ESLint, TypeScript, Prettier
      and jscpd
  frontend_i18n_library_evaluation: 'react-i18next and FormatJS (both MIT-licensed,
    actively maintained open source) were evaluated for the locale-catalog mechanism
    and deliberately not adopted this iteration in favor of a dependency-free typed
    TypeScript catalog, to avoid bundle-size/migration surface during a foundation-alignment
    slice that must not touch the 27 existing screens'' business logic. See 03-architecture/i18n-localization/localization-strategy.md.

    '
  backend_tooling_evaluation: 'Spring''s own MessageSource/ResourceBundle (part of
    spring-boot-starter-web, already a dependency) was used for i18n rather than adding
    a new library. JPA/Hibernate, OpenAPI Generator, MapStruct and Lombok were each
    evaluated for the persistence/contract-generation review and deliberately deferred
    with stated reasons; see 03-architecture/technology-architecture/persistence-and-contract-generation-review.md.

    '
  proprietary_dependency_introduced: false
client_stack_market_validation: not_applicable_no_new_stack_element_introduced
stack_toolchain_baseline: 03-architecture/technology-architecture/stack-quality-toolchain-baseline.md
technical_debt_first_action:
  reviewed: 08-qa/technical-debt/technical-debt-index.md
  selected:
  - TD-BE-009 (closed)
  - TD-I18N-002 (materially reduced)
  - TD-IAM-001 (closed)
  - TD-IAM-002 (materially reduced)
  - TD-APP-002 (closed)
  residual_debt_registered_or_left_open: TD-DB-001..004, TD-UX-001..003, TD-STACK-002..004
    and other pre-existing non-blocking debt tracked in 08-qa/technical-debt/technical-debt-index.md
tools_run:
  backend:
  - Maven Surefire (unit + Spring Boot integration tests, including local-database-backed
    tests)
  - JaCoCo (coverage)
  frontend_employee_portal:
  - TypeScript strict-mode typecheck (tsc)
  - ESLint with typescript-eslint (lint)
  - Vitest + V8 coverage provider (test:coverage)
  - Vite production build
  - jscpd (duplication)
  - Prettier (format:check)
  - license-checker-rseidelsohn (license:check)
  - npm audit
  mobile_app:
  - TypeScript strict-mode typecheck
  - ESLint
  - Vitest + V8 coverage provider
  - jscpd
  - Prettier
  - npm audit
  repository_wide:
  - Trivy filesystem scan (vuln, secret, misconfig; all severities)
  - Full-repository YAML parse (pyyaml, 878 files)
  - Agent-agnostic text scan
commands_or_equivalent_steps:
- mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Pquality "-Dhop.local-db-tests=true"
  verify (working directory 07-implementation/backend)
- npm run quality (working directory 07-implementation/employee-portal)
- npm audit --audit-level=low (working directory 07-implementation/employee-portal)
- npm run quality (working directory 07-implementation/mobile-app)
- npm audit --audit-level=low (working directory 07-implementation/mobile-app)
- trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
  --exit-code 0 --timeout 10m --skip-dirs node_modules --skip-dirs dist --skip-dirs
  coverage --skip-dirs target --format table . (working directory repository root)
- git diff --check (working directory repository root, run after final staging)
results:
  backend: 191 tests, 0 failures, 0 errors, 0 skipped. BUILD SUCCESS.
  employee_portal: 31 test files, 87 tests, 0 failures. All 7 quality sub-gates passed.
  mobile_app: 6 test files, 17 tests, 0 failures. All configured quality sub-gates
    passed with V8 coverage.
coverage_summary:
  backend:
    tool: JaCoCo
    previous_iteration_minimum_line_coverage_percent: 77.32
    current_line_coverage_percent: 77.92
    line_missed: 1769
    line_covered: 6243
    line_total: 8012
    decision: passed_no_regression_below_target_improvement_with_justification
    justification: 'All classes touched or added by this backlog item are at or near
      100% line coverage. Largest remaining gaps are pre-existing, unrelated JDBC
      repositories in modules not touched by this backlog item (JdbcLaboratoryResultsRepository,
      JdbcPatientRepository, JdbcCashierOperationsRepository, JdbcDoctorRepository
      and several catalog repositories). TD-BE-003 updated with the new baseline.

      '
  employee_portal:
    tool: vitest --coverage (v8)
    previous_iteration_minimum_line_coverage_percent: 84.42
    current_line_coverage_percent: 84.44
    decision: passed_no_regression_and_improved
  mobile_app:
    tool: vitest --coverage (v8)
    previous_iteration_minimum_line_coverage_percent: not_measured
    current_line_coverage_percent: 97.15
    decision: passed_above_80_percent_target_and_TD_APP_002_closed
dependency_vulnerability_summary:
  backend_pom: 0 vulnerabilities (Trivy)
  employee_portal_package_lock: 0 vulnerabilities (Trivy and npm audit)
  mobile_app: 0 vulnerabilities (npm audit; dedicated package-lock.json now exists)
sast_summary: 'No new dedicated SAST run in this pass beyond the backend Maven `quality`
  profile''s Checkstyle/PMD/SpotBugs+FindSecBugs executions (which ran as part of
  `-Pquality verify`, non-blocking per the project''s existing `quality.failOnViolation=false`
  configuration, unchanged by this backlog item) and the frontend''s ESLint (including
  its existing security-relevant rule set). No new SAST finding was introduced; no
  new SAST tool was added or removed.

  '
dast_summary_when_applicable: not_applicable_no_runnable_web_api_surface_change_requiring_a_new_DAST_pass
  (existing OWASP ZAP baseline/API scan evidence from HOP-QA-ALIGN-004 remains the
  current DAST baseline; this backlog item added 2 new read-only-adjacent backend
  classes (PermissionCode, RolePermissionCatalog) with no new HTTP endpoint, and one
  new required request field (actorUserId) on an existing endpoint, not a new attack
  surface requiring a fresh DAST run)
secrets_scan_summary: 0 secrets detected (Trivy)
duplicate_code_summary:
  employee_portal: 0 findings (jscpd)
  mobile_app: 0 findings (jscpd)
  backend: not separately re-run this iteration beyond the existing PMD CPD wired
    into the quality profile; no new duplication introduced by the small, focused
    classes added
complexity_summary: 'No new complexity/max-lines-per-function findings were introduced;
  new classes are small and single-purpose (largest new file, RolePermissionCatalog,
  is 68 lines). Employee-portal ESLint run showed the same 24 pre-existing warnings
  on untouched screens as before this backlog item (tracked by TD-FE-003), none newly
  introduced.

  '
owasp_or_secure_code_summary: 'The one genuine hardcoded-actor finding in scope (IdentityAccessService''s
  "system" literal for a real user-initiated action) was fixed, not merely documented.
  Corrective closure also added a Spring Security integration point plus request-time
  authentication/authorization enforcement through HopAuthorizationInterceptor, with
  tests covering 401, 403 and allowed request-context binding. TD-IAM-001 is closed;
  production OIDC/IdP hardening remains a later productization concern.

  '
message_externalization_summary: 'See 03-architecture/i18n-localization/localization-strategy.md.
  New/changed user-visible text introduced by this backlog item''s own code (AppShell
  header/tabs, the language-switch control, identityaccess''s validation/not-found
  messages) is externalized in both es-MX and en-US. Pre-existing text not touched
  by this iteration is inventoried with an explicit remaining-scope list, not silently
  left undocumented.

  '
license_summary:
  employee_portal: 5 MIT, 1 UNLICENSED (unchanged; no new dependency added)
  backend: spring-boot-starter-security added under the existing Spring open source
    stack
  mobile_app: dedicated devDependency lockfile added; npm audit reports 0 vulnerabilities
technology_evolution_review: 'Spring Boot 4.1.0/Java 21/springdoc 3.0.3/PostgreSQL
  driver 42.7.13 remain current; no unsupported or end-of-life runtime detected. JPA/Hibernate,
  OpenAPI Generator, MapStruct, Lombok and react-i18next/FormatJS were each evaluated
  against the current codebase and deliberately deferred with stated revisit triggers
  (TD-STACK-002, TD-STACK-003), not silently ignored. A genuine local development
  infrastructure risk (duplicate, manually-synced Docker-init schema file) was discovered
  and immediately partially remediated (resynced) with the residual architectural
  risk tracked as TD-STACK-004.

  '
technical_debt_items_created_or_updated:
  closed:
  - TD-BE-009
  - TD-IAM-001
  - TD-APP-002
  materially_reduced:
  - TD-I18N-002
  - TD-IAM-002
  status_and_baseline_updated:
  - TD-BE-003
  new:
  - TD-DB-001
  - TD-DB-002
  - TD-DB-003
  - TD-DB-004
  - TD-UX-001
  - TD-UX-002
  - TD-UX-003
  - TD-STACK-002
  - TD-STACK-003
  - TD-STACK-004
accepted_risks: []
blocking_findings: []
decision: passed
```
