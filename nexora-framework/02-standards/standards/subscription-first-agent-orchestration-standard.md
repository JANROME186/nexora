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
3. Use subscription-backed local CLIs when explicitly enabled by the operator, for example:
   - Claude Code CLI through local `claude` login.
   - GitHub Copilot CLI through local `gh auth login` and Copilot subscription.
4. Use filesystem task ingestion for IDE agents such as Cursor, Windsurf, VS Code agent extensions
   or other tools that consume a task file with the operator's existing flat-rate subscription.
5. Use paid API-key providers only through a documented ADR exception outside the standard backlog
   execution path.

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
  subscription_cli_allowed: true
  filesystem_task_ingestion_allowed: true
  ide_subscription_agents_allowed: true
  paid_api_key_exception_requires_adr: true
default_files:
  active_prompt_dir: projects/<project>/08-qa/generated-prompts/active_prompt/
  task_ingestion_file: .agent_next_task.md
  task_result_file: .agent_task_summary.md
operator_controls:
  - NEXORA_AGENT_TASK_FILE
  - NEXORA_AGENT_RESULT_FILE
  - NEXORA_QUOTA_TRACKER
  - NEXORA_OLLAMA_MODEL
```
