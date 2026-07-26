---
artifact:
  id: HOP-PROMPT-HOP-COM-PROMPT-005
  type: auxiliary-prompt
  status: active
  optimization: atomic_context
---

# HOP-COM-PROMPT-005 Auxiliary Prompt

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: HOP-COM-PROMPT-005
name: Close module
intent: Validate and close a completed module before moving to the next dependency.
input:
- selected_module_folder
- ../../PROJECT_STATE.md
- ../../SOURCE_OF_TRUTH.md
- ../../08-qa
expected_output:
- closeout_evidence_yaml
- closeout_evidence_md
- updated_project_state
- updated_source_of_truth
- next_module_recommendation
prompt: 'Close the selected HOP module.

  Validate all backlog items for the module, confirm all required tests and QA evidence are present, confirm traceability
  is complete, and update project state and source registries.

  Re-run or verify non-limited passed evidence for all required backend, frontend, mobile,

  contract, dependency, coverage, build and security gates, including debt-first execution,

  best-practice/standards, duplicate-code, complexity, OWASP/secure-code and message

  externalization/i18n reviews. Do not close the module or recommend

  the next module while any required gate is not_executed, passed_with_execution_limitation,

  closed_with_execution_limitation, blocked_by_missing_toolchain, blocked_by_network or

  blocked_by_unsupported_runtime.

  Confirm coverage did not decrease below the previous measured baseline for any applicable stack.

  Final HOP product closure is not allowed unless every applicable stack reaches at least 80

  percent line coverage and every technical-debt item is closed.

  Recommend the next module only after closeout evidence is written and all mandatory executable

  gates have passed or are explicitly not applicable.

  '
```
