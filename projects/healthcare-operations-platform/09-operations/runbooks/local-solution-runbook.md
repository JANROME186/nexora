# HOP Integrated Local Solution Runbook

Before running commands, load the local toolchain inventory:

`../../03-architecture/technology-architecture/local-toolchain-inventory.md`

Before handing a backlog to a commercial execution agent, generate or review a compact prompt:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --task-id COM-MOD-017-BE-002 `
  --title "Implement marketplace entitlement, compatibility and billing-adapter custom rules" `
  --summary-ref projects/healthcare-operations-platform/08-qa/handoffs/COM-MOD-017-BE-001-summary.md
```

As of `NXF-CTX-002`, this is mandatory framework bootstrap behavior. The normal path requires
Python 3.11+, Ollama, `qwen2.5-coder:0.5b`, `rg` and `git`; missing Ollama or a missing required
model blocks prompt generation until the local execution stack is repaired.

This is the single local runbook for starting, validating and stopping the Healthcare Operations
Platform solution. Component README files remain useful for detail, but a reviewer should be able to
use this guide first.

Current active backlog item: `HOP-HARD-WEB-001` (previous stale pointer here read `COM-MOD-015-CLOSEOUT`, corrected by `HOP-HARD-APP-001`).

Latest update: `HOP-HARD-APP-001` is closed. Closed `COM-MOD-014-PORTAL-001` (Imaging study delivery views): patient-portal and doctor-portal each gained a read-only Imaging tab against the existing BCM-IMG-007/BCM-IMG-008 backend (`RadiologySignatureController`/`ImagingStudyDeliveryController`), scoped to the caller's own delivered studies via a new `callerRoleCode`/`callerId` self-access convention mirroring `patientResultHistoryApi.getPatientHistoryAsDoctor`. Found and fixed a real pre-existing authorization gap along the way: those two controllers had zero patient/doctor-role-scoped authorization before this item (only the generic employee `SCREEN_IMAGING_*` permission plus a raw `X-Tenant-Id` header) -- fixed with 2 new permission codes (`PORTAL_PATIENT_IMAGING_VIEW`, `PORTAL_DOCTOR_IMAGING_VIEW`), 2 new `HopAuthorizationInterceptor` self-access bypass blocks (GET-only; mutating verbs remain employee-only) and new `ImagingStudyDeliveryService`/`RadiologySignatureService` overloads that verify real ownership downstream (patient self-match, or a doctor referral check reusing the existing `ReferringDoctorAuthorizationPort` unchanged from `ResultHistoryService`), throwing a new `ImagingAccessDeniedException` (403). `TD-APP-001` reviewed materially_reduced_unchanged (corrected a stale ~569-line mobile-app size figure to the actual 1,158/2,172 lines); `TD-UX-003` reviewed, remains genuinely blocked on the (unselected) native renderer stack. Remediated a pre-existing high-severity npm audit finding (`brace-expansion`) in patient-portal, doctor-portal and mobile-app via `npm audit fix`. Coverage floors: backend 84.77% -> 84.86%, patient portal 94.11% -> 94.42%, doctor portal 96.28% -> 96.55%, mobile 99.21% (unchanged), employee portal 91.68% (unchanged, untouched), public website 98.61% (unchanged, untouched). QA evidence: `08-qa/qa/final-hardening/HOP-HARD-APP-001-validation.md`, security quality evidence: `08-qa/security-quality/HOP-HARD-APP-001/security-quality-evidence.md`, handoff: `08-qa/handoffs/HOP-HARD-APP-001-summary.md`.

Previous update: `COM-MOD-015-CLOSEOUT` is closed. Module `COM-MOD-015 AI Overlay` is `module_closed`; all `BCM-AI-001` through `BCM-AI-008` packages are marked `module_closed` in `capability-package-index.md` and their respective `capability-package.md` files and `traceability.md` matrices. Coverage floors remain backend 70.16%, employee portal 91.00%, public website 98.61%, mobile 99.21%, patient portal 94.11%, doctor portal 96.28%. Material reduction recorded for `TD-FMT-001`, `TD-BE-017`, `TD-BE-022`, `TD-I18N-002`, and `TD-UX-001`. QA evidence: `08-qa/qa/ai-overlay/COM-MOD-015-CLOSEOUT-validation.md`, security quality evidence: `08-qa/security-quality/COM-MOD-015-CLOSEOUT/security-quality-evidence.md`, handoff: `08-qa/handoffs/COM-MOD-015-CLOSEOUT-summary.md`.

Previous update: `COM-MOD-015-QA-001` is closed. Safety, explainability and human-control evidence validated across all 8 AI Overlay sub-packages. Fixed TD-BE-022 (dead review-reason error code, missing review-decision immutability guard). Added vendor-neutrality static scan. QA evidence: `08-qa/qa/ai-overlay/COM-MOD-015-QA-001-validation.md`. Next active backlog item: `COM-MOD-015-CLOSEOUT`.

Previous update: `COM-MOD-014-CLOSEOUT` is closed. Module `COM-MOD-014 Imaging Operations` is `module_closed`; all `BCM-IMG-001` through `BCM-IMG-008` packages are marked `module_closed`. Coverage floors remain backend 84.65%, employee-portal 90.85%, public website 98.61%, mobile 99.21%, patient portal 94.11%, doctor portal 96.28%.

Previous update: `COM-MOD-017-QA-001` is closed. Integrated marketplace validation ran 4 traceability sweeps (`openapi-source.md` vs. the 6 marketplace controllers, IAM permissions across `PermissionCode.java`/`RolePermissionCatalog.java`/`EndpointPermissionRegistry.java`/`permissions.ts`, `ui-model.md` vs. the 4 employee-portal screens, es-MX/en-US i18n key parity) and found/fixed 3 real doc-vs-implementation drifts in capability-package model documents only (`openapi-source.md` path mismatches plus 1 undocumented `getPackage` endpoint; `permissions.md`/`ui-model.md` documented an unimplemented 15-code fine-grained permission model while the shipped system correctly uses the platform's coarse 4-code `SCREEN_MARKETPLACE_*` model, `TD-IAM-002` pattern) -- no production Java or TypeScript source changed, no port, environment variable, Docker service or startup-order changed. Debt-first: closed `TD-BE-018` (all 5 of 5 named custom_implementation_points now closed via the `TD-BE-019` chain closed by `COM-MOD-017-FE-001`); `TD-FE-012` re-confirmed still open/non-blocking. Backend regression gates re-run clean -- `mvn -Pquality "-Dhop.local-db-tests=true" clean verify` (484 tests, 0 failures/errors/skipped, coverage unchanged at 84.65%), checkstyle/PMD/SpotBugs/duplicate-finder reproduced the same pre-existing baseline with 0 new findings, OWASP Dependency-Check (72 dependencies, 0 vulnerabilities). Employee-portal `npm run quality` re-run clean (224 tests, 65 files, 0 failures, coverage unchanged at 90.68%). Trivy fs 0 findings. Next active backlog item is `COM-MOD-017-CLOSEOUT`.

Previous update: `COM-MOD-017-FE-001` is closed. Compiled the 4 marketplace employee-portal screens (`MarketplacePackagesScreen`, `MarketplaceOffersScreen`, `MarketplaceEntitlementsScreen`, `MarketplaceInstallationsScreen`) covering `BCM-PLT-011`'s full `ui-model.md` `employee_portal.screens` scope, a typed `marketplaceApi.ts` facade over the 4 marketplace controllers, and IAM/menu wiring (`permissions.ts`/`AppShell.tsx`/`App.tsx`, `MARKETPLACE_OPERATOR`/`TENANT_ADMIN` roles). Closed `TD-BE-019` for real -- the install control is genuinely gated on real tenant entitlement runtime state. `npm run quality` passed (224 tests, 65 files, 0 failures; employee-portal coverage 89.75% -> 90.68%); registered `TD-FE-012` (10 residual npm audit devDependency-only findings). No runtime component, port, environment variable, database schema, Docker service or startup-order changed.

Earlier update: `COM-MOD-017-BE-002` is closed. Closed 4 of `TD-BE-018`'s 5 custom_implementation_points (entitlement policy evaluator, compatibility evaluation, billing adapter retry/idempotency, installation rollback audit trail); the 5th was repointed to `TD-BE-019`. Found and fixed a real pre-existing infrastructure defect (`TD-BE-020`): `application.properties` unconditionally excluded `DataSourceAutoConfiguration` for every profile, silently breaking every local-profile JDBC adapter repo-wide. Backend coverage raised to a reproducible 84.65% (484 tests, 0 failures/errors/skipped, Docker Compose PostgreSQL 16 up).

Previous update: `COM-MOD-016-QA-001` is closed. Commercial readiness validation confirmed COM-MOD-016-DEF/DOC-001/OPS-001/COM-001 (**COM-MOD-016 Commercial Launch and Customer Enablement**) are complete, consistent and traceable. Found and fixed 4 stale-pointer/registry defects (`capability-package-index.md` + 7 `traceability.md` `commercial_enablement` blocks, `PROJECT_STATE.md` `completed_backlog_items`, `SOURCE_OF_TRUTH.md` missing `sources:` keys). Registered `TD-QA-008` (open, non-blocking). No secrets, PII, vendor lock-in or forbidden execution-status markers found. Preserved all 6 stack coverage floors. Full repository sweeps (YAML parse, stale pointers, evidence state, agent-agnostic, secrets, git diff check) passed clean. Next active backlog item: `COM-MOD-016-CLOSEOUT`.

Previous update: `COM-MOD-016-COM-001` is closed. Formally completed pricing package, sales demo and launch readiness assets (**COM-MOD-016 Commercial Launch and Customer Enablement**). Created commercial packages, capability matrix, pricing model, upgrade/downgrade criteria, sales demo script, demo data checklist, sales enablement one-pager, buyer personas, value proposition, launch readiness checklist and customer acceptance/commercial handoff assets under `06-delivery/commercial-product/`, preserved all 6 stack coverage floors. Full repository sweeps (YAML parse, stale pointers, evidence state, agent-agnostic, secrets, git diff check) passed clean. Next active backlog item: `COM-MOD-016-QA-001`.

Previous update: `COM-MOD-016-OPS-001` is closed. Formally completed operational support, escalation, and release governance documentation (**COM-MOD-016 Commercial Launch and Customer Enablement**). Created 10 governance specifications and master index under `09-operations/governance/` (`GOV-SPEC-001` through `GOV-SPEC-010`), integrated with `onboarding/` and `runbooks/`, preserved all 6 stack coverage floors. Full repository sweeps (YAML parse, stale pointers, evidence state, agent-agnostic, secrets, git diff check) passed clean. Next active backlog item: `COM-MOD-016-COM-001`.

Previous update: `COM-MOD-016-DOC-001` is closed. Formally completed customer onboarding and configuration guides (**COM-MOD-016 Commercial Launch and Customer Enablement**). All 8 onboarding guides and specifications created under `09-operations/onboarding/` (`ONB-GUIDE-001` through `ONB-GUIDE-008`). Preserved all 6 stack coverage floors. Full repository sweeps (YAML parse, stale pointers, evidence state, agent-agnostic, secrets, git diff check) passed clean. Next active backlog item: `COM-MOD-016-OPS-001`.

Previous update: `COM-MOD-013-QA-001` is closed. Integrated validation of COM-MOD-013 found and
closed a major persistence-wiring defect, `TD-DB-005`: `application-local.properties`'s
`spring.sql.init.schema-locations` never registered `db/external-quality-and-compliance/schema.sql`,
compounded by an inverted `@Profile` on the 4 externalqualitycompliance JDBC/in-memory repository
pairs (`@Profile("!local & !test")` on the real JDBC classes instead of `@Profile("local")`, the
convention used by every other module). Together these meant External Quality Control, CAPA, Audit
Management and Quality Event Intake data was silently persisted in memory only -- lost on every
backend restart -- instead of to `hop-local-postgres`. Fixed both root causes; re-ran the
pre-existing `ExternalQualityComplianceLocalDatabaseTest` live against real PostgreSQL (passed,
previously would have failed with `relation "external_quality_evaluations" does not exist` once
only the profile fix was applied). Backend coverage rose from a clean-rebuild 82.57% to 84.24% at
that point (381 tests, 0 failures/errors/skipped), above the previously recorded 84.14% floor.
Also fixed 2 SpotBugs High findings (`DM_DEFAULT_ENCODING`, `NM_SAME_SIMPLE_NAME_AS_SUPERCLASS`), 5
Medium `CT_CONSTRUCTOR_THROW` findings, 1 hardcoded i18n string and 1 `TD-FE-010` function-size
violation in `ComplianceEvidenceScreen.tsx` (employee-portal coverage 89.74% -> 89.75%, 187 tests,
60 files, lint warnings 51 -> 50). A follow-up real DAST pass (OWASP ZAP, required since backend
and employee-portal are both runnable surfaces) executed `zap-api-scan.py` against the full backend
OpenAPI surface (939 URLs, all COM-MOD-013 endpoints included) and `zap-baseline.py` with the Ajax
Spider against the employee portal (125 URLs). The API scan found and this item fixed one further
real defect, `TD-QA-007`: an abrupt client disconnect mid multipart upload to `POST /api/documents`
caused an unhandled 500 (`GlobalExceptionHandler` had no mapping for
`org.springframework.web.multipart.MultipartException`); fixed by adding that mapping to 400 plus a
regression test, confirmed by a clean re-scan (0 FAIL-NEW/0 WARN-NEW, up from 2 WARN-NEW),
raising backend coverage to 84.25% (382 tests). The portal baseline scan found 0 FAIL-NEW and 6
WARN-NEW, all matching the already-known `TD-FE-005` (CSP/COEP, deferred to the production hosting
layer) or dev-server-only artifacts (Vite HMR token, dev-mode source comments, dev-asset caching,
an informational scan-tuning hint) with no production relevance. `vite.config.ts`'s dev-proxy
target, previously hardcoded to `localhost:8080`, now reads an optional `HOP_BACKEND_URL`
environment variable (falls back to the existing default when unset) so the portal could reach the
backend on its session-only `8090` port for this scan. DAST reports saved under
`08-qa/security-quality/COM-MOD-013-QA-001/`. Registered new debt `TD-IAM-004` (5 controllers
assign a synthetic random tenant id instead of the authenticated request's real tenant; deferred
pending a Spring Modulith module-boundary decision -- deny-by-default authorization itself is
unaffected). OWASP Dependency-Check (72 deps), npm audit and Trivy (backend/employee-portal/
repo-wide, all severities) reported 0 vulnerabilities/secrets/misconfigurations. Advanced next active
backlog item to `COM-MOD-013-CLOSEOUT`.

Previous update: `COM-MOD-013-FE-001` is closed. Compiled the Advanced Quality and Compliance
employee-portal UI for External Quality Control (`BCM-QLT-002`), CAPA Management (`BCM-QLT-006`),
Audit Management (`BCM-QLT-007`), Compliance Evidence (`BCM-PLT-007` / `BCM-PLT-008`) and Quality
Event Intake. Added the typed `externalQualityComplianceApi` facade, IAM permission-filtered
navigation, `QUALITY_MANAGER` role and es-MX/en-US message catalogs; materially reduced
`TD-I18N-002` by retrofitting `AuditEventsScreen`. No new runtime component, port, environment
variable, database schema, Docker service or startup order was introduced. Employee-portal
validation passed: 187 tests, 60 files, 0 failures, line coverage raised 88.68% -> 89.74%, npm
audit 0 vulnerabilities, Trivy fs 0 vulnerabilities/secrets/misconfigurations, build/duplication/
format/license/typecheck clean. Next active backlog item: `COM-MOD-013-QA-001`.

Earlier update: `COM-MOD-012-QA-001` is closed. Validated all 8 COM-MOD-012 capabilities live against a running backend. Found and fixed a real resilience defect: the readiness probe did not reflect database connectivity (`management.endpoint.health.group.readiness.include` was unset), fixed by scoping the include to `application-local.properties` and re-verified live via a real `docker stop`/`start` of `hop-local-postgres` (readiness correctly `DOWN`/503 then `UP`; liveness stays `UP` throughout, no unnecessary restart). Executed a dedicated OWASP ZAP API scan against the full backend surface (353 URLs, deferred by BE-001) that found and this item fixed 2 real defects: `TD-QA-005` (a null byte or oversized string value reaching JDBC caused an unhandled 500 across `laboratoryworkflow` and `cashsales`, fixed via a narrow `GlobalExceptionHandler` mapping keyed on PostgreSQL SQLState class 22) and `TD-QA-006` (`AuthController.initiateAssistance` returned 500 instead of 404 for a nonexistent `assistedUserId`, fixed by widening `IdentityAccessExceptionHandler`'s exception-advice scope); a final rescan confirmed 0 FAIL-NEW/0 WARN-NEW/118 PASS. A ZAP baseline scan against the unchanged employee portal found 0 FAIL-NEW. Executed a real backup (`pg_dump`, SHA-256 checksum, `pg_restore --list` showing 415 TOC entries) and restore rehearsal (isolated database, matching row counts). Confirmed the 3 remaining `COM-MOD-012-BE-001` infrastructure forward pointers (distributed trace export, provisioned Grafana/Prometheus/Loki, SLO/SLA alerting) still require infrastructure not available locally and registered `TD-OBS-001` rather than closing them. No new runtime component, port, environment variable or startup order was introduced; port `8080` on this shared local machine was occupied by an unrelated, pre-existing process for a different project, so this session's validation used `server.port=8090` for the backend only (no runbook or config change -- the canonical documented port remains 8080). 367 tests (0 failures, up from 362), backend coverage raised 84.11% -> 84.14%. Next active backlog item: `COM-MOD-012-CLOSEOUT`.

Previous update: `COM-MOD-012-OPS-002` is closed. 10 executable runbook pairs (observability, health/readiness/liveness, metrics/logs/traces validation, backup, restore, incident response, rollback incident handoff, tenant-impact triage, evidence collection, post-incident review) plus an index README were added under `09-operations/runbooks/`, built on `production-deployment-strategy.md`. Every local-executable command was cross-checked against real repository state; unimplemented telemetry and unprovisioned shared-environment infrastructure were documented per-runbook rather than silently marked passed. `TD-DB-004` was materially reduced via `tenant-impact-triage-runbook.md`'s cross-tenant leakage check. No runtime, port, environment variable or startup order changed.

Previous update: `COM-MOD-011-CLOSEOUT` is closed. Formally closed the `COM-MOD-011 Public Website and Digital Growth` module. All 7 capability packages (`BCM-SVC-001/002/003/005`, `BCM-ATT-001/006`, `BCM-PLT-005`) are confirmed `module_closed` in `capability-package-index.md` and their respective `traceability.md` files. Technical debt items `TD-BE-015` and `TD-UX-002` are closed with zero open or blocking technical debt attributable to `COM-MOD-011`. Coverage baselines across all 6 Delivered Stacks were re-affirmed: backend 83.99% (327 tests), employee portal 88.68% (154 tests), public website 98.61% (97 tests), mobile 99.21%, patient portal 94.11%, doctor portal 96.28%. OWASP Dependency-Check, npm audit, and Trivy fs scans confirmed 0 vulnerabilities/secrets/misconfigurations. Repository YAML parse, stale-pointer sweep, and `git diff --check` passed clean.

Previous update: `COM-MOD-011-QA-001` is closed. Integrated quality, privacy, SEO, accessibility, and security validation for COM-MOD-011 was executed clean across all 7 capability packages (`BCM-SVC-001/002/003/005`, `BCM-ATT-001/006`, `BCM-PLT-005`) with 0 vulnerabilities, 0 security findings, 0 test failures, and 0 coverage regressions (backend 83.99%, public website 98.61%, employee portal 88.68%, mobile 99.21%, patient portal 94.11%, doctor portal 96.28%). Maven quality profile (327 tests), OWASP Dependency-Check (0 vulnerabilities), npm audit on public-website and employee-portal (0 vulnerabilities), Trivy fs (0 vulnerabilities, 0 secrets, 0 misconfigurations), repository YAML parse (1,157 files, 0 errors), agent-agnostic scan (0 hits), and git diff --check (0 whitespace errors) passed clean. Stale backlog pointers in all 7 capability package traceability.md files and capability-package-index.md were updated with COM-MOD-011-QA-001 validation entries.

Previous update: `COM-MOD-011-FE-001` is closed. It compiled the content and public-request
administration screens in the existing employee portal at
`07-implementation/employee-portal/`: `PublicContentReviewScreen` consumes the same anonymous
`/api/public/catalog/**/published` endpoints the public website itself calls (deliberately
distinct from the internal catalog-admin API already owned by `DiagnosticCatalogScreen`, so no
`tenantId`/audit/internal field can leak into the staff view by construction);
`PublicAppointmentRequestsScreen` and `PublicQuotationRequestsScreen` consume the existing
internal `/api/care-delivery/appointments` and `/api/care-delivery/quotations` endpoints, filtered
client-side to `channel=="public_website"` and the pending status, with Confirm/Reject and
Issue/Reject actions reusing existing confirm/cancel/issue endpoints — no new backend action
endpoint was created. No new runtime, port or environment variable: this backlog item adds
screens to the already-running employee-portal (port 5173). One additive database column:
`care_delivery.quotations.channel varchar(40)` (nullable, idempotent, no volume reset needed) —
see "Backend defect fixed" note below.

Found and fixed a real defect along the way: `QuotationRequest` had no `channel` field, unlike
`AppointmentSlot`, so public-website-submitted quotation drafts could not be reliably
distinguished from staff-initiated ones. Added `QuotationRequest.channel` (mirroring
`AppointmentSlot`'s `CHANNEL_*` constants); `QuotationManagementService.start()` defaults to
`channel=employee_portal` when omitted (preserving every untouched caller) and rejects
`channel=public_website` from internal callers; `startPublic()` always stamps
`channel=public_website` server-side regardless of input. Because this touched the backend, its
full Maven quality gate was re-run: `mvn -Pquality "-Dhop.local-db-tests=true" clean verify`
(327 tests, 0 failures, backend line coverage 83.96% → 83.99%).

Added 3 new `ScreenKey`/`PermissionCode` pairs (`SCREEN_PUBLIC_CONTENT_REVIEW`,
`SCREEN_PUBLIC_APPOINTMENT_REQUESTS`, `SCREEN_PUBLIC_QUOTATION_REQUESTS`), granted to `ADMIN`
(automatic) and `FRONT_DESK`, with navigation tabs hidden (not disabled) for other roles; all
visible text uses new namespaced es-MX/en-US message groups. Closed `TD-UX-002` (not just
materially reduced): retrofitted the same documented responsive breakpoint set
(`--hop-bp-sm/md/lg`) and automated `jest-axe` accessibility check `COM-MOD-011-WEB-001`
established as the reference pattern into `employee-portal` itself, this debt's originally
discovered `affected_area`, closing the `remaining_scope` that item left open;
`eslint-plugin-jsx-a11y` (newly added) surfaced and fixed one real finding
(`ConfirmDialog.tsx`'s `autoFocus`). Employee-portal coverage rose 88.24% → 88.68% with 154 tests
(54 test files, 0 failures); ESLint 0 errors/38 non-blocking warnings, all pre-existing (0 new
from this item's own files).

**Backend vulnerability fixed during this backlog item's quality gates**: Trivy fs on the backend
directory found 1 MEDIUM vulnerability — `tools.jackson.core:jackson-databind` 3.1.4
(`CVE-2026-59889`, the Jackson 3.x line managed by `spring-boot-starter-parent`). Pinned to 3.1.5
in `backend/pom.xml`, mirroring the existing pin already there for the classic Jackson 2.x line
(a prior CVE); re-scan confirmed 0. OWASP Dependency-Check's first run (108 dependencies, 0
vulnerable) predated this CVE being ingested into its database; a re-verification run could not
complete due to an unrelated stale lock file from another project sharing this machine's local
environment — Trivy's independent confirmation is treated as authoritative. `npm audit` and Trivy
fs (frontend and backend, vuln/secret/misconfig, all severities) reported 0 findings after the
fix; agent-agnostic scan reported 4 false positives (CSS `cursor:` property) and 0 real hits;
`git diff --check` clean. The next active backlog item is `COM-MOD-011-QA-001` (Public web, SEO
and privacy evidence).

Previous update: `COM-MOD-011-WEB-001` is closed. It compiled the public website frontend at
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

**Operational note**: because the seed `INSERT`s use `ON CONFLICT ... DO NOTHING`, this fix does not retroactively correct rows already seeded into an existing local database volume. If you set up your local Postgres volume before this fix, run `docker compose --env-file .env -f compose.local.json down -v` then `up -d` once to get a fresh volume with correctly-cased seed data (any other local-only data in that volume is lost).

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
`docker compose --env-file .env -f compose.local.json up -d` picks them up automatically; an
already-created local database is upgraded transparently by the additive DDL. Optional new request
headers (`X-Forwarded-For`, `X-Public-Session-Token`) are consumed only by the new interceptor;
requests without them are unaffected, so no existing validation step changes. `TD-BE-015` is
closed (`PublicWebApiTest.publicRateLimitBlocksAnonymousTrafficByIpAddress` verifies rate-limit
enforcement end-to-end); `TD-I18N-002` is further reduced by the new `public.error.*` and
`public.rate_limit.*` es-MX/en-US catalog namespaces. Also fixed a pre-existing modeling vs
routing gap in BCM-SVC-005 (`getPublishedPreparationSnapshot` now registered as a Spring MVC
route). Backend line coverage rose from 83.73% to 83.96% (324 tests, 0 failures/errors/skipped
with `-Dhop.local-db-tests=true` against a running compose.local.json PostgreSQL 16 container).
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
unescaped colon in `SOURCE_OF_TRUTH.md`) were found and corrected during modeling. Backend
(83.73%), employee-portal (88.24%), mobile (99.21%), patient-portal (94.11%) and doctor-portal
(96.28%) coverage are re-affirmed unchanged.

Previous update: `COM-MOD-010-CLOSEOUT` is closed. It is a documentation and registry
synchronization backlog item only: no backend, employee-portal, mobile, patient-portal or
doctor-portal source file was changed, and no runtime component, port, environment variable,
startup order or database schema changed. It confirmed all 13 COM-MOD-010 capability packages
(`BCM-INV-001..009`, `BCM-QLT-001/003/004/005`) are `module_closed` in
`capability-package-index.md` and in each of their `traceability.md` files
(`backlog_items.closeout_status: closed`), and confirmed zero open or materially-reduced technical
debt is attributable to COM-MOD-010 after reviewing `technical-debt-index.md`. Backend (83.73%),
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
`application-local.properties`'s schema-locations list alongside the pre-existing per-module schema files;
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
`COM-MOD-009-DEF` is closed (modeled BCM-PLT-001). `COM-MOD-009-BE-001` is closed: it compiled portal access structures, authorization endpoints, password hashing, and granular permissions, adding five columns (username, password_hash, failed_login_attempts, locked_until, last_login_at) to identity.user_accounts with backward-compatible migrations. All Maven tests pass. COM-MOD-009-PORTAL-001 (patient portal commercial workflow) is closed: it compiled LoginForm, Dashboard layouts, and secure self-access interceptor backend rules, raising patient portal Vitest coverage to 89.58% (TD-FE-008 closed). COM-MOD-009-PORTAL-002 (doctor portal commercial workflow) is closed: it rebuilt the doctor-portal frontend on its existing dev port (4002, unchanged) and added backend least-privilege enforcement (a doctorId query filter, three new interceptor self-access blocks, and a new ReferringDoctorAuthorizationPort module boundary) with no new runtime component, port, environment variable, startup order or database schema change; doctor portal Vitest coverage rose to 89.86% (TD-FE-009 closed). COM-MOD-009-APP-001 (patient mobile workflow) is closed: it added PATIENT role permissions, permission-filtered profile/appointments/orders/results/notifications routes, localized es-MX/en-US labels, a patientMobileApi facade and patientMobileWorkflowModel to the existing mobile-app component, with no new runtime component, port, environment variable, startup order, database schema change or infrastructure dependency; mobile quality passed and coverage rose to 99.21%. `COM-MOD-009-QA-001` (channel access and privacy evidence) is closed: it validated channel access, privacy, SAST, dependency scans, and Trivy filesystem scans, and closed TD-FE-011 in patient-portal. `COM-MOD-009-CLOSEOUT` (Module closeout and registry update) is closed. `COM-MOD-010-DEF` (Inventory and Internal Quality capability package models) is closed with 13 capability packages modeled and no runtime component, port, environment variable, startup-order change, database schema change or validation-command change. `COM-MOD-010-BE-001` (Compile product, reagent, lot and stock outputs) is closed. It added one new schema file `backend/src/main/resources/db/inventory-and-internal-quality/schema.sql` (nine tables: `inventory_items`, `stock_lots`, `purchase_orders`, `purchase_order_lines`, `stock_entries`, `stock_exits`, `consumption_records`, `inventory_adjustments`, `waste_records`) and appended `classpath:db/inventory-and-internal-quality/schema.sql` to `application-local.properties` `spring.sql.init.schema-locations` alongside the pre-existing per-module schema files. No new port, environment variable, startup order or Docker-init asset was introduced, and the same `mvn -Pquality "-Dhop.local-db-tests=true" clean verify` command validates it. Because `schema.sql` uses `CREATE TABLE IF NOT EXISTS`, a fresh `docker compose up` (or an equivalent freshly-created local Postgres volume) picks up the nine new tables automatically; if an already-created local database is being reused, run `docker compose down -v; docker compose up -d` once so the `inventory_quality` schema and its tables are created. Backend line coverage rose from 80.60% to 82.94% (308 tests / 0 failures / 0 errors / 0 skipped). At that historical point, work continued with `COM-MOD-010-BE-002`.

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

Quality alignment backlog: `06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.md`.

Enterprise foundation alignment backlog:
`06-delivery/commercial-product/HOP_ENTERPRISE_FOUNDATION_ALIGNMENT_BACKLOG.md`.

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
docker compose --env-file .env -f compose.local.json up -d
```

