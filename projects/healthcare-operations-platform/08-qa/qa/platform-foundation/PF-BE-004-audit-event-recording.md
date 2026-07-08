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
