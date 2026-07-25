# Nexora Context Orchestrator Tool Registry

This registry defines compact local tool references used by generated prompts. Prompts should
reference the tool id and task-specific parameters instead of repeating full commands.

## Tool: backlog_closure_validator

Purpose: validate whether a generated backlog prompt was fully and correctly closed after the
execution agent commits the backlog work.

Tool id: `backlog_closure_validator`

Runtime: Python

Script: `nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py`

Default project prompt pattern:
`projects/healthcare-operations-platform/08-qa/generated-prompts/<TASK_ID>-prompt.md`

Invocation template:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py `
  --root <REPOSITORY_ROOT> `
  --task-id <TASK_ID> `
  --prompt projects/healthcare-operations-platform/08-qa/generated-prompts/<TASK_ID>-prompt.md
```

Required inputs:

- `root`: repository root.
- `task_id`: backlog item being validated.
- `prompt`: generated backlog prompt for the same task id.

Successful closure requires:

- Exit code `0`.
- Closure report status `closed`.
- `Hard findings: 0`.
- Validation evidence at
  `projects/healthcare-operations-platform/08-qa/backlog-validations/<TASK_ID>-closure-validation.md`.
- No closure-fix prompt generated for the same task.
- Clean `git status --short` after validation evidence is committed when applicable.

If this tool reports inconsistencies, stale pointers, missing evidence, dirty worktree, or writes
`<TASK_ID>-closure-fix-prompt.md`, the backlog is not closed. The execution agent must report the
findings, correct them, commit the correction, and run this tool again in strict mode.
