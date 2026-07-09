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
