# PF-OPS-001 Local Runtime QA Evidence

## Scope

Backlog item: `PF-OPS-001 Create local development compose profile`

Validated implementation:

- Docker Compose runtime under `07-implementation/compose.local.json`.
- PostgreSQL 16 local service.
- Redis 7 local service.
- OpenTelemetry Collector local service.
- Environment variable example file.
- PostgreSQL initialization script for Platform Foundation schemas.
- Backend `local` profile with PostgreSQL connection.
- Optional local database integration test.

## Commands

```bash
docker compose --env-file .env.example -f compose.local.json config
docker compose --env-file .env.example -f compose.local.json up -d
docker compose --env-file .env.example -f compose.local.json ps
mvn --settings .mvn/settings.xml test
mvn --settings .mvn/settings.xml "-Dhop.local-db-tests=true" "-Dtest=PlatformFoundationLocalDatabaseTest" test
```

## Result

Status: `passed`

Runtime:

- PostgreSQL: healthy.
- Redis: healthy.
- OpenTelemetry Collector: healthy.

Backend tests:

- Normal suite: 5 tests, 0 failures, 0 errors, 1 skipped optional local database test.
- Local database integration test: 1 test, 0 failures, 0 errors, 0 skipped.

## Notes

The OpenTelemetry Collector image does not include shell, `wget` or `curl`; its healthcheck uses `/otelcol-contrib --version` so the local runtime works with the official minimal image.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-PF-OPS-001
  type: qa-evidence
  name: PF-OPS-001 Local Runtime QA Evidence
  version: 1.0.0
  status: passed
  human_readable: PF-OPS-001-local-runtime.md
  machine_readable: PF-OPS-001-local-runtime.md
  backlog_item: PF-OPS-001
  module: MVP-MOD-001
scope:
- Docker Compose local runtime
- PostgreSQL 16 local service
- Redis 7 local service
- OpenTelemetry Collector local service
- Environment variable example file
- PostgreSQL schema initialization
- Backend local PostgreSQL profile
- Optional local database integration test
validation:
  compose_config:
    command: docker compose --env-file .env.example -f compose.local.json config
    working_directory: projects/healthcare-operations-platform/07-implementation
    status: passed
  compose_up:
    command: docker compose --env-file .env.example -f compose.local.json up -d
    working_directory: projects/healthcare-operations-platform/07-implementation
    status: passed
  compose_ps:
    command: docker compose --env-file .env.example -f compose.local.json ps
    working_directory: projects/healthcare-operations-platform/07-implementation
    status: passed
    services:
      postgres: healthy
      redis: healthy
      otel_collector: healthy
  backend_normal_suite:
    command: mvn --settings .mvn/settings.xml test
    working_directory: projects/healthcare-operations-platform/07-implementation/backend
    status: passed
    results:
      tests_run: 5
      failures: 0
      errors: 0
      skipped: 1
  backend_local_database_test:
    command: mvn --settings .mvn/settings.xml "-Dhop.local-db-tests=true" "-Dtest=PlatformFoundationLocalDatabaseTest"
      test
    working_directory: projects/healthcare-operations-platform/07-implementation/backend
    status: passed
    results:
      tests_run: 1
      failures: 0
      errors: 0
      skipped: 0
next_backlog_item:
  id: PF-BE-002
  title: Implement tenant, laboratory and branch commands
```
