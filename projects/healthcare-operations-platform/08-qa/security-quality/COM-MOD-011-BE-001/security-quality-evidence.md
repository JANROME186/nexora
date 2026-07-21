# COM-MOD-011-BE-001 Security and Quality Evidence

## Overview

Anonymous public-website surface for BCM-SVC-001/002/003/005, BCM-ATT-001, BCM-ATT-006 and BCM-PLT-005, plus real rate-limit enforcement for anonymous public traffic. Materially closes TD-BE-015 and further reduces TD-I18N-002. No new capability package, aggregate or database schema was created.

## Security controls

### Authentication and authorization boundary

- Public paths sit under `/api/public/**`.
- `HopAuthorizationInterceptor` treats paths not registered in `EndpointPermissionRegistry` as anonymous-allowed. `/api/public/**` is deliberately not registered, so only the explicit public-catalog reads and prospective-contact request-intake endpoints are reachable anonymously.
- Every public response DTO omits `tenantId`, `actorId`, audit metadata and other privileged operational fields.
- Public appointment intake never carries a registered Patient link (`patientId` is `null` for `channel = public_website`) and never creates a confirmed booking.
- Public quotation intake never issues, accepts, converts or prices a quotation.

### Rate-limit enforcement

- `PartnerApiKeyRateLimitInterceptor` continues to enforce RN-004 for partner-key requests (`X-Partner-Api-Key`).
- New `PublicApiRateLimitInterceptor` co-located in `integrationinteroperability/apimanagement/adapter/in/web/` enforces RN-007 for anonymous public requests under `/api/public/**`. Both interceptors are registered by `ApiManagementWebConfig`.
- Consumer identity is derived from `RateLimitPolicy.consumerIdentificationMethod`:
  - `ip_address`: `X-Forwarded-For` first entry, falling back to `RemoteAddr`.
  - `session_token`: `X-Public-Session-Token` header, falling back to the requested session id.
- The interceptor reuses `PartnerApiRateLimiter` under a `public::` namespace prefix so partner and public buckets cannot contaminate each other.
- Rejection produces an RFC7807-inspired 429 body: `{status, code=PUBLIC_RATE_LIMIT_EXCEEDED, messageKey=public.error.public_rate_limit_exceeded, message, occurredAt}`.
- Verified by `PublicWebApiTest.publicRateLimitBlocksAnonymousTrafficByIpAddress`.

### Message externalization

Every user-facing string routed through the public surface is a key against the shared i18n catalog (`public.error.*` and `public.rate_limit.*`). Added to `messages.properties`, `messages_es_MX.properties` and `messages_en_US.properties`:

- `public.error.public_rate_limit_exceeded`
- `public.error.public_catalog_not_published`
- `public.error.public_appointment_request_invalid`
- `public.error.public_quotation_request_invalid`
- `public.error.public_prospective_contact_required`
- `public.error.public_channel_forbidden`
- `public.rate_limit.identification_method_missing`
- `public.rate_limit.window_size_seconds`

No hardcoded error strings were introduced.

### Input validation

Every public request body carries `jakarta.validation` `@NotBlank` constraints on `tenantId/laboratoryId/branchId` and catalog item references. The published-only invariant is enforced at three points:

1. `CatalogPublicReadAdapter` filters/short-circuits on `status == published`.
2. `AppointmentSchedulingService.requestFromProspectiveContact` reuses the existing published-catalog validation for every requested item.
3. `QuotationManagementService.startPublic` reuses the existing published-catalog validation for every quotation line.

The shared `PublicWebExceptionHandler` maps `InvalidFrontDeskCommandException`, `FrontDeskConflictException` and `FrontDeskEntityNotFoundException` (via `PublicIntakeException`) into the public error envelope with an appropriate HTTP status.

### Cross-module boundaries

Two new named interfaces isolate the `publicweb` module:

- `catalogtestconfiguration::catalog-public-read-port` (`CatalogPublicReadPort` + `CatalogPublicReadAdapter`).
- `frontdeskcaredelivery::public-intake-port` (`PublicIntakePort` + `PublicIntakeAdapter`).

`PlatformFoundationModulithTest.moduleBoundariesAreValid` passes.

## Quality gates

| Gate | Result |
| --- | --- |
| Backend tests (`mvn clean verify -Pquality -Dhop.local-db-tests=true`) | 324 tests, 0 failures, 0 errors, 0 skipped |
| Backend line coverage (JaCoCo, clean rebuild) | 83.96% (9,806 / 11,679 lines) — above the 83.73% floor |
| Spring Modulith boundary check | 0 violations |
| Catalog/People/FrontDesk/PlatformFoundation contract tests | 3 tests, all passing |
| OWASP Dependency-Check | 108 dependencies scanned, 0 vulnerabilities |
| Trivy filesystem scan (v0.72.0, all severities, vuln+secret+misconfig) | 0 vulns, 0 secrets, 0 misconfigurations |
| YAML parse | 1,154 files, 0 errors |
| Agent-agnostic scan | 27 documentation/CSS mentions; **0** real agent-vendor references in source code |
| Secrets scan (Trivy) | 0 findings |
| Stale-pointer sweep | 0 stale pointers |
| `git diff --check` | 0 whitespace errors (LF/CRLF normalization warnings only) |

## Coverage preservation

- Employee portal: 88.24% (unchanged; not touched)
- Mobile TypeScript foundation: 99.21% (unchanged)
- Patient portal: 94.11% (unchanged)
- Doctor portal: 96.28% (unchanged)

## Technical debt

- **Closed**: TD-BE-015 (rate-limit enforcement scoped to partner-API-key-bearing requests only). Public-classification traffic now has an end-to-end enforcement path driven by `RateLimitPolicy.consumerIdentificationMethod`.
- **Materially reduced**: TD-I18N-002 (full message-catalog adoption). Added the `public.error.*` and `public.rate_limit.*` key namespaces.
