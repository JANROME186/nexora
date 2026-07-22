# COM-MOD-011-FE-001 Validation Evidence

## Backlog item

- **Id**: COM-MOD-011-FE-001
- **Module**: COM-MOD-011 Public Website and Digital Growth
- **Status**: closed
- **Scope**: compile the staff-facing content and public-request administration screens in the existing employee portal under `07-implementation/employee-portal/` — published-catalog content review (services, tests, panels, preparations) and triage queues for public-website appointment/quotation requests — reusing existing public/internal APIs, with dynamic permission-gated navigation and es-MX/en-US i18n.

## Screens and APIs consumed

| Screen | Capability | API consumed | Method |
| --- | --- | --- | --- |
| PublicContentReviewScreen | BCM-SVC-001/002/003/005 | `/api/public/catalog/{diagnostic-services,tests,panels,preparations}/published` | GET (same anonymous endpoint the public website itself calls) |
| PublicAppointmentRequestsScreen | BCM-ATT-001 | `/api/care-delivery/appointments`, `.../confirm`, `.../cancel` | GET/POST (existing internal endpoints, filtered client-side to `channel=="public_website" && status=="requested"`) |
| PublicQuotationRequestsScreen | BCM-ATT-006 | `/api/care-delivery/quotations`, `.../issue`, `.../cancel` | GET/POST (existing internal endpoints, filtered client-side to `channel=="public_website" && status=="draft"`) |

No new backend endpoint was created. The content-review screen deliberately reuses the *public* catalog endpoints (not the internal catalog-admin API already owned by `DiagnosticCatalogScreen`) so staff see exactly what the public site shows and no internal field (tenantId, audit metadata) can leak into the view by construction.

## Real defect found and fixed: QuotationRequest had no channel field

Unlike `AppointmentSlot`, `QuotationRequest` had no `channel` field, so a public-website-submitted quotation draft was indistinguishable from a staff-initiated one except by an unreliable `status=="draft" && patientId==null` heuristic — there was no reliable way to build the quotation-request queue screen. Per explicit product decision, this was treated as a real defect and fixed on the backend:

- Added `QuotationRequest.channel` (mirrors `AppointmentSlot`'s `CHANNEL_*` constants) and an additive, nullable `care_delivery.quotations.channel` column.
- `StartQuotationCommand` gained an optional `channel` field. `QuotationManagementService.start()` defaults to `channel=employee_portal` when omitted (so every existing caller/test that posts no `channel`, e.g. `CashSalesApiTest`, keeps working unmodified) and rejects `channel=public_website` from internal callers. `startPublic()` always stamps `channel=public_website` regardless of any input (defense-in-depth, same pattern `AppointmentSlot.request()` already used).
- `PublicQuotationIntakeResult` gained a `channel` field for symmetry with `PublicAppointmentIntakeResult`.
- 4 backend tests added/extended (see below); backend line coverage rose from 83.96% to **83.99%** (327 tests, 0 failures).

Because this touched the backend, the full backend Maven `-Pquality -Dhop.local-db-tests=true` gate was re-run (not just the frontend gate).

## Screens

- **PublicContentReviewScreen**: multi-area toolbar (services/tests/panels/preparations), read-only `DataTable` of published items, loading/empty/error states.
- **PublicAppointmentRequestsScreen**: queue table + detail panel + Confirm/Reject actions, each behind a `ConfirmDialog`, wrapped in `useAsyncAction` with loading/error/success `StatusBanner` feedback. On success the item leaves the queue but the detail panel keeps showing the updated record (status + success message), rather than being cleared — a real UX bug caught and fixed by the screen's own tests during this iteration.
- **PublicQuotationRequestsScreen**: same shape, Issue/Reject actions.

All three are built on the existing `DataTable.tsx`/`statusPresentation.ts` (TD-FE-010 reusable components) and the small-sub-component decomposition convention, so they introduced **0 new** `max-lines-per-function`/complexity ESLint warnings.

## Navigation, permissions and i18n

3 new `ScreenKey`/`PermissionCode` pairs (`SCREEN_PUBLIC_CONTENT_REVIEW`, `SCREEN_PUBLIC_APPOINTMENT_REQUESTS`, `SCREEN_PUBLIC_QUOTATION_REQUESTS`) were added to `state/permissions.ts`, granted to `ADMIN` (automatic, derived from `PERMISSION_CODES`) and `FRONT_DESK` (already owns reception/diagnostic-orders/diagnostic-catalog — the natural owner of public-request triage). Navigation tabs are hidden (not just disabled) for roles without the permission, matching the existing `AppShell` convention. All visible text in the 3 new screens is sourced from new namespaced es-MX/en-US message groups (`t.publicContentReview`, `t.publicAppointmentRequests`, `t.publicQuotationRequests`) — no hardcoded label.

## Technical debt addressed: TD-UX-002 (closed)

COM-MOD-011-WEB-001 implemented both of TD-UX-002's acceptance criteria as the reference pattern in the new public-website module, but explicitly left `employee-portal` (the debt's originally discovered `affected_area`) untouched. This backlog item retrofitted the same pattern into `employee-portal` itself:

