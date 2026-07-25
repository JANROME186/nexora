# MVP-MOD-008-BE-001 Validation — Integration Adapter Contracts and API Governance Backend Compilation

**Status:** passed
**Backlog item:** MVP-MOD-008-BE-001
**Module:** MVP-MOD-008 Integration and Migration Readiness
**Code implemented:** Yes

## Scope

Compiled the backend outputs for the three MVP-MOD-008-DEF capability packages into two new
Spring Modulith modules:

| Capability | Java module | Base API path |
| --- | --- | --- |
| BCM-PLT-004 Integration Management | `integrationinteroperability.integrationmanagement` | `/api/platform/integration` |
| BCM-PLT-005 API Management | `integrationinteroperability.apimanagement` | `/api/platform/api-management` |
| BCM-PLT-010 Open Data Ingestion and Migration | `datamigrationportability.migrationmanagement` | `/api/platform/migration` |

Every endpoint listed in each capability's `openapi-source.md` is functional — none responds
unimplemented. Custom rules explicitly deferred to MVP-MOD-008-BE-002 are documented in code
Javadoc, `traceability.md` and this evidence, matching the precedent set by MVP-MOD-004-BE-001.

## Key implementation decisions

- **BCM-PLT-004**: `IntegrationAdapterPort` + `LocalDeterministicPassthroughIntegrationAdapter`
  mirror `FiscalAdapterPort`. Messages are idempotently reprocessed by `(endpointId,
  externalMessageId)`. Normalization failures are captured as canonical error codes only, never raw
  provider text.
- **BCM-PLT-005**: Operation classification defaults to `internal`; partner-key issuance validates
  granted scopes against partner-classified operations at issuance time.
- **BCM-PLT-010**: Real manifest parsing (SnakeYAML) and checksum verification, real CSV row
  parsing (Apache Commons CSV — the one new dependency this backlog item adds), JSON/NDJSON row
  counting (Jackson) and ZIP bundle extraction (`java.util.zip`). `commitImport`/
  `retryImportExecution` run a real but honestly-scoped execution/reconciliation lifecycle shell
  with an intentionally empty `domainCommandsInvoked` list — the "never write directly to a business
  aggregate" invariant is preserved by construction rather than bypassed for a demo.
- The three new capabilities are HOP's first backend modules to render a first-class `code` field
  on every error response, turning MVP-MOD-008-DEF's modeling-stage `code`-field decision into
  working code (`IntegrationExceptionHandler`, `MigrationExceptionHandler`).
- All three new base paths were registered in `EndpointPermissionRegistry` against three new
  `PermissionCode` values — unmapped API paths are otherwise silently allowed by the authorization
  interceptor, so this was verified deliberately rather than assumed.

## Debt-first review

- **TD-STACK-003** (no OpenAPI-Generator tooling): evaluated introducing the generator for the 3
  new modules and deliberately deferred it — 3 of 18 modules using generated code while 15 stay
  hand-written would fragment the codebase more than it would reduce this item's actual scope.
  Instead tightened the existing compensating control (checked 1:1 controller-to-contract mapping).
  The BCM-PLT-005 TypeScript-client pilot commitment for MVP-MOD-008-FE-001 is unchanged.
- **TD-I18N-002** (structured error codes): materially reduced further — the `code` field modeled
  by MVP-MOD-008-DEF is now real, working code for the first time in HOP's backend.
- **TD-BE-013** (new, open): XLSX row-level parsing is not implemented — Apache POI was
  deliberately not added given its heavy transitive dependency footprint and no immediate provider
  demand. XLSX packages are still accepted and archived; only row counting is skipped, surfaced as
  a non-blocking dry-run warning.

## Quality gates

`mvn -Pquality "-Dhop.local-db-tests=true" clean verify` passed: **239 tests, 0 failures, 0
errors, 0 skipped** (includes the Spring Modulith module-boundary test, new API tests for both
modules against real Postgres, and parser unit tests). **JaCoCo line coverage reached 80.08%**,
crossing the project's 80% final-closure target for the backend stack (up from the 78.51% floor).
OWASP Dependency-Check (explicitly run against the new commons-csv dependency) and a
repository-root Trivy scan (`vuln,secret,misconfig`) both found **0 vulnerabilities**. PMD (344,
repo-wide) and SpotBugs (21, repo-wide) findings are registered under the existing TD-BE-002
gradual-remediation debt item, non-blocking per `quality.failOnViolation=false`; none is a new
CORRECTNESS or high-priority SECURITY finding in the new modules.

