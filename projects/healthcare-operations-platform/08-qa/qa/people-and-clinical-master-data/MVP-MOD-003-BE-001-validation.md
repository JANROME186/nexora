# MVP-MOD-003-BE-001 — People and Clinical Master Data Backend Compilation Validation

Machine-readable evidence: [MVP-MOD-003-BE-001-validation.yaml](MVP-MOD-003-BE-001-validation.yaml)

## Scope

Backlog item `MVP-MOD-003-BE-001` compiles the backend outputs for the People and Clinical Master
Data bounded contexts (`patient-management` and `medical-staff`) from the four modeled MVP-MOD-003
capability packages (BCM-PER-001, BCM-PER-002, BCM-PER-003, BCM-ATT-002). Business requirement
version analyzed: `v0.68.0` (no impact assessment required — matches `last_analyzed_version` in
`BUSINESS_REQUIREMENT_INDEX.yaml`).

## What was compiled

- A new Spring Modulith application module `peopleclinicalmasterdata`
  (`com.nexora.hop.platformfoundation.peopleclinicalmasterdata`) with
  `allowedDependencies: [organizationmanagement, auditcompliance]`.
- Sibling sub-packages `personmanagement`, `patientmanagement`, `doctormanagement` and
  `patientregistration`, each with `adapter/`, `application/` and `domain/` layers.
- Shared value objects `PersonName`, `PersonDocument`, `PersonContact`, `PersonAddress`,
  validation helpers `PeopleValidation` and a single `PeopleExceptionHandler` mapping the shared
  exception hierarchy to RFC-shaped HTTP responses.
- Domain records, repository ports, application services, REST controllers, in-memory adapters and
  JDBC adapters for the Patient aggregate (AGG-001), the Doctor aggregate (AGG-005) and the
  Patient Registration process record.
- Cross-module directory ports `PatientDirectory` and `DoctorDirectory` used by downstream
  contexts to consume `PatientSnapshot` and `DoctorSnapshot` without depending on aggregate
  types (BCM-PER-002 RN-003 / BCM-PER-003 RN-003 boundary policy).
- A new Postgres schema `people` (`db/people-and-clinical-master-data/schema.sql`, 8 tables),
  wired into `application-local.yml`.
- Only the business rules marked `generatable: true` in each capability's `business-rules.yaml`
  are enforced (uniqueness checks, required-field checks, tenant existence, natural-key
  normalization, deceased-terminal guard, retired-terminal guard, audit envelope).
- Every operation marked `generatable: false` in `openapi-source.yaml` (patient merge,
  representative revocation, consent revocation, doctor suspension, credential verification,
  credential revocation, portal access preparation, registration commit, index rebuild and merge
  coordination) is implemented as an explicit hook: it throws
  `PeopleCustomRuleNotImplementedException`, mapped to HTTP 501 with a `ruleId` and
  `backlogItem: MVP-MOD-003-BE-002` in the response body. No custom business logic was invented.

## Tests executed

| Test class | Result |
|---|---|
| `PeopleClinicalMasterDataApiTest` (8 tests) | Passed |
| `PeopleClinicalMasterDataContractTest` (1 test, all 4 openapi-source models covered) | Passed |
| `PeopleClinicalMasterDataLocalDatabaseTest` (1 test, requires Postgres) | Passed |
| Full backend suite (`mvn test`) | 47/47 passed, 5 skipped without Postgres |
| Full backend suite with `-Dhop.local-db-tests=true` against Docker Postgres | 52/52 passed |
| `PlatformFoundationModulithTest` | Passed (module boundaries valid) |

## Model gaps found

None. The four capability packages compiled cleanly against the modeled OpenAPI operations,
business rules and permissions.

## Out of scope (per backlog boundaries)

Complex custom rules (duplicate detection with tenant-configurable confidence, portal identity
linking, credential expiration cascade, patient merge cascade, consent revocation history),
employee portal UI (MVP-MOD-003-FE-001), mobile app and the formal QA validation
`MVP-MOD-003-QA-001` remain out of scope for this backlog item.

## Outcome

`MVP-MOD-003-BE-001` is **closed**. Next backlog item: `MVP-MOD-003-BE-002` (implement duplicate
detection and portal identity custom rules).
