# MVP-MOD-008-BE-002 Validation — Integration Retry/Dead-Letter, API Deprecation/Rate-Limit and Migration Checkpoint Custom Rules

Status: **passed** · Module: MVP-MOD-008 Integration and Migration Readiness · Release: REL-001

Machine-readable source of truth: [MVP-MOD-008-BE-002-validation.yaml](MVP-MOD-008-BE-002-validation.yaml)

## Summary

MVP-MOD-008-BE-002 implements the three custom-rule hooks MVP-MOD-008-BE-001 deliberately left open
rather than compiling as lifecycle shells or endpoint stubs:

1. **Integration retry/dead-letter** (BCM-PLT-004, RN-004/RN-005): bounded exponential-backoff retry
   with a dead-letter transition, and a correlation id propagated across every retry.
2. **API deprecation/rate-limit** (BCM-PLT-005, RN-003/RN-004/RN-005): the deprecation-window-elapsed
   retirement transition, a working rate-limit enforcement interceptor for partner API keys, and a
   real audit gap fix.
3. **Migration checkpoints/retry/reconciliation** (BCM-PLT-010, RN-003/RN-004/RN-005): a real domain
   command port used as the sole write-boundary interaction point, checkpointed idempotent resume,
   and incremental post-import reconciliation reporting.

Debt-first: closed **TD-BE-013** (XLSX row-level parsing) with a real Apache POI implementation
before any feature work, as required by the backlog prompt's debt-first rule and this task's
explicit priority list.

## Debt-first action: TD-BE-013 closed

`ImportFileParser.countXlsxRows` now reads the first worksheet with Apache POI (`poi-ooxml`),
counting data rows and excluding the header row and any fully-blank trailing row — the same
header-skipping semantics `countCsvRows` already used. `MigrationManagementService.runDryRunValidation`
no longer emits a "row-level count not evaluated" warning for `.xlsx` files. Adding `poi-ooxml`
pulled in a `commons-io` version conflicting with `commons-csv`'s own transitive `commons-io`;
resolved with a `dependencyManagement` pin (2.20.0), the same technique already used in this
`pom.xml` for the `jackson-databind` CVE pin.

Two new debt items were honestly registered for the two real scope boundaries found during this
implementation, following the same disclosure convention BE-001 used for TD-BE-013 itself:

- **TD-BE-014** — `MigrationDomainCommandPort`'s only implementation is a local deterministic
  stand-in, not real cross-module command wiring, because no HOP business module yet exposes a
  migration-import application command.
- **TD-BE-015** — Rate-limit enforcement is scoped to requests carrying a partner API key; there is
  no request-path-to-`operationId` classification mapping yet for public/internal traffic.

## Integration retry/dead-letter (BCM-PLT-004)

- `IntegrationMessageRecord` gained `correlationId`, `nextRetryAt` and `deadLetterReason`.
- The first retry after an initial receive failure is immediately allowed; a retry that itself
  fails schedules a backoff window (30s → 120s → 300s → 900s → 1800s); retrying before that window
  elapses is rejected (`INTEGRATION_RETRY_NOT_YET_DUE`).
- After 5 failed attempts the message transitions to `dead_lettered` (previously an unused status
  constant) with a recorded reason; further retries are rejected
  (`INTEGRATION_MESSAGE_DEAD_LETTERED`).
- A deterministic `correlationId` (SHA-256 of `endpointId|externalMessageId`) is derived once and
  threaded through `IntegrationAdapterPort.acknowledgeMessage` on every attempt.
- `IntegrationMessageRecord.isReadyForDownstreamRouting()` makes the "only an acknowledged message
  may reach a domain module" invariant explicit and unit-tested.

## API deprecation/rate-limit (BCM-PLT-005)

- `ApiManagementService.retireDeprecatedOperation` completes the deprecation lifecycle, gated by
  the scheduled window having actually elapsed (`API_DEPRECATION_WINDOW_NOT_ELAPSED` otherwise).
- `PartnerApiRateLimiter` (fixed-window, one-minute counter) and
  `PartnerApiKeyRateLimitInterceptor` (a self-registering `WebMvcConfigurer` local to the
  `apimanagement` package, kept independent of `identityaccess`'s security wiring to respect Spring
  Modulith module boundaries) enforce `RateLimitPolicy` for any request carrying the
  `X-Partner-Api-Key` header.
- `setRateLimitPolicy` now records a `RateLimitPolicySet` audit event — a real RN-005 gap left by
  BE-001, since it previously never called `AuditRecorder` at all.

## Migration checkpoints/retry/reconciliation (BCM-PLT-010)

- New `MigrationDomainCommandPort` (mirroring `IntegrationAdapterPort`/`FiscalAdapterPort`) is the
  sole interaction point `commitImport`/`retryImportExecution` use to advance an import —
  `INV-MIG-003` (never write directly to a business aggregate) is satisfied by construction.
- `ImportExecution.checkpoint` is a real delimiter-joined list of completed entity categories;
  execution stops at the first failing category, and `retryImportExecution` resumes by skipping
  every already-completed category instead of re-invoking it.
- A bounded `MAX_EXECUTION_ATTEMPTS` (5) ceiling transitions the job to `failed` and rejects further
  retries (`MIGRATION_EXECUTION_ATTEMPTS_EXHAUSTED`) once exhausted.
- A `post_import` `ReconciliationReport` is written after every commit/retry attempt, incrementally
  reflecting exactly what that attempt completed or rejected. The job reaches `reconciled` once
  every category succeeds.

## Structured errors: `messageKey` added

Every BCM-PLT-004/005/010 error response now carries a `messageKey` (e.g.
`integration.error.integration_message_dead_lettered`) alongside the existing `code`, with
es-MX/en-US catalog entries registered for every code these two exception handlers use — further
reducing TD-I18N-002 beyond the `code`-only baseline BE-001 established.

## Quality gates

| Gate | Result |
|---|---|
| `mvn -Pquality "-Dhop.local-db-tests=true" clean verify` | passed — 265 tests, 0 failures/errors/skipped |
| JaCoCo line coverage | 80.49% (up from the 80.08% floor) |
| OWASP Dependency-Check | 0 vulnerabilities |
| Trivy (`vuln,secret,misconfig`) | 0 findings |
| PMD / SpotBugs / CPD | 354 / 23 / 1 — repo-wide, non-blocking, tracked by TD-BE-002 |
| Duplicate Finder | passed |
| Spotless | repo-wide pre-existing debt, unchanged (577+ files, not this backlog item's regression) |
| CycloneDX SBOM | generated |

Full detail, including per-test breakdown and exact business-rule mappings, is in the
[machine-readable evidence](MVP-MOD-008-BE-002-validation.yaml).

## Closure

MVP-MOD-008-BE-002 is **closed**. HOP is not commercially complete or GA-ready. Next backlog item:
**MVP-MOD-008-FE-001** (compile integration and migration administration UI outputs).
