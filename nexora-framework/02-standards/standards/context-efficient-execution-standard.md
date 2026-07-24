# Context Efficient Execution and Local Orchestrator Standard

This standard defines the mandatory Nexora Framework execution stack for prompt generation and
agent orchestration.

The base stack is Python 3.11+, Ollama local service, at least one approved open source Ollama
model, `ripgrep` and `git`. The bootstrap default model is `qwen2.5-coder:0.5b`;
`qwen2.5-coder:3b`, `llama3.2:3b` and `qwen2.5-coder:7b` are also approved.

Ollama is now the primary local orchestrator. The final prompt is rendered by Python from a
canonical context so repeated runs remain stable. If the canonical context hash has not changed,
the cached prompt is reused. If Ollama or the required model is missing, framework bootstrap is
incomplete unless the operator explicitly runs a diagnostic fallback.

## Required Flow

1. Read the active backlog pointer.
2. Inspect only relevant lines or sections.
3. Build and hash the canonical context.
4. Ask Ollama for deterministic orchestration metadata.
5. Render a compact prompt with `ROOT` defined once.
6. Persist the prompt to `08-qa/generated-prompts/<TASK_ID>-prompt.md`.
7. Reuse the cached prompt while the context hash is unchanged.
8. Send the compact prompt to the execution agent.
9. At closure, write `<TASK_ID>-summary.md` with `Status`, `Cambios Clave`, `Deuda Técnica Creada` and `Siguiente Paso`.

## Format Policy

New task and handoff artifacts should use Markdown with minimal YAML frontmatter. Compact inventories or configuration may use TOML or Markdown tables.

Existing YAML artifacts remain supported until a controlled migration is completed. New monolithic YAML state/task artifacts should not be introduced unless automation truly requires them. When large YAML artifacts remain, projects must register migration debt instead of ignoring the issue.

## Prompt Contract

```text
# TASK: [ID_TAREA] - [TITULO]
ROOT: [RUTA_BASE]

## 1. Alcance / Objetivos Directos
- [Instrucciones concisas]

## 2. Contexto Inmediato (Punteros)
- Ref: [handoff previo o archivo puntual]

## 3. Entregables
- [Archivos a crear/modificar]
- Crear [TASK_ID]-summary.md con Status, Cambios Clave, Deuda Técnica Creada y Siguiente Paso.

## 4. Criterios de Cierre
- [Status esperado]
- [Pruebas obligatorias]
- [Conventional Commit sugerido]
```

Quality gates, security checks, coverage floors, stale-pointer sweeps and clean git status remain mandatory. Token optimization never justifies skipping validation.
