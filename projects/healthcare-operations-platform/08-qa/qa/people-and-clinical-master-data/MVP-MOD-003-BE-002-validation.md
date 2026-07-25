# MVP-MOD-003-BE-002 — Duplicate Detection and Portal Identity Custom Rules Validation

Machine-readable evidence: [MVP-MOD-003-BE-002-validation.md](MVP-MOD-003-BE-002-validation.md)

## Scope

Backlog item `MVP-MOD-003-BE-002` replaces the HTTP 501 custom-rule hooks compiled by
`MVP-MOD-003-BE-001` with functional implementations across the four People and Clinical Master
Data capability packages (BCM-PER-001, BCM-PER-002, BCM-PER-003, BCM-ATT-002). Business
requirement version: `v0.68.0` (no impact assessment required).

## Custom rules implemented

**BCM-PER-001 (Person Management)** — `PersonDuplicateDetectionEngine` (tenant-configurable
weighted confidence scoring across family name, given name, birth date, sex at birth and a
one-way-hashed national identifier), `PersonDocumentUniquenessPolicy`, `TenantPeoplePolicyStore`
(tenant overrides with safe defaults), and `PersonMergeCoordination` for cross-capability merge
coordination.

**BCM-PER-002 (Patient Management, AGG-001)** — `mergePatient` (soft-merge, idempotent, never
deletes), `revokePatientRepresentative`, `revokePatientConsent` (append-only, original evidence
row never mutated), and duplicate-detection consultation wired into `registerPatient`.

**BCM-PER-003 (Doctor Management, AGG-005)** — `suspendDoctor`, `preparePortalAccess`,
`verifyDoctorCredential`, `revokeDoctorCredential`, duplicate-detection consultation wired into
`registerDoctor`, and referring-eligibility (RN-004/RN-006) as a computed query
(`isEligibleAsReferringDoctor`) rather than a change to the already-shipped `status` field
(see `TD-BE-005`).

**BCM-ATT-002 (Patient Registration)** — `commitPatientRegistration` fully implemented as an
orchestration that consults duplicate detection, blocks on unresolved high-confidence matches,
requires representative details for representative registrations, enforces tenant mandatory
consents, and commits exclusively through `PatientManagementService` (AGG-001 ownership
preserved). `startPatientRegistration` now applies the age-of-majority default
(RN-008).

## Endpoints that stopped returning 501

| Endpoint | Capability |
|---|---|
| `POST /api/people/persons/index/rebuild` | BCM-PER-001 |
| `POST /api/people/persons/merges` | BCM-PER-001 |
| `GET /api/people/persons/merges/{id}` | BCM-PER-001 |
| `POST /api/people/patients/{id}/merge` | BCM-PER-002 |
| `POST /api/people/patients/{id}/representatives/{repId}/revoke` | BCM-PER-002 |
| `POST /api/people/patients/{id}/consents/{consentId}/revoke` | BCM-PER-002 |
| `POST /api/people/doctors/{id}/suspend` | BCM-PER-003 |
| `POST /api/people/doctors/{id}/portal-access` | BCM-PER-003 |
| `POST /api/people/doctors/{id}/credentials/{credId}/verify` | BCM-PER-003 |
| `POST /api/people/doctors/{id}/credentials/{credId}/revoke` | BCM-PER-003 |
| `POST /api/care-delivery/patient-registrations/{id}/commit` | BCM-ATT-002 |

## Endpoints still deferred

None. Every operation listed in BE-001's `custom_rule_hooks_deferred_to_be_002` is implemented.

## Tests added or updated

`PeopleClinicalMasterDataApiTest`: rewrote the two tests that previously asserted 501 for patient
and doctor custom rules into `patientMergeRepresentativeAndConsentCustomRulesWork` and
`doctorSuspendPortalAccessAndCredentialCustomRulesWork`; updated the person-search test for the new
202 rebuild and 404 unknown-merge behavior; added
`personMergeCoordinationAppliesPatientMergeWhenBothRecordsArePatients`; replaced the registration
501-commit test with `patientRegistrationCanBeStartedCommittedAndCancelled` and added
`patientRegistrationDefaultsMinorToRepresentativeRegistration` and
`patientRegistrationRequiresMatchResolutionOnHighConfidenceDuplicateAndMandatoryConsent`.

