# MVP-MOD-002-QA-001 - Security Quality Evidence

- Artifact: HOP-SQ-MVP-MOD-002-QA-001-001
- Status: passed
- Backlog item: MVP-MOD-002-QA-001
- Module: MVP-MOD-002 Diagnostic Catalog

## Open Source First Review

No proprietary runtime dependency was introduced. The reviewed implementation stack remains based on
open source, standards-based technologies:

| Area | Technology | Version | License |
|---|---|---:|---|
| Backend framework | Spring Boot | 3.5.14 | Apache-2.0 |
| Backend modularity | Spring Modulith | 1.4.5 | Apache-2.0 |
| Embedded runtime | Apache Tomcat | 10.1.55 | Apache-2.0 |
| JSON baseline | Jackson BOM override | 2.21.4 | Apache-2.0 |
| Database driver | PostgreSQL JDBC | 42.7.11 | BSD-2-Clause |
| Frontend | React | 18.3.1 | MIT |
| Frontend build | Vite | 6.4.3 | MIT |
| Frontend tests | Vitest | 3.2.7 | MIT |
| Security scan | Trivy | 0.69.2 | Apache-2.0 |

## Vulnerability Remediation

The initial Trivy scan found 25 HIGH/CRITICAL dependency findings in the backend Maven graph. The
backend `pom.xml` was upgraded to a safer dependency baseline:

- Spring Boot parent: 3.5.14
- Spring Modulith: 1.4.5
- Jackson BOM override: 2.21.4
- Tomcat: 10.1.55
- PostgreSQL JDBC: 42.7.11

The final Trivy scan passed with 0 HIGH or CRITICAL findings across the backend and employee portal
dependency manifests.

## Quality Gates

| Gate | Command | Result |
|---|---|---|
| Backend tests | `mvn --settings .mvn/settings.xml test` | passed - 42 run, 0 failures, 0 errors, 5 skipped |
| Backend PostgreSQL tests | `mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"` | passed - 42 run, 0 failures, 0 errors, 0 skipped |
| Frontend static analysis | `npm run typecheck` | passed |
| Frontend coverage | `npm run test:coverage` | passed - 68.7% lines/statements, 85.83% branches, 35.43% functions |
| Frontend audit | `npm audit --audit-level=high` | passed - 0 vulnerabilities |
| Frontend build | `npm run build` | passed |
| Trivy filesystem scan | `trivy fs --scanners vuln,secret,misconfig --severity HIGH,CRITICAL --exit-code 1 --no-progress .` | passed after remediation |
| Integrated runtime smoke | API health plus catalog create/list/publish | passed |

## DAST

OWASP ZAP was not available in the local toolchain during this validation. Manual integrated HTTP
smoke checks passed, and `TD-QA-001` was registered to add automated DAST baseline scans for runnable
web/API surfaces before release hardening.

## Technical Debt

- `TD-QA-001`: automate DAST baseline scans for runnable web and API surfaces.
- `TD-QA-002`: upgrade the Trivy scanner version in local and CI quality toolchains.
- `TD-BE-001`: configure Mockito as a Java agent for future JDK test compatibility.

No blocking security exceptions remain for MVP-MOD-002 closeout.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-MVP-MOD-002-QA-001-001
  type: security-quality-evidence
  name: MVP-MOD-002-QA-001 Integrated Open Source Security Quality Evidence
  version: 1.0.0
  status: passed
  human_readable: security-quality-evidence.md
  machine_readable: security-quality-evidence.md
  created_date: 2026-07-09
  owner: Nexora Product Architecture Team
scope:
  backlog_item: MVP-MOD-002-QA-001
  module: MVP-MOD-002 Diagnostic Catalog
  implementation_root: 07-implementation/
  standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  objective: Validate the integrated Diagnostic Catalog module under open-source-first,
    security and quality expectations.
