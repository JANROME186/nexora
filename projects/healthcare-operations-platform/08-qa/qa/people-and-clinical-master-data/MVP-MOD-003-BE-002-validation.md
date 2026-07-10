# MVP-MOD-003-BE-002 — Duplicate Detection and Portal Identity Custom Rules Validation

Machine-readable evidence: [MVP-MOD-003-BE-002-validation.yaml](MVP-MOD-003-BE-002-validation.yaml)

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
- `docker compose --env-file .env.example -f compose.local.yml up -d postgres`.
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
