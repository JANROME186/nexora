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
| Backend OWASP Dependency-Check | `mvn -Pquality org.owasp:dependency-check-maven:check -DautoUpdate=false` | Post-fix revalidation passed; 65 deps scanned, 0 vulnerable deps, 0 vulnerabilities |
| Backend Trivy fs scan | `trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL --skip-dirs target --skip-dirs .m2 --skip-dirs .mvn` | 1 MEDIUM before fix -> 0 after fix; 0 secrets, 0 misconfigurations |
| YAML parse | all touched/added `.yml`/`.yaml` | 0 errors |
| Agent-agnostic scan | grep for vendor/agent patterns | 4 false positives (CSS `cursor:`), 0 real hits |
| git diff --check | `git diff --check` | 0 whitespace errors |

**OWASP Dependency-Check post-fix revalidation**: the project-profile scan passed after the Jackson fix with Dependency-Check 12.1.3, using the local advisory database at `C:/Documents/Proyectos/Laboratorio/dependency-check-data`. The generated JSON report is `07-implementation/backend/target/dependency-check-report.json`, dated `2026-07-22T18:03:17.077591400Z`, and reports 65 dependencies, 0 vulnerable dependencies and 0 vulnerabilities.

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

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-SQ-COM-MOD-011-FE-001
  type: security-quality-evidence
  name: COM-MOD-011-FE-001 Security and Quality Evidence
  version: 1.0.0
  status: passed
  captured_on: 2026-07-22
scope: 'Employee portal (07-implementation/employee-portal/) content-and-request administration
  screens: staff review of published public-catalog content and triage queues for
  public-website appointment/quotation requests, consuming existing public/internal
  APIs. Also touches 07-implementation/backend/ for a real, documented defect fix:
  QuotationRequest gained a channel field so public-website-submitted quotation drafts
  are reliably distinguishable from staff-initiated ones (mirroring AppointmentSlot,
  which already had this field).'
open_source_first_check:
  new_dependency_added: true
  new_dependencies:
  - name: eslint-plugin-jsx-a11y
    kind: dev
    license: MIT
    reason: static accessibility linting, closes TD-UX-002 in employee-portal
  - name: jest-axe
    kind: dev
    license: MIT
    reason: automated axe-core accessibility regression checks wired into npm run
      quality
  - name: '@types/jest-axe'
    kind: dev
    license: MIT
    reason: TypeScript types for jest-axe under strict mode
  stack_reviewed: React 18, TypeScript 5, Vite 6, Vitest, ESLint, jscpd, Prettier,
    npm audit, Trivy; backend Spring Boot 4.1 / Java 21, Maven, JaCoCo, OWASP Dependency-Check,
    Trivy
  vulnerabilities_found: 0
  license_check: passed
  notes: No new production/runtime dependency was added beyond react/react-dom. All
    3 new dependencies are dev-only (lint/test tooling), MIT-licensed. No new backend
    dependency was added; the CVE-2026-59889 fix (below) pins an existing transitive
    Jackson 3.x dependency to a patched version already published by upstream, not
    a new dependency.
security_controls:
  authentication:
    screens_gated_by_iam: All 3 new screens are gated behind new permission codes
      (SCREEN_PUBLIC_CONTENT_REVIEW, SCREEN_PUBLIC_APPOINTMENT_REQUESTS, SCREEN_PUBLIC_QUOTATION_REQUESTS)
      granted only to ADMIN and FRONT_DESK roles in ROLE_PERMISSION_CATALOG; navigation
      tabs are hidden, not just disabled, for other roles, matching the enterprise-product-foundation-standard
      iam_permission_model.
  no_internal_identifier_exposure_in_content_review: PublicContentReviewScreen consumes
    the same anonymous /api/public/catalog/**/published endpoints the public website
    itself calls (via publicContentApi.ts); the response DTOs never carry tenantId,
    audit metadata or another internal identifier, so none can be displayed by construction.
  no_new_state_changing_endpoint: Every triage action (confirm/cancel appointment,
    issue/cancel quotation) reuses an existing, already-IAM-gated internal endpoint.
    No new POST/PUT/DELETE endpoint was created.
  message_externalization:
    hardcoded_ui_strings_added: 0
    locales_covered:
    - es-MX
    - en-US
    namespaced_keys: t.publicContentReview.*, t.publicAppointmentRequests.*, t.publicQuotationRequests.*
  input_validation:
    backend_channel_validation: QuotationManagementService.start() validates the (optional)
      channel against a fixed whitelist (QUOTATION_CHANNELS) and explicitly rejects
      channel=public_website from internal callers (InvalidFrontDeskCommandException
      -> HTTP 400); startPublic() always stamps channel=public_website server-side,
      ignoring any client-supplied value, so a staff caller cannot spoof a public
      request and a public caller cannot claim an internal channel.
  xss_posture: All catalog/request text is rendered through React's default JSX text-node
    escaping; no dangerouslySetInnerHTML is used in any new file.
