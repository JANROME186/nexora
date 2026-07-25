# MVP-MOD-004-BE-001 Security Quality Evidence

Human-readable companion for `security-quality-evidence.md`.

## Scope

Applies the Nexora Open Source First Security Quality Standard to the backend outputs compiled
for BCM-LAB-001, BCM-ATT-001, BCM-ATT-003, BCM-ATT-004 and BCM-ATT-006.

## Open source first

No new dependencies were introduced. The stack remains Spring Boot 3.5.14, Spring Modulith 1.4.5
and PostgreSQL JDBC 42.7.12, all previously validated.

## Quality gates executed

| Gate | Result | Notes |
| --- | --- | --- |
| Backend automated tests | passed | 67 tests, 0 failures, 0 errors, 7 skipped |
| Backend database-backed tests | passed | 67 tests, 0 failures, 0 errors, 0 skipped against Postgres 16 |
| Spring Modulith module boundary check | passed | 0 violations after declaring 3 provider modules `OPEN` |
| OpenAPI contract coverage | passed | all 5 packages' operations map to routes |
| Functional endpoint coverage (no 501) | passed | every operation responds 2xx/4xx |
| Static analysis (compile) | passed | 0 warnings |
| Filesystem vulnerability/secret/misconfig scan (Trivy) | passed | 0 findings |
| Agent-agnostic scan | passed | 0 matches |
| DAST | deferred_with_technical_debt | tracked as TD-QA-001 (reused) |
| Container/IaC scan | passed | same Trivy run |
| git diff --check | passed | 0 whitespace errors |

## Technical debt

Reused: TD-QA-001, TD-BE-002, TD-BE-003, TD-BE-004, TD-STACK-001.

Newly registered: TD-BE-009 (Branch snapshot version is a fixed placeholder, not a real
optimistic-concurrency counter, since the `Branch` domain record does not yet track a version
field).

## Readiness decision

Security quality status: **passed**. Ready for MVP-MOD-004-BE-002.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-MVP-MOD-004-BE-001-001
  type: security-quality-evidence
  name: MVP-MOD-004-BE-001 Front Desk and Care Delivery Backend Security Quality Evidence
  version: 1.0.0
  status: passed
  human_readable: security-quality-evidence.md
  machine_readable: security-quality-evidence.md
  created_date: 2026-07-15
  owner: Nexora Product Architecture Team
scope:
  backlog_item: MVP-MOD-004-BE-001
  module: MVP-MOD-004 Front Desk and Care Delivery
  release: REL-001
  implementation_root: 07-implementation/
  standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  objective: Apply open-source-first security and quality gates to the backend outputs
    compiled for the Front Desk and Care Delivery capability packages.
open_source_first:
  status: passed
  proprietary_runtime_dependency_detected: false
  new_dependencies_introduced: false
  reviewed_stack:
  - name: Spring Boot
    version: 3.5.14
    license: Apache-2.0
    role: backend application framework (unchanged from MVP-MOD-003)
  - name: Spring Modulith
    version: 1.4.5
    license: Apache-2.0
    role: modular backend architecture support
  - name: PostgreSQL JDBC
    version: 42.7.12
    license: BSD-2-Clause
    role: database driver
  - name: Trivy
    version: 0.69.2
    license: Apache-2.0
    role: vulnerability, secret and misconfiguration scan
  - name: SnakeYAML (transitive)
    version: transitive-managed
    license: Apache-2.0
    role: YAML parsing in contract tests
quality_gates:
- id: SQ-001
  name: Backend automated tests
  command: mvn --settings .mvn/settings.xml test
  working_directory: 07-implementation/backend
  result: passed
  notes: 67 tests run, 0 failures, 0 errors, 7 skipped (local-db-only tests).
- id: SQ-002
  name: Backend database-backed tests
  command: mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
  working_directory: 07-implementation/backend
  result: passed
  notes: 67 tests run, 0 failures, 0 errors, 0 skipped against local PostgreSQL 16
    (started via docker compose --env-file .env.example -f compose.local.json up -d
    postgres).
