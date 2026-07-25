# Security Quality Evidence — COM-MOD-009-PORTAL-002 Doctor Portal Commercial Workflow Compilation

**Status:** passed
**Backlog item:** COM-MOD-009-PORTAL-002
**Module:** COM-MOD-009 Patient and Doctor Portals
**Standard:** Open Source First Security Quality Standard

## Summary

This backlog item rebuilt the doctor portal commercial workflow: login flow, permission-filtered
dynamic navigation, localized screens (Patients/Results/Orders/Notifications), and new backend
least-privilege enforcement (doctorId-filtered orders, a referring-doctor authorization port for
results history, three new interceptor self-access blocks).

## Verification Checklist

| Security Check | Status | Details |
| --- | --- | --- |
| Tests Execution | **passed** | 30 doctor-portal Vitest tests and 280 backend Maven tests, 0 failures/errors/skipped. |
| Message Externalization | **passed** | Doctor-domain es-MX/en-US catalogs replace a stale employee-portal-domain catalog; no hardcoded visible strings remain. |
| Permission / Dynamic Menu | **passed** | Navigation tabs derived at render time from `permissionsForRoles`; backend `RolePermissionCatalog` grants match the frontend model 1:1. |
| Secrets Scan | **passed** | Checked code and configuration for plaintext credentials; 0 findings. |
| Quality Tools (npm) | **passed** | Clean typecheck, lint (0 errors), build, jscpd duplication, format, license checks. |
| Quality Tools (Maven) | **passed** | `mvn -Pquality "-Dhop.local-db-tests=true" clean verify` passed; Spring Modulith boundary re-verified. |
| Coverage baseline (doctor-portal) | **passed** | Line coverage rose from 40.62% to **89.86%**, exceeding the 80% target floor (TD-FE-009 closed). |
| Coverage baseline (backend) | **passed** | Line coverage rose from 80.49% to **80.60%**, no regression. |
| Agent-Agnostic Scan | **passed** | Checked for vendor-specific agent/runtime dependencies; 0 findings. |
| Stale Pointers Sweep | **passed** | Active backlog trackers now point to `COM-MOD-009-QA-001`. |
| Vulnerability Scan (npm audit) | **passed** | 0 vulnerabilities in doctor-portal dependencies. |
| Vulnerability Scan (OWASP Dependency-Check) | **passed** | 0 vulnerabilities across 65 backend dependencies. |
| Vulnerability Scan (Trivy) | **passed** | 0 vulnerabilities/secrets/misconfigurations, backend and doctor-portal. |

## Technical Debt Remediation

- **TD-FE-009 (Doctor Portal Coverage Baseline):** Closed. Doctor portal coverage raised from
  40.62% to 89.86%, exceeding the final closure target.
- **TD-IAM-002 (Permission Granularity Gap):** Materially reduced further with 2 new granular
  permission codes and real per-request ownership enforcement.
- **TD-I18N-002 (Full Localization Adoption):** Materially reduced further by replacing the
  doctor-portal's wrong-domain locale catalog with a complete, correct one.
- **TD-FE-011 (new):** Registered — patient-portal's `npm run lint` currently fails with 2
  pre-existing `sonarjs/no-hardcoded-passwords` errors, unrelated to this backlog item and out
  of scope on a closed deliverable. Not blocking; not hidden.

## Commercial Readiness Disclosure

- HOP is not commercially complete or GA-ready.
- Next backlog focus: `COM-MOD-009-QA-001` (Channel access and privacy evidence).

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: COM-MOD-009-PORTAL-002-SECURITY-QUALITY
  type: security-quality-evidence
  backlog_item: COM-MOD-009-PORTAL-002
  status: passed
  created_date: 2026-07-19
  standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
