# MVP-MOD-002-BE-001 — Diagnostic Catalog Backend Compilation Validation

Machine-readable evidence: [MVP-MOD-002-BE-001-validation.md](MVP-MOD-002-BE-001-validation.md)

## Scope

Backlog item `MVP-MOD-002-BE-001` compiles the backend outputs for the `catalog-test-configuration`
bounded context from the eight modeled Diagnostic Catalog capability packages (BCM-SVC-001, 002,
003, 004, 005, 006, 007, 009). Business requirement version analyzed: `v0.68.0` (no impact
assessment required — matches `last_analyzed_version` in `BUSINESS_REQUIREMENT_INDEX.md`).

## What was compiled

- A new Spring Modulith application module `catalogtestconfiguration`
  (`com.nexora.hop.platformfoundation.catalogtestconfiguration`) with
  `allowedDependencies: [organizationmanagement, auditcompliance]`.
- Domain records, repository ports, application services, REST controllers, in-memory adapters and
  JDBC adapters for all 19 entities across the 8 capabilities.
- A new Postgres schema `catalog` (`db/catalog-test-configuration/schema.sql`, 17 tables), wired into
  `application-local.properties`.
- Only the business rules marked `generatable: true` in each capability's `business-rules.md` are
  enforced (uniqueness checks, required-field checks, simple numeric/format validation).
- Every operation marked `generatable: false` in `openapi-source.md` (publish, effective-dated
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

1. **BCM-SVC-006 Reference Range Management**: `business-rules.md` lists RN-002/RN-003 enforcement
   points as applying to both create and update, but `openapi-source.md` marks
   `createReferenceRange` as `generatable: true`. Only RN-001 is enforced at create time; RN-002/003
   are deferred entirely to `MVP-MOD-002-BE-002`. Suggested fix: align the two model files.
2. **BCM-SVC-007 Sample Catalog**: `business-model.md` (`ENT-SMP-002` `SampleRequirement`) omits
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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-MVP-MOD-002-BE-001-001
  type: qa-validation-evidence
  name: MVP-MOD-002-BE-001 Diagnostic Catalog Backend Compilation Validation
  version: 1.0.0
  status: passed
  human_readable: MVP-MOD-002-BE-001-validation.md
  machine_readable: MVP-MOD-002-BE-001-validation.md
  created_date: 2026-07-08
  owner: Nexora Product Architecture Team
scope:
  backlog_item: MVP-MOD-002-BE-001
  module: MVP-MOD-002 Diagnostic Catalog
  release: REL-001
  execution_flow_stage: compile
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  bounded_context: catalog-test-configuration
  implementation_root: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/catalogtestconfiguration/
capabilities_compiled:
- capability_id: BCM-SVC-001
  name: Diagnostic Service Catalog
  package: diagnosticservicecatalog
  entities:
  - DiagnosticService
  - ServiceComponentLink
  base_path: /api/catalog/diagnostic-services
- capability_id: BCM-SVC-002
  name: Test Catalog
  package: testcatalog
  entities:
  - TestDefinition
  - TestAnalyteLink
  - TestSampleRequirementLink
  base_path: /api/catalog/tests
- capability_id: BCM-SVC-003
  name: Panel Catalog
  package: panelcatalog
  entities:
  - PanelDefinition
  - PanelMember
  base_path: /api/catalog/panels
- capability_id: BCM-SVC-004
  name: Analyte Catalog
  package: analytecatalog
  entities:
  - AnalyteDefinition
  - AnalyteResultConstraint
  - AnalyteCodedValue
  base_path: /api/catalog/analytes
- capability_id: BCM-SVC-005
  name: Patient Preparation Management
  package: patientpreparationmanagement
  entities:
  - PreparationInstruction
  - PreparationAssignment
  base_path: /api/catalog/preparations
- capability_id: BCM-SVC-006
  name: Reference Range Management
  package: referencerangemanagement
  entities:
  - ReferenceRange
  - ReferenceRangeSegment
  base_path: /api/catalog/reference-ranges
- capability_id: BCM-SVC-007
  name: Sample Catalog
  package: samplecatalog
  entities:
  - SampleType
  - SampleRequirement
  base_path: /api/catalog/samples
- capability_id: BCM-SVC-009
  name: Price List Management
  package: pricelistmanagement
  entities:
  - PriceList
  - PriceEntry
  base_path: /api/catalog/price-lists
generated_outputs:
  backend:
  - Spring Modulith application module catalogtestconfiguration (package-info.java,
    allowedDependencies organizationmanagement, auditcompliance)
  - Domain records and repository ports for all 19 entities across the 8 capabilities
  - Application services enforcing only the business rules marked generatable true
    in each capability's business-rules.md
  - REST controllers rendered from each capability's openapi-source.md (9 controllers
    total covering diagnostic services, tests, panels, analytes, preparations, reference
    ranges, sample types, sample requirements, price lists)
  - In-memory adapters (profile "!local") and JDBC adapters (profile "local") per
    repository port
  - db/catalog-test-configuration/schema.sql (new "catalog" Postgres schema, 17 tables)
  - Shared exception/validation infrastructure (CatalogEntityNotFoundException, InvalidCatalogCommandException,
    CatalogCustomRuleNotImplementedException mapped to HTTP 501, CatalogExceptionHandler,
    CatalogValidation)
  tests:
  - CatalogTestConfigurationApiTest (8 tests covering generatable create/list/get/deprecate
    flows and 501 hook responses for every capability)
  - CatalogTestConfigurationContractTest (asserts every openapi-source.md operation
    across the 8 capability packages resolves to a registered Spring MVC route)
  - CatalogTestConfigurationLocalDatabaseTest (validates JDBC persistence against
    a real Postgres instance using db/catalog-test-configuration/schema.sql)
