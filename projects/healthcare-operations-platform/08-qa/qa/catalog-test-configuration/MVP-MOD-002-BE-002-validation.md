# MVP-MOD-002-BE-002 — Diagnostic Catalog Custom Business Rules Validation

- Artifact: HOP-QA-MVP-MOD-002-BE-002-001
- Status: passed
- Backlog item: MVP-MOD-002-BE-002 (Implement catalog custom business rules)
- Module: MVP-MOD-002 Diagnostic Catalog · Release REL-001 · Stage: implement_rules
- Business requirement version: v0.68.0 (no impact pending)
- Bounded context: `catalog-test-configuration`

## Objective

Replace the HTTP 501 `CatalogCustomRuleNotImplementedException` hooks left by MVP-MOD-002-BE-001
with functional, tested custom business rules across the eight Diagnostic Catalog capabilities:
publication validation, immutable published snapshots, rejection of direct edits to published
entries, preparation assignment gating, effective-dated versioning with overlap prevention, and
effective-context resolution for reference ranges and price lists.

## Shared infrastructure added

- `CatalogConflictException` mapped to **HTTP 409** in `shared/CatalogExceptionHandler` for
  published-entity immutability and effective-window overlap conflicts.
- `EffectiveDating` shared helper for validity-window overlap detection and effective-on-date
  resolution, reused by reference range and price list rules.

## Custom rules implemented

| Capability | Rule(s) | Behavior |
|---|---|---|
| BCM-SVC-001 Diagnostic Service | RN-002, CUS-SVC-001-03, RN-003 | Publish requires ≥1 component; published snapshot projection; direct edit of published → 409. |
| BCM-SVC-002 Test | RN-003, CUS-SVC-002-04, RN-004 | Publish requires ≥1 analyte and ≥1 sample requirement; snapshot; direct edit of published → 409. |
| BCM-SVC-003 Panel | RN-002/003, CUS-SVC-003-03, RN-004 | Publish requires ≥2 members and ≥1 mandatory; snapshot; direct edit of published → 409. |
| BCM-SVC-004 Analyte | RN-003/006, CUS-SVC-004-03, RN-004 | Publish only complete analytes (numeric: unit+precision, coded: ≥1 coded value); snapshot; edit of published → 409. |
| BCM-SVC-005 Preparation | RN-002, RN-004, RN-005 | Publish requires full localization (+ fasting duration); assignment allowed only for published; edit of published → 409. |
| BCM-SVC-006 Reference Range | RN-001/002/003/004/005/006 | Bounds + critical-threshold consistency, demographic overlap detection, functional draft update, publish overlap prevention, effective-context resolution. |
| BCM-SVC-007 Sample | RN-003/005, CUS-SVC-007-04, RN-004 | Publish requires min volume + published sample type; snapshot; edit of published → 409; `publishSampleType` added. |
| BCM-SVC-009 Price List | RN-003/004/005/006 | Functional draft update, publish requires ≥1 entry, effective-window overlap prevention, effective price resolution. |

### Published snapshot / immutability model

Publishing transitions a draft entry to `published` and freezes it: any direct `update` of a
published entry is rejected with **HTTP 409**. Because the published record can no longer be
mutated, the live published record *is* the immutable snapshot returned by the
`published-snapshot` projections. A separate historical snapshot store was not required for this
backlog item.

## Endpoints that no longer return HTTP 501

All of the following now return functional 2xx/4xx responses:

- Diagnostic services: `POST …/{id}/publish`, `GET …/{id}/published-snapshot`, `PUT …/{id}` (published → 409)
- Tests: `POST …/{id}/publish`, `GET …/{id}/published-snapshot`, `PUT …/{id}` (published → 409)
- Panels: `POST …/{id}/publish`, `GET …/{id}/published-snapshot`, `PUT …/{id}` (published → 409)
- Analytes: `POST …/{id}/publish`, `GET …/{id}/published-snapshot`, `PUT …/{id}` (published → 409)
- Preparations: `POST …/{id}/publish`, `POST …/{id}/assignments`, `PUT …/{id}` (published → 409)
- Reference ranges: `PUT …/{id}`, `POST …/{id}/publish`, `GET …/effective`
- Sample requirements: `POST …/{id}/publish`, `GET …/{id}/published-snapshot`, `PUT …/{id}` (published → 409)
- Sample types: `POST …/types/{id}/publish` (new operation, GAP-BE-002-01)
- Price lists: `PUT …/{id}`, `POST …/{id}/publish`, `GET …/effective`