New file `DoctorEligibilityRulesTest` (3 tests) covers `isEligibleAsReferringDoctor` directly, since
it is a cross-context policy consulted through `DoctorDirectory` rather than its own REST operation.

`PeopleClinicalMasterDataContractTest` and `PeopleClinicalMasterDataLocalDatabaseTest` were not
modified — no routes changed and the JDBC paths they exercise are unaffected by the new logic
layered on top.

## Confirmation executed

The original implementation was delivered with a build/test limitation. Follow-up validation
executed the required gates and fixed the issues found during confirmation:

- `mvn --settings .mvn/settings.xml test`: 58 tests, 0 failures, 0 errors, 6 skipped.
- `docker compose --env-file .env.example -f compose.local.json up -d postgres`.
- `mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"`: 58 tests, 0 failures,
  0 errors, 0 skipped.
- Programmatic YAML parse: 470 files OK.
- Trivy filesystem scan for HIGH/CRITICAL vulnerabilities, secrets and misconfigurations: passed.

Fixes applied during confirmation: in-memory credential/representative saves now replace existing
child records by id, patient snapshots follow merge chains, JDBC natural-key searches avoid
PostgreSQL null-parameter type inference failures, and YAML evidence formatting was corrected.

## Out of scope (confirmed)

UI implementation (`MVP-MOD-003-FE-001`), mobile app, patient/doctor self-service portals,
capability-package redesign, and any change to `BUSINESS_REQUIREMENT.md`.

## Outcome

`MVP-MOD-003-BE-002` is **closed**. Next backlog item: `MVP-MOD-003-FE-001` (compile patient and
doctor management UI outputs).

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-MVP-MOD-003-BE-002-001
  type: qa-validation-evidence
  name: MVP-MOD-003-BE-002 Duplicate Detection and Portal Identity Custom Rules Validation
  version: 1.0.0
  status: passed
  human_readable: MVP-MOD-003-BE-002-validation.md
  machine_readable: MVP-MOD-003-BE-002-validation.md
  created_date: 2026-07-09
  owner: Nexora Product Architecture Team
scope:
  backlog_item: MVP-MOD-003-BE-002
  module: MVP-MOD-003 People and Clinical Master Data
  release: REL-001
  execution_flow_stage: custom_implementation
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  bounded_contexts:
  - patient-management
  - medical-staff
  implementation_root: 07-implementation/backend/src/main/java/com/nexora/hop/platformfoundation/peopleclinicalmasterdata/
  predecessor_evidence: 08-qa/qa/people-and-clinical-master-data/MVP-MOD-003-BE-001-validation.md
custom_rules_implemented:
- capability: BCM-PER-001
  rules:
  - RN-002
  - RN-003
  - RN-004
  - RN-007
  new_components:
  - PersonDuplicateDetectionEngine (weighted confidence scoring against PersonDuplicateScoringPolicy;
    family/given name, birth date, sex at birth and hashed national identifier)
  - PersonDocumentUniquenessPolicy (RN-004 cross-context primary document uniqueness)
  - TenantPeoplePolicyStore (tenant-configurable scoring weights, age-of-majority
    years, mandatory consent types; safe defaults, no proprietary dependency)
  - PersonMergeCoordination + PersonMergeCoordinationRepository (RN-007 merge coordination
    across BCM-PER-002/BCM-PER-003 without inventing a Doctor-merge capability not
    present in bcm-per-003-doctor-management/openapi-source.md)
  operations_no_longer_501:
  - POST /api/people/persons/index/rebuild (rebuildPersonSearchIndex)
  - POST /api/people/persons/merges (initiatePersonMergeCoordination)
  - GET /api/people/persons/merges/{id} (getPersonMergeCoordination)
  note: detectPersonDuplicates already returned normalized-key matches in BE-001;
    BE-002 adds the tenant-configurable weighted confidence score to the same operation's
    response.
