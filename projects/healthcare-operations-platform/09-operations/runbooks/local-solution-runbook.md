# HOP Integrated Local Solution Runbook

This is the single local runbook for starting, validating and stopping the Healthcare Operations
Platform solution. Component README files remain useful for detail, but a reviewer should be able to
use this guide first.

Current active backlog item: `COM-MOD-011-FE-001`.

Latest update: `COM-MOD-011-WEB-001` is closed. It compiled the public website frontend at
`07-implementation/public-website/` (React 19 + TypeScript 5 strict + Vite 6), consuming the
anonymous `/api/public/**` surface compiled by `COM-MOD-011-BE-001`: published catalog discovery
(diagnostic services, tests, panels, preparations — each with a list and detail page) and public
appointment/quotation request intake (`BCM-ATT-001 RN-008`, `BCM-ATT-006 RN-009`), with an
explicit client-side cooldown for `BCM-PLT-005 RN-007`'s 429 rate-limit responses since the
backend sends no `Retry-After` header. Added a new local dev runtime on port 4004 (proxying `/api`
to the existing backend on port 8080) and three optional, defaulted environment variables
(`VITE_TENANT_ID`, `VITE_LABORATORY_ID`, `VITE_DEFAULT_BRANCH_ID`); no backend port, database
schema or startup order changed. Added SEO (per-page title/description/canonical/Open Graph,
`robots.txt`, `sitemap.xml`), accessibility (`eslint-plugin-jsx-a11y` plus an automated `jest-axe`
regression check wired into `npm run test`/`quality`), privacy (a `/privacy` notice page, required
consent checkboxes on both request forms) and i18n (es-MX/en-US, no hardcoded strings)
foundations. Materially reduced `TD-UX-002` (documented responsive breakpoints in `styles.css` +
automated accessibility check, the debt's own acceptance criteria) as the reference pattern for
this new module; `employee-portal` itself was not touched, so the debt is not closed. First
coverage baseline for this stack: 97 tests, 34 test files, 0 failures, 98.61% line/statement
coverage. ESLint 0 errors/16 non-blocking warnings; `jscpd` 3.9% duplication (below the 5%
threshold); Prettier clean; `license-checker` MIT 3/UNLICENSED 1; `npm audit` and Trivy fs
(vuln/secret/misconfig, all severities) both 0 findings; agent-agnostic scan 0 real hits; `git
diff --check` clean. Verified locally via `npm run build && npm run preview` (production shell
served correctly). Docker later became reachable in the same session, enabling full live
end-to-end verification against a real backend and Postgres instance for all 10
`/api/public/**` operations through the real dev proxy — this surfaced and fixed a real
pre-existing defect (see "Backend defect fixed" note below). The next active backlog item is
`COM-MOD-011-FE-001` (Content and request administration screens).

**Backend defect fixed during this backlog item's live verification**: `backend/src/main/resources/db/catalog-test-configuration/schema.sql` seeded catalog rows (analytes, sample types, sample requirements, test definitions, diagnostic services) with `status='PUBLISHED'` (uppercase), while every catalog domain class's `STATUS_PUBLISHED` constant is the lowercase literal `published`. A case-sensitive filter therefore silently excluded every seeded catalog row from any published-only view, project-wide — not specific to this backlog item, but blocking verification of its core discovery flow, which is what surfaced it. Fixed by correcting the 10 seed literals to lowercase; no Java source changed.

**Operational note**: because the seed `INSERT`s use `ON CONFLICT ... DO NOTHING`, this fix does not retroactively correct rows already seeded into an existing local database volume. If you set up your local Postgres volume before this fix, run `docker compose --env-file .env -f compose.local.yml down -v` then `up -d` once to get a fresh volume with correctly-cased seed data (any other local-only data in that volume is lost).

Backend regression gates re-run clean after the fix: `mvn -Pquality "-Dhop.local-db-tests=true" clean verify` (324 tests, 0 failures/errors/skipped, coverage unchanged at 83.96%), `checkstyle`/`pmd`/`spotbugs`/`duplicate-finder` (0 new violations), OWASP Dependency-Check (65 dependencies, 0 vulnerabilities) and Trivy fs on the backend directory (0 vulnerabilities/secrets/misconfigurations).

Previous update: `COM-MOD-011-BE-001` is closed. It compiled the backend for HOP's anonymous
public-website surface without introducing a new runtime component, port, environment variable or
startup order. Ten new REST operations are reachable anonymously under `/api/public`:

- `GET /api/public/catalog/{diagnostic-services,tests,panels,preparations}/published`
- `GET /api/public/catalog/{diagnostic-services,tests,panels,preparations}/{id}/published-snapshot`
- `POST /api/public/care-delivery/appointment-requests` (BCM-ATT-001 `RN-008`)
- `POST /api/public/care-delivery/quotation-requests` (BCM-ATT-006 `RN-009`)

A new `publicweb` Spring Modulith module hosts the controllers and depends only on two new named
interfaces: `catalogtestconfiguration::catalog-public-read-port` (published-only snapshots) and
`frontdeskcaredelivery::public-intake-port` (anonymous appointment/quotation intake).
`BCM-PLT-005 RN-007` rate-limit enforcement was compiled as a new
`PublicApiRateLimitInterceptor` co-located with the existing partner interceptor and registered by
`ApiManagementWebConfig` for `/api/public/**`. Two additive DDL migrations use
`ADD COLUMN IF NOT EXISTS`/`ALTER COLUMN DROP NOT NULL`:

- `integration_interoperability.rate_limit_policies` gains `consumer_identification_method
  varchar(32) NOT NULL DEFAULT 'partner_api_key'`. Pre-existing partner policies keep the previous
  behavior.
- `care_delivery.appointments` gains `prospective_full_name`, `prospective_phone`,
  `prospective_email` and relaxes `patient_id` nullability so anonymous `channel = public_website`
  requests can capture a reused BCM-ATT-006 `ProspectiveContact` shape without a registered Patient
  link (`RN-008`).

Because both migrations use `CREATE TABLE IF NOT EXISTS` and `ADD COLUMN IF NOT EXISTS`, a fresh
`docker compose --env-file .env -f compose.local.yml up -d` picks them up automatically; an
already-created local database is upgraded transparently by the additive DDL. Optional new request
headers (`X-Forwarded-For`, `X-Public-Session-Token`) are consumed only by the new interceptor;
requests without them are unaffected, so no existing validation step changes. `TD-BE-015` is
closed (`PublicWebApiTest.publicRateLimitBlocksAnonymousTrafficByIpAddress` verifies rate-limit
enforcement end-to-end); `TD-I18N-002` is further reduced by the new `public.error.*` and
`public.rate_limit.*` es-MX/en-US catalog namespaces. Also fixed a pre-existing modeling vs
routing gap in BCM-SVC-005 (`getPublishedPreparationSnapshot` now registered as a Spring MVC
route). Backend line coverage rose from 83.73% to 83.96% (324 tests, 0 failures/errors/skipped
with `-Dhop.local-db-tests=true` against a running compose.local.yml PostgreSQL 16 container).
OWASP Dependency-Check (108 dependencies, 0 vulnerabilities), Trivy fs (vuln/secret/misconfig,
all severities: 0 findings), YAML parse (1,154 files, 0 errors), agent-agnostic scan (0 real
source-code hits) and `git diff --check` (0 whitespace errors) all pass. The next active backlog
item is `COM-MOD-011-WEB-001` (Compile public website service discovery and conversion flows).

Previous update: `COM-MOD-011-DEF` is closed. It is a definition-only capability-package modeling
backlog item: it added no backend, employee-portal, mobile, patient-portal or doctor-portal
source file, and no runtime component, port, environment variable, startup order or database
schema changed. All 7 COM-MOD-011 capabilities (`BCM-SVC-001/002/003/005`, `BCM-ATT-001/006`,
`BCM-PLT-005`) were confirmed reused from already-modeled/compiled capability packages owned by
MVP-MOD-002, MVP-MOD-004 and MVP-MOD-008, with zero new capability package, aggregate or schema
created. `TD-BE-015` was materially reduced via a new `BCM-PLT-005` `RN-007` and
`RateLimitPolicy.consumerIdentificationMethod` field (later closed by `COM-MOD-011-BE-001`).
Three pre-existing stale roadmap/status pointers and one pre-existing YAML-validity defect (an
unescaped colon in `SOURCE_OF_TRUTH.yaml`) were found and corrected during modeling. Backend
(83.73%), employee-portal (88.24%), mobile (99.21%), patient-portal (94.11%) and doctor-portal
(96.28%) coverage are re-affirmed unchanged.

Previous update: `COM-MOD-010-CLOSEOUT` is closed. It is a documentation and registry
synchronization backlog item only: no backend, employee-portal, mobile, patient-portal or
doctor-portal source file was changed, and no runtime component, port, environment variable,
startup order or database schema changed. It confirmed all 13 COM-MOD-010 capability packages
(`BCM-INV-001..009`, `BCM-QLT-001/003/004/005`) are `module_closed` in
`capability-package-index.yaml` and in each of their `traceability.yaml` files
(`backlog_items.closeout_status: closed`), and confirmed zero open or materially-reduced technical
debt is attributable to COM-MOD-010 after reviewing `technical-debt-index.yaml`. Backend (83.73%),
employee-portal (88.24%), mobile (99.21%), patient-portal (94.11%) and doctor-portal (96.28%)
coverage are re-affirmed unchanged from `COM-MOD-010-QA-001`/`COM-MOD-009` evidence. YAML parse, a
repository-wide stale-pointer sweep and `git diff --check` were executed for this closeout.

Previous update: `COM-MOD-010-QA-001` is closed. It validated end-to-end traceability across all 13
COM-MOD-010 capability packages and added one new backend integration test class
(`InventoryQualityControlsLocalDatabaseTest.java`) exercising the 4 `BCM-QLT` JDBC adapters against
a real local Postgres instance, closing a real coverage gap left by `COM-MOD-010-BE-002`. No new
port, environment variable, startup order or database schema change was introduced. Backend
coverage was corrected and raised from a reproducibly-measured 81.90% (a jacoco.exec
measurement-inflation artifact, same pattern as the earlier MVP-MOD-005-QA-001 correction) to
83.73% (315 tests, 0 failures/errors/skipped); employee-portal coverage confirmed at 88.24% (124
tests, 48 test files). `npm audit`, OWASP Dependency-Check (65 dependencies) and Trivy fs
(vuln/secret/misconfig, all severities) reported 0 vulnerabilities/secrets/misconfigurations.

Earlier update: `COM-MOD-010-FE-001` is closed. It added 11 permission-filtered employee-portal
screens (inventory catalog, reagent profiles, stock lots, purchase orders, combined stock
entries/exits/consumption movements, adjustments, waste disposal, internal quality control runs,
calibrations, equipment profile/availability, maintenance events) and a typed
`inventoryQualityApi` facade consuming the already-closed `COM-MOD-010-BE-001`/`BE-002` REST APIs.
No new port, environment variable, startup order or database schema change was introduced.
Employee-portal validation passed with 124 tests (48 test files), 87.87% line coverage, `npm
audit` 0 vulnerabilities and Trivy fs (vuln/secret/misconfig, all severities) 0 findings.

`MVP-MOD-008 Integration and Migration Readiness` is closed through `MVP-MOD-008-CLOSEOUT`.
Closeout evidence confirms backend quality at 265 tests and 80.49% coverage, employee-portal
quality at 101 tests and 86.47% coverage, and 0 reported vulnerabilities in the recorded
Dependency-Check, npm audit and Trivy evidence.

HOP Enterprise Quality Alignment (`HOP-QA-ALIGN-001` through `HOP-QA-ALIGN-CLOSEOUT`) is closed.
HOP Enterprise Product Foundation Alignment (`HOP-ENT-FOUND-001`) is closed — see
`08-qa/qa/enterprise-foundation/HOP-ENT-FOUND-001-validation.md`.

No functional backlog item is currently paused. `MVP-MOD-005 Cashier and Billing Request` is
closed in full (`MVP-MOD-005-DEF` through `MVP-MOD-005-CLOSEOUT`); `MVP-MOD-006 Laboratory
Workflow` is closed in full (`MVP-MOD-006-DEF` through `MVP-MOD-006-CLOSEOUT`); `MVP-MOD-007
Results and Digital Delivery` is closed in full (`MVP-MOD-007-DEF` through `MVP-MOD-007-CLOSEOUT`):
capability package models, backend compilation and custom rules, employee/patient/doctor portal UI,
mobile result view, and the result access/PDF/notification validation are all closed. Its closeout
(`MVP-MOD-007-CLOSEOUT`) closed TD-BE-010 (diagnostic order cancellation now checks real Sample
state via the laboratoryworkflow SampleReadPort instead of order status alone), found and fixed a
real employee-portal coverage regression (84.44% floor -> 84.03%, restored to 85.50%), measured
patient-portal and doctor-portal coverage for the first time (41.93% and 40.62%, tracked as
TD-FE-008/TD-FE-009), and re-confirmed backend coverage at 78.51% and mobile coverage at 98.87%
with no regressions. `HOP-ENT-FOUND-001` closed the enterprise product foundation alignment
(localization, IAM permission catalog, session baseline, database deliverables, UX/UI,
persistence/contract-generation review, debt burn-down and coverage improvement). Functional
development has moved to `MVP-MOD-008 Integration and Migration Readiness`. `MVP-MOD-008-DEF`
(capability package models for BCM-PLT-004, BCM-PLT-005, BCM-PLT-010) is closed as a
definition-only backlog item that added no runtime component, port, environment variable, startup
order or validation command; this runbook required no edit. `MVP-MOD-008-BE-001` (backend
compilation) is closed: it added two new local-Postgres schemas —
`backend/src/main/resources/db/integration-interoperability/schema.sql` and
`backend/src/main/resources/db/data-migration-portability/schema.sql` — both wired into
`application-local.yml`'s schema-locations list alongside the pre-existing per-module schema files;
no new port, environment variable, startup order or validation command was introduced (the same
documented commands below validate them). `MVP-MOD-008-BE-002` (integration retry/dead-letter, API
deprecation/rate-limit and migration checkpoint custom rules) is closed: it added 3 columns
(`correlation_id`, `next_retry_at`, `dead_letter_reason`) to
`integration_interoperability.integration_message_records` and widened
`data_migration_portability.import_executions.checkpoint` from `varchar(160)` to `text`, both in the
same two schema files above — no new file, port, environment variable, startup order or validation
command. A pre-existing local Postgres container created before this change needs those columns
added manually (`ALTER TABLE ... ADD COLUMN IF NOT EXISTS ...`), since `schema.sql`'s
`CREATE TABLE IF NOT EXISTS` does not retroactively alter an already-created table; a fresh
`docker compose up` picks them up automatically. It also added an `X-Partner-Api-Key` request header
consumed only by the new rate-limit interceptor — requests without it are unaffected, so no existing
validation step changes. `MVP-MOD-008-FE-001` is closed: it added employee-portal integration,
API-governance and migration administration screens with no new runtime port, environment variable,
startup order or infrastructure dependency. `MVP-MOD-008-QA-001` and `MVP-MOD-008-CLOSEOUT` are closed.
`COM-MOD-009-DEF` is closed (modeled BCM-PLT-001). `COM-MOD-009-BE-001` is closed: it compiled portal access structures, authorization endpoints, password hashing, and granular permissions, adding five columns (username, password_hash, failed_login_attempts, locked_until, last_login_at) to identity.user_accounts with backward-compatible migrations. All Maven tests pass. COM-MOD-009-PORTAL-001 (patient portal commercial workflow) is closed: it compiled LoginForm, Dashboard layouts, and secure self-access interceptor backend rules, raising patient portal Vitest coverage to 89.58% (TD-FE-008 closed). COM-MOD-009-PORTAL-002 (doctor portal commercial workflow) is closed: it rebuilt the doctor-portal frontend on its existing dev port (4002, unchanged) and added backend least-privilege enforcement (a doctorId query filter, three new interceptor self-access blocks, and a new ReferringDoctorAuthorizationPort module boundary) with no new runtime component, port, environment variable, startup order or database schema change; doctor portal Vitest coverage rose to 89.86% (TD-FE-009 closed). COM-MOD-009-APP-001 (patient mobile workflow) is closed: it added PATIENT role permissions, permission-filtered profile/appointments/orders/results/notifications routes, localized es-MX/en-US labels, a patientMobileApi facade and patientMobileWorkflowModel to the existing mobile-app component, with no new runtime component, port, environment variable, startup order, database schema change or infrastructure dependency; mobile quality passed and coverage rose to 99.21%. `COM-MOD-009-QA-001` (channel access and privacy evidence) is closed: it validated channel access, privacy, SAST, dependency scans, and Trivy filesystem scans, and closed TD-FE-011 in patient-portal. `COM-MOD-009-CLOSEOUT` (Module closeout and registry update) is closed. `COM-MOD-010-DEF` (Inventory and Internal Quality capability package models) is closed with 13 capability packages modeled and no runtime component, port, environment variable, startup-order change, database schema change or validation-command change. `COM-MOD-010-BE-001` (Compile product, reagent, lot and stock outputs) is closed. It added one new schema file `backend/src/main/resources/db/inventory-and-internal-quality/schema.sql` (nine tables: `inventory_items`, `stock_lots`, `purchase_orders`, `purchase_order_lines`, `stock_entries`, `stock_exits`, `consumption_records`, `inventory_adjustments`, `waste_records`) and appended `classpath:db/inventory-and-internal-quality/schema.sql` to `application-local.yml` `spring.sql.init.schema-locations` alongside the pre-existing per-module schema files. No new port, environment variable, startup order or Docker-init asset was introduced, and the same `mvn -Pquality "-Dhop.local-db-tests=true" clean verify` command validates it. Because `schema.sql` uses `CREATE TABLE IF NOT EXISTS`, a fresh `docker compose up` (or an equivalent freshly-created local Postgres volume) picks up the nine new tables automatically; if an already-created local database is being reused, run `docker compose down -v; docker compose up -d` once so the `inventory_quality` schema and its tables are created. Backend line coverage rose from 80.60% to 82.94% (308 tests / 0 failures / 0 errors / 0 skipped). At that historical point, work continued with `COM-MOD-010-BE-002`.

**Local Postgres schema note (added by HOP-ENT-FOUND-001)**: the local-database-backed backend
tests require the running Postgres container's schema to match
`backend/src/main/resources/db/platform-foundation/schema.sql`. If
`runtime/local/postgres/init/001-create-platform-foundation-schemas.sql` (the Docker init mount)
ever drifts from that file, resync the two files and run the reset steps below before re-running
local-database tests (see `TD-STACK-004`).

## Cashier And Billing Request Smoke

For `MVP-MOD-005-BE-002`, create an accepted diagnostic order or accepted quotation, create a sale through `POST /api/revenue/cashier/sales`, open a cash session, register payment, verify the sale totals, close the cash session, create a billing request from the paid sale, then exercise billing submit/retry/cancel through the provider-agnostic local deterministic fiscal adapter boundary.

For `MVP-MOD-005-FE-001`, use the employee portal's Cash Sessions, Sales and Billing Requests tabs
to drive the same flow end to end from the UI instead of raw HTTP calls.

Quality alignment backlog: `06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.yaml`.

Enterprise foundation alignment backlog:
`06-delivery/commercial-product/HOP_ENTERPRISE_FOUNDATION_ALIGNMENT_BACKLOG.yaml`.

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
8. Cashier and Billing Request backend baseline (`MVP-MOD-005-BE-001`): create an accepted
   diagnostic order or accepted quotation, then `POST /api/revenue/cashier/sales` to create a
   payable sale. Open a cash session with `POST /api/revenue/cashier/sessions`, register a cash or
   card payment with `POST /api/revenue/cashier/sales/{saleId}/payments`, verify
   `GET /api/revenue/cashier/sales/{saleId}` shows `partially_paid` or `paid` totals, and close the
   session with `POST /api/revenue/cashier/sessions/{sessionId}/close`. After a sale is paid,
   `POST /api/revenue/billing-requests` creates an invoice request and
   `GET /api/revenue/billing-requests/{invoiceRequestId}/tax-lines` returns tax lines; submit,
   retry and cancel execute the provider-agnostic local deterministic fiscal adapter boundary
   (`MVP-MOD-005-BE-002`).
9. With a tenant, laboratory and branch selected, open the three new Cashier and Billing Request
   tabs (`MVP-MOD-005-FE-001`): "Cash Sessions" (open a session, list sessions, close a session with
   a counted amount — a counted amount differing from the expected amount with no variance reason
   surfaces `CASH_VARIANCE_REASON_REQUIRED`), "Sales" (create a sale from an accepted diagnostic
   order or accepted quotation, view lines/totals/outstanding balance/payment status, register a
   payment — a payment above the outstanding balance surfaces `PAYMENT_EXCEEDS_OUTSTANDING_BALANCE`
   — and cancel a non-paid sale; a paid sale shows a hint to create its billing request) and
   "Billing Requests" (create a billing request from a paid sale's id — a billing request for a
   sale that is not yet paid surfaces `BILLING_SALE_REQUIRED` — view tax lines, and execute
   submit/retry/cancel against the fiscal adapter boundary). Every action shows a loading, success
   or error banner and financial/destructive actions require an explicit confirmation dialog.
10. With a tenant, laboratory and branch selected, open the 11 new Inventory and Internal Quality
    tabs (`COM-MOD-010-FE-001`): "Catálogo de Inventario" (register/update/discontinue inventory
    items), "Reactivos" (assign/load a reagent profile), "Lotes" (register/quarantine/expire stock
    lots), "Compras" (build purchase-order lines, create, submit/approve/cancel, receive a line),
    "Movimientos de Stock" (record stock entries, exits and consumption), "Ajustes de Inventario"
    and "Mermas" (record adjustments and waste disposal — waste disposal requires an explicit
    confirmation dialog since it is irreversible), "Control de Calidad Interno" (record a QC run
    and override its acceptance decision), "Equipos" (set an equipment profile and change/load its
    availability history), and "Calibraciones"/"Mantenimiento" (record and list calibration and
    maintenance events). Every action shows a loading, success or error banner; empty result sets
    show an explicit empty-state message.

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

OWASP Dependency-Check uses the local advisory database at:

```text
C:\Documents\Proyectos\Laboratorio\dependency-check-data
```

This database is refreshed manually once per day by the project operator or security reviewer. It is
not the responsibility of the Nexora framework or backlog agents to update/download the NVD database
during ordinary backlog execution. Agents must run the scan against the database available at that
moment and document the database path plus freshness timestamp/date in QA/security evidence.

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
- The employee portal's `LaboratoryResult` type (used by `ResultReleaseScreen`, `TechnicalValidationScreen`, `MedicalValidationScreen` and the `MVP-MOD-007-FE-001` Result Search screen) does not match the real `BCM-LAB-006` backend record field-for-field; `MVP-MOD-007-FE-001` worked around this locally for its own screen via response normalization. Tracked as `TD-FE-007`.
- In this sandboxed development environment, Maven runs `--offline` and the backend `-Pquality` profile's Spotless/Checkstyle/PMD/SpotBugs/Dependency-Check plugins are not cached locally, so `QA-003` cannot execute here (plugin resolution failure, not a code finding); `QA-001`/`QA-002` (`mvn test`, including JaCoCo) run successfully offline and remain authoritative in this environment. Run `QA-003` in an environment with network access before a release-readiness or GA gate.

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