## Model change

**GAP-BE-002-01 (BCM-SVC-007):** the sample catalog contract modeled a `published` SampleType status
and RN-003 (`CATALOG_SAMPLE_TYPE_NOT_PUBLISHED`) but exposed no way to publish a sample type, which
would make every sample requirement permanently unpublishable. The `publishSampleType` operation was
added to the editable `openapi-source.md` model (source of truth) and implemented, closing the gap
at the model level so contract/route parity is preserved.

## Documented boundaries (non-blocking)

- **GAP-BE-001-03 (LaboratoryDirectory):** `laboratoryId` remains an opaque required string; no
  cross-module laboratory existence validation was added.
- **GAP-BE-001-02 (SampleRequirement scoping):** retained as implemented in MVP-MOD-002-BE-001.
- **Reference range species segmentation:** not modeled by ENT-REF-002; overlap detection covers
  sex, age and condition only.
- **Effective resolution scope:** the effective reference-range and price resolution endpoints carry
  no laboratory in the contract, so resolution searches published entries by analyte/item. Adding a
  laboratory scope is recommended for a future contract revision.

## Validation results

| ID | Validation | Result |
|---|---|---|
| VAL-001 | Backend compiles (`mvn test-compile`) | passed |
| VAL-002 | Backend test suite (`mvn test`) | passed — 42 run, 0 failures, 0 errors, 5 skipped |
| VAL-003 | Suite against real Postgres (`-Dhop.local-db-tests=true`) | passed — 42 run, 0 failures, 0 errors, 0 skipped (fresh volume) |
| VAL-004 | Spring Modulith boundaries | passed |
| VAL-005 | OpenAPI/contract coverage (incl. new `publishSampleType`) | passed |
| VAL-006 | Former 501 hooks now functional | passed |
| VAL-007 | YAML repository files parseable | passed |
| VAL-008 | Agent-agnostic scan | passed |

New/updated tests: `CatalogCustomRulesApiTest` (10 tests) added; `CatalogTestConfigurationApiTest`
(8 tests) updated so the assertions that previously expected HTTP 501 now assert functional
publication, snapshot, immutability (409), assignment gating, effective resolution and overlap (409)
behavior.

> VAL-003 note: the Postgres-backed suite passes with 42 tests, 0 failures and 0 skipped against a
> real Postgres 16 instance, exercising every `LocalDatabaseTest` (including
> `CatalogTestConfigurationLocalDatabaseTest`) and the extended JDBC adapters
> (`ReferenceRange.findByAnalyteRefId`, `PriceList.findByStatus`).
> `CatalogTestConfigurationLocalDatabaseTest` now suffixes its catalog codes with a random per-run
> token, so it tolerates a Postgres volume retained from a previous run — no `docker compose down -v`
> is required (confirmed by two consecutive runs against a non-empty volume).

## Readiness

- MVP-MOD-002-BE-002: **closed**.
- Blocking gaps: none.
- Next backlog item: **MVP-MOD-002-FE-001** (compile MVP-MOD-002 employee catalog UI outputs).

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-MVP-MOD-002-BE-002-001
  type: qa-validation-evidence
  name: MVP-MOD-002-BE-002 Diagnostic Catalog Custom Business Rules Validation
  version: 1.0.0
  status: passed
  human_readable: MVP-MOD-002-BE-002-validation.md
  machine_readable: MVP-MOD-002-BE-002-validation.md
  created_date: 2026-07-09
  owner: Nexora Product Architecture Team
