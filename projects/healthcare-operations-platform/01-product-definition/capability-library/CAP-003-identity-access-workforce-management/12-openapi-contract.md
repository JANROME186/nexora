# 12 OpenAPI Contract

The source of truth for Identity, Access & Workforce APIs is:

`05-contracts/contracts/openapi/iam/iam.openapi.yaml`

## Initial endpoint scope

- `POST /v1/employees`
- `GET /v1/employees`
- `GET /v1/employees/{employeeId}`
- `PATCH /v1/employees/{employeeId}`
- `POST /v1/employees/{employeeId}/activate`
- `POST /v1/employees/{employeeId}/deactivate`
- `POST /v1/users/invitations`
- `POST /v1/users/invitations/{invitationId}/accept`
- `GET /v1/users`
- `POST /v1/users/{userId}/suspend`
- `POST /v1/users/{userId}/reactivate`
- `POST /v1/roles`
- `GET /v1/roles`
- `PATCH /v1/roles/{roleId}`
- `POST /v1/users/{userId}/roles`
- `DELETE /v1/users/{userId}/roles/{roleId}`
- `GET /v1/permissions`
- `GET /v1/users/{userId}/effective-permissions`
- `POST /v1/access/evaluate`
