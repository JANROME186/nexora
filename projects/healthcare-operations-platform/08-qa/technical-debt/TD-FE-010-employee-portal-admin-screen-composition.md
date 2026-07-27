---
id: TD-FE-010
format: markdown_structured_payload
type: technical-debt-item
name: Employee-portal generated administration screens exceed function-size and complexity
  warning thresholds
version: 1.2.0
status: materially_reduced
---

# Employee Portal Generated Administration Screens Exceed Function Size And Complexity Warning Thresholds

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-FE-010
  type: technical-debt-item
  name: Employee-portal generated administration screens exceed function-size and
    complexity warning thresholds
  version: 1.2.0
  status: materially_reduced
  created_date: 2026-07-19
  updated_date: 2026-07-27
source:
  discovered_during_backlog_item: MVP-MOD-008-FE-001
  module: MVP-MOD-008 Integration and Migration Readiness
  evidence: 08-qa/qa/integration-and-migration-readiness/MVP-MOD-008-FE-001-validation.md
classification:
  category: frontend_maintainability
  affected_area: employee_portal_admin_screen_composition
  affected_components:
  - 07-implementation/employee-portal/src/components/screens/IntegrationEndpointsScreen.tsx
  - 07-implementation/employee-portal/src/components/screens/ApiManagementScreen.tsx
  - 07-implementation/employee-portal/src/components/screens/MigrationJobsScreen.tsx
  risk_level: medium
  urgency: medium
  blocking: false
  reason_non_blocking: ESLint completed with exit code 0 and no errors; the warnings
    are maintainability warnings, not runtime failures, security findings, dependency
    vulnerabilities, test failures or coverage regressions. The new screens are covered
    by automated tests and the employee-portal line coverage improved from 85.50%
    to 86.47%.
current_state:
  issue: MVP-MOD-008-FE-001 added three broad generated administration screens that
    compile several capability operations each. ESLint reports max-lines-per-function
    warnings for all three and a complexity warning for MigrationJobsScreen. Comparable
    warnings already exist in older employee-portal screens, but these new warnings
    are explicitly registered so they are not hidden by the non-blocking lint profile.
  compensating_controls:
  - npm run quality passed with 101 frontend tests, production build, duplication,
    format and license checks.
  - npm run audit:all passed with 0 vulnerabilities.
  - Trivy filesystem scan over the employee portal reported 0 npm vulnerabilities.
  - Visible labels for the new screens are externalized in es-MX/en-US catalogs.
target_state:
  preferred_remediation: Decompose generated administration screens into smaller screen
    sections, form components and table/action components while preserving the typed
    integrationMigrationApi operation facade.
  quality_goal: New or touched employee-portal screen files should not introduce max-lines-per-function,
    complexity or cognitive-complexity warnings unless a dedicated technical-debt
    entry is created with owner, scope and target backlog.
remediation:
  strategy: gradual_refactor_when_employee_portal_admin_screens_are_next_touched
  owner: frontend_team
  estimated_effort: medium
  estimated_cost_impact: low
  target_backlog: MVP-MOD-008-QA-001_or_next_employee_portal_touch
  dependencies_or_prerequisites:
  - Preserve MVP-MOD-008-FE-001 screen behavior and tests.
  acceptance_criteria:
  - IntegrationEndpointsScreen, ApiManagementScreen and MigrationJobsScreen are decomposed
    so each function satisfies the configured ESLint function-size/complexity warning
    thresholds.
  - Existing tests remain green and coverage does not regress below the recorded 86.47%
    employee-portal baseline.
  - No visible text is moved back into component code; es-MX/en-US catalogs remain
    the source for labels.
progress_log:
- backlog_item: HOP-HARD-FE-001
  date: 2026-07-27
  action: 'Applied the COM-MOD-010-FE-001 shared decomposition pattern (small local sub-components
    receiving state and typed action handles as props, one useAsyncAction per operation kept in
    the top-level screen component only, plus a dedicated custom hook per screen bundling state
    and async actions where the JSX-only decomposition was not by itself sufficient) to all 3
    brand-new HOP-HARD-FE-001 screens (AppointmentsScreen, AdmissionsScreen, QuotationsScreen) and
    to the 2 legacy screens this item touched (PatientsScreen, DoctorsScreen). `npm run lint`
    reports 0 max-lines-per-function/complexity/cognitive-complexity warnings for any of the 3 new
    screens, meeting the quality_goal for new files without exception. PatientsScreen and
    DoctorsScreen (legacy, pre-existing max-lines-per-function warnings from before this item)
    were extended with 6 new sub-components (PatientEditPanel, PatientDocumentsPanel,
    RepresentativesPanel, RepresentativesTable, DoctorEditPanel, SpecialtiesPanel) specifically so
    the additional update/deactivate/documents/specialty logic did not introduce a NEW warning
    category (complexity/cognitive-complexity) beyond the pre-existing max-lines-per-function
    warning both screens already carried.'
  scope_decision: Did not rewrite the three originally-named legacy files (IntegrationEndpointsScreen,
    ApiManagementScreen, MigrationJobsScreen) in this backlog item; HOP-HARD-FE-001 does not touch
    BCM-PLT-004/005/010 and a behavior-preserving rewrite of unrelated, already-tested screens
    carries regression risk disproportionate to this item's scope.
  result: 0 lint errors (unchanged); the three originally-flagged files remain unchanged (still
    open on those specific files); no new employee-portal screen file introduced a
    max-lines-per-function/complexity/cognitive-complexity warning in this iteration.
- backlog_item: COM-MOD-010-FE-001
  date: 2026-07-20
  action: 'Implemented the preferred_remediation pattern for real: added two new shared
    components (components/common/DataTable.tsx generic list-table, components/common/statusPresentation.ts
    status-badge classifier) plus a per-screen decomposition convention (small local
    form/panel sub-components receiving state and typed action handles as props, one
    useAsyncAction per operation kept in the top-level screen component only). Applied
    this pattern to all 11 new Inventory and Internal Quality screens (BCM-INV-001..009,
    BCM-QLT-001/003/004/005); `npm run lint` reports 0 new max-lines-per-function/complexity/cognitive-complexity
    warnings for any of them, meeting the quality_goal for new files without exception.'
  scope_decision: Did not rewrite the three originally-named legacy files (IntegrationEndpointsScreen,
    ApiManagementScreen, MigrationJobsScreen) in this backlog item; COM-MOD-010-FE-001
    does not touch BCM-PLT-004/005/010 and a behavior-preserving rewrite of unrelated,
    already-tested screens carries regression risk disproportionate to this item's
    scope. status moved from `open` to `materially_reduced` because the underlying
    pattern this debt asked for now exists, is proven across 11 real screens, and
    is available to apply to the three legacy files the next time BCM-PLT-004/005/010
    screens are touched.
  result: 0 lint errors (unchanged); warning count for the three originally-flagged
    files is unchanged (still open on those specific files) but no longer grows with
    new employee-portal feature work.
```
