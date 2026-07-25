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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-PF-BE-002
  type: qa-evidence
  name: PF-BE-002 Organization Commands QA Evidence
  version: 1.0.0
  status: passed
  human_readable: PF-BE-002-organization-commands.md
  machine_readable: PF-BE-002-organization-commands.md
backlog_item:
  id: PF-BE-002
  title: Implement tenant, laboratory and branch commands
  module: MVP-MOD-001
  status: complete
implemented_behavior:
- create_tenant
- get_tenant
- create_laboratory
- get_laboratory
- create_branch
- get_branch
- reject_laboratory_without_existing_tenant
- reject_branch_without_existing_laboratory
- persist_organization_records_with_local_postgresql_profile
validation:
  standard_suite:
    command: mvn --settings .mvn/settings.xml test
    working_directory: projects/healthcare-operations-platform/07-implementation/backend
    status: passed
    tests_run: 9
    failures: 0
    errors: 0
    skipped: 2
  local_database_suite:
    command: mvn --settings .mvn/settings.xml "-Dhop.local-db-tests=true" "-Dtest=PlatformFoundationLocalDatabaseTest,OrganizationManagementLocalDatabaseTest"
      test
    working_directory: projects/healthcare-operations-platform/07-implementation/backend
    status: passed
    tests_run: 2
    failures: 0
    errors: 0
    skipped: 0
  compose_services_healthy:
  - postgres
  - redis
  - otel-collector
completion_decision:
  status: complete
  next_backlog_item: PF-BE-003
  next_backlog_title: Implement user account and role assignment baseline
```
