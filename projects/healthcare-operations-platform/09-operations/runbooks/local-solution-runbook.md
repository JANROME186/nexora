# HOP Integrated Local Solution Runbook

This is the single local runbook for starting, validating and stopping the Healthcare Operations
Platform solution. Component README files remain useful for detail, but a reviewer should be able to
use this guide first.

Current active backlog item: `MVP-MOD-005-FE-001`.

HOP Enterprise Quality Alignment (`HOP-QA-ALIGN-001` through `HOP-QA-ALIGN-CLOSEOUT`) is closed.

Paused functional backlog item: none — `MVP-MOD-005-BE-002` is closed; `MVP-MOD-005-FE-001` is active.

## Cashier And Billing Request Smoke

For `MVP-MOD-005-BE-002`, create an accepted diagnostic order or accepted quotation, create a sale through `POST /api/revenue/cashier/sales`, open a cash session, register payment, verify the sale totals, close the cash session, create a billing request from the paid sale, then exercise billing submit/retry/cancel through the provider-agnostic local deterministic fiscal adapter boundary.

Quality alignment backlog: `06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.yaml`.

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
7. With a tenant, laboratory and branch selected, open the two new Front Desk and Care Delivery
   tabs (`MVP-MOD-004-FE-001`): "Front Desk" (start a walk-in or scheduled reception visit,
   confirm identity, update priority, advance to admission, abandon — the queue lists in the
   tenant-configurable priority order the backend returns) and "Diagnostic Orders" (create a
   walk-in, appointment, admission or quotation-conversion order with one or more test/panel
   lines, then price, accept, complete or cancel it; order detail shows the immutable patient,
   doctor, branch and pricing snapshots captured at order time). Cancelling an accepted or
   in-progress order requires an override justification of at least 15 characters
   (`ORDER_CANCELLATION_OVERRIDE_REQUIRED`, HTTP 409) shown as a plain-text business error. Every
   action shows a loading, success or error banner and destructive actions (abandon, cancel)
   require an explicit confirmation dialog. Appointment Scheduling, Admission Management and
   Quotation Management screens remain tracked as `TD-FE-006`.

## Quality Validation

Backend standard tests:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\backend
mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml test
```

Backend with local PostgreSQL running:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\backend
mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
```

Backend enterprise quality profile:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\backend
mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Pquality verify checkstyle:checkstyle pmd:pmd pmd:cpd spotbugs:spotbugs cyclonedx:makeAggregateBom duplicate-finder:check
```

Backend dependency vulnerability scan, all severities:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\backend
mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Pquality org.owasp:dependency-check-maven:check
```

Employee portal enterprise quality profile:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\employee-portal
npm run quality
npm audit --audit-level=low
```

Mobile foundation enterprise quality profile:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\mobile-app
npm run quality
```

Note: the mobile foundation currently reuses the employee portal TypeScript and Vitest toolchain.
Run `npm install` in `employee-portal` first when needed.

Integrated all-severity vulnerability, secret and misconfiguration scan:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation
trivy fs --scanners vuln,secret,misconfig --exit-code 1 --no-progress --skip-dirs "backend/.m2,backend/target,employee-portal/node_modules,employee-portal/dist,mobile-app/node_modules" .
```

OWASP ZAP DAST baseline for the employee portal, with infrastructure, backend and employee portal already running. On Docker Desktop with a WSL2 backend, `--network host` does not bridge to the
Windows host, so use `--add-host` and target `host.docker.internal`; `vite.config.ts`'s
`server.allowedHosts` must include `"host.docker.internal"` or Vite returns HTTP 403 to the scanner:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation
docker run --rm --add-host=host.docker.internal:host-gateway -v "${PWD}/../08-qa/security-quality/HOP-QA-ALIGN-004:/zap/wrk" ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t http://host.docker.internal:5173 -r zap-employee-portal.html -J zap-employee-portal.json -m 2
```

OWASP ZAP API scan for the backend, with infrastructure and backend already running. The backend
must expose a live OpenAPI document at `/v3/api-docs` (via `springdoc-openapi-starter-webmvc-api`):

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation
docker run --rm --add-host=host.docker.internal:host-gateway -v "${PWD}/../08-qa/security-quality/HOP-QA-ALIGN-004:/zap/wrk" ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py -t http://host.docker.internal:8080/v3/api-docs -f openapi -r zap-backend-api.html -J zap-backend-api.json
```

If Maven, Java, Node, npm, Docker, network access or audit endpoints are missing or blocked, request support and keep the current backlog item open. Do not replace mandatory executable gates with manual source review.

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
- Confirm `employee-portal/vite.config.ts` still proxies `/api` to `http://localhost:8080`.

Mobile tests cannot find TypeScript or Vitest:

- Run `npm install` in `07-implementation/employee-portal` first.

## Known Limitations

- Mobile app is currently a renderer-agnostic TypeScript foundation, not a native runnable app.
- Mobile line coverage measurement is blocked by the shared-toolchain reuse pattern described above; tracked as `TD-APP-002`.
- DAST (employee portal baseline, backend API scan) executed successfully during `HOP-QA-ALIGN-004`; `TD-QA-001` is closed.
- The employee portal dev server does not set `Content-Security-Policy` or `Cross-Origin-Embedder-Policy` (a production-strength policy would break Vite's HMR); tracked as `TD-FE-005`, must close before any production deployment.
- A malformed empty-key query/form parameter causes an unhandled 500 on `POST /api/platform/tenants`; tracked as `TD-QA-004`.
- Release supply-chain gates are configured, but release-policy hardening remains tracked as `TD-BE-004`.
- Message externalization and magic-string remediation baseline established (`HOP-QA-ALIGN-005`, `TD-I18N-001` closed); remaining full-adoption work (backend API code field, full frontend/mobile i18n-library adoption) tracked as `TD-I18N-002`.
- The employee portal has no Appointment Scheduling, Admission Management or Quotation Management screens yet (`MVP-MOD-004-FE-001` delivered Front Desk/Reception and Diagnostic Orders only); administrators with API access are not blocked. Tracked as `TD-FE-006`.

## Component Detail

- `07-implementation/README.md`
- `07-implementation/backend/README.md`
- `07-implementation/employee-portal/README.md`
- `07-implementation/mobile-app/README.md`

## Feedback

If this runbook is incomplete, ambiguous or still requires hidden manual component-by-component
knowledge for basic startup, register feedback under:

```text
08-qa/framework-feedback/
```
