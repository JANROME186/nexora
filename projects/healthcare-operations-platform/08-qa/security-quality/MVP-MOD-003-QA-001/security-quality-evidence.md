# MVP-MOD-003-QA-001 — Security & Quality Evidence

Status: **passed**
Machine-readable evidence: `security-quality-evidence.md`

## Summary

All mandatory executable security and quality gates for MVP-MOD-003-QA-001 passed after re-running
the previously limited checks in a compatible local environment with Docker/Postgres, Java 21,
Maven 3.9.11, Node/npm and the local Trivy database available.

## Gate results

| Gate | Result |
|---|---|
| QG-001 Backend Maven tests (no DB) | Passed: 58 tests, 0 failures, 0 errors, 6 skipped |
| QG-002 Backend Maven tests (Postgres) | Passed: 58 tests, 0 failures, 0 errors, 0 skipped |
| QG-003 Employee portal typecheck | Passed |
| QG-004 Employee portal test suite | Passed: 10 files, 18 tests |
| QG-005 Employee portal coverage | Passed: 74.63% statements, 81.17% branches, 45.21% functions, 74.63% lines |
| QG-006 Employee portal build | Passed |
| QG-007 Employee portal dependency audit | Passed: 0 vulnerabilities |
| QG-008 Full project YAML parse | Passed |
| QG-009 Secrets scan | Passed |
| QG-010 Agent-agnostic scan | Passed |
| QG-011 Stale-pointer scan | Passed after fix |
| QG-012 OpenAPI-to-controller contract check | Passed: 42/42 |
| QG-013 BE-002 custom-rule source review | Passed with findings: TD-BE-007, TD-BE-008 |
| QG-014 FE-001 API/UI coverage source review | Passed with findings: TD-FE-002 |
| QG-015 git diff --check | Passed with pre-existing repo-wide line-ending noise unrelated to this item |
| QG-016 Trivy filesystem scan | Passed: 0 HIGH/CRITICAL vulnerabilities and no blocking secret/misconfiguration findings |

## Exceptions

None. The previous execution limitation has been resolved by executing the missing gates.

## Technical debt registered

- **TD-BE-007**: no proactive credential-expiration scheduler; only a reactive check exists.
- **TD-BE-008**: read-model document/credential masking is fixed, not tenant-configurable.
- **TD-FE-002**: patient/doctor update, patient documents and doctor specialty assignment UI not
  yet built.

## Recommendation

Proceed to **MVP-MOD-003-CLOSEOUT**. None of the three newly registered technical debt items block
closeout.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SEC-MVP-MOD-003-QA-001-001
  type: security-quality-evidence
  name: MVP-MOD-003-QA-001 People and Clinical Master Data Integrated Validation Security
    & Quality Evidence
  version: 1.0.0
  status: passed
  human_readable: security-quality-evidence.md
  machine_readable: security-quality-evidence.md
  created_date: 2026-07-14
  owner: Nexora Product Architecture Team
scope:
  backlog_item: MVP-MOD-003-QA-001
  module: MVP-MOD-003 People and Clinical Master Data
  related_qa_evidence: ../../qa/people-and-clinical-master-data/MVP-MOD-003-QA-001-validation.md
  predecessor_evidence:
  - 08-qa/security-quality/MVP-MOD-003-BE-001/security-quality-evidence.md
  - 08-qa/security-quality/MVP-MOD-003-BE-002/security-quality-evidence.md
  - 08-qa/security-quality/MVP-MOD-003-FE-001/security-quality-evidence.md
quality_gates:
- id: QG-001
  name: Backend Maven test suite (no local database)
  command: mvn --settings .mvn/settings.xml test
  working_directory: 07-implementation/backend
  result: passed
  detail: 'Executed with Apache Maven 3.9.11 and Java 21.0.7. Result: 58 tests run,
    0 failures, 0 errors, 6 skipped for the non-local-database profile.

    '
- id: QG-002
  name: Backend Maven test suite (with local Postgres)
  command: docker compose --env-file .env.example -f compose.local.json up -d postgres;
    mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
  working_directory: 07-implementation/backend and 07-implementation
  result: passed
  detail: 'Docker/Postgres local was started and the backend suite executed with -Dhop.local-db-tests=true.
    Result: 58 tests run, 0 failures, 0 errors, 0 skipped.

    '
- id: QG-003
  name: Employee portal TypeScript static analysis
  command: npm run typecheck
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: tsc --noEmit completed with 0 errors.
- id: QG-004
  name: Employee portal unit/UI test suite
  command: npm test -- --run
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: 10 Vitest files passed; 18 tests passed.
- id: QG-005
  name: Employee portal coverage
  command: npm run test:coverage
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: 'Coverage completed successfully. All files: 74.63% statements, 81.17% branches,
    45.21% functions, 74.63% lines.

    '
- id: QG-006
  name: Employee portal production build
  command: npm run build
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: Vite production build completed successfully.
- id: QG-007
  name: Employee portal dependency vulnerability audit
  command: npm audit --audit-level=high
  working_directory: 07-implementation/employee-portal
  result: passed
  detail: npm audit completed with 0 vulnerabilities.