## Readiness

- MVP-MOD-008-BE-001: **closed**
- Next backlog item: **MVP-MOD-008-BE-002** (integration retry/dead-letter, API
  deprecation/rate-limit and migration checkpoint custom rules)
- HOP commercially complete / GA-ready: **No** — unchanged
- Backend coverage: **78.51% → 80.08%** (target reached). Employee portal 85.50%, mobile 98.87%,
  patient portal 41.93%, doctor portal 40.62% — all unchanged, not regressed (this item did not
  touch those stacks).

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-MVP-MOD-008-BE-001
  type: qa-validation-evidence
  name: MVP-MOD-008-BE-001 Integration Adapter Contracts and API Governance Backend
    Compilation Validation
  version: 1.0.0
  status: passed
  human_readable: MVP-MOD-008-BE-001-validation.md
  machine_readable: MVP-MOD-008-BE-001-validation.md
  created_date: 2026-07-18
  owner: Nexora Backend Engineering Team
scope:
  backlog_item: MVP-MOD-008-BE-001
  module: MVP-MOD-008 Integration and Migration Readiness
  release: REL-001
  execution_flow_stage: compile
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  code_implemented: true
  working_directory: projects/healthcare-operations-platform/07-implementation/backend
preflight:
  purpose: Loaded PROJECT_STATE.md, SOURCE_OF_TRUTH.md (root and project), the
    HOP commercial product backlog/execution prompts, technical-debt-index.md, the
    three MVP-MOD-008-DEF capability packages, their DEF QA/security evidence, and
    confirmed BUSINESS_REQUIREMENT.md exists, before compiling. Reviewed the existing
    FiscalAdapterPort/DocumentStoragePort/NotificationProviderPort adapter-port pattern,
    the SampleReadPort named-interface pattern, the IAM/session/tenant/ permission
    enforcement mechanism (HopAuthorizationInterceptor + EndpointPermissionRegistry
    + PermissionCode), and MessageSource/error-code conventions before writing any
    code.
  corrections_applied: []
  stale_pointer_sweep_after_preflight:
    method: Repository-wide review confirming active_module/current_module/active_backlog_item
      already correctly named MVP-MOD-008/MVP-MOD-008-BE-001 at the start of this
      backlog item.
    result: passed
    detail: No stale pointer required correction; MVP-MOD-008-DEF's own closure left
      registries clean.
debt_first_review:
  applicable: true
  rationale: Reviewed technical-debt-index.md before feature implementation per
    the debt-first rule. TD-STACK-003 and TD-I18N-002 were prioritized per the backlog
    prompt's mandatory_execution_notes.
  debt_items_addressed:
  - id: TD-STACK-003
    action: compensating_control_extended
    detail: 'Evaluated introducing openapi-generator-maven-plugin for the 3 new modules
      and deliberately deferred it (would fragment the codebase''s contract-generation
      style across only 3 of 18 modules rather than reduce the item''s actual repo-wide
      scope). Extended the compensating control instead: every new controller is a
      checked 1:1 rendering of its capability''s openapi-source.md operations list
      with Javadoc stating the source contract. The concrete TypeScript-client pilot
      commitment (BCM-PLT-005, MVP-MOD-008-FE-001) is unchanged.'
  - id: TD-I18N-002
    action: materially_reduced_further
    detail: 'Implemented the first-class `code` field modeled by MVP-MOD-008-DEF:
      IntegrationExceptionHandler and MigrationExceptionHandler render `code` from
      IntegrationErrorCodes/MigrationErrorCodes constants matching each capability''s
      openapi-source.md error_model.domain_errors exactly. This is HOP''s first
      working code-bearing error response. The five pre-existing *ApiErrorResponse
      shapes remain unmigrated (unchanged, separately tracked).'
  new_debt_registered:
  - id: TD-BE-013
    reason: XLSX row-level parsing not implemented (Apache POI deliberately not added
      given its heavy transitive footprint and no immediate provider demand). Format
      is accepted/declared; row counting returns ROWS_NOT_COUNTED and surfaces as
      a non-blocking dry-run warning.
