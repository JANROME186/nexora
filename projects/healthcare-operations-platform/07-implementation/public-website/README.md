# public-website

Anonymous, public-facing service-discovery and conversion website for the Healthcare Operations
Platform. Consumes the `/api/public/**` surface compiled in COM-MOD-011-BE-001 (published catalog
reads plus appointment/quotation request intake). No authentication, no internal identifiers, no
private data — see `08-qa/security-quality/COM-MOD-011-WEB-001/` for the full evidence trail.

## Stack

React 19 + TypeScript 5 (strict) + Vite 6, matching the conventions already used by
`patient-portal`/`doctor-portal`: a hand-rolled `LocaleContext` for es-MX/en-US i18n, a plain
`fetch`-based `httpClient`, and no state-management library. Routing is a small hand-rolled
History-API router (`src/router/`) rather than a new dependency, since the app only needs simple
path matching with params. Vitest + Testing Library + `jest-axe` cover unit, integration and
automated accessibility regressions.

## Scripts

- `npm run dev` — start the Vite dev server (proxies `/api` to `http://localhost:8080`).
- `npm run build` — typecheck (project references) + production build.
- `npm run test` / `npm run test:coverage` — Vitest unit/integration/accessibility tests.
- `npm run lint` — ESLint (react, react-hooks, jsx-a11y, security, sonarjs).
- `npm run duplication` — jscpd duplicate-code scan.
- `npm run quality` — the full local gate chain (typecheck, lint, test:coverage, build,
  duplication, format:check, license:check).

## Configuration

`src/config/siteConfig.ts` holds the deployment identity (`tenantId`, `laboratoryId`, branch list)
that the public catalog/request endpoints require. There is no public branch directory API
(COM-MOD-011-DEF did not model one), so branches are deployment-owned static configuration,
overridable via `VITE_TENANT_ID` / `VITE_LABORATORY_ID` / `VITE_DEFAULT_BRANCH_ID`. Defaults match
the local-solution seed fixtures (`tenant-local` / `lab-local` / `branch-local`).

## Accessibility and responsive design

`eslint-plugin-jsx-a11y` runs as part of `npm run lint`, and `src/test/accessibility.test.tsx` runs
`jest-axe` against key pages as part of `npm run test`/`npm run quality`. `src/styles.css` documents
a three-tier mobile-first breakpoint set (`40rem`/`60rem`/`75rem`) applied across every screen. This
is the reference implementation for TD-UX-002's acceptance criteria on a frontend surface.
