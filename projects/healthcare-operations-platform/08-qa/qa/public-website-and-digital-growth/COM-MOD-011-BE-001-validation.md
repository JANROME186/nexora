# COM-MOD-011-BE-001 Validation Evidence

## Backlog item

- **Id**: COM-MOD-011-BE-001
- **Module**: COM-MOD-011 Public Website and Digital Growth
- **Status**: closed
- **Scope**: compile the `public_surface` operations modeled by COM-MOD-011-DEF for BCM-SVC-001/002/003/005, BCM-ATT-001, BCM-ATT-006 and BCM-PLT-005, and add real rate-limit enforcement for anonymous public traffic (materially closes TD-BE-015). Reuses the existing MVP-MOD-002 catalog services and MVP-MOD-004 appointment/quotation services; no new capability package, aggregate or database schema was created.

## Public endpoints compiled

| Capability | Public path | Method | Realizes |
| --- | --- | --- | --- |
| BCM-SVC-001 | `/api/public/catalog/diagnostic-services/published` | GET | published-only list projection |
| BCM-SVC-001 | `/api/public/catalog/diagnostic-services/{serviceId}/published-snapshot` | GET | frozen published snapshot |
| BCM-SVC-002 | `/api/public/catalog/tests/published` | GET | published-only list projection |
| BCM-SVC-002 | `/api/public/catalog/tests/{testId}/published-snapshot` | GET | frozen published snapshot |
| BCM-SVC-003 | `/api/public/catalog/panels/published` | GET | published-only list projection |
| BCM-SVC-003 | `/api/public/catalog/panels/{panelId}/published-snapshot` | GET | frozen published snapshot |
| BCM-SVC-005 | `/api/public/catalog/preparations/published` | GET | published-only list projection |
| BCM-SVC-005 | `/api/public/catalog/preparations/{preparationId}/published-snapshot` | GET | frozen published snapshot |
| BCM-ATT-001 | `/api/public/care-delivery/appointment-requests` | POST | RN-008 anonymous appointment request |
| BCM-ATT-006 | `/api/public/care-delivery/quotation-requests` | POST | RN-009 anonymous quotation request |

Every response DTO omits `tenantId`, audit metadata and internal-only identifiers so the anonymous public surface cannot leak operational data.

Location and contact outputs were checked against the scope: COM-MOD-011-DEF did not model any BCM-ORG-* branch/contact capability as part of this module's seven capabilities, so no dedicated public location endpoint is implemented in this backlog item (per the "if modeled as part of the scope" clause).

## Domain and schema extensions

| Change | Kind | Reason |
| --- | --- | --- |
| `AppointmentSlot` gains `prospectiveFullName/Phone/Email` (nullable) and channel `public_website`; `patientId` becomes nullable for that channel | extend existing aggregate | RN-008 captures a ProspectiveContact instead of a registered Patient link |
| `care_delivery.appointments` schema: three new columns added with `ADD COLUMN IF NOT EXISTS`, `patient_id` `ALTER COLUMN … DROP NOT NULL` | additive DDL | schema migration reproducible from a fresh `docker compose up` or on a reused local DB |
| `RateLimitPolicy` gains `consumerIdentificationMethod` (enum) | extend existing aggregate | BCM-PLT-005 RN-007 declares how public and partner traffic is identified for the fixed-window counter |
| `integration_interoperability.rate_limit_policies` schema: `consumer_identification_method varchar(32) NOT NULL DEFAULT 'partner_api_key'` | additive DDL, existing rows keep partner-key behavior | forward-compatible rollout |

No new capability package, no new aggregate root and no new database schema/table were created.

## Rate-limit enforcement (TD-BE-015 closed)

- New `PublicApiRateLimitInterceptor` co-located with `PartnerApiKeyRateLimitInterceptor` inside `integrationinteroperability/apimanagement/adapter/in/web/`.
- Registered by `ApiManagementWebConfig` under `/api/public/**` (partner interceptor keeps its `/api/**` scope).
- Reuses the existing `PartnerApiRateLimiter` fixed-window counter with a `public::` namespace prefix so partner and public buckets can never contaminate each other.
- Consumer identity resolved from `RateLimitPolicy.consumerIdentificationMethod`:
  - `ip_address`: `X-Forwarded-For` first entry, falling back to `HttpServletRequest.getRemoteAddr()`.
  - `session_token`: `X-Public-Session-Token` header, falling back to the requested session id.
