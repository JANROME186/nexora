# PF-OPS-001 Local Runtime QA Evidence

## Scope

Backlog item: `PF-OPS-001 Create local development compose profile`

Validated implementation:

- Docker Compose runtime under `07-implementation/compose.local.yml`.
- PostgreSQL 16 local service.
- Redis 7 local service.
- OpenTelemetry Collector local service.
- Environment variable example file.
- PostgreSQL initialization script for Platform Foundation schemas.
- Backend `local` profile with PostgreSQL connection.
- Optional local database integration test.

## Commands

```bash
docker compose --env-file .env.example -f compose.local.yml config
docker compose --env-file .env.example -f compose.local.yml up -d
docker compose --env-file .env.example -f compose.local.yml ps
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
