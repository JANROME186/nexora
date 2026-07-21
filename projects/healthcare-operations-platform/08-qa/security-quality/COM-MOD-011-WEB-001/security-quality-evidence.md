# COM-MOD-011-WEB-001 Security and Quality Evidence

**Status**: passed · **Captured on**: 2026-07-21

## Scope

Public website frontend (`07-implementation/public-website/`) consuming the anonymous `/api/public/**` surface compiled by COM-MOD-011-BE-001: published catalog discovery and public appointment/quotation request intake, with SEO/accessibility/privacy/i18n foundations.

## Open-source-first check

Three new **dev-only** dependencies, all MIT-licensed: `eslint-plugin-jsx-a11y` (static a11y linting), `jest-axe` + `@types/jest-axe` (automated axe-core accessibility regression checks). No new production/runtime dependency was added beyond `react`/`react-dom`, already used by every other portal. No router or i18n library was added — routing and i18n follow the same hand-rolled-primitive pattern already used by `patient-portal`/`doctor-portal`.

## Security controls

- **Anonymous by design**: the frontend never sends an auth token/session header to `/api/public/**`, matching the backend's deliberate omission of that prefix from `EndpointPermissionRegistry`.
- **No internal identifiers exposed**: response DTOs mirror the backend's `publicweb` records exactly; `tenantId`/`laboratoryId`/`branchId` sent in request bodies come from deployment-owned site configuration, never from a visible or user-editable field.
- **Rate limit (429) handled explicitly**: since the backend sends no `Retry-After` header, the frontend runs a fixed client-side cooldown on any rate-limited response, disabling the submit button and showing a localized countdown — verified by tests mocking a 429 response.
- **Message externalization**: every backend error code maps to a localized message; unrecognized codes and network failures fall back to a generic localized message, never the server's raw text.
- **Input validation**: client-side checks (contact-required-or, at-least-one-item, consent) are a UX convenience layer only — the backend's own validation remains authoritative. No `dangerouslySetInnerHTML` is used anywhere; all text renders through React's default escaping.
- **Privacy**: a dedicated `/privacy` page explains data collection/use; both forms require explicit consent before submission; only name/phone/email plus selected test/panel identifiers are collected, no clinical data.

## Evidence commands and results

| Check | Command | Result |
| --- | --- | --- |
| Typecheck + build | `npm run build` | 0 TypeScript errors (strict mode) |
| Test + coverage | `npm run test:coverage` | 97 tests, 34 files, 0 failures; 98.61% line/stmt, 93.15% branch, 87.70% function (first measurement) |
| Lint | `npm run lint` | 0 errors, 16 non-blocking warnings |
| Duplication | `npm run duplication` | 16 clones, 3.9% (threshold 5%) |
| Format | `npm run format:check` | passed |
| License | `npm run license:check` | MIT 3, UNLICENSED 1 |
| npm audit | `npm audit --audit-level=low` | 0 vulnerabilities |
| Trivy fs scan | `trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL` | 0 vulnerabilities, 0 secrets, 0 misconfigurations |
| YAML parse | all touched/added `.yml`/`.yaml` | 0 errors |
| Agent-agnostic scan | grep for vendor/agent patterns | 1 false positive (CSS `cursor:`), 0 real hits |
| git diff --check | `git diff --check` | 0 whitespace errors |
| Local build verification | `npm run build && vite preview` | production shell served correctly; live backend integration not exercised (no reachable Docker daemon this session) |

## Closure

- **Materially reduced**: TD-UX-002 — reference implementation of documented responsive breakpoints and an automated `jest-axe` accessibility check wired into `npm run test`/`quality`, plus `eslint-plugin-jsx-a11y` in `npm run lint`. `employee-portal` itself remains untouched (out of this item's scope), so the debt is materially reduced, not closed.
- **Reduced**: TD-I18N-002 — full es-MX/en-US externalization of every visible string in the new module.
- **Created debt**: none.
- **Ready for next backlog item**: COM-MOD-011-FE-001 (Content and request administration screens).