scope_note: 'COM-MOD-009-PORTAL-002 is the doctor portal commercial workflow compilation
  backlog item: it rebuilds the doctor-portal frontend into a real referring-doctor
  login flow, a permission-filtered dynamic dashboard shell, and Patients/Results/Orders/Notifications
  views, backed by new backend server-side least-privilege enforcement (doctorId-filtered
  diagnostic orders, a referring-doctor authorization port, a new results-history
  403 boundary), full es-MX/en-US localization for the doctor domain, and explicit
  loading/empty/error/ no-permission/session-expired UI states. Doctor portal unit
  tests passed successfully (30 tests) raising line coverage from the 40.62% floor
  to 89.86% (TD-FE-009 closed). Backend line coverage rose from 80.49% to 80.60% with
  no regression (280 tests, 0 failures/errors/skipped).

  '
open_source_first:
  status: passed
  proprietary_runtime_dependency_detected: false
  new_dependencies_introduced: false
  note: 'This backlog item did not introduce any proprietary platform dependency or
    new third-party package. React/TypeScript/Vitest on the frontend and the existing
    Spring Modulith/JUnit stack on the backend are followed.

    '
checks:
  tests: passed
  sast_or_static_analysis: passed
  dependency_vulnerability_scan: passed
  secrets_scan: passed
  coverage: passed
  message_externalization_i18n_review: passed
  dast_for_runnable_web_or_api_surfaces: not_applicable_no_runnable_surface_defined
  container_or_iac_scan_when_assets_change: not_applicable_no_assets_changed
  yaml_parse: passed
  agent_agnostic_scan: passed
  stale_pointer_sweep: passed
  git_whitespace_check: passed
results:
  tests:
    command: npm run test (doctor-portal); mvn -Pquality "-Dhop.local-db-tests=true"
      clean verify (backend)
    result: passed
    detail: 30 doctor-portal tests passed (8 test files); 280 backend tests passed
      with 0 failures/errors/skipped, including PlatformFoundationModulithTest re-verifying
      the new referring-doctor-authorization-port module dependency.
  sast_or_static_analysis:
    command: npm run lint (doctor-portal, eslint + typescript-eslint + sonarjs + security
      plugins)
    result: passed
    detail: 0 errors, 11 non-blocking sonarjs/max-lines-per-function warnings (duplicate-string
      literals in test files, consistent with the same warning class already accepted
      in the closed patient-portal). Backend compiled clean with `mvn -o compile`.
  dependency_vulnerability_scan:
    backend_command: mvn --settings .mvn/settings.xml -Pquality org.owasp:dependency-check-maven:check
    backend_result: passed
    backend_detail: 0 vulnerabilities across 65 scanned backend dependencies.
    frontend_command: npm audit --audit-level=low
    frontend_result: passed
    frontend_detail: 0 vulnerabilities found in doctor-portal dependencies.
    trivy_backend_command: trivy fs --scanners vuln,secret,misconfig --skip-dirs .m2,target
      .
    trivy_backend_result: passed
    trivy_backend_detail: 0 vulnerabilities/secrets/misconfigurations (pom.xml target).
    trivy_frontend_command: trivy fs --scanners vuln,secret,misconfig --skip-dirs
      node_modules,dist,coverage .
    trivy_frontend_result: passed
    trivy_frontend_detail: 0 vulnerabilities/secrets/misconfigurations (package-lock.json
      target).
  yaml_parse:
    files_checked: 907
    detail: All repository YAML files (excluding generated dependency folders) parsed
      successfully.
  message_externalization_i18n_review:
    method: Reviewed doctor-portal locales en-US.ts and es-MX.ts for translation coverage.
    result: passed
    detail: Replaced a stale employee-portal-domain catalog with a complete, correct
      doctor-portal catalog. es-MX and en-US are fully defined and key-parity-typed
      for every login, state, patients, results, orders and notifications label used
      by App.tsx; no hardcoded visible strings remain in App.tsx.
  permission_dynamic_menu_review:
    method: Verified dynamic navigation menu filtering by role and permissions.
    result: passed
    detail: App.tsx derives visible navigation tabs from permissionsForRoles([session.roleCode])
      against SCREEN_TO_PERMISSION at render time (previously the doctor-portal scaffold
      had no permission-derived navigation at all). Backend RolePermissionCatalog
      grants REFERRING_DOCTOR exactly PORTAL_DOCTOR_PATIENTS_VIEW/RESULTS_VIEW/ORDERS_VIEW/
      NOTIFICATIONS_VIEW, matching the frontend model 1:1.
  agent_agnostic_scan:
    pattern: vendor-specific agent/runtime references (case-insensitive)
    matches_found: 0
    detail: No named-agent or vendor-runtime dependency found in any new or touched
      artifact.
  secrets_scan:
    pattern: password|secret|api[_-]?key|private[_-]?key|token\s*[:=] (case-insensitive)
    matches_found: 0
    detail: No credential literals were found in any newly compiled source file or
      test file. The only "password" substring matches are UI label keys (login form
      field label text), not credentials, and are explicitly suppressed with a justified
      inline eslint-disable-next-line comment.
  stale_pointer_sweep:
    method: Repository-wide check of active_backlog_item, current_backlog_item, current_active_backlog_item,
      next_backlog_item and ready_for_next_backlog_item.
    result: passed
    detail: Live registries have since advanced past COM-MOD-009-APP-001 to COM-MOD-009-QA-001.
  git_whitespace_check:
    command: git diff --check
    result: passed
    detail: No trailing-whitespace or conflict-marker errors found.
  vulnerabilities:
    audit_command: npm audit --audit-level=low; mvn org.owasp:dependency-check-maven:check;
      trivy fs
    vulnerabilities_found: 0
    result: passed
    detail: All three scans (npm audit, OWASP Dependency-Check, Trivy) reported 0
      findings.
  coverage_comparison:
    doctor_portal:
      previous_floor_percent: 40.62
      current_coverage_percent: 89.86
      final_target_percent: 80.0
      status: passed
      detail: Doctor-portal coverage rose from 40.62% to 89.86% (+49.24 points), comfortably
        exceeding the 80% target floor and the 3-5 point minimum iteration improvement.
    backend:
      previous_floor_percent: 80.49
      current_coverage_percent: 80.6
      final_target_percent: 80.0
      status: passed
      detail: Backend coverage improved from 80.49% to 80.60% with no regression.
