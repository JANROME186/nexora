# 07 Implementation

Use this folder when implementation code or generated implementation assets are colocated in this repository.

Current status:

- `MVP-MOD-001 Platform Foundation` implementation has started.
- Backend skeleton is located in `backend/`.
- Employee portal administration web app is located in `employee-portal/`.
- Local runtime profile is located in `compose.local.yml`.

Implementation order:

1. `PF-BE-001` Backend project skeleton.
2. `PF-OPS-001` Local development compose profile.
3. `PF-BE-002` Tenant, laboratory and branch commands.
4. `PF-BE-003` User account and role assignment baseline.
5. `PF-BE-004` Append-only audit event recording.
6. `PF-FE-001` Employee portal administration screens.
7. `PF-APP-001` Mobile app foundation.
8. `PF-QA-001` Smoke and contract tests.

## Local Runtime

Create a local environment file from the example when needed:

```bash
copy .env.example .env
```

Start local dependencies:

```bash
docker compose --env-file .env -f compose.local.yml up -d
```

Check local dependencies:

```bash
docker compose --env-file .env -f compose.local.yml ps
```

Run the backend against local PostgreSQL:

```bash
cd backend
mvn --settings .mvn/settings.xml spring-boot:run -Dspring-boot.run.profiles=local
```

Stop local dependencies:

```bash
docker compose --env-file .env -f compose.local.yml down
```

Remove local dependency data:

```bash
docker compose --env-file .env -f compose.local.yml down -v
```
