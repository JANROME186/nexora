# HOP-ENT-FOUND-001 Enterprise Product Foundation Alignment Validation

Machine-readable source: `HOP-ENT-FOUND-001-validation.md`.

## Objective

Align HOP with the enterprise product foundation standard across localization, IAM/dynamic menus,
login/session baseline, database deliverables, UX/UI, code documentation, persistence,
contract-first review, technical debt and coverage policy before resuming
`MVP-MOD-007-PORTAL-001`.

## Execution model

Two scoped implementation passes (backend; frontend+mobile) were run, then **personally
re-verified from a clean state** rather than accepted on the implementing agents' self-reports.
This caught and fixed two real defects — see below.

## Debt-first action

Reviewed the technical-debt index before feature work. Closed **TD-BE-009** (branch version
placeholder — `Branch` now carries a real `version` field). Materially reduced **TD-I18N-002**
(full i18n adoption — a real backend `MessageSource` baseline and frontend/mobile locale-keyed
catalogs with a working language switch now exist end to end). `TD-QA-004` was reviewed but left
untouched — reproducing it safely needs a live malformed-parameter request and carries regression
risk without that reproduction.

## Two defects found and fixed during validation

1. **Coverage measurement gap**: the first `mvn test` run (no local-DB flag) showed backend
   coverage at 76.11%, below the 76.99% floor. Root cause: every `Jdbc*Repository` class is
   skipped entirely without `-Dhop.local-db-tests=true`; the new `branches.version` logic added
   lines to an already-0%-covered class, mechanically dragging the aggregate down. Re-running with
   the local-DB flag (the methodology every prior authoritative measurement in this project used)
   surfaced defect 2.
2. **Stale duplicate schema file**: with local-DB tests enabled, two integration tests failed with
   `BadSqlGrammarException` — the live local Postgres container didn't have the new `version`
   column. Root cause: `runtime/local/postgres/init/001-create-platform-foundation-schemas.sql` is
   a hand-maintained duplicate of the authoritative `schema.sql`, mounted as a Docker init script
   that runs (and silently wins) before Spring Boot's own schema initializer. Resynced the
   duplicate, reset the local Postgres volume, reran — all local-database tests passed. Registered
   as **TD-STACK-004** so this class of risk stays tracked.

## Real code delivered

- **Backend**: `Branch`/`BranchSnapshot` versioning (TD-BE-009), actor-attributed role assignment
  (removed a hardcoded `"system"`), Spring `MessageSource` i18n baseline on `identityaccess`, a
  27-code `PermissionCode`/`RolePermissionCatalog`/`AuthorizationService` IAM domain model,
  request-time backend authorization for mapped API paths, and new
  `organization.countries/locales/currencies` reference tables plus minimal diagnostic catalog seed
  data.
- **Employee portal**: locale-keyed `es-MX`/`en-US` catalogs with a working `AppShell` language
  switch, permission-filtered navigation backed by a documented local-dev session fixture, session
  headers sent to backend API calls, and CSS design tokens.
- **Mobile app**: the same locale-keyed catalog pattern, a mirrored permission model applied to
  the route list, API session-header injection and a measured Vitest coverage gate.

A screen-count discrepancy in the original task briefs ("26 screens") was caught during
validation — `AppShell.TABS` actually has **27** entries, and both implementations independently
used the correct 27. All architecture docs were corrected from 26 to 27.

## Validation results

| Gate | Result |
|---|---|
| Backend `mvn -Pquality -Dhop.local-db-tests=true verify` | **191 tests, 0 failures/errors, 0 skipped**, coverage 76.99% → **77.92%** |
| Employee portal `npm run quality` | **31 files / 87 tests**, coverage 83.98% → **84.44%**, all 7 sub-gates passed |
| Employee portal `npm audit` | 0 vulnerabilities |
| Mobile app `npm run quality` | **6 files / 17 tests**, coverage **97.15%**, all configured gates passed |
| Mobile app `npm audit` | 0 vulnerabilities |
| Trivy filesystem scan | 0 vulnerabilities; no secret findings reported; no misconfiguration targets detected |
| YAML parse (878 files) | 0 errors (2 fixed during this pass) |
| Agent-agnostic scan | 1 match, confirmed false positive (`cursor: pointer`) |

