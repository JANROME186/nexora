# MVP-MOD-002-BE-001 — Diagnostic Catalog Backend Compilation Validation

Machine-readable evidence: [MVP-MOD-002-BE-001-validation.yaml](MVP-MOD-002-BE-001-validation.yaml)

## Scope

Backlog item `MVP-MOD-002-BE-001` compiles the backend outputs for the `catalog-test-configuration`
bounded context from the eight modeled Diagnostic Catalog capability packages (BCM-SVC-001, 002,
003, 004, 005, 006, 007, 009). Business requirement version analyzed: `v0.68.0` (no impact
assessment required — matches `last_analyzed_version` in `BUSINESS_REQUIREMENT_INDEX.yaml`).

## What was compiled

- A new Spring Modulith application module `catalogtestconfiguration`
  (`com.nexora.hop.platformfoundation.catalogtestconfiguration`) with
  `allowedDependencies: [organizationmanagement, auditcompliance]`.
- Domain records, repository ports, application services, REST controllers, in-memory adapters and
  JDBC adapters for all 19 entities across the 8 capabilities.
- A new Postgres schema `catalog` (`db/catalog-test-configuration/schema.sql`, 17 tables), wired into
  `application-local.yml`.
- Only the business rules marked `generatable: true` in each capability's `business-rules.yaml` are
  enforced (uniqueness checks, required-field checks, simple numeric/format validation).
- Every operation marked `generatable: false` in `openapi-source.yaml` (publish, effective-dated
  snapshot queries, preparation assignment, and any update operation guarded by an immutable
  versioning rule) is implemented as an explicit hook: it throws
  `CatalogCustomRuleNotImplementedException`, mapped to HTTP 501 with a `ruleId` and
  `backlogItem: MVP-MOD-002-BE-002` in the response body. No custom business logic was invented.

## Tests executed

| Test class | Result |
|---|---|
| `CatalogTestConfigurationApiTest` (8 tests) | Passed |
| `CatalogTestConfigurationContractTest` (1 test) | Passed |
| `CatalogTestConfigurationLocalDatabaseTest` (1 test, requires Postgres) | Passed |
| Full backend suite (`mvn test`) | 31/31 passed, 4 skipped without Postgres |
| Full backend suite with `-Dhop.local-db-tests=true` against Docker Postgres | 32/32 passed |
| `PlatformFoundationModulithTest` | Passed (module boundaries valid) |

## Model gaps found (documented, not blocking)

1. **BCM-SVC-006 Reference Range Management**: `business-rules.yaml` lists RN-002/RN-003 enforcement
   points as applying to both create and update, but `openapi-source.yaml` marks
   `createReferenceRange` as `generatable: true`. Only RN-001 is enforced at create time; RN-002/003
   are deferred entirely to `MVP-MOD-002-BE-002`. Suggested fix: align the two model files.
2. **BCM-SVC-007 Sample Catalog**: `business-model.yaml` (`ENT-SMP-002` `SampleRequirement`) omits
   `tenantId`/`laboratoryId` fields present on every other entity. Added them for scoping
   consistency (minimum compatible option). Suggested fix: add the fields to the model.
3. **All capabilities**: no `LaboratoryDirectory` cross-module port exists yet (only
   `TenantDirectory`); `laboratoryId` is accepted as an opaque required string. Recommended as a
   future backlog item, not blocking.

## Out of scope (per backlog boundaries)

Custom business rules, employee portal UI, mobile app, and `MVP-MOD-002-QA-001` formal QA
validation remain out of scope for this backlog item.

## Outcome

`MVP-MOD-002-BE-001` is **closed**. Next backlog item: `MVP-MOD-002-BE-002` (implement catalog
custom business rules).