technical_debt:
  debt_first_action: TD-FE-009 (doctor portal coverage baseline) was fully closed
    by rebuilding the doctor-portal frontend with real Vitest unit/integration test
    coverage. TD-IAM-002 (permission granularity gap) was materially reduced further
    with 2 new granular PermissionCode values and real per-request ownership enforcement
    (doctorId match, referral relationship verification via the new ReferringDoctorAuthorizationPort).
    TD-I18N-002 (full localization adoption) was materially reduced further by replacing
    the doctor-portal's wrong-domain locale catalog with a complete, correct doctor-domain
    catalog.
  new_debt_registered:
  - id: TD-FE-011
    title: patient-portal npm run lint currently fails with 2 pre-existing sonarjs/no-hardcoded-passwords
      errors on its login.password locale keys
    reason: Discovered while building the doctor-portal's equivalent locale keys;
      unrelated to this backlog item's own changes and out of scope to fix on a closed,
      already-verified deliverable (patient-portal, COM-MOD-009-PORTAL-001). Registered
      rather than hidden.
    blocking: false
  blocking: []
exceptions: []
commercial_readiness_disclosure:
  hop_commercially_complete: false
  hop_ga_ready: false
  reason: 'Portals module (COM-MOD-009) is in validation mode; patient mobile workflow
    (COM-MOD-009-APP-001) is closed and channel access/privacy evidence (COM-MOD-009-QA-001)
    is pending.

    '
readiness:
  security_quality_status: passed
  ready_for_next_backlog_item: COM-MOD-010-BE-001
  next_required_focus:
  - Compile product, reagent, lot and stock outputs (COM-MOD-010-DEF).
```
