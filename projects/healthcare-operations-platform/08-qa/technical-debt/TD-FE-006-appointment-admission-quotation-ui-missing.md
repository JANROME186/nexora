---
id: TD-FE-006
format: markdown_structured_payload
type: technical-debt-item
name: Employee portal is missing dedicated Appointment Scheduling, Admission Management
  and Quotation Management UI
version: 2.0.0
status: closed
---

# Employee Portal Is Missing Dedicated Appointment Scheduling, Admission Management And Quotation Management Ui

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-FE-006
  type: technical-debt-item
  name: Employee portal is missing dedicated Appointment Scheduling, Admission Management
    and Quotation Management UI
  version: 2.0.0
  status: closed
  created_date: 2026-07-16
  updated_date: 2026-07-27
  closed_by_backlog_item: HOP-HARD-FE-001
source:
  discovered_during_backlog_item: MVP-MOD-004-FE-001
  module: MVP-MOD-004 Front Desk and Care Delivery
  evidence: 08-qa/qa/front-desk-care-delivery/MVP-MOD-004-FE-001-validation.md
classification:
  category: ui_completeness
  affected_area: employee_portal_front_desk_care_delivery_screens
  affected_components:
  - 07-implementation/employee-portal/src/api/frontDeskApi.ts
  - 07-implementation/employee-portal/src/components/screens/ReceptionScreen.tsx
  - 07-implementation/employee-portal/src/components/screens/DiagnosticOrdersScreen.tsx
  risk_level: low
  blocking: false
  reason_non_blocking: 'MVP-MOD-004-FE-001 delivered the two screens the backlog objective
    and acceptance summary name explicitly — the front desk worklist (Reception Management)
    and diagnostic order creation/ lifecycle (Diagnostic Order Management), covering
    both walk-in and scheduled intake via the order''s intakeChannel selector. Appointment
    Scheduling, Admission Management and Quotation Management are fully implemented,
    tested backend capabilities (MVP-MOD-004-BE-001/BE-002) with no employee-portal
    client or screen yet; administrators with API access are not blocked, only the
    portal convenience is missing. Registering this now (rather than leaving it undocumented)
    mirrors the disposition TD-FE-002 used for the equivalent gap after MVP-MOD-003-FE-001.

    '
current_state:
  issue: '07-implementation/employee-portal/src/api/frontDeskApi.ts only exports client
    functions for BCM-ATT-003 (Reception Management) and BCM-LAB-001 (Diagnostic Order
    Management). BCM-ATT-001 (Appointment Scheduling: request/confirm/check-in/cancel/no-show,
    6 endpoints), BCM-ATT-004 (Admission Management: start/mark-ready/commit/reject,
    4 endpoints) and BCM-ATT-006 (Quotation Management: start/issue/accept/convert/cancel/expire,
    6 endpoints) have no employee-portal client function and therefore no screen or
    AppShell tab.

    '
  compensating_control:
  - Every operation in this list is reachable and already covered by backend contract/API
    tests (MVP-MOD-004-BE-001/BE-002 QA evidence), so administrators with direct API
    access are not blocked; only the employee-portal UI convenience is missing.
  - Reception Management's visit-start flow already supports linking a walk-in visit
    to an appointment id captured out-of-band, so front-desk check-in is not blocked
    by the missing Appointment Scheduling screen.
target_state:
  preferred_open_source_tooling:
  - No new tooling required; extend frontDeskApi.ts with the 16 missing client functions
    and add AppointmentsScreen.tsx, AdmissionsScreen.tsx and QuotationsScreen.tsx
    following the same useAsyncAction/StatusBanner/ConfirmDialog/i18n patterns already
    used by ReceptionScreen.tsx and DiagnosticOrdersScreen.tsx.
  expected_integration_points:
  - 07-implementation/employee-portal/src/api/frontDeskApi.ts
  - 07-implementation/employee-portal/src/components/screens/AppointmentsScreen.tsx
    (SCR-APT-001-01/02/03)
  - 07-implementation/employee-portal/src/components/screens/AdmissionsScreen.tsx
    (SCR-ADM-004-01/02/03)
  - 07-implementation/employee-portal/src/components/screens/QuotationsScreen.tsx
    (SCR-QUO-006-01/02/03)
  - 07-implementation/employee-portal/src/components/layout/AppShell.tsx (3 new ScreenKey
    tabs)
remediation:
  strategy: gradual_when_a_future_ui_backlog_item_covers_scheduling_admission_or_quotation_workflows
  owner: frontend_platform_team
  target_backlog: mvp_mod_004_follow_up_ui_backlog_item_not_yet_scheduled
  priority: P2
  recommended_trigger:
  - A future MVP-MOD-004 follow-up UI backlog item, or a subsequent module requiring
    appointment scheduling, admission intake or quotation workflows from the portal.
  acceptance_criteria:
  - frontDeskApi.ts exports a function for every Appointment Scheduling, Admission
    Management and Quotation Management operation declared in the 3 capability packages'
    openapi-source.md files.
  - AppointmentsScreen.tsx, AdmissionsScreen.tsx and QuotationsScreen.tsx exist with
    loading/ error/empty/confirmation/success states consistent with the rest of the
    module, and are wired into AppShell.tsx.
  - Admission's commit-to-order and Quotation's convert-to-order flows are exercised
    end to end from the portal, not only via direct API calls.
closure:
  backlog_item: HOP-HARD-FE-001
  evidence: 08-qa/qa/final-hardening/HOP-HARD-FE-001-validation.md
  summary: 'frontDeskApi.ts extended with the full BCM-ATT-001/004/006 controller surface (25
    functions: list/get/request/confirm/check-in/cancel/no-show/requested-items/preparation-instructions
    for appointments; list/get/catalog-selections/start/mark-ready/commit/reject for admissions;
    list/get/lines/start/issue/accept/convert/cancel/expire for quotations). Three new screens
    (AppointmentsScreen.tsx, AdmissionsScreen.tsx, QuotationsScreen.tsx) were added following the
    existing useAsyncAction/StatusBanner/ConfirmDialog/DataTable pattern, wired into 3 new
    ScreenKey/PermissionCode entries (SCREEN_APPOINTMENTS/ADMISSIONS/QUOTATIONS, granted to
    FRONT_DESK alongside the existing reception/diagnostic-orders permissions and to ADMIN via the
    existing PERMISSION_CODES derivation), 3 new AppShell tab labels (es-MX/en-US locale catalogs)
    and App.tsx''s SCREEN_COMPONENTS map. Admission''s commit-to-order and Quotation''s
    convert-to-order flows are each exercised end to end by a dedicated test
    (AdmissionsScreen.test.tsx "commits a ready admission to a diagnostic order",
    QuotationsScreen.test.tsx "accepts an issued quotation and converts it to a diagnostic
    order"), not only asserted via direct API-shape tests. All three acceptance criteria are met;
    closed.'
  new_tests:
  - AppointmentsScreen.test.tsx (4 tests), AdmissionsScreen.test.tsx (4 tests), QuotationsScreen.test.tsx
    (4 tests)
  - frontDeskApi.test.ts extended with request-shape assertions for all 25 new functions
  - AppSmoke.test.tsx and SessionContext.test.tsx updated for the 3 new navigation tabs (62 -> 65
    ADMIN tabs; FRONT_DESK 12 -> 15 tabs)
```