scope:
  backlog_item: MVP-MOD-002-BE-002
  module: MVP-MOD-002 Diagnostic Catalog
  release: REL-001
  execution_flow_stage: implement_rules
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  bounded_context: catalog-test-configuration
  implementation_root: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/catalogtestconfiguration/
  objective: 'Replace the HTTP 501 CatalogCustomRuleNotImplementedException hooks
    left by MVP-MOD-002-BE-001 with functional, tested custom business rules for publication,
    immutable versioning/snapshots, preparation assignment, effective-dated versioning
    with overlap prevention, and effective-context resolution across the eight Diagnostic
    Catalog capabilities.

    '
change_detection:
  last_analyzed_business_requirement_version: v0.68.0
  current_business_requirement_version: v0.68.0
  impact_pending: false
shared_infrastructure_added:
- CatalogConflictException mapped to HTTP 409 (published-entity immutability and effective-window
  overlap conflicts) in shared/CatalogExceptionHandler.
- EffectiveDating shared helper (window overlap detection and effective-on-date resolution)
  reused by reference range and price list rules.
custom_rules_implemented:
- capability: BCM-SVC-001
  name: Diagnostic Service Catalog
  rules:
  - id: RN-002
    behavior: publishDiagnosticService transitions draft -> published; requires at
      least one orderable component; records DiagnosticServicePublished audit event.
  - id: CUS-SVC-001-03
    behavior: getPublishedServiceSnapshot returns the frozen published record; 404
      while draft.
  - id: RN-003
    behavior: updateDiagnosticService on a published service is rejected with HTTP
      409 (immutable; create a new draft version).
- capability: BCM-SVC-002
  name: Test Catalog
  rules:
  - id: RN-003
    behavior: publishTest transitions draft -> published; requires at least one analyte
      link and one sample requirement link before publication; audit event recorded.
  - id: CUS-SVC-002-04
    behavior: getPublishedTestSnapshot returns the frozen published record; 404 while
      draft.
  - id: RN-004
    behavior: updateTest on a published test is rejected with HTTP 409.
- capability: BCM-SVC-003
  name: Panel Catalog
  rules:
  - id: RN-002/RN-003
    behavior: publishPanel requires at least two member tests and at least one mandatory
      member; transitions draft -> published; audit event recorded.
  - id: CUS-SVC-003-03
    behavior: getPublishedPanelSnapshot returns the frozen published record; 404 while
      draft.
  - id: RN-004
    behavior: updatePanel on a published panel is rejected with HTTP 409.
- capability: BCM-SVC-004
  name: Analyte Catalog
  rules:
  - id: RN-003/RN-006
    behavior: publishAnalyte publishes only complete analytes (numeric requires unit
      and decimal precision; coded requires at least one coded value); audit event
      recorded.
  - id: CUS-SVC-004-03
    behavior: getPublishedAnalyteSnapshot returns the frozen published record; 404
      while draft.
  - id: RN-004
    behavior: updateAnalyte on a published analyte is rejected with HTTP 409.
- capability: BCM-SVC-005
  name: Patient Preparation Management
  rules:
  - id: RN-002
    behavior: publishPreparation requires full localization (title and instruction
      text in every language) and a duration for fasting preparations; audit event
      recorded.
  - id: RN-004
    behavior: assignPreparation is allowed only for a published preparation, validates
      the target type/reference, persists the assignment and records a PreparationAssigned
      audit event.
  - id: RN-005
    behavior: updatePreparation on a published preparation is rejected with HTTP 409.
- capability: BCM-SVC-006
  name: Reference Range Management
  rules:
  - id: RN-001/RN-002
    behavior: segment normal bounds ordering plus critical threshold consistency (critical
      low <= normal low, critical high >= normal high, critical low <= critical high)
      enforced on create, update and publish.
  - id: RN-003
    behavior: demographic overlap detection across segments (sex, age, condition)
      enforced on create, update and publish.
  - id: RN-004
    behavior: updateReferenceRange is functional for draft ranges and rejected with
      HTTP 409 for published ranges.
  - id: RN-005
    behavior: publishReferenceRange rejects an effective window that overlaps another
      published range for the same analyte (HTTP 409); audit event recorded.
  - id: RN-006
    behavior: getEffectiveRangeSnapshot resolves the published range effective on
      the observation date whose segment matches the patient sex and age context.
