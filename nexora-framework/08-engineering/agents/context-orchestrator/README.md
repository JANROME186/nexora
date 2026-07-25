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

Every generated backlog prompt includes a mandatory post-commit closure validation rule. After an
execution agent finishes the implementation, evidence and registry synchronization, the agent must
commit the completed work first and then run `tool: backlog_closure_validator` from
`tool-registry.md` against the generated prompt.

Tool reference used in compact prompts:

```text
tool: backlog_closure_validator
task_id: <TASK_ID>
prompt_ref: projects/healthcare-operations-platform/08-qa/generated-prompts/<TASK_ID>-prompt.md
```

The tool registry owns the full invocation template:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --prompt projects/healthcare-operations-platform/08-qa/generated-prompts/COM-MOD-017-BE-001-prompt.md
```

The validator uses deterministic repository checks first and Ollama as the mandatory local
summarizer. Ollama does not decide closure; P0/P1 deterministic findings are authoritative. If the
backlog is incomplete, it writes a compact correction prompt to:

`projects/healthcare-operations-platform/08-qa/generated-prompts/<TASK_ID>-closure-fix-prompt.md`

Closure is valid only when the strict validator exits with code `0`, the report status is `closed`,
hard findings are `0`, the closure validation evidence exists under
`projects/healthcare-operations-platform/08-qa/backlog-validations/`, and `git status --short` is
clean after the validation evidence is committed when applicable. If the validator reports stale
pointers, missing evidence, dirty worktree, or generates a closure-fix prompt, the agent must report
the inconsistencies, correct them, commit the corrections and run the strict validator again.

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