custom_rule_hooks_deferred_to_be_002:
- capability: BCM-SVC-001
  rules:
  - RN-002
  - RN-003
  - CUS-SVC-001-03
  operations:
  - publishDiagnosticService
  - getPublishedServiceSnapshot
  - updateDiagnosticService-when-published
- capability: BCM-SVC-002
  rules:
  - RN-003
  - RN-004
  - RN-005
  - CUS-SVC-002-04
  operations:
  - publishTest
  - getPublishedTestSnapshot
  - updateTest-when-published
- capability: BCM-SVC-003
  rules:
  - RN-002
  - RN-003
  - RN-004
  - CUS-SVC-003-03
  operations:
  - publishPanel
  - getPublishedPanelSnapshot
  - updatePanel-when-published
- capability: BCM-SVC-004
  rules:
  - RN-003
  - RN-004
  - RN-006
  - CUS-SVC-004-03
  operations:
  - publishAnalyte
  - getPublishedAnalyteSnapshot
  - updateAnalyte-when-published
- capability: BCM-SVC-005
  rules:
  - RN-002
  - RN-004
  - RN-005
  operations:
  - publishPreparation
  - assignPreparation
  - updatePreparation-when-published
- capability: BCM-SVC-006
  rules:
  - RN-002
  - RN-003
  - RN-004
  - RN-005
  - RN-006
  operations:
  - updateReferenceRange
  - publishReferenceRange
  - getEffectiveRangeSnapshot
- capability: BCM-SVC-007
  rules:
  - RN-003
  - RN-004
  - RN-005
  - CUS-SVC-007-04
  operations:
  - publishSampleRequirement
  - getPublishedSampleRequirementSnapshot
  - updateSampleRequirement-when-published
- capability: BCM-SVC-009
  rules:
  - RN-003
  - RN-004
  - RN-005
  - RN-006
  operations:
  - updatePriceList
  - publishPriceList
  - getEffectivePriceSnapshot
model_gaps_identified:
- id: GAP-BE-001-01
  capability: BCM-SVC-006
  statement: 'business-rules.md lists RN-002 (critical threshold consistency) and
    RN-003 (demographic overlap detection) enforcement_point as applying to both create
    and update, but openapi-source.md marks createReferenceRange as generatable:
    true while updateReferenceRange is generatable: false. This implementation enforces
    only RN-001 (normal low <= normal high) at create time and defers RN-002/RN-003
    entirely to MVP-MOD-002-BE-002, consistent with the contract-level generatable
    flag.

    '
  resolution: minimum_compatible_option_applied
  correction_needed_in: bcm-svc-006-reference-range-management/business-rules.md
    or openapi-source.md (align enforcement_point classification with the intended
    create-time validation scope)
