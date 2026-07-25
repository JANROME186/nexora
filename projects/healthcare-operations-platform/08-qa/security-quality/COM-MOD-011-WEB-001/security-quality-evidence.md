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
| Local build verification | `npm run build && vite preview` | production shell served correctly |
| Live backend end-to-end verification | real `docker compose up -d` + `mvn spring-boot:run` + `npm run dev` through the real `/api` proxy | all 10 `/api/public/**` operations exercised against real Postgres data, exact expected shapes returned |

## Incidental defect found and fixed

Live verification against a freshly seeded local Postgres instance initially showed every published-catalog list endpoint returning `[]` and every snapshot endpoint returning `404`, despite the seed rows existing and being marked published. Root cause: `backend/src/main/resources/db/catalog-test-configuration/schema.sql` seeded rows with `status='PUBLISHED'` (uppercase) while every catalog domain class's `STATUS_PUBLISHED` constant is the lowercase literal `"published"` — a case-sensitive filter silently excluded all seeded data from any published-only view, project-wide (not specific to this backlog item, but blocking its own verification, which is what surfaced it).

**Fix**: corrected all 10 seed literals in `schema.sql` from `'PUBLISHED'` to `'published'`. No Java source changed.

**Regression gates re-run clean**: `mvn -Pquality -Dhop.local-db-tests=true clean verify` (324 tests, 0 failures, backend coverage unchanged at 83.96%), `checkstyle/pmd/spotbugs/duplicate-finder` (0 new violations), OWASP Dependency-Check (65 deps, 0 vulnerabilities), Trivy fs scan on the backend directory (0 vulnerabilities/secrets/misconfigurations).

## Closure

