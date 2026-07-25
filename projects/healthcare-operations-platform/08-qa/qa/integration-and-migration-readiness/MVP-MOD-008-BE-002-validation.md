# MVP-MOD-008-BE-002 Validation — Integration Retry/Dead-Letter, API Deprecation/Rate-Limit and Migration Checkpoint Custom Rules

Status: **passed** · Module: MVP-MOD-008 Integration and Migration Readiness · Release: REL-001

Machine-readable source of truth: [MVP-MOD-008-BE-002-validation.md](MVP-MOD-008-BE-002-validation.md)

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
[machine-readable evidence](MVP-MOD-008-BE-002-validation.md).

## Closure

MVP-MOD-008-BE-002 is **closed**. HOP is not commercially complete or GA-ready. Next backlog item:
**MVP-MOD-008-FE-001** (compile integration and migration administration UI outputs).

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-MVP-MOD-008-BE-002
  type: qa-validation-evidence
  name: MVP-MOD-008-BE-002 Integration Retry/Dead-Letter, API Deprecation/Rate-Limit
    and Migration Checkpoint Custom Rules Validation
  version: 1.0.0
  status: passed
  human_readable: MVP-MOD-008-BE-002-validation.md
  machine_readable: MVP-MOD-008-BE-002-validation.md
  created_date: 2026-07-19
  owner: Nexora Backend Engineering Team
scope:
  backlog_item: MVP-MOD-008-BE-002
  module: MVP-MOD-008 Integration and Migration Readiness
  release: REL-001
  execution_flow_stage: implement_rules
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  code_implemented: true
  working_directory: projects/healthcare-operations-platform/07-implementation/backend
preflight:
  purpose: Loaded PROJECT_STATE.md, SOURCE_OF_TRUTH.md (root and project), the
    HOP commercial product backlog/execution prompts, technical-debt-index.md, the
    three MVP-MOD-008 capability packages (bcm-plt-004, bcm-plt-005, bcm-plt-010)
    and their business-rules.md/generation-plan.md, plus MVP-MOD-008-DEF-validation.md,
    MVP-MOD-008-BE-001-validation.md and its security-quality evidence, before implementing
    any custom rule. Mapped every explicit MVP-MOD-008-BE-002 refinement hook named
    by BE-001 (CUS-INT-004-04/05, CUS-APIM deprecation/rate-limit, CUS-MIG-010-04/05/06)
    to the exact code locations and business-rule ids (RN-004/RN-005 for BCM-PLT-004;
    RN-003/RN-004/RN-005 for BCM-PLT-005; RN-003/RN-004/RN-005 for BCM-PLT-010) before
    writing any code. Reviewed the established retry/idempotency conventions in cashsales.billingrequestmanagement
    (FiscalAdapterPort.retry/cancel, BillingRequestManagementService .retry, LocalDeterministicFiscalAdapter)
    to mirror rather than reinvent them for IntegrationAdapterPort and the new MigrationDomainCommandPort.
  corrections_applied: []
  stale_pointer_sweep_after_preflight:
    method: Repository-wide review confirming active_module/current_module/active_backlog_item
      already correctly named MVP-MOD-008/MVP-MOD-008-BE-002 at the start of this
      backlog item.
    result: passed
    detail: No stale pointer required correction; MVP-MOD-008-BE-001's own closure
      left registries clean.
