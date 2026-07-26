# Nexora Context Orchestrator Tool Registry

This registry defines compact local tool references used by generated prompts. Prompts should
reference the tool id instead of repeating full commands or task-specific paths.

Runtime configuration:

- All framework Python programs must be versioned under `nexora-framework/`.
- Workstation paths, secrets, provider login state, local quota state and caches must use
  environment variables or ignored local paths.
- Configuration runbook:
  `nexora-framework/08-engineering/agents/context-orchestrator/runtime-configuration-runbook.md`

## Tool: commercial_agent_router

Purpose: route one optimized active backlog prompt through the most efficient local or
subscription-backed runtime without keeping long commercial chat sessions alive or using API-key
token billing by default.

Tool id: `commercial_agent_router`

Runtime: Python

Script: `nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py`

Default active prompt folder:
`projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt/`

Local quota state:
`.nexora/runtime/quota_tracker.json`

Invocation template:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py
```

Default behavior:

- Read the only prompt file present in `active_prompt/`.
- Infer task id and complexity from the compact prompt.
- Respect the prompt `EXECUTION_FLOW`:
  - `manual` means operator/IDE handoff is preferred and no headless CLI execution is required.
  - `cli` means a subscription-backed local CLI may be used when enabled and available.
- Select the best enabled local/subscription provider by complexity, quota window and block state.
- Run in dry-run mode unless `--execute` is explicitly provided.
- Use Ollama local as the mandatory fallback provider.
- Persist quota/rate-limit state locally outside git.

Execution template:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py --execute
```

Supported runtimes:

- `ollama_local` through the local Ollama HTTP API.
- `filesystem_task_ingestion` through ignored local files `.agent_next_task.md` and
  `.agent_task_summary.md`.
- `claude_code_cli` through the local Claude Code CLI subprocess and the operator's local login.
- `codex_cli` through local `codex exec` and the operator's ChatGPT/Codex CLI login.
- `gemini_cli` through local `gemini -p` when Google OAuth or enterprise Code Assist eligibility is valid.
- `kiro_ide_cli` through local `kiro chat` as an IDE handoff route; it does not provide
  deterministic headless output and must not be used as an automatic closure validator.
- `github_copilot_cli` through local `gh`/Copilot CLI configuration and the operator's subscription.

API-key SDK providers are not part of the default Nexora routing path. A paid API-key route requires
an ADR exception and must not be introduced by a product execution agent.

When a CLI provider is blocked by missing login, missing binary, quota, permissions, unsupported
headless execution or sandbox constraints, the operator must regenerate the active prompt with
`context_orchestrator.py --execution-flow manual` and hand it to the IDE agent. This keeps the
commercial-token payload optimized by Ollama while avoiding false backlog closures caused by
execution-surface limits.

Execution agents must not spawn commercial subagents for file exploration, broad search, formatting
or QA evidence generation. Those tasks must use local shell/Python/Ollama first. Commercial routing
is reserved for focused implementation, architecture or review work with a compact active prompt.

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
