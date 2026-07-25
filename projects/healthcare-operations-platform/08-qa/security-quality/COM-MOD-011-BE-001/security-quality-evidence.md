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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-COM-MOD-011-BE-001
  type: security-quality-evidence
  name: COM-MOD-011-BE-001 Security and Quality Evidence
  version: 1.0.0
  status: passed
  captured_on: 2026-07-21
scope: Backend implementation of the anonymous public-website surface for BCM-SVC-001/002/003/005,
  BCM-ATT-001, BCM-ATT-006 and BCM-PLT-005, plus real rate-limit enforcement for anonymous
  public traffic (materially closes TD-BE-015).
security_controls:
  authentication:
    public_paths_prefix: /api/public/
    authorization_bypass_mechanism: HopAuthorizationInterceptor is a no-op for paths
      not registered in EndpointPermissionRegistry. /api/public/** is deliberately
      not registered, so anonymous access is allowed only to published-only catalog
      reads and prospective-contact request intake endpoints.
    anonymous_traffic_isolation: Every public controller returns DTOs that omit tenantId,
      actorId, audit metadata and other privileged operational fields. Public appointment/quotation
      intake never carries a registered Patient link and never creates confirmed/issued
      records.
  rate_limiting:
    partner_classification:
      interceptor: PartnerApiKeyRateLimitInterceptor
      identity: X-Partner-Api-Key header
      status: continues_to_enforce_rn_004
    public_classification:
      interceptor: PublicApiRateLimitInterceptor
      identity: derived from RateLimitPolicy.consumerIdentificationMethod (ip_address
        via X-Forwarded-For|RemoteAddr, session_token via X-Public-Session-Token|
        requested session id)
      status: newly_enforced_by_com_mod_011_be_001
      rejection_response:
        http_status: 429
        code: PUBLIC_RATE_LIMIT_EXCEEDED
        message_key: public.error.public_rate_limit_exceeded
        body_shape:
          status: null
          code: null
          messageKey: null
          message: null
          occurredAt: null
      test_coverage:
        acceptance_test: PublicWebApiTest.publicRateLimitBlocksAnonymousTrafficByIpAddress
        rate_limit_semantics: fixed-window counter shared with PartnerApiRateLimiter
          under namespace prefix "public::" so partner and public buckets are isolated
  message_externalization:
    hardcoded_error_strings_added: 0
    new_i18n_keys:
    - public.error.public_rate_limit_exceeded
    - public.error.public_catalog_not_published
    - public.error.public_appointment_request_invalid
    - public.error.public_quotation_request_invalid
    - public.error.public_prospective_contact_required
    - public.error.public_channel_forbidden
    - public.rate_limit.identification_method_missing
    - public.rate_limit.window_size_seconds
    locales_covered:
    - es-MX
    - en-US
    - default
  input_validation:
    request_body_validation: jakarta.validation @Valid + @NotBlank on tenantId, laboratoryId,
      branchId and catalog item references
    published_only_enforcement:
      catalog_read: enforced in CatalogPublicReadAdapter (status == published) and
        each catalog service's listPublished()/getPublishedSnapshot()
      intake_lines: AppointmentSchedulingService.requestFromProspectiveContact and
        QuotationManagementService.startPublic reuse the existing published-catalog
        validation
    error_handler: PublicWebExceptionHandler maps every InvalidCommand/Conflict/NotFound
      to the shared public error envelope
  cross_module_boundaries:
    catalog_public_read_port: catalog-public-read-port named interface exposes only
      published-only snapshots as records; internal catalog service classes remain
      private to the catalogtestconfiguration module
    frontdesk_public_intake_port: public-intake-port named interface exposes only
      submit-appointment/ submit-quotation with a translated PublicIntakeException,
      isolating the publicweb module from internal frontdeskcaredelivery exception
      types
evidence_commands:
  backend_verify:
    command: mvn -f pom.xml clean verify -Pquality -Dhop.local-db-tests=true
    environment: compose.local.json with postgres:16-alpine reachable at localhost:5432
    result: BUILD SUCCESS in 49.561 s; 324 tests, 0 failures/errors/skipped
  jacoco_coverage:
    file: 07-implementation/backend/target/site/jacoco/index.html
    line_coverage: 83.96%
    line_covered_of_total: 9806/11679
    previous_baseline: 83.73%
    delta: +0.23 pp
  owasp_dependency_check:
    command: mvn org.owasp:dependency-check-maven:check -DautoUpdate=false
    dependencies: 108
    vulnerable: 0
    findings: 0
    report: 07-implementation/backend/target/dependency-check-report.html
  trivy_filesystem_scan:
    command: trivy fs --skip-dirs target --skip-dirs node_modules --scanners vuln,secret,misconfig
      --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL --exit-code 0 .
    version: 0.72.0
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
  yaml_parse:
    method: Python yaml.safe_load_all over every .yml/.yaml under the repository (excluding
      .git, node_modules, target, build, dist, coverage, .venv)
    files_parsed: 1154
    errors: 0
  agent_agnostic_scan:
    method: Python case-insensitive regex over source-only files under projects/healthcare-operations-platform
      (excluding .m2, target, node_modules, build, dist, coverage, .git)
    forbidden_patterns:
    - openai
    - claude
    - cursor
    - gemini
    - copilot
    total_matches: 27
    disposition:
    - 25 matches in QA evidence, security-quality evidence and steering YAML/MD documenting
      the agent-agnostic scan patterns themselves.
    - 1 match on the CSS `cursor: pointer` pseudo-property in employee-portal/src/styles.css.
    - 1 match in enterprise-foundation-alignment.md (steering scan-pattern reference).
    real_source_code_hits: 0
  secrets_scan:
    tool: trivy secret scanner (part of the fs scan above)
    findings: 0
  stale_pointer_sweep:
    method: Repository grep for TD-BE-015 blocking status, COM-MOD-011-BE-001 pointer
      values and pre-existing coverage baselines
    findings: 0 stale pointers (all updated by this backlog item's tracking edits)
  git_diff_check:
    command: git diff --check
    result: 0 whitespace errors (only Git LF/CRLF normalization warnings, exit code
      0)
closure:
  technical_debt_closed:
  - id: TD-BE-015
    status_before: materially_reduced
    status_after: closed
    evidence: PublicWebApiTest.publicRateLimitBlocksAnonymousTrafficByIpAddress plus
      RateLimitPolicy.consumerIdentificationMethod persisted end-to-end
  technical_debt_materially_reduced:
  - id: TD-I18N-002
    contribution: introduced the public.error.* and public.rate_limit.* i18n key namespace
      with parity across es-MX, en-US and the default bundle
```
