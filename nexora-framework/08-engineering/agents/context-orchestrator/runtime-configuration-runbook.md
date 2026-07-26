---
artifact:
  id: NXF-RUN-CONTEXT-ORCHESTRATOR-RUNTIME
  type: framework-runbook
  status: active
  owner: Nexora Engineering
---

# Nexora Framework Runtime Configuration Runbook

All Python programs required by Nexora Framework execution must live inside the repository under
`nexora-framework/`. Operators must not keep private framework scripts outside the repo because
agents need a stable, auditable and agent-agnostic execution surface.

Runtime values that depend on the workstation, user, provider account or deployment environment must
be configured outside versioned source files.

## Versioned Python Programs

Current Python tools:

- `nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py`
- `nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py`
- `nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py`
- `nexora-framework/08-engineering/agents/context-orchestrator/frontmatter_migrator.py`
- `nexora-framework/08-engineering/agents/context-orchestrator/zero_yaml_migrator.py`
- `nexora-framework/08-engineering/agents/context-orchestrator/framework_managed_artifact_optimizer.py`

New framework automation must be added under `nexora-framework/` and committed. If a script needs
local-only inputs, read them from environment variables, provider CLI configuration or ignored
runtime paths.

## Ignored Local Paths

The following paths are reserved for local runtime state and must not be committed:

- `.nexora/runtime/`
- `.nexora/cache/`
- `.nexora/secrets/`
- `.env`
- `.env.*`

Allowed templates:

- `.env.example`
- `**/.env.example`

Do not store API keys, tokens, provider account information, private paths with credentials,
browser/session data or generated quota state in committed files.

## Required Base Stack

- Python 3.11 or newer.
- Ollama local service.
- Approved open source Ollama model, default `qwen2.5-coder:0.5b`.
- `git`.
- `ripgrep`.

Install framework Python dependencies:

```powershell
python -m pip install -r nexora-framework/08-engineering/agents/context-orchestrator/requirements.txt
```

Optional execution adapters must use local CLI/editor subscription sessions, not API-key token
billing. If a paid API-key provider is ever required, it needs an ADR exception and must remain
outside normal backlog execution.

## Environment Variables

Required or recommended Nexora variables:

| Variable | Purpose |
| --- | --- |
| `NEXORA_ROOT` | Absolute repository root. |
| `NEXORA_PROJECT_PATH` | Project folder under `projects/`. |
| `NEXORA_ACTIVE_PROMPT_DIR` | Active prompt inbox used by router and validator. |
| `NEXORA_QUOTA_TRACKER` | Local ignored quota tracker path. |
| `NEXORA_ORCHESTRATOR_LOG` | Local ignored JSONL execution trace for prompt generation and routing. |
| `NEXORA_PROVIDER_TIMEOUT_SECONDS` | Max seconds before the router kills a provider process tree. |
| `NEXORA_PROVIDER_HEARTBEAT_SECONDS` | Seconds between provider heartbeat trace events. |
| `NEXORA_OLLAMA_MODEL` | Default local Ollama model. |
| `NEXORA_EXECUTION_FLOW` | Default prompt execution flow: `manual` or `cli`. |
| `NEXORA_AGENT_TASK_FILE` | Ignored task ingestion file written for local IDE agents. |
| `NEXORA_AGENT_RESULT_FILE` | Ignored summary file expected from local IDE agents. |

Template file:

`nexora-framework/08-engineering/agents/context-orchestrator/.env.example`

## Windows PowerShell Setup

Temporary session variables:

```powershell
$env:NEXORA_ROOT="C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora"
$env:NEXORA_PROJECT_PATH="projects/healthcare-operations-platform"
$env:NEXORA_ACTIVE_PROMPT_DIR="projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt"
$env:NEXORA_QUOTA_TRACKER=".nexora/runtime/quota_tracker.json"
$env:NEXORA_ORCHESTRATOR_LOG=".nexora/runtime/orchestrator-events.jsonl"
$env:NEXORA_PROVIDER_TIMEOUT_SECONDS="600"
$env:NEXORA_PROVIDER_HEARTBEAT_SECONDS="30"
$env:NEXORA_OLLAMA_MODEL="qwen2.5-coder:0.5b"
$env:NEXORA_EXECUTION_FLOW="manual"
$env:NEXORA_AGENT_TASK_FILE=".agent_next_task.md"
$env:NEXORA_AGENT_RESULT_FILE=".agent_task_summary.md"
```

Optional subscription-backed CLI login checks:

```powershell
claude --version
codex --version
codex login status
gemini --version
kiro --version
gh auth status
gh extension list
```

Persistent user variables, when desired:

```powershell
[Environment]::SetEnvironmentVariable("NEXORA_ROOT", "C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora", "User")
[Environment]::SetEnvironmentVariable("NEXORA_OLLAMA_MODEL", "qwen2.5-coder:0.5b", "User")
```

