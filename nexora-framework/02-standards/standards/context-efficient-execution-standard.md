# Context Efficient Execution and Local Orchestrator Standard

This standard reduces commercial LLM token usage without lowering delivery quality.

Nexora agents should use a local preprocessing layer before sending work to a commercial execution agent. The preferred local layer is a Python orchestrator with optional Ollama support. Its job is to inspect only the active backlog pointers, select the smallest useful context, and generate a synthetic prompt of roughly 300-600 tokens.

The orchestrator is optional infrastructure, not a vendor lock. If Ollama is unavailable, the Python script must fall back to deterministic local inspection with `rg` and targeted file reads.

## Required Flow

1. Read the active backlog pointer.
2. Inspect only relevant lines or sections.
3. Generate a compact prompt with `ROOT` defined once.
4. Send the compact prompt to the commercial execution agent.
5. At closure, write `<TASK_ID>-summary.md` with `Status`, `Cambios Clave`, `Deuda Técnica Creada` and `Siguiente Paso`.

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
