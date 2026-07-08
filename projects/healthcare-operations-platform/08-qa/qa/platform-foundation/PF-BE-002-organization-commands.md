# PF-BE-002 - Organization Commands QA Evidence

## Scope

Backlog item `PF-BE-002` implements the organization-management baseline for tenant, laboratory and branch.

## Implemented Behavior

- Create tenant through `POST /api/platform/tenants`.
- Query tenant through `GET /api/platform/tenants/{tenantId}`.
- Create laboratory through `POST /api/organization/laboratories`.
- Query laboratory through `GET /api/organization/laboratories/{laboratoryId}`.
- Create branch through `POST /api/organization/branches`.
- Query branch through `GET /api/organization/branches/{branchId}`.
- Reject laboratory creation when the tenant does not exist.
- Reject branch creation when the laboratory does not exist.
- Persist organization records in PostgreSQL when the `local` profile is active.

## Validation Commands

```bash
mvn --settings .mvn/settings.xml test
```

Result: passed.

```bash
mvn --settings .mvn/settings.xml "-Dhop.local-db-tests=true" "-Dtest=PlatformFoundationLocalDatabaseTest,OrganizationManagementLocalDatabaseTest" test
```

Result: passed.

## Evidence Summary

- Standard backend suite: 9 tests, 0 failures, 0 errors, 2 skipped optional local database tests.
- Local database suite: 2 tests, 0 failures, 0 errors, 0 skipped.
- Local PostgreSQL profile created and persisted tenant, laboratory and branch records.
- Docker Compose local runtime remained healthy for PostgreSQL, Redis and OpenTelemetry Collector.

## Decision

`PF-BE-002` is complete. The next backlog item is `PF-BE-003 Implement user account and role assignment baseline`.
