# CAP-001 Patient Management - Commands and Queries

## Commands

| Command ID | Command | Result |
|---|---|---|
| CMD-001 | RegisterPatient | Patient registered or duplicate warning. |
| CMD-002 | UpdatePatientProfile | Patient profile updated. |
| CMD-003 | DeactivatePatient | Patient marked inactive. |
| CMD-004 | ReactivatePatient | Patient restored to active status. |
| CMD-005 | BlockPatient | Patient blocked. |
| CMD-006 | RecordPatientConsent | Consent recorded. |
| CMD-007 | AddPatientGuardian | Guardian added. |

## Queries

| Query ID | Query | Result |
|---|---|---|
| QRY-001 | SearchPatients | Paginated patient list. |
| QRY-002 | GetPatientById | Full patient profile with authorization filtering. |
| QRY-003 | GetPatientSummary | Operational summary. |
| QRY-004 | GetPatientTimeline | Patient timeline. |
| QRY-005 | GetPatientConsents | Consent history. |
| QRY-006 | FindPossibleDuplicates | Duplicate candidates. |
