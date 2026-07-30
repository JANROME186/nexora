---
id: HOP-HARD-INT-001-closure-validation
type: backlog-closure-validation
status: incomplete
model: qwen2.5-coder:0.5b
---

# HOP-HARD-INT-001 Closure Validation

Status: `incomplete`

Hard findings: `1`

Ollama summary: No riesgos o acciones soportados por hard_findings

## Structured Payload

```yaml
artifact:
  id: HOP-HARD-INT-001-closure-validation
  type: backlog-closure-validation
  status: incomplete
  model: qwen2.5-coder:0.5b
context:
  task_id: HOP-HARD-INT-001
  project: projects/healthcare-operations-platform
  prompt_path: projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt/HOP-HARD-INT-001-prompt.md
  qa_evidence_exists: true
  qa_status: validated
  security_evidence_exists: true
  security_status: validated
  handoff_exists: true
  project_state_active_backlog_item: HOP-HARD-QA-001
  project_state_next_backlog_item: null
  product_backlog_current_baseline_active: HOP-HARD-QA-001
  product_backlog_item_status: closed
  product_backlog_module_id: HOP-FINAL-HARDENING
  progress_ledger_active_backlog_item: HOP-HARD-QA-001
  progress_ledger_current_iteration: HOP-HARD-QA-001
  progress_ledger_module_closed: null
  progress_ledger_package_status: null
  execution_prompt_previous_backlog_item: HOP-HARD-INT-001
  execution_prompt_previous_status: closed
  protected_validator_paths:
  - nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
  - nexora-framework/08-engineering/agents/context-orchestrator/tool-registry.md
  protected_validator_changes: ''
  auto_repairs_applied:
  - id: root_project_state_synced
    path: PROJECT_STATE.md
  - id: active_backlog_item_status_synced
    path: projects/healthcare-operations-platform/06-delivery/commercial-product/backlog-map/items/HOP-HARD-QA-001.md
  source_of_truth_checked: true
  git_head: 17bbfe9
  git_clean: false
  hard_findings:
  - id: git_worktree_not_clean
    severity: P0
    detail: "M PROJECT_STATE.md\n M projects/healthcare-operations-platform/06-delivery/commercial-product/backlog-map/items/HOP-HARD-QA-001.md"
ollama_review:
  summary: No riesgos o acciones soportados por hard_findings
  top_risks: []
  required_actions: []
```
