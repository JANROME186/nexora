---
artifact:
  id: HOP-PROMPT-HOP-COM-PROMPT-001
  type: auxiliary-prompt
  status: active
  optimization: atomic_context
---

# HOP-COM-PROMPT-001 Auxiliary Prompt

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
id: HOP-COM-PROMPT-001
name: Select next executable backlog item
intent: Determine the next backlog item to execute using project state and commercial backlog dependency order.
input:
- ../../PROJECT_STATE.md
- HOP_COMMERCIAL_PRODUCT_BACKLOG.md
expected_output:
- selected_module_id
- selected_backlog_item_id
- dependency_status
- blocking_gaps
- execution_plan
prompt: 'Load the HOP project state and HOP commercial product backlog.

  Prefer the latest compact handoff under ../../08-qa/handoffs/ when it exists.

  Use targeted rg/read operations for only the active backlog lines; do not preload whole registries into the commercial prompt.

  Select the next executable backlog item by dependency order.

  If any dependency is incomplete, stop and report the exact blocker.

  If the selected item changes code, load 08-qa/technical-debt/technical-debt-index.md and include the required debt-first
  action in the execution plan.

  If the selected item is a definition item, prepare the capability package modeling plan.

  If the selected item is an implementation item, verify that the definition package exists first.

  '
```
