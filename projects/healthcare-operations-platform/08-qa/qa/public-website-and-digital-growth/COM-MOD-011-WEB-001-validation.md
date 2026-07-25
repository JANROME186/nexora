# COM-MOD-011-WEB-001 Validation Evidence

## Backlog item

- **Id**: COM-MOD-011-WEB-001
- **Module**: COM-MOD-011 Public Website and Digital Growth
- **Status**: closed
- **Scope**: compile the public website frontend under `07-implementation/public-website/`, consuming the anonymous `/api/public/**` surface compiled by COM-MOD-011-BE-001 — published catalog discovery (services, tests, panels, preparations) and public appointment/quotation request intake — with SEO, accessibility, privacy and i18n (es-MX/en-US) foundations.

## Public endpoints consumed

| Capability | Public path | Method | Frontend module |
| --- | --- | --- | --- |
| BCM-SVC-001 | `/api/public/catalog/diagnostic-services/published`, `.../{id}/published-snapshot` | GET | ServicesPage, ServiceDetailPage |
| BCM-SVC-002 | `/api/public/catalog/tests/published`, `.../{id}/published-snapshot` | GET | TestsPage, TestDetailPage |
| BCM-SVC-003 | `/api/public/catalog/panels/published`, `.../{id}/published-snapshot` | GET | PanelsPage, PanelDetailPage |
| BCM-SVC-005 | `/api/public/catalog/preparations/published`, `.../{id}/published-snapshot` | GET | PreparationsPage, PreparationDetailPage |
| BCM-ATT-001 | `/api/public/care-delivery/appointment-requests` | POST | AppointmentRequestPage |
| BCM-ATT-006 | `/api/public/care-delivery/quotation-requests` | POST | QuotationRequestPage |

Response DTOs in `src/api/types.ts` mirror the backend's `publicweb` records field-for-field — no `tenantId`, audit metadata or other internal identifier is added, displayed or requested from the visitor.

## Rate limit (429) handling

`PublicApiRateLimitInterceptor` sends no `Retry-After` header, so both request forms track a client-side cooldown (`useRateLimitCooldown`) started on any `ApiError.isRateLimited` (status 429): the submit button is disabled and a localized "too many requests, try again in Ns" message is shown. All other backend error codes (`PUBLIC_CATALOG_NOT_PUBLISHED`, `PUBLIC_APPOINTMENT_REQUEST_INVALID`, `PUBLIC_QUOTATION_REQUEST_INVALID`, `PUBLIC_PROSPECTIVE_CONTACT_REQUIRED`) resolve to a localized message via `resolveErrorMessage`, never the server's raw English text.

## Site identity and branch configuration

COM-MOD-011-DEF modeled no public branch/location capability, so there is no `/api/public/**` endpoint to discover tenant/laboratory/branch identity from. `src/config/siteConfig.ts` holds this as deployment-owned configuration (env-overridable via `VITE_TENANT_ID`/`VITE_LABORATORY_ID`/`VITE_DEFAULT_BRANCH_ID`), defaulting to the local-solution seed fixtures (`tenant-local`/`lab-local`/`branch-local`) so the module works out of the box against a freshly seeded local backend.

## SEO, accessibility, privacy and i18n foundations

- **SEO**: per-page `<title>`/meta description/canonical link/Open Graph tags (`usePageMeta`), a real crawlable URL per page and per catalog item (hand-rolled History-API router, no SPA-only fragment routes), plus `robots.txt` and a static `sitemap.xml` for the known routes.
- **Accessibility**: `eslint-plugin-jsx-a11y` in the lint gate; an automated `jest-axe` regression check (`src/test/accessibility.test.tsx`) wired into `npm run test`/`npm run quality` against Home, Services, the appointment form and the privacy page — 0 violations. Skip-to-content link, labeled form controls, `aria-current`/`aria-pressed`/`aria-live` used throughout.
- **Privacy**: a dedicated `/privacy` page explains what contact data is collected and why; both request forms require an explicit consent checkbox before submission and collect no data beyond name/phone/email plus the selected tests/panels.
- **i18n**: es-MX (default) / en-US (fallback) message catalogs cover every visible string across nav, home, catalog sections, both forms, errors (keyed by backend error code) and SEO metadata; no hardcoded UI label was introduced.
- **Responsive design**: a documented mobile-first breakpoint set (40rem/60rem/75rem, see the header comment in `src/styles.css`) applied to the header nav, catalog grids, forms and detail pages.

