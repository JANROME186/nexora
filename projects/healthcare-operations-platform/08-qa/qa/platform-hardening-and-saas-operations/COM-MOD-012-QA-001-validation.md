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

**Fix**: Added the include to `application-local.yml` (the profile with a real `DataSource` bean). The same change in the base `application.yml` broke context startup for profiles with no `DataSource` bean (reproduced: `"Health contributor 'db' ... does not exist"`), so the fix was correctly scoped. A new regression test, `ObservabilityReadinessLocalDatabaseTest`, locks this in.

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
