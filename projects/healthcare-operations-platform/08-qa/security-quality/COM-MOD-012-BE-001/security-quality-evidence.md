# COM-MOD-012-BE-001 Security and Quality Evidence

## Overview

Backend compilation of BCM-ORG-001 tenant provisioning/listing/status-transition operations, the new BCM-PLT-002 Platform Configuration and Feature Flags module, and BCM-PLT-006 observability extensions (Prometheus metrics, liveness/readiness health groups, tenant/user/trace MDC logging context). Materially reduces TD-IAM-002 and TD-DB-004; further reduces TD-I18N-002. Registers TD-BE-016, TD-BE-017 and TD-IAM-003 for honestly-scoped deferred capability-package extensions (BCM-PLT-001/005/007/008/009 were not fully implemented — see the QA validation evidence for the scoping rationale).

## Security controls

### Authorization

- All new/extended endpoints sit under `/api/platform/**`, already enforced by `HopAuthorizationInterceptor`.
- Two new `EndpointPermissionRegistry` entries (`/api/platform/config`, `/api/platform/feature-flags`) map to a new `PermissionCode.SCREEN_PLATFORM_CONFIGURATION`, granted to `ADMIN` only via the existing deny-by-default `RolePermissionCatalog`.
- `listTenants` and `updateTenantStatus` automatically inherit the pre-existing `/api/platform/tenants` prefix rule; no registry change was needed.

### Privileged-operation audit

- `updateTenantStatus` → `TenantStatusChanged` audit event (previous/new status, reason).
- `updateFeatureFlag` → `FeatureFlagUpdated` audit event (enabledByDefault, rolloutPercentage, updatedBy).
- Both reuse the existing BCM-PLT-007 `AuditRecorder` — the PF-BE-004 mechanism, not a new one.

### Header input hardening (real finding, fixed)

SpotBugs/FindSecBugs flagged `SERVLET_HEADER` (CWE-807) on the new `RequestObservabilityContextFilter`, which reads `X-HOP-TENANT-ID`, `X-HOP-USER-ID` and `traceparent` request headers to populate logging context. Two concrete risks were identified and closed in code, not suppressed:

1. **Log injection**: a header value containing CR/LF could forge extra log lines. `sanitizeForLogging()` strips every control character and bounds the value to 120 characters before it reaches MDC.
2. **Header reflection**: the `traceparent` trace-id segment was previously echoed back verbatim in the `X-Trace-Id` response header. `resolveTraceId()` now only reuses it when it matches the strict W3C 32-lowercase-hex format; anything else is discarded and a fresh id is minted.

Both are covered by dedicated tests (`stripsControlCharactersFromTenantAndUserHeadersBeforeLogging`, `rejectsMalformedTraceparentAndMintsANewTraceIdInstead`). SpotBugs still reports the rule at low priority (rank 15) because it fires on any direct header read — the identical rule already fires on the pre-existing, accepted `HopAuthenticationResolver` — so this is a residual, non-blocking, already-accepted class of finding, not a new risk.

### Message externalization

New i18n keys added to `messages.properties`, `messages_es_MX.properties`, `messages_en_US.properties`:

- `organizationmanagement.error.entity_not_found`
- `organizationmanagement.error.organization_command_invalid`
- `organizationmanagement.error.tenant_code_conflict`
- `platformconfiguration.error.command_invalid`

No hardcoded error strings were introduced by this backlog item's new code paths.

### Input validation

- `jakarta.validation @NotBlank` on the new request DTOs' required fields.
- Tenant `code` uniqueness enforced by an application-level pre-check (`findTenantByCode`) plus a database unique index (`tenants_code_key`) as a defense-in-depth backstop against a race condition.
- Feature flag `flagKey` validated against the `platform.<area>.<name>` namespace regex; `rolloutPercentage` bounded to 0-100.
- Tenant `status` validated against the four-value enum.
- `OrganizationManagementExceptionHandler` and `PlatformConfigurationExceptionHandler` map every invalid-command/not-found/conflict exception to a structured `{status, code, messageKey, message, occurredAt}` envelope.

### Cross-module boundaries

`platformconfiguration`'s `package-info.java` declares `allowedDependencies=[auditcompliance]` only — it does not reach into `organizationmanagement` or `identityaccess` internals; tenant scoping for `evaluateFeatureFlags` is a caller-supplied request parameter, not a cross-module security-context read. `PlatformFoundationModulithTest.moduleBoundariesAreValid` passes with the new module and the two extended modules.

## Quality gates

| Gate | Result |
| --- | --- |
| Backend tests (`mvn -Pquality clean verify -Dhop.local-db-tests=true`) | 362 tests, 0 failures, 0 errors, 0 skipped |
| Backend line coverage (JaCoCo) | 84.11% (10,039 / 11,935 lines) — above the 83.99% floor (+0.12pp) |
| Checkstyle | 22 findings, 0 in new/touched files |
| PMD/CPD | 480 findings / 2 duplications; 6 in touched files, all pre-existing accepted patterns |
| SpotBugs/FindSecBugs | 35 findings; 2 new (`SERVLET_HEADER`, low priority, mitigated in code) |
| Duplicate-finder | passed, no new classpath conflicts |
| SBOM (CycloneDX) | 110 components |
| Spring Modulith boundary check | 0 violations |
| OWASP Dependency-Check | 115 dependencies scanned, 0 vulnerabilities |
| Trivy filesystem scan (v0.72.0, all severities, vuln+secret+misconfig) | 0 vulns, 0 secrets, 0 misconfigurations |
| YAML parse | 1,248 files, 0 errors |
| Agent-agnostic scan | 70 documentation/CSS mentions; **0** real agent-vendor references in source code |
| Secrets scan (Trivy) | 0 findings |
| Stale-pointer sweep | 0 stale pointers |
| `git diff --check` | 0 whitespace errors (LF/CRLF normalization warnings only) |

## Technical debt

- **Materially reduced**: TD-IAM-002 (permission granularity — two new endpoint-level registry entries), TD-DB-004 (tenant isolation — `isolationStrategy` field persisted, real suspend/archive containment control added to `tenant-impact-triage-runbook.yaml`), TD-I18N-002 (message externalization — two new i18n namespaces).
- **Registered (new, honestly-scoped deferrals)**: TD-BE-016 (BCM-PLT-007 search/export not fully compiled, low risk), TD-BE-017 (BCM-PLT-009 Workflow Engine not implemented, medium risk), TD-IAM-003 (BCM-PLT-001 MFA/service-account/scope-grammar not implemented, low risk).
