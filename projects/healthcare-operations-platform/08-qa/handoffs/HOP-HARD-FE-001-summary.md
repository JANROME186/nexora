---
id: HOP-HARD-FE-001-summary
type: backlog-handoff
status: closed
backlog_item: HOP-HARD-FE-001
---

# HOP-HARD-FE-001 Summary

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-FE-001-summary
  type: backlog-handoff
  status: closed
  backlog_item: HOP-HARD-FE-001
  module_id: HOP-FINAL-HARDENING
summary:
  closed_scope:
  - 'TD-FE-002 -- Employee portal patient/doctor update, patient document management and doctor
    specialty assignment UI: 11 new peopleApi.ts client functions against already-implemented
    backend endpoints; PatientsScreen.tsx and DoctorsScreen.tsx gained edit/deactivate/retire
    panels plus documents and specialty-assignment management; the 3 dead exports the debt named
    (getPatient, getDoctor, getPatientRegistration) were removed after confirming they remained
    unreferenced by any screen.'
  - 'TD-FE-006 -- Employee portal Appointment Scheduling, Admission Management and Quotation
    Management UI: frontDeskApi.ts extended with the full BCM-ATT-001/004/006 controller surface
    (25 functions); 3 new screens (AppointmentsScreen, AdmissionsScreen, QuotationsScreen) wired
    into 3 new ScreenKey/PermissionCode pairs, 3 new AppShell tabs (es-MX/en-US) and App.tsx.
    Admission commit-to-order and quotation convert-to-order are each exercised end to end by a
    dedicated test.'
  materially_reduced:
  - 'TD-FE-010 -- screen-composition lint debt: extended the shared decomposition pattern to all
    3 new screens (0 new lint warnings) and to the 2 touched legacy screens (no new warning
    category introduced).'
  - 'TD-UX-001 -- shared component library: all 3 new screens adopt
    DataTable/StatusBanner/ScopeIndicator/ConfirmDialog; 6 new focused panel components extracted
    from the 2 touched legacy screens.'
  reviewed_unchanged:
  - 'TD-FE-003 -- quality profile: re-verified end to end under real new-feature load; all gates
    passed.'
  - 'TD-FE-005 -- production security headers: still blocked on a not-yet-scheduled production
    hosting backlog item; compensating control (no DOM-XSS sink) re-confirmed.'
  - 'TD-FE-012 -- npm audit devDependency findings: re-verified unchanged (10 high, dev-only, 0
    production).'
  - 'TD-I18N-002 -- full localization adoption: the 3 new screens'' navigation labels are
    locale-aware; their body copy follows the same pre-existing, documented convention as the
    other ~13 MESSAGES-direct screens (not a regression).'
validation:
  qa_evidence: 08-qa/qa/final-hardening/HOP-HARD-FE-001-validation.md
  security_quality_evidence: 08-qa/security-quality/HOP-HARD-FE-001/security-quality-evidence.md
  frontend_gate:
    command: npm run quality (employee-portal)
    status: passed
    tests_run: 275
    line_coverage_percent: 91.68
  trivy_frontend_filesystem:
    status: passed_zero_findings
technical_debt:
  closed:
  - TD-FE-002
  - TD-FE-006
  materially_reduced:
  - TD-FE-010
  - TD-UX-001
notable_findings_for_next_owner:
- 'The backend already fully implements every operation TD-FE-002/TD-FE-006 needed
  (PatientController/DoctorController/AppointmentController/AdmissionRequestController/QuotationController
  in 07-implementation/backend/.../peopleclinicalmasterdata and .../frontdeskcaredelivery); this
  item was a pure frontend client+UI compilation exercise with zero backend risk, consistent with
  how both debts were originally scoped.'
- 'AppointmentsScreen/AdmissionsScreen/QuotationsScreen each needed a dedicated custom hook
  (useAppointmentsScreenState / useAdmissionsScreenState / useQuotationsScreenState) bundling
  state and useAsyncAction calls, not just JSX sub-component extraction, to get the top-level
  screen component under ESLint''s 120-line max-lines-per-function threshold with 0 warnings --
  JSX-only decomposition (detail panels, list forms) was necessary but not sufficient for
  brand-new screens with this many independent lifecycle actions. This hook-extraction pattern is
  worth reusing for any future screen with 5+ independent async actions.'
- 'employee-portal already has a `publicRequestsApi.ts` (COM-MOD-011-FE-001) exposing a narrower
  triage-scoped subset of the same BCM-ATT-001/006 endpoints (list/confirm/cancel for
  appointments; list/issue/cancel for quotations) for PublicAppointmentRequestsScreen/PublicQuotationRequestsScreen.
  frontDeskApi.ts''s new, broader functions are a separate, intentionally non-overlapping module
  for the general staff-initiated lifecycle (request/check-in/no-show/accept/convert/expire) --
  do not merge the two API modules; they serve different screens with different scopes.'
- 'TD-FE-005 remains genuinely blocked: no employee-portal production hosting/deployment backlog
  item exists yet anywhere in the backlog map. The next owner who defines that hosting layer is
  the one who should close TD-FE-005.'
next:
  backlog_item: HOP-HARD-APP-001
  focus: Mobile, patient portal, doctor portal and imaging delivery hardening.
```
