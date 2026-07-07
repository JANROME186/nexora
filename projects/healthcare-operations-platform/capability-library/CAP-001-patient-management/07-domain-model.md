# CAP-001 Patient Management - DDD Domain Model

## Bounded context

`Patient Management Context`

## Aggregate roots

| Aggregate | Purpose |
|---|---|
| Patient | Governs identity, demographics, status and lifecycle. |
| PatientConsent | Governs consent scope, version and validity. |
| PatientGuardian | Governs minor/responsible party relationship. |

## Entities

- Patient.
- PatientIdentifier.
- PatientContactPoint.
- PatientAddress.
- PatientGuardian.
- PatientConsent.
- PatientDocument.
- PatientClinicalProfile.
- PatientCommunicationPreference.

## Value objects

- PatientId.
- TenantId.
- BranchId.
- FullName.
- BirthDate.
- Age.
- SexAtBirth.
- EmailAddress.
- PhoneNumber.
- Address.
- ConsentScope.
- PatientStatus.
- DuplicateMatchScore.

## Domain services

| Service | Responsibility |
|---|---|
| PatientDuplicateDetector | Calculates potential duplicate matches. |
| PatientEligibilityService | Validates whether a patient can be used in operational flows. |
| PatientPrivacyPolicyService | Evaluates data visibility based on actor, tenant and country pack. |
| PatientConsentPolicyService | Evaluates required consent by action/channel. |

## Invariants

- Patient must always belong to exactly one tenant.
- Patient status transitions must follow the state machine.
- Minor patient must have guardian when policy requires it.
- Consent must be versioned and auditable.
- Sensitive fields must be protected by authorization policies.