## Readiness

**Closed after corrective closure.** All 11 required foundation areas are addressed with real code
and honest, explicitly registered residual gaps. `TD-BE-009`, `TD-IAM-001` and `TD-APP-002` are
closed; `TD-I18N-002` and `TD-IAM-002` are materially reduced. Backend, frontend and mobile all
improved or established coverage with zero regression. Next backlog item:
`MVP-MOD-007-PORTAL-001`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-ENT-FOUND-001-001
  type: qa-validation-evidence
  name: HOP-ENT-FOUND-001 Enterprise Product Foundation Alignment Validation
  version: 1.0.0
  status: passed
  human_readable: HOP-ENT-FOUND-001-validation.md
  machine_readable: HOP-ENT-FOUND-001-validation.md
  created_date: 2026-07-17
  owner: Nexora Product Architecture Team
scope:
  backlog_item: HOP-ENT-FOUND-001
  module: HOP-ENTERPRISE-FOUNDATION-ALIGNMENT
  release: REL-001
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  implementation_roots:
  - 07-implementation/backend
  - 07-implementation/employee-portal
  - 07-implementation/mobile-app
  predecessor_evidence:
  - 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-FE-001-validation.md
  objective: 'Align HOP with the enterprise product foundation standard (localization,
    IAM permissions, dynamic menus, login/session baseline, database product deliverables,
    UX/UI baseline, code documentation, persistence architecture, contract-first review,
    technical debt and coverage policy) before resuming MVP-MOD-007-PORTAL-001.

    '
execution_model: 'Two independent, scoped implementation passes (backend; frontend+mobile)
  were executed and then personally verified by re-running every mandatory gate from
  a clean state, rather than accepting self-reported results. This verification pass
  found and fixed two real defects before closure: (1) a coverage regression traced
  to an incomplete local-database test run rather than actual new code being untested,
  and (2) a genuinely stale, manually-duplicated Docker-init schema file that caused
  two local-database integration tests to fail once actually exercised against a live
  Postgres instance. Both are documented in detail below and in 03-architecture/enterprise-foundation/enterprise-foundation-alignment.md.

  '
