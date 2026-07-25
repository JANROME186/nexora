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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-PF-BE-003
  type: qa-evidence
  name: PF-BE-003 Identity Access QA Evidence
  version: 1.0.0
  status: passed
  human_readable: PF-BE-003-identity-access.md
  machine_readable: PF-BE-003-identity-access.md
backlog_item:
  id: PF-BE-003
  title: Implement user account and role assignment baseline
  module: MVP-MOD-001
  status: complete
implemented_behavior:
- create_user_account
- get_user_account
- assign_scoped_role_to_user
- reject_user_creation_without_existing_tenant
- reject_role_assignment_without_existing_user
- reject_role_assignment_without_role_code_or_scope
- persist_user_accounts_and_role_assignments_with_local_postgresql_profile
validation:
  standard_suite:
    command: mvn --settings .mvn/settings.xml test
    working_directory: projects/healthcare-operations-platform/07-implementation/backend
    status: passed
    tests_run: 14
    failures: 0
    errors: 0
    skipped: 3
  local_database_suite:
    command: mvn --settings .mvn/settings.xml "-Dhop.local-db-tests=true" "-Dtest=PlatformFoundationLocalDatabaseTest,OrganizationManagementLocalDatabaseTest,IdentityAccessLocalDatabaseTest"
      test
    working_directory: projects/healthcare-operations-platform/07-implementation/backend
    status: passed
    tests_run: 3
    failures: 0
    errors: 0
    skipped: 0
  compose_services_healthy:
  - postgres
  - redis
  - otel-collector
completion_decision:
  status: complete
  next_backlog_item: PF-BE-004
  next_backlog_title: Implement append-only audit event recording
```