open_source_first:
  status: passed
  proprietary_runtime_dependency_detected: false
  reviewed_stack:
  - name: Spring Boot
    version: 3.5.14
    license: Apache-2.0
    role: backend application framework
  - name: Spring Modulith
    version: 1.4.5
    license: Apache-2.0
    role: modular backend architecture support
  - name: Apache Tomcat
    version: 10.1.55
    license: Apache-2.0
    role: embedded servlet runtime
  - name: Jackson
    version: 2.21.4
    license: Apache-2.0
    role: JSON serialization baseline through Maven BOM override
  - name: PostgreSQL JDBC
    version: 42.7.11
    license: BSD-2-Clause
    role: database driver
  - name: React
    version: 18.3.1
    license: MIT
    role: employee portal UI
  - name: Vite
    version: 6.4.3
    license: MIT
    role: frontend build tool
  - name: Vitest
    version: 3.2.7
    license: MIT
    role: frontend tests and coverage
  - name: Trivy
    version: 0.69.2
    license: Apache-2.0
    role: vulnerability, secret and misconfiguration scan
quality_gates:
- id: SQ-001
  name: Backend automated tests
  command: mvn --settings .mvn/settings.xml test
  working_directory: 07-implementation/backend
  result: passed
  notes: 42 tests run, 0 failures, 0 errors, 5 skipped.
- id: SQ-002
  name: Backend database-backed tests
  command: mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
  working_directory: 07-implementation/backend
  result: passed
  notes: 42 tests run, 0 failures, 0 errors, 0 skipped against local PostgreSQL.
- id: SQ-003
  name: Frontend static analysis
  command: npm run typecheck
  working_directory: 07-implementation/employee-portal
  result: passed
- id: SQ-004
  name: Frontend coverage
  command: npm run test:coverage
  working_directory: 07-implementation/employee-portal
  result: passed
  measured:
    lines: 68.7
    statements: 68.7
    branches: 85.83
    functions: 35.43
- id: SQ-005
  name: Frontend dependency vulnerability scan
  command: npm audit --audit-level=high
  working_directory: 07-implementation/employee-portal
  result: passed
  notes: found 0 vulnerabilities.
- id: SQ-006
  name: Filesystem vulnerability, secret and misconfiguration scan
  command: trivy fs --scanners vuln,secret,misconfig --severity HIGH,CRITICAL --exit-code
    1 --no-progress .
  working_directory: 07-implementation
  result: passed_after_remediation
  notes: Initial scan failed with 25 HIGH/CRITICAL backend dependency findings; final
    scan passed with 0 HIGH/CRITICAL findings.
- id: SQ-007
  name: Production frontend build
  command: npm run build
  working_directory: 07-implementation/employee-portal
  result: passed
- id: SQ-008
  name: Integrated runtime smoke
  command: HTTP health checks and Diagnostic Catalog create/list/publish smoke
  working_directory: 07-implementation
  result: passed
  notes: Backend health, frontend availability and diagnostic service create/list/publish
    flow passed after dependency remediation.
- id: SQ-009
  name: DAST
  command: not_executed
  result: deferred_with_technical_debt
  notes: OWASP ZAP was not available locally. Manual integrated HTTP smoke passed;
    TD-QA-001 tracks automated DAST enablement.
- id: SQ-010
  name: Container or IaC scan
  command: trivy fs --scanners vuln,secret,misconfig --severity HIGH,CRITICAL --exit-code
    1 --no-progress .
  working_directory: 07-implementation
  result: passed
  notes: Compose and local runtime files were covered by the filesystem misconfiguration
    scan.
dependency_remediation:
  initial_scan_status: failed
  initial_backend_high_critical_findings: 25
  remediated_dependencies:
    spring_boot_parent: 3.5.14
    spring_modulith: 1.4.5
    jackson_bom: 2.21.4
    tomcat: 10.1.55
    postgresql_jdbc: 42.7.11
  final_scan_status: passed
  final_high_critical_findings: 0
technical_debt:
  registered:
  - TD-QA-001
  - TD-QA-002
  - TD-BE-001
  blocking: []
exceptions: []
readiness:
  security_quality_status: passed
  ready_for_next_backlog_item: MVP-MOD-002-CLOSEOUT
  next_required_focus:
  - Close out the MVP-MOD-002 capability package group.
  - Add automated DAST before release hardening or when CI quality gates are expanded.
  - Apply registered technology debt gradually when affected components are touched.
```
