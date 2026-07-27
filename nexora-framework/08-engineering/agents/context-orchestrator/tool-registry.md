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

Local orchestration trace:
`.nexora/runtime/orchestrator-events.jsonl`

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
- Select the best enabled local/subscription provider by complexity, confirmed quota-limit state
  and rotation policy.
- If `--provider` or `--agent` is supplied, use that provider/agent as an explicit operator
  override. This bypasses automatic selection and rotation balancing for the current run.
- Run in dry-run mode unless `--execute` is explicitly provided.
- Use Ollama local as the mandatory fallback provider.
- Persist quota/rate-limit state locally outside git.
- Append JSONL trace events to `.nexora/runtime/orchestrator-events.jsonl`.
- Emit console heartbeat events every 30 seconds while a CLI provider is still running.
- For first observed automation runs, allow generous provider timeouts and use heartbeat telemetry
  (`elapsed_seconds`, `remaining_timeout_seconds`, `stdout_bytes`, `stderr_bytes`) to diagnose
  whether a process is active, silent or waiting.
- Refuse headless CLI execution unless `tool: agent_cli_preflight` produced a fresh ready
  certificate for the selected provider.
- Balance provider consumption by penalizing recent successful providers and selecting another
  enabled/configured commercial CLI or IDE handoff route when available.
- After a successful headless CLI provider execution, run automatic post-provider closure:
  diagnostic validation, project commit when diagnostic findings are clean, strict closure
  validation, prompt archival and closure evidence commit.
- When automatic closure cannot complete after the configured attempts, write operator feedback to
  `.nexora/runtime/orchestrator-closure-feedback.md` and exit non-zero.

Execution template:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_cli_preflight.py --provider all
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py --execute
```

Explicit provider/agent override:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_cli_preflight.py --provider claude_code_cli
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py --execute --agent claude_code_cli
```

When an explicit provider/agent is requested, the router must not rebalance to another provider for
that run. If the requested provider is missing, disabled, not configured, preflight-not-ready,
blocked by quota or returns a rate-limit/quota response, the run fails and reports that provider's
reason. Without `--provider`/`--agent`, the router uses automatic selection and balance policy.

Optional closure controls:

```powershell
$env:NEXORA_ORCHESTRATOR_CLOSURE_ATTEMPTS="3"
$env:NEXORA_ORCHESTRATOR_CLOSURE_FEEDBACK_FILE=".nexora/runtime/orchestrator-closure-feedback.md"
$env:NEXORA_PROVIDER_ROTATION_RECENT_SUCCESSES="1"
$env:NEXORA_PROVIDER_ROTATION_PENALTY="50"
```

Use `--skip-closure` only for diagnostic framework development. Product backlog CLI execution must
leave either a closed backlog or a closure feedback report explaining the remaining blockers.

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

Only provider responses that explicitly report plan quota/rate limits, such as HTTP 429 or
quota-exceeded messages, may persist a provider as blocked in local runtime state. Missing login,
missing binary, permissions, unsupported headless execution, silent process failures or sandbox
constraints are treated as unavailable for the current run only. When those non-quota issues occur,
the operator may fix the environment, rerun preflight/router, or regenerate the active prompt with
`context_orchestrator.py --execution-flow manual` and hand it to the IDE agent.

Execution agents must not spawn commercial subagents for file exploration, broad search, formatting
or QA evidence generation. Those tasks must use local shell/Python/Ollama first. Commercial routing
is reserved for focused implementation, architecture or review work with a compact active prompt.

## Tool: agent_cli_preflight

Purpose: certify that local/subscription CLI providers are installed, authenticated and able to
answer a tiny headless smoke prompt before the router starts backlog execution.

Tool id: `agent_cli_preflight`

Runtime: Python

Script: `nexora-framework/08-engineering/agents/context-orchestrator/agent_cli_preflight.py`

Certificate:
`.nexora/runtime/agent-cli-preflight.json`

Invocation template:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_cli_preflight.py --provider all
```

Recommended first-run timing:

```powershell
$env:NEXORA_PREFLIGHT_TIMEOUT_SECONDS="300"
$env:NEXORA_PROVIDER_TIMEOUT_SECONDS="14400"
$env:NEXORA_PROVIDER_HEARTBEAT_SECONDS="30"
```

Provider-specific invocation:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_cli_preflight.py --provider codex_cli
```

Default behavior:

- Resolve each CLI binary from PATH.
- Run a version check.
- Run an auth check when the CLI exposes one.
- Run a tiny prompt smoke test for headless providers.
- Mark IDE-only routes, such as Kiro handoff, as not certified for automatic headless execution.
- Write a local ignored certificate for the router.
- Append trace events to `.nexora/runtime/orchestrator-events.jsonl`.

The router must not execute a headless CLI provider when the certificate is missing, stale or marks
that provider as not ready. Operator actions reported by preflight must be resolved before backlog
execution begins.

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

## Tool: framework_managed_artifact_optimizer

Purpose: compact framework-managed HOP tracking, backlog and prompt files into small root indexes
plus atomic records that are loaded on demand.

Tool id: `framework_managed_artifact_optimizer`

Runtime: Python

Script: `nexora-framework/08-engineering/agents/context-orchestrator/framework_managed_artifact_optimizer.py`

Invocation template:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/framework_managed_artifact_optimizer.py
```

First migration or recovery from the current committed large baseline:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/framework_managed_artifact_optimizer.py --seed-from-git-head
```

Managed outputs:

- compact `PROJECT_STATE.md`;
- compact `SOURCE_OF_TRUTH.md`;
- compact `06-delivery/commercial-product/HOP_COMMERCIAL_PRODUCT_BACKLOG.md`;
- compact `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.md`;
- atomic backlog records under `06-delivery/commercial-product/backlog-map/`;
- atomic prompt records under `06-delivery/commercial-product/prompt-library/`;
- progress/source ledgers under `08-qa/project-tracking/`.

Agents must not paste ledger contents back into root files. Root files must stay as indexes.

## Tool: orchestrator_execution_log

Purpose: follow the local orchestrator/router execution trace without waiting blindly for a final
CLI response.

Tool id: `orchestrator_execution_log`

Runtime: PowerShell

Invocation template:

```powershell
Get-Content .nexora/runtime/orchestrator-events.jsonl -Wait -Tail 30
```

The log is local runtime state and must not be committed.
