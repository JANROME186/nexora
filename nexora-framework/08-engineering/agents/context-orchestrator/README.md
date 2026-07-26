# Context Orchestrator

This Python helper generates a compact backlog prompt using lazy loading and Ollama-first local orchestration.

Default usage generates the preferred manual/IDE handoff prompt:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
```

By default, the script infers the active HOP backlog, validates the required Ollama model and writes
a deterministic prompt file to:

`projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt/<TASK_ID>-prompt.md`

The `active_prompt/` folder is the execution inbox and must contain exactly one operative prompt.
When a new prompt is generated, older active prompts are moved to `history_prompt/`.

Example:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --task-id COM-MOD-017-BE-001 `
  --title "Compile marketplace backend outputs" `
  --summary-ref projects/healthcare-operations-platform/08-qa/handoffs/COM-MOD-017-DEF-summary.md
```

Ollama is required. The bootstrap default model is `qwen2.5-coder:0.5b`; install it with:

```powershell
ollama pull qwen2.5-coder:0.5b
```

Larger approved models such as `qwen2.5-coder:3b` or `qwen2.5-coder:7b` may be selected with
`--ollama-model` when the local workstation has them installed.

The final prompt is rendered from canonical fields and cached by context hash. This keeps repeated
executions stable while still using Ollama as the primary local orchestration source.

The output must remain agent agnostic. It should point to files and commands instead of pasting complete artifacts into the commercial prompt.

## Execution Flows

The orchestrator supports two explicit flows. The default is `manual` because it avoids depending
on a headless CLI and lets the operator paste the optimized prompt into IDE agents such as
Antigravity, Kiro or any other subscription-backed IDE.

Manual flow:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py --execution-flow manual --refresh
```

CLI flow:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py --execution-flow cli --refresh
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py --execute
```

If a CLI route is blocked by login, quota, sandbox, missing binary or permissions, the operator must
switch to manual flow and give the generated prompt to the IDE agent. The backlog must not be
declared closed because of a resolvable CLI limitation.

The selected flow is part of the canonical context hash, so repeated executions with the same flow
produce stable output and manual/CLI prompts do not overwrite each other's cache state.

## Runtime Configuration

All Python programs used by this orchestrator are versioned in this repository. Local runtime state,
provider login state, quotas and workstation-specific paths must be configured through environment
variables or ignored paths, never by editing committed source files.

Configuration runbook:

`nexora-framework/08-engineering/agents/context-orchestrator/runtime-configuration-runbook.md`

Environment template:

`nexora-framework/08-engineering/agents/context-orchestrator/.env.example`

Supported local-only paths are `.nexora/runtime/`, `.nexora/cache/`, `.nexora/secrets/` and local
`.env` files. They are intentionally excluded from git.

## Subscription-First Agent Runtime Routing

Nexora execution is centralized through Python to avoid long interactive commercial chats, large
context windows and duplicate token-consumption billing. The standard route is local/subscription
first:

1. Generate the active compact prompt with `context_orchestrator.py`.
2. Choose the execution flow explicitly:
   - `--execution-flow manual` for operator handoff to IDE agents.
   - `--execution-flow cli` for enabled subscription-backed CLI execution.
3. Route the prompt with `tool: commercial_agent_router` only when CLI/task-ingestion execution is intentional.
4. Execute focused work with local Ollama, a subscription-backed CLI or filesystem task ingestion.
5. Write the handoff summary, commit, validate closure and exit the session.

Framework standard:

`nexora-framework/02-standards/standards/subscription-first-agent-orchestration-standard.md`

Tool reference:

```text
tool: commercial_agent_router
```

The tool registry owns the invocation template:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py
```

By default the router runs in dry-run mode and selects a provider without invoking an external
runtime. Use `--execute` only when the operator intentionally wants Python to execute the selected
local/subscription route. The mandatory fallback is local Ollama. Optional routes are local CLI
sessions, such as Claude Code CLI, Codex CLI, Gemini CLI, Kiro IDE CLI or GitHub Copilot CLI, and filesystem task ingestion for IDE
agents.

The local quota tracker is written to `.nexora/runtime/quota_tracker.json`, which is ignored by git.
It stores rate-limit pauses and recent routing events. It must never contain API keys, web tokens or
provider account data; authentication belongs to local CLI/editor login outside the repository.

Execution agents must not create commercial subagents for broad file reading, project exploration,
formatting, QA evidence drafting or stale-pointer sweeps. Those tasks must use local shell, Python,
ripgrep, deterministic scripts and Ollama first. Subscription-backed CLI/IDE execution should be
stateless, focused, short-lived and based on the active optimized prompt only.

## Backlog Closure Validation

Every generated backlog prompt includes a mandatory post-commit closure validation rule. After an
execution agent finishes the implementation, evidence and registry synchronization, the agent must
commit the completed work first and then run `tool: backlog_closure_validator` from
`tool-registry.md`. The tool reads the only prompt present in `active_prompt/`; no task id or prompt
path parameters are required in the normal flow.

Tool reference used in compact prompts:

```text
tool: backlog_closure_validator
```

The tool registry owns the full invocation template:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
```

The validator uses deterministic repository checks first and Ollama as the mandatory local
summarizer. Ollama does not decide closure; P0/P1 deterministic findings are authoritative. If the
backlog is incomplete, it writes a compact correction prompt to:

`projects/healthcare-operations-platform/08-qa/generated-prompts/<TASK_ID>-closure-fix-prompt.md`

Closure is valid only when the strict validator exits with code `0`, the report status is `closed`,
hard findings are `0`, the closure validation evidence exists under
`projects/healthcare-operations-platform/08-qa/backlog-validations/`, the active prompt is moved to
`history_prompt/`, the validation evidence is committed automatically, and `git status --short` is
clean. If the validator reports stale pointers, missing evidence, dirty worktree, or generates a
closure-fix prompt, the agent must report the inconsistencies, correct them, commit the corrections
and run the strict validator again.

Execution agents must not modify `backlog_validator.py` or `tool-registry.md` while closing product
backlog work. Those files are protected controls; changing them during closure is treated as a P0
finding. The agent may attempt closure at most 3 times. If the validator still reports P0/P1
findings after the third attempt, the agent must stop, leave the findings visible, and report the
remaining gaps plus the technical explanation for why the item appears closeable.

## Frontmatter Migration

Heavy YAML/Markdown artifacts are migrated with the local frontmatter migrator. Inventory and
structured conversion use Python/PyYAML only; Ollama is used only for narrative files when
`--use-ollama` is explicitly enabled.

Install the local Python requirements:

```powershell
python -m pip install -r nexora-framework/08-engineering/agents/context-orchestrator/requirements.txt
```

Pilot inventory:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/frontmatter_migrator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --scope projects/healthcare-operations-platform `
  --limit 20
```

Write pilot conversions without archiving source YAML:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/frontmatter_migrator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --scope projects/healthcare-operations-platform/08-qa/qa `
  --limit 20 `
  --apply
```

When a target Markdown file already exists, rerun with `--combine-existing` after review so the
converted YAML and existing Markdown are preserved in a single optimized artifact.