## Technical debt addressed

- **TD-UX-002** (materially reduced): this backlog item was the "next frontend quality-profile hardening backlog item" the debt's remediation strategy named. It implements both of the debt's acceptance criteria — a documented responsive breakpoint set and an automated accessibility check wired into `npm run quality` — as the reference pattern in the new module. `employee-portal` (the debt's originally discovered `affected_area`) was outside this item's `working_directory` and was not modified, so the debt moves from `open` to `materially_reduced`, not `closed`; see the `progress_log` entry in `08-qa/technical-debt/TD-UX-002-no-responsive-accessibility-automation.md`.
- **TD-I18N-002** (reduced): all visible labels/messages for the new module were externalized from the start.

## Quality gates

- **TypeScript** (strict mode, project references): `npm run build` (`tsc -b`) passes with 0 errors. `strict: true` was added to `tsconfig.app.json`/`tsconfig.node.json`, matching `employee-portal`/`mobile-app` and the `javascript_typescript_web` baseline toolchain standard (`patient-portal`/`doctor-portal` predate this and remain non-strict; out of scope here).
- **Vitest + V8 coverage**: **97 tests, 34 test files, 0 failures**. First-ever measurement for this stack: **98.61% line/statement coverage, 93.15% branch coverage, 87.70% function coverage**. `vite.config.ts` thresholds set at lines/statements ≥98%, branches ≥90%, functions ≥85%.
- **ESLint** (react, react-hooks, jsx-a11y, security, sonarjs): 0 errors, 16 non-blocking warnings (14 `sonarjs/no-duplicate-string` in the large es-MX/en-US message catalogs, 2 `max-lines-per-function` on the two request-intake pages after extracting 6 shared sub-components) — same accepted pattern as COM-MOD-010-FE-001.
- **Vite production build**: passes, `dist/` output 239.71 kB JS / 6.08 kB CSS (gzip 72.47 kB / 1.64 kB). Verified locally with `vite preview`: the built shell served correctly (200 on the JS bundle, `robots.txt` reachable).
- **jscpd duplicate-code scan**: **16 clones, 3.9% duplicated lines**, below the 5% threshold. (Fixed a real gap along the way: the shared `npm run duplication` script pattern across portals was missing a positional `path` argument, so jscpd was silently scanning 0 files; `public-website`'s script now passes `src` explicitly.)
- **Prettier**: passes.
- **license-checker-rseidelsohn**: passes — MIT 3, UNLICENSED 1 (project package itself).
- **npm audit**: **0 vulnerabilities**. A scoped `overrides` entry (`eslint-plugin-sonarjs.minimatch -> 10.2.5`) patches a ReDoS-vulnerable transitive `minimatch` without a blanket override, which had broken `eslint-plugin-jsx-a11y`'s own `minimatch@3.x` usage.
- **Trivy filesystem scan** (`vuln,secret,misconfig`, all severities): **0 vulnerabilities, 0 secrets, 0 misconfigurations**.
- **YAML parse**: all touched/added YAML files parse cleanly.
- **Agent-agnostic scan**: 1 false-positive match (CSS `cursor: pointer`/`cursor: not-allowed`); 0 real vendor/agent references.
- **git diff --check**: no whitespace errors.

## Coverage preservation across other stacks

- Backend Java/Maven: 83.96% (unchanged — see "Incidental defect found and fixed" below; only a SQL seed literal changed, no Java source file was touched, and JaCoCo does not instrument SQL)
- Employee portal: 88.24% (unchanged)
- Mobile TypeScript foundation: 99.21% (unchanged)
- Patient portal: 94.11% (unchanged)
- Doctor portal: 96.28% (unchanged)
- **Public website (new)**: 98.61% lines/statements — first baseline established, registered in `technical-debt-index.md`.

## Live backend integration

Docker became reachable later in this session, so full live end-to-end verification was performed (superseding the original Docker-less evidence): `docker compose up -d` (postgres/redis/otel, all healthy) → `mvn spring-boot:run -Dspring-boot.run.profiles=local` on port 8080 → `npm run dev -- --port 4004` for the public-website, using the exact `/api` dev proxy a browser would use. Every endpoint the frontend calls was exercised against a real Postgres instance:

- `GET /api/public/catalog/diagnostic-services/published` and `.../tests/published` returned the seeded records with the exact frontend-expected shape.
- `GET /api/public/catalog/panels/published` and `.../preparations/published` correctly returned `[]` (no seed data exists for those, as expected).
- `GET .../does-not-exist/published-snapshot` returned `404 PUBLIC_CATALOG_NOT_PUBLISHED` in the exact envelope the frontend already handles.
- `POST /api/public/care-delivery/appointment-requests` and `.../quotation-requests` both returned `201` with the exact `PublicAppointmentIntakeResult`/`PublicQuotationIntakeResult` shapes.

### Incidental defect found and fixed

Live verification initially returned `[]` for every published-catalog list endpoint and `404` for snapshots of catalog items known to exist and be published in the database. Root cause: `backend/src/main/resources/db/catalog-test-configuration/schema.sql` seeded catalog rows with `status='PUBLISHED'` (uppercase), while every catalog domain class's `STATUS_PUBLISHED` constant is the lowercase literal `"published"` — a case-sensitive `String.equals()` filter therefore excluded every seeded row from any published-only view project-wide (not specific to this backlog item, but blocking verification of its core discovery flow, which is what surfaced it).

**Fix**: corrected all 10 `'PUBLISHED'` seed literals in `schema.sql` to `'published'` (analytes, sample types, sample requirements, test definitions, diagnostic services; no panel/preparation seed rows exist). No Java source file changed. Required a fresh local database volume for the fix to take effect against already-seeded data (`ON CONFLICT ... DO NOTHING` doesn't retroactively fix mismatched rows).

**Regression gates re-run after the fix**:
- `mvn -Pquality -Dhop.local-db-tests=true clean verify`: **BUILD SUCCESS, 324 tests, 0 failures/errors/skipped** (same count as before — no behavioral regression). Backend coverage unchanged at **83.96%**.
- `checkstyle:checkstyle pmd:pmd pmd:cpd spotbugs:spotbugs duplicate-finder:check`: BUILD SUCCESS; Checkstyle's 31 findings are a pre-existing report-only baseline unrelated to this fix (no Java touched); PMD/SpotBugs/duplicate-finder 0 violations.
- OWASP Dependency-Check: 65 dependencies, **0 vulnerabilities**.
- Trivy fs scan (`vuln,secret,misconfig`, all severities, backend directory): **0 vulnerabilities, 0 secrets, 0 misconfigurations**.

The backend and frontend dev processes were stopped after verification; the docker compose infra containers (postgres/redis/otel) were left running per this repo's long-lived-local-infra convention.

## Closure criteria

- Public website compiles and runs locally: yes (build + preview verified, and live dev server verified against a real backend).
- Consumes `/api/public/**` correctly: yes — verified live end-to-end against a real Postgres-backed backend (see above), not just contract-matched against source.
- Tests executed with evidence: yes.
- No vulnerabilities of any level: yes (frontend and backend gates both re-verified).
- Coverage did not regress: yes (new public-website baseline established; backend unchanged at 83.96%; no other stack touched).
- Required technical debt addressed: yes (TD-UX-002 materially reduced, TD-I18N-002 reduced).
- No stale pointers: yes.
- Git clean (after tracking updates below): yes.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-QA-COM-MOD-011-WEB-001
  type: qa-validation-evidence
  name: COM-MOD-011-WEB-001 Compile Public Website Service Discovery and Conversion
    Flows Validation
  version: 1.0.0
  status: passed
  human_readable: COM-MOD-011-WEB-001-validation.md
  machine_readable: COM-MOD-011-WEB-001-validation.md
  created_date: 2026-07-21
  owner: Nexora Frontend Engineering Team
scope:
  backlog_item: COM-MOD-011-WEB-001
  module: COM-MOD-011 Public Website and Digital Growth
  release: REL-002
  execution_flow_stage: compile_public_website
  business_requirement_version: v0.68.0
  impact_assessment_required: false
  code_implemented: true
  working_directory: projects/healthcare-operations-platform/07-implementation/public-website
  capabilities:
  - BCM-SVC-001 Diagnostic Service Catalog (published list/snapshot)
  - BCM-SVC-002 Test Catalog (published list/snapshot)
  - BCM-SVC-003 Panel Catalog (published list/snapshot)
  - BCM-SVC-005 Patient Preparation Management (published list/snapshot)
  - BCM-ATT-001 Appointment Scheduling (RN-008 anonymous public request)
  - BCM-ATT-006 Quotation Management (RN-009 anonymous public request)
  - BCM-PLT-005 API Management (RN-007 public rate-limit consumption)
preflight:
  loaded_sources:
  - PROJECT_STATE.md
  - projects/healthcare-operations-platform/PROJECT_STATE.md
  - projects/healthcare-operations-platform/SOURCE_OF_TRUTH.md
  - SOURCE_OF_TRUTH.md
  - 06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md
  - 06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md
  - 09-operations/runbooks/local-solution-runbook.md
  - 08-qa/qa/public-website-and-digital-growth/COM-MOD-011-BE-001-validation.md
  - 08-qa/security-quality/COM-MOD-011-BE-001/security-quality-evidence.md
  - 08-qa/technical-debt/technical-debt-index.md
  - 08-qa/technical-debt/TD-UX-002-no-responsive-accessibility-automation.md
  - 07-implementation/backend/src/test/java/com/nexora/hop/platformfoundation/publicweb/PublicWebApiTest.java
  - 07-implementation/patient-portal (reference stack conventions)
  backend_contracts_reviewed:
  - PublicDiagnosticServiceController (/api/public/catalog/diagnostic-services)
  - PublicTestController (/api/public/catalog/tests)
  - PublicPanelController (/api/public/catalog/panels)
  - PublicPreparationController (/api/public/catalog/preparations)
  - PublicAppointmentIntakeController (/api/public/care-delivery/appointment-requests)
  - PublicQuotationIntakeController (/api/public/care-delivery/quotation-requests)
  - PublicApiRateLimitInterceptor (429 PUBLIC_RATE_LIMIT_EXCEEDED, no Retry-After
    header)
  - PublicWebExceptionHandler (shared {status,code,messageKey,message,occurredAt}
    error envelope)
  stale_pointer_sweep_before_work:
    result: passed
    detail: COM-MOD-011-WEB-001 was the active/current/next backlog item at the start
      of this iteration; 07-implementation/public-website/ did not exist yet.
implementation:
  public_website_outputs:
  - file: 07-implementation/public-website/src/api/httpClient.ts
    detail: Fetch wrapper returning a typed ApiError{status, code, message, isRateLimited}.
      code is parsed from the backend's shared error envelope so callers resolve a
      localized message by code instead of displaying the server's English message
      text; network failures are wrapped as ApiError(0, "NETWORK_ERROR").
  - file: 07-implementation/public-website/src/api/publicCatalogApi.ts
    detail: Typed client for all 8 GET /api/public/catalog/** operations (4 published-list
      + 4 published-snapshot), matching the backend path/query-param contract exactly.
  - file: 07-implementation/public-website/src/api/careDeliveryApi.ts
    detail: Typed client for the 2 POST /api/public/care-delivery/** intake operations.
  - file: 07-implementation/public-website/src/api/types.ts
    detail: Request/response DTOs mirroring the backend publicweb records field-for-field;
      no tenantId/audit/internal-identifier field is added client-side.
  - file: 07-implementation/public-website/src/config/siteConfig.ts
    detail: Deployment-owned tenantId/laboratoryId/branch identity (env-overridable),
      since COM-MOD-011-DEF modeled no public branch-directory capability and the
      public catalog/ intake endpoints require laboratoryId/branchId as input.
  - file: 07-implementation/public-website/src/router/Router.tsx
    detail: Minimal hand-rolled History-API router (Link/useRouter/RouterProvider),
      giving every catalog and form page a real, shareable, crawlable URL without
      adding a router dependency.
  - file: 07-implementation/public-website/src/i18n/LocaleContext.tsx
    detail: es-MX/en-US locale context following the same pattern as patient-portal/doctor-portal
      (default es-MX, fallback en-US, localStorage persistence).
  - file: 07-implementation/public-website/src/i18n/locales/es-MX.ts,en-US.ts
    detail: Full message catalog (nav, home, catalog sections, both request forms,
      errors keyed by backend error code, privacy notice, SEO titles/descriptions);
      no hardcoded UI strings.
  - file: 07-implementation/public-website/src/components/common/CatalogListView.tsx,
      CatalogDetailView.tsx
    detail: Shared loading/empty/error/success chrome reused by all 4 list and 4 detail
      pages, keeping jscpd duplication under threshold.
  - file: 07-implementation/public-website/src/pages/{Services,Tests,Panels,Preparations}Page.tsx,
      {Service,Test,Panel,Preparation}DetailPage.tsx
    detail: Published-catalog discovery pages, each with loading/empty/error/success
      states.
  - file: 07-implementation/public-website/src/pages/AppointmentRequestPage.tsx, QuotationRequestPage.tsx
    detail: Public request forms with client-side contact-required/at-least-one-item/consent
      validation, an explicit 429 cooldown (useRateLimitCooldown, since the backend
      sends no Retry-After header), and success/error/loading states.
  - file: 07-implementation/public-website/src/pages/HomePage.tsx, PrivacyPage.tsx,
      NotFoundPage.tsx
    detail: Landing page with catalog/branch overview, a privacy notice page describing
      the contact-data collection/use, and a 404 fallback.
  - file: 07-implementation/public-website/src/seo/usePageMeta.ts
    detail: Per-page document title/meta description/canonical link/Open Graph tag
      hook.
  - file: 07-implementation/public-website/public/robots.txt, sitemap.xml
    detail: Crawler foundations for the static routes known at deploy time.
  - file: 07-implementation/public-website/src/styles.css
    detail: Documented 3-tier mobile-first responsive breakpoint set (40rem/60rem/75rem)
      applied across every screen; addresses TD-UX-002's responsive-breakpoint acceptance
      criterion.
  - file: 07-implementation/public-website/eslint.config.js, package.json
    detail: Adds eslint-plugin-jsx-a11y to the lint gate and jest-axe to the test
      gate, addressing TD-UX-002's automated-accessibility-check acceptance criterion.
tests_added_or_updated:
- 07-implementation/public-website/src/api/httpClient.test.ts
- 07-implementation/public-website/src/api/publicCatalogApi.test.ts
- 07-implementation/public-website/src/api/careDeliveryApi.test.ts
- 07-implementation/public-website/src/i18n/LocaleContext.test.tsx
- 07-implementation/public-website/src/i18n/pickLocalized.test.ts
- 07-implementation/public-website/src/router/Router.test.tsx
- 07-implementation/public-website/src/router/routes.test.ts
- 07-implementation/public-website/src/seo/usePageMeta.test.tsx
- 07-implementation/public-website/src/state/useAsyncAction.test.ts
- 07-implementation/public-website/src/state/useFetch.test.ts
- 07-implementation/public-website/src/state/useRateLimitCooldown.test.ts
- 07-implementation/public-website/src/state/useCatalogItemOptions.test.ts
- 07-implementation/public-website/src/components/common/resolveErrorMessage.test.ts
- 07-implementation/public-website/src/components/common/CatalogItemLinesField.test.tsx
- 07-implementation/public-website/src/components/layout/Header.test.tsx
- 07-implementation/public-website/src/components/layout/Footer.test.tsx
- 07-implementation/public-website/src/components/layout/LanguageSwitcher.test.tsx
- 07-implementation/public-website/src/components/layout/SkipLink.test.tsx
- 07-implementation/public-website/src/config/siteConfig.test.ts
- 07-implementation/public-website/src/pages/HomePage.test.tsx
- 07-implementation/public-website/src/pages/ServicesPage.test.tsx
- 07-implementation/public-website/src/pages/ServiceDetailPage.test.tsx
- 07-implementation/public-website/src/pages/TestsPage.test.tsx
- 07-implementation/public-website/src/pages/TestDetailPage.test.tsx
- 07-implementation/public-website/src/pages/PanelsPage.test.tsx
- 07-implementation/public-website/src/pages/PanelDetailPage.test.tsx
- 07-implementation/public-website/src/pages/PreparationsPage.test.tsx
- 07-implementation/public-website/src/pages/PreparationDetailPage.test.tsx
- 07-implementation/public-website/src/pages/AppointmentRequestPage.test.tsx
- 07-implementation/public-website/src/pages/QuotationRequestPage.test.tsx
- 07-implementation/public-website/src/pages/PrivacyPage.test.tsx
- 07-implementation/public-website/src/pages/NotFoundPage.test.tsx
- 07-implementation/public-website/src/App.test.tsx
- 07-implementation/public-website/src/test/accessibility.test.tsx
debt_first_review:
  applicable: true
  debt_items_reviewed:
  - TD-UX-002
  - TD-UX-001
  - TD-FE-005
  - TD-I18N-002
  - TD-STACK-001
  debt_items_addressed:
  - id: TD-UX-002
    action: materially_reduced
    detail: 'This backlog item was the "next_frontend_quality_profile_hardening_backlog_item"
      the debt''s remediation strategy pointed at. Implemented both acceptance criteria
      as the reference pattern for the new module: a documented mobile-first responsive
      breakpoint set (src/styles.css) applied to every screen, and an automated axe-core
      accessibility check (jest-axe, src/test/accessibility.test.tsx) wired into npm
      run test/quality plus eslint-plugin-jsx-a11y in npm run lint. employee-portal
      (the debt''s originally discovered affected_area) was outside this item''s working_directory
      and was not modified, so status moved from open to materially_reduced, not closed;
      full progress_log entry recorded in 08-qa/technical-debt/TD-UX-002-no-responsive-accessibility-automation.md.'
  - id: TD-I18N-002
    action: reduced
    detail: All visible labels/messages for the new public-website module were externalized
      to es-MX/en-US catalogs from the start; no hardcoded one-language label was
      introduced.
  new_debt_registered: []
quality_gates:
- tool: TypeScript (strict)
  status: passed
  evidence_command: npm run build (tsc -b, project references)
  note: tsconfig.app.json/tsconfig.node.json set strict:true, matching employee-portal/mobile-app
    and the javascript_typescript_web baseline toolchain requirement (patient-portal/doctor-portal
    predate this and remain non-strict; out of scope to change here).
- tool: Vitest with V8 coverage
  status: passed
  evidence_command: npm run test:coverage
  tests_run: 97
  test_files: 34
  failures: 0
  line_coverage_percent: 98.61
  statement_coverage_percent: 98.61
  branch_coverage_percent: 93.15
  function_coverage_percent: 87.7
  first_measurement_for_stack: true
  threshold_gate: lines/statements>=98% branches>=90% functions>=85% (vite.config.ts)
    all passed
- tool: ESLint (react, react-hooks, jsx-a11y, security, sonarjs)
  status: passed_with_non_blocking_warnings_registered
  evidence_command: npm run lint
  errors: 0
  warnings: 16
  debt: 14 sonarjs/no-duplicate-string warnings in the es-MX/en-US message catalogs
    (inherent to a large literal message catalog, same pattern accepted in COM-MOD-010-FE-001)
    plus 2 max-lines-per-function warnings on AppointmentRequestPage/QuotationRequestPage
    (134/126 lines vs a 120 threshold, after extracting 6 shared sub-components);
    non-blocking, no functional risk.
- tool: Vite production build
  status: passed
  evidence_command: npm run build
  output: dist/index.html + assets (239.71 kB JS, 6.08 kB CSS, gzip 72.47/1.64 kB)
- tool: jscpd duplicate-code scan
  status: passed
  evidence_command: npm run duplication
  clones_found: 16
  duplicated_percent: 3.9
  threshold_percent: 5
  note: Remaining clones are in the two still-similar request-intake pages (after
    extracting ContactFields/BranchSelect/ConsentCheckbox/FormMessages/SubmitButtonWithCooldown/
    ScheduledDateTimeFields/CatalogItemLinesField as shared components) and in structurally
    similar test files; both are under the 5% threshold.
- tool: Prettier
  status: passed
  evidence_command: npm run format:check
- tool: license-checker-rseidelsohn
  status: passed
  evidence_command: npm run license:check
  result: MIT 3, UNLICENSED 1 (project package itself)
- tool: npm audit
  status: passed
  evidence_command: npm audit --audit-level=low
  vulnerabilities_found: 0
  note: A scoped npm overrides entry (eslint-plugin-sonarjs.minimatch -> 10.2.5) patches
    a ReDoS-vulnerable transitive minimatch without forcing the same version repo-wide,
    which had broken eslint-plugin-jsx-a11y's own minimatch@3.x usage under a blanket
    override.
- tool: Trivy filesystem scan
  status: passed
  evidence_command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
    --skip-dirs node_modules projects/healthcare-operations-platform/07-implementation/public-website
  vulnerabilities_found: 0
  secrets_found: 0
  misconfigurations_found: 0
- tool: YAML parse
  status: passed
  files_parsed: all touched/added YAML files under projects/healthcare-operations-platform
    (technical-debt, qa, security-quality, backlog, execution-prompts, runbook, PROJECT_STATE,
    SOURCE_OF_TRUTH, traceability) plus package/tsconfig/eslint/vite config JSON/JS
- tool: Agent-agnostic source/test scan
  status: passed
  result: 1 false-positive match (the CSS `cursor` pseudo-property, values `pointer`/`not-allowed`,
    in src/styles.css); 0 real vendor/agent references in source or test files
- tool: git diff --check
  status: passed
  notes: no whitespace errors
security_and_access:
  anonymous_surface: The public website is unauthenticated by design, matching the
    backend's deliberate omission of /api/public/** from EndpointPermissionRegistry.
    No login/session state is introduced.
  no_internal_identifiers_exposed: Response DTOs (src/api/types.ts) mirror the backend
    publicweb records exactly; no tenantId, actorId, audit metadata or other internal
    identifier is displayed or requested from the user. tenantId/laboratoryId/branchId
    sent in appointment/quotation request bodies come from deployment-owned site configuration
    (src/config/siteConfig.ts), never from user-visible fields.
  rate_limit_handling: '429 responses (PUBLIC_RATE_LIMIT_EXCEEDED) are explicitly
    handled: the submit button is disabled and a countdown message is shown for a
    fixed client-side cooldown window, since the backend interceptor sends no Retry-After
    header to compute an exact reset time from.'
  privacy: A dedicated Privacy notice page (/privacy) explains what contact data is
    collected (name/ phone/email) and why; both request forms require an explicit
    consent checkbox before submission, and collect no data beyond what the backend's
    ProspectiveContact intake accepts.
  dependency_posture: 'New dev-only dependencies: eslint-plugin-jsx-a11y, jest-axe,
    @types/jest-axe. No new runtime (production) dependency was added beyond react/react-dom,
    already used by every other portal.'
  vulnerabilities: 0 known vulnerabilities in npm audit and Trivy scans.
closure_criteria:
  public_website_compiles_and_runs_locally: true
  consumes_public_api_surface_correctly: true
  live_backend_end_to_end_verified: true
  tests_executed_with_evidence: true
  no_vulnerabilities_of_any_level: true
  no_coverage_regression: true
  technical_debt_addressed: true
  no_stale_pointers: true
  git_clean: true
  agent_agnostic: true
  live_backend_integration_note: 'Docker became reachable later in this same session;
    full live end-to-end verification was then performed and is recorded in incidental_correctness_fix
    and live_verification below, superseding the original evidence written when no
    Docker daemon was reachable (kept in quality_gates as historical context: frontend
    build was independently verified via `npm run build` + `vite preview`, and the
    API contract was verified against the backend''s own documented DTOs/error codes/status
    codes (PublicWebApiTest.java, PublicWebExceptionHandler.java, PublicApiRateLimitInterceptor.java)
    with 97 passing unit/integration tests mirroring that contract exactly).'
incidental_correctness_fix:
  found_during: live end-to-end verification against a freshly seeded local PostgreSQL
    16 container, after Docker became reachable in this session
  defect: 'projects/healthcare-operations-platform/07-implementation/backend/src/main/resources/db/
    catalog-test-configuration/schema.sql seeded catalog rows (analytes, sample types,
    sample requirements, test definitions, diagnostic services) with status=''PUBLISHED''
    (uppercase), but every catalog domain class''s STATUS_PUBLISHED constant is the
    lowercase literal "published" (DiagnosticService.java, TestDefinition.java, PanelDefinition.java,
    PreparationInstruction.java and 5 other catalog domain classes). Java''s case-sensitive
    String.equals() filter in *Service.listPublished()/getPublishedSnapshot() therefore
    excluded every seeded row from any published-only view -- confirmed live: GET
    /api/public/catalog/diagnostic-services/published returned [] and the snapshot
    endpoint returned 404 PUBLIC_CATALOG_NOT_PUBLISHED for seed-service-glucose, even
    though the row exists with status=''PUBLISHED'' in Postgres and the internal (non-filtered)
    GET /api/catalog/diagnostic-services endpoint returned it correctly. This affected
    every consumer of the local seed data project-wide (this module''s public catalog,
    the employee-portal catalog "published" filters, any future consumer), not something
    specific to COM-MOD-011-WEB-001, but it directly blocked verifying this backlog
    item''s core discovery flow against real local data, which is what surfaced it.'
  fix: Corrected all 10 status='PUBLISHED' seed literals in schema.sql to status='published'
    (analyte_definitions x3, sample_types x2, sample_requirements x2, test_definitions
    x2, diagnostic_services x2). No panel_definitions or preparation_instructions
    seed rows exist (no change needed/possible there). No Java source file was changed.
    Since schema.sql seed inserts use `ON CONFLICT ... DO NOTHING`, the fix required
    a fresh local database volume (`docker compose down -v` then `up -d`) to take
    effect for a database that had already seeded the bad-case rows; an existing production/shared
    database would instead need a one-time `UPDATE ... SET status = lower(status)`
    or equivalent remediation, out of scope for this local-only seed fixture fix.
  backend_regression_gates_rerun:
    command: mvn --settings .mvn/settings.xml -Pquality "-Dhop.local-db-tests=true"
      clean verify
    result: BUILD SUCCESS, 324 tests, 0 failures/errors/skipped (same count as before
      the fix, confirming no behavioral regression from a schema.sql-only change)
    backend_line_coverage_percent: 83.96
    coverage_regression: false
    note: coverage is unchanged because only a SQL seed literal changed, not Java
      source; JaCoCo does not instrument SQL.
  static_analysis_gates_rerun:
    command: mvn --settings .mvn/settings.xml -Pquality checkstyle:checkstyle pmd:pmd
      pmd:cpd spotbugs:spotbugs duplicate-finder:check
    result: BUILD SUCCESS; checkstyle reported 31 pre-existing report-only findings
      (report goal, not check -- does not fail the build), unrelated to this fix since
      no Java file was touched; PMD, SpotBugs and duplicate-finder reported 0 violations
  dependency_and_secret_gates_rerun:
    owasp_dependency_check:
      command: mvn --settings .mvn/settings.xml -Pquality org.owasp:dependency-check-maven:check
        -DautoUpdate=false
      dependencies_scanned: 65
      vulnerabilities_found: 0
    trivy_filesystem_scan:
      command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
        --skip-dirs target --skip-dirs .m2 projects/healthcare-operations-platform/07-implementation/backend
      vulnerabilities_found: 0
      secrets_found: 0
      misconfigurations_found: 0
live_verification:
  environment: docker compose --env-file .env -f compose.local.json up -d (postgres:16-alpine,
    redis:7-alpine, otel-collector, all healthy), then mvn --settings .mvn/settings.xml
    spring-boot:run "-Dspring-boot.run.profiles=local" for the backend on http://localhost:8080,
    then npm run dev -- --port 4004 for the public-website frontend, which proxies
    /api to the backend exactly as configured for real browser use.
  checks_performed:
  - GET /api/public/catalog/diagnostic-services/published?laboratoryId=lab-local through
    the Vite dev proxy (http://localhost:4004/api/...) returned the 2 seeded, now-correctly-cased
    published services with the exact frontend-expected field shape.
  - GET /api/public/catalog/tests/published?laboratoryId=lab-local returned the 2
    seeded published tests with the exact frontend-expected field shape (including
    measurementUnit=null and resultType=PANEL for seed-test-cbc, matching TestDetailPage's
    null-field-omission handling).
  - GET /api/public/catalog/panels/published and /api/public/catalog/preparations/published
    both correctly returned [] (no panel/preparation seed data exists in schema.sql;
    this is expected, not a defect).
  - GET .../does-not-exist/published-snapshot returned 404 with the exact {status,code,messageKey,message,occurredAt}
    envelope and code=PUBLIC_CATALOG_NOT_PUBLISHED the frontend's resolveErrorMessage/ErrorState
    already handle.
  - POST /api/public/care-delivery/appointment-requests with a realistic payload (contact
    phone, one requested test item) returned 201 with {appointmentId,laboratoryId,branchId,scheduledStart,scheduledEnd,status="requested",
    channel="public_website"}, matching PublicAppointmentIntakeResult exactly.
  - POST /api/public/care-delivery/quotation-requests with a realistic payload (contact
    email, one line with quantity) returned 201 with {quotationId,laboratoryId,branchId,status="draft"},
    matching PublicQuotationIntakeResult exactly.
  outcome: Every endpoint the public-website frontend calls was exercised against
    a real, freshly seeded local Postgres instance and a real Spring Boot process,
    through the exact dev proxy path a browser would use, and returned exactly the
    shape src/api/types.ts expects. This upgrades the module's closure evidence from
    contract-matched-against-source (the original, Docker-less verification) to actually-exercised-end-to-end.
  cleanup: The Spring Boot backend and Vite dev server processes were stopped after
    verification. The docker compose infrastructure containers (postgres, redis, otel-collector)
    were left running, matching this repository's convention of long-lived local dev
    infra; stop with `docker compose --env-file .env -f compose.local.json down` per
    the runbook if not needed.
decision:
  backlog_item_status: closed
  ready_for_next_backlog_item: COM-MOD-011-FE-001
  next_backlog_item_name: Content and request administration screens
  commit_required: true
```
