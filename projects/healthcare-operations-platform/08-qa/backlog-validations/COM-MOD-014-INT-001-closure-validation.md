---
id: COM-MOD-014-INT-001-closure-validation
type: backlog-closure-validation
status: incomplete
model: qwen2.5-coder:0.5b
---

# COM-MOD-014-INT-001 Closure Validation

Status: `incomplete`

Hard findings: `1`

Ollama summary: The security evidence must be status validated and match the backlog item.

## Structured Payload

```yaml
artifact:
  id: COM-MOD-014-INT-001-closure-validation
  type: backlog-closure-validation
  status: incomplete
  model: qwen2.5-coder:0.5b
context:
  task_id: COM-MOD-014-INT-001
  project: projects/healthcare-operations-platform
  prompt_path: projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt/COM-MOD-014-INT-001-prompt.md
  qa_evidence_exists: true
  qa_status: validated
  security_evidence_exists: true
  security_status: null
  handoff_exists: true
  project_state_active_backlog_item: COM-MOD-014-FE-001
  project_state_next_backlog_item: null
  product_backlog_current_baseline_active: COM-MOD-014-FE-001
  product_backlog_item_status: closed
  execution_prompt_previous_backlog_item: COM-MOD-014-INT-001
  execution_prompt_previous_status: closed
  source_of_truth_checked: true
  git_head: 6790f6d
  git_clean: true
  hard_findings:
  - id: security_evidence_not_validated
    severity: P0
    detail: Security evidence must be status validated and match backlog_item.
ollama_review:
  summary: The security evidence must be status validated and match the backlog item.
  top_risks: []
  required_actions: []
```