1. A documented mobile-first responsive breakpoint set (`--hop-bp-sm/md/lg` at 40rem/60rem/75rem, matching the public-website reference values) added to `src/styles.css`, applied to `.app-shell` (padding/max-width) and table `th`/`td` (padding/font-size) — both shared by every administration screen, including the 3 new ones.
2. An automated accessibility check (axe-core via `jest-axe`, `src/test/accessibility.test.tsx`) covering the `AppShell`/default screen plus the 3 new screens, wired into `npm run test`/`npm run quality` — **0 violations**. `eslint-plugin-jsx-a11y` was added to `eslint.config.js`, which immediately surfaced and led to fixing one real finding: `ConfirmDialog.tsx`'s confirm button had `autoFocus`, flagged by `jsx-a11y/no-autofocus` (removed).

Both acceptance criteria now hold in `employee-portal`; **TD-UX-002 is closed**, not just materially reduced.

## Quality gates

- **TypeScript** (strict): `npm run typecheck` — 0 errors.
- **Vitest + V8 coverage**: **154 tests, 54 test files, 0 failures**. **88.68% line coverage** (up from the 88.24% floor).
- **ESLint** (react, react-hooks, jsx-a11y [new], security, sonarjs): 0 errors, 38 non-blocking warnings, all pre-existing on files this item did not touch. The 3 new screens and the accessibility test contributed 0 new warnings.
- **Vite production build**: passes, `dist/` output 428.85 kB JS / 5.30 kB CSS (gzip 97.78/1.38 kB).
- **jscpd duplicate-code scan**: passes.
- **Prettier**: passes.
- **license-checker-rseidelsohn**: MIT 5, UNLICENSED 1 (project package itself).
- **npm audit**: **0 vulnerabilities**. Narrowed a pre-existing blanket `"minimatch":"10.2.5"` override to scope it under `eslint-plugin-sonarjs` only (matching public-website's convention) — the blanket override was breaking `eslint-plugin-jsx-a11y`'s own compatible `minimatch@3.x` resolution.
- **Trivy filesystem scan** (employee-portal): **0 vulnerabilities, 0 secrets, 0 misconfigurations**.
- **Backend Maven `-Pquality -Dhop.local-db-tests=true`** (re-run because the defect fix touched the backend): **BUILD SUCCESS, 327 tests, 0 failures**, line coverage **83.99%** (up from 83.96%).
- **OWASP Dependency-Check (backend)**: post-fix revalidation passed with `mvn -Pquality org.owasp:dependency-check-maven:check -DautoUpdate=false` using the local advisory database at `C:/Documents/Proyectos/Laboratorio/dependency-check-data`; report `target/dependency-check-report.json` dated `2026-07-22T18:03:17.077591400Z` scanned 65 dependencies, 0 vulnerable dependencies and 0 vulnerabilities.
- **Trivy filesystem scan (backend)**: found **1 MEDIUM vulnerability** before the fix — `tools.jackson.core:jackson-databind` 3.1.4, `CVE-2026-59889` (`@JsonView` bypassed for `@JsonUnwrapped` container properties on deserialization), fixed upstream in 3.1.5/3.2.1. Fixed by pinning `tools.jackson.core:jackson-databind`/`jackson-core` to 3.1.5 in `pom.xml` (mirroring the existing pattern already used to pin the classic Jackson 2.x line for a prior CVE). Re-scan after the fix: **0 vulnerabilities, 0 secrets, 0 misconfigurations**.
- **YAML parse**: all touched/added YAML files parse cleanly.
- **Agent-agnostic scan**: 4 false-positive matches (CSS `cursor: pointer`/`cursor: not-allowed` in `styles.css`); 0 real vendor/agent references.
- **git diff --check**: 0 whitespace errors.

## Coverage across other stacks

- Backend Java/Maven: **83.99%** (raised from 83.96% by the channel-field defect fix and its 4 new tests).
- Employee portal: **88.68%** (raised from 88.24% by the 3 new screens and their tests).
- Public website, mobile, patient portal, doctor portal: unchanged — not touched by this backlog item.

## Closure criteria

- Administration screens implemented and tested: yes.
- Dynamic menu/permissions integrated: yes (hidden, not disabled, for unauthorized roles).
- No hardcoded visible text outside i18n: yes.
- No vulnerabilities of any level: yes (OWASP Dependency-Check and Trivy both confirm 0 findings after the fix).
- Coverage did not regress: yes (both touched stacks improved; no other stack touched).
- Required technical debt addressed: yes (TD-UX-002 closed).
- No stale pointers: yes.
- Git clean (after tracking updates below): yes.
- Agent-agnostic: yes.
