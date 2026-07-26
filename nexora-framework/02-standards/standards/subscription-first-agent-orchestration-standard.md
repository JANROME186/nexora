---
artifact:
  id: NXF-AGT-SUB-001
  type: framework-standard
  status: active
  owner: Nexora Engineering
---

# Subscription-First Agent Orchestration Standard

Nexora Framework must avoid paying twice for AI execution. When an operator already pays for an
IDE, agent tool or assistant subscription, Nexora must prefer local orchestration that uses that
subscription through a local CLI, authenticated editor session or filesystem task-ingestion pattern.

API-key providers that charge by token are not part of the default framework execution path.

## Required Execution Order

1. Use local deterministic tools, Python, shell, git, ripgrep and stack-native quality tools.
2. Use Ollama and approved open source local models for prompt compression, planning and validation.
3. Prefer automatic fallback across enabled subscription-backed CLIs when a selected CLI is not
   available for the current run. Manual IDE handoff is used when no automatic route is viable.
4. Use subscription-backed local CLIs only when explicitly enabled by the operator, for example:
   - Claude Code CLI through local `claude` login.
   - Codex CLI through local `codex login` and the operator's ChatGPT/Codex subscription.
   - GitHub Copilot CLI through local `gh auth login` and Copilot subscription.
   - Gemini CLI through local OAuth or enterprise Code Assist eligibility when supported.
   - Kiro IDE CLI through local `kiro chat` when an IDE session is the intended execution surface.
5. Use filesystem task ingestion for IDE agents such as Cursor, Windsurf, VS Code agent extensions
   or other tools that consume a task file with the operator's existing flat-rate subscription.
6. Use paid API-key providers only through a documented ADR exception outside the standard backlog
   execution path.

## Execution Flow Parameter

The context orchestrator must expose an execution-flow parameter:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py --execution-flow manual
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py --execution-flow cli
```

`manual` is the default and preferred route when the operator will paste the prompt into an IDE
agent. `cli` is used only when a subscription-backed CLI is available and intentionally enabled.
The selected flow is part of the prompt cache key to keep regenerated prompts deterministic for the
same mode and separate across modes.

Before any `cli` execution, Nexora must run `tool: agent_cli_preflight`. The router must refuse
headless CLI execution when the selected provider does not have a fresh ready certificate. This
prevents backlog work from starting when a provider is missing, unauthenticated, quota blocked,
unsupported for headless output or timing out on a tiny smoke prompt.

If `cli` fails because of permissions, missing login, unsupported headless mode, silent execution
blockers or sandbox limits, the router must first try another enabled provider in the same run. The
provider must be persisted as blocked only when its own response explicitly reports quota, rate
limit or plan-limit exhaustion. Manual mode is a last-resort handoff when no automatic provider can
continue.

## Filesystem Task Ingestion

The framework router may write one ignored task file, by default `.agent_next_task.md`, at the
repository root. The operator can instruct an IDE agent: "Atender tarea activa". The IDE agent must
read the task file, execute the prompt and write its summary to `.agent_task_summary.md`.

Both files are local runtime state and must not be committed.

## Agent-Agnostic Boundary

This standard does not make Nexora dependent on a specific IDE, CLI or model vendor. The framework
defines a generic routing pattern:

- Active prompt inbox: `projects/<project>/08-qa/generated-prompts/active_prompt/`.
- Router tool: `tool: commercial_agent_router`.
- Closure validator: `tool: backlog_closure_validator`.
- Local task file: configurable with `NEXORA_AGENT_TASK_FILE`.
- Local result file: configurable with `NEXORA_AGENT_RESULT_FILE`.

Any concrete CLI or IDE is an operator-selected adapter. If a better open source or subscription
tool appears, agents must document the proposed replacement and route it through framework
improvement backlog instead of hardcoding vendor behavior into product code.

## Structured Payload

```yaml
artifact:
  id: NXF-AGT-SUB-001
  type: framework-standard
  status: active
  owner: Nexora Engineering
policy:
  api_key_token_consumption_default_allowed: false
  ollama_required: true
  execution_flow_parameter_required: true
  default_execution_flow: manual
  manual_ide_handoff_preferred_when_cli_unavailable: true
  cli_preflight_required_before_execution: true
  subscription_cli_allowed: true
  codex_cli_allowed: true
  gemini_cli_allowed_when_oauth_or_enterprise_eligible: true
  kiro_ide_cli_allowed_for_ide_task_handoff: true
  filesystem_task_ingestion_allowed: true
  ide_subscription_agents_allowed: true
  paid_api_key_exception_requires_adr: true
default_files:
  active_prompt_dir: projects/<project>/08-qa/generated-prompts/active_prompt/
  cli_preflight_certificate: .nexora/runtime/agent-cli-preflight.json
  task_ingestion_file: .agent_next_task.md
  task_result_file: .agent_task_summary.md
operator_controls:
  - NEXORA_AGENT_TASK_FILE
  - NEXORA_AGENT_RESULT_FILE
  - NEXORA_QUOTA_TRACKER
  - NEXORA_CLI_PREFLIGHT_CERT
  - NEXORA_CLI_PREFLIGHT_MAX_AGE_MINUTES
  - NEXORA_PREFLIGHT_TIMEOUT_SECONDS
  - NEXORA_OLLAMA_MODEL
```