- id: GAP-BE-001-02
  capability: BCM-SVC-007
  statement: 'business-model.md ENT-SMP-002 (SampleRequirement) does not declare
    tenantId/laboratoryId fields, unlike every other catalog entity. This implementation
    adds tenantId and laboratoryId to SampleRequirement for tenant/laboratory scoping
    consistency with the rest of the Diagnostic Catalog (minimum compatible option).

    '
  resolution: minimum_compatible_option_applied
  correction_needed_in: bcm-svc-007-sample-catalog/business-model.md (add tenantId/laboratoryId
    fields to ENT-SMP-002 to match the implementation)
- id: GAP-BE-001-03
  capability: all
  statement: 'No LaboratoryDirectory cross-module port exists yet in organizationmanagement
    (only TenantDirectory). Catalog services validate tenantId via TenantDirectory.tenantExists
    but accept laboratoryId as an opaque required string without existence validation
    against organizationmanagement.Laboratory.

    '
  resolution: minimum_compatible_option_applied
  correction_needed_in: platformfoundation/organizationmanagement (expose a LaboratoryDirectory
    port) - recommended for a future backlog item, not blocking for MVP-MOD-002-BE-001.
out_of_scope_confirmed:
- Custom business rules (RN-002/003/004/005/006 family across capabilities) - reserved
  for MVP-MOD-002-BE-002.
- Employee portal UI (MVP-MOD-002-FE-001).
- Mobile app.
- Formal QA validation backlog item MVP-MOD-002-QA-001 (only this backlog item's own
  evidence is included here).
- Legacy pre-MDPE OpenAPI drafts under 05-contracts/contracts/openapi/catalogs/ and
  .../test-configuration/ (superseded by capability packages; not modified).
validations:
- id: VAL-001
  name: Backend compiles
  method: mvn --settings .mvn/settings.xml compile
  result: passed
- id: VAL-002
  name: Backend test suite passes
  method: mvn --settings .mvn/settings.xml test
  result: passed
  detail: 31 tests run, 0 failures, 4 skipped (local-db tests skipped without a running
    Postgres).
- id: VAL-003
  name: Backend test suite passes against real Postgres
  method: docker compose -f compose.local.json up -d postgres; mvn --settings .mvn/settings.xml
    test -Dhop.local-db-tests=true
  result: passed
  detail: 32 tests run, 0 failures, 0 skipped. Validates db/catalog-test-configuration/schema.sql
    and all 8 JDBC adapters against a real Postgres 16 instance.
- id: VAL-004
  name: Spring Modulith module boundaries remain valid
  method: PlatformFoundationModulithTest (ApplicationModules.of(PlatformFoundationApplication.class).verify())
  result: passed
  detail: catalogtestconfiguration module declares allowedDependencies [organizationmanagement,
    auditcompliance] with no boundary violations.
- id: VAL-005
  name: OpenAPI/contract coverage
  method: CatalogTestConfigurationContractTest cross-checks every operation in all
    8 openapi-source.md files against registered Spring MVC routes.
  result: passed
- id: VAL-006
  name: Custom rule hooks are explicit and return 501
  method: CatalogTestConfigurationApiTest asserts publish/update-when-published/assign/effective-
    snapshot endpoints respond 501 with ruleId and backlogItem=MVP-MOD-002-BE-002.
  result: passed
- id: VAL-007
  name: YAML repository files remain parseable
  method: Parsed all created/modified YAML files (schema.sql is not YAML; PROJECT_STATE.md,
    SOURCE_OF_TRUTH.md and this evidence file parsed without errors).
  result: passed
- id: VAL-008
  name: Agent-agnostic scan
  method: Reviewed all created Java, YAML and SQL artifacts for named-agent, assistant,
    model-vendor or platform-runtime requirements.
  result: passed
  detail: No named-agent or vendor-runtime dependency found in implementation artifacts.
blocking_gaps: []
readiness:
  mvp_mod_002_be_001_status: closed
  ready_for_next_backlog_item: MVP-MOD-002-BE-002
  next_backlog_item_name: Implement catalog custom business rules
  rationale: 'All eight Diagnostic Catalog capabilities compile with generatable CRUD,
    persistence (in-memory and JDBC), REST controllers, audit integration and tenant
    scoping. Every custom business rule reserved for MVP-MOD-002-BE-002 is an explicit,
    testable hook (CatalogCustomRuleNotImplementedException, HTTP 501) rather than
    invented behavior. Two model gaps were identified and handled with the minimum
    compatible option without blocking compilation; both are documented above for
    correction in the source capability packages.

    '
```