evidence_commands:
  frontend_typecheck:
    command: npm run typecheck
    result: 0 TypeScript errors
  frontend_test_and_coverage:
    command: npm run test:coverage
    result: 154 tests, 54 test files, 0 failures; 88.68% line coverage (previous floor
      88.24%)
  frontend_lint:
    command: npm run lint
    result: 0 errors, 38 non-blocking warnings (all pre-existing on files this item
      did not touch); eslint-plugin-jsx-a11y (new) surfaced and fixed 1 real error
      (jsx-a11y/no-autofocus on ConfirmDialog.tsx)
  frontend_duplication:
    command: npm run duplication
    result: passed
  frontend_format_check:
    command: npm run format:check
    result: passed
  frontend_license_check:
    command: npm run license:check
    result: MIT 5, UNLICENSED 1 (project package itself)
  frontend_npm_audit:
    command: npm audit --audit-level=low
    dependencies_added: 3 (dev-only)
    vulnerable: 0
    findings: 0
  frontend_trivy_filesystem_scan:
    command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
      --skip-dirs node_modules projects/healthcare-operations-platform/07-implementation/employee-portal
    version: 0.72.0
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
  backend_maven_quality_profile:
    command: mvn -f pom.xml clean verify -Pquality -Dhop.local-db-tests=true
    result: BUILD SUCCESS; 327 tests, 0 failures/errors/skipped; line coverage 83.99%
      (previous floor 83.96%)
  backend_owasp_dependency_check:
    command: mvn -Pquality org.owasp:dependency-check-maven:check -DautoUpdate=false
    dependency_check_version: 12.1.3
    report: 07-implementation/backend/target/dependency-check-report.json
    report_date_utc: 2026-07-22 18:03:17.077591+00:00
    data_directory: C:/Documents/Proyectos/Laboratorio/dependency-check-data
    dependencies_scanned: 65
    vulnerable_dependencies: 0
    vulnerabilities_found: 0
    post_fix_reverification: passed
    note: Re-run after the Jackson 3.1.5 pin using the project's quality-profile Dependency-Check
      configuration and the local advisory database available at execution time. This
      successful post-fix project-profile scan supersedes the earlier pre-fix Dependency-Check
      evidence.
  backend_trivy_filesystem_scan:
    command: trivy fs --scanners vuln,secret,misconfig --severity UNKNOWN,LOW,MEDIUM,HIGH,CRITICAL
      --skip-dirs target --skip-dirs .m2 --skip-dirs .mvn projects/healthcare-operations-platform/07-implementation/backend
    version: 0.72.0
    vulnerabilities_before_fix: 1 (MEDIUM, CVE-2026-59889, tools.jackson.core:jackson-databind
      3.1.4)
    vulnerabilities_after_fix: 0
    secrets: 0
    misconfigurations: 0
  yaml_parse:
    method: parsed every touched/added .yml/.yaml file under projects/healthcare-operations-platform
    errors: 0
  agent_agnostic_scan:
    method: case-insensitive grep for openai|claude|cursor|gemini|copilot|anthropic
      across every touched employee-portal/backend source and test file
    total_matches: 4
    disposition: the CSS `cursor` pseudo-property (values `pointer`/`not-allowed`)
      in employee-portal/src/styles.css; not a real vendor/agent reference
    real_source_code_hits: 0
  secrets_scan:
    tool: trivy secret scanner (part of both fs scans above)
    findings: 0
  git_diff_check:
    command: git diff --check
    result: 0 whitespace errors
closure:
  vulnerability_fixed:
  - id: CVE-2026-59889
    description: jackson-databind (tools.jackson.core, Jackson 3.x line managed by
      spring-boot-starter-parent 4.1.0) @JsonView bypassed for @JsonUnwrapped container
      properties on deserialization
    severity: MEDIUM
    fixed_version: 3.1.5 (pinned; upstream also offers 3.2.1)
    fix_location: 07-implementation/backend/pom.xml (dependencyManagement, mirrors
      the existing pattern already used to pin the classic Jackson 2.x line for CVE-2026-54515)
    found_via: Trivy filesystem scan on the backend directory during this backlog
      item's mandatory quality gates
    regression_status: 327 backend tests, 0 failures after the pin; JaCoCo coverage
      unchanged at 83.99%
  technical_debt_closed:
  - id: TD-UX-002
    contribution: retrofitted both acceptance criteria (documented responsive breakpoints,
      automated jest-axe accessibility check) into employee-portal itself, this debt's
      originally discovered affected_area, closing the remaining_scope COM-MOD-011-WEB-001
      left open
  real_defect_fixed:
  - description: QuotationRequest had no channel field, unlike AppointmentSlot, so
      public-website-submitted quotation drafts could not be reliably distinguished
      from staff-initiated ones for the new triage queue screen
    files:
    - 07-implementation/backend/.../quotationmanagement/domain/QuotationRequest.java
    - 07-implementation/backend/.../quotationmanagement/application/QuotationManagementService.java
    - 07-implementation/backend/src/main/resources/db/front-desk-care-delivery/schema.sql
    regression_status: 4 new/extended backend tests, 0 new failures; 0 breaking change
      to any existing caller (channel defaults to employee_portal when omitted)
  new_debt_registered: []
```
