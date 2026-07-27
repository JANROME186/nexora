---
id: COM-MOD-015-CLOSEOUT-closure-validation
type: backlog-closure-validation
status: incomplete
model: qwen2.5-coder:0.5b
---

# COM-MOD-015-CLOSEOUT Closure Validation

Status: `incomplete`

Hard findings: `4`

Ollama summary: The task is not closed, but the next one is still active.

## Structured Payload

```yaml
artifact:
  id: COM-MOD-015-CLOSEOUT-closure-validation
  type: backlog-closure-validation
  status: incomplete
  model: qwen2.5-coder:0.5b
context:
  task_id: COM-MOD-015-CLOSEOUT
  project: projects/healthcare-operations-platform
  prompt_path: projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt/COM-MOD-015-CLOSEOUT-prompt.md
  qa_evidence_exists: true
  qa_status: validated
  security_evidence_exists: true
  security_status: validated
  handoff_exists: true
  project_state_active_backlog_item: COM-MOD-015-CLOSEOUT
  project_state_next_backlog_item: null
  product_backlog_current_baseline_active: COM-MOD-015-CLOSEOUT
  product_backlog_item_status: next
  execution_prompt_previous_backlog_item: COM-MOD-015-QA-001
  execution_prompt_previous_status: closed
  protected_validator_paths:
  - nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
  - nexora-framework/08-engineering/agents/context-orchestrator/tool-registry.md
  protected_validator_changes: ''
  source_of_truth_checked: true
  git_head: b594fe5
  git_clean: true
  hard_findings:
  - id: product_backlog_item_not_closed
    severity: P0
    detail: Expected closed, found next.
  - id: product_baseline_stale_active_item
    severity: P0
    detail: HOP commercial product backlog baseline still points to the closed task.
  - id: project_state_stale_active_item
    severity: P0
    detail: PROJECT_STATE commercial_product_delivery still points to the closed task.
  - id: execution_prompt_previous_not_closed
    severity: P0
    detail: Execution prompt must carry the validated task as previous_backlog_item
      closed.
ollama_review:
  summary: The task is not closed, but the next one is still active.
  top_risks: []
  required_actions: []
```