Check containers:

```powershell
docker compose --env-file .env -f compose.local.json ps
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

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health/liveness
```

```powershell
Invoke-RestMethod http://localhost:8080/actuator/health/readiness
```

```powershell
Invoke-WebRequest http://localhost:8080/actuator/prometheus
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

Patient portal enterprise quality profile:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\patient-portal
npm run quality
npm audit --audit-level=low
```

Doctor portal enterprise quality profile:

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation\doctor-portal
npm run quality
npm audit --audit-level=low
```

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
docker compose --env-file .env -f compose.local.json down
```

## Reset Local Data

This deletes local PostgreSQL and Redis data.

```powershell
cd C:\Documents\Proyectos\Laboratorio\NEXORA\git\nexora\projects\healthcare-operations-platform\07-implementation
docker compose --env-file .env -f compose.local.json down -v
```

## Troubleshooting

Backend cannot connect to PostgreSQL:

- Confirm `docker compose --env-file .env -f compose.local.json ps` shows PostgreSQL running.
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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-LOCAL-RUNBOOK-001
  type: integrated-local-solution-runbook
  name: HOP Integrated Local Solution Runbook
  version: 1.0.0
  status: active
  human_readable: local-solution-runbook.md
  machine_readable: local-solution-runbook.md
  standard: ../../../../nexora-framework/02-standards/standards/integrated-local-solution-runbook-standard.md
  local_toolchain_inventory: ../../03-architecture/technology-architecture/local-toolchain-inventory.md
  context_orchestrator: ../../../../nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
  context_optimized_prompt_playbook: ../../../../nexora-framework/05-prompts/prompts/context-optimized-backlog-prompts.md
  owner: Nexora Engineering