- capability: BCM-SVC-007
  name: Sample Catalog
  rules:
  - id: RN-003/RN-005
    behavior: publishSampleRequirement requires a declared minimum volume and a published
      referenced sample type; audit event recorded.
  - id: CUS-SVC-007-04
    behavior: getPublishedSampleRequirementSnapshot returns the frozen published record;
      404 while draft.
  - id: RN-004
    behavior: updateSampleRequirement on a published requirement is rejected with
      HTTP 409.
  - id: SAMPLE-TYPE-PUBLISH
    behavior: publishSampleType added to expose the sample type publication transition
      required by RN-003 (see GAP-BE-002-01).
- capability: BCM-SVC-009
  name: Price List Management
  rules:
  - id: RN-004
    behavior: updatePriceList is functional for draft price lists and rejected with
      HTTP 409 for published price lists.
  - id: RN-003
    behavior: publishPriceList requires at least one price entry; audit event recorded.
  - id: RN-005
    behavior: publishPriceList rejects an effective window that overlaps another published
      price list sharing the same laboratory, currency and agreement scope (HTTP 409).
  - id: RN-006
    behavior: getEffectivePriceSnapshot resolves the published price list that prices
      the item on the sale date honouring an optional currency and agreement filter.
endpoints_no_longer_501:
- POST /api/catalog/diagnostic-services/{serviceId}/publish
- GET /api/catalog/diagnostic-services/{serviceId}/published-snapshot
- PUT /api/catalog/diagnostic-services/{serviceId} (published -> HTTP 409)
- POST /api/catalog/tests/{testId}/publish
- GET /api/catalog/tests/{testId}/published-snapshot
- PUT /api/catalog/tests/{testId} (published -> HTTP 409)
- POST /api/catalog/panels/{panelId}/publish
- GET /api/catalog/panels/{panelId}/published-snapshot
- PUT /api/catalog/panels/{panelId} (published -> HTTP 409)
- POST /api/catalog/analytes/{analyteId}/publish
- GET /api/catalog/analytes/{analyteId}/published-snapshot
- PUT /api/catalog/analytes/{analyteId} (published -> HTTP 409)
- POST /api/catalog/preparations/{preparationId}/publish
- POST /api/catalog/preparations/{preparationId}/assignments
- PUT /api/catalog/preparations/{preparationId} (published -> HTTP 409)
- PUT /api/catalog/reference-ranges/{rangeId}
- POST /api/catalog/reference-ranges/{rangeId}/publish
- GET /api/catalog/reference-ranges/effective
- POST /api/catalog/samples/requirements/{requirementId}/publish
- GET /api/catalog/samples/requirements/{requirementId}/published-snapshot
- PUT /api/catalog/samples/requirements/{requirementId} (published -> HTTP 409)
- POST /api/catalog/samples/types/{sampleTypeId}/publish (new operation, see GAP-BE-002-01)
- PUT /api/catalog/price-lists/{priceListId}
- POST /api/catalog/price-lists/{priceListId}/publish
- GET /api/catalog/price-lists/effective
model_changes:
- id: GAP-BE-002-01
  capability: BCM-SVC-007
  statement: 'bcm-svc-007-sample-catalog/openapi-source.md modeled a published SampleType
    status and RN-003 (CATALOG_SAMPLE_TYPE_NOT_PUBLISHED) but exposed no operation
    to publish a sample type, which would make every sample requirement permanently
    unpublishable.

    '
  resolution: 'Added the publishSampleType operation to the editable openapi-source.md
    model (source of truth) and implemented SampleTypeController POST /types/{sampleTypeId}/publish
    plus SampleCatalogService.publishSampleType. This closes the gap at the model
    level so the contract test still verifies contract/route parity.

    '
documented_boundaries:
- Published-entity immutability uses the published record itself as the immutable
  snapshot. Because a direct update of a published entity is rejected (HTTP 409),
  the live published record is frozen and is what the published-snapshot projections
  return. A separate historical snapshot store was not required for this backlog item.
- GAP-BE-001-03 (LaboratoryDirectory) remains an explicit non-blocking boundary. laboratoryId
  is still accepted as an opaque required string; no cross-module laboratory existence
  validation was added by this backlog item.