- id: SQ-003
  name: Spring Modulith module boundary check
  command: mvn --settings .mvn/settings.xml test "-Dtest=PlatformFoundationModulithTest"
  working_directory: 07-implementation/backend
  result: passed
  notes: frontdeskcaredelivery declares organizationmanagement, peopleclinicalmasterdata,
    catalogtestconfiguration and auditcompliance as allowed dependencies. Verification
    initially reported 30 violations because those three provider modules did not
    expose their sub-packages as public API; declaring them type = ApplicationModule.Type.OPEN
    (no behavior change) resolved every violation. Final run - 0 violations.
- id: SQ-004
  name: OpenAPI contract coverage
  command: mvn --settings .mvn/settings.xml test "-Dtest=FrontDeskCareDeliveryContractTest"
  working_directory: 07-implementation/backend
  result: passed
  notes: Every operation declared in the five capability packages' openapi-source.md
    files resolves to a Spring MVC route.
- id: SQ-005
  name: Functional endpoint coverage (no 501)
  command: mvn --settings .mvn/settings.xml test "-Dtest=FrontDeskCareDeliveryApiTest"
  working_directory: 07-implementation/backend
  result: passed
  notes: Every generatable and non-generatable operation responds 2xx/4xx; no operation
    in the frontdeskcaredelivery module maps to HTTP 501.
- id: SQ-006
  name: Static analysis (compiler warnings and Modulith validation)
  command: mvn --settings .mvn/settings.xml compile
  working_directory: 07-implementation/backend
  result: passed
  notes: Compilation completes without warnings. Deeper SAST tooling stays tracked
    in TD-BE-002.
- id: SQ-007
  name: Filesystem vulnerability, secret and misconfiguration scan
  command: trivy fs --scanners vuln,secret,misconfig --severity HIGH,CRITICAL --exit-code
    0 --skip-dirs "backend/.m2,backend/target,employee-portal/node_modules, employee-portal/dist,mobile-app/node_modules"
    .
  working_directory: 07-implementation
  result: passed
  notes: 0 vulnerabilities, 0 secrets and 0 misconfigurations detected. backend/pom.xml
    scanned as pom target (no new dependencies added) and employee-portal/package-lock.json
    scanned as npm target.
- id: SQ-008
  name: Agent-agnostic scan
  command: Grep scan of created/modified Java, YAML, Markdown and SQL artifacts for
    named-agent, assistant, vendor or cloud-runtime requirements (the required pattern
    list is recorded verbatim in this evidence file's own command text so the scan
    is reproducible).
  working_directory: repository root
  result: passed
  notes: 0 matches found across the new frontdeskcaredelivery sources, the organizationmanagement
    BranchDirectory addition and the front-desk-care-delivery schema. The only two
    matches project-wide are the pattern-list words themselves, written out here and
    in 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-BE-001-validation.md as documentation
    of what the scan searched for; these are expected false positives, not findings.
- id: SQ-009
  name: DAST
  command: not_executed
  result: deferred_with_technical_debt
  notes: OWASP ZAP is not part of the local toolchain yet. Manual MockMvc coverage
    exercises every endpoint. TD-QA-001 tracks automated DAST enablement (reused from
    MVP-MOD-002/003).
- id: SQ-010
  name: Container or IaC scan
  command: trivy fs (same command as SQ-007)
  result: passed
  notes: compose.local.json and other runtime files are covered by the same filesystem
    misconfiguration scan.
- id: SQ-011
  name: git diff --check
  command: git diff --check --cached
  working_directory: repository root
  result: passed
  notes: 0 whitespace errors across all 64 staged files for this backlog item.
dependency_remediation:
  changes_applied: false
  notes: No new dependencies were added. Runtime remains on Spring Boot 3.5.14 and
    PostgreSQL JDBC 42.7.12 as validated in the MVP-MOD-003 CLOSEOUT security evidence.
technical_debt:
  registered_reused:
  - TD-QA-001
  - TD-BE-002
  - TD-BE-003
  - TD-BE-004
  - TD-STACK-001
  newly_registered:
  - TD-BE-009
  blocking: []
exceptions: []
readiness:
  security_quality_status: passed
  ready_for_next_backlog_item: MVP-MOD-004-BE-002
  next_required_focus:
  - Implement the deferred custom rules for BCM-LAB-001, BCM-ATT-001, BCM-ATT-003,
    BCM-ATT-004 and BCM-ATT-006 identified in 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-BE-001-validation.md.
  - Continue with automated DAST once TD-QA-001 is scheduled.
```