debt_first_review:
  applicable: true
  rationale: Reviewed technical-debt-index.md before feature implementation per
    the debt-first rule. TD-BE-013 (XLSX row-level parsing for open data ingestion
    and migration) was prioritized per both the backlog prompt's mandatory_execution_notes
    and this task's explicit priority list, since MVP-MOD-008-BE-002 necessarily touches
    migration ingestion/parsing code.
  debt_items_addressed:
  - id: TD-BE-013
    action: closed
    detail: Added org.apache.poi:poi-ooxml (5.4.1) and implemented ImportFileParser.countXlsxRows(),
      counting data rows on the first worksheet excluding the header and any fully-blank
      trailing row, mirroring countCsvRows's header-skipping semantics. countRows("xlsx",
      ...) now returns a real row count instead of ROWS_NOT_COUNTED; MigrationManagementService.runDryRunValidation
      no longer emits a row-level warning for xlsx files. Verified with 2 new ImportFileParserTest
      cases (header-only-data-rows counted; a trailing fully-blank row is not counted
      as data). Adding poi-ooxml introduced a commons-io dependency-convergence conflict
      (commons-csv wanted 2.20.0, poi wanted 2.18.0, poi's own commons-compress transitive
      wanted 2.16.1); resolved by pinning commons-io 2.20.0 in dependencyManagement,
      the same pattern already used in this pom.xml for jackson-databind's CVE-2026-54515
      pin.
  new_debt_registered:
  - id: TD-BE-014
    reason: MigrationDomainCommandPort's only implementation (LocalDeterministicMigrationDomainCommandAdapter)
      is a local deterministic stand-in, not a real cross-module command delegation,
      because no HOP business module yet exposes a migration-import application command
      for a real adapter to call. INV-MIG-003 is preserved by construction (the port
      is the sole interaction point commitImport/retryImportExecution use), so this
      is an honest capability gap, not a bypassed invariant.
  - id: TD-BE-015
    reason: RN-004 rate-limit enforcement is implemented and working for requests
      bearing a partner API key (the only authenticated per-consumer identity this
      backend has), but public/internal classification-tier requests are not throttled
      because no request-path-to-operationId classification mapping exists yet.
capability_compilation:
- capability_id: BCM-PLT-004
  package_folder: 01-product-definition/business-capabilities/packages/bcm-plt-004-integration-management/
  module: com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement
  custom_rules_implemented:
  - id: CUS-INT-004-04
    rule: RN-004
    detail: IntegrationManagementService now enforces a bounded, auditable retry policy.
      The first retry after an initial receipt failure is immediately allowed (nothing
      to bound yet); a retry that itself fails schedules an exponential backoff window
      (30s/120s/300s/900s/1800s, indexed by the failed attempt count) recorded on
      IntegrationMessageRecord.nextRetryAt, and retrying again before that window
      elapses is rejected with INTEGRATION_RETRY_NOT_YET_DUE. Once MAX_RETRY_COUNT
      (5) attempts have failed, the message transitions to the previously-unused IntegrationMessageRecord.STATUS_DEAD_LETTERED
      status with a recorded deadLetterReason, and further retries are rejected with
      INTEGRATION_MESSAGE_DEAD_LETTERED. A new isReadyForDownstreamRouting() domain
      method makes explicit (and testable) that only an acknowledged message may ever
      reach a target bounded context, closing out RN-001's anti-corruption-layer invariant
      for the retry path too.
  - id: CUS-INT-004-05
    rule: RN-005
    detail: A deterministic correlationId (SHA-256 of endpointId|externalMessageId)
      is derived once at first receipt, stored on IntegrationMessageRecord, and threaded
      through IntegrationAdapterPort.acknowledgeMessage (new parameter) on every subsequent
      retry, so the same correlation id links a message's full inbound/outbound lifecycle
      end to end, mirroring FiscalAdapterPort's idempotencyKey/correlationId threading
      convention.
  endpoints_all_functional: true
