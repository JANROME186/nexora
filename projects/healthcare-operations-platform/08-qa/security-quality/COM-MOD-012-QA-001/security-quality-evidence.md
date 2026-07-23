# COM-MOD-012-QA-001 Security and Quality Evidence

**Artifact ID**: HOP-SQ-COM-MOD-012-QA-001
**Status**: Passed
**Date**: 2026-07-23

---

## Scope

Integrated performance, resilience and security validation of COM-MOD-012 Platform Hardening and SaaS Operations: `BCM-ORG-001` tenant operations, `BCM-PLT-002` platform configuration and feature flags, `BCM-PLT-006` observability, audit trail evidence, and the 8-capability traceability/runbook set. Includes a dedicated OWASP ZAP DAST pass against the full backend API surface (deferred by `COM-MOD-012-BE-001`) and the employee portal, plus a live backup/restore rehearsal.

---

## Open Source First Check

No new dependency was added by this backlog item. All existing dependencies re-scanned clean (OWASP Dependency-Check: 115 dependencies, 0 vulnerable; Trivy fs: 0 vulnerabilities/secrets/misconfigurations).

---

## Security Controls

### Resilience: Readiness Probe Fix
`management.endpoint.health.group.readiness.include` was unset, so the default readiness group only contained `readinessState`, not the `db` `HealthIndicator`. Readiness reported `UP` even with PostgreSQL stopped — an orchestrator would keep routing traffic to a pod with a dead database connection.

**Fix**: Added the include to `application-local.yml` only (the profile with a real `DataSource` bean). The identical change in the base `application.yml` broke application-context startup for profiles with no `DataSource` bean, confirmed by reproducing `"Health contributor 'db' ... does not exist"`.

**Verified live**: A real `docker stop`/`start` of `hop-local-postgres` confirmed readiness correctly flips to `DOWN`/503 and back to `UP`/200; liveness correctly stays `UP` throughout. New regression test: `ObservabilityReadinessLocalDatabaseTest`.

### DAST Findings Fixed

**`TD-QA-005`** — A null byte (SQLState `22021`) or an oversized string value (SQLState `22001`) reaching a JDBC statement caused an unhandled 500 on 6 `laboratoryworkflow` worklist endpoints and `POST /api/revenue/cashier/sessions`. No stack trace or internal path was ever exposed to the client in either case (verified: the response body was always the standard Spring Boot default error JSON).

Fix: `GlobalExceptionHandler.handleDataIntegrityViolationException()` walks the exception's cause chain and remaps to 400 only when the root `SQLException`'s SQLState is in a small, explicitly-scoped set (`22021`, `22001`). Every other `DataIntegrityViolationException` cause (e.g. a real `23505` unique-constraint conflict) is deliberately rethrown unchanged — no behavior change to existing conflict handling elsewhere in the codebase.

Regression tests: `mapsInvalidByteSequenceDataIntegrityViolationToBadRequestBody`, `mapsStringDataRightTruncationDataIntegrityViolationToBadRequestBody`, `rethrowsOtherDataIntegrityViolationCausesUnchanged`.

**`TD-QA-006`** — `POST /api/auth/assistance` returned an unhandled 500 for a nonexistent `assistedUserId` instead of 404, because `IdentityAccessExceptionHandler`'s `@RestControllerAdvice` was scoped (`assignableTypes`) to only `IdentityAccessController`, excluding `AuthController` even though both live in the `identityaccess` module and can both throw `IdentityEntityNotFoundException`.

Fix: Widened `assignableTypes` to `{ IdentityAccessController.class, AuthController.class }`. No new exception-handling logic was added; the existing, already-tested mapping now also covers the controller it was missing.

Regression test: `AuthControllerTest.assistanceForANonexistentAssistedUserReturnsNotFoundInsteadOfServerError`.

### Audit Trail Verification
- `TenantStatusChanged` audit event confirmed live via `GET /api/audit/events`, carrying `previousStatus`/`newStatus`/`reason` metadata.
- `FeatureFlagUpdated` audit event confirmed live, carrying `enabledByDefault`/`rolloutPercentage`/`updatedBy` metadata.

### Header Hardening Re-verification
- Control-character/log-injection attempt via `X-HOP-TENANT-ID` confirmed sanitized; no forged log line appeared.
- A valid W3C `traceparent`'s trace-id segment was correctly reused in `X-Trace-Id`; a malformed `traceparent` was correctly discarded and a fresh id minted, confirming no attacker-controlled value is reflected.

---

## Scans and Reports

| Tool | Status | Detail |
|---|---|---|
| Backend Maven Quality | Passed | 367 tests, 84.14% coverage (floor 84.11%) |
| OWASP Dependency-Check | Passed | 115 deps, 0 vulnerabilities |
| Trivy fs (backend) | Passed | 0 vulns/secrets/misconfigs |
| Trivy fs (repo-wide) | Passed | 0 vulns/secrets/misconfigs |
| OWASP ZAP API scan | Passed | Full `/v3/api-docs` (353 URLs); 0 FAIL-NEW, 0 WARN-NEW after fix (3 before), 0 SQLi/XSS/RCE/path-traversal/SSRF |
| OWASP ZAP baseline scan | Passed | Employee portal (unchanged); 0 FAIL-NEW, 4 WARN-NEW matching existing `TD-FE-005` |
| YAML Parse | Passed | 1,256 files, 0 errors |
| Agent-Agnostic Scan | Passed | 0 real hits |
| Secrets Scan | Passed | 0 findings |
| Git Diff Check | Passed | 0 whitespace errors |

---

## Decision

**Status**: Approved. Ready for closeout. Next backlog item: `COM-MOD-012-CLOSEOUT`.