debt_first_action:
  reviewed: 08-qa/technical-debt/technical-debt-index.md
  candidates_considered:
  - TD-BE-009
  - TD-I18N-002
  - TD-QA-004
  - TD-BE-005
  selected:
  - TD-BE-009
  - TD-I18N-002
  - TD-IAM-001
  - TD-IAM-002
  - TD-APP-002
  disposition: 'TD-BE-009 (branch snapshot version placeholder) was closed: organizationmanagement.domain.
    Branch gained a real version field, BranchSnapshot.from(Branch) now reads it directly,
    and the hardcoded UNVERSIONED constant was removed. TD-I18N-002 (full localization
    adoption) was materially reduced: a working backend MessageSource/ResourceBundle
    baseline and frontend/mobile locale-keyed catalogs with a real language switch
    now exist end to end, satisfying this item''s own stated remediation trigger ("a
    second locale is committed to for the product"). Corrective closure also closed
    TD-IAM-001 by adding request-time backend authorization for mapped API paths,
    materially reduced TD-IAM-002 with API/action permission mapping, and closed TD-APP-002
    by adding measured mobile coverage above 80%. TD-QA-004 (malformed query parameter
    500) was reviewed but not attempted: reproducing it safely requires a live-server
    request with deliberately malformed parameter syntax and carries regression risk
    to legitimate parameter parsing if patched without that reproduction; left open,
    unchanged. TD-BE-005 was reviewed and found unrelated to this iteration''s scope
    (doctor activation gating, not touched).

    '
backend_changes:
  branch_versioning_td_be_009:
    files:
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/organizationmanagement/domain/Branch.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/organizationmanagement/domain/BranchSnapshot.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/organizationmanagement/application/OrganizationManagementService.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/organizationmanagement/adapter/out/jdbc/JdbcOrganizationRepository.java
    - 07-implementation/backend/src/main/resources/db/platform-foundation/schema.sql
    - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/organizationmanagement/domain/BranchSnapshotTest.java
  actor_attribution_fix:
    description: 'IdentityAccessService.assignRole no longer hardcodes "system" as
      RoleAssignment.createdBy; AssignRoleCommand/AssignRoleRequest now require an
      explicit actorUserId, validated the same way as other required fields.

      '
    files:
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/application/AssignRoleCommand.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/application/IdentityAccessService.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/adapter/in/web/IdentityAccessController.java
  i18n_baseline:
    files:
    - 07-implementation/backend/src/main/resources/i18n/messages.properties
    - 07-implementation/backend/src/main/resources/i18n/messages_es_MX.properties
    - 07-implementation/backend/src/main/resources/i18n/messages_en_US.properties
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/sharedkernel/LocalizationConfig.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/sharedkernel/HopMessages.java
    - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/sharedkernel/HopMessagesTest.java
  iam_permission_catalog:
    files:
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/domain/PermissionCode.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/domain/RolePermissionCatalog.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/application/AuthorizationService.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/EndpointPermissionRegistry.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthorizationInterceptor.java
    - 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthenticationResolver.java
    - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/domain/RolePermissionCatalogTest.java
    - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/application/AuthorizationServiceTest.java
    - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/security/EndpointPermissionRegistryTest.java
    - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/identityaccess/security/HopAuthorizationInterceptorTest.java
  spring_modulith_boundary_fix: 'identityaccess/package-info.java''s allowedDependencies
    gained sharedkernel, required once IdentityAccessService started depending on
    the new HopMessages component; verified by PlatformFoundationModulithTest.

    '
  database_reference_data:
    description: 'New organization.countries/locales/currencies tables with idempotent
      seed data (MX/US, es-MX/en-US, MXN/USD), added directly by this validation pass
      (not the implementation subagent) to db/platform-foundation/schema.sql.

      '
    no_backend_api_added: true (see TD-DB-003; no current screen consumes this data)
defects_found_and_fixed_during_validation:
- id: DEFECT-001
  description: 'An initial `mvn test` (without `-Dhop.local-db-tests=true`) plus JaCoCo
    report showed backend line coverage at 76.11%, below the 76.99% floor. Root cause:
    JdbcOrganizationRepository (and every other Jdbc*Repository) is entirely skipped
    by the default test run because it requires a real Postgres connection; the new
    branches.version column read/write logic added lines to an already-0%-covered
    class, mechanically dragging the aggregate percentage down even though no previously-covered
    line lost coverage.

    '
  resolution: 'Re-ran with `-Dhop.local-db-tests=true` against the already-running
    local Postgres container (matching the methodology used by every prior authoritative
    coverage measurement in this project''s history, e.g. MVP-MOD-007-BE-002''s evidence).
    This surfaced DEFECT-002.

    '
- id: DEFECT-002
  description: 'With local-database tests enabled, FrontDeskCareDeliveryLocalDatabaseTest
    and OrganizationManagementLocalDatabaseTest both failed with org.springframework.jdbc.BadSqlGrammarException
    inserting into organization.branches: the live local Postgres container (already
    running for 15+ hours from a prior session) did not have the new version column.
    Root cause: 07-implementation/runtime/local/postgres/init/001-create-platform-foundation-schemas.sql
    is a hand-maintained duplicate of backend/src/main/resources/db/platform-foundation/schema.sql,
    mounted as a Docker postgres-container init script. Docker''s init script runs
    once, at first container/volume creation, before Spring Boot''s own spring.sql.init.schema-locations
    initializer runs; both use idempotent CREATE TABLE IF NOT EXISTS, so Docker''s
    (stale) copy silently won for the already-existing organization.branches table.

    '
  resolution: 'Resynced the duplicate file from the authoritative schema.sql (which
    by then also contained the new country/locale/currency tables), reset the local
    Postgres volume via the runbook''s documented `docker compose down -v` / `up -d`
    reset steps, and re-ran the full backend quality/coverage build. All local-database
    tests passed with 0 failures/errors, including both previously-failing local-database
    tests. Registered as TD-STACK-004 (open, non-blocking) so this class of risk is
    tracked, not just fixed once.

    '
