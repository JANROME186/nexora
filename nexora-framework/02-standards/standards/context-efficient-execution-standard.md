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

The renderer must deduplicate grep/search output before writing the final prompt. Repeated lines
that only confirm the same task id, pointer or state must collapse into one file reference. The
renderer must also filter context by workstream: a backend task includes backend coverage and
backend gates, not unrelated mobile or portal coverage floors unless those components are directly
changed. Generated prompts must use one language consistently; HOP commercial prompts are rendered
in Spanish.

When a task belongs to a project folder, the generated prompt must declare `PROJECT` once after
`ROOT`. All pointers and deliverables inside that project must then use project-relative paths
instead of repeating the full project prefix.

## Format Policy

New task and handoff artifacts should use Markdown with minimal YAML frontmatter. Compact inventories or configuration may use TOML or Markdown tables.

Existing YAML artifacts remain supported until a controlled migration is completed. New monolithic YAML state/task artifacts should not be introduced unless automation truly requires them. When large YAML artifacts remain, projects must register migration debt instead of ignoring the issue.

## Prompt Contract

```text
# TASK: [ID_TAREA] - [TITULO]
ROOT: [RUTA_BASE]
PROJECT: [RUTA_PROYECTO]

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

## Backlog Closure Validation

After an execution agent claims completion, the framework must run the local closure validator:

```powershell
python nexora-framework/08-engineering/agents/context-orchestrator/backlog_validator.py `
  --root C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora `
  --prompt projects/healthcare-operations-platform/08-qa/generated-prompts/<TASK_ID>-prompt.md
```

The validator first applies deterministic repository checks: expected evidence files, evidence
status, project state, product backlog status, execution prompt transition, source-of-truth
references and clean git status. Ollama is then used as the mandatory local summarizer, but it
cannot override deterministic P0/P1 findings.

If the backlog is incomplete, the validator writes
`08-qa/generated-prompts/<TASK_ID>-closure-fix-prompt.md` with only the missing work required to
close the item.
