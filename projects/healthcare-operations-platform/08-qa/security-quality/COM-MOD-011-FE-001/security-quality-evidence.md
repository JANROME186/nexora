# COM-MOD-011-FE-001 Security and Quality Evidence

**Status**: passed · **Captured on**: 2026-07-22

## Scope

Employee portal (`07-implementation/employee-portal/`) content-and-request administration screens: staff review of published public-catalog content and triage queues for public-website appointment/quotation requests, consuming existing public/internal APIs. Also touches `07-implementation/backend/` for a real, documented defect fix: `QuotationRequest` gained a `channel` field so public-website-submitted quotation drafts are reliably distinguishable from staff-initiated ones (mirroring `AppointmentSlot`, which already had this field).

## Open-source-first check

Three new **dev-only** dependencies, all MIT-licensed: `eslint-plugin-jsx-a11y` (static a11y linting), `jest-axe` + `@types/jest-axe` (automated axe-core accessibility regression checks) — closing TD-UX-002 in employee-portal. No new production/runtime dependency was added beyond `react`/`react-dom`. No new backend dependency was added; the CVE-2026-59889 fix below pins an *existing* transitive Jackson 3.x dependency to a patched version already published upstream.

## Security controls

- **IAM-gated navigation**: all 3 new screens are gated behind new permission codes (`SCREEN_PUBLIC_CONTENT_REVIEW`, `SCREEN_PUBLIC_APPOINTMENT_REQUESTS`, `SCREEN_PUBLIC_QUOTATION_REQUESTS`), granted only to `ADMIN` and `FRONT_DESK`; tabs are hidden, not disabled, for other roles.
- **No internal identifiers in content review**: `PublicContentReviewScreen` consumes the same anonymous `/api/public/catalog/**/published` endpoints the public website itself calls; the response DTOs never carry `tenantId`, audit metadata or another internal identifier.
- **No new state-changing endpoint**: every triage action (confirm/cancel appointment, issue/cancel quotation) reuses an existing, already-IAM-gated internal endpoint.
- **Backend channel validation (defense-in-depth)**: `QuotationManagementService.start()` validates the optional `channel` against a fixed whitelist and rejects `channel=public_website` from internal callers (HTTP 400); `startPublic()` always stamps `channel=public_website` server-side regardless of client input, so neither side can spoof the other's channel.
- **Message externalization**: 0 hardcoded UI strings added; all 3 new screens use namespaced es-MX/en-US message groups.
- **XSS posture**: all rendered text uses React's default JSX escaping; no `dangerouslySetInnerHTML` in any new file.

## Evidence commands and results

| Check | Command | Result |
| --- | --- | --- |
| Frontend typecheck | `npm run typecheck` | 0 errors |
| Frontend test + coverage | `npm run test:coverage` | 154 tests, 54 files, 0 failures; 88.68% line coverage (floor 88.24%) |
| Frontend lint | `npm run lint` | 0 errors, 38 non-blocking warnings (pre-existing); jsx-a11y surfaced and fixed 1 real finding |
| Frontend duplication | `npm run duplication` | passed |
| Frontend format | `npm run format:check` | passed |
| Frontend license | `npm run license:check` | MIT 5, UNLICENSED 1 |
| Frontend npm audit | `npm audit --audit-level=low` | 0 vulnerabilities |
| Frontend Trivy fs scan | `trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL` | 0 vulnerabilities, 0 secrets, 0 misconfigurations |
| Backend Maven quality profile | `mvn -Pquality -Dhop.local-db-tests=true clean verify` | BUILD SUCCESS; 327 tests, 0 failures; 83.99% line coverage (floor 83.96%) |
| Backend OWASP Dependency-Check | `mvn org.owasp:dependency-check-maven:check -DautoUpdate=false` | 108 deps scanned, 0 vulnerable (first run); see caveat below |
| Backend Trivy fs scan | `trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL --skip-dirs target --skip-dirs .m2 --skip-dirs .mvn` | 1 MEDIUM before fix -> 0 after fix; 0 secrets, 0 misconfigurations |
| YAML parse | all touched/added `.yml`/`.yaml` | 0 errors |
| Agent-agnostic scan | grep for vendor/agent patterns | 4 false positives (CSS `cursor:`), 0 real hits |
| git diff --check | `git diff --check` | 0 whitespace errors |

**OWASP Dependency-Check caveat**: the first run (before the Jackson fix) reported 0 vulnerable dependencies because its NVD-derived database had not yet ingested CVE-2026-59889, a very recent CVE. A re-verification run after the fix could not complete — the plugin hung indefinitely on `C:\Documents\Proyectos\Allianz\programas\.m2\...\odc.update.lock`, a stale lock file belonging to an unrelated project sharing this machine's local environment, outside this repository's control. Trivy, a separately-sourced vulnerability feed, independently found the vulnerability and independently confirmed the fix, and is treated as authoritative here.

## Vulnerability fixed: CVE-2026-59889

Trivy's backend filesystem scan found **1 MEDIUM vulnerability**: `tools.jackson.core:jackson-databind` 3.1.4 (the Jackson 3.x line managed by `spring-boot-starter-parent 4.1.0`) is affected by CVE-2026-59889 (`@JsonView` bypassed for `@JsonUnwrapped` container properties on deserialization), fixed upstream in 3.1.5/3.2.1.

**Fix**: pinned `tools.jackson.core:jackson-databind`/`jackson-core` to `3.1.5` in `pom.xml`'s `dependencyManagement`, mirroring the existing pattern already used in the same file to pin the classic Jackson 2.x line for a prior CVE (CVE-2026-54515). No new dependency was added — this pins an already-transitively-present dependency to its patched release.

**Regression verified**: 327 backend tests, 0 failures after the pin (up from 324 before this backlog item); JaCoCo line coverage unchanged at 83.99%. Trivy re-scan: 0 vulnerabilities.

## Closure

- **Vulnerability fixed**: CVE-2026-59889 (jackson-databind 3.1.4 -> 3.1.5), found via Trivy, confirmed fixed via Trivy re-scan.
- **Technical debt closed**: TD-UX-002 — both acceptance criteria (responsive breakpoints, automated accessibility check) now hold in `employee-portal` itself, closing the `remaining_scope` COM-MOD-011-WEB-001 left open.
- **Real defect fixed**: `QuotationRequest` gained a `channel` field, closing the gap where public-website-submitted quotation drafts could not be reliably distinguished from staff-initiated ones.
- **Created debt**: none.
- **Ready for next backlog item**: COM-MOD-011-QA-001 (Public web, SEO and privacy evidence).
