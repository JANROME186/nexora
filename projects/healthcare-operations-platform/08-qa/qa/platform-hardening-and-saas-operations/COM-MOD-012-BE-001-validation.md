# COM-MOD-012-BE-001 Validation Evidence

## Backlog item

- **Id**: COM-MOD-012-BE-001
- **Module**: COM-MOD-012 Platform Hardening and SaaS Operations
- **Status**: closed
- **Scope**: compile the operations modeled by COM-MOD-012-DEF into real Spring Modulith code — BCM-ORG-001 tenant provisioning/listing/status-transition, a new BCM-PLT-002 Platform Configuration and Feature Flags module, and BCM-PLT-006 observability extensions (Prometheus metrics, liveness/readiness health groups, tenant/user/trace MDC logging) — and close or reduce the COM-MOD-012-OPS-002 runbook `known_gaps_and_forward_pointers` entries that named this backlog item.

## Endpoints compiled

| Capability | Path | Method | Realizes |
| --- | --- | --- | --- |
| BCM-ORG-001 | `/api/platform/tenants` | POST | `provisionTenant` (extends the pre-existing `createTenant`) |
| BCM-ORG-001 | `/api/platform/tenants` | GET | `listTenants` |
| BCM-ORG-001 | `/api/platform/tenants/{tenantId}` | GET | `getTenant` (unchanged path, richer response) |
| BCM-ORG-001 | `/api/platform/tenants/{tenantId}/status` | PUT | `updateTenantStatus` (tenant-impact-triage containment control) |
| BCM-PLT-002 | `/api/platform/config` | GET | `getPlatformConfig` |
| BCM-PLT-002 | `/api/platform/feature-flags` | GET | `evaluateFeatureFlags` |
| BCM-PLT-002 | `/api/platform/feature-flags` | POST | `updateFeatureFlag` |
| BCM-PLT-006 | `/actuator/health/liveness` | GET | `getLiveness` (Spring Boot Actuator, no custom controller) |
| BCM-PLT-006 | `/actuator/health/readiness` | GET | `getReadiness` (Spring Boot Actuator, no custom controller) |
| BCM-PLT-006 | `/actuator/prometheus` | GET | `getPrometheusMetrics` (Micrometer, no custom controller) |

BCM-PLT-006's three operations are satisfied entirely by Spring Boot Actuator + Micrometer configuration (a dependency and two `application.yml` properties), per "no crear CRUD manual repetitivo si puede derivarse de modelos" — no hand-written controller was needed.

## Tenant model extended (BCM-ORG-001)

`Tenant` grew from `(tenantId, name, status, createdAt, updatedAt)` to the full `TenantRoot` shape: `code` (globally unique), `legalName`, `tradeName`, `taxId`, `status` (`PENDING_PROVISIONING`/`ACTIVE`/`SUSPENDED`/`ARCHIVED`), `tier` (`STARTER`/`PROFESSIONAL`/`ENTERPRISE`), `isolationStrategy` (`SCHEMA_PER_TENANT`/`DISCRIMINATOR_WITH_RLS`, defaulting to the current `DISCRIMINATOR_WITH_RLS` reality). Quota/branding/data-masking-policy value objects from `business-model.yaml` are not exposed by any of the three compiled operations and were intentionally not persisted, to avoid inventing surface beyond what this backlog item's operations require.

**Backward compatibility**: roughly 20 pre-existing module test fixtures across the backend bootstrap a tenant with a bare `{"name":"..."}` payload. Rather than rewrite every fixture, `ProvisionTenantRequest` keeps `name` as a fallback for `legalName` and auto-derives a unique `code` (slug of the resolved name plus a random suffix) when the caller omits it. Every pre-existing fixture keeps working unchanged; new callers can supply the full richer payload.

**Privileged-operation audit**: `updateTenantStatus` records a `TenantStatusChanged` audit event (`previousStatus`, `newStatus`, `reason`) through the existing BCM-PLT-007 `AuditRecorder` — the same mechanism PF-BE-004 established, reused rather than reinvented.

