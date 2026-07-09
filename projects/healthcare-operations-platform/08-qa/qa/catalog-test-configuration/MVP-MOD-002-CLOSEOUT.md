# MVP-MOD-002 - Diagnostic Catalog Closeout

## Scope

This closeout formally closes `MVP-MOD-002 Diagnostic Catalog` for the Healthcare Operations Platform
after completing all implementation and validation backlog items. It also records the official-source
stack market refresh and the quality toolchain gap disposition required by the Nexora Open Source First
Security and Quality Standard.

- Backlog item: `MVP-MOD-002-CLOSEOUT`
- Bounded context: `catalog-test-configuration`
- Release: `REL-001`
- Business requirement version: `v0.68.0` (unchanged, no impact assessment required)

## Capability Packages Closed

`BCM-SVC-001`, `BCM-SVC-002`, `BCM-SVC-003`, `BCM-SVC-004`, `BCM-SVC-005`, `BCM-SVC-006`,
`BCM-SVC-007`, `BCM-SVC-009`.

## Consolidated Backlog Evidence

- `MVP-MOD-002-DEF` capability package model validation.
- `MVP-MOD-002-BE-001` backend catalog outputs (catalog, tests, panels, analytes, preparations, reference ranges, samples, price lists).
- `MVP-MOD-002-BE-002` custom business rules (publication, immutable versioning/snapshots, preparation assignment, effective-dated overlap prevention, effective-context resolution).
- `MVP-MOD-002-FE-001` employee catalog UI outputs and frontend quality gates.
- `MVP-MOD-002-QA-001` integrated validation, dependency remediation and security quality evidence.

## Validation Summary

All validations executed on 2026-07-09 with Java 21.0.7 (LTS), Maven 3.9.11, Node v24.8.0, Trivy 0.69.2
and Docker 29.6.1 against a healthy PostgreSQL 16 local runtime.

- Backend standard suite: 42 tests, 0 failures, 0 errors, 5 skipped (optional local-database tests).
- Backend PostgreSQL-backed suite: 42 tests, 0 failures, 0 errors, 0 skipped.
- Employee portal TypeScript strict check: passed.
- Employee portal coverage: 5 test files, 8 tests, 68.7% lines / 85.83% branches / 35.43% functions.
- Employee portal production build: passed.
- Employee portal `npm audit --audit-level=high`: 0 vulnerabilities.
- Trivy filesystem scan (vuln, secret, misconfig, HIGH/CRITICAL): 0 findings across `backend/pom.xml` and `employee-portal/package-lock.json`.
- YAML parse validation over changed/created artifacts: passed.
- Agent-agnostic reference scan over closeout artifacts: passed.
- DAST: deferred with technical debt `TD-QA-001` (OWASP ZAP unavailable locally; manual integrated HTTP smoke passed in QA-001).

## Stack Market Validation (Official-Source Refresh)

The stack was validated against current stable/LTS versions using official sources on 2026-07-09
(see `03-architecture/technology-architecture/client-stack-market-validation.yaml`).

- Java 21 LTS, Spring Boot 3.5.x, Spring Modulith 1.4.x, Maven 3.9.x, Tomcat 10.1.55 and Jackson 2.21.4
  remain supported and mutually compatible. Newer major lines (Spring Boot 4.x + Spring Modulith 2.x,
  Java 25 LTS) are tracked as gradual debt.
- React 18.3.1, TypeScript 5.9.3 and Vite 6.x are supported baselines; React 19, TypeScript 6.0 and
  Vite 7/8 are tracked as gradual debt.
- PostgreSQL 16 is supported until November 2028; PostgreSQL 18 evaluation is tracked as gradual debt.

Immediate change applied during closeout:

- **PostgreSQL JDBC 42.7.11 → 42.7.12** — official security release (channel-binding enforcement).
  Low-risk same-line patch, applied in `07-implementation/backend/pom.xml` and revalidated
  (backend suites and Trivy scan re-run, all passing).

## Quality Toolchain Disposition

- **Mandatory now (executed):** backend Maven test suite, Trivy filesystem scan, frontend typecheck /
  coverage / build / audit.
- **Already covered:** dependency/secret/misconfiguration scanning (Trivy, npm audit), module boundary
  rules (Spring Modulith verification, ArchUnit available transitively), frontend coverage (Vitest).
- **Not applicable now:** full automated DAST (`TD-QA-001`), mutation testing PIT/Pitest, OpenRewrite.
- **Registered as gradual debt:** `TD-BE-002` (SpotBugs, Find Security Bugs, PMD, PMD CPD, Checkstyle,
  Semgrep CE), `TD-BE-003` (JaCoCo backend coverage), `TD-BE-004` (CycloneDX SBOM, Maven Enforcer,
  License Maven Plugin, OWASP Dependency-Check), `TD-STACK-001` (major framework/runtime upgrades).

## Accepted Risks

- `AR-MOD-002-001` — DAST deferred; runnable surfaces validated by manual HTTP smoke (`TD-QA-001`).
- `AR-MOD-002-002` — Backend deep static analysis and coverage gate not yet configured; compensated by
  Trivy scans and a passing 42-test suite (`TD-BE-002`, `TD-BE-003`).

## Known Boundaries

- Patient and doctor portal catalog views are later read-only scope, not part of MVP-MOD-002.
- The mobile app surface is not required for the Diagnostic Catalog module.
- Release-readiness supply-chain gates (SBOM, license, DAST) remain outstanding as tracked debt.

## Decision

`MVP-MOD-002 Diagnostic Catalog` is **completed** and `MVP-MOD-002-CLOSEOUT` is **closed**. No critical
or high findings remain without accepted risk. The next active backlog item advances to
`MVP-MOD-003-DEF` (People and Clinical Master Data) per the HOP Commercial Product Backlog.
