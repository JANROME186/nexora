---
id: PROJECT-TD-IDX
format: markdown_structured_payload
type: technical-debt-index
version: 0.1.0
status: active
---

# Project Td Idx

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: PROJECT-TD-IDX
  type: technical-debt-index
  version: 0.1.0
  status: active
  human_readable: README.md
  machine_readable: technical-debt-index.md
purpose: Track non-blocking technology modernization and migration debt discovered
  by iterative quality reviews.
policy:
  standard: ../../../../nexora-framework/02-standards/standards/open-source-first-security-quality-standard.md
  review_required_during:
  - every_code_changing_backlog_item
  - dependency_change
  - module_closeout
  - release_readiness
  remediation_strategy: gradual_when_affected_components_are_touched
  final_project_closure_requires_no_open_debt: true
  missing_required_tooling_policy:
    required: true
    rule: If a mandatory validation category applies to a touched stack or runnable
      surface but the project lacks an executable script, plugin, tool or configuration,
      create or update technical debt before closure. Do not treat missing tooling
      as informal not_applicable when the surface exists.
    debt_item_required_fields:
    - missing_validation_category
    - expected_open_source_tool_or_equivalent
    - affected_stack
    - affected_component
    - source_backlog_item
    - risk_level
    - owner_or_responsible_role
    - target_backlog
    - acceptance_criteria
    - closure_blocking_decision
  debt_burndown_intensity:
    early_mvp: at_least_one_relevant_debt_item_per_code_changing_iteration
    module_closeout: reduce_multiple_relevant_items_when_open_debt_exists
    release_or_ga: all_debt_closed
  coverage_policy:
    target_line_coverage_percent: 80
    previous_iteration_coverage_is_hard_floor: true
    below_target_requires_improvement_plan: true
    final_project_closure_requires_target: true
item_path_pattern: 08-qa/technical-debt/<debt-id>.yaml
entries: []
```