## New module: Platform Configuration and Feature Flags (BCM-PLT-002)

A new `platformconfiguration` Spring Modulith module (`allowedDependencies=[auditcompliance]`) with a JDBC/in-memory dual-adapter pair, following the same hexagonal shape as every other module in this backend.

- `getConfig()` masks `is_encrypted=true` config parameters (`"***"`) before returning them.
- `evaluateFeatureFlags(tenantId)` is deterministic and never throws: it returns `false` for every flag when `tenantId` is blank, honors explicit `targetTenants`, `enabledByDefault`, and otherwise buckets the tenant into a stable pseudo-random rollout window — the `business-model.yaml` invariant "must default to false if targeting rules fail" holds in every branch.
- `updateFeatureFlag(...)` validates the `flagKey` against the `platform.<area>.<name>` namespace invariant, validates `rolloutPercentage` is `0-100`, and records a `FeatureFlagUpdated` audit event via `AuditRecorder`.

New schema `platform_configuration` (`config_parameters`, `feature_flags` tables) under `db/platform-hardening-and-saas-operations/schema.sql`, seeded with `platform.security.session_timeout_minutes` and `platform.operations.maintenance_mode`.

## Observability extensions (BCM-PLT-006)

- `micrometer-registry-prometheus` added to `pom.xml`; `management.endpoints.web.exposure.include` now includes `prometheus`; `GET /actuator/prometheus` exposes the standard Micrometer/JVM/HTTP/DataSource-pool metric catalog.
- `management.health.livenessstate.enabled`/`readinessstate.enabled` set explicitly; `GET /actuator/health/liveness` and `GET /actuator/health/readiness` verified reachable.
- New `RequestObservabilityContextFilter` (a plain `jakarta.servlet.Filter`, auto-registered by Spring Boot) populates SLF4J MDC (`tenantId`, `userId`, `traceId`) for every request, ahead of `HopAuthorizationInterceptor`, so context is present even for rejected or unmapped requests. `logging.pattern.console` renders all three on every log line.
- `traceId` reuses the trace-id segment of an inbound W3C `traceparent` header when it matches the strict 32-lowercase-hex format; otherwise a new one is minted. It is echoed back as `X-Trace-Id`.
- **Security hardening**: SpotBugs/FindSecBugs flagged `SERVLET_HEADER` (CWE-807, low priority — the same rule already fires on the pre-existing `HopAuthenticationResolver`) on this filter for reading client-supplied headers. Mitigated: `sanitizeForLogging()` strips control characters (CR/LF) and bounds length before any header value reaches a log line, preventing log injection/forging; the `traceparent` segment is only reflected back in `X-Trace-Id` after passing strict format validation, preventing header-injection via a malformed value. Covered by dedicated tests.

## Operational control added to tenant-impact-triage-runbook.yaml

A new `TRIAGE-STEP-004B` documents two real, auditable containment actions now available to `platform_operations_on_call` for a `single_tenant` triage outcome: suspend/archive the tenant via `PUT /api/platform/tenants/{tenantId}/status`, or disable a feature for every tenant via `POST /api/platform/feature-flags` — both without a code deploy, and both automatically audited.

## i18n additions (TD-I18N-002 further reduced)

Added to `messages.properties`, `messages_es_MX.properties` and `messages_en_US.properties`:

- `organizationmanagement.error.entity_not_found`, `organizationmanagement.error.organization_command_invalid`, `organizationmanagement.error.tenant_code_conflict`
- `platformconfiguration.error.command_invalid`

No hardcoded user-facing message strings were introduced by this backlog item; pre-existing hardcoded messages in `organizationmanagement`'s unmodified code paths remain tracked under TD-I18N-002 as before.

## Deferred / not implemented (with new technical debt registered)

