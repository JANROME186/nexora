---
id: COM-MOD-014-BE-001-closure-validation
type: backlog-closure-validation
status: incomplete
model: qwen2.5-coder:0.5b
---

# COM-MOD-014-BE-001 Closure Validation

Status: `incomplete`

Hard findings: `1`

Ollama summary: The project state for the task 'PROJECT_STATE commercial_product_delivery still points to the closed task.' has been updated to 'closed'.

## Structured Payload

```yaml
artifact:
  id: COM-MOD-014-BE-001-closure-validation
  type: backlog-closure-validation
  status: incomplete
  model: qwen2.5-coder:0.5b
context:
  task_id: COM-MOD-014-BE-001
  project: projects/healthcare-operations-platform
  prompt_path: projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt/COM-MOD-014-BE-001-prompt.md
  qa_evidence_exists: true
  qa_status: validated
  security_evidence_exists: true
  security_status: validated
  handoff_exists: true
  project_state_active_backlog_item: COM-MOD-014-BE-001
  project_state_next_backlog_item: null
  product_backlog_current_baseline_active: COM-MOD-014-INT-001
  product_backlog_item_status: closed
  execution_prompt_previous_backlog_item: COM-MOD-014-BE-001
  execution_prompt_previous_status: closed
  source_of_truth_checked: true
  git_head: d6b7c28
  git_clean: true
  hard_findings:
  - id: project_state_stale_active_item
    severity: P0
    detail: PROJECT_STATE commercial_product_delivery still points to the closed task.
ollama_review:
  summary: The project state for the task 'PROJECT_STATE commercial_product_delivery
    still points to the closed task.' has been updated to 'closed'.
  top_risks: []
  required_actions: []
```