- GAP-BE-001-02 (SampleRequirement tenantId/laboratoryId) is retained as implemented
  in MVP-MOD-002-BE-001; no further model change was required.
- RN-003 reference range species-based segmentation is not modeled by ENT-REF-002,
  so demographic overlap detection covers sex, age and condition only ("species when
  applicable" is not applicable to the current model).
- Effective resolution endpoints (reference range and price list) receive no laboratory
  in the contract, so resolution searches published entries by analyte and item. Adding
  a laboratory scope to these queries is recommended for a future contract revision
  but is a non-blocking boundary here.
validations:
- id: VAL-001
  name: Backend compiles
  method: mvn --settings .mvn/settings.xml test-compile
  result: passed
- id: VAL-002
  name: Backend test suite passes
  method: mvn --settings .mvn/settings.xml test
  result: passed
  detail: 42 tests run, 0 failures, 0 errors, 5 skipped (local-db tests skipped without
    a running Postgres). Includes the new CatalogCustomRulesApiTest (10 tests) and
    the updated CatalogTestConfigurationApiTest (8 tests) with functional assertions
    replacing the former 501 expectations.
- id: VAL-003
  name: Backend test suite passes against real Postgres
  method: docker compose -f compose.local.json up -d postgres; mvn --settings .mvn/settings.xml
    test -Dhop.local-db-tests=true
  result: passed
  detail: 42 tests run, 0 failures, 0 errors, 0 skipped against a real Postgres 16
    instance via docker compose, exercising every LocalDatabaseTest including CatalogTestConfigurationLocalDatabaseTest
    and the extended JDBC adapters (ReferenceRange.findByAnalyteRefId, PriceList.findByStatus).
    CatalogTestConfigurationLocalDatabaseTest now suffixes its catalog codes (DB-SVC-<token>,
    DB-PRC-<token>) with a random per-run token so it tolerates a Postgres volume
    retained from a previous run; a volume reset (docker compose down -v) is no longer
    required and the run was confirmed repeatable against a non-empty volume.
- id: VAL-004
  name: Spring Modulith module boundaries remain valid
  method: PlatformFoundationModulithTest
  result: passed
- id: VAL-005
  name: OpenAPI/contract coverage
  method: CatalogTestConfigurationContractTest cross-checks every operation in all
    8 openapi-source.md files (including the newly modeled publishSampleType) against
    registered Spring MVC routes.
  result: passed
- id: VAL-006
  name: Former 501 hooks now return functional behavior
  method: CatalogCustomRulesApiTest and CatalogTestConfigurationApiTest assert publication,
    snapshot, immutability (409), assignment gating, effective resolution and effective-window
    overlap (409) behavior instead of HTTP 501.
  result: passed
- id: VAL-007
  name: YAML repository files remain parseable
  method: Edited bcm-svc-007 openapi-source.md parsed by SnakeYAML in CatalogTestConfigurationContractTest;
    this evidence YAML and the updated PROJECT_STATE.md / SOURCE_OF_TRUTH.md parse
    without errors.
  result: passed
- id: VAL-008
  name: Agent-agnostic scan
  method: Reviewed all created/modified Java and YAML artifacts for named-agent, assistant,
    model-vendor or platform-runtime references.
  result: passed
  detail: No named-agent or vendor-runtime dependency found.
blocking_gaps: []
readiness:
  mvp_mod_002_be_002_status: closed
  ready_for_next_backlog_item: MVP-MOD-002-FE-001
  next_backlog_item_name: Compile MVP-MOD-002 employee catalog UI outputs
  rationale: 'Every custom business rule reserved for MVP-MOD-002-BE-002 in the MVP-MOD-002-BE-001
    validation is now implemented with functional publication, immutable versioning/snapshot,
    assignment, effective-dated overlap prevention and effective-context resolution
    behavior, covered by new and updated automated tests. The standard backend suite
    passes with 42 tests and no failures, and the Postgres-backed suite passes with
    42 tests, 0 failures and 0 skipped against a real Postgres 16 instance. The one
    model gap discovered (GAP-BE-002-01, missing publishSampleType) was closed in
    the editable contract model.

    '
```