capability_compilation:
- capability_id: BCM-PLT-004
  package_folder: 01-product-definition/business-capabilities/packages/bcm-plt-004-integration-management/
  module: com.nexora.hop.platformfoundation.integrationinteroperability.integrationmanagement
  generatable_outputs_compiled:
  - RegisterIntegrationEndpoint (registerIntegrationEndpoint, listIntegrationEndpoints,
    retireIntegrationEndpoint)
  custom_rules_implemented:
  - id: CUS-INT-004-01
    detail: IntegrationAdapterPort + LocalDeterministicPassthroughIntegrationAdapter
      (mirrors FiscalAdapterPort).
  - id: CUS-INT-004-02
    detail: Canonical error mapping — normalization failures captured as canonical
      error codes only (RN-002).
  - id: CUS-INT-004-03
    detail: Idempotent reprocessing keyed by (endpointId, externalMessageId) (RN-003,
      INV-INT-002).
  - id: CUS-INT-004-06
    detail: Open-source protocol parser evaluation completed; real HL7v2/FHIR/ASTM/DICOM
      parser adoption deferred (TD-BE-013 covers the analogous migration-side gap;
      a dedicated integration-parser debt item is unnecessary since the local deterministic
      adapter already fully satisfies BE-001's scope without a real parser dependency).
  deferred_to_be_002:
  - id: CUS-INT-004-04
    detail: Bounded backoff/dead-letter policy — retryMessage only enforces a basic
      retry-count ceiling.
  - id: CUS-INT-004-05
    detail: Correlation-id propagation across retries.
  endpoints_all_functional: true
- capability_id: BCM-PLT-005
  package_folder: 01-product-definition/business-capabilities/packages/bcm-plt-005-api-management/
  module: com.nexora.hop.platformfoundation.integrationinteroperability.apimanagement
  generatable_outputs_compiled:
  - listApiOperations, revokePartnerApiKey, listPartnerApiKeys, setRateLimitPolicy
  custom_rules_implemented:
  - id: CUS-APIM-005-01
    detail: Classification defaulting (internal by default, INV-APIM-001) and publish-gating
      logic.
  - id: CUS-APIM-005-02
    detail: Partner-key scope-coverage validation against classified partner operations
      at issuance (RN-002).
  - id: RN-003
    detail: Deprecation-window completeness check (from/to ordering + migration note
      required) at scheduling time.
  deferred_to_be_002:
  - detail: Full deprecation governance workflow beyond window-completeness checks.
  - detail: Rate-limit enforcement middleware (Bucket4j candidate); BE-001 only compiles
      policy configuration.
  endpoints_all_functional: true
- capability_id: BCM-PLT-010
  package_folder: 01-product-definition/business-capabilities/packages/bcm-plt-010-open-data-ingestion-and-migration/
  module: com.nexora.hop.platformfoundation.datamigrationportability.migrationmanagement
  generatable_outputs_compiled:
  - createMigrationJob, getMigrationJob, listMigrationJobs, approveImport, getReconciliationReport
  custom_rules_implemented:
  - id: CUS-MIG-010-01
    detail: Real manifest parsing (SnakeYAML, already transitive — no new dependency)
      and SHA-256/ SHA-512/MD5 checksum verification per declared file; real CSV row
      parsing (Apache Commons CSV, new dependency), JSON/NDJSON row counting (Jackson,
      already the platform's JSON library) and ZIP bundle extraction (java.util.zip,
      built-in). XLSX declared-but-not-counted (TD-BE-013).
  - id: CUS-MIG-010-02
    detail: Naive identity field-mapping baseline persisted as MappingTemplate (real
      anti-corruption mapping engine remains BE-002).
  - id: CUS-MIG-010-03
    detail: Structural, required-field, referential-integrity (parsed-vs-declared
      row count) and duplicate-detection-adjacent dry-run validation without mutation
      (RN-002, INV-MIG-002). Business-rule, privacy-and-consent, catalog-consistency,
      financial-reconciliation and clinical-result-integrity categories from the Open
      Data Ingestion Contract's 10-category list are honestly listed as not-yet-evaluated
      in validationCategoriesEvaluated rather than claimed as done.
  deferred_to_be_002:
  - id: CUS-MIG-010-04
    detail: commitImport starts a real, honestly-scoped ImportExecution/ReconciliationReport(pre_import)
      lifecycle shell with an intentionally empty domainCommandsInvoked list — INV-MIG-003
      (never write directly to a business aggregate) is preserved by construction,
      not bypassed. Real per-command domain invocation is BE-002 scope.
  - id: CUS-MIG-010-05
    detail: retryImportExecution starts a new attempt; full checkpoint-based idempotent
      resume (INV-MIG-004) is BE-002 scope.
  - id: CUS-MIG-010-06
    detail: Post-import reconciliation aggregation and full audit wiring beyond the
      events already emitted.
  endpoints_all_functional: true
  events_yaml_extended:
  - MigrationImportApproved
  - MigrationImportRetried
security_and_tenant_isolation:
  server_side_authorization: All 3 new base paths (/api/platform/integration, /api/platform/api-management,
    /api/platform/migration) registered in EndpointPermissionRegistry against new
    PermissionCode values (SCREEN_INTEGRATION_ENDPOINTS, SCREEN_API_MANAGEMENT, SCREEN_MIGRATION_JOBS);
    unmapped paths are otherwise silently allowed by HopAuthorizationInterceptor,
    so this registration was verified deliberately, not assumed. ADMIN role (EnumSet.allOf)
    automatically covers the 3 new codes; no RolePermissionCatalog change was required.
  tenant_isolation: Every service method takes tenantId as an explicit parameter threaded
    from controller to repository query, matching the codebase's existing convention
    (AuthenticatedUserContextHolder is set but not yet consumed by any application
    service anywhere in the codebase — a pre-existing gap, not introduced or worsened
    here).
  input_validation: jakarta.validation (@NotBlank/@NotEmpty/@Positive) on all request
    records; server-side enum/range checks in each service.
  upload_safety: receiveImportPackage accepts a multipart manifest + package; the
    manifest is checksum-verified against the actual uploaded bytes before any parsing
    proceeds (rejects tampered/mismatched packages); the raw package is archived via
    the existing DocumentStoragePort (LocalFilesystemDocumentAdapter), reusing its
    existing path-traversal-safe storage-key resolution rather than adding a new upload
    path.
  structured_errors: IntegrationExceptionHandler / MigrationExceptionHandler render
    {status, code, message, occurredAt}; code values match each capability's openapi-source.md
    error_model.domain_errors.
quality_gates:
- tool: Maven Enforcer
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  notes: Java 21, Maven >=3.9, dependency convergence rules passed.
- tool: Surefire (unit + Spring Modulith boundary + local-database)
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  tests_run: 239
  failures: 0
  errors: 0
  skipped: 0
  notes: Includes PlatformFoundationModulithTest (module-boundary verify), 6 new IntegrationInteroperabilityApiTest
    cases, 4 new DataMigrationPortabilityApiTest cases, 7 ManifestParserTest cases,
    7 ImportFileParserTest cases, and 2+2 new *LocalDatabaseTest cases exercising
    the real Postgres schemas end to end (including the JDBC repository implementations,
    not just the in-memory test defaults).
- tool: JaCoCo
  status: passed
  line_coverage_percent: 80.08
  previous_baseline_percent: 78.51
  final_target_percent: 80
  notes: 'Backend line coverage reached the project''s 80% final-closure target for
    this stack (up from the 78.51% floor), not merely preserved. Source: 07-implementation/backend/target/site/jacoco/jacoco.csv.'
- tool: CycloneDX
  status: passed
  evidence_command: mvn -Pquality "-Dhop.local-db-tests=true" clean verify
  notes: SBOM generated under target outputs (not committed, per repository convention).
- tool: OWASP Dependency-Check
  status: passed
  evidence_command: mvn -Pquality org.owasp:dependency-check-maven:check
  vulnerabilities_found: 0
  notes: Explicitly checked the new commons-csv 1.14.1 dependency; 0 vulnerabilities
    across the whole project.
- tool: Trivy (fs scanners - vuln, secret, misconfig)
  status: passed
  evidence_command: trivy fs --scanners vuln,secret,misconfig .
  findings: 0
  notes: Repository-root scan; 0 vulnerabilities, 0 secrets, 0 misconfigurations.
- tool: PMD
  status: findings_registered
  evidence_file: 07-implementation/backend/target/pmd.xml
  findings: 344
  debt: TD-BE-002
  notes: Repo-wide count (grown from the 263 recorded at MVP-MOD-007-BE-001 with ~90
    new files); non-blocking, gradual remediation per TD-BE-002.
- tool: CPD
  status: passed
  evidence_file: 07-implementation/backend/target/cpd.xml
  findings: 1
  notes: Unchanged from the pre-existing baseline; not attributable to the new modules.
- tool: SpotBugs and Find Security Bugs
  status: findings_registered
  evidence_file: 07-implementation/backend/target/spotbugsXml.xml
  findings: 21
  new_module_findings: A small number of findings (DLS_DEAD_LOCAL_STORE-class style
    findings) appear in the new ManifestParser/ImportFileParser classes; none are
    CORRECTNESS or high-priority SECURITY findings in the new modules specifically.
    Repo-wide SECURITY-category findings (CRLF_INJECTION_LOGS, IMPROPER_UNICODE, UNSAFE_HASH_EQUALS,
    SERVLET_HEADER, XSS_SERVLET) are pre-existing in other modules, unchanged by this
    backlog item.
  debt: TD-BE-002
  notes: Non-blocking per quality.failOnViolation=false; gradual remediation per TD-BE-002.
- tool: Duplicate Finder
  status: passed
  evidence_command: mvn -Pquality org.basepom.maven:duplicate-finder-maven-plugin:check
- tool: Spotless (Google Java Format)
  status: repo_wide_debt_confirmed_unchanged
  evidence_command: mvn -Pquality com.diffplug.spotless:spotless-maven-plugin:check
  notes: Fails against 566+ pre-existing files repo-wide (this tooling has never been
    applied historically); not bound to the mvn verify lifecycle (no <executions>
    in pom.xml), consistent with quality.failOnViolation=false for the other static-analysis
    tools. Not a regression introduced by this backlog item; out of scope to reformat
    the entire codebase in one pass.
repository_sweeps:
- name: YAML parse
  result: passed
  detail: All new/modified YAML files (12 capability-package model files, 2 traceability,
    2 evidence pairs, registries) parse without errors.
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
    in code or models.
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
  mvp_mod_008_be_001_status: closed
  ready_for_next_backlog_item: MVP-MOD-008-BE-002
  next_backlog_item_name: Implement integration retry/dead-letter, API deprecation/rate-limit
    and migration checkpoint custom rules
  hop_commercially_complete: false
  hop_ga_ready: false
  rationale: All generatable outputs and the BE-001-scoped custom rules for BCM-PLT-004,
    BCM-PLT-005 and BCM-PLT-010 are compiled and functional; no endpoint responds
    unimplemented. Every BE-002 deferral is explicit in code Javadoc, traceability.md
    and this evidence, not silently dropped. TD-STACK-003 and TD-I18N-002 were further
    reduced with real implementation (not modeling-only); TD-BE-013 was honestly registered
    for the one real scope gap (XLSX row parsing) found during implementation. Backend
    coverage improved from 78.51% to 80.08%, reaching the stack's final-closure target.
    Employee-portal (85.50%), mobile (98.87%), patient-portal (41.93%) and doctor-portal
    (40.62%) are unchanged and not regressed (this backlog item did not touch those
    stacks). HOP is not commercially complete or GA-ready; MVP-MOD-008-BE-002 through
    MVP-MOD-008-CLOSEOUT and all REL-002/003/004 modules remain planned.
```
