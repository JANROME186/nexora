# Platform Foundation Backend

Backend implementation for `MVP-MOD-001 Platform Foundation`.

## Scope

This backend skeleton establishes the Java 21, Spring Boot 3.x and Spring Modulith baseline for:

- `organizationmanagement`
- `identityaccess`
- `auditcompliance`
- `observability`

The package layout follows hexagonal boundaries inside each context:

- `domain`
- `application`
- `adapter.in.web`
- `adapter.out.memory`
- `adapter.out.jdbc`

## Run

```bash
mvn spring-boot:run
```

The application exposes:

- `GET /actuator/health`
- `GET /api/platform/health`
- `POST /api/platform/tenants`
- `GET /api/platform/tenants/{tenantId}`
- `POST /api/organization/laboratories`
- `GET /api/organization/laboratories/{laboratoryId}`
- `POST /api/organization/branches`
- `GET /api/organization/branches/{branchId}`

## Test

```bash
mvn --settings .mvn/settings.xml test
```

The project includes `.mvn/settings.xml` so local validation uses a repository inside this backend folder and does not depend on workstation-wide Maven settings.

Run the optional local database integration test only after the local runtime is started:

```bash
mvn --settings .mvn/settings.xml "-Dhop.local-db-tests=true" "-Dtest=PlatformFoundationLocalDatabaseTest,OrganizationManagementLocalDatabaseTest" test
```

## Local Database Profile

The `local` profile uses PostgreSQL through the JDBC adapter and initializes the platform foundation schema idempotently.

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

Expected environment variables:

- `HOP_DB_URL`
- `HOP_DB_USERNAME`
- `HOP_DB_PASSWORD`

The Docker Compose runtime is defined in `../compose.local.yml`.
