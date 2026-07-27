---
id: HOP-HARD-FE-001-validation
type: qa-validation-evidence
status: validated
backlog_item: HOP-HARD-FE-001
---

# HOP-HARD-FE-001 Validation

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-FE-001-validation
  type: qa-validation-evidence
  status: validated
  backlog_item: HOP-HARD-FE-001
  module_id: HOP-FINAL-HARDENING
summary:
  decision: validated_two_mapped_items_closed_remaining_materially_reviewed
  implemented:
  - 'TD-FE-002 (closed): peopleApi.ts now exports all 11 previously-missing client functions
    (updatePatient, deactivatePatient, updatePatientRepresentative, listPatientDocuments,
    attachPatientDocument, removePatientDocument, updateDoctor, retireDoctor,
    listSpecialtyAssignments, assignSpecialty, unassignSpecialty), confirmed against the
    already-implemented, already-tested PatientController.java/DoctorController.java endpoints
    (pure client/UI gap, no backend risk, as the debt itself documented). PatientsScreen.tsx
    gained an edit-patient panel, a deactivate action (with confirmation), a documents panel
    (list/attach/remove, with confirmation) and representative edit-in-place; DoctorsScreen.tsx
    gained an edit-doctor panel, a retire action (with confirmation) and a specialty-assignment
    panel (list/assign/unassign, with confirmation) -- all following the existing
    useAsyncAction/StatusBanner/ConfirmDialog pattern. The 3 dead exports named in the debt
    (getPatient, getDoctor, getPatientRegistration) were confirmed still unreferenced by any
    screen and removed, closing the third acceptance criterion.'
  - 'TD-FE-006 (closed): frontDeskApi.ts extended with the full BCM-ATT-001/004/006 controller
    surface (25 functions covering Appointment Scheduling, Admission Management and Quotation
    Management). Three new screens added: AppointmentsScreen.tsx (request/confirm/check-in/cancel/no-show),
    AdmissionsScreen.tsx (start/mark-ready/commit-to-order/reject) and QuotationsScreen.tsx
    (start/issue/accept/convert-to-order/cancel/expire), wired into 3 new ScreenKey/PermissionCode
    pairs (SCREEN_APPOINTMENTS/ADMISSIONS/QUOTATIONS), 3 new AppShell tabs (es-MX/en-US locale
    catalogs) and App.tsx. Admission''s commit-to-order and Quotation''s convert-to-order flows
    are each exercised end to end by a dedicated test, not only asserted via direct API-shape
    tests.'
  - 'TD-FE-010 (materially reduced further): extended the COM-MOD-010-FE-001 shared decomposition
    pattern (small, props-driven sub-components; one useAsyncAction per operation kept in the
    top-level screen only; a per-screen custom hook bundling state/async-actions where JSX-only
    decomposition was insufficient) to all 3 new screens (0 new lint max-lines-per-function/complexity/cognitive-complexity
    warnings) and to the 2 legacy screens this item touched (PatientsScreen, DoctorsScreen: 6 new
    sub-components extracted so the added update/deactivate/documents/specialty logic did not
    introduce any NEW warning category beyond the pre-existing max-lines-per-function warning both
    screens already carried).'
  - 'TD-FE-012 (reviewed, unchanged): re-ran `npm audit --audit-level=low` and `npm audit --omit=dev
    --audit-level=low`; findings unchanged from the COM-MOD-017-FE-001 baseline (10 high-severity,
    all transitive devDependency-only; 0 production vulnerabilities). No drift; remains
    open/non-blocking pending a dedicated devDependency-maintenance backlog item.'
  - 'TD-FE-003 (reviewed, unchanged): re-ran the full `npm run quality` pipeline plus
    `npm run audit:all` under real new-feature load; all gates passed. No change to residual
    scope.'
  - 'TD-FE-005 (reviewed, unchanged): confirmed via grep that no dangerouslySetInnerHTML/innerHTML/eval()
    usage exists across the 3 new screens or the 2 touched legacy screens; the compensating
    control still holds. No employee-portal production hosting backlog item exists yet, so this
    item remains correctly scoped as blocked on that prerequisite.'
  - 'TD-I18N-002 (reviewed, unchanged): the 3 new screens'' AppShell tab labels were added to both
    locale catalogs (es-MX/en-US), keeping navigation fully locale-aware; their screen-body copy
    is inline English JSX text, consistent with (not a regression from) this item''s
    already-documented baseline that screen-body copy is the remaining migration scope.'
  - 'TD-UX-001 (materially reduced further): all 3 new screens adopt the shared
    DataTable/StatusBanner/ScopeIndicator/ConfirmDialog components; the legacy-screen touch also
    produced 6 new focused, reusable panel components in the same spirit as this item''s target
    Button/FormField extraction, though scoped to their specific panels rather than the generic
    primitives this item ultimately still wants.'
technical_debt_result:
  closed:
  - TD-FE-002
  - TD-FE-006
  materially_reduced:
  - TD-FE-010
  - TD-UX-001
  reviewed_unchanged:
  - TD-FE-003
  - TD-FE-005
  - TD-FE-012
  - TD-I18N-002
tests:
  frontend_quality:
    command: npm run quality (typecheck && lint && test:coverage && build && duplication &&
      format:check && license:check)
    working_directory: 07-implementation/employee-portal
    status: passed
    typecheck: passed
    lint_errors: 0
    lint_warnings: 61
    lint_warnings_note: All 61 warnings are pre-existing categories in legacy screens or locale
      catalogs (max-lines-per-function on screens that already carried it before this item;
      sonarjs/no-duplicate-string in locale catalogs and 1 pre-existing legacy screen). The 3
      brand-new screens added by this item (AppointmentsScreen, AdmissionsScreen,
      QuotationsScreen) and the 2 touched legacy screens (PatientsScreen, DoctorsScreen) do not
      introduce any new warning category.
    tests_run: 275
    test_files: 72
    failures: 0
    line_coverage_percent: 91.68
    previous_frontend_floor_percent: 91.00
    coverage_result: improved_above_floor
    build: passed
    duplication: passed_zero_duplicates
    format_check: passed
    license_check: passed_5_mit_1_unlicensed
  audit:
    command: npm run audit:all (npm audit --audit-level=low)
    status: passed_with_documented_non_blocking_findings
    high_severity_findings: 10
    findings_note: Unchanged from the TD-FE-012 baseline; all in transitive devDependencies
      (eslint-plugin-jsx-a11y, eslint-plugin-react, glob/minimatch, test-exclude/@vitest/coverage-v8).
    production_audit:
      command: npm audit --omit=dev --audit-level=low
      status: passed
      vulnerabilities: 0
  added_or_extended_tests:
  - "PatientsScreen.test.tsx - 2 new tests: update+deactivate, documents attach/remove"
  - "DoctorsScreen.test.tsx - 2 new tests: update+retire, specialty assign/unassign"
  - AppointmentsScreen.test.tsx (4 new tests)
  - AdmissionsScreen.test.tsx (4 new tests)
  - QuotationsScreen.test.tsx (4 new tests)
  - "peopleApi.test.ts - extended with 11 new function request-shape assertions, 3 dead-code
    assertions removed"
  - "frontDeskApi.test.ts - extended with 25 new function request-shape assertions"
  - "AppSmoke.test.tsx - extended with navigation to the 3 new screens; ADMIN tab count 62 -> 65"
  - "SessionContext.test.tsx - updated ADMIN tab count 62 -> 65 and FRONT_DESK tab count 12 -> 15,
    including the 3 new screens"
  regression_check:
  - Ran the full frontend suite (275 tests, 72 files) before and after every decomposition
    refactor; 0 regressions in any pre-existing screen or API test.
quality_gates:
  typecheck: passed
  eslint: passed_0_errors
  vitest_coverage: passed
  vite_build: passed
  jscpd: passed
  prettier: passed
  license_checker: passed
  npm_audit: passed_with_documented_non_blocking_dev_only_findings
  trivy_frontend_filesystem: passed_zero_vulnerabilities_zero_secrets_zero_misconfigurations
  git_diff_check: passed
residual_debt:
  note: TD-FE-002 and TD-FE-006 are closed; no residual scope remains under either. TD-FE-010 and
    TD-UX-001 remain materially_reduced (incremental, ongoing by design). TD-FE-003, TD-FE-005,
    TD-FE-012 and TD-I18N-002 remain in their pre-existing status; none regressed and none were
    newly blocked by this item's changes.
  tracked_under:
  - TD-FE-010
  - TD-FE-012
  - TD-I18N-002
  - TD-FE-005
  - TD-UX-001
next_backlog_item: HOP-HARD-APP-001
```