frontend_changes:
  files_created:
  - 07-implementation/employee-portal/src/i18n/locales/es-MX.ts
  - 07-implementation/employee-portal/src/i18n/locales/en-US.ts
  - 07-implementation/employee-portal/src/i18n/LocaleContext.tsx
  - 07-implementation/employee-portal/src/state/SessionContext.tsx
  - 07-implementation/employee-portal/src/state/permissions.ts
  - 07-implementation/employee-portal/src/test/LocaleContext.test.tsx
  - 07-implementation/employee-portal/src/test/SessionContext.test.tsx
  files_modified:
  - 07-implementation/employee-portal/src/i18n/messages.ts (backward-compatible re-export
    of the es-MX catalog)
  - 07-implementation/employee-portal/src/App.tsx (wrapped in LocaleProvider/SessionProvider)
  - 07-implementation/employee-portal/src/components/layout/AppShell.tsx (permission-filtered
    navigation; locale-driven header/tab labels; ES/EN language switch)
  - 07-implementation/employee-portal/src/styles.css (hex values extracted to --hop-color-*
    custom properties, zero visual change)
  - Six existing screen test files updated to match the new locale-sourced default
    text.
  screen_count_correction: 'The implementation task briefs (written before code was
    inspected) said "26 screens"; the actual AppShell.TABS array has 27 entries. Both
    the backend PermissionCode enum and the frontend permissions.ts were implemented
    with the correct 27, verified identical by direct comparison during this validation
    pass. All architecture documentation was corrected from "26" to "27" during validation
    (see enterprise-foundation-alignment.md).

    '
mobile_changes:
  files_created:
  - 07-implementation/mobile-app/src/i18n/locales/es-MX.ts
  - 07-implementation/mobile-app/src/i18n/locales/en-US.ts
  - 07-implementation/mobile-app/src/i18n/locale.ts
  - 07-implementation/mobile-app/src/auth/permissions.ts
  - 07-implementation/mobile-app/src/test/permissions.test.ts
  files_modified:
  - 07-implementation/mobile-app/src/i18n/messages.ts (backward-compatible re-export)
  - 07-implementation/mobile-app/src/auth/localAuth.ts (local-fixture JSDoc disclosure;
    optional locale parameter)
  - 07-implementation/mobile-app/src/navigation/routes.ts (ROUTE_TO_PERMISSION, visibleRoutesForPermissions())
  - 07-implementation/mobile-app/src/test/localAuth.test.ts
validation_commands:
- id: backend_test_and_coverage
  working_directory: 07-implementation/backend
  command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Pquality
    "-Dhop.local-db-tests=true" verify
  result: passed
  detail: 191 tests, 0 failures, 0 errors, 0 skipped. BUILD SUCCESS.
  line_coverage_percent: 77.92
  previous_iteration_minimum_line_coverage_percent: 77.32
  regression: false
- id: employee_portal_quality
  working_directory: 07-implementation/employee-portal
  command: npm run quality
  result: passed
  detail: typecheck, lint (0 errors, 24 pre-existing warnings unrelated to this backlog
    item), 31 test files / 87 tests (0 failures), build, duplication (0 findings),
    format:check, license:check all passed.
  line_coverage_percent: 84.44
  previous_iteration_minimum_line_coverage_percent: 84.42
  regression: false
