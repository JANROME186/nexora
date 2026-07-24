# Context Orchestrator

This Python helper generates a compact backlog prompt using lazy loading and Ollama-first local orchestration.

Default usage:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py
```

By default, the script infers the active HOP backlog, validates the required Ollama model and writes
a deterministic prompt file to:

`projects/healthcare-operations-platform/08-qa/generated-prompts/<TASK_ID>-prompt.md`

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

## Backlog Closure Validation

After an execution agent claims a backlog is complete, run the local validator against the generated
prompt:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --prompt projects/healthcare-operations-platform/08-qa/generated-prompts/COM-MOD-017-BE-001-prompt.md
```

The validator uses deterministic repository checks first and Ollama as the mandatory local
summarizer. If the backlog is incomplete, it writes a compact correction prompt to:

`projects/healthcare-operations-platform/08-qa/generated-prompts/<TASK_ID>-closure-fix-prompt.md`
