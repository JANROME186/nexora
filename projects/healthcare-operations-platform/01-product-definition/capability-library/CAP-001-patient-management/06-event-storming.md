# CAP-001 Patient Management - Event Storming

## Commands

- `RegisterPatient`
- `UpdatePatientProfile`
- `DeactivatePatient`
- `ReactivatePatient`
- `BlockPatient`
- `UnblockPatient`
- `RecordPatientConsent`
- `AddPatientGuardian`
- `DetectPatientDuplicates`

## Domain events

- `PatientRegistrationStarted`
- `PatientDuplicateDetected`
- `PatientRegistered`
- `PatientUpdated`
- `PatientConsentRecorded`
- `PatientGuardianAdded`
- `PatientDeactivated`
- `PatientReactivated`
- `PatientBlocked`
- `PatientUnblocked`

## External events consumed

- `OrderCreated`
- `ResultPublished`
- `InvoiceIssued`
- `PortalAccountCreated`

## Policies

| Policy | Trigger | Action |
|---|---|---|
| Duplicate Detection Policy | `PatientRegistrationStarted` | Search possible matches. |
| Minor Guardian Policy | `RegisterPatient` | Require guardian if minor. |
| Consent Policy | `RecordPatientConsent` | Store consent version and scope. |
| Audit Policy | Any patient mutation | Create audit entry. |

## Read models

- Patient search index.
- Patient summary view.
- Patient timeline.
- Patient portal profile.
