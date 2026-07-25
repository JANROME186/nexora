# CAP-001 Patient Management - OpenAPI Contract Scope

## Contract location

`05-contracts/contracts/openapi/patients/patients.openapi.md`

## API principles

- OpenAPI is the source of truth for external and internal HTTP contracts.
- Breaking changes require versioning and deprecation policy.
- All endpoints must include tenant context through authorization claims or explicitly approved headers.
- All endpoints must return standardized error objects.
- Pagination must follow the shared pagination schema.

## MVP endpoints

| Method | Path | Purpose |
|---|---|---|
| POST | `/v1/patients` | Register patient. |
| GET | `/v1/patients` | Search/list patients. |
| GET | `/v1/patients/{patientId}` | Get patient profile. |
| PATCH | `/v1/patients/{patientId}` | Update patient profile. |
| POST | `/v1/patients/{patientId}/consents` | Record consent. |
| POST | `/v1/patients/{patientId}/guardians` | Add guardian. |
| POST | `/v1/patients/{patientId}/deactivate` | Deactivate patient. |
| POST | `/v1/patients/{patientId}/reactivate` | Reactivate patient. |
| GET | `/v1/patients/duplicates` | Find possible duplicates. |

## Required security scopes

- `patients:create`
- `patients:read`
- `patients:update`
- `patients:deactivate`
- `patients:reactivate`
- `patients:manage-consent`
- `patients:manage-guardian`
