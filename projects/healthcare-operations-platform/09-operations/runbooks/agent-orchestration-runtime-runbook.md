---
artifact:
  id: HOP-RUN-AGENT-ORCHESTRATION-RUNTIME
  type: operations-runbook
  status: active
  owner: Nexora Engineering
  project: healthcare-operations-platform
---

# HOP Agent Orchestration Runtime Runbook

HOP backlog execution must use the Nexora Python orchestrator as the control plane. The objective is
to avoid long commercial chats, large accumulated context and uncontrolled commercial subagents.

All framework Python programs are executed from the repository under `nexora-framework/`. HOP must
not depend on private local scripts outside git. Workstation-specific paths, provider credentials,
quota state and local caches must be configured through environment variables or ignored Nexora
runtime folders.

Framework setup reference:

`nexora-framework/08-engineering/agents/context-orchestrator/runtime-configuration-runbook.md`

## Required Flow

1. Generate or refresh the active prompt:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
```

2. Confirm routing decision without invoking a commercial provider:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py
```

3. Execute through the router only when the operator intentionally enables provider credentials or
CLI configuration:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py --execute
```

4. Complete the backlog item, create the handoff, commit, run closure validation and exit:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
```

## Session Rules

- One backlog item per commercial chat/session.
- Do not ask for the next backlog item in the same session.
- If execution exceeds 10 to 15 meaningful messages, compact the state into the required handoff and
  restart with a fresh optimized prompt.
- Do not launch commercial subagents for broad file inspection, formatting, pointer sweeps, QA
  evidence drafting or repetitive validation.
- Use local shell, `rg`, Python scripts and Ollama for mechanical work.
- Commercial providers should receive only the compact active prompt and focused follow-up context.

## Provider Routing

The local router manages provider selection and quota state in `.nexora/runtime/quota_tracker.json`.
That file is ignored by git and must not contain secrets.

Supported environment variables:

- `NEXORA_ROOT`
- `NEXORA_PROJECT_PATH`
- `NEXORA_ACTIVE_PROMPT_DIR`
- `NEXORA_QUOTA_TRACKER`
- `NEXORA_OLLAMA_MODEL`
- `OPENAI_API_KEY`
- `GEMINI_API_KEY`
- `ANTHROPIC_API_KEY`

Runtime guidance:

- Low complexity: Ollama/local only by default.
- Medium complexity: Gemini Flash or GPT-4o mini when configured, otherwise Ollama.
- High complexity: Claude Sonnet, Claude Code CLI or GPT-4o when configured, otherwise the best
  available fallback.
- HTTP 429 or quota-exceeded responses pause the provider locally and retry with the next available
  route.

Credentials must be provided by environment variables or provider CLI configuration:

- `OPENAI_API_KEY`
- `GEMINI_API_KEY`
- `ANTHROPIC_API_KEY`
- local `claude` CLI login/configuration when `claude_code_cli` is enabled

## Closure Rules

The execution agent must not modify the closure validator or tool registry while closing HOP
backlog work. If the validator still reports P0/P1 findings after 3 closure attempts, the agent must
stop and report:

- remaining findings;
- corrections already made;
- why the backlog appears closeable;
- what human decision or framework change is being requested.

No next backlog pointer may advance until the strict validator reports closure.

<!-- NEXORA_STRUCTURED_PAYLOAD_V1 -->

## Structured Payload

```yaml
artifact:
  id: HOP-RUN-AGENT-ORCHESTRATION-RUNTIME
  type: operations-runbook
  status: active
  owner: Nexora Engineering
project: healthcare-operations-platform
required_tools:
- context_orchestrator
- commercial_agent_router
- backlog_closure_validator
runtime_state:
  quota_tracker: .nexora/runtime/quota_tracker.json
  committed: false
configuration:
  framework_runtime_runbook: nexora-framework/08-engineering/agents/context-orchestrator/runtime-configuration-runbook.md
  framework_python_programs_must_be_versioned: true
  secrets_from_environment_only: true
  local_runtime_paths_excluded_from_git:
  - .nexora/runtime/
  - .nexora/cache/
  - .nexora/secrets/
session_policy:
  max_backlog_items_per_session: 1
  compact_or_restart_after_messages: 15
  handoff_and_exit_required: true
  commercial_subagents_for_mechanical_work: prohibited
closure_policy:
  protected_controls:
  - nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
  - nexora-framework/08-engineering/agents/context-orchestrator/tool-registry.md
  max_closure_attempts: 3
  next_pointer_requires_validator_closed: true
```