- capability: BCM-PER-002
  rules:
  - RN-002
  - RN-005
  - RN-006
  - RN-007
  new_behavior:
  - 'registerPatient now calls PersonDocumentUniquenessPolicy.ensureUnique and PersonDuplicateDetectionEngine.detect
    (non-blocking: recorded via audit, does not reject the command) before persisting.

    '
  - mergePatient implemented as a soft-merge (status=MERGED, mergedIntoPatientId set
    on the source record; survivor untouched; idempotent on re-merge; findSnapshot
    follows the merge chain with a 10-hop guard).
  - revokePatientRepresentative implemented (marks the representative record inactive;
    audited).
  - revokePatientConsent implemented as an append-only new PatientConsent row (granted=false,
    evidenceReference points at the original consent id); the original row is never
    mutated, consistent with JdbcPatientRepository.saveConsent() having no ON CONFLICT
    clause.
  operations_no_longer_501:
  - POST /api/people/patients/{patientId}/merge (mergePatient)
  - POST /api/people/patients/{patientId}/representatives/{representativeId}/revoke
    (revokePatientRepresentative)
  - POST /api/people/patients/{patientId}/consents/{consentId}/revoke (revokePatientConsent)
- capability: BCM-PER-003
  rules:
  - RN-002
  - RN-004
  - RN-005
  - RN-006
  - RN-007
  new_behavior:
  - 'registerDoctor now calls PersonDocumentUniquenessPolicy.ensureUnique and PersonDuplicateDetectionEngine.detect
    (non-blocking, audited) before persisting.

    '
  - suspendDoctor implemented (sets a suspension reason/timestamp; audited).
  - preparePortalAccess implemented (validates at least one verified credential exists;
    returns a portal-identity preparation record).
  - verifyDoctorCredential implemented (rejects credentials already expired at verification
    time with PeopleConflictException).
  - revokeDoctorCredential implemented (marks a credential revoked; audited).
  - RN-004/RN-006 (referring eligibility) implemented as a computed query, DoctorManagementService#isEligibleAsReferringDoctor
    / DoctorDirectory# isEligibleAsReferringDoctor, rather than a change to the already-shipped
    generatable status field. See TD-BE-005.
  operations_no_longer_501:
  - POST /api/people/doctors/{doctorId}/suspend (suspendDoctor)
  - POST /api/people/doctors/{doctorId}/portal-access (preparePortalAccess)
  - POST /api/people/doctors/{doctorId}/credentials/{credentialId}/verify (verifyDoctorCredential)
  - POST /api/people/doctors/{doctorId}/credentials/{credentialId}/revoke (revokeDoctorCredential)
- capability: BCM-ATT-002
  rules:
  - RN-001
  - RN-002
  - RN-003
  - RN-005
  - RN-006
  - RN-008
  new_behavior:
  - 'commitPatientRegistration implemented as a full orchestration (CommitPatientRegistrationCommand)
    that (1) consults PersonDuplicateDetectionEngine and blocks with 409 REGISTRATION_MATCH_RESOLUTION_REQUIRED
    on a high-confidence match without resolvedExistingPatientId, (2) requires representative
    details for representative_registration kind (409 REGISTRATION_REPRESENTATIVE_REQUIRED),
    (3) commits exclusively through PatientManagementService (RegisterPatientCommand
    / AttachPatientRepresentativeCommand / RecordPatientConsentCommand), never persisting
    Patient state itself, preserving AGG-001 ownership, (4) enforces the tenant''s
    mandatory consent types (409 REGISTRATION_CONSENT_MISSING).

    '
  - 'startPatientRegistration implements RN-008: a plain new_patient intake with a
    birth date under the tenant''s age-of-majority policy is defaulted to representative_registration.

    '
  operations_no_longer_501:
  - POST /api/care-delivery/patient-registrations/{id}/commit (commitPatientRegistration)
operations_still_deferred: []
model_gaps_identified:
- id: GAP-001
  identified_during: MVP-MOD-003-QA-001
  rule: RN-005 (bcm-per-003-doctor-management)
  description: 'RN-005 requires a scheduler:credential_expiration_watcher that proactively
    transitions ProfessionalCredential to STATUS_EXPIRED and flags the doctor for
    re-verification. Only a reactive check exists (verifyDoctorCredential rejects
    verifying an already-expired credential); no scheduled job assigns STATUS_EXPIRED
    or raises a re-verification signal. isEligibleAsReferringDoctor still computes
    eligibility correctly in real time against expiresAt, so no incorrect eligibility
    decision results from this gap today.

    '
  tracked_as: 08-qa/technical-debt/TD-BE-007-credential-expiration-scheduler-missing.md
  blocking: false