## Linux/macOS Setup

Temporary shell variables:

```bash
export NEXORA_ROOT="/path/to/nexora"
export NEXORA_PROJECT_PATH="projects/healthcare-operations-platform"
export NEXORA_ACTIVE_PROMPT_DIR="projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt"
export NEXORA_QUOTA_TRACKER=".nexora/runtime/quota_tracker.json"
export NEXORA_ORCHESTRATOR_LOG=".nexora/runtime/orchestrator-events.jsonl"
export NEXORA_PROVIDER_TIMEOUT_SECONDS="600"
export NEXORA_PROVIDER_HEARTBEAT_SECONDS="30"
export NEXORA_OLLAMA_MODEL="qwen2.5-coder:0.5b"
export NEXORA_EXECUTION_FLOW="manual"
export NEXORA_AGENT_TASK_FILE=".agent_next_task.md"
export NEXORA_AGENT_RESULT_FILE=".agent_task_summary.md"
```

Optional subscription-backed CLI login checks:

```bash
claude --version
codex --version
codex login status
gemini --version
kiro --version
gh auth status
gh extension list
```

## Bootstrap Commands

Create ignored runtime state:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py --init-state
```

Generate the active optimized prompt:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py --execution-flow manual
```

The manual flow is the default and preferred route when the operator will paste the optimized prompt
into an IDE agent such as Antigravity, Kiro or another subscription-backed IDE. It keeps Ollama/Python
responsible for compression and lets the commercial IDE receive only the compact backlog prompt.

Generate a CLI-oriented prompt only when a local subscription CLI is enabled and the operator wants
the router to execute it:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py --execution-flow cli
```

Check routing without invoking external runtimes:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py
```

Follow orchestrator/router execution live in PowerShell:

```powershell
Get-Content .nexora/runtime/orchestrator-events.jsonl -Wait -Tail 30
```

The log is JSONL and local-only. It records prompt generation, cache hits, prompt writes, router
startup, prompt loading, provider selection, provider process start/end, heartbeat events during
long CLI execution, timeout, rate-limit and unavailable-provider events.

Write the active prompt into the local IDE task-ingestion file:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py --provider filesystem_task_ingestion --execute
```

Then ask the local IDE/tool agent, using its own subscription: `Atender tarea activa`. The agent must
read `.agent_next_task.md`, execute the backlog and write `.agent_task_summary.md`.

If a CLI route is blocked by login, quota, missing binary, permissions, unsupported headless
execution or sandbox constraints, regenerate the active prompt with `--execution-flow manual` and
handoff to the IDE. Do not close a backlog by exception when operator assistance can resolve the
execution path.

Run closure validation after the backlog work is committed:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
```

Compact framework-managed project tracking artifacts when root state/backlog/prompt files grow:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/framework_managed_artifact_optimizer.py
```

Use `--seed-from-git-head` only for the initial migration from a committed large baseline or to
recover atomic records from the last committed source.

## Operator Responsibilities

- Keep Python framework tools committed under `nexora-framework/`.
- Keep secrets, quota trackers and local state outside git.
- Configure provider access through local CLI/editor login. Do not use token-billed API keys in the
  normal Nexora execution path.
- Keep Ollama running before generating prompts or validating closures.
- Never modify framework validators to make a product backlog item pass.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: NXF-RUN-CONTEXT-ORCHESTRATOR-RUNTIME
  type: framework-runbook
  status: active
  owner: Nexora Engineering
runtime_policy:
  framework_python_programs_must_be_committed: true
  local_secrets_must_not_be_committed: true
  local_runtime_state_must_not_be_committed: true
  environment_variables_supported: true
  windows_linux_supported: true
ignored_paths:
- .nexora/runtime/
- .nexora/cache/
- .nexora/secrets/
- .env
- .env.*
allowed_templates:
- .env.example
- '**/.env.example'
environment_variables:
- NEXORA_ROOT
- NEXORA_PROJECT_PATH
- NEXORA_ACTIVE_PROMPT_DIR
- NEXORA_QUOTA_TRACKER
- NEXORA_ORCHESTRATOR_LOG
- NEXORA_PROVIDER_TIMEOUT_SECONDS
- NEXORA_PROVIDER_HEARTBEAT_SECONDS
- NEXORA_OLLAMA_MODEL
- NEXORA_EXECUTION_FLOW
- NEXORA_AGENT_TASK_FILE
- NEXORA_AGENT_RESULT_FILE
versioned_python_programs:
- nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
- nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py
- nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
- nexora-framework/08-engineering/agents/context-orchestrator/frontmatter_migrator.py
- nexora-framework/08-engineering/agents/context-orchestrator/zero_yaml_migrator.py
- nexora-framework/08-engineering/agents/context-orchestrator/framework_managed_artifact_optimizer.py
```