- id: employee_portal_npm_audit
  working_directory: 07-implementation/employee-portal
  command: npm audit --audit-level=low
  result: passed
  vulnerabilities: 0
- id: mobile_app_quality
  working_directory: 07-implementation/mobile-app
  command: npm run quality
  result: passed
  detail: typecheck, lint, coverage (6 files / 17 tests, 0 failures), duplication,
    format:check all passed.
  line_coverage_percent: 97.15
  previous_iteration_minimum_line_coverage_percent: not_measured
  regression: false
- id: mobile_app_npm_audit
  working_directory: 07-implementation/mobile-app
  command: npm audit --audit-level=low
  result: passed
  vulnerabilities: 0
- id: trivy_filesystem
  working_directory: .
  command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
    --exit-code 0 --timeout 10m --skip-dirs node_modules --skip-dirs dist --skip-dirs
    coverage --skip-dirs target --format table .
  result: passed
  detail: 0 vulnerabilities (backend pom.xml, employee-portal package-lock.json),
    0 secrets, 0 misconfigurations.
- id: yaml_parse
  command: python -c "import yaml,os; parse every .yaml/.yml file outside node_modules/target/dist/coverage/.git/build/.m2"
  result: passed
  detail: 878 files parsed, 0 errors (2 real syntax errors in newly-authored files
    were found and fixed during this pass before the final clean run).
- id: agent_agnostic_scan
  command: grep -ilE "claude|anthropic|copilot|cursor|chatgpt|openai|gemini|codex|windsurf|aider"
    across every file changed by this backlog item
  result: passed
  detail: '1 match, confirmed false positive (the "cursor: pointer" CSS property in
    styles.css), consistent with the same documented false positive in MVP-MOD-007-FE-001''s
    evidence.'
- id: git_diff_check
  command: git diff --check
  result: see closure section below (run again after final commit staging)
technical_debt:
  closed:
  - id: TD-BE-009
    evidence: 08-qa/technical-debt/TD-BE-009-branch-snapshot-version-placeholder.md
  - id: TD-IAM-001
    evidence: 08-qa/technical-debt/TD-IAM-001-backend-authentication-missing.md
  - id: TD-APP-002
    evidence: 08-qa/technical-debt/TD-APP-002-mobile-coverage-80-target.md
  materially_reduced:
  - id: TD-I18N-002
    evidence: 08-qa/technical-debt/TD-I18N-002-full-localization-adoption.md
  - id: TD-IAM-002
    evidence: 08-qa/technical-debt/TD-IAM-002-permission-granularity-gap.md
  registered:
  - TD-DB-001 (medium)
  - TD-DB-002 (medium)
  - TD-DB-003 (low)
  - TD-DB-004 (low)
  - TD-UX-001 (low)
  - TD-UX-002 (medium)
  - TD-UX-003 (low)
  - TD-STACK-002 (low)
  - TD-STACK-003 (medium)
  - TD-STACK-004 (medium)
model_gaps_identified: []
blocking_gaps: []
readiness:
  hop_ent_found_001_status: closed
  ready_for_next_backlog_item: MVP-MOD-007-PORTAL-001
  rationale: 'All 11 required foundation areas are addressed with real code, real
    tests and honest, explicitly-scoped residual productization gaps registered as
    technical debt rather than silently glossed over. Corrective closure added request-time
    backend authorization, API/action permission mapping, web/mobile session headers,
    mobile coverage measurement above 80%, and a minimal diagnostic seed catalog.
    Backend, frontend and mobile improved coverage with zero regression against previous
    measured baselines; backend remains below 80% but is now above the 77.32% floor
    at 77.92%. All repository YAML files parse; vulnerability scans report 0 known
    vulnerabilities for the npm-managed stacks.

    '
```
