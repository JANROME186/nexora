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
added to the editable `openapi-source.yaml` model (source of truth) and implemented, closing the gap
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
| VAL-003 | Suite against real Postgres (`-Dhop.local-db-tests=true`) | **not executed** — Docker daemon unavailable in this environment |
| VAL-004 | Spring Modulith boundaries | passed |
| VAL-005 | OpenAPI/contract coverage (incl. new `publishSampleType`) | passed |
| VAL-006 | Former 501 hooks now functional | passed |
| VAL-007 | YAML repository files parseable | passed |
| VAL-008 | Agent-agnostic scan | passed |

New/updated tests: `CatalogCustomRulesApiTest` (10 tests) added; `CatalogTestConfigurationApiTest`
(8 tests) updated so the assertions that previously expected HTTP 501 now assert functional
publication, snapshot, immutability (409), assignment gating, effective resolution and overlap (409)
behavior.

> VAL-003 note: the Postgres-backed run is the only validation not executed, solely because the
> Docker daemon in this environment returned HTTP 500 / API-version errors. The JDBC adapters were
> extended only with additive read queries (`ReferenceRange.findByAnalyteRefId`,
> `PriceList.findByStatus`) that compile and follow the existing JDBC style; publication writes reuse
> the status upsert paths already validated by MVP-MOD-002-BE-001.

## Readiness

- MVP-MOD-002-BE-002: **closed**.
- Blocking gaps: none.
- Next backlog item: **MVP-MOD-002-FE-001** (compile MVP-MOD-002 employee catalog UI outputs).