- id: QG-008
  name: Full project and framework YAML parse
  command: Recursive PyYAML safe_load_all over every .yaml/.yml file excluding node_modules
  result: passed
  detail: 480 framework and project YAML files parsed, 0 failures, excluding node_modules
    and target.
- id: QG-009
  name: Secrets scan
  command: rg -n -i (api[_-]?key|secret|password|passwd|private[_-]?key|client[_-]?secret)
    with a value-assignment pattern over Java/TypeScript/YAML/properties source
  working_directory: repository_root
  result: passed
  detail: No matches found.
- id: QG-010
  name: Agent-agnostic scan
  command: rg -n -i (claude|anthropic|openai|gpt-|chatgpt|copilot|cursor\.so|gemini|azure
    openai) over Java/TypeScript/YAML/Markdown source
  working_directory: repository_root
  result: passed
  detail: No matches outside of scan-pattern text quoted inside evidence files (not
    real findings).
- id: QG-011
  name: Stale-pointer / backlog registry scan
  command: rg scan for next_backlog_item/current_backlog_item/active_backlog_item
    and for MVP-MOD-003-FE-001 used as a next/current pointer, across PROJECT_STATE.md,
    SOURCE_OF_TRUTH.md, HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md, the local
    runbook, the 4 capability traceability.md files, and capability-package-index.md
  result: passed_after_fix
  detail: One stale pointer found (capability-package-index.md active_capability_package_group)
    and corrected. All other registries were already consistent.
- id: QG-012
  name: OpenAPI-to-controller contract cross-check
  command: Manual line-level comparison of all 4 packages' openapi-source.md operations
    against their backend controllers' Spring MVC route mappings
  result: passed
  detail: 42/42 operations map one-to-one in both directions across PersonController,
    PatientController, DoctorController and PatientRegistrationController.
- id: QG-013
  name: BE-002 custom-rule source-level review
  command: Manual review of the actual backend Java source against each capability's
    business-rules.md custom rule statements
  result: passed_with_findings
  detail: Two medium-severity, non-blocking gaps found and disclosed (RN-005 credential
    expiration scheduler, RN-008 tenant-configurable read-model masking); tracked
    as TD-BE-007 and TD-BE-008. See related QA evidence for detail.
- id: QG-014
  name: FE-001 API/UI coverage source-level review
  command: Manual comparison of peopleApi.ts and the 4 screen components against openapi-source.md
    operations and traceability.md ui_to_api entries
  result: passed_with_findings
  detail: One low-severity, non-blocking, previously-disclosed-as-out-of-scope UI-completeness
    gap formally tracked as TD-FE-002. See related QA evidence for detail.
- id: QG-015
  name: git diff --check (whitespace)
  command: git diff --check (unstaged, whole working tree) and git diff --cached --check
    (scoped to this backlog item's intentional staged changes)
  result: passed_with_preexisting_noise
  detail: Unscoped check flags ~1471 pre-existing CRLF/LF files unrelated to this
    backlog item's changes (confirmed via git diff --stat -w). Scoped cached check
    is clean.
- id: QG-016
  name: Trivy filesystem vulnerability, secret and misconfiguration scan
  command: trivy fs --skip-db-update --scanners vuln,secret,misconfig --severity HIGH,CRITICAL
    --exit-code 1 --no-progress .
  working_directory: repository_root
  result: passed
  detail: Offline Trivy scan completed using the local vulnerability database. backend/pom.xml
    and employee-portal/package-lock.json reported 0 HIGH/CRITICAL vulnerabilities;
    no blocking secret or misconfiguration findings were reported.
exceptions: []
execution_confirmation:
  summary: 'The execution-limited evidence initially left by the prior agent was re-run
    in the local development environment after Docker became available. All mandatory
    backend, frontend, dependency and filesystem security quality gates passed.

    '
  commands_executed:
  - docker compose --env-file .env.example -f compose.local.json up -d postgres
  - mvn --settings .mvn/settings.xml test
  - mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
  - npm run typecheck
  - npm test
  - npm run test:coverage
  - npm run build
  - npm audit --audit-level=high
  - trivy fs --skip-db-update --scanners vuln,secret,misconfig --severity HIGH,CRITICAL
    --exit-code 1 --no-progress .
technical_debt:
- id: TD-BE-007
  title: Professional credential expiration is not proactively transitioned by a scheduler
    and does not flag doctors for re-verification
  ref: ../../technical-debt/TD-BE-007-credential-expiration-scheduler-missing.md
  severity: medium
- id: TD-BE-008
  title: PatientSnapshot/DoctorSnapshot document and credential number masking is
    fixed, not tenant-configurable
  ref: ../../technical-debt/TD-BE-008-read-model-masking-not-tenant-configurable.md
  severity: medium
- id: TD-FE-002
  title: Employee portal is missing patient/doctor update, patient document management
    and doctor specialty assignment UI
  ref: ../../technical-debt/TD-FE-002-patient-doctor-update-documents-specialty-ui-missing.md
  severity: low
overall_result: passed
recommendation: 'Proceed to MVP-MOD-003-CLOSEOUT. TD-BE-007, TD-BE-008 and TD-FE-002
  do not block closeout.

  '
```
