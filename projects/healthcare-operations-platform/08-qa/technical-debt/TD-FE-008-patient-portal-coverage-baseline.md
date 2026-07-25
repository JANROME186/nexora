---
id: TD-FE-008
format: markdown_structured_payload
type: technical-debt-item
name: Establish patient portal test coverage baseline and raise it to the 80 percent
  target
version: 1.0.0
status: closed
---

# Establish Patient Portal Test Coverage Baseline And Raise It To The 80 Percent Target

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-FE-008
  type: technical-debt-item
  name: Establish patient portal test coverage baseline and raise it to the 80 percent
    target
  version: 1.0.0
  status: closed
  created_date: 2026-07-18
source:
  discovered_during_backlog_item: MVP-MOD-007-CLOSEOUT
  module: MVP-MOD-007
  evidence: 08-qa/qa/results-and-digital-delivery/MVP-MOD-007-CLOSEOUT-validation.md
classification:
  category: coverage_quality
  affected_area: patient_portal_test_coverage
  affected_components:
  - 07-implementation/patient-portal
  risk_level: medium
  blocking: false
  reason_non_blocking: 'MVP-MOD-007-PORTAL-001 closed with only an application smoke
    test for patient-portal; no line-coverage percentage was ever recorded in the
    project registries for this stack, so no coverage floor existed to regress against.
    MVP-MOD-007-CLOSEOUT is the first backlog item to measure it (`npm run test:coverage`),
    and records the real starting baseline instead of leaving it undocumented. Patient-portal
    in its current form is a minimal released-result view foundation; expansion of
    its functional surface is planned under COM-MOD-009 (Patient and Doctor Portals),
    which is the natural point to also raise this coverage.

    '
current_state:
  current_line_coverage_percent: 41.93
  previous_iteration_minimum_line_coverage_percent: null
  final_project_closure_target_percent: 80
  issue: '`npm run test:coverage` in 07-implementation/patient-portal measures 41.93%
    statement/line coverage from a single App smoke test. httpClient.ts, resultHistoryApi.ts,
    scopeContext.tsx, permissions.ts and syncAction.ts are entirely uncovered (0%);
    LocaleContext.tsx and SessionContext.tsx are partially covered through the smoke
    test only.

    '
target_state:
  line_coverage_percent: 80
  rule: 'This measured 41.93% becomes the hard floor for patient-portal from this
    point forward: no future patient-portal-touching iteration may drop below 41.93%.
    Coverage must reach 80% before HOP final project closure, following the same 3-5
    percentage point per-iteration improvement expectation applied to the other stacks
    once patient-portal functional work resumes.

    '
remediation:
  strategy: gradual_when_com_mod_009_patient_portal_commercial_workflow_is_implemented
  owner: frontend_platform_team
  target_backlog: COM-MOD-009-PORTAL-001
  priority: P1
  acceptance_criteria:
  - Patient-portal coverage is measured in every patient-portal-touching iteration.
  - Coverage does not drop below the 41.93% floor established here.
  - Coverage reaches at least 80% before HOP final project closure.
```