- capability_id: BCM-PLT-005
  package_folder: 01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/
  module: com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement
  custom_rules_implemented:
  - id: CUS-APIM (deprecation retirement)
    rule: RN-003
    detail: 'New ApiManagementService.retireDeprecatedOperation completes the deprecation
      lifecycle: only reachable from deprecation_scheduled, and only once LocalDateTime.now(clock)
      is after the scheduled deprecationWindowTo (API_DEPRECATION_WINDOW_NOT_ELAPSED
      otherwise, since this synchronous backend has no scheduler to run the transition
      automatically on a timer). New POST /api/platform/api-management/operations/{operationId}/retirement
      endpoint, already covered by the existing SCREEN_API_MANAGEMENT EndpointPermissionRegistry
      entry (coarse-grained per-path permission, per the codebase''s documented convention).'
  - id: CUS-APIM (rate-limit enforcement)
    rule: RN-004
    detail: 'New PartnerApiRateLimiter (in-memory fixed-window, one-minute counter,
      keyed by partner API key id) and PartnerApiKeyRateLimitInterceptor (a self-registering
      WebMvcConfigurer local to apimanagement, independent of identityaccess''s HopAuthorizationInterceptor
      wiring to respect Spring Modulith module boundaries) enforce RateLimitPolicy
      for any request bearing the X-Partner-Api-Key header: an unknown/revoked key
      is rejected 401 API_PARTNER_KEY_INVALID_OR_SCOPE_MISMATCH; a key without a configured
      policy passes through unbounded; exceeding the policy''s requestsPerMinute is
      rejected 429 API_RATE_LIMIT_EXCEEDED. Requests without the header are entirely
      unaffected. Public/internal classification-tier enforcement remains open (TD-BE-015).'
  - id: CUS-APIM (audit gap fix)
    rule: RN-005
    detail: setRateLimitPolicy previously never called AuditRecorder at all, a real
      audit gap left by BE-001 (RN-005 requires "every API-management administrative
      action" to be audited). Now records a RateLimitPolicySet system event on every
      policy change.
  endpoints_all_functional: true
- capability_id: BCM-PLT-010
  package_folder: 01-product-definition/business-capabilities/packages/bcm-plt-010-open-data-ingestion-and-migration/
  module: com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement
  custom_rules_implemented:
  - id: CUS-MIG-010-04
    rule: RN-003
    detail: New MigrationDomainCommandPort (mirroring IntegrationAdapterPort/FiscalAdapterPort)
      is now the sole interaction point commitImport/retryImportExecution use to advance
      an import; INV-MIG-003 (never write directly to a business aggregate) is satisfied
      by construction, not merely by an empty list as in BE-001. For each manifest-declared
      entity category not already checkpointed, the port's invokeImportCommand is
      called and its returned stable command identifier is appended to ImportExecution.domainCommandsInvoked.
      The only current implementation, LocalDeterministicMigrationDomainCommandAdapter,
      honestly stands in for a real per-module command (TD-BE-014) with a deterministic
      per-(job,category) identifier and a deterministic failure marker (a category
      name containing "FAIL") used by tests to exercise the failure/retry path without
      random behaviour.
  - id: CUS-MIG-010-05
    rule: RN-004
    detail: ImportExecution.checkpoint is now a real delimiter-joined list of entity
      categories already completed. Execution stops at the first category whose command
      invocation fails, so the checkpoint always reflects real, contiguous progress.
      retryImportExecution resumes from that checkpoint, skipping every already-completed
      category rather than re-invoking it (idempotent resume). A bounded MAX_EXECUTION_ATTEMPTS
      (5) ceiling transitions the MigrationJob to STATUS_FAILED and rejects further
      retries with MIGRATION_EXECUTION_ATTEMPTS_EXHAUSTED once exhausted.
  - id: CUS-MIG-010-06
    rule: RN-005
    detail: A ReconciliationReport#PHASE_POST_IMPORT report is written after every
      commit/retry attempt (success or failure), incrementally reflecting exactly
      what that attempt completed (importedCounts) and, on failure, which category
      was rejected (rejectedCounts/ warningCounts) — genuinely incremental rather
      than a single end-of-job aggregate. MigrationJob reaches the previously-unused
      STATUS_RECONCILED once every category succeeds.
  endpoints_all_functional: true
security_and_tenant_isolation:
  server_side_authorization: The new POST .../operations/{operationId}/retirement
    endpoint falls under the existing SCREEN_API_MANAGEMENT EndpointPermissionRegistry
    prefix match; no new PermissionCode was needed. PartnerApiKeyRateLimitInterceptor
    is an independent, additive gate (key validity + throttling), not a replacement
    for IAM authorization.
  tenant_isolation: No new tenant-isolation surface was introduced; every new method
    continues the codebase's existing explicit-tenantId-parameter-threading convention
    where applicable (deprecation retirement and rate-limit policy configuration are
    platform-wide, not tenant-scoped, matching scheduleDeprecation/setRateLimitPolicy's
    existing BE-001 scope).
  input_validation: jakarta.validation and server-side enum/state-machine checks unchanged
    and extended consistently for every new command.
  structured_errors: Every BCM-PLT-004/BCM-PLT-005/BCM-PLT-010 error response now
    carries both `code` (unchanged machine identifier) and a new `messageKey` (a deterministic
    `integration.error.<code>` / `migration.error.<code>` i18n/messages catalog key),
    with es-MX/en-US catalog entries registered for every code used by these two exception
    handlers (existing and new), further reducing TD-I18N-002.
quality_gates:
- tool: Maven Enforcer
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  notes: Java 21, Maven >=3.9, dependency convergence rules passed after pinning commons-io
    2.20.0 to resolve a convergence conflict introduced by adding poi-ooxml (TD-BE-013
    closure).
- tool: Surefire (unit + Spring Modulith boundary + local-database)
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  tests_run: 265
  failures: 0
  errors: 0
  skipped: 0
  notes: '26 net new tests over MVP-MOD-008-BE-001''s 239: IntegrationManagementServiceTest
    (5), ApiManagementServiceTest (4), MigrationManagementServiceTest (4), PartnerApiRateLimiterTest
    (3), LocalDeterministicMigrationDomainCommandAdapterTest (3), 2 new ImportFileParserTest
    xlsx cases, 5 new IntegrationInteroperabilityApiTest cases (bounded-backoff rejection,
    deprecation retirement gated by elapsed window x2, partner rate-limit enforcement,
    unknown partner key), 2 new DataMigrationPortabilityApiTest cases (real checkpointed
    domain-command invocation, checkpoint-resume-without-reinvocation), plus the existing
    PlatformFoundationModulithTest re-verifying module boundaries with the new adapter/interceptor
    classes in place. Two pre-existing tests were updated to match the new bounded-retry/real-checkpoint
    behavior (IntegrationInteroperabilityApiTest''s normalization-failure-retry case
    now exercises immediate first-retry-then-bounded-second-retry; DataMigrationPortabilityApiTest''s
    and DataMigrationPortabilityLocalDatabaseTest''s single-category commit lifecycle
    now asserts real "completed"/"reconciled" outcomes with a populated domainCommandsInvoked
    instead of the BE-001 lifecycle-shell''s "in_progress" placeholder).'
- tool: JaCoCo
  status: passed
  line_coverage_percent: 80.49
  previous_baseline_percent: 80.08
  final_target_percent: 80
  notes: 'Backend line coverage improved from 80.08% to 80.49% (7138/8868 lines),
    remaining above the 80% final-closure target reached by MVP-MOD-008-BE-001. Source:
    07-implementation/backend/target/site/jacoco/jacoco.csv.'
- tool: CycloneDX
  status: passed
  evidence_command: mvn -Pquality org.cyclonedx:cyclonedx-maven-plugin:makeAggregateBom
  notes: SBOM generated under target outputs (not committed, per repository convention).
- tool: OWASP Dependency-Check
  status: passed
  evidence_command: mvn -Pquality org.owasp:dependency-check-maven:check
  vulnerabilities_found: 0
  notes: Explicitly checked the new org.apache.poi:poi-ooxml:5.4.1 dependency tree;
    0 vulnerabilities across the whole project.
- tool: Trivy (fs scanners - vuln, secret, misconfig)
  status: passed
  evidence_command: trivy fs --scanners vuln,secret,misconfig .
  findings: 0
  notes: Repository-root scan; 0 vulnerabilities, 0 secrets, 0 misconfigurations across
    backend pom.xml and all frontend package-lock.json files.
- tool: PMD
  status: findings_registered
  evidence_file: 07-implementation/backend/target/pmd.xml
  findings: 354
  debt: TD-BE-002
  notes: Repo-wide count (grown from the 344 recorded at MVP-MOD-008-BE-001 with ~15
    new files); non-blocking, gradual remediation per TD-BE-002.
- tool: CPD
  status: passed
  evidence_file: 07-implementation/backend/target/cpd.xml
  findings: 1
  notes: Unchanged from the pre-existing baseline; not attributable to the new code.
- tool: SpotBugs and Find Security Bugs
  status: findings_registered
  evidence_file: 07-implementation/backend/target/spotbugsXml.xml
  findings: 23
  new_module_findings: A small number of style-category findings (consistent with
    MVP-MOD-008-BE-001's pattern in the original parser classes) appear in the new
    adapter/interceptor classes; none are CORRECTNESS or high-priority SECURITY findings
    in the new code specifically. Repo-wide SECURITY-category findings pre-existing
    in other modules are unchanged by this backlog item.
  debt: TD-BE-002
  notes: Non-blocking per quality.failOnViolation=false; gradual remediation per TD-BE-002.
- tool: Duplicate Finder
  status: passed
  evidence_command: mvn -Pquality org.basepom.maven:duplicate-finder-maven-plugin:check
- tool: Spotless (Google Java Format)
  status: repo_wide_debt_confirmed_unchanged
  evidence_command: mvn -Pquality com.diffplug.spotless:spotless-maven-plugin:check
  notes: Fails against 577+ pre-existing files repo-wide (unchanged historical convention);
    not bound to the mvn verify lifecycle (no <executions> in pom.xml), consistent
    with quality.failOnViolation=false for the other static-analysis tools. Not a
    regression introduced by this backlog item.
repository_sweeps:
- name: YAML parse
  result: passed
  detail: All new/modified YAML files (2 technical-debt items, technical-debt-index.md,
    3 traceability.md, this evidence pair, security-quality evidence pair, registries)
    parse without errors.
- name: git diff --check
  result: passed
  detail: 0 whitespace errors.
- name: Secrets sweep
  result: passed
  detail: Covered by the Trivy secret scanner above (0 findings) plus manual review
    of new files.
- name: Agent-agnostic sweep
  result: passed
  detail: No named-agent, assistant, model-vendor or platform-runtime dependency introduced
    in code, tests or models.
- name: Forbidden-status sweep
  result: passed
  detail: No new content contains closed_with_execution_limitation, passed_with_execution_limitation,
    "not_executed" (as a literal status value), blocked_by, or status:_failed.
- name: No versioned target/coverage artifacts
  result: passed
  detail: 07-implementation/backend/target/ remains untracked (.gitignore-covered);
    no coverage report committed.
blocking_gaps: []
readiness:
  mvp_mod_008_be_002_status: closed
  ready_for_next_backlog_item: MVP-MOD-008-FE-001
  next_backlog_item_name: Compile integration and migration administration UI outputs
  hop_commercially_complete: false
  hop_ga_ready: false
  rationale: All three MVP-MOD-008-BE-001-deferred custom-rule hooks (CUS-INT-004-04/05,
    CUS-APIM deprecation-retirement/rate-limit-enforcement, CUS-MIG-010-04/05/06)
    are implemented with real behaviour, not lifecycle shells, and are exercised by
    both new unit tests and updated/extended API-level tests. TD-BE-013 was closed
    with a real Apache POI implementation as the debt-first action; TD-BE-014 and
    TD-BE-015 are honestly registered for the two genuine scope boundaries found during
    implementation (no HOP module yet exposes a migration-import command; rate-limit
    enforcement is scoped to partner-key-bearing requests). Backend coverage improved
    from 80.08% to 80.49%, remaining above the 80% final-closure target. Employee-portal
    (85.50%), mobile (98.87%), patient-portal (41.93%) and doctor-portal (40.62%)
    are unchanged and not regressed (this backlog item did not touch those stacks).
    HOP is not commercially complete or GA-ready; MVP-MOD-008-FE-001 through MVP-MOD-008-CLOSEOUT
    and all REL-002/003/004 modules remain planned.
```