project:
  name: Healthcare Operations Platform
  slug: healthcare-operations-platform
  current_active_backlog_item: COM-MOD-015-BE-001
  paused_functional_backlog_item: null
  quality_alignment_backlog_status: closed
  quality_alignment_backlog: 06-delivery/commercial-product/HOP_QUALITY_ALIGNMENT_BACKLOG.md
  enterprise_foundation_alignment_backlog_status: closed
  enterprise_foundation_alignment_backlog: 06-delivery/commercial-product/HOP_ENTERPRISE_FOUNDATION_ALIGNMENT_BACKLOG.md
  enterprise_foundation_alignment_closeout_evidence: 08-qa/qa/enterprise-foundation/HOP-ENT-FOUND-001-validation.md
  latest_runbook_update:
    backlog_item: COM-MOD-015-DEF
    status: closed
    next_active_backlog_item: COM-MOD-015-BE-001
    runtime_change: None. Definition-only model work; no application
      source, runtime configuration, dependency, database schema, Docker service
      or UI surface changed.
    validation_summary: 'Capability package models for AI Overlay: modeled BCM-AI-001
      through BCM-AI-008 with 112 structured artifacts. Registered COM-MOD-015 as
      definition_completed in capability-package-index.md. All models keep AI outputs
      advisory, attributable, source-cited, auditable and human-review controlled;
      autonomous clinical validation and provider lock-in are explicitly prohibited.
      Materially reduced TD-FMT-001 through compact Markdown/frontmatter handoff and
      evidence. QA evidence 08-qa/qa/ai-overlay/COM-MOD-015-DEF-validation.md; security
      evidence 08-qa/security-quality/COM-MOD-015-DEF/security-quality-evidence.md.
      Advanced active backlog to COM-MOD-015-BE-001.'
  previous_runbook_update_com_mod_017_qa_001:
    backlog_item: COM-MOD-017-QA-001
    status: closed
    next_active_backlog_item: COM-MOD-017-CLOSEOUT
    validation_summary: 'Integrated marketplace validation: 4 traceability sweeps
      run (openapi-source.md vs. controllers, IAM permissions across 4 layers, ui-model.md
      vs. screens, i18n key parity); found and fixed 3 real doc-vs-implementation
      drifts (openapi-source.md path mismatches plus 1 undocumented endpoint; permissions.md/ui-model.md
      documented an unimplemented fine-grained permission model). Closed TD-BE-018
      as the debt-first action. Backend 484 tests/0 failures, coverage unchanged
      at 84.65%; employee portal 224 tests/65 files/0 failures, coverage unchanged
      at 90.68%; Trivy fs 0 findings.'
  previous_runbook_update_nxf_ctx_002:
    backlog_item: NXF-CTX-002
    status: closed
    next_active_backlog_item: COM-MOD-017-BE-001
    runtime_change: Framework execution stack update only. No product runtime component,
      port, environment variable, database schema, Docker service, dependency or startup-order
      change.
    validation_summary: 'Upgraded Nexora Framework execution from optional prompt
      assistance to mandatory open-source-first local orchestration: Python 3.11+,
      Ollama, qwen2.5-coder:0.5b, ripgrep and git. Agents must generate backlog prompts
      through the context orchestrator before handing work to execution agents; missing
      Ollama or missing required model is a framework bootstrap blocker. Generated
      COM-MOD-017-BE-001 prompt is cached and reproducible with SHA256 62adf7b643aff753adde833d97b62c85c5426c46902cad0d6e3ebe33e8adaf60.'
  previous_runbook_update_nxf_ctx_001:
    backlog_item: NXF-CTX-001
    status: closed
    next_active_backlog_item: COM-MOD-017-BE-001
    runtime_change: None. Framework and HOP documentation/tooling standard update.
      No product runtime component, port, environment variable, database schema, Docker
      service, dependency or startup-order change.
    validation_summary: Added context-efficient execution standard, optional Python/Ollama
      prompt orchestrator, compact handoff protocol and TD-FMT-001 for gradual YAML
      monolith migration. Preserved the active HOP backlog item COM-MOD-017-BE-001.
  previous_runbook_update_com_mod_017_def:
    backlog_item: COM-MOD-017-DEF
    status: closed
    next_active_backlog_item: COM-MOD-017-BE-001
    runtime_change: None. Marketplace capability package definition item. No new runtime
      component, port, environment variable, database schema, Docker service, dependency
      or startup-order change.
    validation_summary: Formally closed COM-MOD-017-DEF. Created BCM-PLT-011 Product
      Marketplace and Entitlements capability package with standard capability artifacts
      plus marketplace package, manifest, offer, license, entitlement, compatibility,
      installation, upgrade, security review, support and telemetry models. Extended
      BCM-PLT-001/002/005/006/007/009 traceability for marketplace enablement. Preserved
      all 6 coverage floors and advanced active backlog to COM-MOD-017-BE-001.
  previous_runbook_update_com_mod_016_closeout:
    backlog_item: COM-MOD-016-CLOSEOUT
    status: closed
    historical_following_backlog_item: COM-MOD-017-DEF
    runtime_change: None. Module closeout registry and documentation item. No new
      runtime component, port, environment variable, database schema, Docker service
      or startup-order change.
    validation_summary: Formally closed COM-MOD-016. All 7 capability packages marked
      module_closed in capability-package-index.md and traceability.md files,
      PROJECT_STATE.md populated with capability_package_progress.COM-MOD-016, TD-QA-008
      reviewed as open non-blocking project-wide toolchain inventory debt, all 6 coverage
      floors preserved, and active backlog advanced to COM-MOD-017-DEF.
  previous_runbook_update_com_mod_016_qa_001:
    backlog_item: COM-MOD-016-QA-001
    status: closed
    next_active_backlog_item: COM-MOD-016-CLOSEOUT
    runtime_change: None. Commercial readiness validation item; only YAML/Markdown
      registry, index, traceability and evidence files were created or corrected.
      No new runtime component, port, environment variable, database schema, Docker
      service or startup-order change.
    validation_summary: 'Formally closed COM-MOD-016-QA-001. Validated COM-MOD-016-DEF/DOC-001/OPS-001/COM-001
      for completeness, MD/YAML consistency and traceability; found and fixed 4 stale-pointer/registry
      defects (capability-package-index.md + 7 traceability.md commercial_enablement
      blocks, PROJECT_STATE.md completed_backlog_items, SOURCE_OF_TRUTH.md missing
      sources: keys); registered TD-QA-008 (open, non-blocking); no secrets, PII,
      vendor lock-in or forbidden execution-status markers found. Preserved all 6
      stack coverage floors, passed YAML parse, stale pointer sweep, evidence-state
      sweep, agent-agnostic scan, secrets scan and git diff --check clean. Advanced
      next active backlog item to COM-MOD-016-CLOSEOUT.'
  previous_runbook_update_com_mod_016_com_001:
    backlog_item: COM-MOD-016-COM-001
    status: closed
    next_active_backlog_item: COM-MOD-016-QA-001
    runtime_change: None. Commercial package, sales enablement, and launch readiness
      documentation item. Generated commercial package, sales enablement and launch
      readiness assets under 06-delivery/commercial-product/. No new runtime component,
      port, environment variable, database schema, Docker service or startup-order
      change.
    validation_summary: Formally closed COM-MOD-016-COM-001. Produced commercial packages,
      capability matrix, pricing model, upgrade/downgrade criteria, sales demo script,
      demo data checklist, one-pager, buyer personas, value proposition, launch readiness
      checklist and customer acceptance/commercial handoff assets. Preserved all 6
      stack coverage floors, passed YAML parse, stale pointer sweep, agent-agnostic
      scan, secrets scan and git diff --check clean. Advanced next active backlog
      item to COM-MOD-016-QA-001.
  previous_runbook_update_com_mod_016_ops_001:
    backlog_item: COM-MOD-016-OPS-001
    status: closed
    next_active_backlog_item: COM-MOD-016-COM-001
    runtime_change: None. Operational support, escalation, and release governance
      documentation item. Generated 10 governance specification pairs under 09-operations/governance/
      and updated master index. No new runtime component, port, environment variable,
      database schema, Docker service or startup-order change.
    validation_summary: Formally closed COM-MOD-016-OPS-001. Produced 10 governance
      specifications and master index under 09-operations/governance/, integrated
      with onboarding/ and runbooks/, preserved all 6 stack coverage floors, passed
      YAML parse, stale pointer sweep, agent-agnostic scan, secrets scan and git diff
      --check clean. Advanced next active backlog item to COM-MOD-016-COM-001.
  previous_runbook_update_com_mod_016_doc_001:
    backlog_item: COM-MOD-016-DOC-001
    status: closed
    next_active_backlog_item: COM-MOD-016-OPS-001
    runtime_change: None. Customer onboarding and configuration guides documentation
      backlog item. No new runtime component, port, environment variable, database
      schema, Docker service or startup-order change.
    validation_summary: Formally closed COM-MOD-016-DOC-001. Produced 8 onboarding
      guides and specifications under 09-operations/onboarding/, preserved all 6 stack
      coverage floors, passed YAML parse, stale pointer sweep, agent-agnostic scan,
      secrets scan and git diff --check clean. Advanced next active backlog item to
      COM-MOD-016-OPS-001.
  previous_runbook_update_com_mod_016_def:
    backlog_item: COM-MOD-016-DEF
    status: closed
    next_active_backlog_item: COM-MOD-016-DOC-001
    runtime_change: None. Capability package definition models for Commercial Launch
      and Customer Enablement. No new runtime component, port, environment variable,
      database schema, Docker service or startup-order change.
    validation_summary: Formally closed COM-MOD-016-DEF. All 7 capability packages
      (BCM-ORG-001, BCM-ORG-002, BCM-ORG-003, BCM-PLT-002, BCM-PLT-006, BCM-PLT-007,
      BCM-PLT-008) modeled and traced in capability-package-index.md and package
      registries. Advanced next active backlog item to COM-MOD-016-DOC-001.
  previous_runbook_update_com_mod_013_qa_001:
    backlog_item: COM-MOD-013-QA-001
    status: closed
    next_active_backlog_item: COM-MOD-013-CLOSEOUT
    runtime_change: 'Real fix, not additive: `application-local.properties`''s `spring.sql.init.schema-locations`
      was missing `classpath:db/external-quality-and-compliance/schema.sql` (added).
      This was compounded by an inverted `@Profile` on the 4 externalqualitycompliance
      JDBC/in-memory repository pairs (corrected to `@Profile("local")` / `@Profile("!local")`,
      matching every other module''s convention). Together these meant the local backend
      previously started fine but silently persisted External Quality/CAPA/Audit/Quality-Event-Intake
      data in memory only -- lost on every restart -- instead of to hop-local-postgres.
      No new component, port, environment variable, Docker service or startup-order
      change; existing schema-locations mechanism now correctly covers this module''s
      tables. Additionally, `employee-portal/vite.config.ts`''s dev-server `/api`
      proxy target, previously hardcoded to `http://localhost:8080` with no override,
      now reads an optional `HOP_BACKEND_URL` environment variable (falls back to
      the existing `http://localhost:8080` default when unset) -- a small, backward-compatible
      fix so the portal can be pointed at an alternate backend port (e.g. the documented
      `8090` workaround below) without editing source.'
    dast_execution: 'This backlog item validates a runnable backend and employee-portal,
      so DAST was executed for real (not `not_applicable`): `zap-api-scan.py` against
      the full backend OpenAPI surface (939 URLs, all COM-MOD-013 endpoints included)
      found and this item fixed a real defect (`TD-QA-007` -- an abrupt client disconnect
      mid multipart upload to POST /api/documents caused an unhandled 500; `GlobalExceptionHandler`
      gained a `MultipartException` -> 400 mapping); re-scan confirmed 0 FAIL-NEW/0
      WARN-NEW. `zap-baseline.py` with the Ajax Spider against the employee portal
      (125 URLs) found 0 FAIL-NEW and 6 WARN-NEW, all matching the already-known `TD-FE-005`
      (CSP/COEP) or dev-server-only artifacts with no production relevance. Reports
      saved under `08-qa/security-quality/COM-MOD-013-QA-001/`.'
    validation_summary: Completed COM-MOD-013-QA-001 clean. Found and closed TD-DB-005
      (the persistence defect above) and TD-QA-007 (the DAST-found defect above);
      backend coverage rose from a clean-rebuild 82.57% to 84.25% (382 tests, 0 failures/errors/skipped).
      Fixed 2 SpotBugs High and 5 Medium findings; employee-portal coverage 89.74%
      -> 89.75% (187 tests, 60 files, 0 failures, lint warnings 51 -> 50). OWASP Dependency-Check
      (72 deps), npm audit and Trivy (backend/employee-portal/repo-wide, all severities)
      reported 0 vulnerabilities/secrets/misconfigurations. Registered new debt TD-IAM-004.
      Advanced next active backlog item to COM-MOD-013-CLOSEOUT.
  previous_runbook_update_com_mod_013_fe_001:
    backlog_item: COM-MOD-013-FE-001
    status: closed
    next_active_backlog_item: COM-MOD-013-QA-001
    runtime_change: None. COM-MOD-013-FE-001 added employee-portal screens, typed
      API facade, IAM permissions and i18n catalogs inside the existing employee-portal
      runtime; no new component, port, environment variable, database schema, Docker
      service or startup-order change.
    validation_summary: Completed COM-MOD-013-FE-001 clean. npm run typecheck, npm
      run test:coverage (187 tests, 60 files, employee-portal line coverage 88.68%
      -> 89.74%), npm run build, npm run duplication, npm run format:check, npm run
      license:check, npm run audit:all and Trivy fs passed. Advanced next active backlog
      item to COM-MOD-013-QA-001.
  previous_runbook_update_com_mod_012_qa_001:
    backlog_item: COM-MOD-012-QA-001
    status: closed
    next_active_backlog_item: COM-MOD-012-CLOSEOUT
    runtime_change: None. GlobalExceptionHandler.java gained a DataIntegrityViolationException
      handler, IdentityAccessExceptionHandler.java's advice scope was widened to also
      cover AuthController, and application-local.properties gained management.endpoint.health.group.readiness.include
      -- none of these are new components, ports, environment variables or startup-order
      changes; the same mvn -Pquality "-Dhop.local-db-tests=true" clean verify command
      and BE-001 start_command validate them.
    environment_note: Port 8080 on this shared local machine was occupied by an unrelated,
      pre-existing Tomcat process for a different, unaffiliated project outside this
      repository. The backend was started with server.port=8090 (via -Dspring-boot.run.jvmArguments)
      for this validation session only; the canonical documented port in this runbook
      remains 8080 and no compose.local.json, application.properties or .env value was changed.
    validation_summary: 'Validated all 8 COM-MOD-012 capabilities live against a running
      backend on port 8090. GET/POST tenant/config/feature-flag operations exercised
      with valid and invalid inputs; 20 concurrent tenant provisions succeeded with
      0 races. Found and fixed a real resilience defect: readiness stayed UP with
      PostgreSQL stopped (management.endpoint.health.group.readiness.include was unset);
      fixed by scoping the include to application-local.properties (the profile with a real
      DataSource bean -- the same include in the base application.properties breaks context
      startup for profiles with none), re-verified live via a real docker stop/start
      of hop-local-postgres (readiness DOWN/503 then UP; liveness stays UP throughout).
      Executed a dedicated OWASP ZAP API scan against the full backend surface (353
      URLs) that found and this item fixed 2 real defects: TD-QA-005 (null byte/oversized
      value reaching JDBC caused an unhandled 500 across laboratoryworkflow and cashsales)
      and TD-QA-006 (AuthController returned 500 instead of 404 for a nonexistent
      assistedUserId); final rescan 0 FAIL-NEW/0 WARN-NEW/118 PASS. ZAP baseline against
      the unchanged employee portal: 0 FAIL-NEW. Executed a real backup (pg_dump,
      SHA-256 checksum, pg_restore --list: 415 TOC entries) and restore rehearsal
      (isolated database, matching row counts 40=40). Confirmed the 3 remaining COM-MOD-012-BE-001
      infrastructure forward pointers (distributed trace export, provisioned Grafana/Prometheus/Loki,
      SLO/SLA alerting) still require infrastructure not available locally; registered
      TD-OBS-001. 367 tests (0 failures/errors/skipped, up from 362), backend coverage
      raised 84.11% -> 84.14%. Next backlog item COM-MOD-012-CLOSEOUT.'
  previous_runbook_update_com_mod_012_be_001:
    backlog_item: COM-MOD-012-BE-001
    status: closed
    next_active_backlog_item: COM-MOD-012-QA-001
    runtime_change: New backend Maven dependency io.micrometer:micrometer-registry-prometheus
      (runtime scope, no separate container/process). New schema file backend/src/main/resources/db/platform-hardening-and-saas-operations/schema.sql
      added to application-local.properties spring.sql.init.schema-locations. No new Docker/compose
      service.
    port_change: none.
    environment_variable_change: none.
    startup_order_change: none.
    validation_summary: 'Compiled BCM-ORG-001 tenant operations (provisionTenant extended
      in place with code/legalName/tradeName/taxId/tier/isolationStrategy; listTenants
      and updateTenantStatus added, both privileged and audited), a new BCM-PLT-002
      platformconfiguration Spring Modulith module (getPlatformConfig, evaluateFeatureFlags,
      updateFeatureFlag), and BCM-PLT-006 observability extensions: GET /actuator/prometheus
      now reachable (verify with Invoke-RestMethod http://localhost:8080/actuator/prometheus),
      GET /actuator/health/liveness and /actuator/health/readiness now reachable,
      and every backend log line now carries tenantId/userId/traceId MDC context via
      the new RequestObservabilityContextFilter. Closed 5 of 8 named COM-MOD-012-OPS-002
      runbook known_gaps_and_forward_pointers entries (Prometheus endpoint, MDC context,
      health groups, metrics catalog, plus a real tenant-impact-triage containment
      control added as TRIAGE-STEP-004B); the remaining 3 (distributed trace export,
      a provisioned Grafana/Prometheus/Loki stack, SLO/SLA alerting) require infrastructure
      not yet provisioned and were re-pointed to future items. A backward-compatible
      ProvisionTenantRequest.name fallback plus auto-derived tenant code kept ~20
      pre-existing module test fixtures working unchanged. A real SpotBugs/FindSecBugs
      SERVLET_HEADER finding on the new MDC filter was fixed in code (control-character
      stripping, strict W3C traceparent validation), not suppressed. 362 tests (0
      failures/errors/skipped, up from 360), backend coverage raised 83.99% -> 84.11%.
      TD-IAM-002 and TD-DB-004 materially reduced further; TD-I18N-002 further reduced;
      TD-BE-016/ TD-BE-017/TD-IAM-003 registered for honestly-scoped deferred BCM-PLT-001/005/007/009
      extensions. Passed mvn -Pquality clean verify (checkstyle, PMD/CPD, SpotBugs/FindSecBugs,
      duplicate-finder, CycloneDX SBOM), OWASP Dependency-Check (115 deps, 0 vulnerable),
      Trivy fs scan (0 vulns/secrets/misconfigs), YAML parse (1,248 files), agent-agnostic
      scan (0 real hits), stale-pointer sweep and git diff --check. Next backlog item
      COM-MOD-012-QA-001.'
  previous_runbook_update:
    backlog_item: COM-MOD-012-OPS-002
    status: closed
    validation_summary: 'Added 10 executable runbook pairs (observability, health/readiness/liveness,
      metrics/logs/traces validation, backup, restore, incident response, rollback
      incident handoff, tenant-impact triage, evidence collection, post-incident review)
      plus an index README under 09-operations/runbooks/, built on production-deployment-strategy.md.
      Every local-executable command (docker compose health checks, backend actuator
      health/info, OTel Collector health, GET /api/audit/events, pg_dump/pg_restore/psql
      against hop-local-postgres) was cross-checked against compose.local.json, .env.example,
      application.properties and AuditComplianceController rather than assumed; unimplemented
      telemetry (Prometheus metrics endpoint, trace export, tenant_id/user_id/trace_id
      MDC logging) and unprovisioned dev/qa/staging/prod infrastructure were documented
      per-runbook as known_gaps_and_forward_pointers rather than silently marked passed.
      TD-DB-004 materially reduced via tenant-impact-triage-runbook.md''s mandatory
      cross-tenant leakage check, an operational compensating control pending native
      PostgreSQL Row Level Security. Capability traceability updated for all 8 COM-MOD-012
      capabilities. This was definition-only operations work with no runtime, port,
      environment variable or startup order change. Verified clean YAML syntax, agent-agnostic
      compliance, secret scan, stale-pointer sweep and git diff --check. Formally
      closed COM-MOD-011 Public Website and Digital Growth module. Confirmed all 7
      capability packages module_closed in capability-package-index.md, TD-BE-015
      and TD-UX-002 closed, 0 open debt attributable to COM-MOD-011, and re-affirmed
      clean coverage baselines (backend 83.99%, public website 98.61%, employee portal
      88.68%, mobile 99.21%, patient portal 94.11%, doctor portal 96.28%) with 0 vulnerabilities
      across OWASP Dependency-Check, npm audit and Trivy fs scans. Executed YAML parse,
      stale-pointer sweep and git diff --check clean. which already had one), so this
      backlog item touched the backend and its full Maven quality gate was re-run:
      327 tests, 0 failures, backend line coverage 83.96% -> 83.99%. Closed TD-UX-002
      by retrofitting employee-portal''s own responsive breakpoints and automated
      jest-axe accessibility check (closing the remaining_scope COM-MOD-011-WEB-001
      left open). Employee-portal coverage rose 88.24% -> 88.68% with 154 tests (54
      test files, 0 failures). Trivy fs on the backend caught 1 MEDIUM vulnerability
      (CVE-2026-59889, jackson-databind 3.1.4) before a pom.xml version-pin fix; re-scan
      confirmed 0. npm audit and Trivy fs (frontend and backend, vuln/secret/misconfig,
      all severities) 0 findings after the fix; agent-agnostic scan 4 false positives
      (CSS `cursor:` property), 0 real hits; git diff --check clean.'
  runbook_confirmation_note: 'MVP-MOD-008-DEF (Integration and Migration Readiness
    capability package models) is a definition-only backlog item: it added no runtime
    component, port, environment variable, startup order or validation command. HOP-ENT-FOUND-001
    was validated using the existing documented commands below; no new runtime component,
    port, environment variable, startup order or validation command was introduced.
    MVP-MOD-008-BE-001 (backend compilation of BCM-PLT-004/ BCM-PLT-005/BCM-PLT-010)
    added two new schema files — backend/src/main/resources/db/integration-interoperability/schema.sql
    and backend/src/main/resources/db/data-migration-portability/schema.sql — both
    added to application-local.properties''s `spring.sql.init.schema-locations` list alongside
    the pre-existing per-module schema files; no new port, environment variable, startup
    order or validation command was introduced (the same `-Pquality "-Dhop.local-db-tests=true"
    clean verify` command validates them). No Docker-init mount script exists for
    these two new schemas — unlike platform-foundation, the local Postgres container
    has no separate init-mount copy to keep in sync, so TD-STACK-004''s drift risk
    does not apply to them. The local-database-backed backend command remains dependent
    on the pre-existing platform-foundation schema/Docker-init-mount sync described
    below (TD-STACK-004). MVP-MOD-008-BE-002 (integration retry/dead-letter, API deprecation/rate-limit
    and migration checkpoint custom rules) added 3 columns (correlation_id, next_retry_at,
    dead_letter_reason) to integration_interoperability.integration_message_records
    and widened data_migration_portability.import_executions.checkpoint from varchar(160)
    to text, both in the same two schema.sql files above (no new file, no new port,
    environment variable, startup order or validation command); a pre-existing local
    Postgres container from before this change needs those columns added manually
    (`ALTER TABLE ... ADD COLUMN IF NOT EXISTS ...`) since schema.sql''s `CREATE TABLE
    IF NOT EXISTS` does not retroactively alter an already-created table — a fresh
    `docker compose up` (or an equivalent freshly-created local Postgres volume) picks
    up the new columns automatically with no manual step. This backlog item also added
    an `X-Partner-Api-Key` request header (consumed only by `PartnerApiKeyRateLimitInterceptor`);
    requests without it are entirely unaffected, so no existing runbook validation
    step changes. MVP-MOD-008-FE-001 added employee-portal integration, API-governance
    and migration administration screens; it did not add a runtime port, environment
    variable, startup order or infrastructure dependency, and remains validated through
    and MVP-MOD-008-CLOSEOUT are closed. COM-MOD-009-DEF is closed (modeled BCM-PLT-001).
    COM-MOD-009-BE-001 (backend compilation of portals authorization structures) added
    authentication, lockout, impersonation sandbox, and granular permissions. It also
    added five columns to identity.user_accounts (username, password_hash, failed_login_attempts,
    locked_until, last_login_at), which are automatically added via ALTER TABLE statements
    to existing local Postgres instances. No new port, environment variable, or startup
    order was introduced; validated using existing Maven test suite. COM-MOD-009-PORTAL-001
    (patient portal commercial workflow) is closed: it compiled LoginForm, Dashboard
    layouts, and secure self-access interceptor backend rules, raising patient portal
    Vitest coverage to 89.58% (TD-FE-008 closed). It did not add a new port, environment
    variable, or startup order; validated using the existing patient-portal and backend
    test suites. COM-MOD-009-PORTAL-002 (doctor portal commercial workflow) is closed:
    it rebuilt the doctor-portal frontend (login, permission-filtered navigation,
    patients/results/orders/notifications screens) against the existing doctor-portal
    dev port (4002, unchanged from vite.config.ts) and added backend least-privilege
    enforcement (a doctorId query filter on the existing GET /api/clinical-operations/diagnostic-orders
    endpoint, three new HopAuthorizationInterceptor self-access blocks, and a new
    ReferringDoctorAuthorizationPort Spring Modulith named interface consumed by the
    existing resultsanddigitaldelivery module). It added no new runtime component,
    port, environment variable, startup order, database schema change or validation
    command; validated using the existing doctor-portal (`npm run quality`) and backend
    (`mvn -Pquality "-Dhop.local-db-tests=true" clean verify`) commands documented
    below. Doctor-portal Vitest coverage rose to 89.86% (TD-FE-009 closed). COM-MOD-009-APP-001
    (patient mobile workflow) is closed: it added PATIENT role permissions, permission-filtered
    profile/appointments/orders/results/notifications routes, localized es-MX/en-US
    labels, a patientMobileApi facade and patientMobileWorkflowModel to the existing
    mobile-app component. It added no new runtime component, port, environment variable,
    startup order, database schema change or infrastructure dependency; validated
    using the existing mobile-app `npm run quality` and `npm audit --audit-level=low`
    commands. Mobile coverage rose to 99.21%. COM-MOD-009-QA-001 (channel access and
    privacy evidence) is closed. COM-MOD-009-CLOSEOUT (Module closeout and registry
    update) is closed. COM-MOD-010-DEF (Inventory and Internal Quality capability
    package models) is closed with 13 capability packages modeled and no runtime component,
    port, environment variable, startup-order change, database schema change or validation-command
    change. COM-MOD-010-BE-001 (Compile product, reagent, lot and stock outputs) is
    closed. It added one new schema file `07-implementation/backend/src/main/resources/db/inventory-and-internal-quality/schema.sql`
    (nine tables: `inventory_items`, `stock_lots`, `purchase_orders`, `purchase_order_lines`,
    `stock_entries`, `stock_exits`, `consumption_records`, `inventory_adjustments`,
    `waste_records`) and appended `classpath:db/inventory-and-internal-quality/schema.sql`
    to `application-local.properties` `spring.sql.init.schema-locations` alongside the pre-existing
    per-module schema files. No new port, environment variable, startup-order or Docker-init
    asset was introduced, and the same `mvn -Pquality "-Dhop.local-db-tests=true"
    clean verify` command validates it. Because `schema.sql` uses `CREATE TABLE IF
    NOT EXISTS`, a fresh `docker compose up` (or an equivalent freshly-created local
    Postgres volume) picks up the nine new tables automatically; if an already-created
    local database is being reused, run `docker compose down -v; docker compose up
    -d` once so the `inventory_quality` schema and its tables are created. Backend
    line coverage rose from 80.60% to 82.94% (308 tests / 0 failures / 0 errors /
    0 skipped). At that historical point, the next active backlog item was COM-MOD-010-BE-002.
    COM-MOD-010-FE-001 (Compile inventory and internal quality UI outputs) is closed.
    It added 11 permission-filtered employee-portal screens and a typed inventoryQualityApi
    facade consuming the existing COM-MOD-010-BE-001/BE-002 REST APIs; it introduced
    no new runtime component, port, environment variable, startup order or database
    schema change, and remains validated through the existing employee-portal `npm
    run quality` command documented below. Employee-portal Vitest coverage rose to
    87.87%. COM-MOD-010-QA-001 (traceability, stock and quality evidence) is closed.
    It added one new backend test class (InventoryQualityControlsLocalDatabaseTest.java)
    exercising the existing inventory_quality schema against a real local Postgres
    instance; no schema file, port, environment variable or startup order changed.
    Backend coverage was corrected and raised from a reproducibly-measured 81.90%
    (a jacoco.exec measurement-inflation artifact matching the earlier MVP-MOD-005-QA-001
    pattern) to 83.73%; employee-portal coverage confirmed at 88.24%. COM-MOD-010-CLOSEOUT
    (Module closeout and registry update) is closed. It is a documentation and registry
    synchronization backlog item only: no backend, employee-portal, mobile, patient-portal
    or doctor-portal source file was changed, and no runtime component, port, environment
    variable, startup order or database schema changed. It confirmed all 13 COM-MOD-010
    capability packages (BCM-INV-001..009, BCM-QLT-001/003/004/005) are module_closed
    in capability-package-index.md and their traceability.md files, and confirmed
    zero open or materially-reduced technical debt is attributable to COM-MOD-010
    after reviewing technical-debt-index.md. Backend (83.73%), employee-portal (88.24%),
    mobile (99.21%), patient-portal (94.11%) and doctor-portal (96.28%) coverage are
    unchanged. COM-MOD-011-DEF (Public Website and Digital Growth capability package
    models) is closed. It is a definition-only backlog item: it added no runtime component,
    port, environment variable, startup order or validation command. All 7 COM-MOD-011
    capabilities were confirmed reused from already-modeled/compiled capability packages
    with no new capability package, aggregate or schema created. COM-MOD-011-BE-001
    (Compile public catalog, location and request outputs) is closed. It added the
    /api/public/** REST surface (ten operations across BCM-SVC-001/002/003/005, BCM-ATT-001/006,
    BCM-PLT-005) served by a new publicweb Spring Modulith module and enforced by
    a new PublicApiRateLimitInterceptor co-located with the partner interceptor. It
    introduced no new port, no new environment variable and no new startup order;
    two additive DDL migrations use ADD COLUMN IF NOT EXISTS on the existing schema
    files so a fresh docker compose up picks them up automatically and an already-created
    local database is upgraded transparently. The same `mvn -Pquality "-Dhop.local-db-tests=true"
    clean verify` command validates the additions. Backend line coverage rose from
    83.73% to 83.96% and TD-BE-015 is closed. COM-MOD-011-WEB-001 (Compile public
    website service discovery and conversion flows) is closed. It added a new frontend
    runtime, `07-implementation/public-website/` (React 19 + TypeScript 5 strict +
    Vite 6, dev port 4004, proxying `/api` to the existing backend on port 8080),
    consuming the `/api/public/**` surface compiled by COM-MOD-011-BE-001: published
    catalog discovery (services/tests/panels/ preparations) and public appointment/quotation
    request intake, with an explicit client-side cooldown for 429 rate-limit responses.
    It added three optional, defaulted environment variables (`VITE_TENANT_ID`, `VITE_LABORATORY_ID`,
    `VITE_DEFAULT_BRANCH_ID`) but no new backend port, no database schema change and
    no startup-order change; validated through the new `npm run quality` command in
    that folder (97 tests, 98.61% line coverage, first baseline for this stack) and
    `npm run build && npm run preview` (production shell served correctly locally).
    Docker later became reachable in the same session, enabling full live end-to-end
    verification against a real backend and Postgres instance for all 10 `/api/public/**`
    operations through the real dev proxy. This surfaced and fixed a real pre-existing
    defect -- `backend/src/main/resources/db/catalog-test-configuration/schema.sql`
    seeded catalog rows with `status=''PUBLISHED''` (uppercase) while every catalog
    domain class''s `STATUS_PUBLISHED` constant is the lowercase literal `published`,
    so a case-sensitive filter silently excluded every seeded catalog row from any
    published-only view -- project-wide, not just for this module. Corrected the 10
    seed literals to lowercase (no Java source changed). Operational note -- because
    `schema.sql` seed inserts use `ON CONFLICT ... DO NOTHING`, this fix does not
    retroactively correct rows already seeded into an existing local database volume
    created before this change; run `docker compose --env-file .env -f compose.local.json
    down -v` then `up -d` once to get a fresh volume with correctly-cased seed data
    (any other local data in that volume is lost, matching the existing local-only,
    disposable nature of this compose stack). Backend regression gates re-run clean
    after the fix -- `mvn -Pquality "-Dhop.local-db-tests=true" clean verify` (324
    tests, 0 failures/errors/skipped, coverage unchanged at 83.96%), checkstyle/pmd/spotbugs/duplicate-finder
    (0 new violations), OWASP Dependency-Check (65 dependencies, 0 vulnerabilities)
    and Trivy fs on the backend directory (0 vulnerabilities/secrets/misconfigurations).
    COM-MOD-011-FE-001 added the content and public-request administration screens
    to the already-running employee-portal (no new runtime, port or environment variable)
    and one additive database column -- `care_delivery.quotations.channel varchar(40)`
    nullable, idempotent, no volume reset needed -- documented above under `latest_runbook_update`.
    It also fixed a real dependency vulnerability found by Trivy on the backend directory:
    `tools.jackson.core:jackson-databind` 3.1.4 (CVE-2026-59889, MEDIUM) was pinned
    to 3.1.5 in `backend/pom.xml`, mirroring the existing pin already there for the
    classic Jackson 2.x line. The next active backlog item is COM-MOD-011-CLOSEOUT.'
  intended_audience:
  - human_reviewer
  - qa_reviewer
  - development_agent
local_ports:
  backend_api: http://localhost:8080
  employee_portal: http://localhost:5173
  public_website: http://localhost:4004
  postgres: localhost:5432
  redis: localhost:6379
  otel_grpc: localhost:4317
  otel_http: localhost:4318
  otel_health: http://localhost:13133
prerequisites:
- id: PRE-001
  name: Docker with Compose support
  required_for: infrastructure_services
- id: PRE-002
  name: Java 21
  required_for: backend_api
- id: PRE-003
  name: Maven 3.9.x or compatible Maven wrapper/settings usage
  required_for: backend_build_and_run
- id: PRE-004
  name: Node.js and npm
  required_for: employee_portal_and_mobile_validation
- id: PRE-005
  name: PowerShell or compatible shell
  required_for: local_commands_on_windows
component_inventory:
- id: INF-001
  name: Local infrastructure services
  type: infrastructure
  path: 07-implementation/compose.local.json
  depends_on:
  - 07-implementation/.env
  port_or_url: postgres:5432, redis:6379, otel:4317/4318/13133
  start_command: docker compose --env-file .env -f compose.local.json up -d
  health_check: docker compose --env-file .env -f compose.local.json ps
  stop_command_or_process_note: docker compose --env-file .env -f compose.local.json
    down
  owner_area: platform_operations
- id: BE-001
  name: HOP backend API
  type: backend
  path: 07-implementation/backend
  depends_on:
  - INF-001
  port_or_url: http://localhost:8080
  start_command: mvn --settings .mvn/settings.xml spring-boot:run "-Dspring-boot.run.profiles=local"
  health_check: GET http://localhost:8080/actuator/health and GET http://localhost:8080/api/platform/health
  stop_command_or_process_note: Stop the Maven Spring Boot process in its terminal.
  owner_area: backend
- id: WEB-001
  name: Employee portal web app
  type: frontend_webapp
  path: 07-implementation/employee-portal
  depends_on:
  - BE-001
  port_or_url: http://localhost:5173
  start_command: npm run dev -- --host 127.0.0.1
  health_check: Open http://localhost:5173 and confirm the app loads.
  stop_command_or_process_note: Stop the Vite process in its terminal.
  owner_area: frontend
- id: APP-001
  name: Mobile app foundation package
  type: mobile_validation_package
  path: 07-implementation/mobile-app
  depends_on:
  - WEB-001 dependencies installed
  port_or_url: not_applicable_renderer_agnostic_package
  start_command: npm test
  health_check: npm run typecheck and npm test
  stop_command_or_process_note: No long-running process.
  owner_area: mobile
startup_order:
- id: STEP-001
  name: Prepare environment file
  working_directory: 07-implementation
  command: Copy-Item .env.example .env
  when: Run only if .env does not exist.
- id: STEP-002
  name: Start infrastructure services
  working_directory: 07-implementation
  command: docker compose --env-file .env -f compose.local.json up -d
  expected_result: PostgreSQL, Redis and OpenTelemetry Collector containers are running.
- id: STEP-003
  name: Verify infrastructure services
  working_directory: 07-implementation
  command: docker compose --env-file .env -f compose.local.json ps
  expected_result: postgres, redis and otel-collector are Up or healthy.
- id: STEP-004
  name: Start backend API
  working_directory: 07-implementation/backend
  command: mvn --settings .mvn/settings.xml spring-boot:run "-Dspring-boot.run.profiles=local"
  expected_result: Backend starts on http://localhost:8080.
- id: STEP-005
  name: Install employee portal dependencies
  working_directory: 07-implementation/employee-portal
  command: npm install
  when: Run when node_modules is missing or package-lock.json changed.
- id: STEP-006
  name: Start employee portal web app
  working_directory: 07-implementation/employee-portal
  command: npm run dev -- --host 127.0.0.1
  expected_result: Employee portal starts on http://localhost:5173 and proxies /api
    to backend.
health_checks:
- id: HC-001
  name: Backend actuator health
  working_directory: repository_root
  command: Invoke-RestMethod http://localhost:8080/actuator/health
  expected_result: status is UP.
- id: HC-002
  name: Platform health
  working_directory: repository_root
  command: Invoke-RestMethod http://localhost:8080/api/platform/health
  expected_result: status is UP.
- id: HC-003
  name: Employee portal availability
  working_directory: repository_root
  command: Invoke-WebRequest http://localhost:5173
  expected_result: HTTP 200.
- id: HC-004
  name: Backend liveness probe (COM-MOD-012-BE-001)
  working_directory: repository_root
  command: Invoke-RestMethod http://localhost:8080/actuator/health/liveness
  expected_result: status is UP.
- id: HC-005
  name: Backend readiness probe (COM-MOD-012-BE-001)
  working_directory: repository_root
  command: Invoke-RestMethod http://localhost:8080/actuator/health/readiness
  expected_result: status is UP.
- id: HC-006
  name: Backend Prometheus metrics scrape endpoint (COM-MOD-012-BE-001)
  working_directory: repository_root
  command: Invoke-WebRequest http://localhost:8080/actuator/prometheus
  expected_result: HTTP 200 with a text/plain Prometheus exposition-format body.
smoke_validation:
- id: SMOKE-001
  name: Open employee portal
  url: http://localhost:5173
  expected_result: HOP employee portal shell loads.
- id: SMOKE-002
  name: Create and inspect platform tenant through UI or API
  expected_result: Tenant creation succeeds and can be retrieved.
- id: SMOKE-003
  name: Diagnostic Catalog baseline
  expected_result: Diagnostic catalog screens load and can use backend /api/catalog
    endpoints when backend is running.
- id: SMOKE-004
  name: People and Clinical Master Data baseline
  expected_result: 'POST /api/people/patients and POST /api/people/doctors register
    a patient and doctor; GET /api/people/persons/search returns both records with
    duplicate-detection scoring. POST /api/care-delivery/patient-registrations starts
    a registration and returns outcome pending; POST .../{id}/commit commits the registration
    through Patient Management (blocks with HTTP 409 on an unresolved high-confidence
    duplicate or missing mandatory consent). Patient merge, representative/consent
    revocation, doctor suspension, portal-access preparation and credential verify/revoke
    custom-rule endpoints (MVP-MOD-003-BE-002) are functional and no longer return
    HTTP 501.

    '
- id: SMOKE-005
  name: People and Clinical Master Data employee portal screens (MVP-MOD-003-FE-001)
  expected_result: 'With tenant, laboratory and branch scope selected (Tenants/Laboratories/Branches
    tabs), the "People Search" tab searches patients and doctors and runs duplicate
    detection with a visible confidence badge; "Patients" registers a patient and
    can load its snapshot, attach/revoke a representative, record/revoke a consent,
    and merge a duplicate into a survivor; "Doctors" registers a doctor and can attach/verify/revoke
    a credential, suspend the doctor (with confirmation) and prepare portal access;
    "Patient Registrations" starts a registration, commits it (showing a visual high-confidence
    duplicate candidate list and a 409 message when a match must be resolved or a
    mandatory consent is missing), and cancels a pending registration (with confirmation).

    '
- id: SMOKE-006
  name: Front Desk and Care Delivery baseline (MVP-MOD-004-BE-001)
  expected_result: 'POST /api/clinical-operations/diagnostic-orders creates a diagnostic
    order with immutable patient/doctor/branch snapshots; POST .../{id}/price, .../accept
    and .../complete transition it through priced, accepted and completed (no endpoint
    returns HTTP 501). POST /api/care-delivery/appointments creates an appointment;
    .../confirm rejects a non-operational branch or an overlapping confirmed appointment
    for the same patient; .../check-in hands off to reception. POST /api/care-delivery/reception-visits
    starts a visit; .../confirm-identity performs a read-only identity check against
    Patient Management; .../advance-to-admission blocks with HTTP 409 until identity
    is confirmed. POST /api/care-delivery/admission-requests starts an admission from
    a confirmed reception visit; .../mark-ready validates published catalog selection;
    .../commit delegates to Diagnostic Order Management and returns createdOrderId.
    POST /api/care-delivery/quotations creates a quotation; .../issue resolves price-list
    pricing and enforces a discount policy cap (HTTP 409 beyond the limit); .../accept
    and .../convert produce a linked diagnostic order.

    '
- id: SMOKE-007
  name: Front Desk and Care Delivery custom rules (MVP-MOD-004-BE-002)
  expected_result: 'POST /api/clinical-operations/diagnostic-orders rejects a doctorId
    that is not isEligibleAsReferringDoctor with HTTP 409; .../price resolves each
    order line''s price independently, so a two-line order spanning two price lists
    still prices correctly; .../cancel accepts just a reasonCode for a draft/priced
    order but requires an additional overrideJustification of at least 15 characters
    once the order is accepted or in_progress. POST /api/care-delivery/appointments/{id}/confirm
    additionally rejects once the tenant''s configured daily branch capacity is reached;
    .../no-show requires the tenant''s configured grace period to have elapsed after
    scheduledEnd; GET .../preparation-instructions returns published preparation instructions
    for the appointment''s requested catalog items. GET /api/care-delivery/reception-visits
    orders the queue by priority (urgent first) then by longest wait time. POST /api/care-delivery/admission-requests/{id}/commit
    only requires the acknowledgements the tenant''s policy marks mandatory (both
    consent and sample-requirement acknowledgement by default). POST /api/care-delivery/quotations/{id}/issue
    resolves each quotation line''s price independently and applies a tenant-configurable
    discount cap instead of a fixed constant.

    '
- id: SMOKE-008
  name: Front Desk and Care Delivery employee portal screens (MVP-MOD-004-FE-001)
  expected_result: 'With tenant, laboratory and branch scope selected, the "Front
    Desk" tab starts a walk-in or scheduled reception visit, confirms identity, updates
    priority, advances to admission (blocking with a visible HTTP 409 message when
    identity is not yet confirmed) and abandons a visit (with confirmation); the queue
    lists in the backend''s priority order. The "Diagnostic Orders" tab creates a
    walk-in, appointment, admission or quotation-conversion order with one or more
    test/panel lines, showing the immutable patient/doctor/branch snapshots and pricing
    snapshot once priced; price/accept/complete/cancel transitions succeed, and cancelling
    an accepted or in-progress order without a 15+ character override justification
    surfaces the backend''s ORDER_CANCELLATION_OVERRIDE_REQUIRED message. Every action
    shows a loading, success or error banner; abandon and cancel require an explicit
    confirmation dialog. Appointment Scheduling, Admission Management and Quotation
    Management screens remain tracked as TD-FE-006.

    '
- id: SMOKE-009
  name: Cashier and Billing Request backend baseline (MVP-MOD-005-BE-001)
  expected_result: 'Create an accepted diagnostic order or accepted quotation, then
    POST /api/revenue/cashier/sales to create a payable Sale. Open a cash session
    with POST /api/revenue/cashier/sessions, register a cash or card payment with
    POST /api/revenue/cashier/sales/{saleId}/payments, verify GET /api/revenue/cashier/sales/{saleId}
    shows partially_paid or paid totals, and close the session with POST /api/revenue/cashier/sessions/{sessionId}/close.
    After a sale is paid, POST /api/revenue/billing-requests creates an InvoiceRequest
    and GET /api/revenue/billing-requests/{invoiceRequestId}/tax-lines returns tax
    lines. submit, retry and cancel billing adapter actions execute the provider-agnostic
    local deterministic fiscal adapter boundary introduced by MVP-MOD-005-BE-002.

    '
- id: SMOKE-011
  name: Inventory and Internal Quality product/reagent/lot/stock backend baseline
    (COM-MOD-010-BE-001)
  expected_result: 'With a tenant, laboratory and branch scope, POST /api/inventory/catalog/items
    creates an InventoryItem (rejects inconsistent itemType/classification with HTTP
    400 INVENTORY_ITEM_TYPE_CLASSIFICATION_MISMATCH and duplicate itemCode with HTTP
    409 INVENTORY_ITEM_CODE_NOT_UNIQUE). PUT .../{id} updates the item; POST .../{id}/discontinue
    marks it discontinued (subsequent writes rejected HTTP 409 INVENTORY_ITEM_DISCONTINUED).
    For a reagent-typed item, POST /api/inventory/reagents/items/{id}/reagent-profile
    assigns the reagent profile (rejects non-reagent items HTTP 409 REAGENT_ITEM_TYPE_NOT_ELIGIBLE
    and non-positive ratios HTTP 400 REAGENT_CONSUMPTION_RATIO_INVALID). POST /api/inventory/lots/items/{id}/lots
    registers a stock lot and bumps InventoryItem. stockSummary.onHandQuantity by
    the received quantity; POST /api/inventory/lots/lots/{id}/ quarantine and .../expire
    transition the lot''s status. POST /api/inventory/purchase-orders creates a draft
    purchase order with lines against InventoryItem scope; /submit /approve /cancel
    drive the header lifecycle; POST /{id}/lines/{lineId}/receive records receipts,
    updates the line and header, and applies a matching stock entry via BCM-INV-005
    StockEntryService. POST /api/inventory/stock-entries, .../stock-exits, .../consumption,
    .../adjustments and .../waste each mutate both InventoryItem.stockSummary and
    StockLot.remainingQuantity atomically with the appropriate guards (STOCK_EXIT_LOT_NOT_ELIGIBLE,
    ADJUSTMENT_APPROVER_SAME_AS_REQUESTER, WASTE_QUANTITY_EXCEEDS_LOT etc.). When
    a waste disposal drives lot.remainingQuantity to zero, the lot transitions to
    disposed. Every write path emits a BCM-PLT-007 audit event.

    '
- id: SMOKE-012
  name: Inventory and Internal Quality employee portal screens (COM-MOD-010-FE-001)
  expected_result: 'With tenant, laboratory and branch scope selected, the "Catálogo
    de Inventario" tab registers an inventory item, lists it and can update/discontinue
    the selected item. "Reactivos" assigns and loads a reagent profile for an item
    id. "Lotes" registers a stock lot for an item, lists lots and can quarantine/mark
    the selected lot as expired. "Compras" builds purchase-order lines, creates the
    order, lists orders and can submit/approve/cancel the selected order and receive
    an individual line. "Movimientos de Stock" records stock entries, exits and consumption
    in three sub-sections sharing the SCREEN_INVENTORY_STOCK_MOVEMENTS permission.
    "Ajustes de Inventario" and "Mermas" record adjustments/waste disposal (waste
    disposal is gated behind a confirmation dialog since it is irreversible) and list
    their history. "Control de Calidad Interno" records a QC run, lists runs and lets
    a supervisor override the selected run''s acceptance decision. "Equipos" sets
    an equipment profile and changes/loads its availability history. "Calibraciones"
    and "Mantenimiento" record and list calibration and maintenance events for an
    equipment item id. Every action shows a loading, success or error banner; empty
    result sets show an explicit empty-state message.

    '
- id: SMOKE-010
  name: Cashier and Billing Request employee portal screens (MVP-MOD-005-FE-001)
  expected_result: 'With tenant, laboratory and branch scope selected, the "Cash Sessions"
    tab opens a session (opening amount/currency), lists sessions, and closes a session
    with a counted amount; closing with a counted amount that differs from the expected
    amount and no variance reason surfaces the backend''s CASH_VARIANCE_REASON_REQUIRED
    message. The "Sales" tab creates a sale from an accepted diagnostic order or accepted
    quotation, shows sale lines/totals/outstanding balance/payment status, registers
    a payment (a payment above the outstanding balance surfaces PAYMENT_EXCEEDS_OUTSTANDING_BALANCE),
    and cancels a non-paid sale with confirmation; once a sale reaches paid it shows
    a hint to create a billing request. The "Billing Requests" tab creates a billing
    request from a paid sale''s id (a billing request for a sale that is not yet paid
    surfaces BILLING_SALE_REQUIRED), shows tax lines, and executes submit/retry/cancel
    against the fiscal adapter boundary, each gated by a confirmation dialog. Every
    action shows a loading, success or error banner.

    '
quality_validation:
- id: QA-001
  name: Backend standard tests
  working_directory: 07-implementation/backend
  command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml test
- id: QA-002
  name: Backend PostgreSQL tests
  working_directory: 07-implementation/backend
  command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml test "-Dhop.local-db-tests=true"
  depends_on:
  - INF-001
  expected_result: 'Local PostgreSQL tests run with no skipped local tests when Docker
    Compose is active, including CashSalesLocalDatabaseTest for the cash_sales schema,
    and (since MVP-MOD-008-BE-001) IntegrationInteroperabilityLocalDatabaseTest for
    the integration_interoperability schema and DataMigrationPortabilityLocalDatabaseTest
    for the data_migration_portability schema.

    '
- id: QA-003
  name: Backend enterprise quality profile
  working_directory: 07-implementation/backend
  command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Pquality
    verify checkstyle:checkstyle pmd:pmd pmd:cpd spotbugs:spotbugs cyclonedx:makeAggregateBom
    duplicate-finder:check
  expected_result: 'Tests, Checkstyle, PMD/CPD, SpotBugs/Find Security Bugs, JaCoCo,
    CycloneDX, Enforcer, license and duplicate dependency/class checks run. Any vulnerability
    or tool failure blocks backlog closure; residual PMD and coverage findings must
    be linked to technical debt.

    '
- id: QA-004
  name: Backend dependency vulnerability scan - all severities
  working_directory: 07-implementation/backend
  command: mvn -gs .mvn/global-settings.xml --settings .mvn/settings.xml -Pquality
    org.owasp:dependency-check-maven:check
  expected_result: OWASP Dependency-Check reports 0 vulnerabilities using the locally
    available advisory database.
  local_advisory_database_policy:
    tool: OWASP Dependency-Check
    data_directory: C:/Documents/Proyectos/Laboratorio/dependency-check-data
    auto_update_during_agent_execution: false
    manual_refresh_frequency: once_per_day
    responsible_party: human_operator_or_security_reviewer
    agent_instruction: Agents execute the scan against the local database available
      at execution time and document the database path plus freshness timestamp/date
      in evidence. Updating the database is a manual operational task outside framework
      and backlog-agent responsibility.
- id: QA-005
  name: Employee portal enterprise quality profile
  working_directory: 07-implementation/employee-portal
  command: npm run quality
  expected_result: Typecheck, lint, coverage, build, duplication, format and license
    gates pass.
- id: QA-006
  name: Employee portal dependency audit - all severities
  working_directory: 07-implementation/employee-portal
  command: npm audit --audit-level=low
  expected_result: 0 vulnerabilities.
- id: QA-007
  name: Mobile foundation enterprise quality profile
  working_directory: 07-implementation/mobile-app
  command: npm run quality
  expected_result: Typecheck, lint, tests, duplication and format gates pass.
- id: QA-008
  name: Integrated all-severity filesystem, secret and misconfiguration scan
  working_directory: 07-implementation
  command: trivy fs --scanners vuln,secret,misconfig --exit-code 1 --no-progress --skip-dirs
    "backend/.m2,backend/target,employee-portal/node_modules,employee-portal/dist,mobile-app/node_modules"
    .
  expected_result: 0 vulnerabilities, 0 secrets and 0 misconfigurations across all
    severities.
- id: QA-009
  name: OWASP ZAP DAST baseline for employee portal
  working_directory: 07-implementation
  command: docker run --rm --add-host=host.docker.internal:host-gateway -v "<repo>/08-qa/security-quality/HOP-QA-ALIGN-004:/zap/wrk"
    ghcr.io/zaproxy/zaproxy:stable zap-baseline.py -t http://host.docker.internal:5173
    -r zap-employee-portal.html -J zap-employee-portal.json -m 2
  windows_docker_desktop_note: --network host does not bridge to the Windows host
    under Docker Desktop's WSL2 backend; use --add-host=host.docker.internal:host-gateway
    and target host.docker.internal instead of localhost/127.0.0.1. vite.config.ts's
    server.allowedHosts must include "host.docker.internal" or Vite returns HTTP 403
    to the scanner.
  depends_on:
  - INF-001
  - BE-001
  - WEB-001
  expected_result: 'ZAP completes and findings of any severity are remediated or registered
    with accepted-risk disposition. If Docker image pull, network mode or permissions
    fail, request support and keep HOP-QA-ALIGN-004 open instead of closing with an
    execution limitation.

    '
- id: QA-010
  name: OWASP ZAP DAST API scan for backend
  working_directory: 07-implementation
  command: docker run --rm --add-host=host.docker.internal:host-gateway -v "<repo>/08-qa/security-quality/HOP-QA-ALIGN-004:/zap/wrk"
    ghcr.io/zaproxy/zaproxy:stable zap-api-scan.py -t http://host.docker.internal:8080/v3/api-docs
    -f openapi -r zap-backend-api.html -J zap-backend-api.json
  windows_docker_desktop_note: Same --add-host / host.docker.internal requirement
    as QA-009.
  depends_on:
  - INF-001
  - BE-001
  expected_result: 'ZAP completes and findings of any severity are remediated or registered
    with accepted-risk disposition. If OpenAPI is unavailable, document the exact
    API-documentation blocker before closing the DAST backlog item.

    '
stop_steps:
- id: STOP-001
  name: Stop employee portal
  instruction: Stop the Vite terminal process with Ctrl+C.
- id: STOP-002
  name: Stop backend API
  instruction: Stop the Maven Spring Boot terminal process with Ctrl+C.
- id: STOP-003
  name: Stop infrastructure services
  working_directory: 07-implementation
  command: docker compose --env-file .env -f compose.local.json down
reset_steps:
- id: RESET-001
  name: Remove local infrastructure volumes
  working_directory: 07-implementation
  command: docker compose --env-file .env -f compose.local.json down -v
  warning: This deletes local PostgreSQL and Redis data.
troubleshooting:
- symptom: Backend cannot connect to PostgreSQL.
  checks:
  - Confirm docker compose services are running.
  - Confirm 07-implementation/.env matches backend local profile variables.
  - Confirm port 5432 is not occupied by another PostgreSQL instance.
- symptom: Employee portal API calls fail.
  checks:
  - Confirm backend is running on http://localhost:8080.
  - Confirm Vite proxy configuration still points /api to http://localhost:8080.
- symptom: Mobile tests cannot find TypeScript or Vitest commands.
  checks:
  - Run npm install in 07-implementation/employee-portal first because mobile currently
    reuses that toolchain.
known_limitations:
- Mobile app is currently a renderer-agnostic TypeScript foundation, not a native
  runnable app.
- Mobile line coverage measurement is blocked because mobile-app reuses employee-portal's
  installed vitest toolchain via a relative binary path, which does not resolve @vitest/coverage-v8
  as a bare module specifier from a sibling directory; tracked as TD-APP-002.
- DAST (QA-009 employee portal baseline, QA-010 backend API scan) executed successfully
  during HOP-QA-ALIGN-004; both closed TD-QA-001. On Docker Desktop with a WSL2 backend,
  ZAP containers must use --add-host=host.docker.internal:host-gateway and target
  http://host.docker.internal:<port> because --network host does not bridge to the
  Windows host.
- The employee portal dev server does not set Content-Security-Policy or Cross-Origin-Embedder-Policy
  (a production-strength policy would break Vite's eval-based HMR); tracked as TD-FE-005,
  must close before any production deployment of the employee portal.
- A malformed empty-key query/form parameter causes an unhandled 500 on POST /api/platform/tenants
  (Tomcat parameter-parsing edge case, no information disclosure); tracked as TD-QA-004.
- Release supply-chain gates are configured but release-policy hardening remains tracked
  as TD-BE-004.
- Message externalization and magic-string remediation baseline established (HOP-QA-ALIGN-005,
  TD-I18N-001 closed); remaining full-adoption work (backend API code field, full
  frontend/mobile i18n-library adoption) tracked as TD-I18N-002.
- The employee portal has no Appointment Scheduling, Admission Management or Quotation
  Management screens yet (MVP-MOD-004-FE-001 delivered Front Desk/Reception and Diagnostic
  Orders only); administrators with API access are not blocked; tracked as TD-FE-006.
- The employee portal's LaboratoryResult type (used by ResultReleaseScreen, TechnicalValidationScreen,
  MedicalValidationScreen and the MVP-MOD-007-FE-001 Result Search screen) does not
  match the real BCM-LAB-006 backend record field-for-field (singular analyteSnapshot/referenceRangeSnapshot/resultValue
  vs. the FE's plural fields, no version field). MVP-MOD-007-FE-001 worked around
  this locally for its own screen via response normalization in resultsDeliveryApi.ts;
  tracked as TD-FE-007.
- HOP-HARD-DATA-001 added native PostgreSQL row-level security (TD-DB-004 closure)
  in backend/src/main/resources/db/final-hardening/schema.sql. It enables RLS on
  every table with a tenant_id column and creates a plain, unprivileged hop_app
  role. This file is deliberately NOT registered in application-local.properties
  spring.sql.init.schema-locations because Spring's script splitter cannot parse
  PostgreSQL dollar-quoted DO blocks; it is applied instead by FinalHardeningSchemaInitializer
  (an ApplicationRunner with the local profile) directly through JDBC after schema init
  completes. It is idempotent (re-run on every startup; guarded with `IF NOT EXISTS` checks), so
  no docker compose down -v or manual step is required for an existing local Postgres volume.
  Operational note - a manual docker exec psql session connects as the bootstrap
  superuser, which always bypasses RLS regardless of policy — to observe tenant-scoped
  visibility from psql directly, run SET ROLE hop_app first in that session.
- In this sandboxed development environment, Maven runs in --offline mode and the
  backend `-Pquality` profile's spotless/checkstyle/pmd/spotbugs/dependency-check
  plugins are not cached locally, so QA-003 cannot execute here (PluginResolutionException,
  not a code finding). QA-001/ QA-002 (mvn test, including JaCoCo) run successfully
  offline and remain the authoritative backend gates in this environment; QA-003 should
  be run in an environment with network access before a release-readiness or GA gate.
quality_gate_command_matrix:
  backend:
  - QA-001
  - QA-002
  - QA-003
  - QA-004
  frontend:
  - QA-005
  - QA-006
  mobile:
  - QA-007
  integrated_security:
  - QA-008
  - QA-009
  - QA-010
  closure_rule: 'A backlog item cannot close with missing permissions, missing Docker,
    missing Maven/Node, blocked dependency endpoints or unsupported runtimes. The
    executor must request support, document exact remediation commands and keep the
    current backlog pointer unchanged.

    '
component_readmes:
- 07-implementation/README.md
- 07-implementation/backend/README.md
- 07-implementation/employee-portal/README.md
- 07-implementation/mobile-app/README.md
feedback_capture:
  project_feedback_index: 08-qa/framework-feedback/framework-feedback-index.md
  technical_debt_index: 08-qa/technical-debt/technical-debt-index.md
  instruction: 'If this runbook is incomplete, ambiguous, or still requires hidden
    manual component-by-component knowledge for basic startup, capture project feedback
    and propose framework feedback when reusable.

    '
```
