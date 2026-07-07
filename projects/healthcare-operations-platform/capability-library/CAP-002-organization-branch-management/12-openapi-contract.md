# 12 OpenAPI Contract

The source of truth for Organization & Branch APIs is:

`contracts/openapi/organizations/organizations.openapi.yaml`

## Initial endpoint scope

- `POST /v1/tenants`
- `GET /v1/tenants/{tenantId}`
- `PATCH /v1/tenants/{tenantId}`
- `POST /v1/tenants/{tenantId}/activate`
- `POST /v1/tenants/{tenantId}/suspend`
- `POST /v1/organizations`
- `GET /v1/organizations`
- `GET /v1/organizations/{organizationId}`
- `PATCH /v1/organizations/{organizationId}`
- `POST /v1/branches`
- `GET /v1/branches`
- `GET /v1/branches/{branchId}`
- `PATCH /v1/branches/{branchId}`
- `POST /v1/branches/{branchId}/activate`
- `POST /v1/branches/{branchId}/deactivate`
- `POST /v1/branches/{branchId}/addresses`
- `POST /v1/branches/{branchId}/schedules`
- `POST /v1/branches/{branchId}/services`
- `DELETE /v1/branches/{branchId}/services/{serviceCode}`
- `GET /v1/branches/{branchId}/availability`

## Contract Rules

- OpenAPI is the API source of truth.
- No backend route may exist without OpenAPI definition.
- No breaking change is allowed without version migration.
- All endpoints must enforce tenant isolation.