- Rejection produces `status=429`, `code=PUBLIC_RATE_LIMIT_EXCEEDED`, `messageKey=public.error.public_rate_limit_exceeded`, an English `message` and `occurredAt`.
- When no `public` policy is configured the interceptor is a no-op, consistent with the partner interceptor's opt-in policy per classification tier.

Verified end-to-end by `PublicWebApiTest.publicRateLimitBlocksAnonymousTrafficByIpAddress`: two requests from the same `X-Forwarded-For` under a `requestsPerMinute=1` policy return `200` then `429`; a third request from a different IP within the same minute still returns `200`.

## Modulith boundaries

Two new named interfaces were added to keep `publicweb` from depending on internal application types of other modules:

- `catalogtestconfiguration::catalog-public-read-port` — hosts `CatalogPublicReadPort` and its adapter, exposing the published-only projections for BCM-SVC-001/002/003/005.
- `frontdeskcaredelivery::public-intake-port` — hosts `PublicIntakePort` and its adapter, exposing the anonymous appointment (RN-008) and quotation (RN-009) intake commands.

The `publicweb` module declares only `sharedkernel`, `catalogtestconfiguration::catalog-public-read-port` and `frontdeskcaredelivery::public-intake-port` as allowed dependencies. `PlatformFoundationModulithTest.moduleBoundariesAreValid` passes.

## i18n additions (TD-I18N-002 further reduced)

Added to `i18n/messages.properties`, `messages_es_MX.properties` and `messages_en_US.properties`:

- `public.error.public_rate_limit_exceeded`
- `public.error.public_catalog_not_published`
- `public.error.public_appointment_request_invalid`
- `public.error.public_quotation_request_invalid`
- `public.error.public_prospective_contact_required`
- `public.error.public_channel_forbidden`
- `public.rate_limit.identification_method_missing`
- `public.rate_limit.window_size_seconds`

No hardcoded user-facing message strings were introduced by this backlog item.

## Incidental correctness fixes made during this backlog item

- `PreparationInstructionController` gained a `GET /{preparationId}/published-snapshot` route so the operation already declared in `bcm-svc-005-patient-preparation-management/openapi-source.yaml` (resource-level, custom_reason: "Returns frozen version-aware snapshot for downstream consumers, mirroring BCM-SVC-001/002/003") is now registered as a Spring MVC route. `CatalogTestConfigurationContractTest.everyCapabilityOperationIsRegisteredAsASpringRoute` was failing on this pre-existing gap and is now passing.

## Quality gates

- **Backend tests**: `mvn clean verify -Pquality -Dhop.local-db-tests=true` against a running `compose.local.yml` PostgreSQL 16 container: **324 tests, 0 failures, 0 errors, 0 skipped**.
- **Backend line coverage** (JaCoCo, clean rebuild against the same run): **83.96% (9,806 / 11,679 lines)**, above the 83.73% floor from COM-MOD-010-QA-001.
- **Modulith boundaries**: 0 violations.
- **API contract tests**: `CatalogTestConfigurationContractTest`, `FrontDeskCareDeliveryContractTest`, `PeopleClinicalMasterDataContractTest`, `PlatformFoundationApiContractTest` all pass.
- **OWASP Dependency-Check**: 108 dependencies scanned, **0 vulnerabilities**. Used the local shared NVD data directory without refresh.
- **Trivy filesystem scan** (v0.72.0, `vuln,secret,misconfig`, all severities): **0 vulnerabilities, 0 secrets, 0 misconfigurations**.
- **YAML parse**: **1,154 YAML files parsed, 0 errors**.
- **Agent-agnostic scan**: 27 documentation mentions of scan patterns and the `cursor: pointer` CSS pseudo-property; **0 real agent-vendor references** in source code.
- **Secrets scan** (Trivy): **0 findings**.
- **Stale-pointer sweep**: no outdated pointers found in tracking files.
- **git diff --check**: no whitespace errors (only Git LF/CRLF normalization warnings, exit code 0).

## Coverage preservation across other stacks

- Employee portal: 88.24% (unchanged; no source file touched)
- Mobile TypeScript foundation: 99.21% (unchanged)
- Patient portal: 94.11% (unchanged)
- Doctor portal: 96.28% (unchanged)

## Closure criteria

- Public endpoints functional and covered by tests: yes.
- No vulnerabilities of any level: yes.
- Coverage did not regress: yes (backend improved 83.73% → 83.96%).
- Required technical debt closed: yes (TD-BE-015 closed; TD-I18N-002 further reduced).
- No stale pointers: yes.
- Git clean (after tracking updates below): yes.