- **Materially reduced**: TD-UX-002 — reference implementation of documented responsive breakpoints and an automated `jest-axe` accessibility check wired into `npm run test`/`quality`, plus `eslint-plugin-jsx-a11y` in `npm run lint`. `employee-portal` itself remains untouched (out of this item's scope), so the debt is materially reduced, not closed.
- **Reduced**: TD-I18N-002 — full es-MX/en-US externalization of every visible string in the new module.
- **Created debt**: none.
- **Ready for next backlog item**: COM-MOD-011-FE-001 (Content and request administration screens).

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-COM-MOD-011-WEB-001
  type: security-quality-evidence
  name: COM-MOD-011-WEB-001 Security and Quality Evidence
  version: 1.0.0
  status: passed
  captured_on: 2026-07-21
scope: 'Public website frontend (07-implementation/public-website/) consuming the
  anonymous /api/public/** surface compiled by COM-MOD-011-BE-001: published catalog
  discovery and public appointment/quotation request intake, with SEO/accessibility/privacy/i18n
  foundations.'
open_source_first_check:
  new_dependency_added: true
  new_dependencies:
  - name: eslint-plugin-jsx-a11y
    kind: dev
    license: MIT
    reason: static accessibility linting, addresses TD-UX-002
  - name: jest-axe
    kind: dev
    license: MIT
    reason: automated axe-core accessibility regression checks wired into npm run
      quality
  - name: '@types/jest-axe'
    kind: dev
    license: MIT
    reason: TypeScript types for jest-axe under strict mode
  stack_reviewed: React 19, TypeScript 5 (strict), Vite 6, Vitest, ESLint, jscpd,
    Prettier, npm audit, Trivy
  vulnerabilities_found: 0
  license_check: passed
  notes: No new production/runtime dependency was added beyond react/react-dom (already
    used by every other portal). The three new dependencies are dev-only (lint/test
    tooling) and MIT-licensed, consistent with the open_source_first_policy accepted-license
    list. No router library or i18n library was added; routing and i18n follow the
    same hand-rolled-primitive pattern already used by patient-portal/doctor-portal
    (LocaleContext, httpClient), keeping the dependency surface minimal.
security_controls:
  authentication:
    public_paths_prefix: /api/public/
    frontend_posture: The public website never sends an auth token/session header;
      it calls /api/public/** exactly as an anonymous visitor's browser would, matching
      the backend's deliberate omission of that prefix from EndpointPermissionRegistry.
  no_internal_identifier_exposure: src/api/types.ts DTOs mirror the backend publicweb
    records exactly (no tenantId, actorId, audit timestamps or other internal identifier).
    tenantId/laboratoryId/branchId sent in appointment/ quotation request bodies are
    sourced from deployment-owned site configuration (src/config/siteConfig.ts), never
    rendered as visible text or taken from a user-editable field.
  rate_limiting:
    backend_behavior: PublicApiRateLimitInterceptor returns 429 PUBLIC_RATE_LIMIT_EXCEEDED
      with no Retry-After header.
    frontend_handling: useRateLimitCooldown starts a fixed client-side cooldown (30s)
      on any ApiError.isRateLimited response, disabling the submit button and showing
      a localized countdown message, so a visitor cannot immediately re-trigger the
      same rate-limited request.
    test_coverage: AppointmentRequestPage.test.tsx / QuotationRequestPage.test.tsx
      assert the cooldown starts and the submit button disables on a mocked 429 response.
  message_externalization:
    hardcoded_ui_strings_added: 0
    locales_covered:
    - es-MX
    - en-US
    error_code_mapping: resolveErrorMessage maps PUBLIC_RATE_LIMIT_EXCEEDED / PUBLIC_CATALOG_NOT_PUBLISHED
      / PUBLIC_APPOINTMENT_REQUEST_INVALID / PUBLIC_QUOTATION_REQUEST_INVALID / PUBLIC_PROSPECTIVE_CONTACT_REQUIRED
      to localized messages; unrecognized codes and network failures fall back to
      a generic localized message, never the server's raw English text.
  input_validation:
    client_side: Both request forms require native HTML5 required fields plus explicit
      checks for contact-required-or (phone or email), at-least-one-selected-item,
      and an explicit consent checkbox before calling the backend. This is a UX convenience
      layer only; the backend's own @NotBlank/business-rule validation (PublicAppointmentRequestBody/PublicQuotationRequestBody,
      PUBLIC_PROSPECTIVE_CONTACT_REQUIRED) remains the authoritative validation boundary.
    xss_posture: All catalog/error text is rendered through React's default JSX text-node
      escaping; no dangerouslySetInnerHTML is used anywhere in the module.
  privacy:
    dedicated_notice_page: /privacy route explains what contact data is collected
      and why.
    consent_required: both request forms block submission until an explicit consent
      checkbox is checked.
    data_minimization: only name/phone/email plus selected test/panel identifiers
      are collected; no clinical information is requested through either public form.
evidence_commands:
  typecheck_and_build:
    command: npm run build (tsc -b && vite build)
    result: 0 TypeScript errors (strict mode); production bundle 239.71 kB JS / 6.08
      kB CSS
  test_and_coverage:
    command: npm run test:coverage
    result: 97 tests, 34 test files, 0 failures; 98.61% line/statement, 93.15% branch,
      87.70% function coverage (first measurement for this stack)
  lint:
    command: npm run lint
    result: 0 errors, 16 non-blocking warnings (sonarjs/no-duplicate-string in message
      catalogs, max-lines-per-function on the two request forms)
  duplication:
    command: npm run duplication
    result: 16 clones, 3.9% duplicated lines, below the 5% threshold
  format_check:
    command: npm run format:check
    result: passed
  license_check:
    command: npm run license:check
    result: MIT 3, UNLICENSED 1 (project package itself)
  npm_audit:
    command: npm audit --audit-level=low
    dependencies_added: 3 (dev-only)
    vulnerable: 0
    findings: 0
  trivy_filesystem_scan:
    command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
      --skip-dirs node_modules projects/healthcare-operations-platform/07-implementation/public-website
    version: 0.72.0
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
  yaml_parse:
    method: parsed every touched/added .yml/.yaml file under projects/healthcare-operations-platform
    errors: 0
  agent_agnostic_scan:
    method: case-insensitive grep for openai|claude|cursor|gemini|copilot|anthropic
      across 07-implementation/public-website source/config/test files
    total_matches: 1
    disposition: the CSS `cursor` pseudo-property (values `pointer`/`not-allowed`)
      in src/styles.css; not a real vendor/agent reference
    real_source_code_hits: 0
  secrets_scan:
    tool: trivy secret scanner (part of the fs scan above)
    findings: 0
  local_build_verification:
    command: npm run build && npm run preview -- --port 4444
    result: production shell served correctly (200 on the JS bundle, robots.txt reachable)
  live_backend_end_to_end_verification:
    environment: docker compose up -d (postgres 16, redis, otel, all healthy) + mvn
      spring-boot:run -Dspring-boot.run.profiles=local (port 8080) + npm run dev --
      --port 4004 (public-website, real /api proxy)
    result: Every /api/public/** endpoint the frontend calls (4 published-list, 4
      published-snapshot, 2 POST intake) was exercised through the real dev proxy
      against a real Postgres instance and returned exactly the shape the frontend's
      TypeScript types expect, including the documented {status,code,messageKey,message,occurredAt}
      404 error envelope for an unknown snapshot id.
    incidental_defect_found_and_fixed: 'Live verification surfaced a real pre-existing
      defect: catalog-test-configuration/ schema.sql seeded rows with status=''PUBLISHED''
      (uppercase) while every catalog domain class''s STATUS_PUBLISHED constant is
      lowercase "published", so a case-sensitive equals() filter silently excluded
      all seeded rows from every published-only view project-wide. Fixed by correcting
      the 10 seed literals to lowercase; no Java source changed. Backend regression
      gates re-run clean: mvn -Pquality -Dhop.local-db-tests=true clean verify (324
      tests, 0 failures, coverage unchanged at 83.96%), checkstyle/pmd/spotbugs/duplicate-finder
      (0 new violations), OWASP Dependency-Check (65 deps, 0 vulnerabilities) and
      Trivy fs scan on the backend directory (0 vulnerabilities/secrets/misconfigurations).'
  git_diff_check:
    command: git diff --check
    result: 0 whitespace errors
closure:
  technical_debt_materially_reduced:
  - id: TD-UX-002
    contribution: reference implementation of documented responsive breakpoints (src/styles.css)
      and an automated jest-axe accessibility check wired into npm run test/quality,
      plus eslint-plugin-jsx-a11y in npm run lint; employee-portal itself remains
      untouched (out of this item's working_directory), so the debt is materially_reduced,
      not closed
  technical_debt_reduced:
  - id: TD-I18N-002
    contribution: full es-MX/en-US externalization of every visible string in the
      new module
  incidental_defect_fixed:
  - description: catalog seed data status literal case mismatch ('PUBLISHED' vs domain
      constant "published") silently hid every seeded catalog row from published-only
      views
    file: 07-implementation/backend/src/main/resources/db/catalog-test-configuration/schema.sql
    found_via: live end-to-end verification of this backlog item's public catalog
      discovery flow against a freshly seeded local database
    regression_status: 0 new failures across backend test suite, static analysis,
      dependency and secret scans after the fix
```
