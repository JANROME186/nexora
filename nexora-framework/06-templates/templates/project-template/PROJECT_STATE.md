---
id: PROJECT_STATE
format: markdown_structured_payload
---

# Project State

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
project: TBD
repository_version: 0.1.0
current_phase: Intake
development_readiness:
  status: not_ready
  ready_to_start_module: null
  business_requirement_ready: false
  technology_debt_backlog_ready: true
  framework_feedback_ready: true
  integrated_local_solution_runbook_ready: false
  client_stack_market_validation_ready: false
  stack_quality_toolchain_baseline_ready: false
  blocking_definition_gaps:
  - business_requirement_missing_requester_must_provide
  - project_brief_not_completed
  - integrated_local_solution_runbook_missing
  - client_stack_market_validation_missing
  - stack_quality_toolchain_baseline_missing
  - capability_map_missing
  - mvp_framework_missing
technology_debt:
  index: 08-qa/technical-debt/technical-debt-index.md
  review_policy: Review during every code-changing backlog item, module closeout and
    release readiness gate.
  remediation_policy: Address accepted debt gradually when affected components are
    touched, with increasing burn-down intensity as the project advances.
  final_project_closure_requires_no_open_debt: true
coverage_policy:
  target_line_coverage_percent: 80
  previous_iteration_coverage_is_hard_floor: true
  final_project_closure_requires_target: true
framework_feedback:
  index: 08-qa/framework-feedback/framework-feedback-index.md
  capture_policy: Capture feedback when execution reveals reusable framework ambiguity,
    gaps or automation opportunities.
  implementation_policy: Agents may propose framework improvements but must not implement
    them unless Nexora explicitly assigns the central framework backlog item.
local_solution_runbook:
  human_readable: 09-operations/runbooks/local-solution-runbook.md
  machine_readable: 09-operations/runbooks/local-solution-runbook.md
  review_policy: Maintain a single integrated local startup, validation and shutdown
    guide for human reviewers.
```
