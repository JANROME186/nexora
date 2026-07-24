# Context Orchestrator

This Python helper generates a compact backlog prompt using lazy loading and optional Ollama compression.

Example:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/context_orchestrator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --task-id COM-MOD-017-BE-001 `
  --title "Compile marketplace backend outputs" `
  --summary-ref projects/healthcare-operations-platform/08-qa/handoffs/COM-MOD-017-DEF-summary.md
```

If `ollama` is available, the script can use `llama3.2` or another local model to compress the generated prompt. If not, it falls back to deterministic `rg`-based context extraction.

The output must remain agent agnostic. It should point to files and commands instead of pasting complete artifacts into the commercial prompt.
