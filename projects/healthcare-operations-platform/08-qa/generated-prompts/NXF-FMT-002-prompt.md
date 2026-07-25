# TASK: NXF-FMT-002 - Framework and HOP Frontmatter Optimization
ROOT: C:/Documents/Proyectos/Laboratorio/NEXORA/git/nexora
PROJECT: projects/healthcare-operations-platform
ORCHESTRATION: ollama_primary

## 1. Alcance / Objetivos Directos
- Pausar desarrollo funcional de HOP hasta cerrar la optimización de formato.
- Migrar/optimizar artefactos YAML/MD pesados a Markdown con frontmatter compacto.
- Usar solo Python, PyYAML y Ollama local; no consumir tokens comerciales.
- Mantener ejecución agent-agnostic, sin dependencias propietarias de agentes o runtimes.
- Cerrar o reducir TD-FMT-001 como deuda bloqueante de formato antes de reanudar HOP.
- Ejecutar inventario, piloto, conversión por lotes, validación de referencias, parseo YAML y git diff --check.
- No avanzar punteros si un gate obligatorio queda bloqueado o sin evidencia.

## 2. Contexto Inmediato (Punteros)
- Prompts y estado: inspeccionar `06-delivery/commercial-product/HOP_COMMERCIAL_BACKLOG_EXECUTION_PROMPTS.yaml` y `PROJECT_STATE.yaml` bajo demanda.
- Contexto principal: `../../nexora-framework/02-standards/standards/frontmatter-artifact-migration-standard.yaml`

## 3. Entregables
- Cambios format_migration y validaciones asociadas.
- QA Evidence: `08-qa/format-migration/NXF-FMT-002-validation.md`
- Security Evidence: `not_applicable_format_migration_no_runtime_code`
- Transición: crear `08-qa/handoffs/NXF-FMT-002-summary.md`.
- Actualizar `PROJECT_STATE.yaml`, `SOURCE_OF_TRUTH.yaml`, backlog/prompts, runbook e índices aplicables.

## 4. Criterios de Cierre
- Gates obligatorios ejecutados; YAML/MD parseables; `git diff --check` limpio.
- Commit: `chore(framework): optimize artifact formats`.
- `git status --short` limpio si no hay bloqueantes.

<!-- ollama_plan_hash: a9c565a697b3edcb5ac518f930e270e7448d7ead1e0dae0c8d4bc0521af96429 -->
