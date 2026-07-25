# Nexora Context Orchestrator Tool Registry

This registry defines compact local tool references used by generated prompts. Prompts should
reference the tool id instead of repeating full commands or task-specific paths.

## Tool: backlog_closure_validator

Purpose: validate whether a generated backlog prompt was fully and correctly closed after the
execution agent commits the backlog work.

Tool id: `backlog_closure_validator`

Runtime: Python

Script: `nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py`

Default active prompt folder:
`projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt/`

Default history prompt folder:
`projects/healthcare-operations-platform/08-qa/generated-prompts/history_prompt/`

Invocation template:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
```

Default behavior:

- Resolve the repository root from the execution directory.
- Read the only prompt file present in `active_prompt/`.
- Infer `task_id` from the prompt header.
- Validate closure deterministically and summarize with local Ollama.
- If closure is correct, move the prompt from `active_prompt/` to `history_prompt/`.
- Commit closure evidence and the prompt move automatically.

Successful closure requires:

- Exit code `0`.
- Closure report status `closed`.
- `Hard findings: 0`.
- Validation evidence at
  `projects/healthcare-operations-platform/08-qa/backlog-validations/<TASK_ID>-closure-validation.md`.
- No closure-fix prompt generated for the same task.
- Clean `git status --short` after automatic validation commit.

If this tool reports inconsistencies, stale pointers, missing evidence, dirty worktree, or writes
`<TASK_ID>-closure-fix-prompt.md`, the backlog is not closed. The execution agent must report the
findings, correct them, commit the correction, and run this tool again in strict mode.

Protected control files:

- `nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py`
- `nexora-framework/08-engineering/agents/context-orchestrator/tool-registry.md`

Execution agents closing HOP/product backlog items must not modify protected control files. The
validator treats protected-file changes as a P0 finding because the execution agent must not be
judge and party in its own closure.

Closure retry policy:

- Try closure at most 3 times.
- Each retry must correct product code, evidence, registries, pointers, tests or documentation.
- A retry must not weaken or edit this tool definition or the closure validator.
- If the strict validator still fails after 3 attempts, stop and report the remaining findings,
  the corrections already made, and the technical rationale for why closure might be acceptable.
