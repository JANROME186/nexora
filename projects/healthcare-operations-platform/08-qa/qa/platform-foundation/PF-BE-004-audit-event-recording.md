# PF-BE-004 - Audit Event Recording QA Evidence

## Scope

Backlog item `PF-BE-004` implements append-only audit event recording for critical Platform Foundation actions.

## Implemented Behavior

- Record audit events for tenant creation.
- Record audit events for laboratory creation.
- Record audit events for branch creation.
- Record audit events for user account creation.
- Record audit events for scoped role assignment.
- Query audit events through `GET /api/audit/events`.
- Filter audit events by `tenantId`.
- Filter audit events by `subjectId`.
- Persist audit events in PostgreSQL when the `local` profile is active.
- Keep audit writes behind an append-only repository contract and insert-only JDBC adapter.

## Module Boundary

`organizationmanagement` and `identityaccess` record audit events through the `AuditRecorder` port exposed at the base package of `auditcompliance`. They do not depend on internal audit application, domain or adapter classes.

## Validation Commands

```bash
mvn --settings .mvn/settings.xml test
```

Result: passed.

```bash
mvn --settings .mvn/settings.xml "-Dhop.local-db-tests=true" "-Dtest=PlatformFoundationLocalDatabaseTest,OrganizationManagementLocalDatabaseTest,IdentityAccessLocalDatabaseTest,AuditComplianceLocalDatabaseTest" test
```

Result: passed.

## Evidence Summary

- Standard backend suite: 18 tests, 0 failures, 0 errors, 4 skipped optional local database tests.
- Local database suite: 4 tests, 0 failures, 0 errors, 0 skipped.
- Spring Modulith boundary verification passed after exposing audit recording through a public module port.
- Local PostgreSQL profile created and persisted audit event records.
- Docker Compose local runtime remained healthy for PostgreSQL, Redis and OpenTelemetry Collector.

## Decision

`PF-BE-004` is complete. The next backlog item is `PF-FE-001 Create employee portal administration screens`.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-PF-BE-004
  type: qa-evidence
  name: PF-BE-004 Audit Event Recording QA Evidence
  version: 1.0.0
  status: passed
  human_readable: PF-BE-004-audit-event-recording.md
  machine_readable: PF-BE-004-audit-event-recording.md
backlog_item:
  id: PF-BE-004
  title: Implement append-only audit event recording
  module: MVP-MOD-001
  status: complete
implemented_behavior:
- record_tenant_creation_audit_event
- record_laboratory_creation_audit_event
- record_branch_creation_audit_event
- record_user_account_creation_audit_event
- record_role_assignment_audit_event
- query_audit_events
- filter_audit_events_by_tenant
- filter_audit_events_by_subject
- persist_audit_events_with_local_postgresql_profile
- expose_audit_recording_through_public_module_port
validation:
  standard_suite:
    command: mvn --settings .mvn/settings.xml test
    working_directory: projects/healthcare-operations-platform/07-implementation/backend
    status: passed
    tests_run: 18
    failures: 0
    errors: 0
    skipped: 4
  local_database_suite:
    command: mvn --settings .mvn/settings.xml "-Dhop.local-db-tests=true" "-Dtest=PlatformFoundationLocalDatabaseTest,OrganizationManagementLocalDatabaseTest,IdentityAccessLocalDatabaseTest,AuditComplianceLocalDatabaseTest"
      test
    working_directory: projects/healthcare-operations-platform/07-implementation/backend
    status: passed
    tests_run: 4
    failures: 0
    errors: 0
    skipped: 0
  compose_services_healthy:
  - postgres
  - redis
  - otel-collector
completion_decision:
  status: complete
  next_backlog_item: PF-FE-001
  next_backlog_title: Create employee portal administration screens
```
