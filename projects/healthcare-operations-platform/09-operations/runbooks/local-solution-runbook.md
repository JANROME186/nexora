# HOP Integrated Local Solution Runbook

This is the single local runbook for starting, validating and stopping the Healthcare Operations
Platform solution. Component README files remain useful for detail, but a reviewer should be able to
use this guide first.

## Prerequisites

- Docker with Compose support.
- Java 21.
- Maven 3.9.x or compatible Maven execution with the backend `.mvn/settings.xml`.
- Node.js and npm.
- PowerShell or a compatible shell on Windows.

## Local URLs

| Component | URL / Port |
|---|---|
| Backend API | `http://localhost:8080` |
| Employee portal | `http://localhost:5173` |
| PostgreSQL | `localhost:5432` |
| Redis | `localhost:6379` |
| OpenTelemetry gRPC | `localhost:4317` |
| OpenTelemetry HTTP | `localhost:4318` |
| OpenTelemetry health | `http://localhost:13133` |

## Startup Order

### 1. Prepare Environment

Working directory:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation
```

Create `.env` only if it does not exist:

```powershell
Copy-Item .env.example .env
```

### 2. Start Infrastructure

Working directory:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation
```

Start PostgreSQL, Redis and OpenTelemetry Collector:

```powershell
docker compose --env-file .env -f compose.local.yml up -d
```

Check containers:

```powershell
docker compose --env-file .env -f compose.local.yml ps
```

### 3. Start Backend API

Open a new terminal.

Working directory:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\backend
```

Start the backend with the local PostgreSQL profile:

```powershell
mvn --settings .mvn/settings.xml spring-boot:run "-Dspring-boot.run.profiles=local"
```

Expected API:

- `http://localhost:8080/actuator/health`
- `http://localhost:8080/api/platform/health`

### 4. Start Employee Portal

Open a new terminal.

Working directory:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\employee-portal
```

Install dependencies when `node_modules` is missing or `package-lock.json` changed:

```powershell
npm install
```

Start the web app:

```powershell
npm run dev -- --host 127.0.0.1
```

Open:

```text
http://localhost:5173
```

The web app proxies `/api` calls to `http://localhost:8080`.

## Health Checks

From any terminal:

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health
```

```powershell
Invoke-RestMethod http://localhost:8080/api/platform/health
```

```powershell
Invoke-WebRequest http://localhost:5173
```

Expected result:

- Backend health returns `UP`.
- Platform health returns `UP`.
- Employee portal returns HTTP 200.

## Smoke Validation

1. Open `http://localhost:5173`.
2. Confirm the employee portal shell loads.
3. Create or inspect a tenant through the Platform Foundation UI or API.
4. Open Diagnostic Catalog navigation and confirm catalog screens load.
5. Register a patient, register a doctor and search persons through the People and Clinical
   Master Data API (`POST /api/people/patients`, `POST /api/people/doctors`,
   `GET /api/people/persons/search?tenantId=...` — now includes tenant-configurable
   duplicate-detection confidence scoring). Start a patient registration through
   `POST /api/care-delivery/patient-registrations` and commit it through
   `POST .../{id}/commit` (blocks with HTTP 409 on an unresolved high-confidence duplicate or a
   missing mandatory consent). Patient merge, representative/consent revocation, doctor
   suspension, portal-access preparation and credential verify/revoke are all functional
   (`MVP-MOD-003-BE-002`) and no longer return HTTP 501.
6. With a tenant, laboratory and branch selected in the employee portal (Tenants/Laboratories/
   Branches tabs), open the four new People and Clinical Master Data tabs (`MVP-MOD-003-FE-001`):
   "People Search" (search, duplicate detection with a confidence badge, merge coordination),
   "Patients" (register, snapshot, representative attach/revoke, consent record/revoke, merge),
   "Doctors" (register, snapshot, credential attach/verify/revoke, suspend, prepare portal access)
   and "Patient Registrations" (start, commit with a visual high-confidence duplicate candidate
   list on a 409 conflict, cancel). Every action shows a loading, success or error banner and
   destructive actions (revoke, merge, suspend, cancel) require an explicit confirmation dialog.

## Quality Validation

Backend:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\backend
mvn --settings .mvn/settings.xml test
```

Backend with local PostgreSQL running:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\backend
mvn --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
```

Employee portal:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\employee-portal
npm run typecheck
npm run test:coverage
npm run build
npm audit --audit-level=high
```

Mobile foundation:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\mobile-app
npm run typecheck
npm test
```

Note: the mobile foundation currently reuses the employee portal TypeScript and Vitest toolchain.
Run `npm install` in `employee-portal` first when needed.

## Stop

Stop the employee portal terminal with `Ctrl+C`.

Stop the backend terminal with `Ctrl+C`.

Stop infrastructure:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation
docker compose --env-file .env -f compose.local.yml down
```

## Reset Local Data

This deletes local PostgreSQL and Redis data.

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation
docker compose --env-file .env -f compose.local.yml down -v
```

## Troubleshooting

Backend cannot connect to PostgreSQL:

- Confirm `docker compose --env-file .env -f compose.local.yml ps` shows PostgreSQL running.
- Confirm `.env` contains `HOP_DB_URL=jdbc:postgresql://localhost:5432/hop`.
- Confirm port `5432` is not occupied by another PostgreSQL instance.

Employee portal API calls fail:

- Confirm backend is running on `http://localhost:8080`.
- Confirm `employee-portal/vite.config.ts` still proxie