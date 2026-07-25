---
id: TD-FE-009
format: markdown_structured_payload
type: technical-debt-item
name: Establish doctor portal test coverage baseline and raise it to the 80 percent
  target
version: 2.0.0
status: closed
---

# Establish Doctor Portal Test Coverage Baseline And Raise It To The 80 Percent Target

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-FE-009
  type: technical-debt-item
  name: Establish doctor portal test coverage baseline and raise it to the 80 percent
    target
  version: 2.0.0
  status: closed
  created_date: 2026-07-18
  closed_date: 2026-07-19
  closed_by_backlog_item: COM-MOD-009-PORTAL-002
source:
  discovered_during_backlog_item: MVP-MOD-007-CLOSEOUT
  module: MVP-MOD-007
  evidence: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-CLOSEOUT-validation.md
classification:
  category: coverage_quality
  affected_area: doctor_portal_test_coverage
  affected_components:
  - 07-implementation/doctor-portal
  risk_level: medium
  blocking: false
  reason_non_blocking: 'MVP-MOD-007-PORTAL-001 closed with only an application smoke
    test for doctor-portal; no line-coverage percentage was ever recorded in the project
    registries for this stack, so no coverage floor existed to regress against. MVP-MOD-007-CLOSEOUT
    is the first backlog item to measure it (`npm run test:coverage`), and records
    the real starting baseline instead of leaving it undocumented. Doctor-portal in
    its current form is a minimal released-result view foundation; expansion of its
    functional surface is planned under COM-MOD-009 (Patient and Doctor Portals),
    which is the natural point to also raise this coverage.

    '
current_state:
  current_line_coverage_percent: 40.62
  previous_iteration_minimum_line_coverage_percent: null
  final_project_closure_target_percent: 80
  issue: '`npm run test:coverage` in 07-implementation/doctor-portal measures 40.62%
    statement/line coverage from a single App smoke test. httpClient.ts, resultHistoryApi.ts,
    scopeContext.tsx, permissions.ts and syncAction.ts are entirely uncovered (0%);
    LocaleContext.tsx and SessionContext.tsx are partially covered through the smoke
    test only.

    '
target_state:
  line_coverage_percent: 80
  rule: 'This measured 40.62% becomes the hard floor for doctor-portal from this point
    forward: no future doctor-portal-touching iteration may drop below 40.62%. Coverage
    must reach 80% before HOP final project closure, following the same 3-5 percentage
    point per-iteration improvement expectation applied to the other stacks once doctor-portal
    functional work resumes.

    '
remediation:
  strategy: gradual_when_com_mod_009_doctor_portal_commercial_workflow_is_implemented
  owner: frontend_platform_team
  target_backlog: COM-MOD-009-PORTAL-002
  priority: P1
  acceptance_criteria:
  - Doctor-portal coverage is measured in every doctor-portal-touching iteration.
  - Coverage does not drop below the 40.62% floor established here.
  - Coverage reaches at least 80% before HOP final project closure.
closure:
  closed_by_backlog_item: COM-MOD-009-PORTAL-002
  closed_date: 2026-07-19
  evidence: 08-qa/qa/patient-and-doctor-portals/COM-MOD-009-PORTAL-002-validation.md
  final_line_coverage_percent: 89.86
  detail: COM-MOD-009-PORTAL-002 rebuilt the doctor-portal frontend (session context,
    permission model, i18n catalogs, API layer and App shell) and expanded the test
    suite from 1 smoke test to 30 tests across 8 test files, raising line coverage
    from the 40.62% floor to 89.86%, exceeding the 80% final-closure target. All acceptance
    criteria satisfied.
```