- id: GAP-002
  identified_during: MVP-MOD-003-QA-001
  rule: RN-008 (bcm-per-002-patient-management and bcm-per-003-doctor-management)
  description: 'RN-008 requires tenant-configured masking of document/credential numbers
    in read models. PatientSnapshot and DoctorSnapshot both apply PersonDocument.maskedNumber(),
    but that is a fixed, tenant-agnostic masking algorithm rather than a tenant-configurable
    policy; document numbers are never rendered unmasked, so there is no raw data-exposure
    regression. Credential numbers are not exposed via DoctorSnapshot at all, so that
    half of the rule is moot in the current read model.

    '
  tracked_as: 08-qa/technical-debt/TD-BE-008-read-model-masking-not-tenant-configurable.md
  blocking: false
out_of_scope_confirmed:
- UI implementation (MVP-MOD-003-FE-001) was not started.
- Mobile app was not touched.
- Patient/doctor self-service portals were not built (preparePortalAccess only prepares
  the backend-side eligibility/identity record consumed by a future portal capability).
- No capability-package redesign; BUSINESS_REQUIREMENT.md was not modified.
- A Doctor-merge aggregate command was not invented (bcm-per-003-doctor-management
  has no mergeDoctor operation); cross-kind merge coordination is recorded decision-only
  (STATUS_RECORDED_NO_AGGREGATE_OPERATION) instead.
tests_added_or_updated:
- PeopleClinicalMasterDataApiTest:
  - name: patientMergeRepresentativeAndConsentCustomRulesWork
    coverage: Replaces the old 501-assertion test.
  - name: doctorSuspendPortalAccessAndCredentialCustomRulesWork
    coverage: Replaces the old 501-assertion test.
  - name: personSearchReturnsBothPatientsAndDoctorsAndDuplicateDetectionAudits
    coverage: Updated; index rebuild now asserts 202 with counts and merge-with-missing-ids
      now asserts 404.
  - name: personMergeCoordinationAppliesPatientMergeWhenBothRecordsArePatients
    coverage: New patient merge coordination coverage.
  - name: patientRegistrationCanBeStartedCommittedAndCancelled
    coverage: Replaces the old 501-assertion test and covers full commit success,
      re-commit conflict and cancel.
  - name: patientRegistrationDefaultsMinorToRepresentativeRegistration
    coverage: New RN-008 plus RN-003 coverage.
  - name: patientRegistrationRequiresMatchResolutionOnHighConfidenceDuplicateAndMandatoryConsent
    coverage: New RN-006 plus RN-005 coverage.
- DoctorEligibilityRulesTest:
    status: new_file
    tests: 3
    coverage: 'Direct service-level coverage of isEligibleAsReferringDoctor across
      credential verification, suspension and credential expiration, since this policy
      is consumed through DoctorDirectory rather than exposed as its own REST operation.

      '
- PeopleClinicalMasterDataContractTest:
    status: not_modified
    coverage: 'No new or removed routes. All operations exercised were already declared
      in openapi-source.md and registered as 501 hooks in BE-001.

      '
- PeopleClinicalMasterDataLocalDatabaseTest:
    status: not_modified
    coverage: 'The JDBC path exercised by this test (patient, doctor and registration
      creation) is unaffected by the new custom-rule logic added on top.

      '
validations:
- id: VAL-001
  name: Backend compiles
  method: mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
  working_directory: 07-implementation/backend
  result: passed
  detail: 'Compilation completed as part of the database-backed Maven test run. A
    prior confirmation attempt surfaced real issues in in-memory replacement semantics
    and PostgreSQL null-parameter typing; those were corrected before this passing
    run.

    '
- id: VAL-002
  name: Backend test suite passes without a local database
  method: mvn --settings .mvn/settings.xml test
  working_directory: 07-implementation/backend
  result: passed
  detail: 'Executed before the database-backed confirmation. Result: 58 tests, 0 failures,
    0 errors, 6 skipped. The skipped tests are the local database tests intentionally
    disabled unless -Dhop.local-db-tests=true is provided.

    '