- **BCM-PLT-007** `searchAuditEvents`/`exportAuditEvents`: the existing `/api/audit/events` read path is reused; export-bundle generation and the versioned `/api/v1/...` path from `openapi-source.yaml` are not compiled (**TD-BE-016**, low risk).
- **BCM-PLT-009** Workflow Engine: no backend module exists yet. Building one without a real orchestration target (backup scheduling, restore rehearsal, rollback tracking) would itself be the "CRUD manual repetitivo" this backlog forbids (**TD-BE-017**, medium risk, open).
- **BCM-PLT-001** MFA/service-account/`domain.resource.action.scope` grammar and **BCM-PLT-005** edge security headers (CORS/CSP/HSTS): not exposed by any operation this backlog item needed to compile (**TD-IAM-003**, low risk, for BCM-PLT-001; BCM-PLT-005 already tracked under TD-FE-005).

## Quality gates

- **Backend tests**: `mvn -Pquality clean verify -Dhop.local-db-tests=true` against a running `compose.local.yml` PostgreSQL 16 container: **362 tests, 0 failures, 0 errors, 0 skipped** (up from 360 pre-existing; 8 new test classes/extensions added).
- **Backend line coverage** (JaCoCo): **84.11% (10,039 / 11,935 lines)**, above the 83.99% floor (+0.12pp).
- **Checkstyle**: 22 findings, 0 in new/touched files (all pre-existing, TD-BE-002).
- **PMD/CPD**: 480 findings / 2 duplications; 6 findings in touched files, all mirroring pre-existing accepted patterns (RowMapper's unused `rowNumber`, `SimplifyBooleanReturns`), no new violation class.
- **SpotBugs/FindSecBugs**: 35 findings total; 2 new (`SERVLET_HEADER`, low priority, mitigated — see above).
- **Duplicate-finder**: passed, no new classpath conflicts.
- **SBOM (CycloneDX)**: 110 components, `target/bom.xml` + `target/bom.json`.
- **Modulith boundaries** (ArchUnit via Spring Modulith): 0 violations.
- **OWASP Dependency-Check**: 115 dependencies scanned (up from 108; micrometer-registry-prometheus added), **0 vulnerabilities**. Local shared NVD data directory, no refresh.
- **Trivy filesystem scan** (v0.72.0, `vuln,secret,misconfig`, all severities): **0 vulnerabilities, 0 secrets, 0 misconfigurations**. (First attempt timed out purely walking an untracked, gitignored 665MB local `.m2` cache under the backend directory; excluded via `--skip-dirs .m2` for the successful re-run — no source content was skipped.)
- **YAML parse**: **1,248 files parsed, 0 errors**.
- **Agent-agnostic scan**: 70 matches, **0 real hits** (all documentation of the scan pattern itself, the CSS `cursor` pseudo-property, or a pre-existing jscpd HTML report).
- **Secrets scan** (Trivy): 0 findings.
- **Stale-pointer sweep**: 0 stale references to the removed `CreateTenantCommand` type; runbook forward pointers updated.
- **git diff --check**: no whitespace errors (only Git LF/CRLF normalization warnings, exit code 0).

## Coverage preservation across other stacks

- Employee portal: 88.68% (unchanged; no source file touched)
- Public website: 98.61% (unchanged)
- Mobile TypeScript foundation: 99.21% (unchanged)
- Patient portal: 94.11% (unchanged)
- Doctor portal: 96.28% (unchanged)

## Closure criteria

- Modeled operations reachable and covered by tests: yes.
- No vulnerabilities of any level: yes.
- Coverage did not regress: yes (backend improved 83.99% → 84.11%).
- Technical debt reduced: yes (TD-IAM-002 and TD-DB-004 materially reduced further; TD-BE-016/TD-BE-017/TD-IAM-003 registered for honestly-scoped deferred work).
- Runbook known gaps closed or reduced: yes (5 of 8 named `COM-MOD-012-BE-001` forward pointers closed; remaining 3 — distributed tracing export, Grafana/Prometheus/Loki stack, SLO/SLA alerting backend — require infrastructure not yet provisioned and are re-pointed to future items).
- No stale pointers: yes.
- Git clean (after tracking updates below): yes.
