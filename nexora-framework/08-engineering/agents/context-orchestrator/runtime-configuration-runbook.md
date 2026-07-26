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

Do not store API keys, tokens, provider account information, private paths with credentials or
generated quota state in committed files.

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

Install optional commercial SDKs only when the operator enables commercial routing:

```powershell
python -m pip install -r nexora-framework/08-engineering/agents/context-orchestrator/commercial-agent-requirements.txt
```

## Environment Variables

Required or recommended Nexora variables:

| Variable | Purpose |
| --- | --- |
| `NEXORA_ROOT` | Absolute repository root. |
| `NEXORA_PROJECT_PATH` | Project folder under `projects/`. |
| `NEXORA_ACTIVE_PROMPT_DIR` | Active prompt inbox used by router and validator. |
| `NEXORA_QUOTA_TRACKER` | Local ignored quota tracker path. |
| `NEXORA_OLLAMA_MODEL` | Default local Ollama model. |
| `OPENAI_API_KEY` | Optional OpenAI SDK credential. |
| `GEMINI_API_KEY` | Optional Google GenAI SDK credential. |
| `ANTHROPIC_API_KEY` | Optional Anthropic SDK credential. |

Template file:

`nexora-framework/08-engineering/agents/context-orchestrator/.env.example`

## Windows PowerShell Setup

Temporary session variables:

```powershell
$env:NEXORA_ROOT="C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora"
$env:NEXORA_PROJECT_PATH="projects/healthcare-operations-platform"
$env:NEXORA_ACTIVE_PROMPT_DIR="projects/healthcare-operations-platform/08-qa/generated-prompts/active_prompt"
$env:NEXORA_QUOTA_TRACKER=".nexora/runtime/quota_tracker.json"
$env:NEXORA_OLLAMA_MODEL="qwen2.5-coder:0.5b"
```

Optional provider credentials:

```powershell
$env:OPENAI_API_KEY="<set-outside-repo>"
$env:GEMINI_API_KEY="<set-outside-repo>"
$env:ANTHROPIC_API_KEY="<set-outside-repo>"
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
export NEXORA_OLLAMA_MODEL="qwen2.5-coder:0.5b"
```

Optional provider credentials:

```bash
export OPENAI_API_KEY="<set-outside-repo>"
export GEMINI_API_KEY="<set-outside-repo>"
export ANTHROPIC_API_KEY="<set-outside-repo>"
```

## Bootstrap Commands

Create ignored runtime state:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py --init-state
```

Generate the active optimized prompt:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
```

Check routing without invoking commercial providers:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py
```

Run closure validation after the backlog work is committed:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
```

## Operator Responsibilities

- Keep Python framework tools committed under `nexora-framework/`.
- Keep secrets, quota trackers and local state outside git.
- Configure provider credentials through OS environment variables or provider CLI login.
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
- NEXORA_OLLAMA_MODEL
- OPENAI_API_KEY
- GEMINI_API_KEY
- ANTHROPIC_API_KEY
versioned_python_programs:
- nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
- nexora-framework/08-engineering/agents/context-orchestrator/agent_runtime_router.py
- nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py
- nexora-framework/08-engineering/agents/context-orchestrator/frontmatter_migrator.py
- nexora-framework/08-engineering/agents/context-orchestrator/zero_yaml_migrator.py
```
