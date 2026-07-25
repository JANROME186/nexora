---
id: COM-MOD-014-BE-001-closure-validation
type: backlog-closure-validation
status: incomplete
model: qwen2.5-coder:0.5b
---

# COM-MOD-014-BE-001 Closure Validation

Status: `incomplete`

Hard findings: `8`

Ollama summary: El producto está en estado de alta calidad y está en la lista de tareas pendientes.

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
  qa_status: closed
  security_evidence_exists: true
  security_status: closed
  handoff_exists: true
  project_state_active_backlog_item: COM-MOD-014-BE-001
  project_state_next_backlog_item: null
  product_backlog_current_baseline_active: COM-MOD-014-INT-001
  product_backlog_item_status: null
  execution_prompt_previous_backlog_item: COM-MOD-014-DEF
  execution_prompt_previous_status: closed
  source_of_truth_checked: true
  git_head: f8dc63a
  git_clean: true
  hard_findings:
  - id: qa_evidence_not_validated
    severity: P0
    detail: QA evidence must be status validated and match backlog_item.
  - id: security_evidence_not_validated
    severity: P0
    detail: Security evidence must be status validated and match backlog_item.
  - id: product_backlog_item_not_closed
    severity: P0
    detail: Expected closed, found None.
  - id: project_state_stale_active_item
    severity: P0
    detail: PROJECT_STATE commercial_product_delivery still points to the closed task.
  - id: execution_prompt_previous_not_closed
    severity: P0
    detail: Execution prompt must carry the validated task as previous_backlog_item
      closed.
  - id: source_of_truth_missing_reference
    severity: P1
    detail: 08-qa/qa/imaging-operations/COM-MOD-014-BE-001-validation.md
  - id: source_of_truth_missing_reference
    severity: P1
    detail: 08-qa/security-quality/COM-MOD-014-BE-001/security-quality-evidence.md
  - id: source_of_truth_missing_reference
    severity: P1
    detail: 08-qa/handoffs/COM-MOD-014-BE-001-summary.md
ollama_review:
  summary: El producto está en estado de alta calidad y está en la lista de tareas
    pendientes.
  top_risks: []
  required_actions: []
```
