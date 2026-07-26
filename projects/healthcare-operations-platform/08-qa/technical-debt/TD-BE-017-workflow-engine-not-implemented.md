---
id: TD-BE-017
format: markdown_structured_payload
type: technical-debt-item
name: BCM-PLT-009 Workflow Engine (listWorkflowExecutions/triggerWorkflow/rollbackWorkflow)
  not implemented
version: 1.0.0
status: materially_reduced
---

# Bcm Plt 009 Workflow Engine (Listworkflowexecutions/Triggerworkflow/Rollbackworkflow) Not Implemented

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: TD-BE-017
  type: technical-debt-item
  name: BCM-PLT-009 Workflow Engine (listWorkflowExecutions/triggerWorkflow/rollbackWorkflow)
    not implemented
  version: 1.0.0
  status: materially_reduced
  created_date: 2026-07-23
source:
  discovered_during_backlog_item: COM-MOD-012-BE-001
  module: COM-MOD-012 Platform Hardening and SaaS Operations
  evidence: 08-qa/qa/platform-hardening-and-saas-operations/COM-MOD-012-BE-001-validation.md
classification:
  category: backend_missing_capability
  affected_area: operational_workflow_orchestration
  affected_components: []
  risk_level: medium
  urgency: low
  blocking: false
  reason_non_blocking: No module or runbook currently calls a workflow-engine API
    to function; backup-runbook.md, restore-runbook.md, incident-response-runbook.md
    and rollback-incident-handoff-runbook.md all operate today via manual/scripted
    procedures that remain fully executable without this capability.
current_state:
  issue: bcm-plt-009-workflow-engine/openapi-source.md models listWorkflowExecutions,
    triggerWorkflow and rollbackWorkflow, intended (per the OPS-002 runbooks' known_gaps_and_forward_pointers)
    to back automated backup scheduling, restore rehearsal and rollback tracking.
    No backend module exists for this yet. COM-MOD-012-BE-001 deliberately did not
    build a workflow-engine shell module with no real executions to orchestrate, since
    that would itself be the "CRUD manual repetitivo" the backlog rules forbid — the
    value of a workflow engine is entirely in what it orchestrates, not in the CRUD
    surface around a WorkflowExecution record.
target_state:
  preferred_remediation: Build the workflow-engine module once there is a first real
    orchestration target to wire it to (e.g. a scheduled backup job per backup-runbook.md's
    own open gap, or restore-rehearsal automation per restore-runbook.md). Model
    WorkflowDefinition/WorkflowExecution as domain types, triggerWorkflow as the entry
    point for that first real job, and listWorkflowExecutions/ rollbackWorkflow as
    genuine operational controls over it rather than placeholder CRUD.
  quality_goal: Do not compile a capability's CRUD surface ahead of having a first
    real business process for it to orchestrate; register it as debt and implement
    it together with that process instead.
remediation:
  strategy: materially_reduced_by_COM_MOD_015_BE_001_then_gradual_dedicated_backlog_item_for_generic_workflow_engine
  owner: backend_team
  estimated_effort: large
  estimated_cost_impact: medium
  target_backlog: a_future_COM_MOD_012_or_dedicated_operations_automation_backlog_item
  dependencies_or_prerequisites:
  - A decided first orchestration target (automated backup scheduling is the leading
    candidate per backup-runbook.md's own known_gaps_and_forward_pointers).
  acceptance_criteria:
  - triggerWorkflow starts a real, observable business process (not a no-op record
    insert).
  - listWorkflowExecutions and rollbackWorkflow operate against that real process's
    execution state.
progress:
- date: 2026-07-26
  backlog_item: COM-MOD-015-BE-001
  result: materially_reduced
  note: AI assistant orchestration introduced a real provider-neutral process target with persisted state, human review and audit records. The generic BCM-PLT-009 workflow-engine API remains open for broader operational automation.
- date: 2026-07-26
  backlog_item: COM-MOD-015-BE-002
  result: status_field_synced
  note: This item's own status field was left at "open" by COM-MOD-015-BE-001 even
    though remediation.strategy and the prior progress entry already recorded
    materially_reduced; corrected the artifact.status and top-level status fields
    (here and in technical-debt-index.md) to materially_reduced so the record is
    internally consistent. No further functional reduction in this backlog item;
    the generic BCM-PLT-009 workflow-engine API remains open for a future dedicated
    operations-automation backlog item.
```