- id: VAL-003
  name: Backend test suite passes against real Postgres
  method: docker compose -f compose.local.json up -d postgres; mvn --settings .mvn/settings.xml
    test -Dhop.local-db-tests=true
  working_directory: 07-implementation
  result: passed
  detail: 'Executed with hop-local-postgres healthy. Result: 58 tests, 0 failures,
    0 errors, 0 skipped. This confirmed the patient, doctor, registration and platform
    JDBC paths against PostgreSQL.

    '
- id: VAL-004
  name: Spring Modulith module boundaries remain valid
  method: PlatformFoundationModulithTest (ApplicationModules.of(PlatformFoundationApplication.class).verify())
  result: passed
  detail: PlatformFoundationModulithTest executed inside the database-backed Maven
    test run.
- id: VAL-005
  name: OpenAPI/contract coverage
  method: PeopleClinicalMasterDataContractTest cross-checks every operation in all
    4 openapi-source.md files against registered Spring MVC routes.
  result: passed
  detail: 'PeopleClinicalMasterDataContractTest executed inside the database-backed
    Maven test run. No routes were added, removed or renamed relative to BE-001.

    '
- id: VAL-006
  name: Deferred custom rule hooks are now functional, not 501
  method: PeopleClinicalMasterDataApiTest asserts merge/revoke-representative/revoke-consent/
    suspend/portal-access/verify-credential/revoke-credential/commit endpoints now
    return 2xx/4xx business outcomes instead of HTTP 501.
  result: passed
  detail: 'PeopleClinicalMasterDataApiTest and DoctorEligibilityRulesTest executed
    inside the database-backed Maven test run. Every operation listed in BE-001''s
    deferred custom-rule hooks now has a non-501 outcome; operations_still_deferred
    is empty.

    '
- id: VAL-007
  name: YAML repository files remain parseable
  method: python/yaml parse of projects/healthcare-operations-platform and nexora-framework
    YAML files
  result: passed
  detail: 'YAML OK: 470 files.'
- id: VAL-008
  name: Agent-agnostic scan
  method: rg scan for named-agent/vendor/runtime references in active BE-002 state
    and evidence files
  result: passed
  detail: '0 matches for named AI agents, assistant vendors or specific AI platform
    runtimes in active BE-002 project state, execution prompt, runbook and evidence
    files.

    '
- id: VAL-009
  name: Security quality gate
  method: See 08-qa/security-quality/MVP-MOD-003-BE-002/security-quality-evidence.md.
  result: passed
execution_confirmation:
  description: 'The original BE-002 implementation was delivered with a stated shell/build
    limitation. Follow-up validation executed the required build, tests, YAML parser
    and security scan, corrected the issues found, and converted this backlog item
    from pending build confirmation to closed.

    '
  issues_found_and_fixed:
  - InMemoryDoctorRepository.saveCredential and InMemoryPatientRepository.saveRepresentative
    now replace existing child records by id instead of appending stale versions.
  - PatientController snapshot retrieval now follows patient merge chains through
    PatientManagementService.findSnapshot.
  - JdbcPatientRepository and JdbcDoctorRepository natural-key searches now build
    optional predicates dynamically, avoiding PostgreSQL null-parameter type inference
    failures.
  - YAML evidence formatting was corrected and validated programmatically.
  commands_executed:
  - mvn --settings .mvn/settings.xml test
  - docker compose --env-file .env.example -f compose.local.json up -d postgres
  - mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
  - python/yaml parse of 470 framework and project YAML files
  - trivy fs --scanners vuln,secret,misconfig --severity HIGH,CRITICAL --exit-code
    1 --no-progress .
blocking_gaps: []
readiness:
  mvp_mod_003_be_002_status: closed
  ready_for_next_backlog_item: MVP-MOD-003-FE-001
  next_backlog_item_name: Compile patient and doctor management UI outputs
  rationale: 'All custom rules explicitly scoped to MVP-MOD-003-BE-002 for BCM-PER-001,
    BCM-PER-002, BCM-PER-003 and BCM-ATT-002 are implemented and covered by executed
    backend tests. No capability was redesigned, AGG-001 Patient and AGG-005 Doctor
    ownership boundaries were preserved, no operation in scope remains at HTTP 501,
    Maven tests pass with real Postgres, YAML files parse and Trivy reports zero HIGH/CRITICAL
    findings for the scanned dependency targets.

    '
```
