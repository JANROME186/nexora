# PF-BE-003 - Identity Access QA Evidence

## Scope

Backlog item `PF-BE-003` implements the identity-access baseline for user accounts and scoped role assignments.

## Implemented Behavior

- Create user account through `POST /api/identity/users`.
- Query user account through `GET /api/identity/users/{userId}`.
- Assign a scoped role through `POST /api/identity/users/{userId}/role-assignments`.
- Reject user creation when the tenant does not exist.
- Reject role assignment when the user does not exist.
- Reject role assignment when `roleCode`, `scope.type` or `scope.id` are missing.
- Persist user accounts and role assignments in PostgreSQL when the `local` profile is active.

## Module Boundary

`identity-access` validates tenant existence through the `TenantDirectory` port exposed at the base package of `organization-management`, keeping cross-context access limited to a read-only lookup instead of internal domain or application types.

## Validation Commands

```bash
mvn --settings .mvn/settings.xml test
```

Result: passed.

```bash
mvn --settings .mvn/settings.xml "-Dhop.local-db-tests=true" "-Dtest=PlatformFoundationLocalDatabaseTest,OrganizationManagementLocalDatabaseTest,IdentityAccessLocalDatabaseTest" test
```

Result: passed.

## Evidence Summary

- Standard backend suite: 14 tests, 0 failures, 0 errors, 3 skipped optional local database tests.
- Local database suite: 3 tests, 0 failures, 0 errors, 0 skipped.
- Local PostgreSQL profile created and persisted user account and role assignment records.
- Docker Compose local runtime remained healthy for PostgreSQL, Redis and OpenTelemetry Collector.

## Decision

`PF-BE-003` is complete. The next backlog item is `PF-BE-004 Implement append-only audit event recording`.
