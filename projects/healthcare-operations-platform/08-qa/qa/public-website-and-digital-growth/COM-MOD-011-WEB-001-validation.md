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

- **TD-UX-002** (materially reduced): this backlog item was the "next frontend quality-profile hardening backlog item" the debt's remediation strategy named. It implements both of the debt's acceptance criteria — a documented responsive breakpoint set and an automated accessibility check wired into `npm run quality` — as the reference pattern in the new module. `employee-portal` (the debt's originally discovered `affected_area`) was outside this item's `working_directory` and was not modified, so the debt moves from `open` to `materially_reduced`, not `closed`; see the `progress_log` entry in `08-qa/technical-debt/TD-UX-002-no-responsive-accessibility-automation.yaml`.
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

- Backend Java/Maven: 83.96% (unchanged; no backend source file touched)
- Employee portal: 88.24% (unchanged)
- Mobile TypeScript foundation: 99.21% (unchanged)
- Patient portal: 94.11% (unchanged)
- Doctor portal: 96.28% (unchanged)
- **Public website (new)**: 98.61% lines/statements — first baseline established, registered in `technical-debt-index.yaml`.

## Live backend integration note

This session's environment had no reachable Docker daemon to bring up `compose.local.yml`'s PostgreSQL container and run the Spring Boot backend live, so end-to-end verification against a *running* backend was not performed. The frontend build/serve was verified locally (`npm run build` + `vite preview`), and the API contract was verified against the backend's own documented DTOs/error codes/status codes with 97 passing tests mirroring that contract exactly. A live backend+frontend smoke run is recommended as part of human review, consistent with the local-solution-runbook's stated purpose of human validation of full local solutions.

## Closure criteria

- Public website compiles and runs locally: yes (build + preview verified).
- Consumes `/api/public/**` correctly: yes (contract-matched against backend source/tests; live run not performed, see note above).
- Tests executed with evidence: yes.
- No vulnerabilities of any level: yes.
- Coverage did not regress: yes (new baseline established; no other stack touched).
- Required technical debt addressed: yes (TD-UX-002 materially reduced, TD-I18N-002 reduced).
- No stale pointers: yes.
- Git clean (after tracking updates below): yes.
