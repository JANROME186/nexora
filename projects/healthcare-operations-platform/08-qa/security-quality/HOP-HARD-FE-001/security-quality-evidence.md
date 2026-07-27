---
id: HOP-HARD-FE-001-security-quality-evidence
type: security-quality-evidence
status: validated
backlog_item: HOP-HARD-FE-001
---

# HOP-HARD-FE-001 Security Quality Evidence

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-FE-001-security-quality-evidence
  type: security-quality-evidence
  status: validated
  backlog_item: HOP-HARD-FE-001
  module_id: HOP-FINAL-HARDENING
tools:
  npm_quality:
    command: npm run quality
    working_directory: 07-implementation/employee-portal
    status: passed
    tests_run: 275
    test_files: 72
    failures: 0
    coverage_line_percent: 91.68
  eslint:
    command: npm run lint
    status: passed
    errors: 0
    warnings: 61
    note: All 61 warnings are pre-existing categories in legacy screens (max-lines-per-function)
      or locale catalogs (sonarjs/no-duplicate-string); the 3 brand-new screens
      (AppointmentsScreen, AdmissionsScreen, QuotationsScreen) added by this item report 0
      warnings of any category, matching the COM-MOD-010-FE-001 precedent for new screens. The 2
      legacy screens this item touched (PatientsScreen, DoctorsScreen) did not gain any new
      warning category beyond the max-lines-per-function warning both already carried.
  npm_audit:
    command: npm run audit:all (npm audit --audit-level=low)
    status: passed_with_documented_non_blocking_findings
    high_severity_findings: 10
    findings_scope: transitive_devdependencies_only
    production_audit_command: npm audit --omit=dev --audit-level=low
    production_audit_result: 0_vulnerabilities
    reference: 08-qa/technical-debt/TD-FE-012-employee-portal-npm-audit-devdependency-high-severity-findings.md
  trivy:
    command: trivy fs --scanners vuln,secret,misconfig --exit-code 0 --no-progress --skip-dirs
      "node_modules,dist" employee-portal
    working_directory: 07-implementation
    status: passed
    vulnerabilities: 0
    secrets: 0
    misconfigurations: 0
  license_checker:
    command: npm run license:check (license-checker-rseidelsohn --summary --production)
    status: passed
    summary: 5 MIT, 1 UNLICENSED (unchanged from prior baseline; production dependencies remain
      only react and react-dom)
  jscpd:
    command: npm run duplication
    status: passed
    duplicates_found: 0
  prettier:
    command: npm run format:check
    status: passed
  git_diff_check:
    status: passed
source_scan:
  intent: Confirm the 3 new screens and 2 touched legacy screens introduce no DOM-XSS sink,
    matching TD-FE-005's existing compensating control.
  command: grep -rE "dangerouslySetInnerHTML|innerHTML|eval\(" src/
  working_directory: 07-implementation/employee-portal
  result: no_matches
authorization_and_permission_wiring:
  new_screens: 3 (AppointmentsScreen, AdmissionsScreen, QuotationsScreen)
  new_screen_keys: SCREEN_APPOINTMENTS, SCREEN_ADMISSIONS, SCREEN_QUOTATIONS
  permission_registration: All 3 new ScreenKey/PermissionCode pairs are registered in
    SCREEN_TO_PERMISSION and granted to FRONT_DESK (alongside the existing reception/diagnostic-orders
    permissions that role already held) and to ADMIN via the existing PERMISSION_CODES derivation,
    matching the enterprise-product-foundation-standard's dynamic-menu-generation requirement that
    unauthorized navigation be hidden, not just disabled.
  backend_authorization: Every new frontend client function calls an existing, already-authorized
    backend endpoint (AppointmentController, AdmissionRequestController, QuotationController,
    PatientController, DoctorController); no new backend endpoint was added by this item, so no
    new EndpointPermissionRegistry entry was required.
  test_coverage: SessionContext.test.tsx extended to assert FRONT_DESK sees the 3 new tabs (12 ->
    15 tabs) and ADMIN sees all 65 tabs, confirming the permission wiring is exercised, not only
    declared.
security_decision:
  no_new_critical_or_high_dependency_vulnerabilities_detected: true
  trivy_all_severities_clean_for_frontend_source_scan: true
  new_navigation_authorization_enforced: true
  no_dom_xss_sink_introduced: true
  residual_devdependency_audit_debt_blocks_final_project_closure: true
  residual_devdependency_audit_debt_blocks_this_slice_closure: false
  rationale: HOP-HARD-FE-001 closed both of its UI-completeness mapped technical-debt items
    (TD-FE-002, TD-FE-006), adding 36 new typed client functions and 3 new screens that each call
    already-authorized, already-tested backend endpoints, with permission-filtered navigation
    wiring verified by test, without introducing any new dependency vulnerability, secret,
    misconfiguration or DOM-XSS sink, and without regressing the frontend line-coverage floor
    (91.00% -> 91.68%). The residual TD-FE-012 npm-audit findings are unchanged, pre-existing,
    transitive-devDependency-only and do not affect the production dependency graph.
next_backlog_item: HOP-HARD-APP-001
```
